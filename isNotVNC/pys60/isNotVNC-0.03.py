#
#
# This file is part of isNotVNC.
#
#    isNotVNC is free software: you can redistribute it and/or modify
#    it under the terms of the GNU General Public License as published by
#    the Free Software Foundation, either version 3 of the License, or
#    (at your option) any later version.
#
#    isNotVNC is distributed in the hope that it will be useful,
#    but WITHOUT ANY WARRANTY; without even the implied warranty of
#    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#    GNU General Public License for more details.
#
#    You should have received a copy of the GNU General Public License
#    along with isNotVNC.  If not, see <http://www.gnu.org/licenses/>.
#
 


import socket
import appuifw
import e32
import graphics
#from key_modifiers import * 
from key_codes import * 
import keypress

class BTReader:
    def connect(self):
        self.sock=socket.socket(socket.AF_BT,socket.SOCK_STREAM)
        addr,services=socket.bt_discover()
        print "Discovered: %s, %s"%(addr,services)
        if len(services)>0:
            import appuifw
            choices=services.keys()
            choices.sort()
            choice=appuifw.popup_menu([unicode(services[x])+": "+x
                                       for x in choices],u'Choose port:')
            port=services[choices[choice]]
        else:
            port=services[services.keys()[0]]
        address=(addr,port)
        print "Connecting to "+str(address)+"...",
        self.sock.connect(address)
        print "OK." 
    def read(self):
        return self.sock.recv(1024)
    def close(self):
        self.sock.close()
    def send(self,data):
    	self.sock.send(data)
	

imagename="e:\Images\snap.jpg"
bt=BTReader()
bt.connect()
esegui=True
received=""
while(esegui):
	try:
		if(len(received)==0):
			received=bt.read()			
		print "Received: "+received
		if received.startswith('QUIT'):
			print "Quitting"
			esegui=False
			received=received[4:]
		elif received.startswith('GET'):
			graphics.screenshot().save( imagename )
			fh = file(imagename, 'rb');
			bt.send(fh.read())
			fh.close()
			print "SENT SCREENSHOT!"
			received=received[3:]
		elif received.startswith('LSoft'):
			e32.reset_inactivity()
			received=received[5:]
			keypress.simulate_key(EKeyLeftSoftkey,EKeyLeftSoftkey)
		elif received.startswith('RSoft'):
			e32.reset_inactivity()
			received=received[5:]
			keypress.simulate_key(EKeyRightSoftkey,EKeyRightSoftkey)
		elif received.startswith('Left'):
			e32.reset_inactivity()
			received=received[4:]
			keypress.simulate_key_mod(EKeyLeftArrow, EKeyLeftArrow,EModifierKeypad)
		elif received.startswith('Right'):
			received=received[5:]
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyRightArrow, EKeyRightArrow,EModifierKeypad)
		elif received.startswith('Up'):
			received=received[2:]
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyUpArrow, EKeyUpArrow,EModifierKeypad)
		elif received.startswith('Down'):
			e32.reset_inactivity()
			received=received[4:]
			keypress.simulate_key_mod(EKeyDownArrow, EKeyDownArrow,EModifierKeypad)
		elif received.startswith('Backspace'):
			e32.reset_inactivity()
			received=received[9:]
			keypress.simulate_key(EKeyBackspace,EKeyBackspace)
		elif received.startswith('Del'):
			e32.reset_inactivity()
			received=received[3:]
			keypress.simulate_key(EKeyBackspace,EKeyBackspace)
		elif received.startswith('Yes'):
			e32.reset_inactivity()
			received=received[3:]
			keypress.simulate_key(EKeyYes,EKeyYes)
		elif received.startswith('No'):
			keypress.simulate_key(EKeyNo,EKeyNo)
			received=received[2:]
			e32.reset_inactivity()
		elif received.startswith('Menu'):
			keypress.simulate_key(EKeyMenu,EKeyMenu)
			received=received[4:]
			e32.reset_inactivity()
		elif received.startswith('Select'):
			received=received[6:]
			keypress.simulate_key(EKeySelect,EKeySelect)
		elif received.startswith('Edit'):
			e32.reset_inactivity()
			received=received[4:]
			keypress.simulate_key(EKeyEdit,EKeyEdit)
		elif ord(received[0])<32 or ord(received[0])>125:
			received=received[1:]
		else:
			e32.reset_inactivity()
			key=ord(received[0])
			received=received[1:]
			keypress.simulate_key(key,key)
			
	except:
		pass
bt.close()
print "QUITTED"

