from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

visitedClass = Blueprint("visitedClass", __name__)

#############################################   Funciones de Lugares Visitados   #############################################

@visitedClass.route('/location/newLocationVisited', methods=['POST'])
def newLocationVisited():    
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    locationVisited = modules.visited(user = user, location = location)
    try:
        modules.sqlAlchemy.session.add(locationVisited)
        modules.sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   id_visited = locationVisited.id_visited,
                   user=locationVisited.user,
                   location=locationVisited.location,
                   date_visited=locationVisited.date_visited.strftime('%Y-%m-%d'))
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")  

@visitedClass.route('/location/readHistory', methods=['POST']) #Devuelve un listado de lugares, las fechas y las veces que se visitaron
def readHistory():
    json_data = request.get_json()
    user = json_data["user"]
    try:
        vtQuery = modules.visited.query.filter_by(user = user).order_by(modules.visited.date_visited).all()
        visitedDict = {}
        for locationVisited in vtQuery:
            date = locationVisited.date_visited.strftime('%Y-%m-%d')
            if(visitedDict.get(date) is None):
                visitedDict[date] = {locationVisited.location : 1}
            elif(locationVisited.location not in visitedDict[date]):
                d1 = {locationVisited.location : 1}
                visitedDict[date].update(d1)
            else:
                visitedDict[date][locationVisited.location] += 1
        return jsonify(exito = "true", historial = visitedDict) 
    except Exception as e:
        print("Error leyendo el historial:", repr(e))
        return jsonify(exito = "false")
  


@visitedClass.route('/location/deleteHistory', methods=['DELETE']) #No se si es necesario una funcion para eliminar los lugares visitados
def deleteHistory():
    json_data = request.get_json()
    user = json_data["user"]
    try:
        vtQuery = modules.visited.query.filter_by(user  = user).delete()
        if(vtQuery == 0):
            print("Error al borrar el historial")
            return jsonify(exito = "false")
        modules.sqlAlchemy.session.commit()
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando el historial: ", repr(e))
        return jsonify(exito = "false")  
