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
	definition(name: "HubConnect Lock", namespace: "shackrat", author: "Steve White", ocfDeviceType: "oic.d.smartlock", importUrl: "https://raw.githubusercontent.com/HubitatCommunity/HubConnect/master/SmartThings/DeviceTypes/HubConnect-Lock.groovy")
	{
        capability "Actuator"
        capability "Lock"
        capability "Lock Codes"
		capability "Battery"
		capability "Refresh"

		attribute "version", "string"
		
		command "sync"
	}

    tiles {
		multiAttributeTile(name:"toggle", type: "generic", width: 6, height: 4)
		{
		    tileAttribute ("device.lock", key: "PRIMARY_CONTROL") 
		    {
		        attributeState "locked", label:'locked', action:"lock.unlock", icon:"st.locks.lock.locked", backgroundColor:"#00A0DC", nextState:"unlocking"
		        attributeState "unlocked", label:'unlocked', action:"lock.lock", icon:"st.locks.lock.unlocked", backgroundColor:"#FFFFFF", nextState:"locking"
		        attributeState "locking", label:'locking', icon:"st.locks.lock.locked", backgroundColor:"#FFFFFF"
		        attributeState "unlocking", label:'unlocking', icon:"st.locks.lock.unlocked", backgroundColor:"#00A0DC"
		    }
		    tileAttribute ("device.battery", key: "SECONDARY_CONTROL") 
		    {
		        attributeState "battery", label: 'battery ${currentValue}%', unit: "%"
		    }
		}
		standardTile("lock", "device.lock", inactiveLabel: false, decoration: "flat", width: 3, height: 2) 
		{
		    state "default", label:'lock', action:"lock.lock", icon: "st.locks.lock.locked"
		}
		standardTile("unlock", "device.lock", inactiveLabel: false, decoration: "flat", width: 3, height: 2) 
		{
		    state "default", label:'unlock', action:"lock.unlock", icon: "st.locks.lock.unlocked"
		}
		standardTile("refresh", "device.lock", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}
		standardTile("sync", "sync", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: 'Sync', action: "sync", icon: "st.Bath.bath19"
		}
		valueTile("version", "version", inactiveLabel: false, decoration: "flat", width: 2, height: 2)
		{
			state "default", label: '${currentValue}'
		}

		main "toggle"
		details(["toggle", "lock", "unlock", "sync", "refresh", "version"])
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
	lock
    
	Locks the lock.
*/
def lock()
{
	// The server will update lock status
	parent.sendDeviceEvent(device.deviceNetworkId, "lock")
}


/*
	unlock
    
	Unlocks the lock.
*/
def unlock()
{
	// The server will update lock status
	parent.sendDeviceEvent(device.deviceNetworkId, "unlock")
}



/*
	setCodeLength
    
	Sets the code length for the lock.
*/
def setCodeLength(length)
{
	// The server will respond with the a "codeLength" event
	parent.sendDeviceEvent(device.deviceNetworkId, "setCodeLength", [length])
}


/*
	deleteCode
    
	Deletes the code at slot <codeNumber> for this lock.
*/
def deleteCode(codeNumber)
{
	// The server will respond with the a "codeChanged" event
	parent.sendDeviceEvent(device.deviceNetworkId, "deleteCode", [codeNumber])
}


/*
	setCode
    
	Adds a code at slot <codeNumber> with <code> and <name> for this lock.
*/
def setCode(codeNumber, code, name = null)
{
	// The server will respond with the a "codeChanged" event
	parent.sendDeviceEvent(device.deviceNetworkId, "setCode", [codeNumber, code, name])
}


/*
	getCodes
    
	Fetches all codes for this lock.
*/
def getCodes()
{
	// The server will respond with the a "codeChanged" event
	parent.sendDeviceEvent(device.deviceNetworkId, "getCodes")
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
	parent.syncDevice(device.deviceNetworkId, "lock")
	sendEvent([name: "version", value: "v${driverVersion.major}.${driverVersion.minor}.${driverVersion.build}"])
}
def getDriverVersion() {[platform: "Universal", major: 1, minor: 3, build: 0]}