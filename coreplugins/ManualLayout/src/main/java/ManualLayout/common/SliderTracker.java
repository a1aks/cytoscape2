/*
 Copyright (c) 2010, The Cytoscape Consortium (www.cytoscape.org)

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
package ManualLayout.common;


import cytoscape.Cytoscape;
import cytoscape.CyNetwork;
import cytoscape.data.SelectEvent;
import cytoscape.data.SelectEventListener;
import cytoscape.view.CytoscapeDesktop;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSlider;


public class SliderTracker implements SelectEventListener, PropertyChangeListener {
	private final JSlider slider;
	private final JCheckBox controllingCheckBox;

	public SliderTracker(final JSlider slider, final JCheckBox controllingCheckBox) {
		this.slider = slider;
		this.controllingCheckBox = controllingCheckBox;

		Cytoscape.getDesktop().getNetworkViewManager().getSwingPropertyChangeSupport()
		         .addPropertyChangeListener(CytoscapeDesktop.NETWORK_VIEW_FOCUSED, this);
	}

	public void onSelectEvent(final SelectEvent event) {
		final boolean currentNetworkHasSelectedNodes = 
			Cytoscape.getCurrentNetworkView().getSelectedNodeIndices().length > 0;

		slider.setEnabled(!controllingCheckBox.isSelected() || currentNetworkHasSelectedNodes);
	} 

	public void propertyChange(final PropertyChangeEvent e) {
		if (e.getPropertyName() == CytoscapeDesktop.NETWORK_VIEW_FOCUSED)
			Cytoscape.getCurrentNetwork().addSelectEventListener(this);
	}
}
