
/*
 Copyright (c) 2006, 2007, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/

package edu.ucsd.bioeng.coreplugin.tableImport.ui;

import cytoscape.data.CyAttributes;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogFontTheme.KEY_FONT;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.BOOLEAN_ICON;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.FLOAT_ICON;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.INT_ICON;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.LIST_ICON;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.STRING_ICON;

import java.awt.Color;
import java.awt.Component;

import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;


/**
 * Cell renderer for alias table in Import Dialog.<br>
 *
 * @author kono
 *
 */
class AliasTableRenderer extends DefaultTableCellRenderer {
	private List<Byte> dataTypes;
	private int primaryKey;
	private final JLabel iconLabel = new JLabel();
	private final JLabel label = new JLabel();

	/**
	 * Creates a new AliasTableRenderer object.
	 *
	 * @param dataTypes  DOCUMENT ME!
	 * @param primaryKey  DOCUMENT ME!
	 */
	public AliasTableRenderer(List<Byte> dataTypes, int primaryKey) {
		this.dataTypes = dataTypes;
		this.primaryKey = primaryKey;

		label.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param dataTypes DOCUMENT ME!
	 */
	public void setDataTypes(List<Byte> dataTypes) {
		this.dataTypes = dataTypes;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param newKey DOCUMENT ME!
	 */
	public void setPrimaryKey(int newKey) {
		this.primaryKey = newKey;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param table DOCUMENT ME!
	 * @param value DOCUMENT ME!
	 * @param isSelected DOCUMENT ME!
	 * @param hasFocus DOCUMENT ME!
	 * @param row DOCUMENT ME!
	 * @param column DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
	                                               boolean hasFocus, int row, int column) {
		setBackground(Color.white);

		if (column != 2) {
			label.setText((value == null) ? "" : value.toString());
			label.setFont(table.getFont());

			if (((Boolean) table.getValueAt(row, 0) == true) && (primaryKey != row)) {
				label.setForeground(Color.green);
				label.setFont(KEY_FONT.getFont());
			} else if (primaryKey == row) {
				label.setForeground(Color.blue);
				label.setFont(KEY_FONT.getFont());
			} else {
				label.setForeground(Color.black);
			}

			return label;
		} else {
			if (dataTypes.get(row) == CyAttributes.TYPE_STRING) {
				iconLabel.setIcon(STRING_ICON.getIcon());
				iconLabel.setText("String");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_INTEGER) {
				iconLabel.setIcon(INT_ICON.getIcon());
				iconLabel.setText("Integer");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_FLOATING) {
				iconLabel.setIcon(FLOAT_ICON.getIcon());
				iconLabel.setText("Float");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_BOOLEAN) {
				iconLabel.setIcon(BOOLEAN_ICON.getIcon());
				iconLabel.setText("Boolean");
			} else if (dataTypes.get(row) == CyAttributes.TYPE_SIMPLE_LIST) {
				iconLabel.setIcon(LIST_ICON.getIcon());
				iconLabel.setText("List");
			}

			iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

			return iconLabel;
		}
	}
}
