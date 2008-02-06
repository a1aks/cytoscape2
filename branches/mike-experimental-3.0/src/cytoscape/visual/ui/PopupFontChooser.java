/*
 File: PopupFontChooser.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

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
package cytoscape.visual.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;


/**
 *
 */
public class PopupFontChooser extends JDialog {
	private final static long serialVersionUID = 1202339876814272L;
    /**
     * DOCUMENT ME!
     *
     * @param f
     *            DOCUMENT ME!
     * @param begin
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Font showDialog(Frame f, Font begin) {
        final PopupFontChooser fpc = new PopupFontChooser(f, begin);
        fpc.setModal(true);
        fpc.setAlwaysOnTop(true);
        return fpc.getThisFont();
    }

    /**
     * DOCUMENT ME!
     *
     * @param f
     *            DOCUMENT ME!
     * @param begin
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Font showDialog(Dialog f, Font begin) {
        PopupFontChooser fpc = new PopupFontChooser(f, begin);
        fpc.setModal(true);
        fpc.setAlwaysOnTop(true);
        return fpc.getThisFont();
    }

    private Font font;
    private FontChooser chooser;

    private PopupFontChooser(Frame parent, Font begin) {
        super(parent, true);
        init(parent, begin);
    }

    private PopupFontChooser(Dialog parent, Font begin) {
        super(parent, true);
        init(parent, begin);
    }

    private void init(Component parent, Font begin) {
        this.setTitle("Please select a font...");
        font = begin;

        if (font == null)
            chooser = new FontChooser();
        else
            chooser = new FontChooser(begin.deriveFont(1F));

        // JComboBox face = chooser.getFaceComboBox();
        JPanel butPanel = new JPanel(false);

        // buttons - OK/Cancel
        JButton okBut = new JButton("OK");
        okBut.addActionListener(
            new AbstractAction() {
	private final static long serialVersionUID = 1202339876795625L;
                public void actionPerformed(ActionEvent e) {
                    font = chooser.getSelectedFont()
                                  .deriveFont(12F);
                    dispose();
                }
            });

        JButton cancelBut = new JButton("Cancel");
        cancelBut.addActionListener(
            new AbstractAction() {
	private final static long serialVersionUID = 1202339876804773L;
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            });

        butPanel.add(okBut);
        butPanel.add(cancelBut);

        Container content = getContentPane();
        content.setLayout(new BorderLayout());

        content.add(chooser, BorderLayout.CENTER);
        content.add(butPanel, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    private Font getThisFont() {
        return font;
    }
}
