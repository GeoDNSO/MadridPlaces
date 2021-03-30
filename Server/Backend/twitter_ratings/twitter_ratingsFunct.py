from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import comments.commentFunct as CommentFunct
import rates.rateFunct as RateFunct

def averageTwitterRate(location): #No se usa
    try:
        rtQuery = modules.twitter_ratings.query.filter_by(location = location).first()
        if rtQuery is None:
            return 0

    except Exception as e:
        print("Error sacando la puntuación por twitter:", repr(e))
        #return jsonify(exito = "false")
        return None

    return rtQuery.twitterRate


def numberOfTwitterComments(location): #A lo mejor no se necesita un URL 
    try:
        cmQuery = modules.twitter_ratings.query.filter_by(location = location).count()
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        return None  
    return cmQuery 