package es.us.dad.mysql.rest;

/**
 * Un enumerador que representa la entidad asociada con el mensaje que la API Rest
 * intercambia con el controlador. Esta entidad difiere de la utilizada entre el
 * controlador y la base de datos para evitar conflictos entre los mensajes intercambiados
 * por ambos elementos.
 *
 */

public enum RestEntityMessage {
	Sensor("SensorRest"), Actuator("ActuatorRest"), Group("GroupRest"), Device("DeviceRest"),
	SensorValue("SensorValueRest"), ActuatorStatus("ActuatorStatusRest");

	private final String value;
	private final String address;

	private RestEntityMessage(String value) {
		this.value = value;
		this.address = value;
	}

	public String getRestEntityMessage() {
		return value;
	}

	public String getAddress() {
		return address;
	}
}
