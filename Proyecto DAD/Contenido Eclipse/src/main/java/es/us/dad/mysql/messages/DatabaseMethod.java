package es.us.dad.mysql.messages;

/**
 *Este enum describe todas las operaciones que se pueden realizar a nivel de base de
 *datos utilizando el Verticle desplegado para este propósito. En caso de necesitar
 *alguna operación adicional, se debe tener en cuenta tanto esta enumeración, como en
 *el Verticle de acceso a la base de datos y el controlador asociado.
 */

public enum DatabaseMethod {
	// Operaciones de Grupo
	CreateGroup, GetGroup, EditGroup, DeleteGroup, AddDeviceToGroup, GetDevicesFromGroupId,

	// Operaciones de Placa
	CreateDevice, GetDevice, EditDevice, DeleteDevice, GetSensorsFromDeviceId, GetActuatorsFromDeviceId,
	GetSensorsFromDeviceIdAndSensorType, GetActuatorsFromDeviceIdAndActuatorType,

	// Operaciones de Sensor
	CreateSensor, GetSensor, EditSensor, DeleteSensor,

	// Operaciones de Actuador
	CreateActuator, GetActuator, EditActuator, DeleteActuator,

	// Operaciones de Sensor value
	CreateSensorValue, DeleteSensorValue, GetLastSensorValueFromSensorId, GetLatestSensorValuesFromSensorId,

	// Operaciones de Actuator status
	CreateActuatorStatus, DeleteActuatorStatus, GetLastActuatorStatusFromActuatorId,
	GetLatestActuatorStatesFromActuatorId,
}
