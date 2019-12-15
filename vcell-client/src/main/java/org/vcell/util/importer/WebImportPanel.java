package org.vcell.util.importer;

import java.awt.BorderLayout;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;

import org.vcell.util.BeanUtils;
import org.vcell.util.gui.ScrollTable;

import cbit.vcell.client.desktop.biomodel.BioModelEditorRightSideTableModel;
import cbit.vcell.client.desktop.biomodel.VCellSortTableModel;
import cbit.vcell.resource.PropertyLoader;

@SuppressWarnings("serial")
public class WebImportPanel extends JPanel {

	public static String[] urlStringOptions = new String[]{
//		"http://www.signaling-gateway.org/molecule/query?afcsid=A000037&type=sbPAXExport",
//		"http://www.signaling-gateway.org/molecule/query?afcsid=A001778&type=sbPAXExport",
		BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.SIGNALLING_QUERY_URL),
//		"http://www.signaling-gateway.org/molecule/query?afcsid=A001852&type=sbPAXExport",
//		"http://www.signaling-gateway.org/molecule/query?afcsid=A001046&type=sbPAXExport",
		BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.BIOPAX_RSABIO12_URL),
		BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.BIOPAX_RSABIO65_URL),
		BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.BIOPAX_RSABIO11452_URL),
		BeanUtils.getDynamicClientProperties().getProperty(PropertyLoader.BIOPAX_RKEGGR01026_URL)};
	
	private List<String> urlList = new ArrayList<String>(Arrays.asList(urlStringOptions));
	//protected final JComboBox urlInput = new JComboBox(urlStringOptions);
	private ScrollTable urlTable = new ScrollTable();
	private VCellSortTableModel<String> urlTableModel = new VCellSortTableModel<String>(new String[]{"URL"}) {

		public Object getValueAt(int rowIndex, int columnIndex) {
			if (rowIndex < urlList.size()) {
				return getValueAt(rowIndex);
			} else {
				return BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT;
			}
		}

		@Override
		protected Comparator<String> getComparator(int col, final boolean ascending) {
			return new Comparator<String>() {

				public int compare(String o1, String o2) {
					int scale = ascending ? 1 : -1;
					return scale * o1.compareTo(o2);
				}
			};
		}

		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return rowIndex >= urlStringOptions.length;
		}

		@Override
		public int getRowCount() {
			return super.getRowCount() + 1;
		}

		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if (!aValue.equals(BioModelEditorRightSideTableModel.ADD_NEW_HERE_TEXT)) {
				if (rowIndex >= urlList.size()) {
					urlList.add((String) aValue);
				} else {
					urlList.set(rowIndex, (String) aValue);
				}
			}
			setData(urlList);
		}	
		
	};
	protected final JLabel urlMessageLabel = new JLabel();

	public WebImportPanel() {
		setLayout(new BorderLayout());
//		urlInput.setEditable(true);
//		add(urlInput);
		urlTableModel.setData(urlList);
		urlTable.setModel(urlTableModel);
		urlTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		add(urlTable.getEnclosingScrollPane(), BorderLayout.CENTER);
		add(urlMessageLabel,BorderLayout.SOUTH);
	}
	
	public URL getURL() { return getURL(true); }
	
	public URL getURL(boolean addDefaultProtocol) { 
		URL url = null;
		try {
			int row = urlTable.getSelectedRow();
			if (row < 0) {
				return null;
			}
			url = new URL(urlTableModel.getValueAt(row));
			urlMessageLabel.setText("");
		} catch (MalformedURLException e) {
			String message = e.getMessage();
			if(addDefaultProtocol) {				
				try {
					String urlStringWithHTTP = "http://" + getURLString();
					url = new URL(urlStringWithHTTP);
					urlMessageLabel.setText("");
				} catch (MalformedURLException e1) {
					urlMessageLabel.setText(message);
				}
			} else {
				urlMessageLabel.setText(message);				
			}
		}
		return url; 
	}
	
	public String getURLString() { 
		int row = urlTable.getSelectedRow();
		if (row >= 0) {
			return urlTableModel.getValueAt(row);
		}
		return null;
	}
	
}
