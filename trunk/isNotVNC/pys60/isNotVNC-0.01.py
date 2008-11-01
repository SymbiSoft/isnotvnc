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
bt.send("ciao\n")
esegui=True
while(esegui):
	try:
		received=bt.read()
		
		print "Received: "+received
		if received.startswith('QUIT'):
			print "Quitting"
			esegui=False
		elif received.startswith('GET'):
			graphics.screenshot().save( imagename )
			fh = file(imagename, 'rb');
			bt.send(fh.read())
			fh.close()
			print "SENT SCREENSHOT!"
		elif received.startswith('LSoft'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyLeftSoftkey,EKeyLeftSoftkey)
		elif received.startswith('RSoft'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyRightSoftkey,EKeyRightSoftkey)
		elif received.startswith('Left'):
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyLeftArrow, EKeyLeftArrow,EModifierKeypad)
		elif received.startswith('Right'):
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyRightArrow, EKeyRightArrow,EModifierKeypad)
		elif received.startswith('Up'):
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyUpArrow, EKeyUpArrow,EModifierKeypad)
		elif received.startswith('Down'):
			e32.reset_inactivity()
			keypress.simulate_key_mod(EKeyDownArrow, EKeyDownArrow,EModifierKeypad)
		elif received.startswith('Backspace'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyBackspace,EKeyBackspace)
		elif received.startswith('Del'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyBackspace,EKeyBackspace)
		elif received.startswith('Yes'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyYes,EKeyYes)
		elif received.startswith('No'):
			keypress.simulate_key(EKeyNo,EKeyNo)
			e32.reset_inactivity()
		elif received.startswith('Menu'):
			keypress.simulate_key(EKeyMenu,EKeyMenu)
			e32.reset_inactivity()
		elif received.startswith('Select'):
			keypress.simulate_key(EKeySelect,EKeySelect)
		elif received.startswith('Edit'):
			e32.reset_inactivity()
			keypress.simulate_key(EKeyEdit,EKeyEdit)
		else:
			e32.reset_inactivity()
			key=ord(received[0])
			keypress.simulate_key(key,key)
			
	except:
		pass
bt.close()
print "QUITTED"

