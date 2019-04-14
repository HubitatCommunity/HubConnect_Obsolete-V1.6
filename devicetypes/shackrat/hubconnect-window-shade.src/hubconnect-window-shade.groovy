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
	definition(name: "HubConnect Window Shade", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.blind", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Window-Shade.groovy", mnmn: "SmartThings", vid: "generic-shade")
	{
		capability "Window Shade"
		capability "Sensor"
		capability "Switch"
		capability "Switch Level"

		attribute "version", "string"

		command "open"
		command "close"
		command "sync"
	}

    tiles(scale: 2)
	{
        multiAttributeTile(name:"windowShade", type: "lighting", width: 6, height: 4)
		{
            tileAttribute ("device.windowShade", key: "PRIMARY_CONTROL")
			{
                attributeState "open", label:'${name}', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
                attributeState "closed", label:'${name}', action:"open", icon:"st.shades.shade-closed", backgroundColor:"#ffffff", nextState:"opening"
                attributeState "partially open", label:'Open', action:"close", icon:"st.shades.shade-open", backgroundColor:"#79b821", nextState:"closing"
                attributeState "opening", label:'${name}', action:"stop", icon:"st.shades.shade-opening", backgroundColor:"#79b821", nextState:"partially open"
                attributeState "closing", label:'${name}', action:"stop", icon:"st.shades.shade-closing", backgroundColor:"#ffffff", nextState:"partially open"
            }
            tileAttribute ("device.level", key: "SLIDER_CONTROL")
			{
                attributeState "level", action:"setLevel"
            }
        }
        standardTile("home", "device.level", width: 2, height: 2, decoration: "flat")
		{
            state "default", label: "home", action:"presetPosition", icon:"st.Home.home2"
        }
        standardTile("refresh", "device.refresh", width: 2, height: 2, inactiveLabel: false, decoration: "flat")
		{
            state "default", label:'', action:"refresh.refresh", icon:"st.secondary.refresh", nextState: "disabled"
            state "disabled", label:'', action:"", icon:"st.secondary.refresh"
        }
        valueTile("battery", "device.battery", decoration: "flat", inactiveLabel: false, width: 2, height: 2)
		{
            state "battery", label:'${currentValue}% battery', unit:""
        }
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.secondary.refresh"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

        main(["windowShade"])
        details(["windowShade", "home", "refresh", "battery", "sync", "version"])
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
	
	opens the window shade.
*/
def on() { open() }


/*
	off
	
	closes the window shade.
*/
def off() { close() }


/*
	open
    
	Opens the window shade.
*/
def open()
{
	// The server will update open/close status
	parent.sendDeviceEvent(device.deviceNetworkId, "open")
}


/*
	close
    
	Closes the window shade.
*/
def close()
{
	// The server will update open/close status
	parent.sendDeviceEvent(device.deviceNetworkId, "close")
}


/*
      setPosition
      
      sets the shade to a partial position, special case for null (Homebridge)
*/
def setPosition(pos) 
{
	if (pos != null)
	{
	   parent.sendDeviceEvent(device.deviceNetworkId, "setPosition", pos)
	}
	else
	{
	   parent.sendDeviceEvent(device.deviceNetworkId, "close")
	}
}
// Dashboard uses setLevel as if shades are 'dimmers'
def setLevel(pos) { setPosition(pos) } 


/*
	refresh
    
	Refreshes the device by requesting an update from the client hub.
*/
def refresh()
{
	// The server will update on/off status
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
	parent.syncDevice(device.deviceNetworkId, "windowshade")
}
def getDriverVersion() {[platform: "SmartThings", major: 1, minor: 3, build: 0]}