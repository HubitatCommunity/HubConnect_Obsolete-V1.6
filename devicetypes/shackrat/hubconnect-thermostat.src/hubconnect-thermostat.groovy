/*
 *	Copyright 2019 Steve White
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *	use this file except in compliance with the License. You may obtain a copy
 *	of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *	License for the specific language governing permissions and limitations
 *	under the License.
 *
 *
 */
import groovy.json.JsonSlurper
metadata 
{
	definition(name: "HubConnect Thermostat", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.thermostat", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Thermostat.groovy")
	{
		capability "Sensor"
		capability "Thermostat"
		capability "Temperature Measurement"
		capability "Relative Humidity Measurement"
		capability "Refresh"
        
        attribute "supportedThermostatModes", "string"
        attribute "supportedThermostatFanModes", "string"
		attribute "version", "string"

		command "switchMode"
		command "switchFanMode"
		command "lowerHeatingSetpoint"
		command "raiseHeatingSetpoint"
		command "lowerCoolSetpoint"
		command "raiseCoolSetpoint"

		command "sync"
	}

	tiles
	{
		multiAttributeTile(name:"temperature", type:"generic", width:3, height:2, canChangeIcon: true)
		{
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL")
			{
				attributeState("temperature", label:'${currentValue}°', icon: "st.alarm.temperature.normal",
					backgroundColors:
					[
							// Celsius
							[value: 0, color: "#153591"],
							[value: 7, color: "#1e9cbb"],
							[value: 15, color: "#90d2a7"],
							[value: 23, color: "#44b621"],
							[value: 28, color: "#f1d801"],
							[value: 35, color: "#d04e00"],
							[value: 37, color: "#bc2323"],
							// Fahrenheit
							[value: 40, color: "#153591"],
							[value: 44, color: "#1e9cbb"],
							[value: 59, color: "#90d2a7"],
							[value: 74, color: "#44b621"],
							[value: 84, color: "#f1d801"],
							[value: 95, color: "#d04e00"],
							[value: 96, color: "#bc2323"]
					]
				)
			}
		}
		standardTile("mode", "device.thermostatMode", width:2, height:2, inactiveLabel: false, decoration: "flat")
		{
			state "off", action:"switchMode", nextState:"...", icon: "st.thermostat.heating-cooling-off"
			state "heat", action:"switchMode", nextState:"...", icon: "st.thermostat.heat"
			state "cool", action:"switchMode", nextState:"...", icon: "st.thermostat.cool"
			state "auto", action:"switchMode", nextState:"...", icon: "st.thermostat.auto"
			state "emergency heat", action:"switchMode", nextState:"...", icon: "st.thermostat.emergency-heat"
			state "...", label: "Updating...",nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("fanMode", "device.thermostatFanMode", width:2, height:2, inactiveLabel: false, decoration: "flat")
		{
			state "auto", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-auto"
			state "on", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-on"
			state "circulate", action:"switchFanMode", nextState:"...", icon: "st.thermostat.fan-circulate"
			state "...", label: "Updating...", nextState:"...", backgroundColor:"#ffffff"
		}
		standardTile("lowerHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "heatingSetpoint", action:"lowerHeatingSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("heatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "heatingSetpoint", label:'${currentValue}° heat', backgroundColor:"#ffffff"
		}
		standardTile("raiseHeatingSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "heatingSetpoint", action:"raiseHeatingSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("lowerCoolSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "coolingSetpoint", action:"lowerCoolSetpoint", icon:"st.thermostat.thermostat-left"
		}
		valueTile("coolingSetpoint", "device.coolingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "coolingSetpoint", label:'${currentValue}° cool', backgroundColor:"#ffffff"
		}
		standardTile("raiseCoolSetpoint", "device.heatingSetpoint", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "heatingSetpoint", action:"raiseCoolSetpoint", icon:"st.thermostat.thermostat-right"
		}
		standardTile("thermostatOperatingState", "device.thermostatOperatingState", width: 2, height:1, decoration: "flat")
		{
			state "thermostatOperatingState", label:'${currentValue}', backgroundColor:"#ffffff"
		}
		standardTile("refresh", "device.thermostatMode", width:2, height:1, inactiveLabel: false, decoration: "flat")
		{
			state "default", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

		main "temperature"
		details(["temperature", "lowerHeatingSetpoint", "heatingSetpoint", "raiseHeatingSetpoint", "lowerCoolSetpoint",
				"coolingSetpoint", "raiseCoolSetpoint", "mode", "fanMode", "sync", "thermostatOperatingState", "refresh", "version"])
	}
}


/*  
	SmartThings Helper Functions - From Z-Wave Thermostat DTH
    https://raw.githubusercontent.com/SmartThingsCommunity/SmartThingsPublic/master/devicetypes/smartthings/zwave-thermostat.src/zwave-thermostat.groovy
*/
def raiseHeatingSetpoint() { alterSetpoint(true, "heatingSetpoint") }
def lowerHeatingSetpoint() { alterSetpoint(false, "heatingSetpoint") }
def raiseCoolSetpoint() { alterSetpoint(true, "coolingSetpoint") }
def lowerCoolSetpoint() { alterSetpoint(false, "coolingSetpoint") }

// Adjusts nextHeatingSetpoint either .5° C/1° F) if raise true/false
def alterSetpoint(raise, setpoint)
{
	def locationScale = getTemperatureScale()
	def setPoint = getTempInLocalScale(setpoint)

	def targetValue = (setpoint == "heatingSetpoint") ? device.currentValue("heatingSetpoint") : device.currentValue("coolingSetpoint")
	def delta = (locationScale == "F") ? 1 : 0.5
    targetValue = raise ? (targetValue + delta) : (targetValue - delta)

	if (setpoint == "heatingSetpoint")
	{
		setHeatingSetpoint(targetValue)
	}
	else if (setpoint == "coolingSetpoint")
	{
		setCoolingSetpoint(targetValue)
	}

	updateThermostatSetpoint(setpoint, targetValue)
}

// thermostatSetpoint is not displayed by any tile as it can't be predictable calculated due to
// the device's quirkiness but it is defined by the capability so it must be set, set it to the most likely value
def updateThermostatSetpoint(setpoint, value)
{
	def scale = getTemperatureScale()
	def heatingSetpoint = (setpoint == "heatingSetpoint") ? value : getTempInLocalScale("heatingSetpoint")
	def coolingSetpoint = (setpoint == "coolingSetpoint") ? value : getTempInLocalScale("coolingSetpoint")

	def mode = device.currentValue("thermostatMode")
	def thermostatSetpoint = heatingSetpoint    // corresponds to (mode == "heat" || mode == "emergency heat")
	if (mode == "cool")
	{
		thermostatSetpoint = coolingSetpoint
	}
	else if (mode == "auto" || mode == "off")
	{
		// Set thermostatSetpoint to the setpoint closest to the current temperature
		def currentTemperature = getTempInLocalScale("temperature")
		if (currentTemperature > (heatingSetpoint + coolingSetpoint)/2)
        {
			thermostatSetpoint = coolingSetpoint
		}
	}
	sendEvent(name: "thermostatSetpoint", value: thermostatSetpoint, unit: getTemperatureScale())
}

// Get stored temperature from currentState in current local scale
def getTempInLocalScale(state)
{
	def temp = device.currentState(state)
	if (temp && temp.value)
	{
		return getTempInLocalScale(temp.value.toBigDecimal(), location.temperatureScale)
	}
	return 0
}


// get/convert temperature to current local scale
def getTempInLocalScale(temp, scale)
{
	if (temp && scale)
	{
		def scaledTemp = convertTemperatureIfNeeded(temp.toBigDecimal(), scale).toDouble()
		return (getTemperatureScale() == "F" ? scaledTemp.round(0).toInteger() : roundC(scaledTemp))
	}
	return 0
}

def roundC (tempC)
{
	return (Math.round(tempC.toDouble() * 2))/2
}


def switchMode()
{
	def supportedModes = device.currentValue("supportedThermostatModes").tokenize(' ,[]')
    def currentMode = device.currentValue("thermostatMode")
	if (supportedModes && supportedModes.size() && supportedModes[0].size() > 1)
	{
		def next = { supportedModes[supportedModes.indexOf(it) + 1] ?: supportedModes[0] }
		def nextMode = next(currentMode)
		"${nextMode}"()
	}
	else
	{
		log.warn "supportedThermostatModes did not return compatible modes"
	}
}


def switchFanMode()
{
	def supportedModes = device.currentValue("supportedThermostatFanModes").tokenize(' ,[]')
    def currentMode = device.currentValue("thermostatFanMode")
	if (supportedModes && supportedModes.size() && supportedModes[0].size() > 1)
	{
		def next = { supportedModes[supportedModes.indexOf(it) + 1] ?: supportedModes[0] }
		def nextMode = next(currentMode)
		"fan${nextMode.capitalize()}"()
	}
	else
	{
		log.warn "supportedThermostatFanModes did not return compatible modes"
	}
}


/*
	installed
    
	Doesn't do much other than call initialize().
*/
def installed()
{
	initialize()
}


/*
	updated
    
	Doesn't do much other than call initialize().
*/
def updated()
{
	initialize()
}


/*
	initialize
    
	Doesn't do much other than call refresh().
*/
def initialize()
{
	refresh()
}


/*
	refresh
    
	Refreshes the device by requesting an update from the client hub.
*/
def refresh()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
    sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}


/*
	parse
    
	In a virtual world this should never be called.
*/
def parse(String description)
{
	log.trace "Msg: Description is $description"
}


/*
	auto
    
	Sets the thermostat operating mode to "auto".
*/
def auto()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "auto")
}


/*
	cool
    
	Sets the thermostat operating mode to "cool".
*/
def cool()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "cool")
}


/*
	emergencyHeat
    
	Turns on emergency heat.
*/
def emergencyHeat()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "emergencyHeat")
}


/*
	fanAuto
    
	Sets the fan operating mode to "auto".
*/
def fanAuto()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "fanAuto")
}


/*
	fanCirculate
    
	Sets the fan operating mode to "circulate".
*/
def fanCirculate()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "fanCirculate")
}


/*
	fanOn
    
	Sets the fan operating mode to "on".
*/
def fanOn()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "fanOn")
}


/*
	heat
    
	Sets the thermostat operating mode to "heat".
*/
def heat()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "heat")
}


/*
	off
    
	Sets the thermostat operating mode to "off".
*/
def off()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "off")
}


/*
	setCoolingSetpoint
    
	Sets the cooling setpoint to <temperature>.
*/
def setCoolingSetpoint(temperature)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setCoolingSetpoint", [temperature.toString()])
}


/*
	setHeatingSetpoint
    
	Sets the heating setpoint to <temperature>.
*/
def setHeatingSetpoint(temperature)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setHeatingSetpoint", [temperature.toString()])
}


/*
	setSchedule
    
	Sets the thermostat schedule to <schedule> (JSON).
*/
def setSchedule(schedule)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setSchedule", [schedule.toString()])
}


/*
	setThermostatFanMode
    
	Sets the fans operating mode to <fanmode>.
*/
def setThermostatFanMode(fanmode)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setThermostatFanMode", [fanmode])
}


/*
	setThermostatMode
    
	Sets the thermostat operating mode to <thermostatmode>.
*/
def setThermostatMode(thermostatmode)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setThermostatMode", [thermostatmode])
}


/*
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "thermostat")
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}