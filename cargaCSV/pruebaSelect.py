#Script de prueba para verificar la correcta decodificación de las imágenes
import requests
import pymysql
import MySQLdb
import PIL.Image as Image
import io

def convertToBinaryData(filename):
    # Convert digital data to binary format
	data=requests.get(pic_url)   # read image
	photo=data.content
	return photo

def selectPrueba():
	
	cursor.execute("SELECT image FROM location_images WHERE location_name LIKE %s", ["Aristocrazy"])
	allImage = cursor.fetchall()
	count = 0
	for i in allImage:
		with open(str(count)+'.jpg', 'wb') as f:
			f.write(i[0])
		count += 1

#Pruebas

pic_url='http://www.esmadrid.com/sites/default/files/recursosturisticos/alojamientos/mmd_008.jpg' 
#pic_url='http://www.esmadrid.com/sites/default/files/recursosturisticos/alojamientos/mmd_010.jpg'
data=requests.get(pic_url)   # read image

#Conexión a la BD

mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()


selectPrueba()
