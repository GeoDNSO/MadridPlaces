from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

import base64
import requests, json 
import pyimgur
import PIL.Image as Image
import io
import os


def showUser(user): #Practicamente lo mismo que el profile, pero de algún otro usuario. Es una función auxiliar
    userQuery = modules.user.query.filter_by(nickname=user).first()
    if userQuery is None:
        return None
    
    return {exito : "true",
                   nickname : userQuery.nickname,
                   name : userQuery.name,
                   surname : userQuery.surname,
                   email : userQuery.email,
                   password : userQuery.password,
                   gender : userQuery.gender,
                   birth_date : userQuery.birth_date.strftime("%Y-%m-%d"),
                   city : userQuery.city,
                   rol : userQuery.rol}

def showPicture(user):
    userQuery = modules.user.query.filter_by(nickname = user).first()
    if userQuery is None:
        return None
    
    return userQuery.profile_image

def completeList(usuario):
  u = {
  "nickname":usuario.nickname,
  "name":usuario.name,
  "surname":usuario.surname,
  "email":usuario.email,
  "password":usuario.password,
  "gender":usuario.gender,
  "birth_date":usuario.birth_date.strftime("%Y-%m-%d"),
  "city":usuario.city,
  "rol":usuario.rol,
  "profile_image": usuario.profile_image
  }
  return u


def jsonifiedList(usuario, password):
  return jsonify(
    exito = "true",
    nickname = usuario.nickname,
    name=usuario.name,
    surname=usuario.surname,
    email=usuario.email,
    password=password,
    gender=usuario.gender,
    birth_date=usuario.birth_date.strftime("%Y-%m-%d"),
    city=usuario.city,
    rol=usuario.rol,
    profile_image=usuario.profile_image) #Se devuelve en binario

def jsonifiedList2(usuario):
  return jsonify(
    exito = "true",
    nickname = usuario.nickname,
    name=usuario.name,
    surname=usuario.surname,
    email=usuario.email,
    password=usuario.password,
    gender=usuario.gender,
    birth_date=usuario.birth_date.strftime("%Y-%m-%d"),
    city=usuario.city,
    rol=usuario.rol,
    profile_image=usuario.profile_image) #Se devuelve en binario

def decode64Img(codedImg): #i para enumerar todas las imágenes
  image = base64.b64decode(str(codedImg))       
  img = Image.open(io.BytesIO(image))
  img.save("perfil.png", "png")
  return "perfil.png"

def uploadImg(imgTemp):
  CLIENT_ID = "9447315b37b3ece" #ID público de mi cuenta de IMGUR
  im = pyimgur.Imgur(CLIENT_ID)
  uploaded_image = im.upload_image(imgTemp, title="TFG Madrid Places")
  return uploaded_image.link

def delImgTemp(imgTemp):
  try:
    os.remove(imgTemp)

  except Exception as e:
    print("Error eliminando la imagen temporal: ", repr(e))