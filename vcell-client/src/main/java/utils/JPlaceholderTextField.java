package utils;

import javax.swing.text.Document;
import java.awt.*;

public class JPlaceholderTextField extends javax.swing.JTextField {
	private String placeholder;

	public JPlaceholderTextField() {
		this("");
	}

	public JPlaceholderTextField(String placeholder) {
		this(null, placeholder, 0);
	}

	public JPlaceholderTextField(String placeholder, int columns) {
		this(null, placeholder, columns);
	}

	public JPlaceholderTextField(String text, String placeholder) {
		this(text, placeholder, 0);
	}

	public JPlaceholderTextField(String text, String placeholder, int columns) {
		this(null, text, placeholder, columns);
	}

	public JPlaceholderTextField(Document doc, String text, String placeholder, int columns) {
		super(doc, text, columns);
		this.setPlaceholder(placeholder);
	}

	public void setPlaceholder(String placeholder) {
		this.placeholder = placeholder;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (!this.getText().isEmpty() || this.isFocusOwner()) return;

		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.GRAY);
		g2.setFont(this.getFont().deriveFont(Font.ITALIC));
		Insets insets = this.getInsets();
		FontMetrics fm = g2.getFontMetrics();
		int x = insets.left;
		int y = (this.getHeight() - fm.getHeight()) / 2 + fm.getAscent();
		String textToDraw = this.isEnabled() ? this.placeholder : "";
		g2.drawString(textToDraw, x, y);
		g2.dispose();
	}
}
