from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import visited.visitedFunct as VisitedFunct

visitedClass = Blueprint("visitedClass", __name__)

#############################################   Funciones de Lugares Visitados   #############################################

@visitedClass.route('/location/newPendingToVisit', methods=['POST']) #Añade a la lista de lugares pendientes por visitar
def newLocationVisited():    
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    try:
        vtQuery = modules.visited.query.filter_by(user = user, location = location).first() #Comprobar primero si ya existe en la BD
        if(vtQuery is not None):
            print("No se ha podido añadir a la lista de lugares pendientes porque el usuario ya lo ha visitado o ya está en la lista")
            return jsonify(exito = "false")
        locationVisited = modules.visited(user = user, location = location, state = "P")
        modules.sqlAlchemy.session.add(locationVisited)
        modules.sqlAlchemy.session.commit()
        print("Se ha añadido correctamente")
        return jsonify(exito = "true")
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")  

@visitedClass.route('/location/deletePendingToVisit', methods=['DELETE']) #No se si es necesario una funcion para eliminar los lugares visitados
def deletePendingToVisit():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    try:
        vt2Query = modules.visited.query.filter_by(user  = user, location = location, state = "P").delete()
        if(vt2Query == 0):
            print("Error al borrar el lugar en la lista de lugares pendientes por visitar")
            return jsonify(exito = "false")
        modules.sqlAlchemy.session.commit()
        print("Se ha eliminado correctamente")
        return jsonify(exito = "true")
    except Exception as e:
        print("Error", repr(e))
        return jsonify(exito = "false")  

@visitedClass.route('/location/newPLaceVisited', methods=['POST']) #Añade a la lista de lugares pendientes por visitar
def newPLaceVisited():    
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    try:
        vtQuery = modules.visited.query.filter_by(user = user, location = location, state = "V").first() #state --> P = Pending | V = Visited
        if(vtQuery is not None): #Comprueba si ya esta visitado
            print("Ya está visitado")
            return jsonify(exito = "false")
        vt2Query = modules.visited.query.filter_by(user = user, location = location, state = "P").first()
        if(vt2Query is not None): #Comprueba si está en la lista de lugares pendientes por visitar
            vt2Query.state = "V" 
        else: #Si es un lugar que has visitado sin estar en la lista de pendientes por visitar
            locationVisited = modules.visited(user = user, location = location, state = "V")
            modules.sqlAlchemy.session.add(locationVisited)
        modules.sqlAlchemy.session.commit()
        print("Se ha añadido correctamente")
        return jsonify(exito = "true")
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")  

@visitedClass.route('/location/deletePlaceVisited', methods=['DELETE']) #No se si es necesario una funcion para eliminar los lugares visitados
def deletePlaceVisited():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    try:
        vt2Query = modules.visited.query.filter_by(user  = user, location = location, state = "V").delete()
        if(vt2Query == 0):
            print("Error al borrar el lugar visitado")
            return jsonify(exito = "false")
        modules.sqlAlchemy.session.commit()
        print("Se ha eliminado correctamente")
        return jsonify(exito = "true")
    except Exception as e:
        print("Error", repr(e))
        return jsonify(exito = "false")  

@visitedClass.route('/location/listPendingToVisit', methods=['POST']) #Devuelve un listado de lugares pendientes por visitar
def listPendingToVisit():
    json_data = request.get_json()
    user = json_data["user"]
    page = json_data["page"]
    quant = json_data["quant"]
    lista = []
    try:
        if(VisitedFunct.checkPagination(user, page, quant, "P") is False):
            return jsonify(exito = "true", list = lista)
        vtQuery = modules.visited.query.filter_by(user = user, state = "P").order_by(modules.visited.date_visited).paginate(per_page=quant, page=page)
        if(vtQuery is not None):
            for pendingVisit in vtQuery.items:
                obj = VisitedFunct.completeList(pendingVisit.location, user)
                lista.append(obj)
            print("success")
            return jsonify(
                    exito = "true",
                    list = lista)
        return jsonify(exito = "false") 
    except Exception as e:
        print("Error leyendo el historial:", repr(e))
        return jsonify(exito = "false")
  
@visitedClass.route('/location/listVisitedPLaces', methods=['POST']) #Devuelve un listado de lugares pendientes por visitar
def listVisitedPLaces():
    json_data = request.get_json()
    user = json_data["user"]
    page = json_data["page"]
    quant = json_data["quant"]
    lista = []
    try:
        if(VisitedFunct.checkPagination(user, page, quant, "V") is False):
            return jsonify(exito = "true", list = lista)
        vtQuery = modules.visited.query.filter_by(user = user, state = "V").order_by(modules.visited.date_visited).paginate(per_page=quant, page=page)
        if(vtQuery is not None):
            for pendingVisit in vtQuery.items:
                obj = VisitedFunct.completeList(pendingVisit.location, user)
                lista.append(obj)
            print("success")
            return jsonify(
                    exito = "true",
                    list = lista)
        return jsonify(exito = "false") 
    except Exception as e:
        print("Error leyendo el historial:", repr(e))
        return jsonify(exito = "false")

@visitedClass.route('/location/countPlaceVisited', methods=['POST']) #Devuelve un listado de lugares, las fechas y las veces que se visitaron
def countPlaceVisited():
    json_data = request.get_json()
    user = json_data["user"]
    try:
        vtQuery = modules.visited.query.filter_by(user = user).count() #Los lugares repetidos se contarán también? Preguntar a Dani qué prefiere
        return jsonify(exito = "true", nVisited = vtQuery) 
    except Exception as e:
        print("Error leyendo el historial:", repr(e))
        return jsonify(exito = "false")