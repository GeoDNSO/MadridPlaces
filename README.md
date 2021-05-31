# MadridPlaces
TFG cuyo objetivo es la creación de una aplicación Android para facilitar el turismo en Madrid.


## Estructura del Proyecto
* **App**. Contiene el proyecto Android de la aplicación
* **Iconos Categorías**. Contiene los recursos .svg usados en la aplicación y obtenidos de la web https://www.flaticon.com/
* **Server**. Contiene el código Python usado para la implementación del Servidor
* **cargaCSV**. Contiene los scripts necesarios para iniciar un proceso ETL a los datos de la aplicación

## Construir el servidor
Para ejecutar el servidor es necesario ejecutar los siguientes comandos (es necesario tener instalada una versión actualizada de Python) desde la carpeta Server/Backend:
* Instalar las dependencias
```
pip install -r requirements.txt
```
* Ejecutar el servidor
```
python backend.py
```

## Ejecutar la aplicación Android
Para ejecutar la aplicación Android bastaría con sincronizar la aplicación con el gradle y ejecutarla en un emulador o dispositivo móvil.

## Licencia
Shield: [![CC BY-NC-SA 4.0][cc-by-nc-sa-shield]][cc-by-nc-sa]

This work is licensed under a
[Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License][cc-by-nc-sa].

[![CC BY-NC-SA 4.0][cc-by-nc-sa-image]][cc-by-nc-sa]

[cc-by-nc-sa]: http://creativecommons.org/licenses/by-nc-sa/4.0/
[cc-by-nc-sa-image]: https://licensebuttons.net/l/by-nc-sa/4.0/88x31.png
[cc-by-nc-sa-shield]: https://img.shields.io/badge/License-CC%20BY--NC--SA%204.0-lightgrey.svg
