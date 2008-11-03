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
	private IsNotVNC isNotVNC;
	
	public ButtonActionListener(String command, IsNotVNC isNotVNC) {
		super();
		this.command=command;
		this.isNotVNC=isNotVNC;
	}

	public void actionPerformed(ActionEvent e) {
		isNotVNC.sendCommand(command);
	}

}
