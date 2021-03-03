from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

#Funciones auxiliares que no usan rutas
import rates.rateFunct as RateFunct

ratesClass = Blueprint("ratesClass", __name__)

#############################################   Funciones Valoraciones   #############################################


@ratesClass.route('/location/newRate', methods=['POST'])
def newRate():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    rate = json_data["rate"]
    createRate = modules.ratings(user = user, location = location, rate = rate)
    
    try:
        rtQuery = modules.ratings.query.filter_by(user = user, location = location).first()
        if(rtQuery is None):
            modules.sqlAlchemy.session.add(createRate)
        else:
        	rtQuery.rate = createRate.rate

        modules.sqlAlchemy.session.commit()
        
        return jsonify(
                exito = "true",
                id_rate = createRate.id_rate,
                user=createRate.user,
                location=createRate.location,
                rate=createRate.rate)

    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

@ratesClass.route('/location/deleteRate', methods=['DELETE'])
def deleteRate():
    json_data = request.get_json()
    id_rate = json_data["id_rate"]
    try:
        rtQuery = modules.ratings.query.filter_by(id_rate = id_rate).delete()
        modules.sqlAlchemy.session.commit()
        if(rtQuery == 0):
            print("Error al borrar la valoracion")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando comentario: ", repr(e))
        return jsonify(exito = "false")  


@ratesClass.route('/location/modifyRate', methods=['POST'])
def modifyRate():
    json_data = request.get_json()
    rate = json_data["rate"]
    id_rate = json_data["id_rate"]
    try:
        modifiedRate = modules.ratings.query.filter_by(id_rate = id_rate).first()
        modifiedRate.rate = rate
        modules.sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando la valoración:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

@ratesClass.route('/location/top100Rated', methods=['GET']) #Devuelve los 100 lugares mejores valorados en una lista de sus nombres y valoraciones
def top100Rated():
    try:
        rtQuery = modules.ratings.query.order_by(modules.ratings.location).all()
        topDict = {}
        for rate in rtQuery:
            location = rate.location
            if(topDict.get(location) is None): #Crea un diccionario siendo la clave el lugar y el valor su valoración media
                topDict[location] = RateFunct.averageRate(location)
        sortedTop = dict(sorted(topDict.items(), key=lambda item: item[1],reverse=True)) #Ordena el diccionario en base a sus valores
        while(len(sortedTop) > 100): #Elimina los ultimos pares hasta que haya al menos 100 
            sortedTop.popitem()
        print(sortedTop)
        return jsonify(exito = "true", TOP100 = sortedTop)  
    except Exception as e:
        print("Error mostrando el TOP 100 de los lugares ", repr(e))
        return jsonify(exito = "false")  
