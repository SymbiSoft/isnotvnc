import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.bluetooth.RemoteDevice;


public class Utils {
	public static void save(byte[] bbuf, int l) {
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
	
	public static String getSavedUrl() {
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

	public static void saveList(String connectionURL, RemoteDevice remoteDevice) {
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
}
