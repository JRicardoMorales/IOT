package paquete;

import java.util.Objects;

public class Placa {

	//Atributos
	
	private String id;
	private String idSensor;
	private String idActuador;
	
	//Constructor
	
	public Placa(String id, String idSensor, String idActuador) {
		this.id = id;
		this.idSensor = idSensor;
		this.idActuador = idActuador;
	}
	
	//Setters and Getters
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getIdSensor() {
		return idSensor;
	}
	public void setIdSensor(String idSensor) {
		this.idSensor = idSensor;
	}
	public String getIdActuador() {
		return idActuador;
	}
	public void setIdActuador(String idActuador) {
		this.idActuador = idActuador;
	}
	
	
	//Equals and Hashcode
	
	@Override
	public int hashCode() {
		return Objects.hash(id, idActuador, idSensor);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Placa other = (Placa) obj;
		return Objects.equals(id, other.id) && Objects.equals(idActuador, other.idActuador)
				&& Objects.equals(idSensor, other.idSensor);
	}
	
	
	//To String 
	
	@Override
	public String toString() {
		return "Placa [id=" + id + ", idSensor=" + idSensor + ", idActuador=" + idActuador + "]";
	}
	
	
}
