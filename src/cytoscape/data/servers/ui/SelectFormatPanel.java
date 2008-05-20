/*
 * NewBioDataServerPanel.java
 *
 * Created on 2006/04/04, 11:47
 */
package cytoscape.data.servers.ui;

import cytoscape.util.OpenBrowser;
import cytoscape.logger.CyLogger;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.event.HyperlinkEvent;


/**
 * Start panel
 *
 * @author kono
 */
public class SelectFormatPanel extends javax.swing.JPanel {
	// HTML document for the message box.
	private static final String HTML_RESOURCE_FILE = "/cytoscape/resources/Gene_Ontology_Wizard.html";

	/** Creates new form NewBioDataServerPanel */
	public SelectFormatPanel() {
		initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */

	// <editor-fold defaultstate="collapsed" desc=" Generated Code ">
	private void initComponents() {
		fileTypeButtonGroup = new javax.swing.ButtonGroup();
		titleLabel = new javax.swing.JLabel();
		jSeparator1 = new javax.swing.JSeparator();
		htmlScrollPane = new javax.swing.JScrollPane();
		messageLabel = new javax.swing.JLabel();
		bdsRadioButton = new javax.swing.JRadioButton();
		oboRadioButton = new javax.swing.JRadioButton();

		titleLabel.setFont(new java.awt.Font("Dialog", 1, 14));
		titleLabel.setText("Welcome to Gene Ontology Wizard");

		try {
			URL descriptionDoc = getClass().getResource(HTML_RESOURCE_FILE);

			descriptionEditorPane = new javax.swing.JEditorPane(descriptionDoc);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		descriptionEditorPane.setEditable(false);
		descriptionEditorPane.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
				public void hyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
					descriptionEditorPaneHyperlinkUpdate(evt);
				}
			});

		htmlScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		htmlScrollPane.setViewportView(descriptionEditorPane);

		messageLabel.setText("Which file format do you want to load?");

		fileTypeButtonGroup.add(bdsRadioButton);
		bdsRadioButton.setSelected(false);
		bdsRadioButton.setText("Cytoscape BioDataServer (.anno and .onto)");
		bdsRadioButton.setActionCommand("annoAndOnto");
		bdsRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		bdsRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));

		fileTypeButtonGroup.add(oboRadioButton);
		oboRadioButton.setText("Gene Ontology (.obo and gene_association)");
		oboRadioButton.setActionCommand("oboAndGa");
		oboRadioButton.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
		oboRadioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
		oboRadioButton.setSelected(true);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                                .add(layout.createSequentialGroup()
		                                           .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(htmlScrollPane,
		                                                                      org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                                                      328, Short.MAX_VALUE))
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(titleLabel,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                      254,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           layout.createSequentialGroup()
		                                                                 .add(65, 65, 65)
		                                                                 .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
		                                                                            .add(oboRadioButton)
		                                                                            .add(bdsRadioButton)))
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(jSeparator1,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                                                      328,
		                                                                      org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
		                                                      .add(org.jdesktop.layout.GroupLayout.LEADING,
		                                                           layout.createSequentialGroup()
		                                                                 .addContainerGap()
		                                                                 .add(messageLabel)))
		                                           .addContainerGap()));

		layout.linkSize(new java.awt.Component[] { bdsRadioButton, oboRadioButton },
		                org.jdesktop.layout.GroupLayout.HORIZONTAL);

		layout.setVerticalGroup(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
		                              .add(layout.createSequentialGroup().addContainerGap()
		                                         .add(titleLabel).add(8, 8, 8)
		                                         .add(jSeparator1,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              14,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(messageLabel).add(21, 21, 21)
		                                         .add(bdsRadioButton,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
		                                              16,
		                                              org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
		                                         .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
		                                         .add(oboRadioButton).add(19, 19, 19)
		                                         .add(htmlScrollPane,
		                                              org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
		                                              182, Short.MAX_VALUE).addContainerGap()));
	} // </editor-fold>

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public String getFileFormatRadioButtonSelected() {
		return fileTypeButtonGroup.getSelection().getActionCommand();
	}

	private void descriptionEditorPaneHyperlinkUpdate(javax.swing.event.HyperlinkEvent evt) {
		// TODO add your handling code here:
		if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
			OpenBrowser.openURL(evt.getURL().toString());
		}
	}

	private ImageIcon getImageIcon() {
		// return new ImageIcon((URL)getResource("clouds.jpg"));
		return null;
	}

	private Object getResource(String key) {
		URL url = null;
		String name = key;

		if (name != null) {
			try {
				Class c = Class.forName("cytoscape.util.swing.BioDataServerWizard");
				url = c.getResource(name);
			} catch (ClassNotFoundException cnfe) {
				CyLogger.getLogger().warn("Unable to find Parent class");
			}

			return url;
		} else

			return null;
	}

	// Variables declaration - do not modify
	private javax.swing.ButtonGroup fileTypeButtonGroup;
	private javax.swing.JEditorPane descriptionEditorPane;
	private javax.swing.JLabel titleLabel;
	private javax.swing.JLabel messageLabel;
	private javax.swing.JRadioButton bdsRadioButton;
	private javax.swing.JRadioButton oboRadioButton;
	private javax.swing.JScrollPane htmlScrollPane;
	private javax.swing.JSeparator jSeparator1;

	// End of variables declaration
}
