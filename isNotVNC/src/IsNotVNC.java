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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import javax.bluetooth.*;
import javax.microedition.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

@SuppressWarnings("unused")
public class IsNotVNC extends JFrame {

	private static final long serialVersionUID = 913025249206080080L;
	private JLabel label = new JLabel();
	private PrintWriter pWriter=null;
    private byte[] bbuf=new byte[128000];
    private InputStream inStream=null;
    MyKeyListener keyListener=new MyKeyListener();
    private boolean getScreen=false;
    
    private class InnerThread extends Thread {
    	private IsNotVNC isNotVNC=null;
    	private boolean run=true;
    	private long sleep=1000;
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
		boolean exit=false;
		while(!exit) {
			StreamConnection connection=streamConnNotifier.acceptAndOpen();
			RemoteDevice dev = RemoteDevice.getRemoteDevice(connection);
			System.out.println("Remote device address: "+dev.getBluetoothAddress());
			//System.out.println("Remote device name: "+dev.getFriendlyName(true));
			//read string from spp client
			inStream=connection.openInputStream();
			String lineRead="";
			OutputStream outStream=connection.openOutputStream();
			pWriter=new PrintWriter(new OutputStreamWriter(outStream));
			
			keyListener.setPrintWriter(pWriter);
			keyListener.setMainAppl(this);

			pWriter.println("Start");
			pWriter.write("Begin String from isNotVNC\n");
			pWriter.flush();
			
			InnerThread inThread=new InnerThread(this);
			
	        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
	        //sendGet();
			while(lineRead==null || !lineRead.equals("QUIT")) {
				if(inStream.available()>0) {
					int l=inStream.read(bbuf);
					System.out.println(new String(bbuf,0,l));
				}
				lineRead=in.readLine();
				if(lineRead!=null) {
					pWriter.println(lineRead);
					pWriter.flush();
					//sendCommand("GET");
				}
			}
			exit=true;
			pWriter.flush();
			pWriter.close();
			streamConnNotifier.close();
		}
	}
	
	protected void sendCommand(String command) {
		pWriter.println(command);
		pWriter.flush();
	}
	
	protected void getGet() throws IOException {
		if(!getScreen) {
			getScreen=true;
			pWriter.println("GET");
	        pWriter.flush();
	        try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(inStream.available()==0);
			int l=inStream.read(bbuf);
			//save(bbuf,l);
			display(bbuf);
			getScreen=false;
		}
	}

	@SuppressWarnings("unused")
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
		ImageIcon icon = new ImageIcon(cbuf);
		label.setIcon(icon);			
	}

	private void startPanel() {
		setSize(300,500);
		JPanel panel = new JPanel();
		panel.setBackground(Color.CYAN);

		panel.add(label);
		label.addKeyListener(keyListener);
		
		addButton(panel,"LSoft");
		addButton(panel,"Select");
		addButton(panel,"RSoft");
		
		addButton(panel,"Left");
		addButton(panel,"Up");
		addButton(panel,"Right");
		addButton(panel,"Down");
		addButton(panel,"GET");
	    
	    panel.addKeyListener(keyListener);
	    
		this.getContentPane().add(panel);

		setVisible(true);
		
	}
	
	private void addButton(JPanel panel, String command) {
		JButton get = new JButton(command);

	    ActionListener actionListener = new ButtonActionListener(command,this);
		
	    get.addActionListener(actionListener);
	    get.addKeyListener(keyListener);
	    panel.add(get);
	}

	public static void main(String[] args) throws IOException {
		//display local device address and name
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());
		IsNotVNC isNotVNC=new IsNotVNC();
		isNotVNC.startPanel();
		isNotVNC.startServer();
	}

}
