import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Vector;

import javax.bluetooth.DataElement;
import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
/**
 * A simple SPP client that connects with an SPP server
 */
public class Client implements DiscoveryListener{
	//object used for waiting
	public static Object lock=new Object();
	//vector containing the devices discovered
	public static Vector<RemoteDevice> vecDevices=new Vector<RemoteDevice>();
	public static String connectionURL=null;
	
	//methods of DiscoveryListener
	public void deviceDiscovered(RemoteDevice btDevice, DeviceClass cod) {
		//add the device to the vector
		if(!vecDevices.contains(btDevice)){
			vecDevices.addElement(btDevice);
		}
	}
	//implement this method since services are not being discovered
	public void servicesDiscovered(int transID, ServiceRecord[] servRecord) {
		if(servRecord!=null && servRecord.length>0){
			int i=0;
			for(i=0;connectionURL==null && i<servRecord.length;i++) {
				connectionURL=servRecord[i].getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
			}
			System.out.println(connectionURL);
			DataElement serviceName = servRecord[i].getAttributeValue(0x0100);
			System.out.println(serviceName.getValue());
		}
		synchronized(lock){
			lock.notify();
		}
	}
	//implement this method since services are not being discovered
	public void serviceSearchCompleted(int transID, int respCode) {
		synchronized(lock){
			lock.notify();
		}
	}
	public void inquiryCompleted(int discType) {
		synchronized(lock){
			lock.notify();
		}
	}//end method
}


