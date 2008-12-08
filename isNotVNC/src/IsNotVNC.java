/**
 This file is part of isNotVNC.

    isNotVNC is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    isNotVNC is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with isNotVNC.  If not, see <http://www.gnu.org/licenses/>.

 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.bluetooth.*;
import javax.microedition.io.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

import com.apple.cocoa.application.NSApplication;

@SuppressWarnings("unused")
public class IsNotVNC extends JFrame implements WindowListener {

	private static final long serialVersionUID = 913025249206080080L;
	private JLabel label = new JLabel();
	private PrintWriter pWriter=null;
    private byte[] bbuf=new byte[128000];
    private InputStream inStream=null;
    MyKeyListener keyListener=new MyKeyListener();
    private boolean getScreen=false;
    private boolean run=true;
    
    private class InnerThread extends Thread {
    	private IsNotVNC isNotVNC=null;
    	private long sleep=500;
        InnerThread(IsNotVNC isNotVNC) {
          super();
          this.isNotVNC=isNotVNC;
          start();
        }

        public void run() {
          while (run) {
            try {
				isNotVNC.getGet();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            
            try {
              sleep(sleep);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          }
        }
      }

	//start server
	private void startServer() throws IOException{

		//Create a UUID for SPP
		UUID uuid = new UUID("1101", true);
		//Create the servicve url
		String connectionString = "btspp://localhost:" + uuid +";name=isNotVNC";
		//open server url
		StreamConnectionNotifier streamConnNotifier = (StreamConnectionNotifier)Connector.open( connectionString );
		//Wait for client connection
		System.out.println("\nServer Started. Waiting for clients to connect...");

		StreamConnection connection=streamConnNotifier.acceptAndOpen();
		RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
		System.out.println("Remote device address: "+dev.getBluetoothAddress());
		//System.out.println("Remote device name: "+dev.getFriendlyName(true));
		//read string from spp client
		inStream=connection.openInputStream();
		OutputStream outStream=connection.openOutputStream();
		
		

        loop(outStream);
        
        run=false;
		pWriter.flush();
		pWriter.close();
		streamConnNotifier.close();

	}
	
	private void loop(OutputStream outStream) throws IOException {
		pWriter=new PrintWriter(new OutputStreamWriter(outStream));
		keyListener.setPrintWriter(pWriter);		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		InnerThread inThread=new InnerThread(this);
		
        String lineRead="";
		while(lineRead==null || !lineRead.startsWith("QUIT")) {
			if(inStream.available()>0) {
				int l=inStream.read(bbuf);
				System.out.println(new String(bbuf,0,l));
			}
			lineRead=in.readLine();
			if(lineRead!=null) {
				pWriter.println(lineRead);
				pWriter.flush();
			}
		}
	}

	protected void sendCommand(String command) {
		pWriter.println(command);
		pWriter.flush();
	}
	
	protected synchronized void getGet() throws IOException {
		if(!getScreen) {
			getScreen=true;
			pWriter.println("GET");
	        pWriter.flush();
			
			int t=10; // wait max 1s (10x100=1000 ms)
			
			while(t>0 && inStream.available()==0){
				try {
					Thread.sleep(100);
					
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			t--;
			
			if(t>0) {
				int l=inStream.read(bbuf);
				//save(bbuf,l);
				display(bbuf);
			}
			getScreen=false;
		}
	}


	private void save(byte[] bbuf, int l) {
		try {
			File f=new File("/image.jpg");
			FileOutputStream fo=new FileOutputStream(f);
			fo.write(bbuf, 0, l);
			fo.flush();
			fo.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void display(byte[] cbuf) {
		try {
			ImageIcon icon = new ImageIcon(cbuf);
			label.setIcon(icon);
		}
		catch(Exception e) {}
	}

	private void startPanel() {
		setSize(300,500);
		JPanel panel = new JPanel();
		panel.setBackground(Color.CYAN);

		panel.add(label);
		label.addKeyListener(keyListener);
		
		addButton(panel,"LSoft");
		addButton(panel,"Up");
		addButton(panel,"RSoft");
		addButton(panel,"Left");
		addButton(panel,"Select");
		addButton(panel,"Right");
		addButton(panel,"GET");
		addButton(panel,"Down");
		addButton(panel,"QUIT");
	    
	    panel.addKeyListener(keyListener);
	    
		this.getContentPane().add(panel);

		setVisible(true);
		addWindowListener(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        System.out.println("Running Shutdown Hook");
		        if(pWriter!=null) {
		        	try {
		        		pWriter.println("QUIT");
						Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		      }
		    });
		
	}
	
	private void addButton(JPanel panel, String command) {
		JButton get = new JButton(command);

	    ActionListener actionListener = new ButtonActionListener(command,this);
		
	    get.addActionListener(actionListener);
	    get.addKeyListener(keyListener);
	    panel.add(get);
	}
	
	/**
	* Bounce the application's dock icon to get the user's attention.
	* 
	* ref: http://lists.apple.com/archives/java-dev/2003/Dec/msg00782.html
	*
	* @param critical Bounce the icon repeatedly if this is true. Bounce it
	* only for one second (usually just one bounce) if this is false.
	*/
	public static void bounceDockIcon(boolean critical) {
		int howMuch = (critical) ? NSApplication.UserAttentionRequestCritical
				: NSApplication.UserAttentionRequestInformational;
		final int requestID = NSApplication.sharedApplication()
				.requestUserAttention(howMuch);
		// Since NSApplication.requestUserAttention() seems to ignore the
		// param and always bounces the dock icon continuously no matter
		// what, make sure it gets cancelled if appropriate.
		// This is Apple bug #3414391
		if (!critical) {
			Thread cancelThread = new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// ignore
					}
					NSApplication.sharedApplication()
							.cancelUserAttentionRequest(requestID);
				}
			});
			cancelThread.start();
		}
	}
	
	public void startClient() throws IOException {
		Client client=new Client();
		//display local d1evice address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());
		//find devices
		
		Client.connectionURL=getSavedUrl();
		if(Client.connectionURL==null)
		{
			DiscoveryAgent agent = localDevice.getDiscoveryAgent();
			System.out.println("Starting device inquiry...");
			agent.startInquiry(DiscoveryAgent.GIAC, client);
			try {
				synchronized(Client.lock){
					Client.lock.wait();
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Device Inquiry Completed. ");
			//print all devices in vecDevices
					
			int deviceCount=Client.vecDevices.size();
			if(deviceCount <= 0){
				System.out.println("No Devices Found .");
				System.exit(0);
			}
			else{
				//print bluetooth device addresses and names in the format [ No. address (name) ]
				System.out.println("Bluetooth Devices: ");
				for (int i = 0; i <deviceCount; i++) {
					RemoteDevice remoteDevice=(RemoteDevice)Client.vecDevices.elementAt(i);
					System.out.println((i+1)+". "+remoteDevice.getBluetoothAddress()+" ("+remoteDevice.getFriendlyName(true)+")");
				}
			}
			System.out.print("Choose Device index: ");
			BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));
			String chosenIndex=bReader.readLine();
			int index=Integer.parseInt(chosenIndex.trim());
			//check for spp service
			RemoteDevice remoteDevice=(RemoteDevice)Client.vecDevices.elementAt(index-1);
			UUID[] uuidSet = new UUID[1];
			uuidSet[0]=new UUID("1101",true);
			System.out.println("\nSearching for service...");
			agent.searchServices(null,uuidSet,remoteDevice,client);
			try {
				synchronized(Client.lock){
					Client.lock.wait();
				}
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(Client.connectionURL==null){
				System.out.println("Device does not support Simple SPP Service.");
				System.exit(0);
			}
			
			saveList(Client.connectionURL,remoteDevice);
		}
		
		//connect to the server and send a line of text
		StreamConnection streamConnection=(StreamConnection)Connector.open(Client.connectionURL);
		//send string
		OutputStream outStream=streamConnection.openOutputStream();
		inStream=streamConnection.openInputStream();
		
		loop(outStream);
		
		inStream.close();
		outStream.close();
		streamConnection.close();
	}

	private String getSavedUrl() {
		String r=null;
		Properties p=new Properties();
		try {
			int k=0;
			try {
				Vector<String> urls=new Vector<String>();
				p.load(new FileInputStream("/isNotVNC.devices"));
				System.out.println("0. Search new device");
				Enumeration<Object> e=p.keys();
				while(e.hasMoreElements()) {
					String url=(String)e.nextElement();
					String device=p.getProperty(url);
					urls.addElement(url);
					System.out.println((k+1)+". "+device);
					k++;
				}
				BufferedReader bReader=new BufferedReader(new InputStreamReader(System.in));
				String chosenIndex=bReader.readLine();
				int index=Integer.parseInt(chosenIndex.trim());
				if(index!=0) r=urls.elementAt(index-1);
			}
			catch (Exception e) {}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return r;
	}

	private void saveList(String connectionURL, RemoteDevice remoteDevice) {
		Properties p=new Properties();
		try {
			try {
			p.load(new FileInputStream("/isNotVNC.devices"));
			}
			catch (Exception e) {}
			String device=p.getProperty(connectionURL);
			if(device==null) {
				p.put(connectionURL, remoteDevice.getFriendlyName(true));
				p.store(new FileOutputStream("/isNotVNC.devices"), "isNotVNC known devices");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) throws IOException {
		//display local device address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());
		IsNotVNC isNotVNC=new IsNotVNC();
		isNotVNC.startPanel();
		//isNotVNC.startServer();
		isNotVNC.startClient();
	}

	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
		
	}

	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
