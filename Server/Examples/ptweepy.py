import tweepy
import os 
from dotenv import load_dotenv

load_dotenv()

consumer_key = os.getenv("consumer_key")
consumer_secret = os.getenv("consumer_secret")
access_token =os.getenv("access_token")
access_token_secret = os.getenv("access_token_secret")

#Autentificación
auth = tweepy.OAuthHandler(consumer_key, consumer_secret)
auth.set_access_token(access_token, access_token_secret)

api = tweepy.API(auth)


def main():
    public_tweets = api.home_timeline()
    for tweet in public_tweets:
        print(tweet.text)

def prueba():
    print("Iniciando pruebas...")
    # Get the User object for twitter...
    user = api.get_user('twitter')
    print(user.screen_name)
    print(user.followers_count)
    for friend in user.friends():
        print(friend.screen_name)

def prueba2():
   friends = api.friends()
   for friend in friends:
       print(friend.screen_name)


main()

prueba()

prueba2()

