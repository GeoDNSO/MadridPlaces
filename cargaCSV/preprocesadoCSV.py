import os, sys
import pandas as pd
import numpy as np
import re
from xml.etree import ElementTree


########## IMPORTANTE: NECESIDAD DE RECOGER HORARIOS, AÚN NO IMPLEMENTADO POR PEREZA


def limpiezaTemplos(dataTemplos):
    separate = re.split(r';', dataTemplos)
    nombre = separate[1]
    descripcion = separate[2] if separate[2] != '' else "Sin especificar"
    #direccion = separate[10] + " " + separate[9] +  " " + separate[12] + ', ' + separate[17] +  " " + separate[19]
    clase_vial = separate[10] if separate[10] != '' else "Sin especificar"
    nombre_vial = separate[9] if separate[9] != '' else "Sin especificar"
    numero_vial = separate[12] if separate[12] != '' and separate[12] != 's/n' else '0'
    codigo_postal = separate[19] if separate[19] != '' and separate[19] != '0' else '00000'
    latitud = separate[24] if separate[24] != '' else '0'
    longitud = separate[25] if separate[25] != '' else '0'
    tipo = separate[29] if separate[29] != '' else "Sin especificar"
    url = separate[8] #Únicamente existe una foto por cada templo
    return [nombre, descripcion, latitud, longitud, tipo, clase_vial, nombre_vial, numero_vial, codigo_postal, url]

def limpiezaMuseos(dataMuseos):
    separate = re.split(r';', dataMuseos)
    if(len(separate) == 31):
        nombre = re.split(r'"',separate[1])
        descripcion = re.split(r'"',separate[2]) if separate[2] != '' else "Sin especificar"
        clase_vial = separate[10].replace('"', '') if separate[10] != '' else "Sin especificar"
        nombre_vial = separate[9].replace('"', '') if separate[9] != '' else "Sin especificar"
        numero_vial = separate[12].replace('"', '') if separate[12] != '' and separate[12] != 's/n' else '0'
        codigo_postal = separate[19].replace('"', '') if separate[19] != '' and separate[19] != '0'else '00000'
        latitud = separate[24].replace('"', '') if separate[24] != '' else '0'
        longitud = separate[25].replace('"', '') if separate[25] != '' else '0'
        tipo = separate[29] if separate[29] != '' else "Sin especificar"
        return [nombre[1], descripcion[1], latitud, longitud, tipo, clase_vial, nombre_vial, numero_vial, codigo_postal]
    return None


def limpiezaMonumentos(dataMonumentos):
    separate = re.split(r'";"', dataMonumentos)
    nombre = separate[0].split('"')[1]
    descripcion = separate[2].replace('"', '') if separate[2] != '' else "Sin especificar"
    clase_vial = separate[8] if separate[8] != '' else "Sin especificar"
    nombre_vial = separate[7] if separate[7] != '' else "Sin especificar"
    numero_vial = separate[10] if separate[10] != '' and separate[10] != 's/n'else '0'
    codigo_postal = separate[17] if separate[17] != '' and separate[17] != '0'else '00000'
    latitud = separate[22] if separate[22] != '' else '0'
    longitud = separate[23] if separate[23] != '' else '0'
    tipo = separate[1] if separate[1] != '' else "Sin especificar"
    return [nombre, descripcion, latitud, longitud, tipo, clase_vial, nombre_vial, numero_vial, codigo_postal]


def limpiezaInfoTurismo(dataInfo):
    separate = re.split(r'";"', dataInfo)
    nombre = separate[0].split('"')[1]
    descripcion = separate[1].replace('"', '') if separate[1] != '' else "Sin especificar"
    clase_vial = separate[9] if separate[9] != '' else "Sin especificar"
    nombre_vial = separate[8] if separate[8] != '' else "Sin especificar"
    numero_vial = separate[11] if separate[11] != '' and separate[11] != 's/n'else '0'
    codigo_postal = separate[18] if separate[18] != '' and separate[18] != '0'else '00000'
    latitud = separate[23] if separate[23] != '' else '0'
    longitud = separate[24] if separate[24] != '' else '0'
    tipo = separate[28].split("/")[3].replace('";', '') if separate[28] != '' else "Sin especificar"
    return [nombre, descripcion, latitud, longitud, tipo, clase_vial, nombre_vial, numero_vial, codigo_postal]

def limpiezaXML(url):
	tree = ElementTree.parse(url+'.xml')
	root = tree.getroot()
	newCSV = pd.DataFrame(columns=['Nombre','Descripcion','Latitud', 'Longitud', 'Tipo', 'Clase_vial', 'Nombre_vial', 'Numero_vial', 'Codigo_postal'])
	for att in root:
	    
	    basicData = att.find('basicData')
	    nombre = basicData.find('name').text.replace('&amp;', '').replace('&nbsp;', '').replace('&Ntilde;', 'Ñ').replace('&ntilde;', 'ñ')
	    if(basicData.find('body').text != None):
	    	descripcion = basicData.find('body').text.replace('&amp;', '').replace('&nbsp;', '').replace('&ntilde;', 'ñ')
	    	descripcion = re.sub('<[^>]+>', '', descripcion) #Expresion regular para quitar todas las etiquetas
	    else:
	    	descripcion = "Sin descripcion"
	    geoData = att.find('geoData')
	    clase_vial = "Calle" #En ningún xml tiene clase vial, por lo que he puesto como calle
	    direccion = geoData.find('address').text.split(',')
	    if len(direccion) == 1:
	        numero_vial = '0'
	    else: 
	        numero_vial = direccion[1] if direccion[1] != '' and direccion[1] != ' ' else '0'

	    nombre_vial = direccion[0]
	    if(geoData.find('zipcode').text != None and geoData.find('zipcode').text != "0" and geoData.find('zipcode').text != ''):
	    	codigo_postal = geoData.find('zipcode').text
	    else:
	    	codigo_postal = '00000'
	    if(geoData.find('latitude').text != None and geoData.find('longitude').text != None):
	    	latitud = geoData.find('latitude').text
	    	longitud = geoData.find('longitude').text
	    else:
	    	latitud = 0 
	    	longitud = 0	
	    extradata = att.find('extradata')
	    for t in extradata:
	        if t.attrib['name'] == "Tipo":
	            tipo = t.text
	            break;
	    newCSV = newCSV.append({'Nombre' : nombre, 'Descripcion' : descripcion, 'Latitud' : latitud, 'Longitud' : longitud,'Tipo' : tipo, 'Clase_vial' : clase_vial, 'Nombre_vial' : nombre_vial, 'Numero_vial' : numero_vial, 'Codigo_postal' : codigo_postal},ignore_index=True)


	newCSV.to_csv(url + 'procesados.csv',index=False, encoding='utf-8', sep= '\t')

#########################################################################   Templos   #########################################################################
print("Comenzando el preproceso de los datasets...")

templos = pd.DataFrame(columns=['Nombre','Descripcion','Latitud', 'Longitud', 'Tipo', 'Clase_vial', 'Nombre_vial', 'Numero_vial', 'Codigo_postal'])
dataTemplos = pd.read_csv("templos.csv", sep='delimiter', header=None, engine='python')
for i in range(0, len(dataTemplos)):
    prueba = dataTemplos.iloc[i, 0] #Recoge toda la fila
    res = limpiezaTemplos(prueba)
    if(res[1] == ""):
        res[1] = "Sin descripcion"
    auxTipo = res[4].split("/")
    if('http' not in res[2] and 'Ntilde' not in res[2] and len(auxTipo) != 1):
        templos = templos.append({'Nombre' : res[0], 'Descripcion' : res[1], 'Latitud' : res[2], 'Longitud' : res[2],'Tipo' : auxTipo[3], 'Clase_vial' : res[5], 'Nombre_vial' : res[6], 'Numero_vial' : res[7], 'Codigo_postal' : res[8]},ignore_index=True)
dataTemplos.drop(dataTemplos.columns, axis=1)

templos.sort_values(by=['Nombre'], inplace=True)

templos.to_csv("TemplosProcesados.csv",index=False, encoding='utf-8-sig', sep= '\t')
print("CSV de Templos creados")

#########################################################################   Museos   #########################################################################

museos = pd.DataFrame(columns=['Nombre','Descripcion','Latitud', 'Longitud', 'Tipo', 'Clase_vial', 'Nombre_vial', 'Numero_vial', 'Codigo_postal'])
dataMuseos = pd.read_csv("museos.csv", sep='delimiter', header=None, engine='python')
for i in range(1, len(dataMuseos)):
    prueba = dataMuseos.iloc[i, 0] #Recoge toda la fila
    res = limpiezaMuseos(prueba)
    if(res != None):
        if(res[1] == ""):
            res[1] = "Sin descripcion"
        auxTipo = res[4].split("/")
        if('http' not in res[2] and 'Ntilde' not in res[2] and len(auxTipo) != 1):
            museos = museos.append({'Nombre' : res[0], 'Descripcion' : res[1], 'Latitud' : res[2], 'Longitud' : res[3],'Tipo' : auxTipo[2], 'Clase_vial' : res[5], 'Nombre_vial' : res[6], 'Numero_vial' : res[7], 'Codigo_postal' : res[8]},ignore_index=True)
dataMuseos.drop(dataMuseos.columns, axis=1)

museos.sort_values(by=['Nombre'], inplace=True)

museos.to_csv("MuseosProcesados.csv",index=False, encoding='utf-8-sig', sep= '\t')

print("CSV de Museos creados")
#########################################################################   Monumentos   #########################################################################
monumentos = pd.DataFrame(columns=['Nombre','Descripcion','Latitud', 'Longitud', 'Tipo', 'Clase_vial', 'Nombre_vial', 'Numero_vial', 'Codigo_postal'])
dataMonumentos = pd.read_csv("monumentos.csv", sep='delimiter', header=None, engine='python')
for i in range(1, len(dataMonumentos)):
    prueba = dataMonumentos.iloc[i, 0] #Recoge toda la fila
    res = limpiezaMonumentos(prueba)
    if(res[1] == ""):
        res[1] = "Sin descripcion"
    if(res[8] == ""):
        res[8] = 0
    if(res[7] == ""):
        res[7] = 0
    monumentos = monumentos.append({'Nombre' : res[0], 'Descripcion' : res[1], 'Latitud' : res[2], 'Longitud' : res[3],'Tipo' : res[4], 'Clase_vial' : res[5], 'Nombre_vial' : res[6], 'Numero_vial' : res[7], 'Codigo_postal' : res[8]},ignore_index=True)
dataMonumentos.drop(dataMonumentos.columns, axis=1)

monumentos.to_csv("MonumentosProcesados.csv",index=False, encoding='utf-8-sig', sep= '\t')

print("CSV de Monumentos creados")
#########################################################################   Información Turistica   #########################################################################
infoTurismo = pd.DataFrame(columns=['Nombre','Descripcion','Latitud', 'Longitud', 'Tipo', 'Clase_vial', 'Nombre_vial', 'Numero_vial', 'Codigo_postal'])
dataInfo = pd.read_csv("informacionTurismo.csv", sep='delimiter', header=None, engine='python')
for i in range(1, len(dataInfo)):
    prueba = dataInfo.iloc[i, 0] #Recoge toda la fila
    res = limpiezaInfoTurismo(prueba)
    if(res[1] == ""):
        res[1] = "Sin descripcion"
    if(res[8] == ""):
        res[8] = 0
    if(res[7] == ""):
        res[7] = 0
    infoTurismo = infoTurismo.append({'Nombre' : res[0], 'Descripcion' : res[1], 'Latitud' : res[2], 'Longitud' : res[3],'Tipo' : res[4], 'Clase_vial' : res[5], 'Nombre_vial' : res[6], 'Numero_vial' : res[7], 'Codigo_postal' : res[8]},ignore_index=True)
dataInfo.drop(dataInfo.columns, axis=1)

infoTurismo.to_csv("informacionTurismoProcesados.csv",index=False, encoding='utf-8-sig', sep= '\t')

print("CSV de Puntos de informacion Turisticas creados")
#########################################################################   Restaurantes   #########################################################################

limpiezaXML("restaurantes")

print("CSV de Restaurantes creados")
#########################################################################   Tiendas   #########################################################################

limpiezaXML("tiendas")

print("CSV de Tiendas creados")
#########################################################################   Clubs   #########################################################################

limpiezaXML("clubs")

print("CSV de Clubs creados")

#########################################################################   Alojamientos   #########################################################################

limpiezaXML("alojamientos")

print("CSV de Alojamientos creados")


print("Preprocesado Completado")