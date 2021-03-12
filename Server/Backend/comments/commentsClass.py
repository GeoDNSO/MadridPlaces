from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

#Funciones auxiliares que no usan rutas
import comments.commentFunct as CommentFunct
import rates.rateFunct as RateFunct
#############################################   Funciones Comentarios   #############################################

commentsClass = Blueprint("commentsClass", __name__)  

@commentsClass.route('/location/newComment&Rate', methods=['POST'])
def newCommentYRate():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    comment = json_data["comment"]
    rate = json_data["rate"]

    createComment = modules.comments(user = user, location = location, comment = comment)
    createRate = modules.ratings(user = user, location = location, rate = rate)
    try:
        ctQuery = modules.comments.query.filter_by(user = user, location = location).first()
        print(ctQuery)
        if(ctQuery is None):
            print('2')
            modules.sqlAlchemy.session.add(createComment)
        else:
            print('3')
            ctQuery.comment = comment

        modules.sqlAlchemy.session.commit()

        rtQuery = modules.ratings.query.filter_by(user = user, location = location).first() #Comprueba si ya existe una valoración
        if(rtQuery is None):
            modules.sqlAlchemy.session.add(createRate) #Si no, se crea
        else:
            print('4')
            rtQuery.rate = rate #Si existe, devuelve su valor

        modules.sqlAlchemy.session.commit()

        return jsonify(
                   exito = "true",
                   id_comment = createComment.id_comment if ctQuery is None else ctQuery.id_comment ,
                   user=createComment.user,
                   location=createComment.location,
                   comment=createComment.comment,
                   created=createComment.created.strftime('%Y-%m-%d %H:%M:%S') if ctQuery is None else ctQuery.created.strftime('%Y-%m-%d %H:%M:%S'),
                   rate=rate)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")   

@commentsClass.route('/location/modifyComment', methods=['POST'])
def modifyComment():
    json_data = request.get_json()
    comment = json_data["comment"]
    id_comment = json_data["id_comment"]
    try:
        modifiedComment = modules.comments.query.filter_by(id_comment = id_comment).first()
        modifiedComment.comment = comment
        modules.sqlAlchemy.session.commit()
    except Exception as e:
        print("Error modificando comentario:", repr(e))
        return jsonify(exito = "false")
        
    return jsonify(exito = "true")

@commentsClass.route('/location/deleteComment', methods=['DELETE'])
def deleteComment():
    json_data = request.get_json()
    id_comment = json_data["id_comment"]
    try:
        cmQuery = modules.comments.query.filter_by(id_comment = id_comment).delete()
        modules.sqlAlchemy.session.commit()
        if(cmQuery == 0):
            print("Error al borrar el comentario")
            return jsonify(exito = "false")
        return jsonify(exito = "true")

    except Exception as e:
        print("Error eliminando comentario: ", repr(e))
        return jsonify(exito = "false")    


@commentsClass.route('/location/showComments', methods=['POST']) #USAR DANI
def showComments():
    json_data = request.get_json()
    location = json_data["location"]  
    page = json_data["page"] #Mostrar de X en X     
    quant = json_data["quant"]  
    try:
        tam = modules.comments.query.filter_by(location=location).count()
        comp = (page   * quant) - tam # tam = 30 page = 7 quant = 5
        #También queremos mostrar los últimos elementos aunque no se muestren "quant" elementos
        if(comp >= quant):
            return jsonify(exito = "true", listComments = [])

        cmQuery = modules.comments.query.filter_by(location = location).order_by(modules.comments.created.desc()).paginate(per_page=quant, page=page)
        if cmQuery is None:
            return jsonify(exito = "false")
        lista = []
        for comment in cmQuery.items:
            rate = RateFunct.showRate(comment.user, location)
            #picture = showPicture(comment.user)
            cmDict = {"user" : comment.user,
            #"picture" : picture,s
            "comment" : comment.comment,
            "rate" : rate,
            "created" : comment.created
            }
            lista.append(cmDict)
        return jsonify(exito = "true", listComments = lista)
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return jsonify(exito = "false")

