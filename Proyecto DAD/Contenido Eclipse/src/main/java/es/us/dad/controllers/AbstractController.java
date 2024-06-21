package es.us.dad.controllers;

import java.util.List;

import com.google.gson.Gson;

import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;

/**
 * 
 * Esta clase extiende AbstractVerticle, por lo que permite definir Verticles
 * en Vertx que reutilizan los métodos definidos en la clase abstracta. Como es
 * una clase abstracta, no puede ser instanciada directamente, sino que debe ser extendida
 * por otra clase que, si es necesario, puede ser implementada. Esta clase abstracta
 * nos permite definir los controladores de nuestro proyecto reutilizando los métodos
 * definidos en ella. Recuerda que los controladores se utilizan para realizar operaciones
 * contra el Verticle para acceder a la base de datos a solicitud de un mensaje
 * enviado desde el Verticle que despliega la API Rest. Esta capa es
 * necesaria para procesar solicitudes complejas que implican llamar a más de un
 * método del acceso a datos MySQLVerticle.
 *
 *
 */

public abstract class AbstractController extends AbstractVerticle {

	/**
	 * Enumeración que define el tipo de objeto que será manejado por la
	 * implementación de AbstractController. Se refiere a uno de los tipos
	 * de datos gestionados por el sistema.
	 */
	
	private DatabaseEntity databaseEntity;

	/**
	 * Instancia de la biblioteca encargada de serializar y deserializar los
	 * objetos que son gestionados desde este AbstractController. Está definida como
	 * transitoria para no ser serializada en caso de serializaciones de la
	 * clase AbstractController.
	 */
	
	protected transient Gson gson = new Gson();

	/**
	 * Constructor de la clase abstracta. Debe ser invocado en el constructor de las
	 * diferentes clases que extienden esta clase abstracta llamando a super.

     * @param databaseEntity Tipo de entidad gestionada por el controlador que se define
     * reutilizando esta clase abstracta
	 */
	
	public AbstractController(DatabaseEntity databaseEntity) {
		super();
		this.databaseEntity = databaseEntity;
	}

	/**
	 * Este método permite enviar un mensaje al Verticle encargado de realizar
	 * operaciones contra la base de datos. Esto requerirá una instancia de un
	 * mensaje de Vertx para indicar el canal en el que se publicará el mensaje,
	 * así como el cuerpo de la solicitud. En caso de un fallo en la solicitud (o
	 * en la respuesta a la solicitud), se responderá al mensaje original con el
	 * código de error 100 y el mensaje de la causa de la excepción.

	 * @param message Mensaje donde se almacena la solicitud a realizar al Verticle de
	 * comunicación con la base de datos. El cuerpo de este mensaje debe
	 * ser necesariamente de tipo DatabaseMessage. El canal de
	 * publicación del mensaje será dado por el canal asociado a la entidad
	 * que gestiona la instancia del controlador.
	 */
	
	protected void launchDatabaseOperation(Message<Object> message) {
		DatabaseMessage databaseMessage = gson.fromJson((String) message.body(), DatabaseMessage.class);
		getVertx().eventBus().request(databaseEntity.getAddress(), gson.toJson(databaseMessage), persistenceMessage -> {
			if (persistenceMessage.succeeded()) {
				message.reply(persistenceMessage.result().body());
			} else {
				message.fail(100, persistenceMessage.cause().getLocalizedMessage());
				System.err.println(persistenceMessage.cause());
			}
		});
	}

	/**
	 * Permite la ejecución de un conjunto de DatabaseMessage contra el Verticle
	 * de Acceso a Datos. Estos mensajes se ejecutarán secuencialmente en el mismo
	 * orden en que se pasan en la lista de mensajes.

	 * @param databaseMessages Lista de DatabaseMessage con el contenido de las solicitudes
	 * a realizar al Verticle de conexión a la base de datos.
	 * 
	 * @return Promesa con el resultado de la implementación. Al ser un método asincrónico,
	 * no retornará directamente el resultado de la ejecución de los mensajes, sino que se
	 * hará a través de una promesa a la que el manejador puede suscribirse mediante el
	 * método onComplete de su futuro: promise.future().onComplete(res -> {...});
	 */
	
	public Promise<List<DatabaseMessage>> launchDatabaseOperations(List<DatabaseMessage> databaseMessages) {
		return ControllersUtils.launchDatabaseOperations(databaseMessages, this.getVertx());
	}

	/**
	 * Publica un mensaje en el canal vinculado a la entidad databaseEntity pasada como
	 * parámetro.

	 * @param databaseEntity Entidad involucrada en el mensaje publicado y a cuyo canal
	 * se enviará la solicitud indicada en el segundo parámetro.

	 * @param databaseMessage Mensaje que describe la tarea a ser realizada por el
	 * Verticle de conexión a la base de datos.

	 * @return Promesa con el resultado de la implementación. Al ser un método
	 * asincrónico, no retornará directamente el resultado de la ejecución del mensaje, sino
	 * que se hará a través de una promesa a la que el manejador puede suscribirse mediante
	 * el método onComplete de su futuro: promise.future().onComplete(res -> {...});
	 */
	
	public Promise<DatabaseMessage> launchDatabaseOperation(DatabaseEntity databaseEntity,
			DatabaseMessage databaseMessage) {
		return ControllersUtils.launchDatabaseOperation(databaseEntity, databaseMessage, getVertx());
	}

}
