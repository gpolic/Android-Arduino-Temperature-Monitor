# Android-Arduino-Temperature-Monitor

Temperature and humidity monitoring for Arduino and Android using Web service (MySQL/PHP)



# What does it do

Temperature Monitor will get readings of temperature and humidity ence every hour

Arduino implements a small web server that shows the latest (current) readings
The Android app is also shows the data with additional analytics

Between the arduino (sensor) and Android there is a Web and database server that stores all the data


# Hardware

* Arduino Uno
* Ethernet module
* DHT11 sensor

Connect the sensor on pins xx of the Arduino
	
# Software

Web host with MySQL and PHP.
Free host at https://www.freewebhostingarea.com/

Arduino code 
Logs the temperature and humidity on the server in 60 minute intervals

Android app is using the Stetho library to assist in troubleshooting the local SQLite database
Statistic graphs with average per month, and graphics per day


# Usage

LAN connection
Visit the page 192.168.1.201 on your home network
There is the latest reading for temperature

Internet
Fireup the Android app and let it sync with the database
It will show todays data per hour. The analytics tasb shows the temperature data complete history


# More info

Additional info