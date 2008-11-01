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
