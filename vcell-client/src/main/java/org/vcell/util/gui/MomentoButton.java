package org.vcell.util.gui;

import javax.swing.JButton;

/**
 * JButton which remembers user data
 * @author GWeatherby
 *
 * @param <T> type of data to remember
 */
@SuppressWarnings("serial")
public class MomentoButton<T> extends JButton{
	private final T userData;

	/**
	 * @param data to remember, possibly null
	 * @param text see {@link JButton#JButton(String) }
	 */
	public MomentoButton(T data, String text) {
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
