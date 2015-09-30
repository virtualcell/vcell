/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.vcell.desktop;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.vcell.util.document.UserInfo;
import org.vcell.util.document.UserLoginInfo;

import cbit.vcell.client.constants.GuiConstants;
import cbit.vcell.server.UserRegistrationOP.NewPasswordUserInfo;


public class RegistrationPanel extends JPanel {
	
	//http://www.iso.org/iso/country_codes/iso_3166_code_lists.htm
	public static final String[] COUNTRY_LIST = new String[] {
		"UNITED STATES",
		"AFGHANISTAN", "ÅLAND ISLANDS", "ALBANIA","ALGERIA","AMERICAN SAMOA",
		"ANDORRA","ANGOLA","ANGUILLA","ANTARCTICA","ANTIGUA AND BARBUDA",
		"ARGENTINA","ARMENIA","ARUBA","AUSTRALIA","AUSTRIA",
		"AZERBAIJAN","BAHAMAS","BAHRAIN","BANGLADESH","BARBADOS",
		"BELARUS","BELGIUM","BELIZE","BENIN","BERMUDA",
		"BHUTAN","BOLIVIA","BOSNIA AND HERZEGOVINA","BOTSWANA","BOUVET ISLAND",
		"BRAZIL","BRITISH INDIAN OCEAN TERRITORY","BRUNEI DARUSSALAM","BULGARIA","BURKINA FASO",
		"BURUNDI","CAMBODIA","CAMEROON","CANADA","CAPE VERDE",
		"CAYMAN ISLANDS","CENTRAL AFRICAN REPUBLIC","CHAD","CHILE","CHINA",
		"CHRISTMAS ISLAND","COCOS (KEELING) ISLANDS","COLOMBIA","COMOROS","CONGO",
		"CONGO, THE DEMOCRATIC REPUBLIC OF THE","COOK ISLANDS","COSTA RICA","COTE DIVOIRE","CROATIA",
		"CUBA","CYPRUS","CZECH REPUBLIC","DENMARK","DJIBOUTI",
		"DOMINICA","DOMINICAN REPUBLIC","ECUADOR","EGYPT","EL SALVADOR",
		"EQUATORIAL GUINEA","ERITREA","ESTONIA","ETHIOPIA","FALKLAND ISLANDS (MALVINAS)",
		"FAROE ISLANDS","FIJI","FINLAND","FRANCE","FRENCH GUIANA",
		"FRENCH POLYNESIA","FRENCH SOUTHERN TERRITORIES","GABON","GAMBIA","GEORGIA",
		"GERMANY","GHANA","GIBRALTAR","GREECE","GREENLAND",
		"GRENADA","GUADELOUPE","GUAM","GUATEMALA","GUERNSEY",
		"GUINEA","GUINEA-BISSAU","GUYANA","HAITI","HEARD ISLAND AND MCDONALD ISLANDS",
		"HOLY SEE (VATICAN CITY STATE)","HONDURAS","HONG KONG","HUNGARY","ICELAND",
		"INDIA","INDONESIA","IRAN, ISLAMIC REPUBLIC OF","IRAQ","IRELAND",
		"ISLE OF MAN","ISRAEL","ITALY","JAMAICA","JAPAN",
		"JERSEY","JORDAN","KAZAKHSTAN","KENYA","KIRIBATI",
		"KOREA, DEMOCRATIC PEOPLES REPUBLIC OF","KOREA, REPUBLIC OF","KUWAIT","KYRGYZSTAN","LAO PEOPLES DEMOCRATIC REPUBLIC",
		"LATVIA","LEBANON","LESOTHO","LIBERIA","LIBYAN ARAB JAMAHIRIYA",
		"LIECHTENSTEIN","LITHUANIA","LUXEMBOURG","MACAO","MACEDONIA, THE FORMER YUGOSLAV REPUBLIC OF",
		"MADAGASCAR","MALAWI","MALAYSIA","MALDIVES","MALI",
		"MALTA","MARSHALL ISLANDS","MARTINIQUE","MAURITANIA","MAURITIUS",
		"MAYOTTE","MEXICO","MICRONESIA, FEDERATED STATES OF","MOLDOVA, REPUBLIC OF","MONACO",
		"MONGOLIA","MONTENEGRO","MONTSERRAT","MOROCCO","MOZAMBIQUE",
		"MYANMAR","NAMIBIA","NAURU","NEPAL","NETHERLANDS",
		"NETHERLANDS ANTILLES","NEW CALEDONIA","NEW ZEALAND","NICARAGUA","NIGER",
		"NIGERIA","NIUE","NORFOLK ISLAND","NORTHERN MARIANA ISLANDS","NORWAY",
		"OMAN","PAKISTAN","PALAU","PALESTINIAN TERRITORY, OCCUPIED","PANAMA",
		"PAPUA NEW GUINEA","PARAGUAY","PERU","PHILIPPINES","PITCAIRN",
		"POLAND","PORTUGAL","PUERTO RICO","QATAR","REUNION",
		"ROMANIA","RUSSIAN FEDERATION","RWANDA","SAINT BARTHELEMY","SAINT HELENA",
		"SAINT KITTS AND NEVIS","SAINT LUCIA","SAINT MARTIN","SAINT PIERRE AND MIQUELON","SAINT VINCENT AND THE GRENADINES",
		"SAMOA","SAN MARINO","SAO TOME AND PRINCIPE","SAUDI ARABIA","SENEGAL",
		"SERBIA","SEYCHELLES","SIERRA LEONE","SINGAPORE","SLOVAKIA",
		"SLOVENIA","SOLOMON ISLANDS","SOMALIA","SOUTH AFRICA","SOUTH GEORGIA AND THE SOUTH SANDWICH ISLANDS",
		"SPAIN","SRI LANKA","SUDAN","SURINAME","SVALBARD AND JAN MAYEN",
		"SWAZILAND","SWEDEN","SWITZERLAND","SYRIAN ARAB REPUBLIC","TAIWAN",/*"TAIWAN, PROVINCE OF CHINA",*/
		"TAJIKISTAN","TANZANIA, UNITED REPUBLIC OF","THAILAND","TIMOR-LESTE","TOGO",
		"TOKELAU","TONGA","TRINIDAD AND TOBAGO","TUNISIA","TURKEY",
		"TURKMENISTAN","TURKS AND CAICOS ISLANDS","TUVALU","UGANDA","UKRAINE",
		"UNITED ARAB EMIRATES","UNITED KINGDOM","UNITED STATES MINOR OUTLYING ISLANDS","URUGUAY",
		"UZBEKISTAN","VANUATU","VENEZUELA","VIET NAM","VIRGIN ISLANDS, BRITISH",
		"VIRGIN ISLANDS, U.S.","WALLIS AND FUTUNA","WESTERN SAHARA","YEMEN","ZAMBIA",
		"ZIMBABWE"
		};

	private JTextField textFieldLoginID = new JTextField();
	private JPasswordField textFieldPassword1 = new JPasswordField();
	private JPasswordField textFieldPassword2 = new JPasswordField();
	private JTextField textFieldEMail = new JTextField();
	private JTextField textFieldFirstName = new JTextField();
	private JTextField textFieldTitle = new JTextField();
	private JTextField textFieldOrganization = new JTextField();
	final JComboBox comboBoxCountry = new JComboBox(COUNTRY_LIST);
	private JCheckBox checkBoxNoEmail = new JCheckBox();

	public RegistrationPanel() {
		super();
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0,0,0,7,7,7,0};
		gridBagLayout.rowHeights = new int[] {0,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7,7};
		setLayout(gridBagLayout);

		final JLabel virtualCellUserLabel = new JLabel();
		virtualCellUserLabel.setText("Virtual Cell User Registration");
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weightx = 0;
		gridBagConstraints.gridwidth = 4;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridx = 0;
		add(virtualCellUserLabel, gridBagConstraints);

		final JTextArea welcomeTextArea = new JTextArea();
		welcomeTextArea.setMargin(new Insets(4, 4, 4, 4));
		welcomeTextArea.setWrapStyleWord(true);
		welcomeTextArea.setLineWrap(true);
		welcomeTextArea.setEditable(false);
		welcomeTextArea.setText("Welcome to The Virtual Cell user registration page. Please fill out all of the information below and you will be able to immediately begin using the Virtual Cell.  Required fields have an asterisk(*). ");
		welcomeTextArea.setBorder(GuiConstants.TAB_PANEL_BORDER);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_1.gridwidth = 4;
		gridBagConstraints_1.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_1.weightx = 0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.gridx = 0;
		add(welcomeTextArea, gridBagConstraints_1);

		final JLabel loginIdLabel = new JLabel();
		loginIdLabel.setText("Login ID *");
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.anchor = GridBagConstraints.EAST;
		gridBagConstraints_2.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_2.gridy = 2;
		gridBagConstraints_2.gridx = 0;
		add(loginIdLabel, gridBagConstraints_2);

		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_3.weightx = 1;
		gridBagConstraints_3.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_3.gridwidth = 3;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 1;
		add(textFieldLoginID, gridBagConstraints_3);

		final JLabel passwordLabel = new JLabel();
		passwordLabel.setText("Password *");
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.anchor = GridBagConstraints.EAST;
		gridBagConstraints_4.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_4.gridy = 3;
		gridBagConstraints_4.gridx = 0;
		add(passwordLabel, gridBagConstraints_4);

		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_6.weightx = 1;
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridy = 3;
		gridBagConstraints_6.gridx = 1;
		add(textFieldPassword1, gridBagConstraints_6);

		final JLabel passwordLabel_1 = new JLabel();
		passwordLabel_1.setText("Re-type Password *");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_5.gridy = 3;
		gridBagConstraints_5.gridx = 2;
		add(passwordLabel_1, gridBagConstraints_5);

		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_7.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_7.weightx = 1;
		gridBagConstraints_7.gridy = 3;
		gridBagConstraints_7.gridx = 3;
		add(textFieldPassword2, gridBagConstraints_7);

		final JEditorPane pleaseEnterYourTextArea = new JEditorPane("text/html",null);
		pleaseEnterYourTextArea.setMargin(new Insets(4, 4, 4, 4));
		pleaseEnterYourTextArea.setEditable(false);
		//pleaseEnterYourTextArea.setWrapStyleWord(true);
		//pleaseEnterYourTextArea.setLineWrap(true);
		pleaseEnterYourTextArea.setText(
			"<html>Passwords may only contain letters and numbers and are case sensitive.  "+
			"The email address will only be used to send infrequent notifications of VCell improvements and maintenance issues.  "+
			"For VCell support contact <i><b>vcell_support@uchc.edu</b></i>.  To join the VCell discussion group goto <i><b>groups.google.com/group/vcell-discuss</b></i>.</html>");
		pleaseEnterYourTextArea.setBorder(GuiConstants.TAB_PANEL_BORDER);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.insets = new Insets(4, 4, 20, 4);
		gridBagConstraints_8.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_8.gridwidth = 3;
		gridBagConstraints_8.gridy = 4;
		gridBagConstraints_8.gridx = 1;
		add(pleaseEnterYourTextArea, gridBagConstraints_8);

		final JLabel emailLabel = new JLabel();
		emailLabel.setText("E-Mail *");
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_9.anchor = GridBagConstraints.EAST;
		gridBagConstraints_9.gridy = 5;
		gridBagConstraints_9.gridx = 0;
		add(emailLabel, gridBagConstraints_9);

		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_10.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_10.gridwidth = 3;
		gridBagConstraints_10.gridy = 5;
		gridBagConstraints_10.gridx = 1;
		add(textFieldEMail, gridBagConstraints_10);

		final JLabel firstNameLabel = new JLabel();
		firstNameLabel.setText("Name *");
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.anchor = GridBagConstraints.EAST;
		gridBagConstraints_11.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_11.gridy = 6;
		gridBagConstraints_11.gridx = 0;
		add(firstNameLabel, gridBagConstraints_11);

		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_13.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_13.gridwidth = 3;
		gridBagConstraints_13.gridy = 6;
		gridBagConstraints_13.gridx = 1;
		add(textFieldFirstName, gridBagConstraints_13);

		final JLabel titleLabel = new JLabel();
		titleLabel.setText("Title");
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_15.anchor = GridBagConstraints.EAST;
		gridBagConstraints_15.gridy = 8;
		gridBagConstraints_15.gridx = 0;
		add(titleLabel, gridBagConstraints_15);

		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_16.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_16.gridwidth = 3;
		gridBagConstraints_16.gridy = 8;
		gridBagConstraints_16.gridx = 1;
		add(textFieldTitle, gridBagConstraints_16);

		final JLabel oranizationLabel = new JLabel();
		oranizationLabel.setText("Organization");
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.anchor = GridBagConstraints.EAST;
		gridBagConstraints_17.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_17.gridy = 9;
		gridBagConstraints_17.gridx = 0;
		add(oranizationLabel, gridBagConstraints_17);

		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_18.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_18.gridwidth = 3;
		gridBagConstraints_18.gridy = 9;
		gridBagConstraints_18.gridx = 1;
		add(textFieldOrganization, gridBagConstraints_18);

		final JLabel countryLabel = new JLabel();
		countryLabel.setText("Country");
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_27.anchor = GridBagConstraints.EAST;
		gridBagConstraints_27.gridy = 13;
		gridBagConstraints_27.gridx = 0;
		add(countryLabel, gridBagConstraints_27);

		comboBoxCountry.setActionCommand("comboBoxCountry");
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.insets = new Insets(4, 4, 4, 4);
		gridBagConstraints_28.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_28.gridwidth = 3;
		gridBagConstraints_28.gridy = 13;
		gridBagConstraints_28.gridx = 1;
		add(comboBoxCountry, gridBagConstraints_28);

		checkBoxNoEmail.setText("I DO NOT wish to be e-mailed periodically with VCell Software notifications.");
		final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
		gridBagConstraints_31.anchor = GridBagConstraints.WEST;
		gridBagConstraints_31.insets = new Insets(4, 0, 4, 4);
		gridBagConstraints_31.gridwidth = 4;
		gridBagConstraints_31.gridy = 15;
		gridBagConstraints_31.gridx = 0;
		add(checkBoxNoEmail, gridBagConstraints_31);
	}

	public void setUserInfo(UserInfo userInfo,boolean bLoggedIn){
		textFieldLoginID.setEnabled(true);
		textFieldLoginID.setText(userInfo.userid);
		textFieldLoginID.setEnabled(!bLoggedIn);
		
		textFieldPassword1.setText("");
		textFieldPassword2.setText("");
		textFieldEMail.setText(userInfo.email);
		textFieldFirstName.setText(userInfo.wholeName);
//		textFieldLastName.setText(userInfo.lastName);
		textFieldTitle.setText((userInfo.title==null?"":userInfo.title));
		textFieldOrganization.setText((userInfo.company==null?"":userInfo.company));
//		textFieldAddress1.setText(userInfo.address1);
//		textFieldAddress2.setText(userInfo.address2);
		comboBoxCountry.setSelectedItem(userInfo.country);
//		textFieldCity.setText(userInfo.city);
//		textFieldState.setText(userInfo.state);
//		textFieldZIP.setText(userInfo.zip);
		checkBoxNoEmail.setSelected(!userInfo.notify);
	}

	public NewPasswordUserInfo getUserInfo(){
		NewPasswordUserInfo userInfo = new NewPasswordUserInfo();
		userInfo.userid = textFieldLoginID.getText();
		char[] password1Char = textFieldPassword1.getPassword();
		userInfo.digestedPassword0 = (password1Char==null || password1Char.length==0?null:new UserLoginInfo.DigestedPassword(new String(password1Char)));
		char[] password2Char = textFieldPassword2.getPassword();
		userInfo.otherDigestedPassword = (password2Char==null || password2Char.length==0?null:new UserLoginInfo.DigestedPassword(new String(password2Char)));
		userInfo.email = textFieldEMail.getText();
		userInfo.wholeName = textFieldFirstName.getText();
//		userInfo.lastName = textFieldLastName.getText();
		userInfo.title = textFieldTitle.getText();
		userInfo.company = textFieldOrganization.getText();
//		userInfo.address1 = textFieldAddress1.getText();
//		userInfo.address2 = (textFieldAddress2.getText() == null || textFieldAddress2.getText().length()==0?null:textFieldAddress2.getText());
		userInfo.country = (String)comboBoxCountry.getSelectedItem();
//		userInfo.city = textFieldCity.getText();
//		userInfo.state = textFieldState.getText();
//		userInfo.zip = textFieldZIP.getText();
		userInfo.notify = !checkBoxNoEmail.isSelected();
		return userInfo;
	}
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(600,600);
	}
	
	public void reset(){
		textFieldLoginID.setEnabled(true);
		textFieldLoginID.setText("");
		
		textFieldPassword1.setText("");
		textFieldPassword2.setText("");
		textFieldEMail.setText("");
		textFieldFirstName.setText("");
//		textFieldLastName.setText("");
		textFieldTitle.setText("");
		textFieldOrganization.setText("");
//		textFieldAddress1.setText("");
//		textFieldAddress2.setText("");
		comboBoxCountry.setSelectedItem("");
//		textFieldCity.setText("");
//		textFieldState.setText("");
//		textFieldZIP.setText("");
		checkBoxNoEmail.setSelected(false);
	}
}
