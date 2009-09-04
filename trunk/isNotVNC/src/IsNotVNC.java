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
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
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
	private JLabel screen = new JLabel();
	
    private MyKeyListener keyListener=new MyKeyListener();
    private Frame mainFrame=new Frame();
    private Frame connectionFrame=new Frame("Connection setting");
    
    private boolean protocol=false;
    private static final boolean TCP=false;
    private static final boolean BT=true;
    
    private boolean mode=false;
    private static final boolean CLIENT=false;
    private static final boolean SERVER=true;
    
    private Communication comm=null;

	public void display(byte[] cbuf) {
		try {
			ImageIcon icon = new ImageIcon(cbuf);
//			System.out.println(icon.getIconWidth()+" "+icon.getIconHeight());
			screen.setIcon(icon);
			screen.setSize(240,320);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startConnectionPanel() {
		Rectangle bounds = this.getGraphicsConfiguration().getBounds();
		connectionFrame.setLocation(300 + bounds.x, 20 + bounds.y);

		connectionFrame.setSize(300,100);
		JPanel connectionPanel = new JPanel();
		connectionPanel.setBackground(Color.LIGHT_GRAY);
		
		JRadioButton tcpButton = new JRadioButton("TCP");
		tcpButton.setMnemonic(KeyEvent.VK_B);
		tcpButton.setActionCommand("TCP");
		tcpButton.setSelected(protocol);

		JRadioButton btButton = new JRadioButton("BT");
		btButton.setMnemonic(KeyEvent.VK_C);
		btButton.setActionCommand("BT");
		btButton.setSelected(protocol);

		final JRadioButton clientButton = new JRadioButton("CLIENT");
		clientButton.setMnemonic(KeyEvent.VK_D);
		clientButton.setActionCommand("CLIENT");
		clientButton.setSelected(mode);
		
		final JRadioButton serverButton = new JRadioButton("SERVER");
		serverButton.setMnemonic(KeyEvent.VK_R);
		serverButton.setActionCommand("SERVER");
		serverButton.setSelected(mode);

		    //Group the radio buttons.
		ButtonGroup protocolGroup = new ButtonGroup();
		protocolGroup.add(tcpButton);
		protocolGroup.add(btButton);
		
		ButtonGroup modeGroup = new ButtonGroup();
		modeGroup.add(clientButton);
		modeGroup.add(serverButton);

		ActionListener al=new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				String command=e.getActionCommand();
				if(command.equals("TCP")) {
					protocol=TCP;
					serverButton.setSelected(false);
					clientButton.setSelected(true);
					
					mode=CLIENT;
				}
				if(command.equals("BT")) {
					protocol=BT;
				}
				if(command.equals("CLIENT")) {mode=CLIENT;}
				if(command.equals("SERVER")) {protocol=SERVER;}
			}
		};

		    //Register a listener for the radio buttons.
		tcpButton.addActionListener(al);
		btButton.addActionListener(al);
		clientButton.addActionListener(al);
		serverButton.addActionListener(al);

		JPanel protocolPanel = new JPanel(new GridLayout(0, 1));
		protocolPanel.add(tcpButton);
		protocolPanel.add(btButton);
		
		JPanel modePanel = new JPanel(new GridLayout(0, 1));
		modePanel.add(clientButton);
		modePanel.add(serverButton);
		
		connectionPanel.add(protocolPanel);
		connectionPanel.add(modePanel);

		addButton(connectionPanel,"GO!!",new KeyListener() {
			public void keyPressed(KeyEvent arg0) {}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
			
		},
		new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				connectionFrame.setVisible(false);
				
				startPanel("Connected");
				try {
					if(mode==SERVER && protocol==BT) comm.startServerBluetooth();
					if(mode==CLIENT && protocol==BT) comm.startClientBluetooth();
					if(mode==CLIENT && protocol==TCP) comm.startClientTcp();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
			
		},1);
		connectionFrame.add(connectionPanel);

		connectionFrame.setVisible(true);
	}
	
	public Communication getCommunication() {
		return comm;
	}

	private void startPanel(String connectionName) {
		comm=new Communication(this);
		mainFrame.setTitle(connectionName);
		Rectangle bounds = this.getGraphicsConfiguration().getBounds();
		mainFrame.setLocation(bounds.x, bounds.y);

		mainFrame.setSize(300,500);
		JPanel mainPanel = new JPanel();
		mainPanel.setBackground(Color.CYAN);
		mainPanel.setLayout(null);

		screen.setText("SCREEN");
		screen.setSize(240,320);
		screen.setLocation(30,10);
		mainPanel.add(screen);
		
		//label.addKeyListener(keyListener);
		
		addButton(mainPanel,"LSoft",1);
		addButton(mainPanel,"Up",2);
		addButton(mainPanel,"RSoft",3);
		addButton(mainPanel,"Left",4);
		addButton(mainPanel,"Select",5);
		addButton(mainPanel,"Right",6);
		addButton(mainPanel,"GET",7);
		addButton(mainPanel,"Down",8);
		addButton(mainPanel,"QUIT",9);
		
		
	    
	    mainPanel.addKeyListener(keyListener);
	    
		//this.getContentPane().add(mainPanel);
	    mainFrame.add(mainPanel);

	    mainFrame.setVisible(true);
	    mainPanel.setVisible(true);
	    screen.setVisible(true);
	    
		addWindowListener(this);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		      public void run() {
		        System.out.println("Running Shutdown Hook");
		        if(comm!=null) comm.quit();
		      }
		    });
		
	}
	
	private void addButton(JPanel panel, String command, int order) {
		addButton(panel, command, keyListener, new ButtonActionListener(command,comm),order);
	}
	
	private void addButton(JPanel panel, String command, KeyListener k, ActionListener actionListener,int order) {
		JButton get = new JButton(command);
		order--;
		int r=order/3;
		int c=order%3;

	    get.addActionListener(actionListener);
	    get.addKeyListener(k);
	    panel.add(get);
	    get.setLocation(20+c*90,330+r*35);
	    get.setSize(80, 30);
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


	public static void main(String[] args) throws IOException {
		//display local device address and name
		
		PrintStream out=new PrintStream("/isnotvnc.log");
		
		//System.setOut(out); 
		//System.setErr(out);
		
		
		LocalDevice localDevice = LocalDevice.getLocalDevice();
		System.out.println("Address: "+localDevice.getBluetoothAddress());
		System.out.println("Name: "+localDevice.getFriendlyName());
		IsNotVNC isNotVNC=new IsNotVNC();
		
		//isNotVNC.startConnectionPanel();
		
		//connectionFrame.setVisible(false);
		
		
		isNotVNC.startPanel("Connected");
		try {
			isNotVNC.getCommunication().startClientBluetooth();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void windowActivated(WindowEvent arg0) {
		
	}

	public void windowClosed(WindowEvent arg0) {
		
	}

	public void windowClosing(WindowEvent arg0) {
		System.exit(0);
	}

	public void windowDeactivated(WindowEvent arg0) {
		
	}

	public void windowDeiconified(WindowEvent arg0) {
		
	}

	public void windowIconified(WindowEvent arg0) {
		
	}

	public void windowOpened(WindowEvent arg0) {
		
	}

	public MyKeyListener getKeyListener() {
		return keyListener;
	}

}
