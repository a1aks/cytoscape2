
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

package ManualLayout.control.view;

import ManualLayout.control.actions.align.*;

import cytoscape.view.*;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;


/**
 *
 */
public class AlignPanel extends JPanel {
	/**
	 * Creates a new AlignPanel object.
	 */
	public AlignPanel() {
		ImageIcon hari = new ImageIcon(getClass().getResource("/H_ALIGN_RIGHT.gif"));
		ImageIcon haci = new ImageIcon(getClass().getResource("/H_ALIGN_CENTER.gif"));
		ImageIcon hali = new ImageIcon(getClass().getResource("/H_ALIGN_LEFT.gif"));
		ImageIcon vati = new ImageIcon(getClass().getResource("/V_ALIGN_TOP.gif"));
		ImageIcon vaci = new ImageIcon(getClass().getResource("/V_ALIGN_CENTER.gif"));
		ImageIcon vabi = new ImageIcon(getClass().getResource("/V_ALIGN_BOTTOM.gif"));

		HAlignRight har = new HAlignRight(hari);
		HAlignCenter hac = new HAlignCenter(haci);
		HAlignLeft hal = new HAlignLeft(hali);

		VAlignTop vat = new VAlignTop(vati);
		VAlignCenter vac = new VAlignCenter(vaci);
		VAlignBottom vab = new VAlignBottom(vabi);

		setLayout(new GridLayout(1,6));

		add(createJButton(hal, "Horizontal Left"));
		add(createJButton(hac, "Horizontal Center"));
		add(createJButton(har, "Horizontal Right"));

		add(createJButton(vat, "Vertical Top"));
		add(createJButton(vac, "Vertical Center"));
		add(createJButton(vab, "Vertical Bottom"));

		setBorder(new TitledBorder("Align"));
	}

	protected JButton createJButton(Action a, String tt) {
		JButton b = new JButton(a);
		b.setToolTipText(tt);
		b.setPreferredSize(new Dimension(27, 18));
		b.setMaximumSize(new Dimension(27, 18));
		b.setBorder(BorderFactory.createEmptyBorder());
		b.setBorderPainted(false);
		b.setOpaque(false);
		b.setContentAreaFilled(false);

		return b;
	}
}
