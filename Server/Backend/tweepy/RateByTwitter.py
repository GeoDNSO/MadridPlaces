import tweepy
import os
import io
import json
from dotenv import load_dotenv
import tweepy
from tweepy import OAuthHandler
from tweepy.streaming import StreamListener
from tweepy import Stream
import time
import csv
import re
import MySQLdb
import preprocessor as p
from textblob import TextBlob

##############          Este script se ejecutará semanalmente todos los Lunes          ##############
############## para actualizar las opiniones de twitter acerca de los lugares de la BD ##############


def classifyPolarity(polarity):
    rate = 0
    #Vamos a clasificarlo del 1 al 5, siendo 1 muy negativo y 5 muy positivo
    if (polarity == 0):  # 3
        rate = 3
    elif (polarity > 0 and polarity <= 0.6): #4
        rate = 4
    elif (polarity > 0.6 and polarity <= 1): #5
        rate = 5
    elif (polarity > -0.6 and polarity <= 0): #2
        rate = 2
    elif (polarity > -1 and polarity <= -0.6): #1
        rate = 1
    return rate


#Conexión a la BD
mydb = MySQLdb.connect(host='localhost',
    user='root',
    passwd='',
    db='tfg')
cursor = mydb.cursor()

#Lectura del .env

load_dotenv()
consumer_key = os.getenv("consumer_key")
consumer_secret = os.getenv("consumer_secret")
access_token =os.getenv("access_token")
access_token_secret = os.getenv("access_token_secret")

#Autentificación
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

#Usamos la variable api para utilizar distintas funcionalidades
api = tweepy.API(auth, wait_on_rate_limit=True)

#Recogemos de la BD los nombres de todos los lugares

cursor.execute( "SELECT name FROM location") # --> ~5000 lugares
locations = cursor.fetchall()
for location in locations:
	try:
		tweets = tweepy.Cursor(api.search, q=location[0]).items(5) #Generamos los últimos 5 tweets acerca del lugar (parametro modificable)
		listRates = []
		avgRate = 0
		for tweet in tweets:
		    cleanedTweet = p.clean(tweet.text) #Preprocesa el texto, quita los #, urls y emojis.
		    if(len(cleanedTweet) > 3): #Para hacer el análisis como mínimo es necesario 3 letras
		        t = TextBlob(cleanedTweet).detect_language()
		        analyzed = TextBlob(cleanedTweet)
		        if(t  == 'es'):
		            enAnalyzed = analyzed.translate(to='en') #TextBlob no analiza frases en español, por lo que hay que traducirlo. Se perderá un poco de precisión
		        #print(enAnalyzed) # Comprobar la traducción
		        #print(analyzed.tags) # --> Se puede ver el análisis sintáctico de la frase
		        #print(enAnalyzed.sentiment)
		        rate = classifyPolarity(enAnalyzed.sentiment.polarity)
		        listRates.append(rate)
		if(len(listRates) != 0):    
			avgRate = sum(listRates) / len(listRates)
		print(location[0], avgRate)
		cursor.execute("INSERT IGNORE INTO twitter_ratings (location, twitterRate) VALUES (%s, %s)", [location, avgRate])
		mydb.commit()
	except tweepy.error.TweepError:
        raise

print("Finalizado")