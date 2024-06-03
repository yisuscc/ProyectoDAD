#include <Arduino.h>
#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
////DEfines y variables globales////// 
// Configuaracion del puerto del relay
int relay = 21;
// estas no se my bien para que sirven; 
const int DEVICE_ID = 124; // esto no sé muy bien para qué sirve.
int test_delay = 1000; //parece un delay  para las peticiones a la api
boolean describe_tests = true; //no sé para que sirve
//Credenciales de la red wifi
#define STASSID "RED_GEnerica"    //"Your_Wifi_SSID"
#define STAPSK "90899899" //"Your_Wifi_PASSWORD"
const char *MQTT_CLIENT_NAME = "PlaquitaVonPlaquez"; //TODO Cambiar
// LAs variables a enviar del actuador 
const int placaID = 1234;// CAmbiar siempre que sea necesario
//#define groupID 1
const int groupID = 1;
//const char groupIdChar = '1';
const int actuadorID = 1234;
// el timestamp lo generamos luego
boolean status;
//configuración del mqttt
WiFiClient espClient;
PubSubClient client(espClient);
const char *MQTT_BROKER_ADRESS = "10.166.227.171"; //en micaso coincide con la del server rest
const uint16_t MQTT_PORT = 1883;
/////FUNCIONES////////
 String creaJSON(long timestamp, boolean estado){
  //Serializa el JSON
    DynamicJsonDocument doc(2048);
    doc["idActuador"]= actuadorID;
    doc["placaId"] = placaID;
    doc["timestamp"] = timestamp;
    doc["status"] = estado;
    doc["idGroup"] = groupID;
    String str;
    serializeJson(doc, str);
    return str;
  }
  //MQTT 
 
  void ConnectMqtt()
{
  Serial.print("Starting MQTT connection...");
  if (client.connect(MQTT_CLIENT_NAME))
  {
    client.subscribe("1");
    client.publish("1", "connected");// Cambiar al group id
    client.publish("Placas", "connected");
  }
  else
  {
    Serial.print("Failed MQTT connection, rc=");
    Serial.print(client.state());
    Serial.println(" try again in 5 seconds");

    delay(5000);
  }
}
 void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{
  Serial.println("Received on ");
  Serial.print(topic);
  Serial.print(": ");

  String content = "";
  for (size_t i = 0; i < length; i++)
  {
    content.concat((char)payload[i]);
  }
 if(content=="1"){
  digitalWrite(relay,HIGH);

 }else if(content =="0"){
  digitalWrite(relay,LOW);
 }
 else{
  Serial.println("XD");
 }
 status = digitalRead(relay)==HIGH?true:false;
 //enviamos el estado del actuador
 Serial.println(creaJSON(1000,status));
//sendPost(creaJSON());
}
 void IniMQTT(){
  client.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
  client.setCallback(OnMqttReceived);
  }
  void HandleMqtt()
{
  if (!client.connected())
  {
    ConnectMqtt();
  }
  client.loop();
}

  void setup() {
  // primero establecemos el valor del pin 


  pinMode(relay,OUTPUT);
  digitalWrite(relay,HIGH); 
  //2 conexion puerto serie
  Serial.begin(9600);
  //3 conexion wifi
   WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);
  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.println(".");
  }
  Serial.println("Conectado al wifi");
  //4 conexion mqtt 
  IniMQTT();
  // enviamos el 1 post

}


void loop() {
  // put your main code here, to run repeatedly:
  // conmutamos el relay porque nos da la gana
 HandleMqtt();

  

}
