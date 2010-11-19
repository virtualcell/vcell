package cbit.gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;

@SuppressWarnings("serial")
public class LabelButton extends JButton {

	public LabelButton(Icon icon) {
		super(icon);
		initialize();
	}

	public LabelButton(String text, Icon icon) {
		super(text, icon);
		initialize();
	}

	public LabelButton(String text) {
		super(text);
		initialize();
	}
	
	private void initialize() {
		setContentAreaFilled(false);
		setBorder(BorderFactory.createEtchedBorder());
		setFocusable(false);
		setBorderPainted(false);
		setRolloverEnabled(true);		
	}
}
