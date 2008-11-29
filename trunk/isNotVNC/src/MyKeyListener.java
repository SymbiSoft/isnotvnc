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
	
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case 37:
			pWriter.println("Left\n");
			pWriter.flush();
			break;
		case 38:
			pWriter.println("Up\n");
			pWriter.flush();
			break;
		case 39:
			pWriter.println("Right\n");
			pWriter.flush();
			break;
		case 40:
			pWriter.println("Down\n");
			pWriter.flush();
			break;
		}
		key.consume();
	}

	public void keyReleased(KeyEvent key) {
		key.consume();		
	}

	public void keyTyped(KeyEvent key) {
		char c=key.getKeyChar();
		if((int)c<=127) {
			pWriter.println(c);
		}
		else {
			switch(c) {
			case 13:pWriter.println("Select");break;
			case 10:pWriter.println("Select");break;
			case '':pWriter.println("e'");break;
			case '“':pWriter.println("i'");break;
			case 'ˆ':pWriter.println("a'");break;
			case '':pWriter.println("u'");break;
			case '˜':pWriter.println("o'");break;
			case 'Ž':pWriter.println("e'");break;
			}
		}
		pWriter.flush();
		key.consume();

	}

	public void setPrintWriter(PrintWriter writer) {
		this.pWriter=writer;
	}

}
