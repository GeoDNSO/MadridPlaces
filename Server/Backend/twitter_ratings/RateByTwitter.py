import os
import io
from dotenv import load_dotenv

import tweepy
from tweepy import OAuthHandler
import MySQLdb #Conexión a la BD
import preprocessor as p #Preprocesa el texto del tweet
from textblob import TextBlob #Realiza el análisis del sentimiento
from google_trans_new import google_translator #Traduce el texto para el análisis en inglés

##############          Este script se ejecutará semanalmente todos los Lunes          ##############
############## para actualizar las opiniones de twitter acerca de los lugares de la BD ##############


def classifyPolarity(polarity):
    rate = 0
    #Vamos a clasificarlo del 1 al 5, siendo 1 muy negativo y 5 muy positivo
    if (polarity == 0):  # 3
        rate = 3
    elif (polarity > 0 and polarity <= 0.5): #4
        rate = 4
    elif (polarity > 0.5 and polarity <= 1): #5
        rate = 5
    elif (polarity > -0.5 and polarity <= 0): #2
        rate = 2
    elif (polarity > -1 and polarity <= -0.5): #1
        rate = 1
    return rate

def detectSentiment(polarity):
    sentiment = 0
    if (polarity > 0):
        sentiment = 1
    elif (polarity < 0):
        sentiment = 1
    return sentiment
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

#Inicializamos la API de Google traductor
translator = google_translator()

#Recogemos de la BD los nombres de todos los lugares
cursor.execute( "SELECT name FROM location") # --> ~5000 lugares
locations = cursor.fetchall()

for location in locations:
	try:
		tweets = tweepy.Cursor(api.search, q=location[0], tweet_mode='extended').items(10) #Generamos los últimos 5 tweets acerca del lugar (parametro modificable)
		listRates = []
		n_positiveTweets = 0
		n_negativeTweets = 0
		n_neutralTweets = 0

		avgRate = 0 #Si no hay tweets, avgRate = 0
		for tweet in tweets:
		    cleanedTweet = p.clean(tweet.full_text) #Preprocesa el texto, quita los #, urls y emojis.

		    translation = cleanedTweet

		    if(len(cleanedTweet) > 3): #Para hacer el análisis como mínimo es necesario 3 letras
		    	try:
		    		lang = translator.detect(cleanedTweet)
		    		if(lang  == 'es'):
		    			translation = translator.translate(cleanedTweet) #TextBlob no analiza frases en español, por lo que hay que traducirlo. Se perderá un poco de precisión
		    	except Exception as e:
		    		print("Ha habido un error:", repr(e))
		    		pass
		    	analyzed = TextBlob(translation)
		        #print(analyzed) # Comprobar la traducción
		        #print(analyzed.tags) # --> Se puede ver el análisis sintáctico de la frase
		        #print(analyzed.sentiment)
		    	rate = classifyPolarity(analyzed.sentiment.polarity)
		    	listRates.append(rate)
		    	sentiment = detectSentiment(analyzed.sentiment.polarity)
		    	if(sentiment == 0):
		    		n_neutralTweets = n_neutralTweets + 1
		    	elif(sentiment == 1):
		    		n_positiveTweets = n_positiveTweets + 1
		    	elif(sentiment == -1):
		    		n_negativeTweets = n_negativeTweets + 1

		if(len(listRates) != 0):    
			avgRate = sum(listRates) / len(listRates)
		n_tweets = (n_positiveTweets + n_negativeTweets + n_neutralTweets)
		print(location[0], avgRate)
		cursor.execute("INSERT IGNORE INTO twitter_ratings (location, twitterRate, n_tweets, n_positiveTweets, n_negativeTweets, n_neutralTweets) VALUES (%s, %s, %s, %s, %s, %s)", [location, avgRate, n_tweets, n_positiveTweets, n_negativeTweets, n_neutralTweets])
		mydb.commit()
	except tweepy.error.TweepError:
		raise

print("Finalizado")