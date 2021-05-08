from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
#Para imagenes
import requests, json 
import user.userFunct as UserFunct

def initParameters():
    json_data = request.get_json()
    userSrc = json_data["userSrc"]
    userDst = json_data["userDst"]
    return userSrc, userDst

def initParametersFriends():
    json_data = request.get_json()
    user = json_data["user"]
    friend = json_data["friend"]
    return user, friend

def countFriends(user):
	cont = modules.friends.query.filter(((modules.friends.userSrc == user) | (modules.friends.userDst == user))).count()
	return cont

def completeFriendList(user, state, date):
	obj = {}
	obj["user"] = UserFunct.friendData(modules.user.query.filter_by(nickname = user).first())
	obj["state"] = state
	obj["date"] = date
	return obj