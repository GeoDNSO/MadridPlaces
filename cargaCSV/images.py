#Script necesario para realizar un scrapping con Selenium ya que Google no deja usar Beautiful Soup para hacer Web Scrapping
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

import urllib.request
from bs4 import BeautifulSoup


def find_urls(inp,url,driver,iterate):
	try:
		driver.get(url)
		for j in range (1,iterate+1):
			imgurl = driver.find_element_by_xpath('//div//div//div//div//div//div//div//div//div//div['+str(j)+']//a[1]//div[1]//img[1]')
			imgurl.click() #Hace un click como un usuario normal
			img = driver.find_element_by_xpath('//body/div[2]/c-wiz/div[4]/div[2]/div[3]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[1]/div[2]/a/img').get_attribute("src")

			parsedSrc = img[img.find(",")+1:] #Cuando recoges la URL del src, se mantiene codificado, así que se descarta la cabecera para que se decodifique correctamente
			img = base64.b64decode(parsedSrc)
			sql=" INSERT IGNORE INTO location_images (location_name, image) VALUES (%s, %s)" 
			cursor.execute(sql, (inp, img)) 
			mydb.commit()
	except:
		print(inp) #Para saber qué lugares están fallando y probablemente se deba almacenar fotos manualmente (No creo que haya muchos lugares con error)
		pass

def selectLugar():
	lista = []
	cursor.execute("SELECT name, road_class, road_name, road_number, category, id_type FROM location Join type_of_place on location.type_of_place = type_of_place.id_type")
	allNames = cursor.fetchall()
	for obj in allNames:
		if(obj[5] < 6 or obj[5] >= 15):
			lista.append({"locationName" : obj[0], "search" : obj[0] + " " + obj[4] + " " + obj[1] + " " + obj[2] + " " + obj[3]})
	return lista

def main():
	lista = selectLugar() #Devuelve la lista de los nombres de todos los lugares exceptos los monumentos
	driver = webdriver.Chrome() #Accede al navegador
	count = 0 #Contador para evitar el captcha de Google
	for name in lista:

	    if(count < 15):
	    	inp = name["search"] #Recoge el nombre del lugar con su direccion para aumentar la precisión de búsqueda
	    	url = 'https://www.google.com/search?q='+str(inp)+'&source=lnms&tbm=isch&sa=X&ved=2ahUKEwie44_AnqLpAhUhBWMBHUFGD90Q_AUoAXoECBUQAw&biw=1920&bih=947'
	    	find_urls(name["locationName"],url,driver,5) #Recoge 5 fotos scrappeando Google fotos y los almacena en formato BLOB en la BD
	    	count += 1
	    else:
	    	time.sleep(20) #Duerme y reinicia el contador
	    	count = 0



def Monumentos():
	dataMonumentos = pd.read_csv("monumentos.csv", sep='delimiter', header=None, engine='python')
	for i in range(1, len(dataMonumentos)):
		prueba = dataMonumentos.iloc[i, 0]
		separate = re.split(r'";"', prueba)
		name = separate[0].split('"')[1]
		url = separate[6].replace('"', '')
		try:
			r = requests.get(url)
			html = r.text
			soup=BeautifulSoup(html,'html.parser')
			allImg = soup.find_all('a', class_="carouselNoticia-link")
			for a in allImg:
				image = "https://patrimonioypaisaje.madrid.es" + a["href"]
				data=requests.get(image)   # read image
				photo=data.content
				sql=" INSERT IGNORE INTO location_images (location_name, image) VALUES (%s, %s)" 
				cursor.execute(sql, (name, photo)) 
				mydb.commit()
		except:
			print(name)
			pass

#Conexión a la BD

mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()

main()

Monumentos()