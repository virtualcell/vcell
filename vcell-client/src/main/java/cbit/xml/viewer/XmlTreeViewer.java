/*
 * Copyright (C) 1999-2011 University of Connecticut Health Center
 *
 * Licensed under the MIT License (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *  http://www.opensource.org/licenses/mit-license.php
 */

package cbit.xml.viewer;

/**
 * Insert the type's description here.
 * Creation date: (3/30/2001 3:38:59 PM)
 * @author: Daniel Lucio
 */
public class XmlTreeViewer extends javax.swing.JPanel {
	private javax.swing.JTree ivjJTree1 = null;
	private org.jdom2.Document fieldDocument = new org.jdom2.Document();
/**
 * XmlTreeViewer constructor comment.
 */
public XmlTreeViewer() {
	super();
	initialize();
}
/**
 */
private javax.swing.tree.MutableTreeNode buildTree(org.jdom2.Element root) {
	if (root == null) return (null);
	//
	//  Create an anonymous class subclassing the DefaultMutableTreeNode...
	XmlTreeViewerNode node = new XmlTreeViewerNode(root);
	//
	java.util.Iterator elements = root.getChildren().iterator();
	while (elements.hasNext()) {
		node.add(buildTree((org.jdom2.Element) elements.next()));
	}
	//
	java.util.Iterator attributes = root.getAttributes().iterator();
	while (attributes.hasNext()) {
		node.add(new XmlTreeViewerNode (attributes.next()));
	}
	return (node);
}
/**
 * Gets the document property (org.jdom2.Document) value.
 * @return The document property value.
 * @see #setDocument
 */
public org.jdom2.Document getDocument() {
	return fieldDocument;
}
/**
 * Return the JTree1 property value.
 * @return javax.swing.JTree
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTree getJTree1() {
	if (ivjJTree1 == null) {
		try {
			ivjJTree1 = new javax.swing.JTree();
			ivjJTree1.setName("JTree1");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTree1;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	System.out.println("--------- UNCAUGHT EXCEPTION ---------");
	exception.printStackTrace(System.out);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("XmlTreeViewer");
		setLayout(new java.awt.BorderLayout());
		setSize(309, 369);
		add(getJTree1(), "Center");
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		XmlTreeViewer aXmlTreeViewer;
		aXmlTreeViewer = new XmlTreeViewer();
		frame.setContentPane(aXmlTreeViewer);
		frame.setSize(aXmlTreeViewer.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		exception.printStackTrace(System.out);
	}
}
/**
 * Sets the document property (org.jdom2.Document) value.
 * @param document The new value for the property.
 * @exception java.beans.PropertyVetoException The exception description.
 * @see #getDocument
 */
public void setDocument(org.jdom2.Document document) /*throws java.beans.PropertyVetoException*/ {
	org.jdom2.Document oldValue = fieldDocument;
	//fireVetoableChange("document", oldValue, document);
	fieldDocument = document;
	firePropertyChange("document", oldValue, document);
	//
	try {
		if (document!=null){
			getJTree1().setModel(new javax.swing.tree.DefaultTreeModel(buildTree(document.getRootElement())));
		}else{
			getJTree1().setModel(new javax.swing.tree.DefaultTreeModel(new javax.swing.tree.DefaultMutableTreeNode(null)));
		}
	} catch (Exception exc) {
		handleException(exc);
		getJTree1().setModel(new javax.swing.tree.DefaultTreeModel(new javax.swing.tree.DefaultMutableTreeNode(null)));
	}
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE1FBB0B6GGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E135D8FDCCDB5795670FEC6B9FCCEB252C25D23BC6DDB6510EAE1E5A255906262CC5513430A9A5C40BB4A6C5ED5651AD2BD2AD6A2AE9D22FEDC820CDD21222B5C993C292A00E0D0DA1A498E30879C8870D3B2246E3C80BD49E7881AE365F5B73B3183469F64E3D6F3E77C81EE1C58B52CF57775C0FF36EB9677E4EFD107A536B2A6D0EFAC26C55C47B6B329312778EA1518DEF3839ACF03E2C16187E4EG0E
	1031D3D5508E4358815A4F749C0476B0E082E45896C1DBC73E5AF3CC132F51649CF687A1B1974EA96CB9294976421C9A1085G9117B11F997025E0C06B5F382F106C7B1962B8047B187664A85256EE535AC672E7FB76826AFA0E5A09EBEA209F8660611D24F27C9C34215139193E2949386A4BA6933E793838A0AA4160F8B291D5C4F10CF649BE3A4F71D08DDDC7C8B174735CA87BFDBF6E9D005F2300D9G333B77FCC833EB7C5327C9F9C7326A2D9CF61738193DFB5C065D95F33FF69C27BE3E3F81FD697200EF
	G9300B500750092403BB076B6F97894862EFFD29D1412C9A955AF4B830918720194939370BB980491CD7611B8A5A9B1C2D87CFE5953EAE14FF2E06BFE730557B11F94DE6431AC7A683B2462497B9D87ACFCD2D17829234243FC314FE3780469EE886552DD5F3AB457166A7EA7B16B4EEE58612C36503D76219DCE1736CF2855501DE845D8FFBC00313E813F6DDA9EB0F91C4AEDA6B94B3BD7E8EEE0FFB30CB5F9F9EE0C9F6636C42A9EA5F56D8FD87A216019FC47BDDEE643BA2FE18B4B0BC79A717E14023C92C0
	FCE53547G81C094B06B6339716229367FB1B7C685C59D484493B148F7367611063FC2ABE6D5C598C925D5A1B5A50E89CADC18CC08E9224DA9ECC05BA7172CFB02666C9468781B189295C10DCBA958DDC80A2A2824591C0D8DC7F00EA25251E8BCA9020224CC783EBFEEF89B7DAB2455FF48B1C1950D99ECFCCB837AF2DE98039DE183784D7CD2FA86FDFE8F1CFF9D40267B217FB46EF7CDD4D2E2A2980CC852DCC60E62C988E943D8DD22F73B8466D7G58DEBFBD0B32A1983F514677491EBEEA5AE7A8BE05668B
	4AA23F9B62GF2245C46E37D72B80F755021A7C86B0E2FDB463A66619D4E7CB6966375ED37673CC477C43BD87FCF7D632635F21BF55E2F96CCC75D00D907737F387FE1BDD6171BB0D625E0FFA520464FFD7C6469C6163383C2DA64D97A218289A6468C0F33390FB5A22F0A73E2CA9D92E5B195039FE9ADC7EC0D35C67ECECBFAEEED24F29972FD9092AC2D4B0B9BB1A785D5D5621399D53CAE2AEABCB5E30C17D0DD694C24ECDC952EEBEBA3720934G792E4F5F56F8145E03D4CA24C66BCB3471B21A2BE2C2CC02
	5103D2A625B2794EB3986FB9F1D13BAB95E77653DCCFE4705E315AE5935E44B3A871A438822FAE117D184711902F3DB407C006B5F8885AF1BF4F4905F3BCA71BEEBEC6B6FFACBB2C383060DBDFF3EC72335CAA759BB949EC7A79D9EB5B3753BB2349534C5693C12C59CBABAA4F8129997759837B13F66030F61E839F1EBBEE3AE7DA708725C40239D277F128298EE3A27EBBA382BD61053E267BED306968EDE3E31A8F597835A674E14244854C5EBFDEC09F52F741A7423C10752597A08F024177C78530063D85B6
	D23E40797EF67E96F87E8A1EE5967B0002F66E6F678E7137405A39C2923515BBF33DE1EC6D4C4F656D063FDD798BCE662B9F34204E7A8E28A59D5CCFDB1A4C3C561FCECBD3F1CA295A99B34D66F8B0594D66B7201D0EABE9F584AECC167BA32F996DC54E55449A977C680E1A9B68B0B8A752F1A77F303AB896C6FFCCF45CDEAFA728DC6E383DDED6DE6175B28F46F6F7F09EFE75BC46E378E2D9ED8C7099E0AD60A9C079C56E071A160BE405BAB71202721392925717DCE0DEEFDA1C6C4EE97DE7DA2A791D439052
	9AD1C6E33E97F4D6F5C23ED25906BE1439209F6E647CFCA30C77B7267727E9BF2B77DF3EB2CED64F1D4B4150F9A0FC37F4B8753B777A795C5C3B5FE54D3D87DD565CFB54F50449E18F73CB040F1FF0AD4F4DE7DDD65C6C24E3AF0576533CDE14B2AAA1D517B0EE241773E481784C8658895853496ABF63016FADA0BFCC434385D4DD1745A9320B2E5D524757C620BF8148832C83EC86B01D47322873FDE43CF6388317F15D8361FA139FDD17192E8834AC7FDD3D7CBB2590E43BAEF3BBDE746199F5BE72898BEAB0
	B8A0A1A7C6582198A7493DB55A7AE3DD1C133EC1779C057EAC4A3AB8A7E5DBB8A795FD7BD1B2DCE44DC93B22379C85DD6CFE97F7991C34973ECF184EBDFDDC67EBB06E8F206C449573B9519FAF40F71F8FDAB69ED9B20EF13DC04C631FAF99C7FB67162C37AD992FB62DE7E75B5A4C4F76172DB612787E1A1C5F0C59GBB53C3DAFB6F6E876D3BFFF55329555868BB44328EE0D4B51F7FA24C3F894B835C37375EB373FD71DB379CEF6650FF301B69ED6DB6FC9B48FF55496E302FF7F55FE721D52E4BFC61F50C4F
	65BE4C31C4B763F33BAEDF24722CC94E72FC3D1E67EBE02C321B733C5F0D77AA9204DC8564818AG1BG65C14E73CF3B7CE4851E0FCAF284C8A9E12291F63ED77AD6775D7A1383BC1ECF7D70F132694BC3B963D999E4F12C69E12D6B05876D583273BD6B417385E0AC8A48826E852C87947770733D6EEAE5673B9A0B2B7D2AAACC4D524A4D8F074FA7C30C24444E36BD3C5C5936755CD92319CDDEAF5E255DB0CEC2707E82788123A1EE4F4805956B6A552CE5DDE5F64D7791BD3F7EE37A7DF86F5D2ACFC6BEBFFB
	D7F5B07FF43BF35740305B3A863E67362E01917772B5AE6A4E7D7D71AF37797BC3FBDB7BB036B244B29FE08314GF66A31FD49FD164556A24F2E46E644C109FE752BDCFF29A777D937FBD67EDFC105875F1D13122ACAC95C1F4B1F77E03CD4C926C2A67B2D87FD2444E7E6D5B6D5137F4103FC1B902719183E41FE9766EF30FD7A59AEF9F35B9A72DA47E540EB9D17E16F72EF13D16F72F1DB70653665F35F4AFE7B0A0F3E61613C51C5D9E432FC9F3EAF24696934284767BEDF1DAE9BC90FCA69B8FE88F476220F
	6C7D0D4B64F2E3AF638776F6E968473D87C5FCAFF38EDC90B9871E7C4EB7494953E1871E590A83BF5376AA6CB5EA1A41B735FE34238246DE831C6CAB2B6D848C83A6GAAA03F0F4770DC1B8B5AB161930DF51092A505E6A8885983086628A2447977E73DBF372F5F752F6C6BA63FF50E3A7DF766E81B5FBAC7AF794DB94AE4C1EDDF34796FC2D25FB76C473789FB1BFEA02404991AD77F85D0CB87889B7061668689GG2897GGD0CB818294G94G88G88GE1FBB0B69B7061668689GG2897GG8CGGG
	GGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGC089GGGG
**end of data**/
}
}
