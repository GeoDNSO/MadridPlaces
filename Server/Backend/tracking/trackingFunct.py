import modules
import time
from datetime import datetime

def deleteTrack(): #Recorre todos los puntos trackeados de la BD y si ya han pasado 1 hora o más, se eliminan
	tcQuery = modules.tracking.query.all()
	dt = datetime.today()  # Recoge los segundos actuales
	currentTime = dt.timestamp()
	try:
		if tcQuery is not None: #Si no hay puntos, no hace nada
			for point in tcQuery:
				seconds = int(currentTime - point.passed.timestamp()) #Se obtiene los segundos desde que se creo el punto
				if(seconds >= 3600): #Si supera la hora, se elimina
					print("Se ha eliminado el punto con id: " + str(point.id_track))
					modules.tracking.query.filter_by(id_track = point.id_track).delete()
					modules.sqlAlchemy.session.commit()

	except Exception as e:
		print("Error borrando el tracking", repr(e))