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


class location(sqlAlchemy.Model):
    __tablename__ = 'location'
    name = sqlAlchemy.Column(sqlAlchemy.String(255), primary_key = True)
    description = sqlAlchemy.Column(sqlAlchemy.String(255)) #A lo mejor es necesario cambiar la longitud
    direction = sqlAlchemy.Column(sqlAlchemy.String(255))
    coordinate_latitude = sqlAlchemy.Column(sqlAlchemy.Float())
    coordinate_longitude = sqlAlchemy.Column(sqlAlchemy.Float())
    picture = sqlAlchemy.Column(sqlAlchemy.String(255)) #Aun no esta decidido de como mostrar una imagen, si por URL o por BD
    type_of_place = sqlAlchemy.Column(sqlAlchemy.String(255))
    city = sqlAlchemy.Column(sqlAlchemy.String(255), default="Madrid")
    affluence = sqlAlchemy.Column(sqlAlchemy.String(255))

class comments(sqlAlchemy.Model):
    __tablename__ = 'comments'
    id_comment = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    user = sqlAlchemy.Column(sqlAlchemy.String(255))
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    comment = sqlAlchemy.Column(sqlAlchemy.String(255))  

class ratings(sqlAlchemy.Model):
    __tablename__ = 'ratings'
    id_rate = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    user = sqlAlchemy.Column(sqlAlchemy.String(255))
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    rate = sqlAlchemy.Column(sqlAlchemy.Integer())  
#Funciones Usuario

#Cifrado de Passwords

def passwordCipher(password):#Servirá en el momento de crear una cuenta nueva o para modificar una contraseña
    #Generamos la salt aleatoria, con (rounds=16) lo ejecuta 16 veces para una mayor seguridad, pero realentiza el proceso
    hashed = bcrypt.hashpw(password.encode(), bcrypt.gensalt()) #Es necesario tenerlo como bytes para hacer el hash
    return hashed

def passwordVerify(password, pwdCipher): #Comprueba si la contraseña es correcta
    if (bcrypt.checkpw(password.encode(), pwdCipher.encode()) is False):
        return False
    return True


#app = Flask(__name__)
#api = Api(app)
#api.add_resource(user, '/login/')


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
                    password=userQuery.password,
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
        
    return jsonify(exito = "true") #A lo mejor es necesario devolver los datos del nuevo usuario

#Perfil Usuario
@app.route('/profileUser/', methods=['GET', 'POST'])
def profileUser():
    json_data = request.get_json()
    nickname = json_data["nickname"]
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


#Funciones Lugares
@app.route('/location/newLocation', methods=['POST'])
def newLocation():
    json_data = request.get_json()
    name = json_data["name"]
    description = json_data["description"]
    direction = json_data["direction"]
    coordinate_latitude = json_data["coordinate_latitude"]
    coordinate_longitude = json_data["coordinate_longitude"]
    picture = json_data["picture"]
    type_of_place = json_data["type_of_place"]
    affluence = json_data["affluence"]
    createLocation = location(name = name, description = description, direction = direction, coordinate_latitude = coordinate_latitude, 
        coordinate_longitude = coordinate_longitude, picture = picture, type_of_place = type_of_place, affluence = affluence)
    
    try:
        sqlAlchemy.session.add(createLocation)
        sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   name=createLocation.name,
                   description=createLocation.description,
                   direction=createLocation.direction,
                   coordinate_latitude=createLocation.coordinate_latitude,
                   coordinate_longitude=createLocation.coordinate_longitude,
                   picture=createLocation.picture,
                   type_of_place=createLocation.type_of_place,
                   city=createLocation.city,
                   affluence=createLocation.affluence)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

@app.route('/location/modifyLocation', methods=['POST'])
def modifyLocation():

    json_data = request.get_json()
    name = json_data["name"]
    description = json_data["description"]
    direction = json_data["direction"]
    coordinate_latitude = json_data["coordinate_latitude"]
    coordinate_longitude = json_data["coordinate_longitude"]
    picture = json_data["picture"]
    type_of_place = json_data["type_of_place"]
    affluence = json_data["affluence"]
    try:
        modifiedLocation = location.query.filter_by(name=name).first()
        modifiedLocation.name = name
        modifiedLocation.description = description
        modifiedLocation.direction = direction
        modifiedLocation.coordinate_latitude = coordinate_latitude
        modifiedLocation.coordinate_longitude = coordinate_longitude
        modifiedLocation.picture = picture
        modifiedLocation.type_of_place = type_of_place
        modifiedLocation.affluence = affluence
        sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando lugar:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

@app.route('/location/readLocation', methods=['GET', 'POST'])
def readLocation():
    json_data = request.get_json()
    name = json_data["name"]    

    lcQuery = location.query.filter_by(name = name).first()

    if (lcQuery is not None):
        print("success")
        return jsonify(
                exito = "true",
                name = lcQuery.name,
                description=lcQuery.description,
                direction=lcQuery.direction,
                coordinate_latitude=lcQuery.coordinate_latitude,
                coordinate_longitude=lcQuery.coordinate_longitude,
                picture=lcQuery.picture,
                type_of_place=lcQuery.type_of_place,
                city=lcQuery.city,
                affluence=lcQuery.affluence)

    print("failure")
    return jsonify(exito = "false")    

@app.route('/location/deleteLocation', methods=['DELETE'])
def deleteLocation():
    json_data = request.get_json()
    name = json_data["name"]    
    try:
        deleteQuery = location.query.filter_by(name=name).delete()
        sqlAlchemy.session.commit()
        if(deleteQuery == 0):
            print("Error al borrar el lugar:")
            return jsonify(exito = "false")
        return jsonify(exito = "true") 
    except Exception as e:
        print("Error borrando la fila :", repr(e))
        return jsonify(exito = "false")   

@app.route('/location/newComment', methods=['POST'])
def newComment():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    comment = json_data["comment"]
    createComment = comments(user = user, location = location, comment = comment)
    
    try:
        sqlAlchemy.session.add(createComment)
        sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   id_comment = createComment.id_comment,
                   user=createComment.user,
                   location=createComment.location,
                   comment=createComment.comment)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")    

@app.route('/location/listComments', methods=['POST'])
def listComments():
    json_data = request.get_json()
    location = json_data["location"]
    try:
        cmQuery = comments.query.filter_by(location = location).all()
        lista = []
        for comment in cmQuery:
            lista.append(usuario.nickname)
    except Exception as e:
        print("Error leyendo usuarios:", repr(e))
        return jsonify(exito = "false")

    return jsonify(exito = "true", users = lista)   

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True)