package cytoscape.actions;

import java.awt.event.ActionEvent;
import javax.swing.*;
import giny.view.GraphView;
import cytoscape.view.CyNetworkView;
import cytoscape.Cytoscape;

public class SelectionModeAction extends JMenu {

	public SelectionModeAction() {
		super("Mouse Drag Selects");

		ButtonGroup modeGroup = new ButtonGroup();
		JCheckBoxMenuItem nodes = new JCheckBoxMenuItem(new AbstractAction(
				"Nodes") {
			public void actionPerformed(ActionEvent e) {
				// Do this in the GUI Event Dispatch thread...
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Cytoscape.setSelectionMode(Cytoscape.SELECT_NODES_ONLY);
					}
				});
			}
		});
		nodes.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK));

		JCheckBoxMenuItem edges = new JCheckBoxMenuItem(new AbstractAction(
				"Edges") {
			public void actionPerformed(ActionEvent e) {
				// Do this in the GUI Event Dispatch thread...
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Cytoscape.setSelectionMode(Cytoscape.SELECT_EDGES_ONLY);
					}
				});
			}
		});
		edges.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_E, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK));

		JCheckBoxMenuItem nodesAndEdges = new JCheckBoxMenuItem(
				new AbstractAction("Nodes and Edges") {
					public void actionPerformed(ActionEvent e) {
						// Do this in the GUI Event Dispatch thread...
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								Cytoscape
										.setSelectionMode(Cytoscape.SELECT_NODES_AND_EDGES);
							}
						});
					}
				});
		nodesAndEdges.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				java.awt.event.KeyEvent.VK_N, ActionEvent.CTRL_MASK
						| ActionEvent.SHIFT_MASK | ActionEvent.ALT_MASK));

		modeGroup.add(nodes);
		modeGroup.add(edges);
		modeGroup.add(nodesAndEdges);

		add(nodes);
		add(edges);
		add(nodesAndEdges);
		// nodes.setSelected(true);
		nodesAndEdges.setSelected(true);
		GraphView view = Cytoscape.getCurrentNetworkView();
		view.enableNodeSelection();
		view.enableEdgeSelection();
		Cytoscape.setSelectionMode(Cytoscape.SELECT_NODES_AND_EDGES);
	}

}
