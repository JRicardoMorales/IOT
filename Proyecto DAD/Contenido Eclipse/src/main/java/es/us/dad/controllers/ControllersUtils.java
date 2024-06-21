package es.us.dad.controllers;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;

/**
 * 
 * Esta clase ofrece una serie de métodos de utilidad para controladores. Todos los
 * métodos están marcados como estáticos, por lo que no es necesario instanciar la clase
 * para utilizarlos.
 *
 */

public class ControllersUtils {
	protected static transient Gson gson = new Gson();

	/**
	 * 
	 * Ejecuta un conjunto de operaciones contra el Verticle de acceso a datos. Estas
	 * operaciones están definidas por los mensajes que serán enviados a dicho Verticle.
	 * Los mensajes serán enviados secuencialmente, respetando el orden definido en la
	 * lista pasada por parámetro. En caso de que algún mensaje devuelva un error, el resto
	 * de los mensajes seguirán siendo enviados, aunque la respuesta puede ser verificada
	 * en la lista de mensajes devuelta en la Promesa.

	 * @param databaseMessages Lista de mensajes de tipo DatabaseMessage que serán enviados
	 * al controlador de la base de datos para ser ejecutados secuencialmente.
	 
	 * @param vertx Instancia de Vertx que permitirá poner en contexto las llamadas y hacer
 	 * uso del bus de eventos asociado.

	 * @return Promesa con la lista de mensajes procesados. Los mensajes de tipo
	 * DatabaseMessage serán los mismos que los pasados por parámetro pero contendrán los
	 * valores de las respuestas en la propiedad del cuerpo de la respuesta.
	 */
	
	public static Promise<List<DatabaseMessage>> launchDatabaseOperations(List<DatabaseMessage> databaseMessages,
			Vertx vertx) {
		return launchDatabaseOperationsAux(0, databaseMessages, vertx, 0);
	}

	/**
	 * 
	 * Ejecuta un conjunto de operaciones contra el Verticle de acceso a datos. Estas
	 * operaciones están definidas por los mensajes que serán enviados a dicho Verticle.
	 * La única diferencia con respecto al método launchDatabaseOperations es que esta
	 * vez se inserta un retraso de milisegundos entre cada una de las operaciones.
	 * Los mensajes serán enviados secuencialmente, respetando el orden definido en la
	 * lista pasada por parámetro. En caso de que algún mensaje devuelva un error, el
	 * resto de los mensajes seguirán siendo enviados, aunque la respuesta puede ser
	 * verificada en la lista de mensajes devuelta en la Promesa.

	 * @param databaseMessages Lista de mensajes de tipo DatabaseMessage que serán enviados
	 * al controlador de la base de datos para ser ejecutados secuencialmente.
	
	 * @param vertx Instancia de Vertx que permitirá poner en contexto las llamadas y hacer
	 * uso del bus de eventos asociado.
	
	 * @param delay Retraso en milisegundos entre cada envío de mensaje al controlador de la
	 * base de datos.

	 * @return Promesa con la lista de mensajes procesados. Los mensajes de tipo DatabaseMessage
	 * serán los mismos que los pasados por parámetro pero contendrán los valores de las
	 * respuestas en la propiedad del cuerpo de la respuesta.
	 */
	
	public static Promise<List<DatabaseMessage>> launchDatabaseOperations(List<DatabaseMessage> databaseMessages,
			Vertx vertx, int delay) {
		return launchDatabaseOperationsAux(0, databaseMessages, vertx, delay);
	}

	private static Promise<List<DatabaseMessage>> launchDatabaseOperationsAux(int currentMessagePosition,
			List<DatabaseMessage> databaseMessages, Vertx vertx, int delay) {
		Promise<List<DatabaseMessage>> result = Future.factory.promise();
		Promise<DatabaseMessage> promise = Promise.promise();
		vertx.setTimer(delay, function -> {
			Promise<DatabaseMessage> promiseAux = launchDatabaseOperation(
					databaseMessages.get(currentMessagePosition).getEntity(),
					databaseMessages.get(currentMessagePosition), vertx);
			promiseAux.future().onComplete(res -> promise.complete(res.result()));
		});

		promise.future().onComplete(res -> {
			if (currentMessagePosition == databaseMessages.size() - 1) {
				List<DatabaseMessage> resPromise = new ArrayList<DatabaseMessage>();
				resPromise.add(0, res.result());
				result.complete(resPromise);
			} else {
				launchDatabaseOperationsAux(currentMessagePosition + 1, databaseMessages, vertx, delay).future()
						.onComplete(resRec -> {
							List<DatabaseMessage> resPromise = resRec.result();
							resPromise.add(0, res.result());
							result.complete(resPromise);
						});
			}
		});
		return result;

	}

	/**
	 * 
	 * Envía un mensaje al controlador de la base de datos con el contenido especificado
	 * en databaseMessage y al canal definido por databaseEntity.

	 * @param databaseEntity Entidad sobre la cual se realiza la operación indicada y a
	 * través del canal de la cual se enviará el mensaje para su procesamiento.
	
	 * @param databaseMessage Contenido del mensaje que indica la operación a realizar y el
	 * cuerpo de la solicitud con la información necesaria para llevar a cabo dicha operación.
	
	 * @param vertx Instancia de Vertx que permitirá poner en contexto las llamadas y hacer uso del
	 * bus de eventos asociado.
       
     * @return
	 */
	
	public static Promise<DatabaseMessage> launchDatabaseOperation(DatabaseEntity databaseEntity,
			DatabaseMessage databaseMessage, Vertx vertx) {
		Promise<DatabaseMessage> ret = Promise.promise();
		vertx.eventBus().request(databaseEntity.getAddress(), gson.toJson(databaseMessage), persistenceMessage -> {
			if (persistenceMessage.succeeded()) {
				ret.complete(gson.fromJson(persistenceMessage.result().body().toString(), DatabaseMessage.class));
			} else {
				ret.fail(persistenceMessage.cause());
			}
		});
		return ret;
	}
}
