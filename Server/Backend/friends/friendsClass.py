from flask import Blueprint
from flask import request
from flask import jsonify
import modules
import friends.friendsFunct as FriendsFunct
from sqlalchemy import or_
from sqlalchemy import and_

friendsClass = Blueprint("friendsClass", __name__)


@friendsClass.route('/friends/sendRequest', methods=['POST']) 
def sendRequest():
    userSrc, userDst = FriendsFunct.initParameters()
    state = "P" # P --> Pending | A --> Accepted

    try:
        friendsQuery = modules.friends.query.filter(((modules.friends.userSrc == userSrc) & (modules.friends.userDst == userDst)) | ((modules.friends.userSrc == userDst) & (modules.friends.userDst == userSrc))).all()
        if friendsQuery:
            print("No se puede enviar petición de amistad porque ya existe o el usuario ya es tu amigo/a")
            return jsonify(exito = "false")
        if(FriendsFunct.countFriends(userSrc) >= 50): #No puedes enviar más peticiones de amistad si tienes 50 amigos
            print("Has superado el numero máximo de amigos")
            return jsonify(exito = "false")
        if(userSrc == userDst):
            print("No te puedes enviar petición de amistad a ti mismo")
            return jsonify(exito = "false") 

        newRelFriends = modules.friends(userSrc = userSrc, userDst = userDst, state = state)
        modules.sqlAlchemy.session.add(newRelFriends)
        modules.sqlAlchemy.session.commit()
        return jsonify(exito = "true")
    except Exception as e:
        print("Ha habido algún error: ", repr(e))
        return jsonify(exito = "false")

@friendsClass.route('/friends/acceptRequest', methods=['POST']) #Crea una recomendación
def acceptRequest():
    userSrc, userDst = FriendsFunct.initParameters() #userSrc es el usuario que te ha enviado la petición de amistad | userDst eres tú
    state = "A"
    if(FriendsFunct.countFriends(userSrc) >= 50):
        print("Has superado el numero máximo de amigos")
        return jsonify(exito = "false")
    friendRequest =  modules.friends.query.filter_by(userSrc = userSrc, userDst = userDst, state = "P").first()
    if friendRequest is None:
        print("No existe la petición de amistad")
        return jsonify(exito = "false")
    friendRequest.state = state
    modules.sqlAlchemy.session.commit()
    return jsonify(exito = "true")

@friendsClass.route('/friends/refuseRequest', methods=['DELETE']) #Crea una recomendación
def refuseRequest():
    userSrc, userDst = FriendsFunct.initParameters() #userSrc es el usuario que te ha enviado la petición de amistad | userDst eres tú

    deleteRequest =  modules.friends.query.filter_by(userSrc = userSrc, userDst = userDst, state = "P").delete()
    if deleteRequest == 0:
        print("No existe la petición de amistad")
        return jsonify(exito = "false")
    modules.sqlAlchemy.session.commit()
    return jsonify(exito = "true")

@friendsClass.route('/friends/deleteFriend', methods=['DELETE']) #Crea una recomendación
def deleteFriend():
    user, friend = FriendsFunct.initParametersFriends() #userSrc es el usuario que te ha enviado la petición de amistad | userDst eres tú

    deleteFriend =  modules.friends.query.filter(((modules.friends.userSrc == friend) & (modules.friends.userDst == user)) | ((modules.friends.userSrc == user) & (modules.friends.userDst == friend))).delete()
    if deleteFriend == 0:
        print("No existe la petición de amistad")
        return jsonify(exito = "false")
    modules.sqlAlchemy.session.commit()
    return jsonify(exito = "true")

@friendsClass.route('/friends/listFriends', methods=['POST']) #Crea una recomendación
def listFriends():
    json_data = request.get_json()
    user = json_data["user"]
    state = "A"
    friendList = []
    friendsQuery = modules.friends.query.filter(((modules.friends.userSrc == user) | (modules.friends.userDst == user)) & (modules.friends.state == state)).all()
    for friend in friendsQuery:
        userFriend = friend.userDst if (friend.userSrc == user) else friend.userSrc
        obj = FriendsFunct.completeFriendList(userFriend, state, friend.created)
        friendList.append(obj)
    return jsonify(exito = "true", list = friendList)

@friendsClass.route('/friends/listFriendRequests', methods=['POST']) #Crea una recomendación
def listFriendRequests():
    json_data = request.get_json()
    user = json_data["user"]
    state = "P"
    requestList = []
    requestsFriend = modules.friends.query.filter((modules.friends.userDst == user) & (modules.friends.state == state)).all()
    for req in requestsFriend:
        userFriend = req.userDst if (req.userSrc == user) else req.userSrc
        obj = FriendsFunct.completeFriendList(userFriend, state, req.created)
        requestList.append(obj)
    return jsonify(exito = "true", list = requestList)
    