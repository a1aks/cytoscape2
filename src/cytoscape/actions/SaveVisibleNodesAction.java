//-------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//-------------------------------------------------------------------------
package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;

import cytoscape.data.CyNetworkUtilities;
import cytoscape.view.NetworkView;
//-------------------------------------------------------------------------
public class SaveVisibleNodesAction extends AbstractAction {
    NetworkView networkView;
    
    public SaveVisibleNodesAction (NetworkView networkView) {
        super("Visible Nodes as List...");
        this.networkView = networkView;
    }

    public void actionPerformed (ActionEvent e) {
        File currentDirectory = networkView.getCytoscapeObj().getCurrentDirectory();
        JFileChooser chooser = new JFileChooser(currentDirectory);
        if (chooser.showSaveDialog (networkView.getMainFrame()) == chooser.APPROVE_OPTION) {
            String name = chooser.getSelectedFile().toString();
            currentDirectory = chooser.getCurrentDirectory();
            networkView.getCytoscapeObj().setCurrentDirectory(currentDirectory);
            boolean itWorked =
                    CyNetworkUtilities.saveVisibleNodeNames(networkView.getNetwork(), name, networkView.getCytoscapeObj().getConfiguration().isYFiles());
            Object[] options = {"OK"};
            if(itWorked) {
                JOptionPane.showOptionDialog(null,
                                         "Visible Nodes Saved.",
                                         "Visible Nodes Saved.",
                                         JOptionPane.DEFAULT_OPTION,
                                         JOptionPane.PLAIN_MESSAGE,
                                         null, options, options[0]);
            }
        }
    }
}

