from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

def showRate(user, location):
    try:
        rtQuery = modules.ratings.query.filter_by(location = location, user = user).all()
        if len(rtQuery) is 0:
            return -1
        return rtQuery[0].rate
    except Exception as e:
        print("Error:", repr(e))
        return -1

def averageRate(location): #A lo mejor no se necesita un URL 
    #json_data = request.get_json()
    #location = json_data["location"]
    try:
        rtQuery = modules.ratings.query.filter_by(location = location).all()
        cant = 0
        total = 0
        for rate in rtQuery:
            cant = cant + 1
            total = total + rate.rate
        result = total / cant
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return None

    #return jsonify(exito = "true", avgRate = result)   
    return round(result, 2)
