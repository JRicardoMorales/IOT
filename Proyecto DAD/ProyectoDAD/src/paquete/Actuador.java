package paquete;

import java.util.Objects;

public class Actuador {
	
	//Atributos
	
	private String id;
	private Integer actuador;
	private Long timespam;
	private Double value;
	private String idPlaca;
	
	//Constructor 
	
	public Actuador(String id, Integer actuador, Long timespam, Double value, String idPlaca) {
		this.id = id;
		this.actuador = actuador;
		this.timespam = timespam;
		this.value = value;
		this.idPlaca = idPlaca;
	}
	
	//Getters and Setters
	
	public void setId(String id) {
		this.id = id;
	}

	public void setActuador(Integer actuador) {
		this.actuador = actuador;
	}

	public void setTimespam(Long timespam) {
		this.timespam = timespam;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setIdPlaca(String idPlaca) {
		this.idPlaca = idPlaca;
	}

	
	//Equals and Hashcode
	
	@Override
	public int hashCode() {
		return Objects.hash(actuador, id, idPlaca, timespam, value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Actuador other = (Actuador) obj;
		return Objects.equals(actuador, other.actuador) && Objects.equals(id, other.id)
				&& Objects.equals(idPlaca, other.idPlaca) && Objects.equals(timespam, other.timespam)
				&& Objects.equals(value, other.value);
	}

	
	//To String
	
	@Override
	public String toString() {
		return "Actuador [id=" + id + ", actuador=" + actuador + ", timespam=" + timespam + ", value=" + value
				+ ", idPlaca=" + idPlaca + "]";
	}
	
	
	
	
	
	
	
	
	
	

}
