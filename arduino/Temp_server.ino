#include <SPI.h>
#include <Ethernet.h>
#include <DHT.h>

// Setup debug printing macros.
//#define DHT_DEBUG                     // Uncomment to enable printing out nice debug messages.
#define DEBUG_PRINTER Serial            // Define where debug output will be printed.
#ifdef DHT_DEBUG
  #define DEBUG_PRINT(...) { DEBUG_PRINTER.print(__VA_ARGS__); }
  #define DEBUG_PRINTLN(...) { DEBUG_PRINTER.println(__VA_ARGS__); }
#else
  #define DEBUG_PRINT(...) {}
  #define DEBUG_PRINTLN(...) {}
#endif

#define DHTTYPE DHT22       // Required to use the DHT library
#define DHTPIN 2
#define SETUP_DELAY 30000   // Delay to allow sensor to initialize
DHT dht(DHTPIN, DHTTYPE);

EthernetServer server(80);           //server port
EthernetClient client;
String readString;

long lastReadingTime = 0;
long interval = 60000 * 60;             // LOGGING INTERVAL 60 minutes
boolean _debug_on_page = false;
boolean dataSent = false;

//global variables for sensor data
float humidity = 0.0f;
float temperature = 0.0f;
float heatIndex = 0.0f;

void setup() {
  byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };   //physical mac address
  byte ip[] = { 192, 168, 1, 201 };                      // ip in lan (that's what you need to use in your browser. ("192.168.1.178")
  byte gateway[] = { 192, 168, 1, 1 };                   // internet access via router
  byte subnet[] = { 255, 255, 255, 0 };                  //subnet mask
  byte dnServer[] = {8,8,8,8};                           // google's server
  
// Open serial communications and wait for port to open :
  Serial.begin(115200);
  while (!Serial) { }   // wait for serial port to connect. Needed for Leonardo only
 
  pinMode(LED_BUILTIN, OUTPUT);
  // start the Ethernet connection and the server:
  Ethernet.begin(mac, ip, dnServer, gateway, subnet);
  dht.begin();
  delay(SETUP_DELAY);   // Give the sensor some time to initialize

  server.begin();
  DEBUG_PRINT("Server is at ");     DEBUG_PRINTLN(Ethernet.localIP());
  getData();  // prepare data for web server
}


void loop() {
  if (millis() - lastReadingTime > interval) {              // check for a reading in intervals
    // time to get fresh data from sensor and post to server
    DEBUG_PRINTLN("Getting data from sensor");
    getData();
    lastReadingTime = millis();
    postToWebServer();
  }
  //check for Web client
  listenForEthernetClients();  
}


void postToWebServer() {
    String data = "temp1=" + String(temperature) + "&hum1=" + String(humidity);

    DEBUG_PRINT("Sending HTTP POST data: ");     DEBUG_PRINTLN(data);
    if (client.connect("gpolic.eu5.org", 80)) {  // if local server then use byte ip
      dataSent = true;
      client.println("POST /add.php HTTP/1.1"); 
      client.println("Host: gpolic.eu5.org"); // SERVER ADDRESS HERE TOO
      client.println("Content-Type: application/x-www-form-urlencoded"); 
      client.print("Content-Length: "); 
      client.println(data.length()); 
      client.println(); 
      client.print(data); 
    } 
    else dataSent = false;
    
    if (client.connected()) { 
    client.stop();  // DISCONNECT FROM THE SERVER
  }
}

void getData() {
  humidity = dht.readHumidity();
  temperature = dht.readTemperature();
  while(isnan(humidity) || isnan(temperature)) { // if error readings
    delay(2000);
    humidity = dht.readHumidity();                 // retry read
    temperature = dht.readTemperature();
  }
  heatIndex = dht.computeHeatIndex(temperature, humidity, false);  
  DEBUG_PRINT("Temperature: "); DEBUG_PRINTLN(temperature);
  DEBUG_PRINT("Humidity: "); DEBUG_PRINTLN(humidity);
}


void listenForEthernetClients() {
  // Create a client connection
  EthernetClient client = server.available();
  if (client) {
    while (client.connected()) {
      if (client.available()) {
        DEBUG_PRINTLN("Client connected");
        char c = client.read();

        //read char by char HTTP request
        if (readString.length() < 100) {
          //store characters to string
          readString += c;
          //Serial.print(c);
        }

        //if HTTP request has ended
        if (c == '\n') {
          DEBUG_PRINTLN(readString); //print to serial monitor for debuging

          client.println("HTTP/1.1 200 OK"); //send new page
          client.println("Content-Type: text/html");
          client.println();
          client.println("<HTML>");
          client.println("<HEAD>");
          client.println("<meta name='apple-mobile-web-app-capable' content='yes' />");
          client.println("<meta name='apple-mobile-web-app-status-bar-style' content='black-translucent' />");
          client.println("<link rel='stylesheet' type='text/css' href='http://randomnerdtutorials.com/ethernetcss.css' />");
          client.println("<TITLE>Home Temperature and Humidity Project</TITLE>");
          client.println("</HEAD>");
          client.println("<BODY>");
          client.println("<H1>Temperature and Humidity Monitoring Project</H1>");
          client.println("<hr />");
          client.println("<H1>Temperature"); client.println(temperature);   client.println("C</H1>");
          client.println("<H1>Humidity"); client.println(humidity);   client.println("%</H1>");
          client.println("<H1>Heat Index"); client.println(heatIndex);   client.println("</H1>");
          client.println("<br />");
          client.println("<hr />");
          client.println("<H2>Have fun :-)</H2>");
          if(_debug_on_page)  {
            if (dataSent) client.println("<H2>Data sent to server</H2>");
            client.println("<H2>Time for next sensor update: "); 
            client.println((lastReadingTime + interval - millis())/1000); client.println(" seconds </H2>");
          }
/*        client.println("<br />");
          client.println("<a href=\"/?button1on\"\">Turn On LED</a>");
          client.println("<a href=\"/?button1off\"\">Turn Off LED</a><br />");
          client.println("<br />");
          client.println("<br />");
          client.println("<a href=\"/?button2on\"\">Past Hour</a>");
          client.println("<a href=\"/?button2off\"\">Past Averages</a><br />");   */
          client.println("<br />");
          client.println("</BODY>");
          client.println("</HTML>");

          delay(1);
          //stopping client
          client.stop();
          DEBUG_PRINTLN("Client disconnected");

          //clearing string for next read
          readString = "";
        }
      }
    }
  }
}

