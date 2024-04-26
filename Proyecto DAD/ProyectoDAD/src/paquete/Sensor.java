package paquete;

import java.util.Objects;

public class Sensor {
	
	
	//Atributos 
	
	private String id;
	private Integer status;
	private Long timespam;
	private Double value;
	private String idPlaca;
	
	//Constructor
	
	public Sensor(String id, Integer status, Long timespam, Double value, String idPlaca) {
		super();
		this.id = id;
		this.status = status;
		this.timespam = timespam;
		this.value = value;
		this.idPlaca = idPlaca;
	}
	
	//Getters and Setters
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public Long getTimespam() {
		return timespam;
	}
	public void setTimespam(Long timespam) {
		this.timespam = timespam;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public String getIdPlaca() {
		return idPlaca;
	}
	public void setIdPlaca(String idPlaca) {
		this.idPlaca = idPlaca;
	}
	
	//Equals and Hashcode
	
	@Override
	public int hashCode() {
		return Objects.hash(id, idPlaca, status, timespam, value);
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
		return Objects.equals(id, other.id) && Objects.equals(idPlaca, other.idPlaca)
				&& Objects.equals(status, other.status) && Objects.equals(timespam, other.timespam)
				&& Objects.equals(value, other.value);
	}

	
	//To String
	@Override
	public String toString() {
		return "Sensor [id=" + id + ", status=" + status + ", timespam=" + timespam + ", value=" + value + ", idPlaca="
				+ idPlaca + "]";
	}
	
	
	
	
}
