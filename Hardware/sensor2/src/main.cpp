#include <Arduino.h>
#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <NTPClient.h>
#include <MQUnifiedsensor.h>

////Defines y variables globales
//Configuración del sensor

//Credenciales de la red wifi
#define STASSID "reee"    //"Your_Wifi_SSID"
#define STAPSK "seet" //"Your_Wifi_PASSWORD"

// Variables a enviar
const int sensorID  =1234;// TODO
const  int placaID = 1234;
const int  groupID = 1234;
double concentracion; 


// COSAS PARA EL TIEMPO 
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

//COSAS Para HTTP
HTTPClient http;
String serverName = "http://10.166.227.171/"; //TODO Cambiar
int test_delay = 1000; 
boolean describe_tests = true; 

// Cosas para el sensor 
#define placa "ESP-32"
#define Voltage_Resolution 3.3
#define pin 34
#define type "MQ-135"
#define ADC_Bit_Resolution 12
#define RatioMQ135CleanAir 3.6  
MQUnifiedsensor MQ135(placa, Voltage_Resolution, ADC_Bit_Resolution, pin, type);
//FUNCIONEs///
//MQ135
void sensorSetup(){
  //FUENTE hackster.io
   //Set math model to calculate the PPM concentration and the value of constants   
    MQ135.setRegressionMethod(1); //_PPM =  a*ratio^b   
    MQ135.setA(110.47); 
    MQ135.setB(-2.862); 
    // Configurate the ecuation values to get NH4 concentration    
    MQ135.init();    
    Serial.print("Calibrating please wait.");   
    float calcR0 = 0;   
    for(int i = 1; i<=10; i ++)   {     
        MQ135.update(); // Update data, the arduino will be read the voltage on the analog pin     
        calcR0 += MQ135.calibrate(RatioMQ135CleanAir);    
        Serial.print(".");   
    }   
    MQ135.setR0(calcR0/10);   
    Serial.println("  done!.");      
    if(isinf(calcR0)) { Serial.println("Warning: Conection issue founded, R0 is infite (Open circuit detected) please check your wiring and supply"); while(1);}   
    if(calcR0 == 0){Serial.println("Warning: Conection issue founded, R0 is zero (Analog pin with short circuit to ground) please check your wiring and supply"); while(1);}   
    /*****************************  MQ CAlibration **************************/                   
    MQ135.serialDebug(false); 
}
//JSON
 String creaJSON(long timestamp){
  //Serializa el JSON
    DynamicJsonDocument doc(2048);
    doc["idSensor"]= sensorID;
    doc["placaId"] = placaID;
    doc["timestamp"] = timestamp;
    doc["concentracion"] = concentracion;
    doc["idGroup"] = groupID;
    String str;
    serializeJson(doc, str);
    return str;
  }

//HTTP
void test_response(int httpResponseCode)
{
  delay(test_delay);
  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String payload = http.getString();
    Serial.println(payload);
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}
void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}
void sendPost(String json ){

  describe("Enviamos el estado del sensor");
  String serverPath = serverName + "/api/sensor";
  http.begin(serverPath.c_str());
  test_response(http.POST(json));
}

void setup() {
  // put your setup code here, to run once:
// configuramos el sensor
sensorSetup();
//Configuración serie 
Serial.begin(9600);
// configuramos el wifi
 WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.println(".");
  }
    Serial.println("Conectado al wifi");
  // esperamos a que se caliente el sensor
  delay(5000);



}

void loop() {
  MQ135.update();
  timeClient.update();
  concentracion = MQ135.readSensor(); 
Serial.println(creaJSON(timeClient.getEpochTime()));
sendPost(creaJSON(timeClient.getEpochTime()));

delay(5000);
}

