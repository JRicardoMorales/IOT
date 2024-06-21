package es.us.dad.mysql.messages;

/**
 * Un enumerador que indica el perfil de operación de la base de datos.
 * Identifica el tipo de verbo de dicha operación siguiendo la nomenclatura CRUD.
 */
public enum DatabaseMessageType {
	SELECT, INSERT, UPDATE, DELETE
}
