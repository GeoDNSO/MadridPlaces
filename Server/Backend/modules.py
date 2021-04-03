from flask_sqlalchemy import SQLAlchemy
from app import app


sqlAlchemy = SQLAlchemy(app)


#############################################   Clases #############################################

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
    profile_image = sqlAlchemy.Column(sqlAlchemy.String(255), nullable=True)
    
class location(sqlAlchemy.Model):
    __tablename__ = 'location'
    name = sqlAlchemy.Column(sqlAlchemy.String(255), primary_key = True)
    description = sqlAlchemy.Column(sqlAlchemy.String(1500)) #A lo mejor es necesario cambiar la longitud
    coordinate_latitude = sqlAlchemy.Column(sqlAlchemy.Float())
    coordinate_longitude = sqlAlchemy.Column(sqlAlchemy.Float()) 
    type_of_place = sqlAlchemy.Column(sqlAlchemy.Integer())
    city = sqlAlchemy.Column(sqlAlchemy.String(255), default="Madrid")
    road_class = sqlAlchemy.Column(sqlAlchemy.String(255))
    road_name = sqlAlchemy.Column(sqlAlchemy.String(255))
    road_number = sqlAlchemy.Column(sqlAlchemy.String(255))
    zipcode = sqlAlchemy.Column(sqlAlchemy.Integer())
    affluence = sqlAlchemy.Column(sqlAlchemy.String(255))

class tPlace(sqlAlchemy.Model):
    __tablename__ = 'type_of_place'
    id_type = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    category = sqlAlchemy.Column(sqlAlchemy.String(255))

class location_images(sqlAlchemy.Model):
    __tablename__ = 'location_images'
    id_image = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    image = sqlAlchemy.Column(sqlAlchemy.String(1000))
    location_name = sqlAlchemy.Column(sqlAlchemy.String(255))


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

class tracking(sqlAlchemy.Model):
    __tablename__ = 'tracking'
    id_track = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    user = sqlAlchemy.Column(sqlAlchemy.String(255))
    latitude = sqlAlchemy.Column(sqlAlchemy.Float())
    longitude = sqlAlchemy.Column(sqlAlchemy.Float())
    passed = sqlAlchemy.Column(sqlAlchemy.DateTime, default = sqlAlchemy.func.now())

class twitter_ratings(sqlAlchemy.Model):
    __tablename__ = 'twitter_ratings'
    id_twitterRate = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    twitterRate = sqlAlchemy.Column(sqlAlchemy.Integer())  
    n_tweets = sqlAlchemy.Column(sqlAlchemy.Integer())  
    n_positiveTweets = sqlAlchemy.Column(sqlAlchemy.Integer())  
    n_negativeTweets = sqlAlchemy.Column(sqlAlchemy.Integer())
    n_neutralTweets = sqlAlchemy.Column(sqlAlchemy.Integer())  

class favorites(sqlAlchemy.Model):
    __tablename__ = 'location_favorites'
    id_favorite = sqlAlchemy.Column(sqlAlchemy.Integer(), primary_key = True)
    location = sqlAlchemy.Column(sqlAlchemy.String(255))
    user = sqlAlchemy.Column(sqlAlchemy.String(255))