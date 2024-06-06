#include <Arduino.h>
#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <NTPClient.h>
////DEfines y variables globales////// 
// Configuaracion del puerto del relay
int relay = 21;
// estas no se my bien para que sirven; 
//const int DEVICE_ID = 124; // esto no sé muy bien para qué sirve.

//Credenciales de la red wifi
#define STASSID "xdd"    //"Your_Wifi_SSID"
#define STAPSK "xdd" //"Your_Wifi_PASSWORD"
const char *MQTT_CLIENT_NAME = "Actuador"; //TODO Cambiar

// LAs variables a enviar del actuador 
const int placaID = 1234;// CAmbiar siempre que sea necesario
//#define groupID 1
const int groupID = 1235; // CAMBIAR TAMBIEN EN la configuración de mqtt conect

const int actuadorID = 1234;// CAMBIAR
// el timestamp lo generamos luego
boolean status;

//configuración del mqttt
WiFiClient espClient;
PubSubClient client(espClient);
const char *MQTT_BROKER_ADRESS = "10.166.227.171"; //en micaso coincide con la del server rest
const uint16_t MQTT_PORT = 1883;

// COSAS PARA EL TIEMPO 
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

//COSAS Para HTTP
HTTPClient http;
String serverName = "http://10.166.227.171/"; //TODO Cambiar
int test_delay = 1000; 
boolean describe_tests = true; 


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

  describe("Enviamos el estado del actuador");
  String serverPath = serverName + "/api/actuador";
  http.begin(serverPath.c_str());
  test_response(http.POST(json));
}
  //MQTT 
 
  void ConnectMqtt()
{
  Serial.print("Starting MQTT connection...");
  if (client.connect(MQTT_CLIENT_NAME))
  {// Aqui es donse se pone el groupID 
   const char* groupIDChar = String(groupID).c_str();
    client.subscribe(groupIDChar);
    //client.publish(groupIDChar, "connected");// Cambiar al group id
    //SERIA utili poner un mesaje un poco mas especifico
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
  Serial.println("Dato Incorrecto");
 }
 status = digitalRead(relay)==HIGH?true:false;
 timeClient.update();
sendPost(creaJSON(timeClient.getEpochTime(),status));
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
   timeClient.update();
  sendPost(creaJSON(timeClient.getEpochTime(),status));

}


void loop() {
 HandleMqtt();
}
