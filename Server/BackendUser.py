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
    created =  sqlAlchemy.Column(sqlAlchemy.DateTime, default = sqlAlchemy.func.now(), onupdate = sqlAlchemy.func.now())

class ratings(sqlAlchemy.Model):
    __tablename__ = 'ratings'
    id_rate = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    user = sqlAlchemy.Column(sqlAlchemy.String(255))
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    rate = sqlAlchemy.Column(sqlAlchemy.Integer())  

class visited(sqlAlchemy.Model):
    __tablename__ = 'visited'
    id_visited = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    user = sqlAlchemy.Column(sqlAlchemy.String(255))
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    date_visited =  sqlAlchemy.Column(sqlAlchemy.DateTime, default = sqlAlchemy.func.now())

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
            u = {
                "nickname":usuario.nickname,
                "name":usuario.name,
                "surname":usuario.surname,
                "email":usuario.email,
                "password":usuario.password,
                "gender":usuario.gender,
                "birth_date":usuario.birth_date.strftime("%Y-%m-%d"),
                "city":usuario.city,
                "rol":usuario.rol
                }
            lista.append(u)
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


#############################################   Funciones Lugares   #############################################

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



#############################################   Funciones Comentarios   #############################################

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
                   comment=createComment.comment,
                   created=createComment.created.strftime('%Y-%m-%d %H:%M:%S'))
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")    

@app.route('/location/modifyComment', methods=['POST'])
def modifyComment():
    json_data = request.get_json()
    comment = json_data["comment"]
    id_comment = json_data["id_comment"]
    try:
        modifiedComment = comments.query.filter_by(id_comment = id_comment).first()
        modifiedComment.comment = comment
        sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando comentario:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

@app.route('/location/deleteComment', methods=['DELETE'])
def deleteComment():
    json_data = request.get_json()
    id_comment = json_data["id_comment"]
    try:
        cmQuery = comments.query.filter_by(id_comment = id_comment).delete()
        sqlAlchemy.session.commit()
        if(cmQuery == 0):
            print("Error al borrar el comentario")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando comentario: ", repr(e))
        return jsonify(exito = "false")    


#@app.route('/location/listComments', methods=['POST'])
def listComments(location): #A lo mejor no se necesita un URL 
    #json_data = request.get_json()
    #location = json_data["location"]
    try:
        cmQuery = comments.query.filter_by(location = location).all()
        lista = []
        for comment in cmQuery:
            cmDict = {comment.user : comment.comment}
            lista.append(cmDict)
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return None

    #return jsonify(exito = "true", comments = lista)  
    return lista 


@app.route('/location/newRate', methods=['POST'])
def newRate():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    rate = json_data["rate"]
    createRate = ratings(user = user, location = location, rate = rate)
    
    try:
        rtQuery = ratings.query.filter_by(user = user, location = location).first()
        if(rtQuery is None):
            sqlAlchemy.session.add(createRate)
            sqlAlchemy.session.commit()
            return jsonify(
                    exito = "true",
                    id_rate = createRate.id_rate,
                    user=createRate.user,
                    location=createRate.location,
                    rate=createRate.rate)
        print("El usuario no puede valorar dos veces")
        return jsonify(exito = "false")
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")

@app.route('/location/deleteRate', methods=['DELETE'])
def deleteRate():
    json_data = request.get_json()
    id_rate = json_data["id_rate"]
    try:
        rtQuery = ratings.query.filter_by(id_rate = id_rate).delete()
        sqlAlchemy.session.commit()
        if(rtQuery == 0):
            print("Error al borrar la valoracion")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando comentario: ", repr(e))
        return jsonify(exito = "false")  


@app.route('/location/modifyRate', methods=['POST'])
def modifyRate():
    json_data = request.get_json()
    rate = json_data["rate"]
    id_rate = json_data["id_rate"]
    try:
        modifiedRate = ratings.query.filter_by(id_rate = id_rate).first()
        modifiedRate.rate = rate
        sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando la valoración:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

#@app.route('/location/averageRate', methods=['POST'])
def averageRate(location): #A lo mejor no se necesita un URL 
    #json_data = request.get_json()
    #location = json_data["location"]
    try:
        rtQuery = ratings.query.filter_by(location = location).all()
        cant = 0
        total = 0
        for rate in rtQuery:
            cant = cant + 1
            total = total + rate.rate
        result = total / cant
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return None

    #return jsonify(exito = "true", avgRate = result)   
    return round(result, 2)

@app.route('/location/newLocationVisited', methods=['POST'])
def newLocationVisited():    
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    locationVisited = visited(user = user, location = location)
    try:
        sqlAlchemy.session.add(locationVisited)
        sqlAlchemy.session.commit()
        return jsonify(
                   exito = "true",
                   id_visited = locationVisited.id_visited,
                   user=locationVisited.user,
                   location=locationVisited.location,
                   date_visited=locationVisited.date_visited.strftime('%Y-%m-%d'))
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")  

@app.route('/location/readHistory', methods=['POST']) #Devuelve un listado de lugares, las fechas y las veces que se visitaron
def readHistory():
    json_data = request.get_json()
    user = json_data["user"]
    try:
        vtQuery = visited.query.filter_by(user = user).order_by(visited.date_visited).all()
        visitedDict = {}
        for locationVisited in vtQuery:
            date = locationVisited.date_visited.strftime('%Y-%m-%d')
            if(visitedDict.get(date) is None):
                visitedDict[date] = {locationVisited.location : 1}
            elif(locationVisited.location not in visitedDict[date]):
                d1 = {locationVisited.location : 1}
                visitedDict[date].update(d1)
            else:
                visitedDict[date][locationVisited.location] += 1
        return jsonify(exito = "true", historial = visitedDict) 
    except Exception as e:
        print("Error leyendo el historial:", repr(e))
        return jsonify(exito = "false")
  


@app.route('/location/deleteHistory', methods=['DELETE']) #No se si es necesario una funcion para eliminar los lugares visitados
def deleteHistory():
    json_data = request.get_json()
    user = json_data["user"]
    try:
        vtQuery = visited.query.filter_by(user  = user).delete()
        if(vtQuery == 0):
            print("Error al borrar el historial")
            return jsonify(exito = "false")
        sqlAlchemy.session.commit()
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando el historial: ", repr(e))
        return jsonify(exito = "false")  

@app.route('/location/stats', methods=['POST'])
def stats():
    json_data = request.get_json()
    name = json_data["name"] 
    try:
        stQuery = location.query.filter_by(name=name).first()
        comments = listComments(name)
        avgRate = averageRate(name)
        return jsonify(
                exito = "true",
                name = stQuery.name,
                description=stQuery.description,
                direction=stQuery.direction,
                coordinate_latitude=stQuery.coordinate_latitude,
                coordinate_longitude=stQuery.coordinate_longitude,
                picture=stQuery.picture,
                type_of_place=stQuery.type_of_place,
                city=stQuery.city,
                affluence=stQuery.affluence,
                rate = avgRate,
                appComments = comments)

    except Exception as e:
        print("Error mostrando las estadisticas: ", repr(e))
        return jsonify(exito = "false")  

@app.route('/location/top100Rated', methods=['GET'])
def top100Rated():
    try:
        rtQuery = ratings.query.order_by(ratings.location).all()
        topDict = {}
        for rate in rtQuery:
            location = rate.location
            if(topDict.get(location) is None): #Crea un diccionario siendo la clave el lugar y el valor su valoración media
                topDict[location] = averageRate(location)
        sortedTop = dict(sorted(topDict.items(), key=lambda item: item[1],reverse=True)) #Ordena el diccionario en base a sus valores
        while(len(sortedTop) > 100): #Elimina los utlimos pares hasta que haya al menos 100 
            sortedTop.popitem()
        print(sortedTop)
        return jsonify(exito = "true", TOP100 = sortedTop)  
    except Exception as e:
        print("Error mostrando el TOP 100 de los lugares ", repr(e))
        return jsonify(exito = "false")  

app.run(host="0.0.0.0", port=5000, debug=True, threaded=True)