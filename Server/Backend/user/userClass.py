from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules
import base64
import bcrypt #Para hashear las contraseñas, necesidad de instalar con pip install bcrypt
import PIL.Image as Image
import io
import os
import location.locationFunct as LocationFunct
userClass = Blueprint("userClass", __name__)

#############################################   Cifrado de Passwords   #############################################

def passwordCipher(password):#Servirá en el momento de crear una cuenta nueva o para modificar una contraseña
    #Generamos la salt aleatoria, con (rounds=16) lo ejecuta 16 veces para una mayor seguridad, pero realentiza el proceso
    hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt()) #Es necesario tenerlo como bytes para hacer el hash
    return hashed

def passwordVerify(password, pwdCipher): #Comprueba si la contraseña es correcta
    if (bcrypt.checkpw(password.encode(), pwdCipher.encode()) is False):
        return False
    return True


#Login
@userClass.route('/login/', methods=['GET', 'POST'])
def login():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    password = json_data["password"]

    #userQuery = user.query.filter_by(nickname = nickname, password = password).first()
    userQuery = modules.user.query.filter_by(nickname = nickname).first()

    if (userQuery is not None):
        if (passwordVerify(password, userQuery.password) is True):
            print("success")
            return jsonify(
                    exito = "true",
                    nickname = userQuery.nickname,
                    name=userQuery.name,
                    surname=userQuery.surname,
                    email=userQuery.email,
                    password=password,
                    gender=userQuery.gender,
                    birth_date=userQuery.birth_date.strftime("%Y-%m-%d"),
                    city=userQuery.city,
                    rol=userQuery.rol,
                    profile_image=userQuery.profile_image) #Se devuelve en binario
        else:
          print("Contraseña incorrecta")
    else:
      print("No existe el usuario")
    return jsonify(exito = "false")    

#Registro
@userClass.route('/registration/', methods=['GET', 'POST'])
def registration():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    name = json_data["name"]
    surname = json_data["surname"]
    email = json_data["email"]
    password = json_data["password"]
    gender = json_data["gender"]
    birth_date = json_data["birth_date"]
    profile_image = json_data["profile_image"]
    pwdCipher = passwordCipher(password)
    if(profile_image == ""):
      blobImage = ""
    else:
      blobImage = base64.b64decode(str(profile_image))

    newUser = modules.user(nickname=nickname, name=name, surname=surname, email=email, password=pwdCipher, gender=gender, birth_date=birth_date)
    
    try:
        modules.sqlAlchemy.session.add(newUser)
        modules.sqlAlchemy.session.commit()
        return jsonify(exito = "true")

    except Exception as e:
        print("Error registrando el nuevo usuario :", repr(e))
        return jsonify(exito = "false")

#Lista de Usuarios
@userClass.route('/listUsers/', methods=['GET', 'POST'])
def listUsers():
    try:
        #usuarios = user.query.order_by(user.nickname.desc()).all()
        usuarios = modules.user.query.order_by(modules.user.nickname).all()
        lista = []
        for usuario in usuarios:
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
                "profile_image":profile_image
                }
            lista.append(u)
    except Exception as e:
        print("Error leyendo usuarios:", repr(e))
        return jsonify(exito = "false")

    return jsonify(exito = "true", users = lista)

#Eliminar Usuario
@userClass.route('/deleteUser/', methods=['DELETE']) #Recibe un JSON User para borrar, supongamos que recoge solamente el nickname
def deleteUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]    
    deleteQuery = modules.user.query.filter_by(nickname=nickname).delete()
    modules.sqlAlchemy.session.commit()

    if(deleteQuery == 0):
        print("Error al borrar el usuario:")
        return jsonify(exito = "false")

    return jsonify(exito = "true")

#Funcion de existe
@userClass.route('/userExist/', methods=['GET', 'POST']) #Función auxiliar para comprobar si el usuario existe
def existUser(): 
    json_data = request.get_json()
    nickname = json_data["nickname"] 
    isUserQuery = modules.user.query.filter_by(nickname=nickname).first()
    if(isUserQuery is None):
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

#Modificar Usuario
@userClass.route('/modifyUser/', methods=['PUT'])
def modifyUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    name = json_data["name"]
    surname = json_data["surname"]
    email = json_data["email"]
    password = json_data["password"]
    gender = json_data["gender"]
    birth_date = json_data["birth_date"]
    profile_image = json_data["profile_image"]
    pwdCipher = passwordCipher(password)
    try:
        modifiedUser = modules.user.query.filter_by(nickname=nickname).first()
        modifiedUser.nickname = nickname
        modifiedUser.name = name
        modifiedUser.surname = surname
        modifiedUser.email = email
        modifiedUser.password = pwdCipher
        modifiedUser.gender = gender
        modifiedUser.birth_date = birth_date
        modifiedUser.profile_image = profile_image
        modules.sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando usuarios:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(
                   exito = "true",
                   nickname=modifiedUser.nickname,
                   name=modifiedUser.name,
                   surname=modifiedUser.surname,
                   email=modifiedUser.email,
                   password=password,
                   gender=modifiedUser.gender,
                   birth_date=modifiedUser.birth_date.strftime("%Y-%m-%d"),
                   city=modifiedUser.city,
                   rol=modifiedUser.rol,
                   profile_image = modifiedUser.profile_image)

#Perfil Usuario
@userClass.route('/profileUser/', methods=['GET', 'POST']) #No se usa
def profileUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    userQuery = modules.user.query.filter_by(nickname=nickname).first()
    if userQuery is None:
        return jsonify(exito = "false")
    
    return jsonify(exito = "true",
                   nickname = userQuery.nickname,
                   name=userQuery.name,
                   surname=userQuery.surname,
                   email=userQuery.email,
                   password=userQuery.password,
                   gender=userQuery.gender,
                   birth_date=userQuery.birth_date.strftime("%Y-%m-%d"),
                   city=userQuery.city,
                   rol=userQuery.rol,
                   profile_image = modifiedUser.profile_image)