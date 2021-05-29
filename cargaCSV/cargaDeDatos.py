#Script para insertar las categorías y los lugares en la BD
import os, sys
import pandas as pd
import numpy as np
import re
import MySQLdb


mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()


def insertarTipos(URL): #Inserta todas las categorias que existen
	lastCategory = []
	csv_data = pd.read_csv(URL + ".csv", sep='delimiter', header=None, engine='python', encoding='utf-8-sig')
	for i in range(1, len(csv_data)):
		prueba = csv_data.iloc[i, 0]
		separate = re.split(r'\t', prueba)
		sep = re.sub('[*0-9]', '', separate[4]) #Quitar algunos casos donde el tab no funciona bien
		separate[4] = categorias(separate[4]) #Cambia los nombres de las categorias 

		if(sep != "-." and separate[4] not in lastCategory):
			cursor.execute("INSERT IGNORE INTO type_of_place (category) VALUES (%s)", [separate[4]])
			lastCategory.append(separate[4])
	#close the connection to the database.
	mydb.commit()


def insertarSQL(URL): #Inserta todos los lugares
	csv_data = pd.read_csv(URL + ".csv", sep='delimiter', header=None, engine='python',  encoding='utf-8-sig')
	for i in range(1, len(csv_data)):
		prueba = csv_data.iloc[i, 0]
		separate = re.split(r'\t', prueba)
		separate[0] = separate[0].replace('&Aacute;lbora', 'Á')
		separate[0] = separate[0].replace('&aacute;lbora', 'á')
		separate[1] = re.sub('\u0301', '', separate[1]) #Descartar manualmente caracteres raros
		separate[1] = re.sub('\u0303', '', separate[1])	
		separate[0] = re.sub('\u014c', 'O', separate[0])				
		separate[1] = re.sub('\u014c', 'O', separate[1])			
		separate[1] = re.sub('\u200b', '', separate[1])	
		separate[1] = re.sub('\u2010', '', separate[1])	
		separate[1] = re.sub('\x96', '', separate[1])	
		separate[1] = re.sub('\x93', '', separate[1])
		separate[1] = re.sub('\x94', '', separate[1])	
		separate[1] = re.sub('\x85', '', separate[1])
		if(len(separate) == 9 and (separate[2] != '' or separate[3] != '') and separate[2].count('.') == 1):

			separate[4] = categorias(separate[4]) #Cambia los nombres de las categorias

			cursor.execute( "SELECT * FROM type_of_place WHERE category LIKE %s", [separate[4]]) #Recoge la categoria de la tabla de tipos
			tipo = cursor.fetchone()
			sql2 = "INSERT IGNORE INTO location (name, description, coordinate_latitude, coordinate_longitude, type_of_place, road_class, road_name, road_number, zipcode) VALUES (%s,%s,%s,%s,%s,%s,%s,%s,%s)"
			latitude = float(separate[2])
			longitude = float(separate[3])
			road_number = separate[7] if separate[7] != ' ' and separate[7] != '' else "0"
			zipcode = int(separate[8])
			val2 = (separate[0], separate[1],latitude,longitude,tipo[0], separate[5], separate[6], separate[7], zipcode)
			cursor.execute(sql2, [separate[0], separate[1],latitude,longitude,tipo[0], separate[5], separate[6], separate[7], zipcode])
	#close the connection to the database.
	mydb.commit()

def categorias(categoria):
	ret = ""
	if(categoria == "entidadesYorganismos"):
		ret = "Museos" 
	elif(categoria == "OficinasTurismo"):
		ret = "Oficinas de Turismo"
	elif(categoria == "Escultura conceptual o abstracta" or categoria == "Grupo Escultórico" or categoria == "Estatua"or categoria == "Elemento conmemorativo, Lápida" or categoria == "Puente, construcción civil"or categoria == "Fuente, Estanque, Lámina de agua" or categoria == "Elemento de ornamentación" or categoria == "Edificación singular" or categoria == "Puerta, Arco triunfal" or categoria == "MonumentosEdificiosArtisticos"):
		ret = "Monumentos"
	elif(categoria == "TemplosIglesiasCatolicas"):
		ret = "Templos"
	else:
		ret = categoria
	return ret

print("Comenzar a insertar lugares en la BD...")
#########################################################   Inserción de Categorias   #########################################################
insertarTipos("datasets_procesados/informacionTurismoProcesados")
insertarTipos("datasets_procesados/clubsprocesados")
insertarTipos("datasets_procesados/tiendasProcesados")
insertarTipos("datasets_procesados/parquesProcesados")
insertarTipos("datasets_procesados/restaurantesProcesados")
insertarTipos("datasets_procesados/alojamientosprocesados")
insertarTipos("datasets_procesados/MonumentosProcesados")
insertarTipos("datasets_procesados/MuseosProcesados")
insertarTipos("datasets_procesados/TemplosProcesados")

print("Completado la inserción de categorias")
#########################################################   Inserción de Lugares   #########################################################

insertarSQL("datasets_procesados/clubsprocesados")
insertarSQL("datasets_procesados/informacionTurismoProcesados")
insertarSQL("datasets_procesados/tiendasProcesados")
insertarSQL("datasets_procesados/parquesProcesados")
insertarSQL("datasets_procesados/restaurantesProcesados")
insertarSQL("datasets_procesados/alojamientosprocesados")	
insertarSQL("datasets_procesados/MonumentosProcesados")
insertarSQL("datasets_procesados/MuseosProcesados")
insertarSQL("datasets_procesados/TemplosProcesados")

print("Completado las inserciones de los lugares")

print("Finalizado")