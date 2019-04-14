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
	definition(name: "HubConnect Smoke/CO Detector", namespace: "shackrat", author: "Steve White", ocfDeviceType: "x.com.st.d.sensor.smoke", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-SmokeCO.groovy")
	{
		capability "Smoke Detector"
		capability "Carbon Monoxide Detector"
		capability "Battery"
		capability "Refresh"

		attribute "version", "string"
		
		command "sync"
	}

	tiles (scale: 2)
	{
		multiAttributeTile(name:"smoke", type: "lighting", width: 6, height: 4)
		{
			tileAttribute ("device.alarmState", key: "PRIMARY_CONTROL")
			{
				attributeState("clear", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
				attributeState("smoke", label:"SMOKE", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
				attributeState("carbonMonoxide", label:"MONOXIDE", icon:"st.alarm.carbon-monoxide.carbon-monoxide", backgroundColor:"#e86d13")
				attributeState("tested", label:"TEST", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
			}
		}
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 3, height: 2)
		{
			state "battery", label:'${currentValue}% battery', unit:""
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 3, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

		main "smoke"
		details(["smoke", "sync", "battery", "version"])
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
	parent.sendDeviceEvent(device.deviceNetworkId, "contact", "refresh")
    sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}


/*
	sync
    
	Synchronizes the device details with the parent.
*/
def sync()
{
	// The server will respond with updated status and details
	parent.syncDevice(device.deviceNetworkId, "smoke")
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}