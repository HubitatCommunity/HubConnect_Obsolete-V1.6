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
	definition(name: "HubConnect Siren", namespace: "shackrat", author: "Steve White", ocfDeviceType: "x.com.st.d.siren", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Siren.groovy")
	{
		capability "Actuator"
		capability "Alarm"
		capability "Battery"
		capability "Switch"
		capability "Refresh"

		attribute "version", "string"
		
		command "sync"
	}

	tiles
	{
		standardTile("alarm", "device.alarm", width: 2, height: 2)
		{
			state "off", label: 'off', action: 'alarm.both', icon: "st.alarm.alarm.alarm", backgroundColor: "#ffffff"
			state "both", label: 'alarm!', action: 'alarm.off', icon: "st.alarm.alarm.alarm", backgroundColor: "#e86d13"
		}
		standardTile("off", "device.alarm", inactiveLabel: false, decoration: "flat")
		{
			state "default", label: '', action: "alarm.off", icon: "st.secondary.off"
		}
		standardTile("siren", "device.alarm", inactiveLabel: false, decoration: "flat")
		{
			state "off", label: 'siren', action: "alarm.siren", icon: "st.alarm.alarm.alarm", backgroundColor: "#ffffff", defaultState: true			
			state "siren", label: 'siren on!', action: "alarm.off", icon: "st.alarm.alarm.alarm", backgroundColor: "#e86d13"		
		}
		standardTile("strobe", "device.alarm", inactiveLabel: false, decoration: "flat")
		{
			state "off", label: 'strobe', action: "alarm.strobe", icon:"st.alarm.alarm.alarm", backgroundColor: "#ffffff", defaultState: true			
			state "strobe", label: 'strobe on!', action: "alarm.off", icon:"st.alarm.alarm.alarm", backgroundColor: "#e86d13"
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat")
		{
			state "battery", label: '${currentValue}% battery', unit: ""
		}
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat")
		{
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat")
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat")
		{
			state "default", label: '${currentValue}'
		}

		main "alarm"
		details(["alarm", "siren", "strobe", "off", "sync", "refresh", "battery", "version"])
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
	on
    
	Turns the siren on.
*/
def on()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "on")
}


/*
	off
    
	Turns the siren off.
*/
def off()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "off")
}


/*
	strobe
    
	Turns the strobe (if equipped).
*/
def strobe()
{
	// The server will update strobe status
	parent.sendDeviceEvent(device.deviceNetworkId, "strobe")
}


/*
	siren
    
	Turns the siren (if equipped).
*/
def siren()
{
	// The server will update siren status
	parent.sendDeviceEvent(device.deviceNetworkId, "siren")
}


/*
	both
    
	Turns the siren & strobe (if equipped).
*/
def both()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "both")
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
	parent.syncDevice(device.deviceNetworkId, "siren")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}