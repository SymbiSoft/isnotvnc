import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;



public class MyKeyListener implements KeyListener {
	private static final long serialVersionUID = -3338512540370634056L;
	private PrintWriter pWriter=null;
	private IsNotVNC isNotVNC=null;
	
	public void keyPressed(KeyEvent arg0) {
		//System.out.println("pressed");
	}

	public void keyReleased(KeyEvent arg0) {
		//System.out.println("released");
		
	}

	public void keyTyped(KeyEvent key) {
		//System.out.println("typed");

		char c=key.getKeyChar();
		System.out.print(c);
		pWriter.println(c);
		pWriter.flush();
		//isNotVNC.sendCommand("GET");
	}

	public void setPrintWriter(PrintWriter writer) {
		this.pWriter=writer;
	}

	public void setMainAppl(IsNotVNC isNotVNC) {
		this.isNotVNC=isNotVNC;
	}

}
