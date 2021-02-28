import requests
import pymysql
import MySQLdb
import PIL.Image as Image
import io

from selenium import webdriver
import time
import requests
import shutil
import os
import base64




def convertToBinaryData(filename):
    # Convert digital data to binary format
	data=requests.get(pic_url)   # read image
	photo=data.content
	return photo

def selectPrueba():
	
	cursor.execute("SELECT name, road_class, road_name, road_number, category, id_type FROM location Join type_of_place on location.type_of_place = type_of_place.id_type")
	allImage = cursor.fetchall()
	for obj in allImage:
		if(obj[5] >= 6 and obj[5] < 15):
			print(obj[0] + " " + obj[4] + " " + obj[1] + " " + obj[2] + " " + obj[3] )

def find_urls(inp,url,driver,iterate):
    driver.get(url)
    for j in range (1,iterate+1):
    	try:
	        imgurl = driver.find_element_by_xpath('//div//div//div//div//div//div//div//div//div//div['+str(j)+']//a[1]//div[1]//img[1]')
	        imgurl.click()
	        img = driver.find_element_by_xpath('//body/div[2]/c-wiz/div[4]/div[2]/div[3]/div/div/div[3]/div[2]/c-wiz/div/div[1]/div[1]/div[1]/div[2]/a/img').get_attribute("src")
	        #save_img(inp, img, j)
	        parsedSrc = img[img.find(",")+1:]
	        img = base64.b64decode(parsedSrc)
	        sql=" INSERT IGNORE INTO location_images (location_name, image) VALUES (%s, %s)" 
	        cursor.execute(sql, (inp, img)) 
	        mydb.commit()
    	except:
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
	lista = selectLugar()
	driver = webdriver.Chrome()
	count = 0
	for name in lista:

	    if(count < 15):
	    	inp = name["search"]
	    	url = 'https://www.google.com/search?q='+str(inp)+'&source=lnms&tbm=isch&sa=X&ved=2ahUKEwie44_AnqLpAhUhBWMBHUFGD90Q_AUoAXoECBUQAw&biw=1920&bih=947'
	    	find_urls(name["locationName"],url,driver,5)
	    	count += 1
	    else:
	    	time.sleep(20)
	    	count = 0
#Conexión a la BD

mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()

main()