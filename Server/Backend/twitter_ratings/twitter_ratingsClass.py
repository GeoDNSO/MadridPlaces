from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

twitter_ratingsClass = Blueprint("twitter_ratingsClass", __name__)

@twitter_ratingsClass.route('/location/newTwitterRate', methods=['POST']) #No se usara
def newTwitterRate():
    json_data = request.get_json()
    location = json_data["location"]
    twitterRate = json_data["twitterRate"]
    createRate = modules.twitter_ratings(location = location, twitterRate = twitterRate)
    
    try:
        rtQuery = modules.twitter_ratings.query.filter_by(location = location).first()
        if(rtQuery is None):
            modules.sqlAlchemy.session.add(createRate)
        else:
        	rtQuery.twitterRate = createRate.twitterRate

        modules.sqlAlchemy.session.commit()
        
        return jsonify(
                exito = "true",
                id_twitterRate = createRate.id_twitterRate,
                location=createRate.location,
                twitterRate=createRate.twitterRate)

    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")


@twitter_ratingsClass.route('/location/deleteTwitterRate', methods=['DELETE']) #No se usara 
def deleteTwitterRate():
    json_data = request.get_json()
    id_twitterRate = json_data["id_twitterRate"]
    try:
        rtQuery = modules.twitter_ratings.query.filter_by(id_twitterRate = id_twitterRate).delete()
        modules.sqlAlchemy.session.commit()
        if(rtQuery == 0):
            print("Error al borrar la valoracion de twitter")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false")  


@twitter_ratingsClass.route('/location/modifyTwitterRate', methods=['POST'])
def modifyTwitterRate():
    json_data = request.get_json()
    twitterRate = json_data["twitterRate"]
    id_twitterRate = json_data["id_twitterRate"]
    try:
        modifiedRate = modules.twitter_ratings.query.filter_by(id_twitterRate = id_twitterRate).first()
        modifiedRate.twitterRate = twitterRate
        modules.sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando la valoración de twitter:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")