//

/** Copyright (c) 2002 Institute for Sytems Biology and the Whitehead Institute
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and the
 ** California Institute of Technology and Japan Science and Technology
 ** Corporation have no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall the
 ** California Institute of Technology or the Japan Science and Technology
 ** Corporation be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if the
 ** California Institute of Technology and/or Japan Science and Technology
 ** Corporation have been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/

// EdgeAttributesPopupDetails.java
//
// $Revision$
// $Date$
// $Author$
//


package cytoscape.dialogs;

import cytoscape.*;
import javax.swing.*;
import y.base.*;
import y.view.*;
import java.awt.*;
import java.awt.event.*;


/**
 * This class provides a detailed list of attribute information for a
 * given edge.
 */
public class EdgeAttributesPopupDetails extends JDialog {

    public EdgeAttributesPopupDetails (Frame parentFrame, String name,
				       GraphObjAttributes edgeAttributes) {
	super (parentFrame, "Edge Attributes - "+name, false);

	JScrollPane scrollPanel = new JScrollPane(getContentComponent(edgeAttributes, name));

	JPanel buttonPanel = new JPanel();
	JButton dismissButton = new JButton ("Dismiss");
	dismissButton.addActionListener (new DismissAction (this));
	buttonPanel.add(dismissButton, BorderLayout.CENTER);
	
	JPanel panel = new JPanel();
	panel.setLayout (new BorderLayout());
	panel.add(scrollPanel, BorderLayout.CENTER);
	panel.add(buttonPanel, BorderLayout.SOUTH);

	setContentPane(panel);
    }

    protected Component getContentComponent
	(GraphObjAttributes edgeAttributes, String name) {
	
	String contents = name;

	if (name.length() == 0) {
	    contents = "Unable to locate attributes for selected edge";
	} else {
	    String attributes[] = edgeAttributes.getAttributeNames();
	    for (int i = 0; i < attributes.length; i++) {
		Object value = edgeAttributes.getValue(attributes[i], name);

		if (value != null)
		    contents += "\n\n" + attributes[i] + ":\n" + value;
	    }
	}

	JTextArea textArea = new JTextArea(contents, 8, 40);

	return textArea;
    }

    protected class DismissAction extends AbstractAction {
	private JDialog dialog;

	DismissAction (JDialog dialog) { super(""); this.dialog = dialog; }

	public void actionPerformed (ActionEvent e) {
	    dialog.dispose();
	}
    }
}

