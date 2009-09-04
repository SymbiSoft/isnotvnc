import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class Communication {
    private boolean getScreen=false;
    private PrintWriter pWriter=null;
    private byte[] bbuf=new byte[128000];
    private InputStream inStream=null;
    private InnerThread inThread=null;
	private IsNotVNC isNotVNC=null;
    
    public Communication(IsNotVNC isNotVNC) {
    	this.isNotVNC=isNotVNC;
    }

    
	public void startServerBluetooth() throws IOException{

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
        
        inThread.setRun(false);
		pWriter.flush();
		pWriter.close();
		streamConnNotifier.close();

	}
	
	
	
	private void loop(OutputStream outStream) throws IOException {
		pWriter=new PrintWriter(new OutputStreamWriter(outStream));
		isNotVNC.getKeyListener().setPrintWriter(pWriter);		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		inThread=new InnerThread(this);
		
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
		if(pWriter!=null && command!=null) {
			pWriter.println(command);
			pWriter.flush();
		}
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
				isNotVNC.display(bbuf);
			}
			getScreen=false;
		}
	}
	
	public void startClientBluetooth() throws IOException {
		Client client=new Client();
		//display local d1evice address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());
		//find devices
		
		Client.connectionURL=Utils.getSavedUrl();
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
			
			Utils.saveList(Client.connectionURL,remoteDevice);
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
	
	public void startClientTcp() throws IOException {
		String addr_tcp="192.168.1.188";
		try {
			InetAddress addr = InetAddress.getByName("java.sun.com");
			int port = 12008;
			SocketAddress sockaddr = new InetSocketAddress(addr_tcp, port);

			// Create an unbound socket
			Socket sock = new Socket();

			// This method will block no more than timeoutMs.
			// If the timeout occurs, SocketTimeoutException is thrown.
			int timeoutMs = 2000; // 2 seconds
			sock.connect(sockaddr, timeoutMs);
		} catch (UnknownHostException e) {
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {
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


	public void quit() {
		if(pWriter!=null) {
        	try {
        		pWriter.println("QUIT");
				Thread.sleep(1000);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }		
	}
}
