package es.us.dad.mysql.entities;

/**
 * Representación de la entidad Sensor. Un sensor estará asociado a un dispositivo a
 * través del identificador del dispositivo. Los valores del sensor definidos a través
 * de la entidad SensorValue estarán asociados a dicho sensor.
 */

public class Sensor {

	/**
	 * Nombre del sensor. Se puede utilizar para diferenciar entre diferentes sensores asociados
	 * con el mismo dispositivo.
	 */
	
	private String name;

	/**
	 * Identificador que representa el sensor y sirve como clave primaria. Cada sensor asociado
	 * con una placa tendrá un identificador único en la base de datos.
	 */
	
	private Integer idSensor;

	/**
	 * Identificador del dispositivo al cual está vinculado el sensor.
	 */
	
	private Integer idDevice;

	/**
	 * Tipo de sensor que se definirá a través del enumerador SensorType.
	 */
	
	private SensorType sensorType;

	/**
	 * Valor lógico que indica si el sensor ha sido eliminado. La eliminación se realiza
	 * marcando este campo como verdadero, por lo que el valor nunca se elimina
	 * permanentemente de la base de datos.
	 */
	
	private Boolean removed;

	public Sensor() {
		super();
	}

	public Sensor(String name, Integer idDevice, SensorType sensorType, Boolean removed) {
		super();
		this.name = name;
		this.idDevice = idDevice;
		this.sensorType = sensorType;
		this.removed = removed;
	}

	public Sensor(String name, Integer idDevice, String sensorType, Boolean removed) {
		super();
		this.name = name;
		this.idDevice = idDevice;
		this.sensorType = SensorType.valueOf(sensorType);
		this.removed = removed;
	}

	public Sensor(Integer idSensor, String name, Integer idDevice, SensorType sensorType, boolean removed) {
		super();
		this.name = name;
		this.idSensor = idSensor;
		this.idDevice = idDevice;
		this.sensorType = sensorType;
		this.removed = removed;
	}

	public Sensor(Integer idSensor, String name, Integer idDevice, String sensorType, boolean removed) {
		super();
		this.name = name;
		this.idSensor = idSensor;
		this.idDevice = idDevice;
		this.sensorType = SensorType.valueOf(sensorType);
		this.removed = removed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	public SensorType getSensorType() {
		return sensorType;
	}

	public void setSensorType(SensorType sensorType) {
		this.sensorType = sensorType;
	}

	public Boolean isRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result + ((idSensor == null) ? 0 : idSensor.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((removed == null) ? 0 : removed.hashCode());
		result = prime * result + ((sensorType == null) ? 0 : sensorType.hashCode());
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
		Sensor other = (Sensor) obj;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (idSensor == null) {
			if (other.idSensor != null)
				return false;
		} else if (!idSensor.equals(other.idSensor))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (sensorType != other.sensorType)
			return false;
		return true;
	}

	public boolean equalsWithNoIdConsidered(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sensor other = (Sensor) obj;
		if (idDevice == null) {
			if (other.idDevice != null)
				return false;
		} else if (!idDevice.equals(other.idDevice))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (sensorType != other.sensorType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Sensor [name=" + name + ", idSensor=" + idSensor + ", idDevice=" + idDevice + ", sensorType="
				+ sensorType + ", removed=" + removed + "]";
	}

}
