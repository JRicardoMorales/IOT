#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <NTPClient.h>
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include <MQ135.h>

// Reemplaza 0 por la ID de este dispositivo actual.
const int DEVICE_ID = 124;
const int ID_SENSOR = 74;
int cont;
boolean describe_tests = true;

// Reemplaza 0.0.0.0 por la IP local de tu servidor (ipconfig [Windows] o ifconfig [Linux o MacOS] obtiene la IP asignada a tu PC).
String serverName = "http://192.168.43.128/";
HTTPClient http;

// Reemplaza WifiName y WifiPassword por tus credenciales de WiFi.
#define STASSID "dosbesos"    //"Tu_Wifi_SSID"
#define STAPSK "Olvidona" //"Tu_Wifi_PASSWORD"

// NTP (Protocolo de tiempo de red) ajustes
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

// MQTT configuration
WiFiClient espClient;
PubSubClient client(espClient);

// Direccion IP del server, donde se despliega el broker MQTT
const char *MQTT_BROKER_ADRESS = "192.168.43.128";
const uint16_t MQTT_PORT = 1883;

// Nombre del cliente MQTT
const char *MQTT_CLIENT_NAME = "ArduinoClient_1";

// Ajustes de pines de entrada y salida
const int digitalSensorPin = 32;
const int actuatorPin = 33;
MQ135 mq135_sensor = MQ135(digitalSensorPin);



// callback a ejecutar cuando se recibe un mensaje
// en este ejemplo, muestra por serial el mensaje recibido
void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Recibido en ");
  Serial.print(topic);
  Serial.print(": ");

  String content = "";
  for (size_t i = 0; i < length; i++)
  {
    content.concat((char)payload[i]);
  }
     // Verificar si el contenido es igual a "1"
  if (content == "1")
  {
    // Encender el Relé
    digitalWrite(actuatorPin, HIGH);
  }
  else
  {
    // Apagar el Relé
    digitalWrite(actuatorPin, LOW);
  }
  Serial.print(content);
  Serial.println();
}

// inicia la comunicacion MQTT
// inicia establece el servidor y el callback al recibir un mensaje
void InitMqtt()
{
  client.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);
  client.setCallback(OnMqttReceived);
}

// Setup
void setup()
{
  Serial.begin(9600);
  Serial.println();
  Serial.print("Conectando a ");
  Serial.println(STASSID);

  /* Configura explícitamente el ESP32 para que sea un cliente WiFi, de lo contrario, por defecto, intentará
  actuar como cliente y como punto de acceso, lo que podría causar problemas de red con tus otros dispositivos WiFi en tu red WiFi. */
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

  InitMqtt();

  Serial.println("");
  Serial.println("Conexión Wifi conectada con exito");
  Serial.println("direccion IP: ");
  Serial.println(WiFi.localIP());
  Serial.println("Setup!");

  // Configura los modos de los pines para los actuadores (modo de salida) y los sensores (modo de entrada). Los números de pines deben ser descritos por el número GPIO (https://www.upesy.com/blogs/tutorials/esp32-pinout-reference-gpio-pins-ultimate-guide)
  // Para ESP32 WROOM 32D https://uelectronics.com/producto/esp32-38-pines-esp-wroom-32/
  // Debes encontrar el pinout para tu versión específica de la placa
  pinMode(actuatorPin, OUTPUT);
  pinMode(digitalSensorPin, INPUT);

  // Inicia y obtiene el tiempo
  timeClient.begin();
}

String response;

String serializeSensorValueBody(int idSensor, long timestamp, float value)
{
  // StaticJsonObject asigna memoria en la pila, puede ser reemplazado por DynamicJsonDocument que asigna en el montón.
  //
  DynamicJsonDocument doc(2048);

  // Añade valores en el documento
  //
  doc["idSensor"] = idSensor;
  doc["timestamp"] = timestamp;
  doc["value"] = value;
  doc["removed"] = false;

  // Genera el JSON minimizado y envíalo al puerto Serial.
  //
  String output;
  serializeJson(doc, output);
  Serial.println(output);

  return output;
}

String serializeActuatorStatusBody(float status, bool statusBinary, int idActuator, long timestamp)
{
  DynamicJsonDocument doc(2048);

  doc["status"] = status;
  doc["statusBinary"] = statusBinary;
  doc["idActuator"] = idActuator;
  doc["timestamp"] = timestamp;
  doc["removed"] = false;

  String output;
  serializeJson(doc, output);
  return output;
}

String serializeDeviceBody(String deviceSerialId, String name, String mqttChannel, int idGroup)
{
  DynamicJsonDocument doc(2048);

  doc["deviceSerialId"] = deviceSerialId;
  doc["name"] = name;
  doc["mqttChannel"] = mqttChannel;
  doc["idGroup"] = idGroup;

  String output;
  serializeJson(doc, output);
  return output;
}

void deserializeActuatorStatusBody(String responseJson)
{
  if (responseJson != "")
  {
    DynamicJsonDocument doc(2048);

    // Deserializa el documento JSON
    DeserializationError error = deserializeJson(doc, responseJson);

    // Test para ver si se realiza con exito
    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // Valores de ejecucion
    int idActuatorState = doc["idActuatorState"];
    float status = doc["status"];
    bool statusBinary = doc["statusBinary"];
    int idActuator = doc["idActuator"];
    long timestamp = doc["timestamp"];

    Serial.println(("Actuator status deserialized: [idActuatorState: " + String(idActuatorState) + ", status: " + String(status) + ", statusBinary: " + String(statusBinary) + ", idActuator" + String(idActuator) + ", timestamp: " + String(timestamp) + "]").c_str());
  }
}

void deserializeDeviceBody(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("codigo de respuesta HTTP: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    DynamicJsonDocument doc(2048);

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    int idDevice = doc["idDevice"];
    String deviceSerialId = doc["deviceSerialId"];
    String name = doc["name"];
    String mqttChannel = doc["mqttChannel"];
    int idGroup = doc["idGroup"];

    Serial.println(("Device deserialized: [idDevice: " + String(idDevice) + ", name: " + name + ", deviceSerialId: " + deviceSerialId + ", mqttChannel" + mqttChannel + ", idGroup: " + idGroup + "]").c_str());
  }
  else
  {
    Serial.print("Codigo de error: ");
    Serial.println(httpResponseCode);
  }
}

void deserializeSensorsFromDevice(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("Codigo de respuesta HTTP: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    // asignar memoria para el documento
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    // parseo a JSON array
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // extraccion de valores
    JsonArray array = doc.as<JsonArray>();
    for (JsonObject sensor : array)
    {
      int idSensor = sensor["idSensor"];
      String name = sensor["name"];
      String sensorType = sensor["sensorType"];
      int idDevice = sensor["idDevice"];

      Serial.println(("Sensor deserialized: [idSensor: " + String(idSensor) + ", name: " + name + ", sensorType: " + sensorType + ", idDevice: " + String(idDevice) + "]").c_str());
    }
  }
  else
  {
    Serial.print("Codigo de error: ");
    Serial.println(httpResponseCode);
  }
}

void deserializeActuatorsFromDevice(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {
    Serial.print("Codigo de respuesta HTTP: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    // asiganer memoria para el documento
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    // parseo a JSON array
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    // extraccion de valores
    JsonArray array = doc.as<JsonArray>();
    for (JsonObject sensor : array)
    {
      int idActuator = sensor["idActuator"];
      String name = sensor["name"];
      String actuatorType = sensor["actuatorType"];
      int idDevice = sensor["idDevice"];

      Serial.println(("Actuator deserialized: [idActuator: " + String(idActuator) + ", name: " + name + ", actuatorType: " + actuatorType + ", idDevice: " + String(idDevice) + "]").c_str());
    }
  }
  else
  {
    Serial.print("Codigo de error: ");
    Serial.println(httpResponseCode);
  }
}

void test_response(int httpResponseCode)
{
  if (httpResponseCode > 0)
  {
    Serial.print("Codigo de respuesta HTTP: ");
    Serial.println(httpResponseCode);
    String payload = http.getString();
    Serial.println(payload);
  }
  else
  {
    Serial.print("Codigo de error: ");
    Serial.println(httpResponseCode);
  }
}

void describe(char *description)
{
  if (describe_tests)
    Serial.println(description);
}

void GET_tests()
{
  describe("Test GET toda la información sobre la placa");
  String serverPath = serverName + "api/devices/" + String(DEVICE_ID);
  http.begin(serverPath.c_str());
  // test_response(http.GET());
  deserializeDeviceBody(http.GET());

  describe("Test GET sensores a partir de deviceID");
  serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/sensors";
  http.begin(serverPath.c_str());
  deserializeSensorsFromDevice(http.GET());

  describe("Test GET actuadores a partir de deviceID");
  serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/actuators";
  http.begin(serverPath.c_str());
  deserializeActuatorsFromDevice(http.GET());

  describe("Test GET sensores a partir de deviceID y tipo");
  serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/sensors/AirQuality";
  http.begin(serverPath.c_str());
  deserializeSensorsFromDevice(http.GET());

  describe("Test GET actuadores a partir de deviceID");
  serverPath = serverName + "api/devices/" + String(DEVICE_ID) + "/actuators/Relay";
  http.begin(serverPath.c_str());
  deserializeActuatorsFromDevice(http.GET());
}

void POST_tests()
{
  String actuator_states_body = serializeActuatorStatusBody(random(2000, 4000) / 100, true, 1, millis());
  describe("Test POST con actuator state");
  String serverPath = serverName + "api/actuator_states";
  http.begin(serverPath.c_str());
  test_response(http.POST(actuator_states_body));

  String sensor_value_body = serializeSensorValueBody(74, millis(), mq135_sensor.getPPM());
  describe("Test POST con sensor value");
  serverPath = serverName + "api/sensor_values";
  http.begin(serverPath.c_str());
  test_response(http.POST(sensor_value_body));

  // String device_body = serializeDeviceBody(String(DEVICE_ID), ("Name_" + String(DEVICE_ID)).c_str(), ("mqtt_" + String(DEVICE_ID)).c_str(), 12);
  // describe("Test POST with path and body and response");
  // serverPath = serverName + "api/device";
  // http.begin(serverPath.c_str());
  // test_response(http.POST(actuator_states_body));
}

// conecta o reconecta al MQTT
// consigue conectar -> suscribe a topic y publica un mensaje
// no -> espera 5 segundos
void ConnectMqtt()
{
  Serial.print("Empezando conexion MQTT...");
  if (client.connect(MQTT_CLIENT_NAME))
  {
    client.subscribe("mqttChannelDevice5");
    client.publish("hola/mundo", "conectado");
  }
  else
  {
    Serial.print("Fallo en la conexion MQTT, rc=");
    Serial.print(client.state());
    Serial.println(" vuelve a intentarlo en 5 segundos");

    delay(5000);
  }
}

// gestiona la comunicación MQTT
// comprueba que el cliente está conectado
// no -> intenta reconectar
// si -> llama al MQTT loop
void HandleMqtt()
{
  if (!client.connected())
  {
    ConnectMqtt();
  }
  client.loop();
}

// Run the tests!
void loop()
{



   if(cont == 0){
    POST_tests();
    cont = 10000;
    String sensor_values_body;
    String sensor_value_body;

 
  float ppm = mq135_sensor.getPPM();

  Serial.println("Calidad de aire: ");
  Serial.println(ppm);
  Serial.print(" PPM");

  }else{
    cont = cont - 1;
  }




  HandleMqtt();
}