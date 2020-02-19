/**
 * HubConnect Remote Client for SmartThings
 *
 * Copyright 2019 Steve White, Retail Media Concepts LLC.
 *
 * HubConnect for Hubitat is a software package created and licensed by Retail Media Concepts LLC.
 * HubConnect, along with associated elements, including but not limited to online and/or electronic documentation are
 * protected by international laws and treaties governing intellectual property rights.
 *
 * This software has been licensed to you. All rights are reserved. You may use and/or modify the software.
 * You may not sublicense or distribute this software or any modifications to third parties in any way.
 *
 * By downloading, installing, and/or executing this software you hereby agree to the terms and conditions set forth in the HubConnect license agreement.
 * <http://irisusers.com/hubitat/hubconnect/HubConnect_License_Agreement.html>
 *
 * Hubitat is the trademark and intellectual property of Hubitat, Inc. Retail Media Concepts LLC has no formal or informal affiliations or relationships with Hubitat.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License Agreement
 * for the specific language governing permissions and limitations under the License.
 *
 */
import groovy.transform.Field
import groovy.json.JsonOutput
include 'asynchttp_v1'
definition(
	name: "HubConnect Remote Client",
	namespace: "shackrat",
	author: "Steve White",
	description: "Synchronizes devices and events across hubs..",
	category: "My Apps",
	iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
	iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
	iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png"
)


// Preferences pages
preferences
{
	page(name: "mainPage")
	page(name: "connectPage")
	page(name: "devicePage")
	page(name: "sensorsPage")
	page(name: "customDevicePage")
    page(name: "shmConfigPage")
	page(name: "dynamicDevicePage")
    page(name: "upgradePage")
}


// Map containing driver and attribute definitions for each device class
@Field static NATIVE_DEVICES =
[
	"arlocamera":		[driver: "Arlo Camera", selector: "arloProCameras", capability: "device.ArloProCamera", prefGroup: "smartthings", synthetic: false, attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arloqcamera":		[driver: "Arlo Camera", selector: "arloQCameras", capability: "device.ArloQCamera", prefGroup: "smartthings", synthetic: false, attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arlogocamera":		[driver: "Arlo Camera", selector: "arloGoCameras", capability: "device.ArloGoCamera", prefGroup: "smartthings", synthetic: false, attr: ["switch", "motion", "sound", "rssi", "battery"]],
	"arrival":			[driver: "Arrival Sensor", selector: "smartThingsArrival", capability: "presenceSensor", prefGroup: "other", synthetic: false, attr: ["presence", "battery", "tone"]],
	"audioVolume":		[driver: "AVR", selector: "audioVolume", capability: "audioVolume", prefGroup: "audio", synthetic: false, attr: ["switch", "mediaInputSource", "mute", "volume"]],
	"bulb":				[driver: "Bulb", selector: "genericBulbs", capability: "changeLevel", prefGroup: "switches", synthetic: false, attr: ["switch", "level"]],
	"button":			[driver: "Button", selector: "genericButtons", capability: "Button", prefGroup: "other", synthetic: false, attr: ["numberOfButtons", "pushed", "held", "doubleTapped", "button", "temperature", "battery"]],
	"contact":			[driver: "Contact Sensor", selector: "genericContacts", capability: "contactSensor", prefGroup: "sensors", synthetic: false, attr: ["contact", "temperature", "battery"]],
	"dimmer":			[driver: "Dimmer", selector: "genericDimmers", capability: "switchLevel", prefGroup: "switches", synthetic: false, attr: ["switch", "level"]],
	"domemotion":		[driver: "DomeMotion Sensor", selector: "domeMotions", capability: "motionSensor", prefGroup: "sensors", synthetic: false, attr: ["motion", "temperature", "illuminance", "battery"]],
	"energy":			[driver: "Energy Meter", selector: "energyMeters", capability: "energyMeter", prefGroup: "switches", synthetic: false, attr: ["energy"]],
	"energyplug":		[driver: "DomeAeon Plug", selector: "energyPlugs", capability: "energyMeter", prefGroup: "switches", synthetic: false, attr: ["switch", "power", "voltage", "current", "energy", "acceleration"]],
	"fancontrol":		[driver: "Fan Controller", selector: "fanControl", capability: "fanControl", prefGroup: "switches", synthetic: false, attr: ["speed"]],
	"fanspeed":			[driver: "FanSpeed Controller", selector: "fanSpeedControl", capability: "fanControl", prefGroup: "switches", synthetic: false, attr: ["speed"]],
	"garagedoor":		[driver: "Garage Door", selector: "garageDoors", capability: "garageDoorControl", prefGroup: "other", synthetic: false, attr: ["door", "contact"]],
	"gvomnisensor":		[driver: "GvOmniSensor", selector: "gvOmniSensor", capability: "waterSensor", prefGroup: "sensors", synthetic: false, attr: ["acceleration", "carbonDioxide", "carbonMonoxide", "contact", "humidity", "illuminance", "motion", "presence", "smoke", "temperature", "variable", "water"]],
	"irissmartplug":	[driver: "Iris SmartPlug", selector: "smartPlugs", capability: "device.IrisSmartPlug", prefGroup: "shackrat", synthetic: false, attr: ["switch", "power", "voltage", "ACFrequency"]],
	"irisv3motion":		[driver: "IrisV3 Motion Sensor", selector: "irisV3Motions", capability: "motionSensor", prefGroup: "sensors", synthetic: false, attr: ["motion", "temperature", "humidity", "battery"]],
	"keypad":			[driver: "Keypad", selector: "genericKeypads", capability: "securityKeypad", prefGroup: "safety", synthetic: false, attr: ["motion", "temperature", "battery", "tamper", "alarm", "lastCodeName"]],
	"lock":				[driver: "Lock", selector: "genericLocks", capability: "lock", prefGroup: "safety", synthetic: false, attr: ["lock", "lockCodes", "lastCodeName", "codeChanged", "codeLength", "maxCodes", "battery"]],
	"mobileApp":		[driver: "Mobile App", selector: "mobileApp", capability: "notification", prefGroup: "other", synthetic: false, attr: ["presence", "notificationText"]],
	"moisture":			[driver: "Moisture Sensor", selector: "genericMoistures", capability: "waterSensor", prefGroup: "safety", synthetic: false, attr: ["water", "temperature", "battery"]],
	"motion":			[driver: "Motion Sensor", selector: "genericMotions", capability: "motionSensor", prefGroup: "sensors", synthetic: false, attr: ["motion", "temperature", "battery"]],
	"multipurpose":		[driver: "Multipurpose Sensor", selector: "genericMultipurposes", capability: "accelerationSensor", prefGroup: "sensors", synthetic: false, attr: ["contact", "temperature", "battery", "acceleration", "threeAxis"]],
	"netatmowxbase":	[driver: "Netatmo Basestation", selector: "netatmoWxBasetations", capability: "relativeHumidityMeasurement", prefGroup: "netatmowx", synthetic: true, attr: ["temperature", "humidity", "pressure", "carbonDioxide", "soundPressureLevel", "sound", "min_temp", "max_temp", "temp_trend", "pressure_trend"]],
	"netatmowxmodule":	[driver: "Netatmo Additional Module", selector: "netatmoWxModule", capability: "relativeHumidityMeasurement", prefGroup: "netatmowx", synthetic: true, attr: ["temperature", "humidity", "carbonDioxide", "min_temp", "max_temp", "temp_trend", "battery"]],
	"netatmowxoutdoor":	[driver: "Netatmo Outdoor Module", selector: "netatmoWxOutdoor", capability: "relativeHumidityMeasurement", prefGroup: "netatmowx", synthetic: true, attr: ["temperature", "humidity", "min_temp", "max_temp", "temp_trend", "battery"]],
	"netatmowxrain":	[driver: "Netatmo Rain", selector: "netatmoWxRain", capability: "sensor", prefGroup: "netatmowx", synthetic: true, attr: ["rain", "rainSumHour", "rainSumDay", "units", "battery"]],
	"netatmowxwind":	[driver: "Netatmo Wind", selector: "netatmoWxWind", capability: "sensor", prefGroup: "netatmowx", synthetic: true, attr: ["WindStrength", "WindAngle", "GustStrength", "GustAngle", "max_wind_str", "date_max_wind_str", "units", "battery"]],
	"omnipurpose":		[driver: "Omnipurpose Sensor", selector: "genericOmnipurposes", capability: "relativeHumidityMeasurement", prefGroup: "sensors", synthetic: false, attr: ["motion", "temperature", "humidity", "illuminance", "ultravioletIndex", "tamper", "battery"]],
	"pocketsocket":		[driver: "Pocket Socket", selector: "pocketSockets", capability: "switch", prefGroup: "switches", synthetic: false, attr: ["switch", "power"]],
	"power":			[driver: "Power Meter", selector: "powerMeters", capability: "powerMeter", prefGroup: "switches", synthetic: false, attr: ["power"]],
	"presence":			[driver: "Presence Sensor", selector: "genericPresences", capability: "presenceSensor", prefGroup: "other", synthetic: false, attr: ["presence", "battery"]],
	"ringdoorbell":		[driver: "Ring Doorbell", selector: "ringDoorbellPros", capability: "device.RingDoorbellPro", prefGroup: "smartthings", synthetic: false, attr: ["numberOfButtons", "pushed", "motion"]],
	"rgbbulb":			[driver: "RGB Bulb", selector: "genericRGBs", capability: "colorControl", prefGroup: "switches", synthetic: false, attr: ["switch", "level", "hue", "saturation", "RGB", "color", "colorMode", "colorTemperature"]],
	"rgbwbulb":			[driver: "RGBW Bulb", selector: "genericRGBW", capability: "colorMode", prefGroup: "switches", synthetic: false, attr: ["switch", "level", "hue", "saturation", "RGB(w)", "color", "colorMode", "colorTemperature"]],
	"shock":			[driver: "Shock Sensor", selector: "genericShocks", capability: "shockSensor", prefGroup: "sensors", synthetic: false, attr: ["shock", "battery"]],
	"siren":			[driver: "Siren", selector: "genericSirens", capability: "alarm", prefGroup: "safety", synthetic: false, attr: ["switch", "alarm", "battery"]],
	"smartsmoke":		[driver: "Smart SmokeCO", selector: "smartSmokeCO", capability: "device.HaloSmokeAlarm", prefGroup: "safety", synthetic: false, attr: ["smoke", "carbonMonoxide", "battery", "temperature", "humidity", "switch", "level", "hue", "saturation", "pressure"]],
	"smoke":			[driver: "SmokeCO", selector: "genericSmokeCO", capability: "smokeDetector", prefGroup: "safety", synthetic: false, attr: ["smoke", "carbonMonoxide", "battery"]],
	"speaker":			[driver: "Speaker", selector: "genericSpeakers", capability: "musicPlayer", prefGroup: "audio", synthetic: false, attr: ["level", "mute", "volume", "status", "trackData", "trackDescription"]],
	"speechSynthesis":	[driver: "SpeechSynthesis", selector: "speechSynth", capability: "speechSynthesis", prefGroup: "other", synthetic: false, attr: ["mute", "version", "volume"]],
	"switch":			[driver: "Switch", selector: "genericSwitches", capability: "switch", prefGroup: "switches", synthetic: false, attr: ["switch"]],
	"thermostat":		[driver: "Thermostat", selector: "genericThermostats", capability: "thermostat", prefGroup: "other", synthetic: false, attr: ["coolingSetpoint", "heatingSetpoint", "schedule", "supportedThermostatFanModes", "supportedThermostatModes", "temperature", "thermostatFanMode", "thermostatMode", "thermostatOperatingState", "thermostatSetpoint"]],
	"windowshade":		[driver: "Window Shade", selector: "windowShades", capability: "windowShade", prefGroup: "other", synthetic: false, attr: ["switch", "position", "windowShade"]],
	"valve":			[driver: "Valve", selector: "genericValves", capability: "valve", prefGroup: "other", synthetic: false, attr: ["valve"]],
	"zwaverepeater":	[driver: "Iris Z-Wave Repeater", selector: "zwaveRepeaters", capability: "device.IrisZ-WaveRepeater", prefGroup: "shackrat", synthetic: false, attr: ["status", "lastRefresh", "deviceMSR", "lastMsgRcvd"]]
]


// Mapping to receive events - Note: ST can only support 4 path parts!
mappings
{
	// Client mappings
    path("/event/:deviceId/:deviceCommand/:commandParams")
	{
		action: [GET: "remoteDeviceCommand"]
	}
    path("/device/:deviceId/sync/:type")
	{
		action: [GET: "getDeviceSync"]
	}
    path("/modes/get")
	{
		action: [GET: "getAllModes"]
	}
    path("/modes/set/:name")
	{
		action: [GET: "serverModeChangeEvent"]
	}
    path("/hsm/get")
	{
		action: [GET: "getAllHSMStates"]
	}
    path("/hsm/set/:name")
	{
		action: [GET: "hsmReceiveEvent"]
	}
	path("/system/setCommStatus/:status")
	{
		action: [GET: "setCommStatus"]
	}
	path("/system/drivers/save")
	{
		action: [POST: "saveCustomDrivers"]
	}
    path("/system/versions/get")
	{
		action: [GET: "getVersions"]
	}
    path("/system/initialize")
	{
		action: [GET: "remoteInitialize"]
	}
    path("/system/update")
	{
		action: [GET: "remoteUpdate"]
	}
    path("/system/tsreport/get")
	{
		action: [GET: "getTSReport"]
	}

	// Server mappings
    path("/devices/save")
	{
		action: [POST: "saveDevices"]
	}
    path("/device/:deviceId/event/:event")
	{
		action: [GET: "deviceEvent"]
	}
}


/*
	getDeviceSync

	Purpose: Retrieves the physical device details and returns them to the controller (main) hub.

	URL Format: GET /device/:deviceId/sync/:type

	Notes: Called by HTTP request from controller hub.
*/
def getDeviceSync()
{
	log.info "Received device update request from server: [${params.deviceId}, type ${params.type}]"

	def device = getDevice(params)
	if (device)
	{
		def currentAttributes = getAttributeMap(device, params.type)
		def label = device.label ?: device.name
		jsonResponse([status: "success", name: "${device.name}", label: "${label}", currentValues: currentAttributes])
	}
}


/*
	getDevice

	Purpose: Helper function to retreive a device from all groups of devices.
*/
def getDevice(params)
{
	def foundDevice = null

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		if (foundDevice != null) return
		foundDevice = settings."${device.selector}"?.find{it.id == params.deviceId}
	}

	// Custom devices drivers
	if (foundDevice == null)
	{
		state.customDrivers?.each
		{
	 	  groupname, device ->
			if (foundDevice != null) return
			foundDevice = settings."custom_${groupname}".find{it.id == params.deviceId}
		}
	}
	return foundDevice
}


/*
	remoteDeviceCommand

	Purpose: Event handler for remote hub device events.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: Called from HTTP request from server (remote) hub.
*/
def remoteDeviceCommand()
{
	def commandParams = params.commandParams != "null" ? parseJson(URLDecoder.decode(params.commandParams)) : []

	// Get the device
	def device = getDevice(params)
	if (device == null)
	{
		log.error "Could not locate a device with an id of ${params.deviceId}"
		return jsonResponse([status: "error"])
	}

	if (enableDebug) log.info "Received command from server: [\"${device.label ?: device.name}\": ${params.deviceCommand}]"

	def deviceCommand = params.deviceCommand

	// Fix for broken button ST drivers
	if (deviceCommand == "push" && !device.hasCommand("push"))
	{
		deviceCommand = "push${commandParams[0]}"
		commandParams = []
	}

	// Make sure the physical device supports the command
	if (!device.hasCommand(deviceCommand))
	{
		log.error "The device [${device.label ?: device.name}] does not support the command ${params.deviceCommand}."
		return jsonResponse([status: "error"])
	}

	// Execute the command
	device."${deviceCommand}"(*commandParams)

	jsonResponse([status: "success"])
}


/*
	serverModeChangeEvent

	Purpose: Event handler for server (controller) mode change events.

	URL Format: (GET) /modes/:name

	Notes: Called from HTTP request from controller hub.
*/
def serverModeChangeEvent()
{
    def modeName = params?.name ? URLDecoder.decode(params?.name) : ""
	if (enableDebug) log.debug "Received mode event from server: ${modeName}"

    def modeExists = location.modes?.find{it.name == modeName}
    if (modeExists)
	{
        setLocationMode(modeName)
        jsonResponse([status: "complete"])
    }
	else
	{
		jsonResponse([status: "error"])
    }
}


/*
	hsmReceiveEvent

	Purpose: Event handler for server (controller) HSM status change events.

	URL Format: (GET) /hsm/set/:name

	Notes: Called from HTTP request from server hub.
			SMH Modes: away, stay, off
*/
def hsmReceiveEvent()
{
    def hsmState = params?.name ? URLDecoder.decode(params?.name) : ""

    def hsmToSHM =
	[
		armAway:	settings?.armAway,
		armHome:	settings?.armHome,
		armNight:	settings?.armNight,
		disarm:		"off"
	]

    if (hsmToSHM.find{it.key == hsmState})
	{
		def shmState = hsmToSHM?."${hsmState}"
		if (shmState != null)
		{
			if (enableDebug) log.debug "Received HSM/SHM event from server: ${hsmState}, setting SHM state to ${shmState}"
			sendLocationEvent(name: "alarmSystemStatus", value: shmState)
			jsonResponse([status: "complete"])
		}
		else
		{
			if (enableDebug) log.debug "HSM/SHM event error ${hsmState} SHM has not been configured for this alarm state."
			jsonResponse([status: "error"])
		}
	}
	else
	{
		log.error "Received HSM event from server: ${hsmState} does not exist!"
		jsonResponse([status: "error"])
    }
}


/*
	subscribeLocalEvents

	Purpose: Subscribes to all device events for all attribute returned by getSupportedAttributes()

	Notes: 	Thank god this isn't SmartThings, or this would time out after about 10 subscriptions!

*/
def subscribeLocalEvents()
{
	log.info "Subscribing to events.."
	unsubscribe()

	NATIVE_DEVICES.each
	{
	  groupname, device ->
		def selectedDevices = settings."${device.selector}"
		if (selectedDevices?.size()) getSupportedAttributes(groupname).each { subscribe(selectedDevices, it, groupname != "button" ? realtimeEventHandler : buttonEventTranslator) }
	}

	// Special handling for Smart Plugs & Power Meters - Kinda Kludgy
	if (!sp_EnablePower && smartPlugs?.size()) unsubscribe(smartPlugs, "power", realtimeEventHandler)
	if (!sp_EnableVolts && smartPlugs?.size()) unsubscribe(smartPlugs, "voltage", realtimeEventHandler)
	if (!pm_EnableVolts && powerMeters?.size()) unsubscribe(powerMeters, "voltage", realtimeEventHandler)

	// Custom defined drivers
	state.customDrivers?.each
	{
	  groupname, driver ->
		if (settings."custom_${groupname}"?.size()) getSupportedAttributes(groupname).each { subscribe(settings."custom_${groupname}", it, realtimeEventHandler) }
	}
}


/*
	buttonEventTranslator

	Purpose: Translates SmartThings button events into Hubitat button events.
*/
def buttonEventTranslator(evt)
{
	def data = parseJson(evt.data)
    return realtimeEventHandler([name: evt.value, value: data.buttonNumber, unit: "", isStateChange: true, data: "", deviceId: evt.deviceId, device: evt.device])
}


/*
	realtimeEventHandler

	Purpose: Event handler for all local device events.

	URL Format: /device/localDeviceId/event/name/value/unit

	Notes: Handles everything from this hub!
*/
def realtimeEventHandler(evt)
{
	if (state.commDisabled) return

	def event =
	[
		name:			evt.name,
		value:			evt.value,
		unit:			evt.unit,
		displayName:	evt.device.label ?: evt.device.name,
		data:			evt.data
	]

	def data = URLEncoder.encode(JsonOutput.toJson(event), "UTF-8")

	if (enableDebug) log.debug "Sending event to server: ${evt.device?.label ?: evt.device?.name} [${evt.name}: ${evt.value} ${evt.unit}]"
	sendGetCommand("/device/${evt.deviceId}/event/${data}")
}


/*
	getAttributeMap

	Purpose: Returns a map of current attribute values for (device) with the device class (deviceType).

	Notes: Calls getSupportedAttributes() to obtain list of attributes.
*/
def getAttributeMap(device, deviceClass)
{
	def deviceAttributes = getSupportedAttributes(deviceClass)
	def currentAttributes = []
	deviceAttributes.each
	{
		if (device.supportedAttributes.find{attr -> attr.toString() == it})  // Filter only attributes the device supports
			currentAttributes << [name: "${it}", value: device.currentValue("${it}"), unit: it == "temperature" ? "°"+getTemperatureScale() : it == "power" ? "W" :  it == "voltage" ? "V" : ""]
	}
	return currentAttributes
}


/*
	getSupportedAttributes

	Purpose: Returns a list of supported attribute values for the device class (deviceType).

	Notes: Called from getAttributeMap().
*/
def getSupportedAttributes(deviceClass)
{
	if (NATIVE_DEVICES.find{it.key == deviceClass}) return NATIVE_DEVICES[deviceClass].attr
	if (state.customDrivers.find{it.key == deviceClass}) return state.customDrivers[deviceClass].attr
	return null
}


/*
	realtimeModeChangeHandler

	URL Format: GET /modes/set/modeName

	Purpose: Event handler for mode change events on the controller hub (this one).
*/
def realtimeModeChangeHandler(evt)
{
	if (!pushModes) return

	def newMode = evt.value
	if (enableDebug) log.debug "Sending mode change event to server: ${newMode}"
	sendGetCommand("/modes/set/${URLEncoder.encode(newMode)}")
}


/*
	realtimeHSMChangeHandler

	URL Format: GET /hsm/set/hsmStateName

	Purpose: Event handler for HSM state change events on the controller hub (this one).
*/
def realtimeHSMChangeHandler(evt)
{
	if (!pushHSM) return
	def newState = evt.value

    def hsmToSHM =
	[
		armAway:	settings?.armAway,
		armHome:	settings?.armHome,
		armNight:	settings?.armNight,
		disarm:		"off"
	]

	def hsmState = hsmToSHM.find{it.value == newState}?.key
    if (hsmState)
	{
		if (enableDebug) log.debug "Sending SHM to HSM state change event to server: ${newState} to ${hsmState}"
		sendGetCommand("/hsm/set/${URLEncoder.encode(hsmState)}")
	}
    else if (enableDebug) log.debug "Error sending SHM to HSM state change to server: ${hsmState} is not mapped to ${newState}."
}


/*
	saveDevicesToServer

	Purpose: Sends all of the devices selected (& current attribute values) from this hub to the controller hub.

	URL Format: POST /devices/save

	Notes: Makes a single POST request for each group of devices.
*/
def saveDevicesToServer()
{
	if (state.saveDevices == false) return

	// Fetch all devices and attributes for each device group and send them to the master.
	List idList = []
    def devices = []
	NATIVE_DEVICES.each
	{
	  groupname, device ->

		devices = []
		settings."${device.selector}".each
		{
			devices << [id: it.id, label: it.label ?: it.name, attr: getAttributeMap(it, groupname)]
			idList << it.id
		}
		if (devices != [])
		{
			if (enableDebug) log.info "Sending devices to server: ${groupname} - ${devices}"
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: devices])
		}
	}

	// Custom defined device drivers
	state.customDrivers.each
	{
	  groupname, driver ->

		devices = []
		settings?."custom_${groupname}"?.each
		{
			devices << [id: it.id, label: it.label ?: it.name, attr: getAttributeMap(it, groupname)]
			idList << it.id
		}
		if (devices != [])
		{
			if (enableDebug) log.info "Sending custom devices to remote: ${groupname} - ${devices}"
			sendPostCommand("/devices/save", [deviceclass: groupname, devices: devices])
		}
	}
	if (cleanupDevices) sendPostCommand("/devices/save", [cleanupDevices: idList])
	state.saveDevices = false
}


/*
	sendDeviceEvent

	Purpose: Send an event to a client device.

	URL format: GET /event/:deviceId/:deviceCommand/:commandParams

	Notes: CALLED FROM CHILD DEVICE
*/
def sendDeviceEvent(deviceId, deviceCommand, List commandParams=[])
{
	if (state.commDisabled) return

	def dniParts = deviceId.split(":")
	def paramsEncoded = commandParams ? URLEncoder.encode(new groovy.json.JsonBuilder(commandParams).toString()) : null

	sendGetCommand("/event/${dniParts[1]}/${deviceCommand}/${paramsEncoded}")
}


/*
	deviceEvent

	Purpose: Handler for events received from physical devices on remote hubs.

	URL Format: (GET) /device/:deviceId/event/:event
*/
def deviceEvent()
{
	def eventraw = params.event ? URLDecoder.decode(params.event) : null
	if (eventraw == null) return

	def event = parseJson(new String(eventraw))
	def data = event?.data ?: ""
	def unit = event?.unit ?: ""

	def childDevice = getChildDevices()?.find { it.deviceNetworkId == "${serverIP}:${params.deviceId}"}
	if (childDevice)
	{
		if (enableDebug) log.debug "Received event from Server/${childDevice.label}: [${event.name}, ${event.value} ${unit}, isStateChange: ${event.isStateChange}]"
		childDevice.sendEvent([name: event.name, value: event.value, unit: unit, descriptionText: "${childDevice.displayName} ${event.name} is ${event.value} ${unit}", isStateChange: event.isStateChange, data: data])
		return jsonResponse([status: "complete"])
	}
	else if (enableDebug) log.warn "Ignoring Received event from Server: Device Not Found!"

	return jsonResponse([status: "error"])
}


/*
	saveDevices

	Purpose: Save linked devices as received from the remote client hub.

	URL Format: (POST) /devices/save

	Notes: 	Since this is SmartThings, lets hope it does not time out after creating three devices!
*/
def saveDevices()
{
	// Device cleanup?
	if (request?.JSON?.cleanupDevices != null)
	{
		childDevices.each
		{
		  child ->
			if (child.deviceNetworkId != "serverhub-${serverIP}" && request.JSON.cleanupDevices.find{"${serverIP}:${it}" == child.deviceNetworkId} == null)
			{
				if (enableDebug) log.info "Deleting device ${child.label} as it is no longer shared with this hub."
				deleteChildDevice(child.deviceNetworkId)
			}
		}
	}

	// Find the device class
	else if (!request?.JSON?.deviceclass || !request?.JSON?.devices)
	{
		return jsonResponse([status: "error"])
	}

	if (NATIVE_DEVICES.find {it.key == request.JSON.deviceclass})
	{
		// Create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "HubConnect ${NATIVE_DEVICES[request.JSON.deviceclass].driver}") }
	}
	else if (state.customDrivers.find {it.key == request.JSON.deviceclass})
	{
		// Get the custom device type and create the devices
		request.JSON.devices.each { createLinkedChildDevice(it, "${state.customDrivers[request.JSON.deviceclass].driver}") }
	}

	jsonResponse([status: "complete"])
}


/*
	createLinkedChildDevice

	Purpose: Helper function to create child devices.

	Notes: 	Called from saveDevices()
*/
private createLinkedChildDevice(dev, driverType)
{
	def childDevice = getChildDevices()?.find{it.deviceNetworkId == "${serverIP}:${dev.id}"}
	if (childDevice)
	{
		// Device exists
		if (enableDebug) log.trace "${driverType} ${dev.label} exists... Skipping creation.."
        return
	}
	else
	{
		if (enableDebug) log.trace "Creating Device ${driverType} - ${dev.label}... ${serverIP}:${dev.id}..."
		try
		{
			childDevice = addChildDevice("shackrat", driverType, "${serverIP}:${dev.id}", null, [name: dev.label, label: dev.label])
		}
		catch (errorException)
		{
			log.error "... Uunable to create device ${dev.label}: ${errorException}."
			childDevice = null
		}
	}

	// Set the value of the primary attributes
	if (childDevice)
	{
		dev.attr.each
		{
	 	 attribute ->
			childDevice.sendEvent([name: attribute.name, value: attribute.value, unit: attribute.unit])
		}
	}
}


/*
	syncDevice

	Purpose: Sync device details with the physcial device by requeting an update of all attribute values from the remote hub.

	Notes: CALLED FROM CHILD DEVICE
*/
def syncDevice(deviceId, deviceType)
{
	def dniParts = deviceId.split(":")

	def childDevice = getChildDevices()?.find { it.deviceNetworkId == "${deviceId}"}
	if (childDevice)
	{
		if (enableDebug) log.debug "Requesting device sync from ${clientName}: ${childDevice.label}"

		def data = httpGetWithReturn("/device/${dniParts[1]}/sync/${deviceType}")

		if (data?.status == "success")
		{
			childDevice.setLabel(data.label)

			data?.currentValues.each
			{
			  attr ->
				childDevice.sendEvent([name: attr.name, value: attr.value, unit: attr.unit, descriptionText: "Sync: ${childDevice.displayName} ${attr.name} is ${attr.value} ${attr.unit}", isStateChange: true])
			}
		}
	}
}


/*
	httpGetWithReturn

	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Command is absolute and must begin with '/'
			Returns JSON Map if successful.
*/
def httpGetWithReturn(command)
{
	def serverURI = state.clientURI + command

	def requestParams =
	[
		uri:  serverURI,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]

	try
	{
		httpGet(requestParams)
		{
		  response ->
			if (response?.status == 200)
			{
				return response.data
			}
			else
			{
				log.error "httpGet() request failed with error ${response?.status}"
				return [status: "error", message: "httpGet() request failed with status code ${response?.status}"]
			}
		}
	}
	catch (Exception e)
	{
		log.error "httpGet() failed with error ${e.message}"
		return [status: "error", message: e.message]
	}
}


/*
	sendGetCommand

	Purpose: Helper function to format GET requests with the proper oAuth token.

	Notes: 	Executes async http request and does not return data.
*/
def sendGetCommand(command)
{
	def serverURI = state.clientURI + command

	def requestParams =
	[
		uri:  serverURI,
		requestContentType: "application/json",
		headers:
		[
			Authorization: "Bearer ${state.clientToken}"
		]
	]

	try
	{
		asynchttp_v1.get((enableDebug ? "asyncHTTPHandler" : null), requestParams)
	}
	catch (Exception e)
	{
		log.error "asynchttpGet() failed with error ${e.message}"
	}
}


/*
	asyncHTTPHandler

	Purpose: Helper function to handle returned data from asyncHttpGet.

	Notes: 	Does not return data, only logs errors when debugging is enabled.
*/
def asyncHTTPHandler(response, data)
{
	if (response?.status != 200)
	{
		log.error "httpGet() request failed with error ${response?.status}"
	}
}


/*
	sendPostCommand

	Purpose: Helper function to format POST requests with the proper oAuth token.

	Notes: 	Returns JSON Map if successful.
*/
def sendPostCommand(command, data)
{
	def serverURI = state.clientURI + command + "?access_token=" + state.clientToken

	def requestParams =
    [
		uri:  serverURI,
		requestContentType: "application/json",
		body: data
	]

	try
	{
		httpPostJson(requestParams)
		{
		  response ->
			if (response?.status == 200)
			{
				return response.data
			}
			else
			{
				log.error "httpPost() request failed with error ${response?.status}"
			}
		}
	}
	catch (Exception e)
	{
		log.error "httpPostJson() failed with error ${e.message}"
		return [status: "error", message: e.message]
	}
}


/*
	appHealth

	Purpose: Checks in with the controller hub every 1 minute.

	URL Format: /ping

	Notes: 	Hubs are considered in a warning state after missing 2 pings (2 minutes).
			Hubs are considered offline after missing 5 pings (5 minutes).
			When a hub is offline, the virtual hub device presence state will be set to "not present".
*/
def appHealth()
{
	sendGetCommand("/ping")
}


/*
	setCommStatus

	Purpose: Event handler which disables events communications between hubs.

	Notes: 	This is useful if the coordinator has to be rebooted to prevent HTTP errors on the remote hubs.
*/
def setCommStatus()
{
	log.info "Received setCommStatus command from server: disabled ${params.status}]"
	state.commDisabled = params.status == "false" ? false : true
	jsonResponse([status: "success", switch: params.status == "false" ? "on" : "off"])
}


/*
	getAllModes

	Purpose: Returns a list of all configured modes.

	URL Format: (GET) /modes/get

	Notes: Called from HTTP request from controller hub.
*/
def getAllModes()
{
	jsonResponse(modes: location.modes, active: location.mode)
}


/*
	getAllHSMStates

	Purpose: Returns a map of all Smart Home Monitor states and current state on the remote hub.

	URL Format: GET /hsm/get

	Notes: Called from HTTP request on the remote hub.
*/
def getAllHSMStates()
{
	jsonResponse(hsmSetArm: ["armHome", "armNight", "off"], hsmStatus: location.currentState("alarmSystemStatus")?.value)
}


/*
	saveCustomDrivers

	Purpose: Saves custom drivers defined in server app to this client instance

	Notes: Sent from server hub.
*/
def saveCustomDrivers()
{
	if (request?.JSON?.find{it.key == "customdrivers"})
	{
		// Clean up from deleted drivers
		state.customDrivers.each
		{
	  	  key, driver ->
			if (request?.JSON?.customdrivers?.findAll{it.key == key}.size() == 0)
			{
				if (enableDebug) log.debug "Unsubscribing from events and removing device selector for ${key}"
				unsubscribe(settings."custom_${key}")
				app.removeSetting("custom_${key}")
			}
		}
		state.customDrivers = request.JSON.customdrivers
		jsonResponse([status: "success"])
	}
	else
	{
		jsonResponse([status: "error"])
	}
}


/*
	installed

	Purpose: Standard install function.

	Notes: Doesn't do much.
*/
def installed()
{
	log.info "${app.name} Installed"

	state.saveDevices = false
	state.installedVersion = appVersion

	initialize()
}


/*
	updated

	Purpose: Standard update function.

	Notes: Still doesn't do much.
*/
def updated()
{
	log.info "${app.name} Updated"

	if (state?.customDrivers == null)
	{
		state.customDrivers = [:]
	}

	initialize()
	state.installedVersion = appVersion
}
def remoteUpdate(params) { updated(); jsonResponse([status: "success"]) }


/*
	initialize

	Purpose: Initialize the server instance.

	Notes: Parses the oAuth link into the token and base URL.  A real token exchange would obviate the need for this.
*/
def initialize()
{
	log.info "${app.name} Initialized"
	unschedule()

   	state.commDisabled = false

	if (isConnected)
	{
		saveDevicesToServer()
		subscribeLocalEvents()
		if (pushModes) subscribe(location, "mode", realtimeModeChangeHandler)
		if (pushHSM) subscribe(location, "alarmSystemStatus", realtimeHSMChangeHandler)
		runEvery1Minute("appHealth")
	}

	state.saveDevices = false
}


/*
	remoteInitialize

	Purpose: Allows the server to re-initialize this remote.

	URL Format: (GET) /system/initialize

	Notes: Called from HTTP request from server hub.
*/
def remoteInitialize()
{
	initialize()
	jsonResponse([status: "success"])
}


/*
	jsonResponse

	Purpose: Helper function to render JSON responses
*/
def jsonResponse(respMap)
{
	render contentType: 'application/json', data: JsonOutput.toJson(respMap)
}


/*
	getDevicePageStatus

	Purpose: Helper function to set flags for configured devices.
*/
def getDevicePageStatus()
{
	def status = [:]
	NATIVE_DEVICES.each
	{  groupname, device ->
		status["${device.prefGroup}"] = status["${device.prefGroup}"] != null ?: settings?."${device.selector}"?.size()
	}

	// Custom defined device drivers
	state.customDrivers.each
	{
	  groupname, driver ->
		status["custom"] = status["custom"] != null ?: settings?."custom_${groupname}"?.size()
	}

	status["all"] = status.find{it.value == true} ? true : null
	status
}


/*
	mainPage

	Purpose: Displays the main (landing) page.

	Notes: 	Not very exciting.
*/
def mainPage()
{
	if (isConnected && state.installedVersion != appVersion) return upgradePage()

	dynamicPage(name: "mainPage", uninstall: true, install: true)
	{
		section("-= Main Menu=-")
		{
			href "connectPage", title: "Connect to Server Hub...", description: "", state: isConnected ? "complete" : null
			if (isConnected)
			{
				if (isConnected) href "devicePage", title: "Select devices to synchronize to Server hub...", description: "", state: devicePageStatus.all ? "complete" : null
				href "shmConfigPage", title: "Configure SHM to HSM mapping...", description: "", state: (armAway != null || armHome != null || armNight != null) ? "complete" : null
				input "pushModes", "bool", title: "Push mode changes to Server Hub?", description: "", defaultValue: false
				input "pushHSM", "bool", title: "Send HSM changes to Server Hub?", description: "", defaultValue: false
			}
		}
		section("-= Debug Menu =-")
		{
			input "enableDebug", "bool", title: "Enable debug output?", required: false, defaultValue: false
		}
		section()
		{
			paragraph title: "HubConnect v${appVersion.major}.${appVersion.minor}.${appVersion.build}", "SmartThings Client build ${moduleBuild}\n${appCopyright}"
		}
	}
}


/*
	upgradePage

	Purpose: Displays the splash page to force users to initialize the app after an upgrade.
*/
def upgradePage()
{
	dynamicPage(name: "upgradePage", uninstall: false, install: true)
	{
		section("New Version Detected!")
		{
			paragraph "This HubConnect Remote Client has an upgrade that has been installed... \n\n Please click [Save] to complete the installation."
		}
	}
}


/*
	connectPage

	Purpose: Displays the local & remote oAuth links.

	Notes: 	Really should create a proper token exchange someday.
*/
def connectPage()
{
	if (!state?.accessToken)
	{
		state.accessToken = createAccessToken()
	}

    def responseText = ""
	if (serverKey)
	{
		def accessData
		try
		{
			accessData = parseJson(new String(serverKey.decodeBase64()))
		}
		catch (errorException)
		{
			log.error "Error reading connection key: ${errorException}."
			responseText = "Error: Corrupt or invalid connection key"
			state.connected = false
            accessData = null
		}
        if (accessData && accessData?.token && accessData?.type == "smartthings")
		{
			// Set the coordinator hub details
			state.clientURI = accessData.uri
			state.clientToken = accessData.token
			state.clientType = accessData.type

			// Send our connect string to the coordinator
			def connectKey = new groovy.json.JsonBuilder([uri: apiServerUrl("/api/smartapps/installations/${app.id}"), type: "remote", token: state.accessToken, mac: location.hubs[0].id]).toString().bytes.encodeBase64()
			def response = httpGetWithReturn("/connect/${connectKey}")

			if ("${response.status}" == "success")
			{
				state.connected = true
			}
			else
			{
				state.connected = false
				responseText = "Error: ${response?.message}"
			}
		}
		else if (accessData?.type != "smartthings") responseText = "Error: Connection key is not for this platform"
	}

	// Reset connection data if handshake failed
	if (serverKey == null || disconnectHub || state.connected == false)
	{
		state.clientURI = null
		state.clientToken = null
		state.clientType = null
		state.connected = false
		if (disconnectHub)
		{
			app.updateSetting("serverKey", [type: "string", value: ""])
			app.updateSetting("disconnectHub", [type: "bool", value: false])
		}
	}

	dynamicPage(name: "connectPage", uninstall: false, install: false)
	{
		section("Server Details")
		{
			input "serverIP", "string", title: "Local LAN IP Address of the Server Hub:", required: false, defaultValue: null, submitOnChange: true
			if (serverIP) input "serverKey", "string", title: "Paste the server connection key here:", required: false, defaultValue: null, submitOnChange: true
		}
		section()
		{
			if (state.connected)
			{
				paragraph "Connected!"
				input "disconnectHub", "bool", title: "Disconnect Server Hub...", description: "This will erase the connection key.", required: false, submitOnChange: true
			}
			else paragraph "Not Connected :: ${responseText}", required: true
		}
	}
}


/*
	devicePage

	Purpose: Displays the page where devices are selected to be linked to the controller hub.

	Notes: 	Really could stand to be better organized.
*/
def devicePage()
{
	def totalNativeDevices = 0
	def requiredDrivers = ""
	NATIVE_DEVICES.each
	{devicegroup, device ->
		if (settings."${device.selector}"?.size())
		{
			totalNativeDevices += settings."${device.selector}"?.size()
			requiredDrivers += "\t${device.driver}\n"
		}
	}

	def totalCustomDevices = 0
	state.customDrivers?.each
	{devicegroup, device ->
		totalCustomDevices += settings."${devicegroup}"?.size() ?: 0
	}

	def totalDevices = totalNativeDevices + totalCustomDevices

	dynamicPage(name: "devicePage", uninstall: false, install: false)
	{
		section("Select Devices to Link to Server Hub  (${totalDevices} connected)")
		{
			href "dynamicDevicePage", title: "Sensors", description: "Contact, Motion, Multipurpose, Omnipurpose, Shock, GV Connector", state: devicePageStatus.sensors ? "complete" : null, params: [prefGroup: "sensors", title: "Sensors"]
			href "dynamicDevicePage", title: "Shackrat's Drivers", description: "Iris Smart Plug, Z-Wave Repeaters", state: devicePageStatus.shackrat ? "complete" : null, params: [prefGroup: "shackrat", title: "Shackrat's Drivers"]
			href "dynamicDevicePage", title: "Switches, Dimmers, & Fans", description: "Switch, Dimmer, Bulb, Power Meters", state: devicePageStatus.switches ? "complete" : null, params: [prefGroup: "switches", title: "Switches, Dimmers, & Fans"]
			href "dynamicDevicePage", title: "Safety & Security", description: "Locks, Keypads, Smoke & Carbon Monoxide, Leak, Sirens", state: devicePageStatus.safety ? "complete" : null, params: [prefGroup: "safety", title: "Safety & Security"]
			href "dynamicDevicePage", title: "SmartThings Devices", description: "Arlo Cameras, Ring Doorbells", state: devicePageStatus.smartthings ? "complete" : null, params: [prefGroup: "smartthings", title: "SmartThings-Specific Devices"]
			href "dynamicDevicePage", title: "Audio Devices", description: "Speakers, AV Receivers", state: devicePageStatus.audio ? "complete" : null, params: [prefGroup: "audio", title: "Audio Devices"]
			href "dynamicDevicePage", title: "Netatmo Weather Stations", description: "Netatmo Weather Stations & Devices", state: devicePageStatus.netatmowx ? "complete" : null, params: [prefGroup: "netatmowx", title: "Weather Stations & Devices"]
			href "dynamicDevicePage", title: "Other Devices", description: "Presence, Button, Valves, Garage Doors, Speech Synthesis, Window Shades", state: devicePageStatus.other ? "complete" : null, params: [prefGroup: "other", title: "Other Devices"]
			href "customDevicePage", title: "Custom Devices", description: "Devices with user-defined drivers.", state: devicePageStatus.custom ? "complete" : null
		}
		if (state.saveDevices)
		{
			section()
			{
				paragraph "Changes to remote devices will be saved on exit."
				input "cleanupDevices", "bool", title: "Remove unused devices on the remote hub?", required: false, defaultValue: true
			}
		}
		if (requiredDrivers?.size())
		{
			section("-= Required Drivers =-")
			{
				paragraph "Please make sure the following native drivers are installed on the Coordinator (master) hub before clicking \"Done\": \n${requiredDrivers}"
			}
		}
	}
}


/*
	dynamicDevicePage

	Purpose: Displays a device selection page.
*/
def dynamicDevicePage(params)
{
	state.saveDevices = true

	dynamicPage(name: "dynamicDevicePage", title: params.title, uninstall: false, install: false)
	{
		NATIVE_DEVICES.each
		{
		  groupname, device ->
			if (device.prefGroup == params.prefGroup)
			{
				section("-= Select ${device.driver} Devcies (${settings?."${device.selector}"?.size() ?: "0"} connected) =-")
				{
          def capability = (device.synthetic && (settings."syn_${device.selector}" == null || settings."syn_${device.selector}" != "attribute")) ? "device." + (settings."syn_${device.selector}" == "synthetic" ? "HubConnect" : "") + device.driver.replace(" ", "") : device.capability.contains("device.") ? device.capability : "capability.${device.capability}"
          input "${device.selector}", "${capability}", title: "${device.driver}s ${device.attr}:", required: false, multiple: true, defaultValue: null
          if (device.synthetic) input "syn_${device.selector}", "enum", title: "DeviceMatch Selection Type? ${settings."${device.selector}"?.size() ? " (Changing may affect the availability of previously selected devices)" : ""}", options: [physical: "Device Driver", synthetic: "HubConnect Driver", attribute: "Primary Attribute"], required: false, defaultValue: "physical", submitOnChange: true

					// Customizations
					if (groupname == "irissmartplug")
					{
						input "sp_EnablePower", "bool", title: "Enable power meter reporting?", required: false, defaultValue: true
						input "sp_EnableVolts", "bool", title: "Enable voltage reporting?", required: false, defaultValue: true
					}
					else if (groupname == "power")
					{
						input "pm_EnableVolts", "bool", title: "Enable voltage reporting?", required: false, defaultValue: true
					}
				}
			}
		}
	}
}


/*
	customDevicePage

	Purpose: Displays the page where custom (user-defined) devices are selected to be linked to the controller hub.

	Notes: 	First attempt at remotely defined device definitions.
*/
def customDevicePage()
{
	state.saveDevices = true

	dynamicPage(name: "customDevicePage", uninstall: false, install: false)
	{
		state.customDrivers.each
		{
		  groupname, driver ->
			def customSel = settings."custom_${groupname}"
			section("-= Select ${driver.driver} Devices (${customSel?.size() ?: "0"} connected) =-")
			{
				input "custom_${groupname}", "capability.${driver.selector.substring(driver.selector.lastIndexOf("_") + 1)}", title: "${driver.driver} Devices (${driver.attr}):", required: false, multiple: true, defaultValue: null
			}
		}
	}
}


/*
	shmConfigPage

	Purpose: Configures HSM to SHM Mappings.

	Notes: 	Not very exciting.
*/
def shmConfigPage()
{
	def shmStates = ["away", "stay", "off"]
	dynamicPage(name: "shmConfigPage", uninstall: true, install: true)
	{
		section("-= SHM to HSM Mode Mapping =-")
		{
			input "armAway", "enum", title: "Set HSM to this mode when HSM changes to armAway", options: shmStates, description: "", defaultValue: "away"
			input "armHome", "enum", title: "Set HSM to this mode when HSM changes to armHome", options: shmStates, description: "", defaultValue: "off"
			input "armNight", "enum", title: "Set HSM to this mode when HSM changes to armNight", options: shmStates, description: "", defaultValue: "stay"
		}
	}
}


/*
	getVersions

	URL Format: (GET) /system/versions/get

	Purpose: Returns Remote Client & Active driver versions to server container.
*/
def getVersions()
{
	// Get hub app & drivers
	def remoteDrivers = [:]
	getChildDevices()?.each
	{
	   device ->
		if (remoteDrivers[device.typeName] == null) remoteDrivers[device.typeName] = device.getDriverVersion()
	}
	jsonResponse([apps: [[appName: app.label, appVersion: appVersion]], drivers: remoteDrivers])
}


/*
	getTSReport

	URL Format: (GET) /system/tsreport/get

	Purpose: Returns a technical support report for this remote client.
*/
def getTSReport()
{
	jsonResponse([
		app: [
			appId: app.id,
			appVersion: getAppVersion().toString(),
			installedVersion: state.installedVersion
		],
		prefs: [
			thisClientName: thisClientName,
			serverKey: serverKey,
			pushModes: pushModes,
			pushHSM: pushHSM,
			enableDebug: enableDebug,
		],
		state: [
			clientURI: state?.clientURI,
			connectionType: state.connectionType,
			customDrivers: state.customDrivers,
			commDisabled: state.commDisabled
		],
		devices: [
			incomingDevices: getChildDevices()?.size() - (hubDevice != null ? 1 : 0),
			deviceIdList: "N/A"
		],
		hub: [
			deviceStatus: "N/A",
			connectionType: "http",
			eventSocketStatus:  "N/A",
			hsmStatus:  "N/A",
			modeStatus:  "N/A",
			presence:  "N/A",
			switch:  "N/A",
			version:  "N/A",
			subscribedDevices:  "N/A",
			connectionAttempts:  "N/A",
			refreshSocket:  "N/A",
			refreshHour:  "N/A",
			refreshMinute: "N/A",
			hardwareID: location?.hubs[0]?.getType(),
			firmwareVersion: location?.hubs[0]?.firmwareVersionString,
			localIP: location?.hubs[0]?.getLocalIP()
		]
	])
}
def getIsConnected(){(state?.clientURI?.size() > 0 && state?.clientToken?.size() > 0) ? true : false}
def getAppVersion() {[platform: "SmartThings", major: 1, minor: 6, build: 4]}
def getAppCopyright(){"© 2019 Steve White, Retail Media Concepts LLC"}
