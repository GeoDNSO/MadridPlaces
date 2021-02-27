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

def selectPrueba(location_name):
	
	cursor.execute("SELECT image FROM location_images WHERE location_name LIKE %s", [location_name])
	allImage = cursor.fetchall()
	count = 0
	for i in allImage:
		with open(str(count)+'.jpg', 'wb') as f:
			f.write(i[0])
		count += 1

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
	cursor.execute("SELECT name FROM location")
	allNames = cursor.fetchall()
	for name in allNames:
		lista.append(name)
	return lista

#Conexión a la BD

mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()

selectPrueba("Enjabonarte")
lista = selectLugar()
driver = webdriver.Chrome()
count = 0
for name in lista:

    if(count < 15):
    	inp = name
    	url = 'https://www.google.com/search?q='+str(inp)+'&source=lnms&tbm=isch&sa=X&ved=2ahUKEwie44_AnqLpAhUhBWMBHUFGD90Q_AUoAXoECBUQAw&biw=1920&bih=947'
    	find_urls(inp,url,driver,4)
    	count += 1
    	print(count)
    else:
    	time.sleep(20)
    	count = 0