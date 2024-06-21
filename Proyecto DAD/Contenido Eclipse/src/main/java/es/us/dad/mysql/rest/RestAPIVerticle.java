package es.us.dad.mysql.rest;

import java.util.Calendar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import es.us.dad.mysql.entities.Actuator;
import es.us.dad.mysql.entities.ActuatorStatus;
import es.us.dad.mysql.entities.ActuatorType;
import es.us.dad.mysql.entities.Device;
import es.us.dad.mysql.entities.Group;
import es.us.dad.mysql.entities.Sensor;
import es.us.dad.mysql.entities.SensorType;
import es.us.dad.mysql.entities.SensorValue;
import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import es.us.dad.mysql.messages.DatabaseMessageIdAndActuatorType;
import es.us.dad.mysql.messages.DatabaseMessageIdAndSensorType;
import es.us.dad.mysql.messages.DatabaseMessageLatestValues;
import es.us.dad.mysql.messages.DatabaseMessageType;
import es.us.dad.mysql.messages.DatabaseMethod;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestAPIVerticle extends AbstractVerticle {

	private transient Gson gson;

	@Override
	public void start(Promise<Void> startFuture) {

		// Instanciar un objeto Gson serializador utilizando un formato de fecha específico
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Definiendo el objeto del enrutador
		Router router = Router.router(vertx);

		// Manejando cualquier resultado de inicio del servidor
		HttpServer httpServer = vertx.createHttpServer();
		httpServer.requestHandler(router::handle).listen(80, result -> {
			if (result.succeeded()) {
				System.out.println("API Rest is listening on port 80");
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// Definiendo las rutas URI para cada método en la interfaz RESTful, incluyendo el manejo del cuerpo.
		router.route("/api*").handler(BodyHandler.create());

		// Definicion del punto de acceso para las operaciones CRUD
		router.get("/api/groups/:group").handler(this::getGroupById);
		router.post("/api/groups").handler(this::addGroup);
		router.delete("/api/groups/:groupid").handler(this::deleteGroup);
		router.put("/api/groups/:groupid").handler(this::putGroup);
		router.get("/api/groups/:groupid/devices").handler(this::getDevicesFromGroup);
		router.put("/api/groups/:groupid/devices/:deviceid").handler(this::addDeviceToGroup);

		router.get("/api/devices/:device").handler(this::getDeviceById);
		router.post("/api/devices").handler(this::addDevice);
		router.delete("/api/devices/:deviceid").handler(this::deleteDevice);
		router.put("/api/devices/:deviceid").handler(this::putDevice);
		router.get("/api/devices/:deviceid/sensors").handler(this::getSensorsFromDevice);
		router.get("/api/devices/:deviceid/actuators").handler(this::getActuatorsFromDevice);
		router.get("/api/devices/:deviceid/sensors/:type").handler(this::getSensorsFromDeviceAndType);
		router.get("/api/devices/:deviceid/actuators/:type").handler(this::getActuatorsFromDeviceAndType);

		router.get("/api/sensors/:sensor").handler(this::getSensorById);
		router.post("/api/sensors").handler(this::addSensor);
		router.delete("/api/sensors/:sensorid").handler(this::deleteSensor);
		router.put("/api/sensors/:sensorid").handler(this::putSensor);

		router.get("/api/actuators/:actuator").handler(this::getActuatorById);
		router.post("/api/actuators").handler(this::addActuator);
		router.delete("/api/actuators/:actuatorid").handler(this::deleteActuator);
		router.put("/api/actuators/:actuatorid").handler(this::putActuator);

		router.post("/api/sensor_values").handler(this::addSensorValue);
		router.delete("/api/sensor_values/:sensorvalueid").handler(this::deleteSensorValue);
		router.get("/api/sensor_values/:sensorid/last").handler(this::getLastSensorValue);
		router.get("/api/sensor_values/:sensorid/latest/:limit").handler(this::getLatestSensorValue);

		router.post("/api/actuator_states").handler(this::addActuatorStatus);
		router.delete("/api/actuator_states/:actuatorstatusid").handler(this::deleteActuatorStatus);
		router.get("/api/actuator_states/:actuatorid/last").handler(this::getLastActuatorStatus);
		router.get("/api/actuator_states/:actuatorid/latest/:limit").handler(this::getLatestActuatorStatus);
	}

	/**
	 * Deserialización del mensaje enviado en el cuerpo de un mensaje al tipo DatabaseMessage.
	 * Es útil para gestionar el intercambio de mensajes entre el controlador y la API REST.

	 * @param handler AsyncResult<Message<Object>> devuelto por el controlador Verticle

	 * @return DatabaseMessage deserializado
	 */
	
	private DatabaseMessage deserializeDatabaseMessageFromMessageHandler(AsyncResult<Message<Object>> handler) {
		return gson.fromJson(handler.result().body().toString(), DatabaseMessage.class);
	}

	/**
	 * Función de controlador GET para el grupo correspondiente a la ruta /api/groups/:groupid 
	 * 
	 * @param routingContext
	 */
	
	private void getGroupById(RoutingContext routingContext) {
		int groupId = Integer.parseInt(routingContext.request().getParam("groupid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Group,
				DatabaseMethod.GetGroup, groupId);

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Group.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para el grupo correspondiente a la ruta /api/groups 
	 * 
	 * @param routingContext
	 */
	
	private void addGroup(RoutingContext routingContext) {
		final Group group = gson.fromJson(routingContext.getBodyAsString(), Group.class);
		if (group == null || group.getMqttChannel() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.Group,
				DatabaseMethod.CreateGroup, gson.toJson(group));

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Group.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador DELETE para el grupo correspondiente a la ruta /api/groups/:groupid
	 * 
	 * @param routingContext
	 */
	
	private void deleteGroup(RoutingContext routingContext) {
		int groupId = Integer.parseInt(routingContext.request().getParam("groupid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.Group,
				DatabaseMethod.DeleteGroup, groupId);

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(responseMessage.getResponseBody());
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador PUT para el grupo correspondiente a la ruta /api/groups/:groupid 
	 * 
	 * @param routingContext
	 */
	
	private void putGroup(RoutingContext routingContext) {
		final Group group = gson.fromJson(routingContext.getBodyAsString(), Group.class);
		int groupId = Integer.parseInt(routingContext.request().getParam("groupid"));

		if (group == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		group.setIdGroup(groupId);
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Group,
				DatabaseMethod.EditGroup, gson.toJson(group));

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Group.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para la placa correspondiente a la ruta /api/groups/:groupid/devices 
	 * 
	 * @param routingContext
	 */
	
	private void getDevicesFromGroup(RoutingContext routingContext) {
		int groupId = Integer.parseInt(routingContext.request().getParam("groupid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Group,
				DatabaseMethod.GetDevicesFromGroupId, groupId);

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Device[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador PUT para la placa correspondiente a la ruta /api/groups/:groupid/devices/:deviceid
	 * 
	 * @param routingContext
	 */
	
	private void addDeviceToGroup(RoutingContext routingContext) {
		int groupId = Integer.parseInt(routingContext.request().getParam("groupid"));
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));
		Device device = new Device();
		device.setIdGroup(groupId);
		device.setIdDevice(deviceId);

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Group,
				DatabaseMethod.AddDeviceToGroup, gson.toJson(device));

		vertx.eventBus().request(RestEntityMessage.Group.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Device.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para la placa correspondiente a la ruta /api/devices/:device
	 * 
	 * @param routingContext
	 */
	
	private void getDeviceById(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("device"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
				DatabaseMethod.GetDevice, deviceId);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Device.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para la placa correspondiente a la ruta /api/devices
	 * 
	 * @param routingContext
	 */
	
	private void addDevice(RoutingContext routingContext) {
		final Device device = gson.fromJson(routingContext.getBodyAsString(), Device.class);
		if (device == null || device.getMqttChannel() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.Device,
				DatabaseMethod.CreateDevice, gson.toJson(device));

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Device.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador DELETE para la placa correspondiente a la ruta /api/devices/:deviceid 
	 * 
	 * @param routingContext
	 */
	
	private void deleteDevice(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.Device,
				DatabaseMethod.DeleteDevice, deviceId);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(responseMessage.getResponseBody());
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador PUT para la placa correspondiente a la ruta /api/devices/:deviceid 
	 * 
	 * @param routingContext
	 */
	
	private void putDevice(RoutingContext routingContext) {
		final Device device = gson.fromJson(routingContext.getBodyAsString(), Device.class);
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));

		if (device == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		device.setIdGroup(deviceId);
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Device,
				DatabaseMethod.EditDevice, gson.toJson(device));

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Device.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el sensor correspondiente a la ruta /api/devices/:deviceid/sensors 
	 * 
	 * @param routingContext
	 */
	
	private void getSensorsFromDevice(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
				DatabaseMethod.GetSensorsFromDeviceId, deviceId);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Sensor[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el actuador correspondiente a la ruta /api/devices/:deviceid/actuators 
	 * 
	 * @param routingContext
	 */
	
	private void getActuatorsFromDevice(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
				DatabaseMethod.GetActuatorsFromDeviceId, deviceId);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Actuator[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el sensor correspondiente a la ruta /api/devices/:deviceid/sensors/:type 
	 * 
	 * @param routingContext
	 */
	
	private void getSensorsFromDeviceAndType(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));
		SensorType type = SensorType.valueOf(routingContext.request().getParam("type"));
		DatabaseMessageIdAndSensorType databaseMessageIdAndSensorType = new DatabaseMessageIdAndSensorType(deviceId,
				type);

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
				DatabaseMethod.GetSensorsFromDeviceIdAndSensorType, databaseMessageIdAndSensorType);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Sensor[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el actuador correspondiente a la ruta /api/devices/:deviceid/actuators/:type
	 * 
	 * @param routingContext
	 */
	
	private void getActuatorsFromDeviceAndType(RoutingContext routingContext) {
		int deviceId = Integer.parseInt(routingContext.request().getParam("deviceid"));
		ActuatorType type = ActuatorType.valueOf(routingContext.request().getParam("type"));
		DatabaseMessageIdAndActuatorType databaseMessageIdAndActuatorType = new DatabaseMessageIdAndActuatorType(
				deviceId, type);

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Device,
				DatabaseMethod.GetActuatorsFromDeviceIdAndActuatorType, databaseMessageIdAndActuatorType);

		vertx.eventBus().request(RestEntityMessage.Device.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Actuator[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el sensor correspondiente a la ruta /api/sensors/:sensorid 
	 * 
	 * @param routingContext
	 */
	
	private void getSensorById(RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensor"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Sensor,
				DatabaseMethod.GetSensor, sensorId);

		vertx.eventBus().request(RestEntityMessage.Sensor.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Sensor.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para el sensor correspondiente a la ruta /api/sensors 
	 * 
	 * @param routingContext
	 */
	
	private void addSensor(RoutingContext routingContext) {
		final Sensor sensor = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		if (sensor == null || sensor.getIdDevice() == null || sensor.getSensorType() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.Sensor,
				DatabaseMethod.CreateSensor, gson.toJson(sensor));

		vertx.eventBus().request(RestEntityMessage.Sensor.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Sensor.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador DELETE para el sensor correspondiente a la ruta /api/sensors/:sensorid 
	 * 
	 * @param routingContext
	 */
	
	private void deleteSensor(RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.Sensor,
				DatabaseMethod.DeleteSensor, sensorId);

		vertx.eventBus().request(RestEntityMessage.Sensor.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(responseMessage.getResponseBody());
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador PUT para el sensor correspondiente a la ruta /api/sensors/:sensorid 
	 * 
	 * @param routingContext
	 */
	
	private void putSensor(RoutingContext routingContext) {
		final Sensor sensor = gson.fromJson(routingContext.getBodyAsString(), Sensor.class);
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorid"));

		if (sensor == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		sensor.setIdSensor(sensorId);
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Sensor,
				DatabaseMethod.EditSensor, gson.toJson(sensor));

		vertx.eventBus().request(RestEntityMessage.Sensor.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Sensor.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el actuador correspondiente a la ruta /api/actuator/:actuatorid 
	 * 
	 * @param routingContext
	 */
	
	private void getActuatorById(RoutingContext routingContext) {
		int actuatorId = Integer.parseInt(routingContext.request().getParam("actuator"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.Actuator,
				DatabaseMethod.GetActuator, actuatorId);

		vertx.eventBus().request(RestEntityMessage.Actuator.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Actuator.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para el actuador correspondiente a la ruta /api/actuators 
	 * 
	 * @param routingContext
	 */
	
	private void addActuator(RoutingContext routingContext) {
		final Actuator actuator = gson.fromJson(routingContext.getBodyAsString(), Actuator.class);
		if (actuator == null || actuator.getIdDevice() == null || actuator.getActuatorType() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.Actuator,
				DatabaseMethod.CreateActuator, gson.toJson(actuator));

		vertx.eventBus().request(RestEntityMessage.Actuator.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Actuator.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador DELETE para el actuador correspondiente a la ruta /api/actuators/:actuatorid 
	 * 
	 * @param routingContext
	 */
	
	private void deleteActuator(RoutingContext routingContext) {
		int actuatorId = Integer.parseInt(routingContext.request().getParam("actuatorid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.Actuator,
				DatabaseMethod.DeleteActuator, actuatorId);

		vertx.eventBus().request(RestEntityMessage.Actuator.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(responseMessage.getResponseBody());
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador PUT para el actuador correspondiente a la ruta /api/actuators/:actuatorid 
	 * 
	 * @param routingContext
	 */
	
	private void putActuator(RoutingContext routingContext) {
		final Actuator actuator = gson.fromJson(routingContext.getBodyAsString(), Actuator.class);
		int actuatorId = Integer.parseInt(routingContext.request().getParam("actuatorid"));

		if (actuator == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		actuator.setIdActuator(actuatorId);
		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Actuator,
				DatabaseMethod.EditActuator, gson.toJson(actuator));

		vertx.eventBus().request(RestEntityMessage.Sensor.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(Actuator.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para el sensorValue correspondiente a la ruta /api/sensor_values 
	 * 
	 * @param routingContext
	 */
	
	private void addSensorValue(RoutingContext routingContext) {
		final SensorValue sensorValue = gson.fromJson(routingContext.getBodyAsString(), SensorValue.class);
		if (sensorValue == null || sensorValue.getIdSensor() == null || sensorValue.getValue() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		if (sensorValue.getTimestamp() == null) {
			sensorValue.setTimestamp(Calendar.getInstance().getTimeInMillis());
		}

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.SensorValue,
				DatabaseMethod.CreateSensorValue, gson.toJson(sensorValue));

		vertx.eventBus().request(RestEntityMessage.SensorValue.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
						.end(gson.toJson(responseMessage.getResponseBodyAs(SensorValue.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador DELETE para el sensorValue correspondiente a la ruta
	 * /api/sensor_values/lastest/:sensorvalueid 
	 * 
	 * @param routingContext
	 */
	
	private void deleteSensorValue(RoutingContext routingContext) {
		int sensorValueId = Integer.parseInt(routingContext.request().getParam("sensorvalueid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.SensorValue,
				DatabaseMethod.DeleteSensorValue, sensorValueId);

		vertx.eventBus().request(RestEntityMessage.SensorValue.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(responseMessage.getResponseBody());
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el sensorValue correspondiente a la ruta /api/sensor_values/:sensorid/last
	 * 
	 * @param routingContext
	 */
	
	private void getLastSensorValue(RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.SensorValue,
				DatabaseMethod.GetLastSensorValueFromSensorId, sensorId);

		vertx.eventBus().request(RestEntityMessage.SensorValue.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(SensorValue.class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador GET para el sensorValue correspondiente a la ruta
	 * /api/sensor_values/:sensorid/latest/:limit 
	 * 
	 * @param routingContext
	 */
	
	private void getLatestSensorValue(RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorid"));
		int limit = Integer.parseInt(routingContext.request().getParam("limit"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.SensorValue,
				DatabaseMethod.GetLatestSensorValuesFromSensorId, new DatabaseMessageLatestValues(sensorId, limit));

		vertx.eventBus().request(RestEntityMessage.SensorValue.getAddress(), gson.toJson(databaseMessage), handler -> {
			if (handler.succeeded()) {
				DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
						.end(gson.toJson(responseMessage.getResponseBodyAs(SensorValue[].class)));
			} else {
				routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			}
		});
	}

	/**
	 * Función de controlador POST para el actuatorStatus correspondiente a la ruta /api/actuator_states 
	 * 
	 * @param routingContext
	 */
	
	private void addActuatorStatus(RoutingContext routingContext) {
		final ActuatorStatus actuatorStatus = gson.fromJson(routingContext.getBodyAsString(), ActuatorStatus.class);
		if (actuatorStatus == null || actuatorStatus.getIdActuator() == null || actuatorStatus.getStatus() == null) {
			routingContext.response().putHeader("content-type", "application/json").setStatusCode(500).end();
			return;
		}

		if (actuatorStatus.getTimestamp() == null) {
			actuatorStatus.setTimestamp(Calendar.getInstance().getTimeInMillis());
		}

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.INSERT, DatabaseEntity.ActuatorStatus,
				DatabaseMethod.CreateActuatorStatus, gson.toJson(actuatorStatus));

		vertx.eventBus().request(RestEntityMessage.ActuatorStatus.getAddress(), gson.toJson(databaseMessage),
				handler -> {
					if (handler.succeeded()) {
						DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(201)
								.end(gson.toJson(responseMessage.getResponseBodyAs(ActuatorStatus.class)));
					} else {
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(500)
								.end();
					}
				});
	}

	/**
	 * Función de controlador DELETE para el actuatorStatus correspondiente a la ruta
	 * /api/actuator_states/lastest/:actuatorstatusid
	 * 
	 * @param routingContext
	 */
	
	private void deleteActuatorStatus(RoutingContext routingContext) {
		int sensorValueId = Integer.parseInt(routingContext.request().getParam("actuatorstatusid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.DELETE, DatabaseEntity.ActuatorStatus,
				DatabaseMethod.DeleteActuatorStatus, sensorValueId);

		vertx.eventBus().request(RestEntityMessage.ActuatorStatus.getAddress(), gson.toJson(databaseMessage),
				handler -> {
					if (handler.succeeded()) {
						DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
								.end(responseMessage.getResponseBody());
					} else {
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(500)
								.end();
					}
				});
	}

	/**
	 * Función de controlador GET para el actuatorStatus correspondiente a la ruta /api/actuator_states/:actuatorid/last
	 * 
	 * @param routingContext
	 */
	
	private void getLastActuatorStatus(RoutingContext routingContext) {
		int actuatorId = Integer.parseInt(routingContext.request().getParam("actuatorid"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.ActuatorStatus,
				DatabaseMethod.GetLastActuatorStatusFromActuatorId, actuatorId);

		vertx.eventBus().request(RestEntityMessage.ActuatorStatus.getAddress(), gson.toJson(databaseMessage),
				handler -> {
					if (handler.succeeded()) {
						DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
								.end(gson.toJson(responseMessage.getResponseBodyAs(SensorValue.class)));
					} else {
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(500)
								.end();
					}
				});
	}

	/**
	 * Función de controlador GET para el actuatorStatus correspondiente a la ruta
	 * /api/actuator_states/:actuatorid/latest/:limit
	 * 
	 * @param routingContext
	 */
	
	private void getLatestActuatorStatus(RoutingContext routingContext) {
		int actuatorId = Integer.parseInt(routingContext.request().getParam("actuatorid"));
		int limit = Integer.parseInt(routingContext.request().getParam("limit"));

		DatabaseMessage databaseMessage = new DatabaseMessage(DatabaseMessageType.SELECT, DatabaseEntity.ActuatorStatus,
				DatabaseMethod.GetLatestActuatorStatesFromActuatorId,
				new DatabaseMessageLatestValues(actuatorId, limit));

		vertx.eventBus().request(RestEntityMessage.ActuatorStatus.getAddress(), gson.toJson(databaseMessage),
				handler -> {
					if (handler.succeeded()) {
						DatabaseMessage responseMessage = deserializeDatabaseMessageFromMessageHandler(handler);
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(200)
								.end(gson.toJson(responseMessage.getResponseBodyAs(ActuatorStatus[].class)));
					} else {
						routingContext.response().putHeader("content-type", "application/json").setStatusCode(500)
								.end();
					}
				});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
