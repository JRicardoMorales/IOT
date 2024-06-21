package es.us.dad.mysql.entities;

/**
 * Esta clase representa un dispositivo al cual se pueden conectar diferentes sensores
 * y actuadores. Generalmente, estos dispositivos serán ESP32, ESP8266, etc. Además, los
 * dispositivos estarán vinculados a cierto grupo.
 */

public class Device {

	/**
	 * Identificador único del dispositivo en el sistema. Este es la clave primaria.
	 * Al ser autogenerado, pueden ocurrir problemas de depuración cuando un dispositivo
	 * es eliminado, ya que este identificador no puede ser reutilizado.
	 */
	
	private Integer idDevice;

	/**
	 * Para solucionar el problema anterior, se define esta propiedad que también es un
	 * identificador para el dispositivo. Sin embargo, como no es autoincremental
	 * (pero sí es único), puede ser reutilizado en caso de que el sensor original con
	 * ese valor sea eliminado.
	 */
	
	private String deviceSerialId;

	/**
	 * Nombre asignado al dispositivo.
	 */
	
	private String name;

	/**
	 * Canal MQTT asociado con este dispositivo y a través del cual se compartirán los
	 * mensajes MQTT necesarios entre el servidor y dicho dispositivo.
	 */
	
	private String mqttChannel;

	/**
	 * Identificador del grupo al que pertenece el dispositivo.
	 */
	
	private Integer idGroup;

	/**
	 * Marca de tiempo de cuándo se tomó la última lectura de un sensor asociado
	 * con este dispositivo.
	 */
	
	private Long lastTimestampSensorModified;

	/**
	 * Marca de tiempo de cuándo se estableció por última vez el estado de un actuador
	 * asociado con este dispositivo.
	 */
	
	private Long lastTimestampActuatorModified;

	public Device() {
		super();
	}

	public Device(String deviceSerialId, String name, String mqttChannel,
			Long lastTimestampSensorModified, Long lastTimestampActuatorModified, Integer idGroup) {
		super();
		this.deviceSerialId = deviceSerialId;
		this.name = name;
		this.mqttChannel = mqttChannel;
		this.lastTimestampSensorModified = lastTimestampSensorModified;
		this.lastTimestampActuatorModified = lastTimestampActuatorModified;
		this.idGroup = idGroup;
	}

	public Device(Integer idDevice, String deviceSerialId, String name, Integer idGroup, String mqttChannel,
			Long lastTimestampSensorModified, Long lastTimestampActuatorModified) {
		super();
		this.idDevice = idDevice;
		this.deviceSerialId = deviceSerialId;
		this.name = name;
		this.idGroup = idGroup;
		this.mqttChannel = mqttChannel;
		this.lastTimestampSensorModified = lastTimestampSensorModified;
		this.lastTimestampActuatorModified = lastTimestampActuatorModified;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	public String getDeviceSerialId() {
		return deviceSerialId;
	}

	public void setDeviceSerialId(String deviceSerialId) {
		this.deviceSerialId = deviceSerialId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMqttChannel() {
		return mqttChannel;
	}

	public void setMqttChannel(String mqttChannel) {
		this.mqttChannel = mqttChannel;
	}

	public Long getLastTimestampSensorModified() {
		return lastTimestampSensorModified;
	}

	public void setLastTimestampSensorModified(Long lastTimestampSensorModified) {
		this.lastTimestampSensorModified = lastTimestampSensorModified;
	}

	public Long getLastTimestampActuatorModified() {
		return lastTimestampActuatorModified;
	}

	public void setLastTimestampActuatorModified(Long lastTimestampActuatorModified) {
		this.lastTimestampActuatorModified = lastTimestampActuatorModified;
	}

	public Integer getIdGroup() {
		return idGroup;
	}

	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceSerialId == null) ? 0 : deviceSerialId.hashCode());
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result + ((idGroup == null) ? 0 : idGroup.hashCode());
		result = prime * result
				+ ((lastTimestampActuatorModified == null) ? 0 : lastTimestampActuatorModified.hashCode());
		result = prime * result + ((lastTimestampSensorModified == null) ? 0 : lastTimestampSensorModified.hashCode());
		result = prime * result + ((mqttChannel == null) ? 0 : mqttChannel.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (deviceSerialId == null) {
			if (other.deviceSerialId != null)
				return false;
		} else if (!deviceSerialId.equals(other.deviceSerialId))
			return false;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (idGroup == null) {
			if (other.idGroup != null)
				return false;
		} else if (!idGroup.equals(other.idGroup))
			return false;
		if (lastTimestampActuatorModified == null) {
			if (other.lastTimestampActuatorModified != null)
				return false;
		} else if (!lastTimestampActuatorModified.equals(other.lastTimestampActuatorModified))
			return false;
		if (lastTimestampSensorModified == null) {
			if (other.lastTimestampSensorModified != null)
				return false;
		} else if (!lastTimestampSensorModified.equals(other.lastTimestampSensorModified))
			return false;
		if (mqttChannel == null) {
			if (other.mqttChannel != null)
				return false;
		} else if (!mqttChannel.equals(other.mqttChannel))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Device [idDevice=" + idDevice + ", deviceSerialId=" + deviceSerialId + ", name=" + name
				+ ", mqttChannel=" + mqttChannel + ", idGroup=" + idGroup + ", lastTimestampSensorModified="
				+ lastTimestampSensorModified + ", lastTimestampActuatorModified=" + lastTimestampActuatorModified
				+ "]";
	}

	public boolean equalsWithNoIdConsidered(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Device other = (Device) obj;
		if (deviceSerialId == null) {
			if (other.deviceSerialId != null)
				return false;
		} else if (!deviceSerialId.equals(other.deviceSerialId))
			return false;
		if (idGroup == null) {
			if (other.idGroup != null)
				return false;
		} else if (!idGroup.equals(other.idGroup))
			return false;
		if (lastTimestampActuatorModified == null) {
			if (other.lastTimestampActuatorModified != null)
				return false;
		} else if (!lastTimestampActuatorModified.equals(other.lastTimestampActuatorModified))
			return false;
		if (lastTimestampSensorModified == null) {
			if (other.lastTimestampSensorModified != null)
				return false;
		} else if (!lastTimestampSensorModified.equals(other.lastTimestampSensorModified))
			return false;
		if (mqttChannel == null) {
			if (other.mqttChannel != null)
				return false;
		} else if (!mqttChannel.equals(other.mqttChannel))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
