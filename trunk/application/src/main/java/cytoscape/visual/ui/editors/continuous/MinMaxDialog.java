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
package cytoscape.visual.ui.editors.continuous;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;
import cytoscape.data.attr.util.MultiHashMapHelpers;


/**
 *
 * @author  kono
 */
public class MinMaxDialog extends javax.swing.JDialog {
	
	private static final Font TEXTBOX_FONT = new java.awt.Font("SansSerif", 1, 10);
	
	private static final long serialVersionUID = 7350824820761046009L;
	
	private static MinMaxDialog dialog;

	/** Creates new form MinMaxDialog */
	private MinMaxDialog(Frame parent, boolean modal, Double min, Double max, final CyAttributes attr, final String attrName) {
		super(parent, modal);
		this.min = min;
		this.max = max;
		this.attr = attr;
		this.attrName = attrName;
		initComponents();
		
		this.minTextField.setText(min.toString());
		this.maxTextField.setText(max.toString());
	}

	private Double min;
	private Double max;
	
	private CyAttributes attr;
	private String attrName;

	/**
	 *  DOCUMENT ME!
	 *
	 * @param min DOCUMENT ME!
	 * @param max DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public static Double[] getMinMax(double min, double max, final CyAttributes attr, final String attrName) {
		final Double[] minMax = new Double[2];
		
		dialog = new MinMaxDialog(Cytoscape.getDesktop(), true, min, max, attr, attrName);
		dialog.setLocationRelativeTo(Cytoscape.getDesktop());
		dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dialog.setVisible(true);

		if ((dialog.min == null) || (dialog.max == null))
			return null;

		minMax[0] = dialog.min;
		minMax[1] = dialog.max;

		return minMax;
	}

	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents() {
		minLabel = new javax.swing.JLabel();
		maxLabel = new javax.swing.JLabel();
		minTextField = new javax.swing.JTextField();
		minTextField.setFont(TEXTBOX_FONT);
		
		maxTextField = new javax.swing.JTextField();
		maxTextField.setFont(TEXTBOX_FONT);
		
		okButton = new javax.swing.JButton();
		cancelButton = new javax.swing.JButton();
		titlePanel = new javax.swing.JPanel();
		titleLabel = new javax.swing.JLabel();
		
		restoreButton = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Set Range");
		setAlwaysOnTop(true);
		setResizable(false);

		minLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
		minLabel.setText("Min");

		maxLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
		maxLabel.setText("Max");

		okButton.setText("OK");
		okButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		okButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					okButtonActionPerformed(evt);
				}
			});

		cancelButton.setText("Cancel");
		cancelButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		cancelButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					cancelButtonActionPerformed(evt);
				}
			});
		
		restoreButton.setText("Restore");
		restoreButton.setToolTipText("Set range by current attribute's min and max.");
		restoreButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
		restoreButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent evt) {
					restoreButtonActionPerformed(evt);
				}
			});

		titlePanel.setBackground(new java.awt.Color(255, 255, 255));

		titleLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
		titleLabel.setText("Set Value Range");

		org.jdesktop.layout.GroupLayout titlePanelLayout = new org.jdesktop.layout.GroupLayout(titlePanel);
		titlePanel.setLayout(titlePanelLayout);
		titlePanelLayout.setHorizontalGroup(titlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                    .add(titlePanelLayout.createSequentialGroup()
		                                                                         .addContainerGap()
		                                                                         .add(titleLabel)
		                                                                         .addContainerGap(125,
		                                                                                          Short.MAX_VALUE)));
		titlePanelLayout.setVerticalGroup(titlePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                  .add(titlePanelLayout.createSequentialGroup()
		                                                                       .addContainerGap()
		                                                                       .add(titleLabel)
		                                                                       .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                                        Short.MAX_VALUE)));

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(titlePanel,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                     Short.MAX_VALUE)
		                                .add(layout.createSequentialGroup().addContainerGap()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(minLabel,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                           35,
		                                                           org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                                      .add(maxLabel,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           35, Short.MAX_VALUE))
		                                           .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                                      .add(minTextField,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           182, Short.MAX_VALUE)
		                                                      .add(maxTextField,
		                                                           org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                           182, Short.MAX_VALUE))
		                                           .addContainerGap())
		                                .add(org.jdesktop.layout.GroupLayout.TRAILING,
		                                     layout.createSequentialGroup()
		                                           .addContainerGap(163, Short.MAX_VALUE)
		                                           .add(cancelButton)
		                                           .add(restoreButton)
		                                           .add(okButton).addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup()
		                                         .add(titlePanel,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(minLabel)
		                                                    .add(minTextField,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                         30,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(maxLabel)
		                                                    .add(maxTextField,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                         30,
		                                                         org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
		                                                    .add(okButton).add(restoreButton).add(cancelButton))
		                                         .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                          Short.MAX_VALUE)));

		pack();
	} // </editor-fold>


	private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {
		try {
			min = Double.valueOf(minTextField.getText());
			max = Double.valueOf(maxTextField.getText());
		} catch (NumberFormatException e) {
			min = null;
			max = null;
		}

		dispose();
	}

	private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {
		min = null;
		max = null;
		dispose();
	}
	
	private void restoreButtonActionPerformed(ActionEvent evt) {
		final MultiHashMap mhm = attr.getMultiHashMap();
		final CountedIterator it = mhm.getObjectKeys(attrName);
		Object key;
		Double val = null;

		Double maxValue = Double.NEGATIVE_INFINITY;
		Double minValue = Double.POSITIVE_INFINITY;

		while (it.hasNext()) {
			key = it.next();
			try {
				val = Double.parseDouble(
					mhm.getAttributeValue(key.toString(),attrName, null).toString());
			} catch (NumberFormatException nfe) {
				continue;
			}
			
			if (val > maxValue)
				maxValue = val;
			if (val < minValue)
				minValue = val;
		}
		
		minTextField.setText(minValue.toString());
		maxTextField.setText(maxValue.toString());
	}
	
	

	// Variables declaration - do not modify
	private javax.swing.JButton cancelButton;
	private javax.swing.JLabel maxLabel;
	private javax.swing.JTextField maxTextField;
	private javax.swing.JLabel minLabel;
	private javax.swing.JTextField minTextField;
	private javax.swing.JButton okButton;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JPanel titlePanel;
	
	private JButton restoreButton;

	// End of variables declaration
}
