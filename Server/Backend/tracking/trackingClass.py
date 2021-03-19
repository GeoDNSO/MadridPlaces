from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import tracking.trackingFunct as TrackingFunct
#Funciones auxiliares que no usan rutas

#Tracking 
import atexit
from apscheduler.schedulers.background import BackgroundScheduler

trackingClass = Blueprint("trackingClass", __name__)


@trackingClass.route('/user/pointTracked', methods=['POST'])
def pointTracked():
    json_data = request.get_json()
    user = json_data["user"]
    latitude = json_data["latitude"]
    longitude = json_data["longitude"]

    createTrack = modules.tracking(user = user, latitude = latitude, longitude = longitude)
    
    try:
    	tcQuery = modules.tracking.query.filter_by(user = user, latitude = latitude, longitude = longitude).first()
    	if(tcQuery is None): #Si el usuario no se ha movido después de X minutos, no se mete en la BD? Esto hablarlo con Dani
	        modules.sqlAlchemy.session.add(createTrack)
	        modules.sqlAlchemy.session.commit()
	        return jsonify(
	                   exito = "true",
	                   user = createTrack.user,
	                   latitude = createTrack.latitude,
	                   longitude = createTrack.longitude,
	                   passed = createTrack.passed)
    	else:
	        return jsonify(exito = "true")
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

#Tarea automatica que elimina el tracking cada 1 hora

scheduler = BackgroundScheduler()
scheduler.add_job(func=TrackingFunct.deleteTrack, trigger="interval", seconds=3600)
scheduler.start()

# Shut down the scheduler when exiting the app
atexit.register(lambda: scheduler.shutdown())