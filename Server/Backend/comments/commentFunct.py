from flask_sqlalchemy import SQLAlchemy
from app import app
import modules


def listComments(location): #A lo mejor no se necesita un URL 
    try:
        cmQuery = modules.comments.query.filter_by(location = location).all()
        lista = []
        for comment in cmQuery:
            cmDict = {comment.user : comment.comment}
            lista.append(cmDict)
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        return None

    return lista 


def numberOfComments(location): #A lo mejor no se necesita un URL 
    try:
        cmQuery = modules.comments.query.filter_by(location = location).count()
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        return None  
    return cmQuery 

def averageRate(location): #A lo mejor no se necesita un URL 
    #json_data = request.get_json()
    #location = json_data["location"]
    try:
        rtQuery = modules.comments.query.filter_by(location = location).all()
        cant = 0
        total = 0
        for rate in rtQuery:
            cant = cant + 1
            total = total + rate.rate
        if(cant > 0):
            result = total / cant
        else:
            result = 0
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return None

    #return jsonify(exito = "true", avgRate = result)   
    return round(result, 2)
