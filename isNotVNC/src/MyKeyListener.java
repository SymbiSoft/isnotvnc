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

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;



public class MyKeyListener implements KeyListener {
	private static final long serialVersionUID = -3338512540370634056L;
	private PrintWriter pWriter=null;
	private IsNotVNC isNotVNC=null;
	
	public void keyPressed(KeyEvent key) {
		key.consume();
		//System.out.println("pressed");
	}

	public void keyReleased(KeyEvent key) {
		key.consume();
		//System.out.println("released");
		
	}

	public void keyTyped(KeyEvent key) {
		//System.out.println("typed");

		char c=key.getKeyChar();
		System.out.print(c);
		pWriter.println(c);
		pWriter.flush();
		key.consume();
		//isNotVNC.sendCommand("GET");
	}

	public void setPrintWriter(PrintWriter writer) {
		this.pWriter=writer;
	}

	public void setMainAppl(IsNotVNC isNotVNC) {
		this.isNotVNC=isNotVNC;
	}

}
