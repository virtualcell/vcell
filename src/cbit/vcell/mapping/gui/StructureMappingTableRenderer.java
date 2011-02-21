package cbit.vcell.mapping.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.TextAttribute;
import java.text.AttributedString;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.MatteBorder;

import org.vcell.util.Issue;
import org.vcell.util.gui.DefaultScrollTableCellRenderer;

import cbit.vcell.geometry.GeometryClass;
import cbit.vcell.mapping.FeatureMapping;
import cbit.vcell.mapping.StructureMapping;
import cbit.vcell.model.Structure;

@SuppressWarnings("serial")
public class StructureMappingTableRenderer extends DefaultScrollTableCellRenderer
{
	public static class TextIcon implements Icon {
		private String text = null;
		private int width = 20;
		private int height = 20;
		private int superScriptStartIndex = -1;
		private int superScriptEndIndex = -1;
		 
		public TextIcon(String text) {
			this(text, -1, -1);		
		}
		
		public TextIcon(String text, int start, int end) {
			this.text = text;
			this.superScriptStartIndex = start;
			this.superScriptEndIndex = end;
		}
		public void paintIcon(Component c, Graphics g, int x, int y) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setColor(c.getBackground());
		    g2d.fillRect(x, y, width, height);
            g2d.setColor(c.getForeground());
            Font font = g2d.getFont();
            font = font.deriveFont(font.getSize2D() - 1);
            FontMetrics fm = g.getFontMetrics();
            int width = getIconWidth() - fm.stringWidth(text);
            int xoffset = 0;
            if (width > 0) {
            	xoffset = width / 2;
            }
            g2d.setFont(font);
            
            int yoffset = 13;
            if (superScriptStartIndex >= 0 && superScriptEndIndex >= 0) {
            	AttributedString as = new AttributedString(text);
            	as.addAttribute(TextAttribute.SIZE, font.getSize2D());
            	as.addAttribute(TextAttribute.SUPERSCRIPT, TextAttribute.SUPERSCRIPT_SUPER, superScriptStartIndex, superScriptEndIndex);
            	g2d.drawString(as.getIterator(), x, y + yoffset);            	
            } else {
            	g2d.drawString(text, x + xoffset, y + yoffset);
            }
			g2d.dispose();
		}
		
		public int getIconWidth() {
			return width;
		}
		
		public int getIconHeight() {
			return height;
		}
	};
	private static final TextIcon volumeUnitIcon = new TextIcon("[ \u03BCm3 ]", 4, 5);
	private static final TextIcon areaUnitIcon = new TextIcon("[ \u03BCm2 ]", 4, 5);
	private static final TextIcon surfVolUnitIcon = new TextIcon("[ \u03BCm-1 ]", 4, 6);
	private static final TextIcon volSurfUnitIcon = new TextIcon("[ \u03BCm ]");
	
	public StructureMappingTableRenderer() {
		super();
		setHorizontalTextPosition(SwingConstants.LEFT);
	}

	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
	{
		super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		setIcon(null);
		if (table.getModel() instanceof StructureMappingTableModel) {
			StructureMappingTableModel structureMappingTableModel = (StructureMappingTableModel)table.getModel();
			if (value instanceof Structure) {
				Structure structure = (Structure)value;
				setText(structure.getName());
			} else if (value instanceof Double && structureMappingTableModel.isNewSizeColumn(column)) {
				StructureMapping structureMapping = structureMappingTableModel.getStructureMapping(row);
				if (structureMappingTableModel.isNonSpatial()) {	
					setIcon(structureMapping instanceof FeatureMapping ? volumeUnitIcon : areaUnitIcon);
				} else {
					if (structureMapping.getUnitSizeParameter()!=null){
						int role = structureMapping.getUnitSizeParameter().getRole();
						if (role == StructureMapping.ROLE_VolumePerUnitArea) {
							setIcon(volSurfUnitIcon);
						} else if (role == StructureMapping.ROLE_AreaPerUnitVolume) {
							setIcon(surfVolUnitIcon);
						}
					}
				}
			}
			if (structureMappingTableModel.isSubdomainColumn(column)) { // can be null
				if (value == null) {
					setText("Unmapped");
					setForeground(Color.red);
				} else {
					if (value instanceof GeometryClass) {
						setText(((GeometryClass)value).getName());
					} else {
						setText(value.toString());
					}
				}
			}

			List<Issue> issueList = structureMappingTableModel.getIssue(row, column);
			if (issueList.size() > 0) {
				setToolTipText(issueList.get(0).getMessage());
				if (column == 0) {
					setBorder(new MatteBorder(1,1,1,0,Color.red));
				} else if (column == table.getColumnCount() - 1) {
					setBorder(new MatteBorder(1,0,1,1,Color.red));
				} else {
					setBorder(new MatteBorder(1,0,1,0,Color.red));
				}
			} else {
				String toolTip = structureMappingTableModel.getToolTip(row, column);
				setToolTipText(toolTip);
				setBorder(DEFAULT_GAP);
			}
		}
		return this;
	}
}
