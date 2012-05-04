
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

import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogColorTheme.*;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogFontTheme.*;
import static edu.ucsd.bioeng.coreplugin.tableImport.ui.theme.ImportDialogIconSets.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * GUI Component for specify options for network table import.<br>
 *
 * @since Cytoscape 2.4
 * @version 0.8
 * @author Keiichiro Ono
 */
public class NetworkImportOptionsPanel extends JPanel {
	//private PropertyChangeSupport changes = new PropertyChangeSupport(this);
	private final PropertyChangeSupport changes;

	/**
	 * Creates a new NetworkImportOptionsPanel object.
	 */
	public NetworkImportOptionsPanel() {
		initComponents();

		initializeUIStates();
		changes = new PropertyChangeSupport(this);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void addPropertyChangeListener(PropertyChangeListener l) {
		if(changes != null)
			changes.addPropertyChangeListener(l);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	private void initComponents() {
		sourceLabel = new javax.swing.JLabel();
		sourceComboBox = new javax.swing.JComboBox();
		interactionLabel = new javax.swing.JLabel();
		interactionComboBox = new javax.swing.JComboBox();
		targetLabel = new javax.swing.JLabel();
		targetComboBox = new javax.swing.JComboBox();
		iconLabel1 = new javax.swing.JLabel();
		iconLabel2 = new javax.swing.JLabel();
		edgeAttributesLabel = new javax.swing.JLabel();

		setBorder(javax.swing.BorderFactory.createTitledBorder("Interaction Definition"));

		sourceLabel.setText("Source Interaction");
		sourceLabel.setForeground(SOURCE_COLOR.getColor());
		sourceLabel.setFont(LABEL_FONT.getFont());
		sourceComboBox.setName("sourceComboBox");
		sourceComboBox.setForeground(SOURCE_COLOR.getColor());
		sourceComboBox.setFont(ITEM_FONT_LARGE.getFont());
		sourceComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					networkColumnsComboBoxActionPerformed(evt);
				}
			});

		interactionLabel.setText("Interaction Type");
		interactionLabel.setForeground(INTERACTION_COLOR.getColor());
		interactionLabel.setFont(LABEL_FONT.getFont());

		interactionComboBox.setName("interactionComboBox");
		interactionComboBox.setForeground(INTERACTION_COLOR.getColor());
		interactionComboBox.setFont(ITEM_FONT_LARGE.getFont());
		interactionComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					networkColumnsComboBoxActionPerformed(evt);
				}
			});

		targetLabel.setText("Target Interaction");
		targetLabel.setForeground(TARGET_COLOR.getColor());
		targetLabel.setFont(LABEL_FONT.getFont());

		targetComboBox.setName("targetComboBox");
		targetComboBox.setForeground(TARGET_COLOR.getColor());
		targetComboBox.setFont(ITEM_FONT_LARGE.getFont());
		targetComboBox.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					networkColumnsComboBoxActionPerformed(evt);
				}
			});

		iconLabel1.setIcon(INTERACTION_ICON.getIcon());
		iconLabel2.setIcon(INTERACTION_ICON.getIcon());

		edgeAttributesLabel.setFont(LABEL_ITALIC_FONT.getFont());
		edgeAttributesLabel.setForeground(EDGE_ATTR_COLOR.getColor());
		edgeAttributesLabel.setHorizontalAlignment(SwingConstants.CENTER);
		edgeAttributesLabel.setIcon(CAUTION_ICON.getIcon());
		edgeAttributesLabel.setText("Columns in BLUE will be loaded as EDGE ATTRIBUTES.");

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(sourceComboBox, 0,
		                                                                      100, Short.MAX_VALUE)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(iconLabel1)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(sourceLabel)
		                                                                 .add(100, 100, 100)))
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(interactionComboBox,
		                                                                      0, 100,
		                                                                      Short.MAX_VALUE)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                                                 .add(iconLabel2)
		                                                                 .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(interactionLabel)
		                                                                 .add(100, 100, 100)))
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(targetLabel)
		                                                                 .addContainerGap(100,
		                                                                                  Short.MAX_VALUE))
		                                                      .add(layout.createSequentialGroup()
		                                                                 .add(targetComboBox, 0,
		                                                                      100, Short.MAX_VALUE)
		                                                                 .add(22, 22, 22))))
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     edgeAttributesLabel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 100,
		                                     Short.MAX_VALUE));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup()
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(sourceLabel).add(interactionLabel)
		                                                    .add(targetLabel))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(sourceComboBox,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                    .add(iconLabel1)
		                                                    .add(interactionComboBox,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                    .add(iconLabel2)
		                                                    .add(targetComboBox,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(edgeAttributesLabel)
		                                         .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                          Short.MAX_VALUE)));
	} // </editor-fold>

	private void networkColumnsComboBoxActionPerformed(java.awt.event.ActionEvent e) {
		final int sIdx = sourceComboBox.getSelectedIndex() - 1;
		final int tIdx = targetComboBox.getSelectedIndex() - 1;
		final int iIdx = interactionComboBox.getSelectedIndex() - 1;

		List<Integer> colIdx = new ArrayList<Integer>();

		colIdx.add(sIdx);
		colIdx.add(tIdx);
		colIdx.add(iIdx);

		changes.firePropertyChange(ImportTextTableDialog.NETWORK_IMPORT_TEMPLATE_CHANGED, null,
		                           colIdx);
	}

	/* ============================================================================================== */
	private void initializeUIStates() {
		sourceComboBox.setEnabled(false);
		targetComboBox.setEnabled(false);
		interactionComboBox.setEnabled(false);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param columnNames DOCUMENT ME!
	 */
	public void setComboBoxes(String[] columnNames) {
		/*
		 * Cleanup the combo boxes
		 */
		sourceComboBox.removeAllItems();
		targetComboBox.removeAllItems();
		interactionComboBox.removeAllItems();

		for (String item : columnNames) {
			sourceComboBox.addItem(item);
			targetComboBox.addItem(item);
			interactionComboBox.addItem(item);
		}

		interactionComboBox.insertItemAt("Default Interaction", 0);
		sourceComboBox.insertItemAt("Select Source node column...", 0);
		targetComboBox.insertItemAt("Select Target node column...", 0);

		sourceComboBox.setEnabled(true);
		targetComboBox.setEnabled(true);
		interactionComboBox.setEnabled(true);

		sourceComboBox.setSelectedIndex(0);
		targetComboBox.setSelectedIndex(0);
		interactionComboBox.setSelectedIndex(0);
	}

	/*
	 * Get index from combo boxes.
	 */
	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getSourceIndex() {
		return sourceComboBox.getSelectedIndex() - 1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getTargetIndex() {
		return targetComboBox.getSelectedIndex() - 1;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public int getInteractionIndex() {
		return interactionComboBox.getSelectedIndex() - 1;
	}

	// Variables declaration - do not modify
	private javax.swing.JLabel edgeAttributesLabel;
	private javax.swing.JLabel iconLabel1;
	private javax.swing.JLabel iconLabel2;
	private javax.swing.JComboBox interactionComboBox;
	private javax.swing.JLabel interactionLabel;
	private javax.swing.JComboBox sourceComboBox;
	private javax.swing.JLabel sourceLabel;
	private javax.swing.JComboBox targetComboBox;
	private javax.swing.JLabel targetLabel;

	// End of variables declaration
}
