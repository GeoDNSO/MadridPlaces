from flask import request
from flask import jsonify
#Contiene las clases de la BD
import base64
import modules
import comments.commentFunct as CommentFunct
import twitter_ratings.twitter_ratingsFunct as TwitterRatingsFunct
import favorites.favoritesFunct as FavoritesFunct

#Para imagenes
import requests, json 
import pyimgur
import PIL.Image as Image
import io
import os


#def convertImgToURL(img,):

#Borrado automatico cada 1 hora de Trackeo de los usuarios


def decode64Img(codedImg, i): #i para enumerar todas las imágenes
  image = base64.b64decode(str(codedImg))       
  imagePath = "imgTemp" + str(i)
  img = Image.open(io.BytesIO(image))
  img.save(imagePath + ".png", "png")
  return imagePath + ".png"

def uploadImg(imgTemp):
  CLIENT_ID = "9447315b37b3ece"
  im = pyimgur.Imgur(CLIENT_ID)
  uploaded_image = im.upload_image(imgTemp, title="TFG Madrid Places")
  return uploaded_image.link

def delImgTemp(imgTemp):
  try:
    os.remove(imgTemp)

  except Exception as e:
    print("Error eliminando la imagen temporal: ", repr(e))

def checkPagination(page, quant):
  tam = modules.location.query.count()
  comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
  #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
  if(comp >= quant):
    return False
  return True

def checkPaginationTwitter(page, quant):
  tam = modules.twitter_ratings.query.count()
  comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
  #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
  if(comp >= quant):
    return False
  return True
def checkPaginationCategory(idCategory, page, quant):
  tam = modules.location.query.filter_by(type_of_place = idCategory).count()
  comp = (page   * quant) - tam 
  if(comp >= quant):
    return False
  return True

def initParameters(json_data):
  page = json_data["page"] #Mostrar de X en X     
  quant = json_data["quant"]
  user = json_data["user"]
  search = "%{}%".format(json_data["search"])
  return page, quant, user, search

def initParametersProximity(json_data):
  userLatitude = json_data["latitude"]
  userLongitude = json_data["longitude"]
  radius = json_data["radius"] #No se sabe si el rango es estático o dinámico 
  nPlaces = json_data["nPlaces"] #Número de lugares que se quiere mostrar 10, 20, 50, 100
  user = json_data["user"]
  search = "%{}%".format(json_data["search"])
  return userLatitude, userLongitude, radius, nPlaces, user, search
def listImages(location): #Devuelve una lista de imagenes de un lugar espefícico, usado para la función listLocations
    try:
        stQuery = modules.location_images.query.filter_by(location_name=location).all()
        lista = []
        for imagen in stQuery:
            lista.append({"image" : imagen.image})
        return lista
    except Exception as e:
        print("Error mostrando las imágenes: ", repr(e))
        return None


def jsonifiedPlace(createLocation):
    return jsonify(
                   exito = "true",
                   imageList = listImages(createLocation.name),
                   name=createLocation.name,
                   description=createLocation.description,
                   coordinate_latitude=createLocation.coordinate_latitude,
                   coordinate_longitude=createLocation.coordinate_longitude,
                   type_of_place=maptIntToCategory(createLocation.type_of_place),
                   city=createLocation.city,
                   road_class=createLocation.road_class,
                   road_name=createLocation.road_name,
                   road_number=createLocation.road_number,
                   zipcode=createLocation.zipcode,
                   affluence=createLocation.affluence)

def completeList(place, user):
    n_comments = CommentFunct.numberOfComments(place.name)
    imageList = listImages(place.name)
    avgRate = CommentFunct.averageRate(place.name)
    favorite = FavoritesFunct.isFavorite(user, place)
    obj = {"name" : place.name,
    "description":place.description,
    "coordinate_latitude":place.coordinate_latitude,
    "coordinate_longitude":place.coordinate_longitude,
    "type_of_place":maptIntToCategory(place.type_of_place),
    "city":place.city,
    "road_class":place.road_class,
    "road_name":place.road_name,
    "road_number":place.road_number,
    "zipcode":place.zipcode,
    "affluence":place.affluence,
    "imageList" : imageList,
    "rate" : avgRate,
    "n_comments" : n_comments,
    "favorite" : favorite}
    return obj

def listByTwitter(place, user, twitterRate):
    n_comments = TwitterRatingsFunct.numberOfTwitterComments(place.name)
    imageList = listImages(place.name)
    favorite = FavoritesFunct.isFavorite(user, place)
    obj = {"name" : place.name,
    "description":place.description,
    "coordinate_latitude":place.coordinate_latitude,
    "coordinate_longitude":place.coordinate_longitude,
    "type_of_place":maptIntToCategory(place.type_of_place),
    "city":place.city,
    "road_class":place.road_class,
    "road_name":place.road_name,
    "road_number":place.road_number,
    "zipcode":place.zipcode,
    "affluence":place.affluence,
    "imageList" : imageList,
    "rate" : twitterRate,
    "n_comments" : n_comments,
    "favorite" : favorite}
    return obj

def mapCategoryToInt(category):
	idCategories = {
	"Oficinas de Turismo" : 1,
	"Clubs" : 2,
	"Tiendas" : 3,
	"Restaurantes" : 4,
	"Alojamientos" : 5,
	"Monumentos" : 6,
	"Museos" : 7,
	"Templos" : 8
	}

	return idCategories[category]


def maptIntToCategory(idCategory):
  idCategories = {
  1 : "Oficinas de Turismo",
  2 : "Clubs",
  3 :"Tiendas",
  4 :"Restaurantes",
  5 :"Alojamientos",
  6 :"Monumentos",
  7 :"Museos",
  8 :"Templos"
  }

  return idCategories[idCategory]

