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
metadata 
{
	definition(name: "HubConnect Moisture Sensor", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.sensor", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Moisture-Sensor.groovy", mnmn: "SmartThings", vid: "generic-leak")
	{
		capability "Water Sensor"
		capability "Temperature Measurement"
		capability "Battery"
		capability "Refresh"

		attribute "version", "string"
		
		command "sync"
	}

	tiles(scale: 2)
	{
		multiAttributeTile(name:"water", type: "generic", width: 6, height: 4)
		{
			tileAttribute ("device.water", key: "PRIMARY_CONTROL") {
				attributeState "dry", label: "Dry", icon:"st.alarm.water.dry", backgroundColor:"#ffffff"
				attributeState "wet", label: "Wet", icon:"st.alarm.water.wet", backgroundColor:"#00A0DC"
			}
		}
		standardTile("temperatureState", "device.temperature", width: 2, height: 2)
		{
			state "normal", icon:"st.alarm.temperature.normal", backgroundColor:"#ffffff"
			state "freezing", icon:"st.alarm.temperature.freeze", backgroundColor:"#00A0DC"
			state "overheated", icon:"st.alarm.temperature.overheat", backgroundColor:"#e86d13"
		}
		valueTile("temperature", "device.temperature", width: 2, height: 2)
		{
			state("temperature", label:'${currentValue}°',
				backgroundColors:[
					[value: 31, color: "#153591"],
					[value: 44, color: "#1e9cbb"],
					[value: 59, color: "#90d2a7"],
					[value: 74, color: "#44b621"],
					[value: 84, color: "#f1d801"],
					[value: 95, color: "#d04e00"],
					[value: 96, color: "#bc2323"]
				]
			)
		}
		valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2)
		{
			state "battery", label:'${currentValue}% battery', unit:""
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

		main (["water", "temperatureState"])
		details(["water", "temperatureState", "temperature", "battery", "sync", "version"])
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
	parse
    
	In a virtual world this should never be called.
*/
def parse(String description)
{
	log.trace "Msg: Description is $description"
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
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "moisture")
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}