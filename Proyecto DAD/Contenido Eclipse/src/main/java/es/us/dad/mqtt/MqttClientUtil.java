package es.us.dad.mqtt;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import io.vertx.mqtt.MqttClientOptions;

/**
 * Esta clase necesita que el broker Mosquitto se esté ejecutando en localhost.
 * Para instalar el broker Mosquitto en tu computadora, puedes seguir las instrucciones
 * en https://mosquitto.org/download/. El puerto de implementación predeterminado para
 * Mosquitto es 1883. Mosquitto permite solicitudes de usuario anónimas, pero el archivo
 * mosquitto.conf debe modificarse (MacOS: nano /usr/local/etc/mosquitto/mosquitto.conf,
 * Linux: nano /etc/mosquitto/mosquitto.conf, Windows: editar archivo ubicado en la carpeta
 * de instalación de Mosquitto). Para este propósito, descomenta la línea donde se define
 * "allow_anonymous true". Si necesitas conectarte a este broker desde un dispositivo que
 * no sea localhost, descomenta esta línea: "listener 1883 0.0.0.0". Deberás reiniciar el
 * servicio de mosquitto una vez que se realice este cambio (MacOS: brew services restar
 * mosquitto, Windows: net stop mosquitto y net start mosquitto [powershell con derechos
 * de administrador], Linux: sudo systemctl restart mosquitto).
 *
 */
public class MqttClientUtil {

	protected static transient MqttClient mqttClient;

	private static transient MqttClientUtil mqttClientClass = null;

	private MqttClientUtil(Vertx vertx) {
		mqttClient = MqttClient.create(vertx, new MqttClientOptions());
		mqttClient.connect(1883, "localhost", s -> {
			if (s.succeeded()) {
				System.out.println("Sucessfully connected to MQTT brocker");
			} else {
				System.err.println(s.cause());
			}
		});
	}

	public void publishMqttMessage(String topic, String payload, Handler<AsyncResult<Integer>> handler) {
		mqttClient.publish(topic, Buffer.buffer(payload), MqttQoS.AT_LEAST_ONCE, false, false, handler);
	}

	public void subscribeMqttTopic(String topic, Handler<AsyncResult<Integer>> handler) {
		mqttClient.subscribe(topic, MqttQoS.AT_LEAST_ONCE.value(), handler);
	}

	public void unsubscribeMqttTopic(String topic) {
		mqttClient.unsubscribe(topic);
	}

	public static MqttClientUtil getInstance(Vertx vertx) {
		if (mqttClientClass == null) {
			mqttClientClass = new MqttClientUtil(vertx);
		}
		return mqttClientClass;
	}

}
