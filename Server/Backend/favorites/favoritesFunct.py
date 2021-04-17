from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import location.locationFunct as LocationFunct

def completeList(location, user):
	return LocationFunct.listByName(location,user)

def isFavorite(user, location):
    try:
        fvQuery = modules.favorites.query.filter_by(location = location.name, user = user).first()
        if (fvQuery is None):
            return "false"
        return "true"
    except Exception as e:
        print("Error:", repr(e))
        return "false"