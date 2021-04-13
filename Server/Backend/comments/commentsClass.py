from flask import Blueprint
from flask import request
from flask import jsonify
#Contiene las clases de la BD
import modules

#Funciones auxiliares que no usan rutas
import comments.commentFunct as CommentFunct
import user.userFunct as UserFunct
#############################################   Funciones Comentarios   #############################################

commentsClass = Blueprint("commentsClass", __name__)  

@commentsClass.route('/location/newComment&Rate', methods=['POST'])
def newCommentYRate():
    json_data = request.get_json()
    user = json_data["user"]
    location = json_data["location"]
    comment = json_data["comment"]
    rate = json_data["rate"]
    exist = "false" #False el comentario no existia
    try:
        ctQuery = modules.comments.query.filter_by(user = user, location = location).first()
        if(ctQuery is None):
            if(comment != ""):
                createComment = modules.comments(user = user, location = location, comment = comment, rate = rate)
            else:
                createComment = modules.comments(user = user, location = location, rate = rate)

            modules.sqlAlchemy.session.add(createComment)
        else:
            exist = "true"
            if(comment != ""):
                ctQuery.comment = comment
            ctQuery.rate = rate
            #created onUpdate, si se modifica se actualiza automaticamente
        modules.sqlAlchemy.session.commit()

        return jsonify(
                   exito = "true",
                   id_comment = createComment.id_comment if ctQuery is None else ctQuery.id_comment ,
                   user=user,
                   profile_image=UserFunct.showPicture(user),
                   location=location,
                   comment=comment if comment != "" else None,
                   created=createComment.created.strftime('%Y-%m-%d %H:%M:%S') if ctQuery is None else ctQuery.created.strftime('%Y-%m-%d %H:%M:%S'),
                   rate=rate,
                   exist=exist)
    except Exception as e:
        print("Error insertando la nueva fila :", repr(e))
        return jsonify(exito = "false")   

@commentsClass.route('/location/modifyComment', methods=['POST']) #No se usa, para modificar llama otra vez a newComment&Rate
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
            #picture = showPicture(comment.user)
            cmDict = {
            "id_comment" : comment.id_comment,
            "user" : comment.user,
            "profile_image" : UserFunct.showPicture(comment.user),
            "comment" : comment.comment,
            "rate" : comment.rate,
            "created" : comment.created
            }
            lista.append(cmDict)
        return jsonify(exito = "true", listComments = lista)
    except Exception as e:
        print("Error leyendo comentarios:", repr(e))
        #return jsonify(exito = "false")
        return jsonify(exito = "false")

