package es.us.dad.controllers;

import java.util.Calendar;

import es.us.dad.mysql.entities.Actuator;
import es.us.dad.mysql.entities.Device;
import es.us.dad.mysql.messages.DatabaseEntity;
import es.us.dad.mysql.messages.DatabaseMessage;
import es.us.dad.mysql.messages.DatabaseMessageType;
import es.us.dad.mysql.messages.DatabaseMethod;
import es.us.dad.mysql.rest.RestEntityMessage;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Controlador asociado con la entidad ActuatorState. Realizará todas las operaciones
 * relacionadas con esta entidad a solicitud del Verticle que despliega la API Rest. Esta
 * clase extiende la funcionalidad básica implementada por la clase AbstractController.
 */

public class ActuatorStatesController extends AbstractController {

	/**
	 * Constructor de la clase donde se indica el tipo de entidad gestionada por la clase
	 * a la clase AbstractController, donde se define la funcionalidad básica de los
	 * controladores.
	 */
	
	public ActuatorStatesController() {
		super(DatabaseEntity.ActuatorStatus);
	}

	/**
	 * Método que permite lanzar el Verticle. Este método desplegará el controlador que luego
	 * atenderá las solicitudes de uso realizadas por la API Rest. El canal al que está
	 * asociado este controlador dependerá de la entidad controlada por cada controlador.
	 * En este caso, la entidad es ActuatorState. Ten en cuenta que en este caso, el canal
	 * de comunicación no es dado por la clase DatabaseEntity, sino a través de la clase
	 * RestEntityMessage. Esto se debe a la necesidad de definir nombres de canales
	 * diferentes para evitar que se superpongan los mensajes provenientes de la
	 * comunicación entre el controlador y la capa de acceso a datos.
	 */
	
	public void start(Promise<Void> startFuture) {

		/*
		 * El switch considera todos los mensajes que pueden ser gestionados por este
		 * controlador y los delega al método launchDatabaseOperation de la clase
		 * AbstractController, el cual envía el mensaje original para ser procesado por
		 * el Verticle de acceso a datos. Ten en cuenta que este switch no es necesario a
		 * menos que haya métodos en los que las operaciones sean diferentes a la descrita
		 * anteriormente. Sin embargo, para mayor claridad, se ha decidido hacer
		 * explícitos los valores del enumerado DatabaseMethod que gestiona cada
		 * controlador. Esta implementación asume que el mensaje proveniente de la API
		 * Rest está bien definido para que pueda ser reutilizado en la solicitud en la
		 * capa de acceso a datos.
		 */
		
		getVertx().eventBus().consumer(RestEntityMessage.ActuatorStatus.getAddress(), message -> {
			DatabaseMessage databaseMessage = gson.fromJson((String) message.body(), DatabaseMessage.class);
			switch (databaseMessage.getMethod()) {
			case CreateActuatorStatus:
				launchDatabaseOperation(message);
				Actuator actuator = databaseMessage.getRequestBodyAs(Actuator.class);
				launchDatabaseOperation(DatabaseEntity.Device,
						new DatabaseMessage(DatabaseMessageType.UPDATE, DatabaseEntity.Device,
								DatabaseMethod.EditDevice, new Device(actuator.getIdDevice(), null, null, null, null,
										null, Calendar.getInstance().getTimeInMillis())));
				break;
			case DeleteActuatorStatus:
				launchDatabaseOperation(message);
				break;
			case GetLastActuatorStatusFromActuatorId:
				launchDatabaseOperation(message);
				break;
			case GetLatestActuatorStatesFromActuatorId:
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
