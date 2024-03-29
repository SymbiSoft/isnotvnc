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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ButtonActionListener implements ActionListener {
	private String command;
	private Communication comm;
	
	public ButtonActionListener(String command, Communication comm) {
		super();
		this.command=command;
		this.comm=comm;
	}

	public void actionPerformed(ActionEvent e) {
		comm.sendCommand(command);
	}

}
