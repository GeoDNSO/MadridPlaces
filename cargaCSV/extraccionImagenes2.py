#Script necesario para realizar un scrapping con Selenium Para los 66 lugares sin imágenes
import requests
#import pymysql
import MySQLdb
#import io
from selenium import webdriver
import time
import shutil
import os
import base64
import pandas as pd
import numpy as np
import re
from xml.etree import ElementTree
import urllib.request
from bs4 import BeautifulSoup


listaSelenium = [] #Lista global que contendrá todos los lugares que serán procesados con Selenium


def find_urls(inp,url,driver,iterate):
	try:
		driver.get(url)
		for j in range (1,iterate+1):
			imgurl = driver.find_element_by_xpath('//div//div//div//div//div//div//div//div//div//div['+str(j)+']//a[1]//div[1]//img[1]')
			imgurl.click() #Hace un click como un usuario normal
			time.sleep(1)
			img = driver.find_element_by_xpath('//body/div[2]/c-wiz/div[4]/div[2]/div[3]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[1]/div[2]/a/img').get_attribute("src")
			time.sleep(1)
			if("http" in img):
				print(img)
				sql=" INSERT IGNORE INTO location_images (location_name, image) VALUES (%s, %s)" 
				cursor.execute(sql, (inp, img)) 
				mydb.commit()
	except:
		print(inp) #Para saber qué lugares están fallando y probablemente se deba almacenar fotos manualmente (No creo que haya muchos lugares con error)
		pass


def selectLugar(): #Recoge todos los lugares que no tienen imágenes
	cursor.execute("SELECT name, road_class, road_name, road_number, category, id_type, zipcode FROM location Join type_of_place on location.type_of_place = type_of_place.id_type WHERE location.name NOT IN (SELECT location_name FROM location_images)")
	allNames = cursor.fetchall()
	for obj in allNames:
		if(obj[0] == "Fuente mural en la calle Marcelo Usera"):
			listaSelenium.append({"locationName" : obj[0], "search" : obj[0]})
		else:
			listaSelenium.append({"locationName" : obj[0], "search" : obj[0] + ", " + obj[4] + " " + obj[1] + " " + obj[2] + " " + obj[3] + ", " + str(obj[6])})
	

def main(): #Uso de Selenium exclusivamente para datos sin imágenes
	selectLugar() #Devuelve la lista de los nombres de todos los lugares exceptos los monumentos
	print(len(listaSelenium))
	driver = webdriver.Chrome() #Accede al navegador
	count = 0 #Contador para evitar el captcha de Google
	for name in listaSelenium:
		inp = name["search"] #Recoge el nombre del lugar con su direccion para aumentar la precisión de búsqueda
		url = 'https://www.google.com/search?q='+str(inp)+'&source=lnms&tbm=isch&sa=X&ved=2ahUKEwie44_AnqLpAhUhBWMBHUFGD90Q_AUoAXoECBUQAw&biw=1920&bih=947'
		find_urls(name["locationName"],url,driver,5) #Recoge 5 fotos scrappeando Google fotos y los almacena en formato BLOB en la BD
		count += 1


#Conexión a la BD

mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()

main()