from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
#Para imagenes
import requests, json 
import location.locationFunct as LocationFunct
import visited.visitedFunct as VisitedFunct


def newPendingVisit(userDst, location):
  return VisitedFunct.newPendingVisit(userDst, location)

def initParameters():
    json_data = request.get_json()
    userSrc = json_data["userSrc"] #El user que te ha enviado la recomendación
    userDst = json_data["userDst"] #Este user es el user logueado
    location = json_data["location"]
    return userSrc, userDst, location

def initParametersList():
    json_data = request.get_json()
    user = json_data["user"]
    page = json_data["page"]
    quant = json_data["quant"]
    return user, page, quant

def checkPagination(page, quant, user, state):
  tam = modules.recommendations.query.filter_by(userDst=user, state=state).count()
  comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
  #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
  if(comp >= quant):
    return False
  return True

def checkPaginationSent(page, quant, user):
  tam = modules.recommendations.query.filter_by(userSrc=user).count()
  comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
  #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
  if(comp >= quant):
    return False
  return True

def completeList(recommendation):
    obj = {}
    obj["userSrc"] = recommendation.userSrc
    obj["userDst"] = recommendation.userDst
    obj["location"] = recommendation.location
    obj["state"] = recommendation.state
    return obj