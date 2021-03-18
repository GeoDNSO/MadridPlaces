from flask import Blueprint
from flask import request
from flask import jsonify
from pprint import pprint
#Contiene las clases de la BD
import modules

#Funciones auxiliares que no usan rutas
import location.locationFunct as LocationFunct

from geopy.distance import geodesic #Para calcular la proximidad de los lugares



locationClass = Blueprint("locationClass", __name__)

#############################################   Funciones Lugares   #############################################

@locationClass.route('/location/newLocation', methods=['POST'])
def newLocation():
    json_data = request.get_json()
    name = json_data["name"]
    description = json_data["description"]
    coordinate_latitude = json_data["coordinate_latitude"]
    coordinate_longitude = json_data["coordinate_longitude"]
    type_of_place = json_data["type_of_place"]
    road_class = json_data["road_class"]
    road_name = json_data["road_name"]
    road_number = json_data["road_number"]
    zipcode = json_data["zipcode"]
    listImg = json_data["imagesList"]
    #affluence = json_data["affluence"]
    createLocation = modules.location(name = name, description = description, coordinate_latitude = coordinate_latitude, 
        coordinate_longitude = coordinate_longitude, type_of_place = type_of_place, road_class = road_class, road_name = road_name,
        road_number = road_number, zipcode = zipcode)
    
    try:
        modules.sqlAlchemy.session.add(createLocation)
        modules.sqlAlchemy.session.commit()
        i = 0
        #for decodedImg in listImg:
        list = listImg.replace("[", "").replace("]", "").split(",")
        for codedec in list:
            image = LocationFunct.decode64Img(codedec, i) #image = imgTemp1.jpg
            url = LocationFunct.uploadImg(image) # url = https://www.imgur.com/imgTemp1.jpg
            
            createLocationImage = modules.location_images(location_name = name, image=url)
            modules.sqlAlchemy.session.add(createLocationImage)

            LocationFunct.delImgTemp(image) #Elimina la imagen temporal almacenada
            i = i + 1

        modules.sqlAlchemy.session.commit()
        return LocationFunct.jsonifiedPlace(createLocation)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

@locationClass.route('/location/modifyLocation', methods=['POST'])
def modifyLocation():

    json_data = request.get_json()
    name = json_data["name"]
    description = json_data["description"]
    coordinate_latitude = json_data["coordinate_latitude"]
    coordinate_longitude = json_data["coordinate_longitude"]
    road_class = json_data["road_class"]
    road_name = json_data["road_name"]
    road_number = json_data["road_number"]
    zipcode = json_data["zipcode"]
    type_of_place = json_data["type_of_place"]
    affluence = json_data["affluence"]
    try:
        modifiedLocation = modules.location.query.filter_by(name=name).first()
        modifiedLocation.name = name
        modifiedLocation.description = description
        modifiedLocation.coordinate_latitude = coordinate_latitude
        modifiedLocation.coordinate_longitude = coordinate_longitude
        modifiedLocation.type_of_place = type_of_place
        modifiedLocation.road_class = road_class
        modifiedLocation.road_name = road_name
        modifiedLocation.road_number = road_number
        modifiedLocation.zipcode = zipcode
        modifiedLocation.affluence = affluence
        modules.sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando lugar:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

@locationClass.route('/location/readLocation', methods=['GET', 'POST']) #No se usa 
def readLocation():
    json_data = request.get_json()
    name = json_data["name"]    

    lcQuery = modules.location.query.filter_by(name = name).first()

    if (lcQuery is not None):
        print("success")
        return LocationFunct.jsonifiedPlace(lcQuery)

    print("failure")
    return jsonify(exito = "false")    

@locationClass.route('/location/deleteLocation', methods=['DELETE'])
def deleteLocation():
    json_data = request.get_json()
    name = json_data["name"]    
    try:
        deleteQuery = modules.location.query.filter_by(name=name).delete()
        modules.sqlAlchemy.session.commit()
        if(deleteQuery == 0):
            print("Error al borrar el lugar:")
            return jsonify(exito = "false")
        return jsonify(exito = "true") 
    except Exception as e:
        print("Error borrando la fila :", repr(e))
        return jsonify(exito = "false")   

@locationClass.route('/location/listLocations', methods=['GET', 'POST'])
def listLocations():
    json_data = request.get_json()
    page = json_data["page"] #Mostrar de X en X     
    quant = json_data["quant"]
    try:
        tam = modules.location.query.count()
        comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
        #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
        if(comp >= quant):
            return jsonify(exito = "true", list = [])

        places = modules.location.query.paginate(per_page=quant, page=page)
        if(places is not None):
	        all_items = places.items
	        lista = []
	        for place in all_items:
		        obj = LocationFunct.completeList(place)
		        lista.append(obj)
	        print("success")
	        return jsonify(
	                exito = "true",
	                list = lista)

        print("failure")
        return jsonify(exito = "false")   
    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false") 

@locationClass.route('/location/readImages', methods=['POST']) #Devuelve una lista de URLs de las imagenes de un lugar específico
def readImages():
    json_data = request.get_json()
    name = json_data["name"]
    try:
        stQuery = modules.location_images.query.filter_by(location_name=name).all()
        lista = []
        for imagen in stQuery:
            lista.append({"image" : imagen.image})
        return jsonify(exito = "true", list = lista) 
    except Exception as e:
        print("Error mostrando las imágenes: ", repr(e))
        return jsonify(exito = "false")  

def dts(e):#Funcion auxiliar para ordenar la lista por proximidad
    return e['distance']

@locationClass.route('/location/listByProximity', methods=['GET', 'POST']) #Devolver una lista de 30 lugares más próximos
def listByProximity():
    json_data = request.get_json()
    userLatitude = json_data["latitude"]
    userLongitude = json_data["longitude"]
    radius = json_data["radius"] #No se sabe si el rango es estático o dinámico 
    nPlaces = json_data["nPlaces"] #Número de lugares que se quiere mostrar 10, 20, 50, 100
    try:
        user_coords = (userLatitude, userLongitude)
        places = modules.location.query.all()
        lista = [] #Lista con los resultados paginados
        if(places is not None):
	        for place in places:
	        	place_coords = (place.coordinate_latitude, place.coordinate_longitude)
	        	distance = geodesic(user_coords, place_coords).meters #Distancia calcula entre el usuario y el lugar en METROS
	        	if(distance <= radius): #Descartamos los lugares que no estén en el radio
		            obj = LocationFunct.completeList(place)
		            obj["distance"] = distance
		            lista.append(obj)
	        lista.sort(key=dts)
	        print("success")
	        return jsonify(
	                exito = "true",
	                list = lista[0:nPlaces])

        return jsonify(exito = "false")   

    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false") 

@locationClass.route('/location/listByCategory', methods=['GET', 'POST'])
def listByCategory():
    json_data = request.get_json()
    page = json_data["page"] #Mostrar de X en X     
    quant = json_data["quant"]
    category = json_data["category"]
    try:
        idCategory = LocationFunct.mapCategoryToInt(category) #Recoge el número asociado de la categoria
        tam = modules.location.query.filter_by(type_of_place = idCategory).count()

        comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
        #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
        if(comp >= quant):
            return jsonify(exito = "true", list = [])

        places = modules.location.query.filter_by(type_of_place = idCategory).paginate(per_page=quant, page=page)
        if(places is not None):
	        all_items = places.items
	        lista = []
	        for place in all_items:
	            obj = LocationFunct.completeList(place)
	            lista.append(obj)

	        print("success")
	        return jsonify(
	                exito = "true",
	                list = lista)

        print("failure")
        return jsonify(exito = "false")   
    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false") 

@locationClass.route('/location/listByCategoryAndProximity', methods=['GET', 'POST'])
def listByCategoryAndProximity():
    json_data = request.get_json()
    category = json_data["category"]
    userLatitude = json_data["latitude"]
    userLongitude = json_data["longitude"]
    radius = json_data["radius"] #Radio del usuario
    nPlaces = json_data["nPlaces"] #Número de lugares que se quiere mostrar 10, 20, 50, 100, Todos
    try:
        idCategory = LocationFunct.mapCategoryToInt(category) #Recoge el número asociado de la categoria
        places = modules.location.query.filter_by(type_of_place = idCategory)
        if(places is not None):
	        lista = []
	        user_coords = (userLatitude, userLongitude)
	        for place in places:
	        	place_coords = (place.coordinate_latitude, place.coordinate_longitude)
	        	distance = geodesic(user_coords, place_coords).meters #Distancia calcula entre el usuario y el lugar en METROS
		        if(distance <= radius): #Descartamos los lugares que no estén en el radio
		            obj = LocationFunct.completeList(place)
		            obj["distance"] = distance
		            lista.append(obj)
	        lista.sort(key=dts)
	        print("success")
	        return jsonify(
	                exito = "true",
	                list = lista[0:nPlaces])

        print("failure")
        return jsonify(exito = "false")   
    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false") 

@locationClass.route('/location/categories', methods=['GET'])
def categories():
    try:
        categories = modules.tPlace.query.all()
        lista = []
        for c in categories:
            lista.append(c.category)
        return jsonify(exito = "true", list = lista)

    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false") 