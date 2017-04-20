package org.vcell.util.gui;

import javax.swing.JCheckBox;
import javax.swing.JRadioButton;

/**
 * JRadioButton which remembers user data
 * @author GWeatherby
 *
 * @param <T> type of data to remember
 */
@SuppressWarnings("serial")
public class MomentoRadioButton<T> extends JRadioButton {
	private final T userData;

	/**
	 * @param data to remember, possibly null
	 * @param text see {@link JCheckBox#JCheckBox(String, boolean) }
	 * @param selected {@link JCheckBox#JCheckBox(String, boolean) }
	 */
	public MomentoRadioButton(T data, String text, boolean selected) {
		super(text, selected);
		userData = data;
	}
	
	/**
	 * @param data to remember, possibly null
	 * @param text see {@link JCheckBox#JCheckBox(String) }
	 */
	public MomentoRadioButton(T data, String text) {
		super(text);
		userData = data;
	}

	/** 
	 * @return data stored at construction
	 */
	public T getUserData() {
		return userData;
	}
}
