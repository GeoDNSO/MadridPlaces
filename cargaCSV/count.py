import os, sys
import pandas as pd
import numpy as np
import re
from xml.etree import ElementTree


def Xml(url):
	tree = ElementTree.parse(url+'.xml')
	root = tree.getroot()
	cont = 0
	for att in root:
		cont+= 1
	return cont


def Csv1(url):
	csv_data = pd.read_csv(url + ".csv", sep='delimiter', header=None, engine='python')
	return len(csv_data)


def Csv2(url):
	csv_data = pd.read_csv(url + ".csv", sep='delimiter', header=None, engine='python', encoding='utf-8-sig')
	return len(csv_data)


print("Restaurantes: ", Xml("restaurantes"))
print("Restaurantes Procesados: ", Csv2("restaurantesProcesados"))
print("----------------------------------------------")
print("Clubs: ", Xml("clubs"))
print("Clubs Procesados: ", Csv2("clubsProcesados"))
print("----------------------------------------------")
print("Tiendas: ", Xml("tiendas"))
print("Tiendas Procesados: ", Csv2("tiendasProcesados"))
print("----------------------------------------------")
print("Alojamientos: ", Xml("alojamientos"))
print("Alojamientos Procesados: ", Csv2("alojamientosProcesados"))
print("----------------------------------------------")
print("Monumentos: ", Csv1("monumentos"))
print("Monumentos Procesados: ", Csv2("monumentosProcesados"))
print("----------------------------------------------")
print("Templos: ", Csv1("templos"))
print("Templos Procesados: ", Csv2("templosProcesados"))
print("----------------------------------------------")
print("Museos: ", Csv1("museos"))
print("Museos Procesados: ", Csv2("museosProcesados"))
print("----------------------------------------------")
print("Informacion de puntos turisticos: ", Csv1("informacionTurismo"))
print("Informacion de puntos turisticos Procesados: ", Csv2("informacionTurismoProcesados"))
print("----------------------------------------------")