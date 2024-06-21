package es.us.dad.mysql.entities;

/**
 * Esta clase representa los valores generados por un sensor. Cada vez que se reporta el
 * valor de un cierto sensor, se generará una nueva instancia de esta entidad en la base
 * de datos. Los valores en la base de datos nunca se sobrescriben, sino que se generan
 * nuevos con su marca de tiempo correspondiente.
 *
 */

public class SensorValue {

	/**
	 * Clave primaria asociada al valor del sensor. Este identificador es único para cada
	 * valor de un sensor en la base de datos. De esta manera, cada vez que se genera un
	 * nuevo valor del sensor y se almacena en la base de datos, se generará una nueva
	 * tupla con un nuevo identificador.
	 */
	
	private Integer idSensorValue;

	/**
	 * Valor numérico obtenido por el sensor asociado con el identificador de sensorId.
	 */
	
	private Float value;

	/**
	 * Identificador del sensor en el que se ha generado dicho valor. Este sensor representa
	 * de manera única un sensor conectado a un dispositivo.
	 */
	
	private Integer idSensor;

	/**
	 * Marca de tiempo en la cual se genera los datos proporcionados por el sensor. La marca de
	 * tiempo está expresada en tiempo Unix, definido como los milisegundos desde el 1 de enero
	 * de 1970.
	 */
	
	private Long timestamp;

	/**
	 * Valor lógico que indica si el valor del sensor ha sido eliminado. La eliminación se realiza
	 * marcando este campo como verdadero, por lo que el valor nunca se elimina permanentemente de la
	 * base de datos.
	 */
	
	private Boolean removed;

	public SensorValue() {
		super();
	}

	public SensorValue(Float value, Integer idSensor, Long timestamp, Boolean removed) {
		super();
		this.value = value;
		this.idSensor = idSensor;
		this.removed = removed;
		this.timestamp = timestamp;
	}

	public SensorValue(Integer idSensorValue, Float value, Integer idSensor, Long timestamp, Boolean removed) {
		super();
		this.idSensorValue = idSensorValue;
		this.value = value;
		this.idSensor = idSensor;
		this.removed = removed;
		this.timestamp = timestamp;
	}

	public Float getValue() {
		return value;
	}

	public void setValue(Float value) {
		this.value = value;
	}

	public Integer getIdSensor() {
		return idSensor;
	}

	public void setIdSensor(Integer idSensor) {
		this.idSensor = idSensor;
	}

	public Boolean isRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Integer getIdSensorValue() {
		return idSensorValue;
	}

	public void setIdSensorValue(Integer idSensorValue) {
		this.idSensorValue = idSensorValue;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idSensor == null) ? 0 : idSensor.hashCode());
		result = prime * result + ((idSensorValue == null) ? 0 : idSensorValue.hashCode());
		result = prime * result + ((removed == null) ? 0 : removed.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		SensorValue other = (SensorValue) obj;
		if (idSensor == null) {
			if (other.idSensor != null)
				return false;
		} else if (!idSensor.equals(other.idSensor))
			return false;
		if (idSensorValue == null) {
			if (other.idSensorValue != null)
				return false;
		} else if (!idSensorValue.equals(other.idSensorValue))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "SensorValue [idSensorValue=" + idSensorValue + ", value=" + value + ", idSensor=" + idSensor
				+ ", timestamp=" + timestamp + ", removed=" + removed + "]";
	}

	public boolean equalsWithNoIdConsidered(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorValue other = (SensorValue) obj;
		if (idSensor == null) {
			if (other.idSensor != null)
				return false;
		} else if (!idSensor.equals(other.idSensor))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

}
