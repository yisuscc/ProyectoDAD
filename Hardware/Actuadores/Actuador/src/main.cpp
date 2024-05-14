#include <Arduino.h>
#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
// defines y variables globales 

const int DEVICE_ID = 124; / esto no sé muy bien para qué sirve.
int test_delay = 1000; //parece un delay  para las peticiones a la api
boolean describe_tests = true; //no sé para que sirve
// ubicacion del servidor de la api rest?
String serverName = "http://192.168.1.178/";//TODO Cambiar
HTTPClient http;
//Credenciales de la red wifi 
#define STASSID "Your_Wifi_SSID"    //"Your_Wifi_SSID"
#define STAPSK "Your_Wifi_PASSWORD" //"Your_Wifi_PASSWORD"

//configuración del mqttt
WiFiClient espClient;
PubSubClient client(espClient);
const char *MQTT_BROKER_ADRESS = "192.168.1.154"; //en micaso coincide con la del server rest
const uint16_t MQTT_PORT = 1883;

const char *MQTT_CLIENT_NAME = "ArduinoClient_1"; //TODO Cambiar



void setup() {
  // put your setup code here, to run once:
  //1 configuramos el pin del rele
  //2 conexion puerto serie
  Serial.begin(9600);
  //3 conexion wifi
  IniWifi();
  //4 conexion mqtt 

}

void loop() {
  // put your main code here, to run repeatedly:
}

// put function definitions here:
void OnMqttReceived(char *topic, byte *payload, unsigned int length){
  //TODO  completar 
  //determina lo que se hace cuando se recibe un mensaje mqtt
}
void InitMqtt(){
  
  // para inicializar el mqqt
   client.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
  client.setCallback(OnMqttReceived);
}
void IniWifi(){
  //TODO
}
void iniHardware(){
  //TODO
  //aqui colocamos los pines del relé 

}
void ConnectMqtt(){
  //TODO
   Serial.print("Starting MQTT connection...");
  if (client.connect(MQTT_CLIENT_NAME))
  {
    client.subscribe("hello/world");
    client.publish("hello/world", "connected");
  }
  else
  {
    Serial.print("Failed MQTT connection, rc=");
    Serial.print(client.state());
    Serial.println(" try again in 5 seconds");

    delay(5000);
  }

}
