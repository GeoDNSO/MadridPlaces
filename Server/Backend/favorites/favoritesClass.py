from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import location.locationFunct as LocationFunct


favoritesClass = Blueprint("favoritesClass", __name__)

@favoritesClass.route('/location/newFavoritePlace', methods=['POST'])
def newFavoritePlace():
    json_data = request.get_json()
    location = json_data["location"]
    user = json_data["user"]
    try:
        fvQuery = modules.favorites.query.filter_by(location=location, user=user).first()
        if(fvQuery is None):
            createFavorite = modules.favorites(location = location, user = user)
            modules.sqlAlchemy.session.add(createFavorite)
            modules.sqlAlchemy.session.commit()
            return jsonify(
                exito = "true")

        else:
            print("Ya está en favoritos")
            return jsonify(exito = "false")

    except Exception as e:
        print("Error añadiendo favorito:", repr(e))
        return jsonify(exito = "false")


@favoritesClass.route('/location/deleteFavoritePlace', methods=['DELETE'])
def deleteFavoritePlace():
    json_data = request.get_json()
    location = json_data["location"]
    user = json_data["user"]
    try:
        fvQuery = modules.favorites.query.filter_by(location = location, user = user).delete()
        modules.sqlAlchemy.session.commit()
        if(fvQuery == 0):
            print("Error al borrar la valoracion de twitter")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error: ", repr(e))
        return jsonify(exito = "false")  

@favoritesClass.route('/location/listFavoritesPlaces', methods=['POST'])
def showComments():
    json_data = request.get_json()
    user = json_data["user"] 
    page = json_data["page"] #Mostrar de X en X     
    quant = json_data["quant"]
    search = "%{}%".format(json_data["search"])
    try:
        tam = modules.favorites.query.filter(modules.favorites.user == user, modules.favorites.location.like(search)).count()
        comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5

        if(comp >= quant):
            return jsonify(exito = "true", listFavorites = [])

        lfvQuery = modules.favorites.query.filter(modules.favorites.user == user, modules.favorites.location.like(search)).order_by(modules.favorites.location).paginate(per_page=quant, page=page)
        if lfvQuery is None:
            return jsonify(exito = "false")
        lista = []
        for favorite in lfvQuery.items:
            lcQuery = modules.location.query.filter_by(name=favorite.location).first()
            obj = LocationFunct.completeList(lcQuery, favorite.user)
            lista.append(obj)
        return jsonify(exito = "true", listFavorites = lista)

    except Exception as e:
        print("Error listando lugares favoritos:", repr(e))
        #return jsonify(exito = "false")
        return jsonify(exito = "false")
