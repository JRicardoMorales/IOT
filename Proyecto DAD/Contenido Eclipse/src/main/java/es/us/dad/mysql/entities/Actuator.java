package es.us.dad.mysql.entities;

/**
 * Representación de la entidad Actuador. Un actuador estará asociado a un dispositivo a
 * través del identificador del dispositivo. Dicho actuador estará asociado con sus
 * estados definidos a través de la entidad ActuatorStatus.
 *
 */

public class Actuator {

	/**
	 * Nombre del actuador. Se puede utilizar para diferenciar entre diferentes
	 * actuadores asociados al mismo dispositivo.
	 */
	
	private String name;

	/**
	 * Identificador que representa al actuador y sirve como clave primaria.
	 * Cada actuador asociado a una placa tendrá un identificador único en la base
	 * de datos.
	 */
	
	private Integer idActuator;

	/**
	 * Identificador del dispositivo al que está vinculado el actuador.
	 */
	
	private Integer idDevice;

	/**
	 * Tipo de actuador que será definido a través del enumerador ActuatorType.
	 */
	private ActuatorType actuatorType;

	/**
	 * Valor lógico que indica si el actuador ha sido eliminado. La eliminación se realiza
	 * marcando este campo como verdadero, por lo que el valor nunca se elimina
	 * permanentemente de la base de datos.
	 */
	
	private Boolean removed;

	public Actuator() {
		super();
	}

	public Actuator(String name, Integer idDevice, ActuatorType actuatorType, Boolean removed) {
		super();
		this.name = name;
		this.idDevice = idDevice;
		this.actuatorType = actuatorType;
		this.removed = removed;
	}

	public Actuator(String name, Integer idDevice, String actuatorType, Boolean removed) {
		super();
		this.name = name;
		this.idDevice = idDevice;
		this.actuatorType = ActuatorType.valueOf(actuatorType);
		this.removed = removed;
	}

	public Actuator(Integer idActuator, String name, Integer idDevice, ActuatorType actuatorType, Boolean removed) {
		super();
		this.name = name;
		this.idActuator = idActuator;
		this.idDevice = idDevice;
		this.actuatorType = actuatorType;
		this.removed = removed;
	}

	public Actuator(Integer idActuator, String name, Integer idDevice, String actuatorType, Boolean removed) {
		super();
		this.name = name;
		this.idActuator = idActuator;
		this.idDevice = idDevice;
		this.actuatorType = ActuatorType.valueOf(actuatorType);
		this.removed = removed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getIdActuator() {
		return idActuator;
	}

	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}

	public Integer getIdDevice() {
		return idDevice;
	}

	public void setIdDevice(Integer idDevice) {
		this.idDevice = idDevice;
	}

	public ActuatorType getActuatorType() {
		return actuatorType;
	}

	public void setActuatorType(ActuatorType actuatorType) {
		this.actuatorType = actuatorType;
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
		result = prime * result + ((actuatorType == null) ? 0 : actuatorType.hashCode());
		result = prime * result + ((idActuator == null) ? 0 : idActuator.hashCode());
		result = prime * result + ((idDevice == null) ? 0 : idDevice.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((removed == null) ? 0 : removed.hashCode());
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
		Actuator other = (Actuator) obj;
		if (actuatorType != other.actuatorType)
			return false;
		if (idActuator == null) {
			if (other.idActuator != null)
				return false;
		} else if (!idActuator.equals(other.idActuator))
			return false;
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
		return true;
	}

	public boolean equalsWithNoIdConsidered(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actuator other = (Actuator) obj;
		if (actuatorType != other.actuatorType)
			return false;
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
		return true;
	}

	@Override
	public String toString() {
		return "Actuator [name=" + name + ", idActuator=" + idActuator + ", idDevice=" + idDevice + ", actuatorType="
				+ actuatorType + ", removed=" + removed + "]";
	}

}
