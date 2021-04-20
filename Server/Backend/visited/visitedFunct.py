from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import location.locationFunct as LocationFunct

def newPendingVisit(user, location): #Añade a la lista de pendientes por visitar
    vtQuery = modules.visited.query.filter_by(user = user, location= location).first()
    if(vtQuery is None): 
        pendingVisit = modules.visited(user = user, location = location, state = "P")
        modules.sqlAlchemy.session.add(pendingVisit)
        modules.sqlAlchemy.session.commit()
        return True
    return False

def checkPagination(user, page, quant, state):
  tam = modules.visited.query.filter_by(user = user, state = state).count()
  comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
  #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
  if(comp >= quant):
    return False
  return True

def completeList(location, user):
    return LocationFunct.listByName(location, user)

def isVisited(location,user):
    try:
        vtQuery = modules.visited.query.filter_by(location = location, user = user, state = "V").first()
        if (vtQuery is None):
            return ""
        return vtQuery.date_visited.strftime("%Y-%m-%d")
    except Exception as e:
        print("Error:", repr(e))
        return ""

#def isNear(user, location, userLatitude, userLongitude):