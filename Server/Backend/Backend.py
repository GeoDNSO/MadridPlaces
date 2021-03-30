#Configuracion del flask
from app import app
#Clases que contienen las rutas de las peticiones
from user.userClass import userClass
from location.locationClass import locationClass
from comments.commentsClass import commentsClass
from rates.ratesClass import ratesClass
from visited.visitedClass import visitedClass
from tracking.trackingClass import trackingClass
from twitter_ratings.twitter_ratingsClass import twitter_ratingsClass

#Todas las rutas divididas en Blueprints
app.register_blueprint(userClass)
app.register_blueprint(commentsClass)
app.register_blueprint(ratesClass)
app.register_blueprint(visitedClass)
app.register_blueprint(locationClass)
app.register_blueprint(trackingClass)
app.register_blueprint(twitter_ratingsClass)

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True) #Host 0.0.0.0 permite a cualquier máquina interaccionar con el Flask