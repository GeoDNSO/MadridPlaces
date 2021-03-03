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