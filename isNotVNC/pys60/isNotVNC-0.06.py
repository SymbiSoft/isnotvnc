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
import e32socket
import graphics
from key_codes import * 
import keypress

class BTReader:
	def serverbt(self):
		server_socket = socket.socket(socket.AF_BT,socket.SOCK_STREAM)
		p = socket.bt_rfcomm_get_available_server_channel(server_socket)
		server_socket.bind(("", p))
		print "bind done"
		server_socket.listen(1)
		socket.bt_advertise_service( u"isNotVNC", server_socket, True, socket.RFCOMM)
		socket.set_security(server_socket, socket.AUTH)
		print "I am listening"
		(self.sock,peer_addr) = server_socket.accept()
		print "Connection from %s"%peer_addr
	def servertcp(self,HOST,server_socket):
		print 'waiting of the client to connect on ', HOST, ':', PORT
		(self.sock,peer_addr) = server_socket.accept()
		print 'Connected by', peer_addr
	def connecttcp(self):
		self.sock=socket.socket(socket.AF_INET,socket.SOCK_STREAM)
		HOST=appuifw.query(u'Enter remote ip addr')
		# HOST = '217.30.180.11'    # The remote host
		PORT = 12008              # The same port as used by the server
		print "trying to connect to socket"
		self.sock.connect((HOST, PORT))
		print "connected"
	def connectbt(self):
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
	def loop(self):
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
				elif received.startswith('EKey0'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey0,EKey0)
				elif received.startswith('EKey1'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey1,EKey1)
				elif received.startswith('EKey2'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey2,EKey2)
				elif received.startswith('EKey3'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey3,EKey3)
				elif received.startswith('EKey4'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey4,EKey4)
				elif received.startswith('EKey5'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey5,EKey5)
				elif received.startswith('EKey6'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey6,EKey6)
				elif received.startswith('EKey7'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey7,EKey7)
				elif received.startswith('EKey8'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey8,EKey8)
				elif received.startswith('EKey9'):
					e32.reset_inactivity()
					received=received[5:]
					keypress.simulate_key(EKey9,EKey9)
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
				elif ord(received[0])==8:
					e32.reset_inactivity()
					key=ord(received[0])
					received=received[1:]
					keypress.simulate_key(key,key)
				elif ord(received[0])<32 or ord(received[0])>127:
					received=received[1:]
				else:
					e32.reset_inactivity()
					key=ord(received[0])
					received=received[1:]
					keypress.simulate_key(key,key)
			except:
				pass
	
	

imagename="c:\data\Images\snap.jpg"
bt=BTReader()
tcpbt = [u'tcp/ip',u'bluetooth']
choicetcpbt=appuifw.popup_menu(tcpbt,u'Role:')
clientserver = [u'client',u'server']
choice=appuifw.popup_menu(clientserver,u'Role:')
host = ''
if(choicetcpbt==0):
	ap_id = socket.select_access_point()
	apo = socket.access_point(ap_id)
	apo.start()
	host = apo.ip()
	print "PHONE IP IS", host
	socket.set_default_access_point(apo)
	e32.ao_sleep(5)
if(choice==0):
	if(choicetcpbt==0):
		bt.connecttcp()
	else:
		bt.connectbt()
	bt.loop()
	bt.close()
else:
	if(choicetcpbt==0):
		PORT = 12008
		print "define the socket"
		server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
		print "bind the socket"
		server_socket.bind((host, PORT))
		server_socket.listen(1)
	while(True):
		if(choicetcpbt==0):
			bt.servertcp(host,server_socket)
		else:
			bt.serverbt()
		bt.loop()
		bt.close()
	if(choicetcpbt==0):
		server_socket.close()


print "QUITTED"

