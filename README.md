# Android-Arduino-Temperature-Monitor

Temperature and humidity monitoring for Arduino and Android using Web service (MySQL/PHP)



# What does it do

Temperature Monitor will monitor the temperature and humidity at your space

Arduino along with temperature sensor takes readings and stores the data once every hour.

The info can be monitored on Android app and on the web.

There is also a database  that stores all the data


# Hardware

* Arduino Uno
* Ethernet module
* DHT22 sensor


 <p align="center">
  <img src="https://raw.githubusercontent.com/gpolic/Android-Arduino-Temperature-Monitor/master/media/DHT22.jpg" width="320"/>
</p>



Connect the sensor on pin 2 of the Arduino

 <p align="center">
  <img src="https://raw.githubusercontent.com/gpolic/Android-Arduino-Temperature-Monitor/master/media/ARDUINO.jpg" width="320"/>
</p>



	
# Software

Web host with MySQL and PHP.
Example Free host at https://www.freewebhostingarea.com/

Arduino code 
Logs the temperature and humidity on the server in 60 minute intervals

Android app provides statistics and graphs with average per month, per day

 <p align="center">
  <img src="https://raw.githubusercontent.com/gpolic/Android-Arduino-Temperature-Monitor/master/media/androidapp1.png" width="320"/>
</p>


# Usage


Start the Android app and let it sync with the database
It will show todays data per hour. The Analytics tab shows the temperature data complete history



 <p align="center">
  <img src="https://raw.githubusercontent.com/gpolic/Android-Arduino-Temperature-Monitor/master/media/androidapp2.png" width="320"/>
</p>



Open the URL of your server and check the temperature and humidity information


# More info

Additional info
