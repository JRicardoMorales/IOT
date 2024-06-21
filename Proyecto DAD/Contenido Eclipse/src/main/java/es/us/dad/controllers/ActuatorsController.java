package es.us.dad.controllers;

import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import es.us.dad.mysql.rest.RestEntityMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Controlador asociado a la entidad Actuator. Realizará todas las operaciones
 * relacionadas con esta entidad a solicitud del Verticle que despliega la API Rest.
 * Esta clase extiende la funcionalidad básica implementada por la clase AbstractController.
 */

public class ActuatorsController extends AbstractController {

	/**
	 * Constructor de la clase donde se indica el tipo de entidad gestionada por la
	 * clase a la clase AbstractController donde se define la funcionalidad básica
	 * de los controladores.
	 */
	
	public ActuatorsController() {
		super(DatabaseEntity.Actuator);
	}

	/**
	 * Método que permite lanzar el Verticle. Este método desplegará el controlador
	 * que luego atenderá las solicitudes de uso realizadas por la API Rest. El canal
	 * al que está asociado este controlador dependerá de la entidad controlada por
	 * cada controlador. En este caso, la entidad es Actuator. Ten en cuenta que en este
	 * caso, el canal de comunicación no es dado por la clase DatabaseEntity, sino a
	 * través de la clase RestEntityMessage. Esto se debe a la necesidad de definir
	 * nombres de canales diferentes para evitar que se superpongan los mensajes
	 * provenientes de la comunicación entre el controlador y la capa de acceso a datos.
	 */
	
	public void start(Promise<Void> startFuture) {

		getVertx().eventBus().consumer(RestEntityMessage.Actuator.getAddress(), message -> {
			DatabaseMessage databaseMessage = gson.fromJson((String) message.body(), DatabaseMessage.class);
			/*
			 * El switch considera todos los mensajes que pueden ser gestionados por este
			 * controlador y los delega al método launchDatabaseOperation de la clase
			 * AbstractController, el cual envía el mensaje original para ser procesado
			 * por el Verticle de acceso a datos. Ten en cuenta que este switch no es
			 * necesario a menos que haya métodos en los que las operaciones sean
			 * diferentes a la descrita anteriormente. Sin embargo, para mayor claridad,
			 * se ha decidido hacer explícitos los valores del enumerado DatabaseMethod
			 * que gestiona cada controlador. Esta implementación asume que el mensaje
			 * proveniente de la API Rest está bien definido para que pueda ser reutilizado
			 * en la solicitud en la capa de acceso a datos.
			 */
			
			switch (databaseMessage.getMethod()) {
			case CreateActuator:
				launchDatabaseOperation(message);
				break;
			case GetActuator:
				launchDatabaseOperation(message);
				break;
			case EditActuator:
				launchDatabaseOperation(message);
				break;
			case DeleteActuator:
				launchDatabaseOperation(message);
				break;
			default:
				
				/*
				 * En caso de que la solicitud no pueda ser manejada por este controlador,
				 * se emitirá un código de error 401 en respuesta al mensaje recibido de la API Rest.
				 */
				
				message.fail(401, "Method not allowed");
			}
		});
		startFuture.complete();
	}

	
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
