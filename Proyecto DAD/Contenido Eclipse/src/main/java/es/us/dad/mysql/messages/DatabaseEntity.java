package es.us.dad.mysql.messages;

/**
 * Clase que define la enumeración correspondiente a las diferentes entidades almacenadase
 * en la base de datos y gestionadas por el sistema. También se utiliza para obtener el
 * nombre del canal en el cual los mensajes provenientes del controlador serán publicados
 * y como receptor el Verticle para la gestión de acceso a la base de datos.
 */

public enum DatabaseEntity {
	Sensor("Sensor"), Actuator("Actuator"), Group("Group"), Device("Device"), SensorValue("SensorValue"),
	ActuatorStatus("ActuatorStatus");

	/**
	 * Valor textual asociado con el valor enumerado.
	 */
	
	private final String value;

	/**
	 * Valor textual de la dirección del canal donde se publicarán los mensajes asociados
	 * con cada lista.
	 */
	
	private final String address;

	/**
	 * Constructor de la lista que permite definir el valor textual del canal de
	 * publicación y la representación de cada lista.

	 * @param value Representación textual del valor asociado.
	 */
	
	private DatabaseEntity(String value) {
		this.value = value;
		this.address = value;
	}

	public String getDatabaseEntity() {
		return value;
	}

	/**
	 * Canal de publicación para los mensajes asociados con esta entidad.
	 * 
	 * @return
	 */
	
	public String getAddress() {
		return address;
	}
}
