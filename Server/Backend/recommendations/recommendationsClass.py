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
        rcQuery = modules.recommendations.query.filter_by(userSrc = userSrc, userDst = userDst, location = location).first()
        if (rcQuery is not None):
            if(rcQuery.state == "P"):
                return jsonify(exito = "false", mensaje = "No se puede recomendar porque ya has recomendado")
            return jsonify(exito = "false", mensaje = "No se puede recomendar porque el usuario ya ha aceptado la recomendación")
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

@recommendationsClass.route('/recommendations/acceptRecomendation', methods=['POST']) #Acepta una recomendación
def acceptRecomendation():
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

        rcQuery = modules.recommendations.query.filter_by(userDst = user, state = state).paginate(per_page=quant, page=page)
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


@recommendationsClass.route('/recommendations/listAcceptedRecommendations', methods=['POST']) #Muestra las recomendaciones que has aceptado
def AcceptedRecommendations():
    user, page, quant = RecommendationsFunct.initParametersList()
    state = "A"
    try:
        lista = []
        if(RecommendationsFunct.checkPagination(page, quant, user, state) is False):
            return jsonify(exito = "true", list = lista)

        rcQuery = modules.recommendations.query.filter_by(userDst = user, state = state).paginate(per_page=quant, page=page)

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
