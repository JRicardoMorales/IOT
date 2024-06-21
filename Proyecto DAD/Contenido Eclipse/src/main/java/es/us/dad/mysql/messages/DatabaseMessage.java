package es.us.dad.mysql.messages;

import com.google.gson.Gson;

/**
 * Una entidad que modela el contenido de los mensajes que se intercambian entre la API
 * Rest y el controlador, así como entre el controlador y la capa de acceso a datos.
 * Estos mensajes permiten implementar la comunicación asincrónica en la que se basa
 * Vertx para comunicar los diferentes Verticles desplegados en la solución. Los mensajes
 * son autosuficientes, por lo que con la información contenida en ellos se puede
 * determinar la operación a realizar, así como los parámetros de entrada y el resultado
 * obtenido una vez aplicado.
 *
 */
public class DatabaseMessage {

	private transient Gson gson = new Gson();

	/**
	 * Un enumerador que indica el perfil de operación de la base de datos. Identifica
	 * el tipo de verbo de dicha operación siguiendo la nomenclatura CRUD.
	 */
	
	private DatabaseMessageType type;

	/**
	 * Clase que define la enumeración correspondiente a las diferentes entidades
	 * almacenadas en la base de datos y gestionadas por el sistema. También se utiliza
	 * para obtener el nombre del canal en el cual los mensajes provenientes del
	 * controlador serán publicados y como receptor el Verticle para la gestión de acceso
	 * a la base de datos.
	 */
	
	private DatabaseEntity entity;

	/**
	 * Este enumerador describe la operación que se debe aplicar.
	 */
	
	private DatabaseMethod method;

	/**
	 * Propiedad que contiene el cuerpo de la solicitud. A veces, este contenido será un
	 * tipo primitivo convertido a String (número, booleano, etc.) o también puede
	 * contener una serialización de un objeto más complejo, que debe ser deserializado
	 * por el destinatario del mensaje para procesar la solicitud.
	 */
	
	private String requestBody;

	/**
	 * Propiedad que contiene el resultado de la operación realizada. A veces, este
	 * contenido será un tipo primitivo convertido a String (número, booleano, etc.) o
	 * también puede contener una serialización de un objeto más complejo, que debe ser
	 * deserializado por el remitente para procesar la respuesta.
	 */
	
	private String responseBody;

	/**
	 * Código de estado del destinatario al procesar el mensaje. Un código 20X indica que
	 * todo ha funcionado correctamente. Un código 30X, 40X o 50X indica un error en el
	 * procesamiento de la operación.
	 */
	
	private Integer statusCode;

	public DatabaseMessage() {
		super();
	}

	public DatabaseMessage(DatabaseMessageType type, DatabaseEntity entity, DatabaseMethod method, String requestBody) {
		super();
		this.type = type;
		this.entity = entity;
		this.method = method;
		this.requestBody = requestBody;
	}

	public DatabaseMessage(DatabaseMessageType type, DatabaseEntity entity, DatabaseMethod method, Object requestBody) {
		super();
		this.type = type;
		this.entity = entity;
		this.method = method;
		this.requestBody = gson.toJson(requestBody);
	}

	public DatabaseMessage(DatabaseMessageType type, DatabaseEntity entity, DatabaseMethod method, String requestBody,
			String responseBody, Integer statusCode) {
		super();
		this.type = type;
		this.entity = entity;
		this.method = method;
		this.requestBody = requestBody;
		this.responseBody = responseBody;
		this.statusCode = statusCode;
	}

	public DatabaseMessage(DatabaseMessageType type, DatabaseEntity entity, DatabaseMethod method, Object requestBody,
			Object responseBody, Integer statusCode) {
		super();
		this.type = type;
		this.entity = entity;
		this.method = method;
		this.requestBody = requestBody != null ? gson.toJson(requestBody) : null;
		this.responseBody = responseBody != null ? gson.toJson(responseBody) : null;
		this.statusCode = statusCode;
	}

	public DatabaseMessageType getType() {
		return type;
	}

	public void setType(DatabaseMessageType type) {
		this.type = type;
	}

	public DatabaseEntity getEntity() {
		return entity;
	}

	public void setEntity(DatabaseEntity entity) {
		this.entity = entity;
	}

	public DatabaseMethod getMethod() {
		return method;
	}

	public void setMethod(DatabaseMethod method) {
		this.method = method;
	}

	public String getRequestBody() {
		return requestBody;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public <E> E getRequestBodyAs(Class<E> type) {
		return requestBody != null ? gson.fromJson(requestBody, type) : null;
	}

	public <E> E getResponseBodyAs(Class<E> type) {
		return responseBody != null ? gson.fromJson(responseBody, type) : null;
	}

	public void setRequestBody(String requestBody) {
		this.requestBody = requestBody;
	}

	public void setResponseBody(String responseBody) {
		this.responseBody = responseBody;
	}

	public <E> void setRequestBody(E requestBody) {
		this.requestBody = requestBody != null ? gson.toJson(requestBody) : null;
	}

	public <E> void setResponseBody(E responseBody) {
		this.responseBody = responseBody != null ? gson.toJson(responseBody) : null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((entity == null) ? 0 : entity.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((requestBody == null) ? 0 : requestBody.hashCode());
		result = prime * result + ((responseBody == null) ? 0 : responseBody.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		DatabaseMessage other = (DatabaseMessage) obj;
		if (entity != other.entity)
			return false;
		if (method != other.method)
			return false;
		if (requestBody == null) {
			if (other.requestBody != null)
				return false;
		} else if (!requestBody.equals(other.requestBody))
			return false;
		if (responseBody == null) {
			if (other.responseBody != null)
				return false;
		} else if (!responseBody.equals(other.responseBody))
			return false;
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DatabaseMessage [type=" + type + ", entity=" + entity + ", method=" + method + ", requestBody="
				+ requestBody + ", responseBody=" + responseBody + ", statusCode=" + statusCode + "]";
	}

}
