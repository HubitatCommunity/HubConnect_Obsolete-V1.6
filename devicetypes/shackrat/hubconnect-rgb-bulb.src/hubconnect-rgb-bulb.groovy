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
	definition(name: "HubConnect RGB Bulb", namespace: "shackrat", author: "Steve White", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/UniversalDrivers/HubConnect-RGB-Bulb.groovy")
	{
		capability "Switch"
		capability "Switch Level"
		capability "Color Control"
		capability "Color Temperature"
		capability "Refresh"

		attribute "version", "string"

		command "sync"
	}

	tiles(scale: 2) {
	    multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true)
	    {
	        tileAttribute ("device.switch", key: "PRIMARY_CONTROL") 
	        {
	            attributeState "on", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
	            attributeState "off", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
	            attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.lights.philips.hue-single", backgroundColor:"#79b821", nextState:"turningOff"
	            attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.lights.philips.hue-single", backgroundColor:"#ffffff", nextState:"turningOn"
	        }
	        tileAttribute ("device.level", key: "SLIDER_CONTROL") 
	        {
	            attributeState "level", action:"switch level.setLevel"
	        }
	        tileAttribute ("device.color", key: "COLOR_CONTROL") 
	        {
	            attributeState "color", action:"color control.setColor"
	        }
	    }
	    controlTile("colorTempSliderControl", "device.colorTemperature", "slider", width: 2, height: 2, inactiveLabel: false, range:"(2700..6500)") 
	    {
	        state "colorTemperature", action:"color temperature.setColorTemperature"
	    }
	    valueTile("colorTemp", "device.colorTemperature", inactiveLabel: false, decoration: "flat", width: 2, height: 2) 
	    {
	        state "colorTemperature", label: '${currentValue} K'
	    }
	    standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
	    {
		  state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
	    }
	    valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
	    {
		  state "default", label: '${currentValue}'
	    }
	
	    main(["switch"])
	    details(["switch", "sync", "colorTempSliderControl", "version"])
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
    
	Turns the device on.
*/
def on()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "on")
}


/*
	off
    
	Turns the device off.
*/
def off()
{
	// The server will update on/off status
	parent.sendDeviceEvent(device.deviceNetworkId, "off")
}


/*
	setLevel
    
	Sets the level to <level> over duration <duration>.
*/
def setLevel(value, duration=1)
{
	// The server will respond with a level attribute message.
	parent.sendDeviceEvent(device.deviceNetworkId, "setLevel", [value, duration])
}


/*
	setColor
    
	Sets color by setting both hue and saturation as specified by <value>.
*/
def setColor(value)
{
	if (value.hue == null || value.saturation == null) return

	// The server will update status
//	parent.sendDeviceEvent(device.deviceNetworkId, "setColor", [hue: value.hue, saturation: value.saturation, level: value?.level])
	parent.sendDeviceEvent(device.deviceNetworkId, "setColor", [[hue: value.hue, saturation: value.saturation, level: value?.level]])
}


/*
	setHue
    
	Sets the Hue based on <value>.
*/
def setHue(value)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setHue", [value])
}


/*
	setSaturation
    
	Sets the Saturation based on <value>.
*/
def setSaturation(value)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setSaturation", [value])
}


/*
	setColorTemperature
    
	Sets the Color Temperature based on <value>.
*/
def setColorTemperature(value)
{
	// The server will update status
	parent.sendDeviceEvent(device.deviceNetworkId, "setColorTemperature", [value])
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
	parent.syncDevice(device.deviceNetworkId, "rgbbulb")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Universal", major: 1, minor: 2, build: 1]}
