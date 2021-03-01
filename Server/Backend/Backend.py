#from flask import request
#from flask import jsonify

#Configuracion del flask
from app import app

#Contiene las clases de la BD
import modules

#Clases que contienen las rutas de las peticiones
from user.userClass import userClass
from location.locationClass import locationClass
from comments.commentsClass import commentsClass
from rates.ratesClass import ratesClass
from visited.visitedClass import visitedClass

#Todas las rutas divididas en Blueprints
app.register_blueprint(userClass)
app.register_blueprint(locationClass)
app.register_blueprint(commentsClass)
app.register_blueprint(ratesClass)
app.register_blueprint(visitedClass)


#@app.route('/prueba/imagen', methods=['GET'])
#def urlImagen():
#    return jsonify(exito = "true", url = '/Users/GEOgang/Documents/GitHub/TFGTwitter/Fotos/prueba.jpg')

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True) #Host 0.0.0.0 permite a cualquier máquina interaccionar con el Flask