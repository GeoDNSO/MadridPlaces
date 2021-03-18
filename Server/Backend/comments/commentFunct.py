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