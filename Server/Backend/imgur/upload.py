import requests, json 
import pyimgur
import PIL.Image as Image
import io


def convertToBinaryData(filename):
    # Convert digital data to binary format
	data=requests.get(pic_url)   # read image
	photo=data.content
	return photo

pic_url='http://www.esmadrid.com/sites/default/files/recursosturisticos/alojamientos/mmd_008.jpg' 

empImg = convertToBinaryData(pic_url) #Dato en binario


CLIENT_ID = "9447315b37b3ece"
album = None # You can also enter an album ID here
image_path = 'Kitten.jpg'

im = pyimgur.Imgur(CLIENT_ID)
uploaded_image = im.upload_image(image_path, title="Uploaded with PyImgur")
print(uploaded_image.title)
print(uploaded_image.link)
print(uploaded_image.size)
print(uploaded_image.type)