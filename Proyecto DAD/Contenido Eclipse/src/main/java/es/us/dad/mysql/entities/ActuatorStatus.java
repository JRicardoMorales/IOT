package es.us.dad.mysql.entities;

/**
 * Esta clase representa los estados asociados con un actuador. Cada vez que el estado
 * de un cierto actuador cambie, se generará una nueva instancia de esta entidad en
 * la base de datos. Los estados en la base de datos nunca se sobrescriben, sino que se
 * generan nuevos con su correspondiente marca de tiempo.
 */

public class ActuatorStatus {

	/**
	 * Clave primaria asociada al estado del actuador. Este identificador es único para
	 * cada valor de un actuador en la base de datos. De esta manera, cada vez que el
	 * estado del actuador se modifique y se almacene en la base de datos, se generará
	 * una nueva tupla con un nuevo identificador.
	 */
	
	private Integer idActuatorState;

	/**
	 * Estado numérico del actuador. Útil para actuadores cuyo estado puede ser
	 * representado numéricamente (ángulo de rotación, velocidad de rotación, etc.).
	 */
	
	private Float status;

	/**
	 * Estado binario del actuador. Útil para actuadores cuyo estado puede ser
	 * representado por un valor lógico (relé encendido o apagado, LED encendido o
	 * apagado, motor en funcionamiento o detenido, etc.).
	 */
	
	private Boolean statusBinary;

	/**
	 * Identificador del actuador al cual se ha aplicado dicho estado. Este identificador
	 * representa de manera única un actuador conectado a un dispositivo.
	 */
	
	private Integer idActuator;

	/**
	 * Marca de tiempo en la que se aplica el estado del actuador. La marca de tiempo está expresada en tiempo Unix,
	 * definido como los milisegundos desde el 1 de enero de 1970.
	 */
	
	private Long timestamp;

	/**
	 * Valor lógico que indica si se ha eliminado el estado del actuador. La eliminación se
	 * realiza marcando este campo como verdadero, por lo que el valor nunca se elimina
	 * permanentemente de la base de datos.
	 */
	
	private Boolean removed;

	public ActuatorStatus() {
		super();
	}

	public ActuatorStatus(Float status, Boolean statusBinary, Integer idActuator, Long timestamp, Boolean removed) {
		super();
		this.status = status;
		this.idActuator = idActuator;
		this.removed = removed;
		this.timestamp = timestamp;
		this.statusBinary = statusBinary;
	}

	public ActuatorStatus(Integer idActuatorState, Float status, Boolean statusBinary, Integer idActuator,
			Long timestamp, Boolean removed) {
		super();
		this.idActuatorState = idActuatorState;
		this.status = status;
		this.idActuator = idActuator;
		this.removed = removed;
		this.timestamp = timestamp;
		this.statusBinary = statusBinary;
	}

	public Float getStatus() {
		return status;
	}

	public void setStatus(Float status) {
		this.status = status;
	}

	public Boolean isStatusBinary() {
		return statusBinary;
	}

	public void setStatusBinary(Boolean statusBinary) {
		this.statusBinary = statusBinary;
	}

	public Integer getIdActuator() {
		return idActuator;
	}

	public void setIdActuator(Integer idActuator) {
		this.idActuator = idActuator;
	}

	public Boolean isRemoved() {
		return removed;
	}

	public void setRemoved(Boolean removed) {
		this.removed = removed;
	}

	public Integer getIdActuatorState() {
		return idActuatorState;
	}

	public void setIdActuatorState(Integer idActuatorState) {
		this.idActuatorState = idActuatorState;
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
		result = prime * result + ((idActuator == null) ? 0 : idActuator.hashCode());
		result = prime * result + ((idActuatorState == null) ? 0 : idActuatorState.hashCode());
		result = prime * result + ((removed == null) ? 0 : removed.hashCode());
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((statusBinary == null) ? 0 : statusBinary.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
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
		ActuatorStatus other = (ActuatorStatus) obj;
		if (idActuator == null) {
			if (other.idActuator != null)
				return false;
		} else if (!idActuator.equals(other.idActuator))
			return false;
		if (idActuatorState == null) {
			if (other.idActuatorState != null)
				return false;
		} else if (!idActuatorState.equals(other.idActuatorState))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusBinary == null) {
			if (other.statusBinary != null)
				return false;
		} else if (!statusBinary.equals(other.statusBinary))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ActuatorState [idActuatorState=" + idActuatorState + ", status=" + status + ", statusBinary="
				+ statusBinary + ", idActuator=" + idActuator + ", timestamp=" + timestamp + ", removed=" + removed
				+ "]";
	}

	public boolean equalsWithNoIdConsidered(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActuatorStatus other = (ActuatorStatus) obj;
		if (idActuator == null) {
			if (other.idActuator != null)
				return false;
		} else if (!idActuator.equals(other.idActuator))
			return false;
		if (removed == null) {
			if (other.removed != null)
				return false;
		} else if (!removed.equals(other.removed))
			return false;
		if (status == null) {
			if (other.status != null)
				return false;
		} else if (!status.equals(other.status))
			return false;
		if (statusBinary == null) {
			if (other.statusBinary != null)
				return false;
		} else if (!statusBinary.equals(other.statusBinary))
			return false;
		if (timestamp == null) {
			if (other.timestamp != null)
				return false;
		} else if (!timestamp.equals(other.timestamp))
			return false;
		return true;
	}

}
