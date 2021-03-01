from flask import Blueprint
from flask_sqlalchemy import SQLAlchemy
from flask import request
from flask import jsonify
from app import app
import modules


def listComments(location): #A lo mejor no se necesita un URL 
    #json_data = request.get_json()
    #location = json_data["location"]
    try:
        cmQuery = modules.comments.query.filter_by(location = location).all()
        lista = []
        for comment in cmQuery:
            cmDict = {comment.user : comment.comment}
            lista.append(cmDict)
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return None

    #return jsonify(exito = "true", comments = lista)  
    return lista 


