from flask import Blueprint
from flask import request
from flask import jsonify
from pprint import pprint
import modules

import visited.visitedFunct as VisitedFunct
import recommendations.recommendationsFunct as RecommendationsFunct

recommendationsClass = Blueprint("recommendationsClass", __name__)


@recommendationsClass.route('/recommendations/newRecommentation', methods=['POST']) #Crea una recomendación
def newRecommentation():
    userSrc, userDst, location = RecommendationsFunct.initParameters()
    state = "P"   #--> P = pending A = acepted.  Si el usuario rechaza, se elimina directamente 

    try:
        print(userSrc)
        print(userDst)
        print(location)
        userQuery = modules.user.query.filter_by(nickname = userDst).first()
        if (userQuery is None):
            return jsonify(exito = "false", mensaje = "No existe el usuario")
        if (userSrc == userDst):
            return jsonify(exito = "false", mensaje = "No puedes recomendar el lugar a ti mismo")
        rcQuery = modules.recommendations.query.filter_by(userSrc = userSrc, userDst = userDst, location = location).first()
        if (rcQuery is not None):
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque ya has recomendado al usuario este lugar")
        rc2Query = modules.recommendations.query.filter_by(userDst = userDst, location = location).first()
        if (rc2Query is not None):
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque ya le han recomendado al usuario este lugar")
        vsQuery = modules.visited.query.filter_by(user = userDst, location = location).first()
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
        if(VisitedFunct.newPendingVisit(userDst, location) is False): #Añade en la lista de lugares pendientes por visitar.
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

# POR AHORA NO SE USARÁ ESTA FUNCIÓN. LA FUNCIÓN PARA MOSTRAR LA LISTA DE LUGARES PENDIENTES POR VISITAR, ESTÁ EN visitedClass.py
@recommendationsClass.route('/recommendations/listAcceptedRecommendations', methods=['POST']) #Muestra las recomendaciones que has aceptado
def AcceptedRecommendations():
    user, page, quant = RecommendationsFunct.initParametersList()
    state = "A"
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
