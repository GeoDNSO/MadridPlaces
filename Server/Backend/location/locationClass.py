from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

#Funciones auxiliares que no usan rutas
import comments.commentFunct as CommentFunct
import rates.rateFunct as RateFunct
import location.locationFunct as LocationFunct

locationClass = Blueprint("locationClass", __name__)

#############################################   Funciones Lugares   #############################################

@locationClass.route('/location/newLocation', methods=['POST']) #No se usará
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
    #affluence = json_data["affluence"]
    createLocation = modules.location(name = name, description = description, coordinate_latitude = coordinate_latitude, 
        coordinate_longitude = coordinate_longitude, type_of_place = type_of_place, road_class = road_class, road_name = road_name,
        road_number = road_number, zipcode = zipcode)
    
    try:
        modules.sqlAlchemy.session.add(createLocation)
        modules.sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   name=createLocation.name,
                   description=createLocation.description,
                   coordinate_latitude=createLocation.coordinate_latitude,
                   coordinate_longitude=createLocation.coordinate_longitude,
                   type_of_place=createLocation.type_of_place,
                   city=createLocation.city,
                   road_class=createLocation.road_class,
                   road_name=createLocation.road_name,
                   road_number=createLocation.road_number,
                   zipcode=createLocation.zipcode,
                   affluence=createLocation.affluence)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

@locationClass.route('/location/modifyLocation', methods=['POST']) #No se usará
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

@locationClass.route('/location/readLocation', methods=['GET', 'POST'])
def readLocation():
    json_data = request.get_json()
    name = json_data["name"]    

    lcQuery = modules.location.query.filter_by(name = name).first()

    if (lcQuery is not None):
        print("success")
        return jsonify(
                exito = "true",
                name = lcQuery.name,
                description=lcQuery.description,
                coordinate_latitude=lcQuery.coordinate_latitude,
                coordinate_longitude=lcQuery.coordinate_longitude,
                road_class=lcQuery.road_class,
                road_name=lcQuery.road_name,
                road_number=lcQuery.road_number,
                zipcode=lcQuery.zipcode,
                type_of_place=lcQuery.type_of_place,
                city=lcQuery.city,
                affluence=lcQuery.affluence)

    print("failure")
    return jsonify(exito = "false")    

@locationClass.route('/location/deleteLocation', methods=['DELETE']) #No se usará
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

    places = modules.location.query.paginate(per_page=quant, page=page)
    if (places is not None):
        all_items = places.items
        lista = []
        for place in all_items:
            imageList = LocationFunct.listImages(place.name)
            avgRate = RateFunct.averageRate(place.name)
            obj = {"name" : place.name,
            "description":place.description,
            "coordinate_latitude":place.coordinate_latitude,
            "coordinate_longitude":place.coordinate_longitude,
            "type_of_place":place.type_of_place,
            "city":place.city,
            "road_class":place.road_class,
            "road_name":place.road_name,
            "road_number":place.road_number,
            "zipcode":place.zipcode,
            "affluence":place.affluence,
            "imageList" : imageList,
            "rate" : avgRate }
            lista.append(obj)

        print("success")
        return jsonify(
                exito = "true",
                list = lista)

    print("failure")
    return jsonify(exito = "false")   


@locationClass.route('/location/stats', methods=['POST']) #Devuelve el listado de comentarios, los datos del lugar y su valoracion
def stats():
    json_data = request.get_json()
    name = json_data["name"] 
    try:
        stQuery = modules.location.query.filter_by(name=name).first()
        comments = CommentFunct.listComments(name)
        avgRate = RateFunct.averageRate(name)
        return jsonify(
                exito = "true",
                name = stQuery.name,
                description=stQuery.description,
                coordinate_latitude=stQuery.coordinate_latitude,
                coordinate_longitude=stQuery.coordinate_longitude,
                type_of_place=stQuery.type_of_place,
                city=stQuery.city,
                road_class=stQuery.road_class,
                road_name=stQuery.road_name,
                road_number=stQuery.road_number,
                zipcode=stQuery.zipcode,
                affluence=stQuery.affluence,
                rate = avgRate,
                appComments = comments)

    except Exception as e:
        print("Error mostrando las estadisticas: ", repr(e))
        return jsonify(exito = "false")  

@locationClass.route('/location/readImages', methods=['POST'])
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