#HEMOS MODIFICADO LAS PASS DONDE ANTES SE MOSTRABAN ""

from flask_sqlalchemy import SQLAlchemy
from flask import Flask
from flask import request
from flask import jsonify

import bcrypt #Para hashear las contraseñas, necesidad de instalar con pip install bcrypt

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:@localhost/TFG'

sqlAlchemy = SQLAlchemy(app)

#Clases
class user(sqlAlchemy.Model):
    __tablename__ = 'user'
    nickname = sqlAlchemy.Column(sqlAlchemy.String(255), primary_key = True)
    name = sqlAlchemy.Column(sqlAlchemy.String(255))
    surname = sqlAlchemy.Column(sqlAlchemy.String(255))
    email = sqlAlchemy.Column(sqlAlchemy.String(255))
    password = sqlAlchemy.Column(sqlAlchemy.String(255))
    gender = sqlAlchemy.Column(sqlAlchemy.String(50))
    birth_date = sqlAlchemy.Column(sqlAlchemy.DateTime)
    city = sqlAlchemy.Column(sqlAlchemy.String(255), default="Madrid")
    rol = sqlAlchemy.Column(sqlAlchemy.String(255), default="user")


#Funciones

#Cifrado de Passwords

def passwordCipher(password):#Servirá en el momento de crear una cuenta nueva o para modificar una contraseña
    #Generamos la salt aleatoria, con (rounds=16) lo ejecuta 16 veces para una mayor seguridad, pero realentiza el proceso
    hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt()) #Es necesario tenerlo como bytes para hacer el hash
    return hashed

def passwordVerify(password, pwdCipher): #Comprueba si la contraseña es correcta
    if (bcrypt.checkpw(password.encode(), pwdCipher.encode()) is False):
        return False
    return True


""" @app.route('/', methods=['GET', 'POST'])
def parseArguments():
    json_received = request.get_json()
    print(json_received)

    operation = json_received["operation"]
    if operation == "login":
        return login(json_received)
    elif operation == "registration":
        return registration(json_received)
 """
#Login
@app.route('/login/', methods=['GET', 'POST'])
def login():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    password = json_data["password"]

    #userQuery = user.query.filter_by(nickname = nickname, password = password).first()
    userQuery = user.query.filter_by(nickname = nickname).first()

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
                    rol=userQuery.rol)
    print("failure")
    return jsonify(exito = "false")    

#Registro
@app.route('/registration/', methods=['GET', 'POST'])
def registration():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    name = json_data["name"]
    surname = json_data["surname"]
    email = json_data["email"]
    password = json_data["password"]
    gender = json_data["gender"]
    birth_date = json_data["birth_date"]

    pwdCipher = passwordCipher(password)
    newUser = user(nickname=nickname, name=name, surname=surname, email=email, password=pwdCipher, gender=gender, birth_date=birth_date)
    
    try:
        sqlAlchemy.session.add(newUser)
        sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   nickname=newUser.nickname,
                   name=newUser.name,
                   surname=newUser.surname,
                   email=newUser.email,
                   password=newUser.password,
                   gender=newUser.gender,
                   birth_date=newUser.birth_date.strftime("%Y-%m-%d"),
                   city=newUser.city,
                   rol=newUser.rol)

    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

#Lista de Usuarios
@app.route('/listUsers/', methods=['GET', 'POST'])
def listUsers():
    try:
        usuarios = user.query.order_by(user.nickname).all()
        lista = []
        for usuario in usuarios:
            lista.append(usuario.nickname)
    except Exception as e:
        print("Error leyendo usuarios:", repr(e))
        return jsonify(exito = "false")

    return jsonify(exito = "true", users = lista)

#Eliminar Usuario
@app.route('/deleteUser/', methods=['DELETE']) #Recibe un JSON User para borrar, supongamos que recoge solamente el nickname
def deleteUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]    
    deleteQuery = user.query.filter_by(nickname=nickname).delete()
    sqlAlchemy.session.commit()

    if(deleteQuery == 0):
        print("Error al borrar el usuario:")
        return jsonify(exito = "false")

    return jsonify(exito = "true")

#Funcion de existe
@app.route('/userExist/', methods=['GET', 'POST']) #Función auxiliar para comprobar si el usuario existe
def existUser(): 
    json_data = request.get_json()
    nickname = json_data["nickname"] 
    isUserQuery = user.query.filter_by(nickname=nickname).first()
    if(isUserQuery is None):
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

#Modificar Usuario
@app.route('/modifyUser/', methods=['PUT'])
def modifyUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    name = json_data["name"]
    surname = json_data["surname"]
    email = json_data["email"]
    password = json_data["password"]
    gender = json_data["gender"]
    birth_date = json_data["birth_date"]
    pwdCipher = passwordCipher(password)
    try:
        modifiedUser = user.query.filter_by(nickname=nickname).first()
        modifiedUser.nickname = nickname
        modifiedUser.name = name
        modifiedUser.surname = surname
        modifiedUser.email = email
        modifiedUser.password = pwdCipher
        modifiedUser.gender = gender
        modifiedUser.birth_date = birth_date
        sqlAlchemy.session.commit()
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
                   rol=modifiedUser.rol)

#Perfil Usuario
@app.route('/profileUser/', methods=['GET', 'POST'])
def profileUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]
    #nickname = '""OR 1=1 --'
    
    userQuery = user.query.filter_by(nickname=nickname).first()

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
                   rol=userQuery.rol)

""" @app.route('/saludo/<name>', methods=['GET', 'POST'])
def saludar(name):
    print("hola!")
    return "Buenas "+ name + "!" """

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True)