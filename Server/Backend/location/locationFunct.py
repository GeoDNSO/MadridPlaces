from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

def listImages(location): #Devuelve una lista de imagenes de un lugar espefícico, usado para la función listLocations
    try:
        stQuery = modules.location_images.query.filter_by(location_name=location).all()
        lista = []
        for imagen in stQuery:
            lista.append({"image" : imagen.image})
        return lista
    except Exception as e:
        print("Error mostrando las imágenes: ", repr(e))
        return None
