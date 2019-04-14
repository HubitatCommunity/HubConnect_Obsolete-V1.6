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
	definition(name: "HubConnect Multipurpose Sensor", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.sensor", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Multipurpose-Sensor.groovy", mnmn: "SmartThings", vid: "generic-contact-2")
	{
		capability "Contact Sensor"
		capability "Acceleration Sensor"
		capability "Three Axis"
		capability "Temperature Measurement"
		capability "Battery"
		capability "Refresh"

		attribute "version", "string"
		
		command "sync"
	}

	tiles(scale: 2) {
		multiAttributeTile(name: "status", type: "generic", width: 6, height: 4)
		{
			tileAttribute("device.contact", key: "PRIMARY_CONTROL")
			{
				attributeState "open", label: 'Open', icon: "st.contact.contact.open", backgroundColor: "#e86d13"
				attributeState "closed", label: 'Closed', icon: "st.contact.contact.closed", backgroundColor: "#00a0dc"
				attributeState "garage-open", label: 'Open', icon: "st.doors.garage.garage-open", backgroundColor: "#e86d13"
				attributeState "garage-closed", label: 'Closed', icon: "st.doors.garage.garage-closed", backgroundColor: "#00a0dc"
			}
		}
		standardTile("contact", "device.contact", width: 2, height: 2)
		{
			state("open", label: 'Open', icon: "st.contact.contact.open", backgroundColor: "#e86d13")
			state("closed", label: 'Closed', icon: "st.contact.contact.closed", backgroundColor: "#00a0dc")
		}
		standardTile("acceleration", "device.acceleration", width: 2, height: 2)
		{
			state("active", label: 'Active', icon: "st.motion.acceleration.active", backgroundColor: "#00a0dc")
			state("inactive", label: 'Inactive', icon: "st.motion.acceleration.inactive", backgroundColor: "#cccccc")
		}
		valueTile("temperature", "device.temperature", width: 2, height: 2)
		{
			state("temperature", label: '${currentValue}°',
					backgroundColors: [
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
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

		main(["status", "acceleration", "temperature"])
		details(["status", "acceleration", "temperature", "battery", "sync", "refresh", "version"])
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
	parent.syncDevice(device.deviceNetworkId, "multipurpose")
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}