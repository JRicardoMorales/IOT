package es.us.dad.controllers;

import java.util.Calendar;

import es.us.dad.mqtt.MqttClientUtil;
import es.us.dad.mysql.entities.Device;
import es.us.dad.mysql.entities.Group;
import es.us.dad.mysql.entities.Sensor;
import es.us.dad.mysql.entities.SensorValue;
import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import es.us.dad.mysql.messages.DatabaseMessageType;
import es.us.dad.mysql.messages.DatabaseMethod;
import es.us.dad.mysql.rest.RestEntityMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Esta clase ofrece una serie de métodos de utilidad para SensorValues. Todos los
 * métodos están marcados como estáticos, por lo que no es necesario instanciar la clase
 * para utilizarlos.
 *
 */

public class SensorValuesController extends AbstractController {

	/**
	 * Constructor de la clase donde se indica el tipo de entidad gestionada por la clase
	 * a la clase AbstractController, donde se define la funcionalidad básica de los
	 * controladores.
	 */
	
	public SensorValuesController() {
		super(DatabaseEntity.SensorValue);
	}

	/**
	 * Método que permite lanzar el Verticle. Este método desplegará el controlador que
	 * luego atenderá las solicitudes de uso realizadas por la API Rest. El canal al que
	 * está asociado este controlador dependerá de la entidad controlada por cada 
	 * controlador. En este caso, la entidad es SensorValue. Ten en cuenta que en este
	 * caso, el canal de comunicación no es dado por la clase DatabaseEntity, sino a
	 * través de la clase RestEntityMessage. Esto se debe a la necesidad de definir
	 * nombres de canales diferentes para evitar que se superpongan los mensajes
	 * provenientes de la comunicación entre el controlador y la capa de acceso a datos.
	 */
	
	public void start(Promise<Void> startFuture) {
		MqttClientUtil mqttClientUtil = MqttClientUtil.getInstance(vertx);
		getVertx().eventBus().consumer(RestEntityMessage.SensorValue.getAddress(), message -> {
			DatabaseMessage databaseMessage = gson.fromJson((String) message.body(), DatabaseMessage.class);
			/*
			 * El switch considera todos los mensajes que puede gestionar este controlador
			 * y los delega al método launchDatabaseOperation de la clase AbstractController,
			 * el cual envía el mensaje original para ser procesado por el Verticle de
			 * acceso a datos. Ten en cuenta que este switch no es necesario a menos que
			 * haya métodos en los que las operaciones sean diferentes a las descritas
			 * anteriormente. Sin embargo, para mayor claridad, se ha decidido hacer
			 * explícitos los valores del enumerado DatabaseMethod que gestiona cada
			 * controlador. Esta implementación asume que el mensaje proveniente de la
			 * API Rest está bien definido para que pueda ser reutilizado en la solicitud
			 * en la capa de acceso a datos.
			 */
			
			switch (databaseMessage.getMethod()) {
			case CreateSensorValue:
				launchDatabaseOperation(message);
				SensorValue sensorValue = databaseMessage.getRequestBodyAs(SensorValue.class);

				// Obteniendo la entidad del sensor a partir de la propiedad idSensor presente en SensorValue.
				launchDatabaseOperation(DatabaseEntity.Sensor, new DatabaseMessage(DatabaseMessageType.SELECT,
						DatabaseEntity.Sensor, DatabaseMethod.GetSensor, sensorValue.getIdSensor())).future()
						.onComplete(res -> {
							if (res.succeeded()) {
								Sensor sensor = res.result().getResponseBodyAs(Sensor.class);
								if (sensor != null) {

									// Obteniendo la entidad del dispositivo a partir de la propiedad idDevice presente en Sensor.
									launchDatabaseOperation(DatabaseEntity.Device,
											new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
													DatabaseMethod.GetDevice, sensor.getIdDevice()))
											.future().onComplete(resDevice -> {
												Device device = resDevice.result().getResponseBodyAs(Device.class);
												if (resDevice.succeeded()) {
													String jsonValue = "0";
													float valor = sensorValue.getValue();
													if(valor <= 6700) {
														jsonValue = "0";
													} else {
														jsonValue = "1";
													}
													// Publicar mensaje MQTT en el tema MQTT del dispositivo
													mqttClientUtil.publishMqttMessage(device.getMqttChannel(),
															jsonValue, handler -> {
																System.out.println(handler.result());
															});

													// Obteniendo la entidad de grupo a partir de la propiedad idGroup presente en Device
													launchDatabaseOperation(DatabaseEntity.Group,
															new DatabaseMessage(DatabaseMessageType.SELECT,
																	DatabaseEntity.Group, DatabaseMethod.GetGroup,
																	device.getIdGroup()))
															.future().onComplete(resGroup -> {
																Group group = resGroup.result()
																		.getResponseBodyAs(Group.class);
																if (resGroup.succeeded()) {
																	// Publicar mensaje MQTT en el tema MQTT del grupo
																	mqttClientUtil.publishMqttMessage(
																			group.getMqttChannel(),
																			gson.toJson(sensorValue), handler -> {
																				System.out.println(handler.result());
																			});
																}
															});

												}
											});

									// Actualiza la entidad del dispositivo con la marca de tiempo actual donde el último
									// sensor ha sido modificado.
									launchDatabaseOperation(DatabaseEntity.Device,
											new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Device,
													DatabaseMethod.EditDevice,
													new Device(sensor.getIdDevice(), null, null, null, null, null,
															Calendar.getInstance().getTimeInMillis())));
								}
							}
						});
				break;
			case DeleteSensorValue:
				launchDatabaseOperation(message);
				break;
			case GetLastSensorValueFromSensorId:
				launchDatabaseOperation(message);
				break;
			case GetLatestSensorValuesFromSensorId:
				launchDatabaseOperation(message);
				break;
			default:
				/*
				 * En caso de que la solicitud no pueda ser manejada por este controlador,
				 * se emitirá un código de error 401 en respuesta al mensaje recibido de la API Rest.
				 */
				message.fail(401, "Method not allowed");
			}
		});
		startFuture.complete();
	}

	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
