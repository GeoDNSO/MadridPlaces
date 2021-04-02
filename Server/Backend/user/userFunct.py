from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

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
  "profile_image": str(usuario.profile_image)
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
    profile_image=str(usuario.profile_image)) #Se devuelve en binario

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
    profile_image=str(usuario.profile_image)) #Se devuelve en binario
