from flask import Blueprint
from flask import request
from flask import jsonify
from pprint import pprint
import modules

import recommendations.recommendationsFunct as RecommendationsFunct

recommendationsClass = Blueprint("recommendationsClass", __name__)


@recommendationsClass.route('/recommendations/newRecommentation', methods=['POST']) #Crea una recomendación
def newRecommentation():
    userSrc, userDst, location = RecommendationsFunct.initParameters()
    state = "P"   #--> P = pending A = acepted.  Si el usuario rechaza, se elimina directamente 

    try:
        userQuery = modules.user.query.filter_by(nickname = userDst).first() #Comprueba si existe el nickname
        if (userQuery is None):
            return jsonify(exito = "false", mensaje = "No existe el usuario")
        if (userSrc == userDst): #Comprueba si es el mismo usuario
            return jsonify(exito = "false", mensaje = "No puedes recomendar el lugar a ti mismo")
        rcQuery = modules.recommendations.query.filter_by(userSrc = userSrc, userDst = userDst, location = location).first() #Comprueba si ya existe la recomendacion
        if (rcQuery is not None):
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque ya has recomendado al usuario este lugar") 
        rc2Query = modules.recommendations.query.filter_by(userDst = userDst, location = location).first()
        if (rc2Query is not None):
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque ya le han recomendado al usuario este lugar")#Comprueba si ya le han recomendado
        vsQuery = modules.visited.query.filter_by(user = userDst, location = location).first() #Comprueba si ya existe en su lista de pendiente por visitar, o ya lo ha visitado
        if (vsQuery is not None):
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque el usuario ya ha visitado el lugar o lo tiene como pendiente por visitar")    

        newRc = modules.recommendations(userSrc = userSrc, userDst = userDst, location = location, state = state)
        modules.sqlAlchemy.session.add(newRc)
        modules.sqlAlchemy.session.commit()

        return jsonify(exito = "true", mensaje = "Se ha enviado la recomendación correctamente")
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false", mensaje = "Ha habido un error")

@recommendationsClass.route('/recommendations/deleteRecommendation', methods=['DELETE']) #Borra una recomendación
def deleteRecommendation():
    userSrc, userDst, location = RecommendationsFunct.initParameters()
    try:
        rcQuery = modules.recommendations.query.filter_by(userSrc = userSrc, userDst = userDst, location = location).delete()
        modules.sqlAlchemy.session.commit()
        if (rcQuery == 0):
            return jsonify(exito = "false", mensaje = "No existe dicha recomendación")
        return jsonify(exito = "true", mensaje = "Se ha eliminado la recomendación")
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false", mensaje = "Ha habido un error")

@recommendationsClass.route('/recommendations/acceptRecommendation', methods=['POST']) #Acepta una recomendación
def acceptRecommendation():
    userSrc, userDst, location = RecommendationsFunct.initParameters()
    state = "A"   #--> P = pending A = acepted.  Si el usuario rechaza, se elimina directamente 

    try:
        rcQuery = modules.recommendations.query.filter_by(userSrc = userSrc, userDst = userDst, location = location).first()
        if(rcQuery is None):
            return jsonify(exito = "false", mensaje = "No existe dicha recomendación")
        if(rcQuery.state == "A"):
            return jsonify(exito = "false", mensaje = "Ya has aceptado esta recomendación")

        rcQuery.state = state
        modules.sqlAlchemy.session.commit()
        if(RecommendationsFunct.newPendingVisit(userDst, location) is False): #Añade en la lista de lugares pendientes por visitar.
        	return jsonify(exito = "true", mensaje = "Se ha aceptado la recomendación pero ya lo has visitado o lo tienes en tu lista de lugares pendientes")
        return jsonify(exito = "true", mensaje = "Se ha aceptado la recomendación")
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false", mensaje = "Ha habido un error")

@recommendationsClass.route('/recommendations/listPendingRecommendations', methods=['POST']) #Muestra las recomendaciones que has recibido
def PendingRecommendations():
    user, page, quant = RecommendationsFunct.initParametersList()
    state = "P"
    try:
        lista = []
        if(RecommendationsFunct.checkPagination(page, quant, user, state) is False):
            return jsonify(exito = "true", list = lista)

        rcQuery = modules.recommendations.query.filter_by(userDst = user, state = state).order_by(modules.recommendations.created).paginate(per_page=quant, page=page)
        for recommendation in rcQuery.items:
            obj = RecommendationsFunct.completeList(recommendation)
            lista.append(obj)
        print("success")
        return jsonify(
                exito = "true",
                list = lista)
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false")

@recommendationsClass.route('/recommendations/listRecommendationsSent', methods=['POST']) #Muestra las recomendaciones que has aceptado
def AcceptedRecommendations():
    user, page, quant = RecommendationsFunct.initParametersList()
    try:
        lista = []
        if(RecommendationsFunct.checkPaginationSent(page, quant, user) is False):
            return jsonify(exito = "true", list = lista)

        rcQuery = modules.recommendations.query.filter_by(userSrc = user).order_by(modules.recommendations.created).paginate(per_page=quant, page=page)

        for recommendation in rcQuery.items:
            obj = RecommendationsFunct.completeList(recommendation)
            lista.append(obj)
        print("success")
        return jsonify(
                exito = "true",
                list = lista)
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false")
