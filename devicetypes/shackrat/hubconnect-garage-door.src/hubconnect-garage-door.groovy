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
	definition(name: "HubConnect Garage Door", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.garagedoor", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/UniversalDrivers/HubConnect-Garage-Door.groovy")
	{
		capability "Sensor"
		capability "Actuator"
		capability "Garage Door Control"
		capability "Contact Sensor"

		attribute "version", "string"
        
		command "sync"
	}

    tiles(scale: 2)
    {
		multiAttributeTile(name:"garageDoor", type: "generic", width: 6, height: 4)
		{
			tileAttribute ("device.door", key: "PRIMARY_CONTROL")
			{
		        	attributeState "open", label:'${name}', action:"device.close", icon:"st.doors.garage.garage-open", backgroundColor:"#FF0000", nextState:"closing"
		        	attributeState "closed", label:'${name}', action:"device.open", icon:"st.doors.garage.garage-closed", backgroundColor:"#ffffff", nextState:"opening"
		        	attributeState "opening", label:'${name}', action:"device.close", icon:"st.doors.garage.garage-opening", backgroundColor:"#00A0DC", nextState:"open"
		        	attributeState "closing", label:'${name}', action:"device.open", icon:"st.doors.garage.garage-closing", backgroundColor:"#00A0DC", nextState:"closed"
		        	attributeState "unknown", label:'${name}', action:"device.close", icon:"st.doors.garage.garage-open", backgroundColor:"#00A0DC"
			}
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 3, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 3, height: 2)
		{
			state "default", label: '${currentValue}'
		}
		
		main(["garageDoor"])
		details(["garageDoor", "sync", "version"])
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
	open
    
	Opens the garage door.
*/
def open()
{
	// The server will update open/close status
	parent.sendDeviceEvent(device.deviceNetworkId, "open")
}


/*
	close
    
	Closes the garage door.
*/
def close()
{
	// The server will update open/close status
	parent.sendDeviceEvent(device.deviceNetworkId, "close")
}


/*
	refresh
    
	Refreshes the device by requesting an update from the client hub.
*/
def refresh()
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "refresh")
}


/*
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "garagedoor")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Universal", major: 1, minor: 3, build: 0]}