package cytoscape.actions;
//-------------------------------------------------------------------------
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.util.CytoscapeAction;
import cytoscape.data.AttributeSaverDialog;

//-------------------------------------------------------------------------
public class ExportNodeAttributesAction extends CytoscapeAction {
    
  public ExportNodeAttributesAction () {
    super("Node Attributes");
    setPreferredMenu( "File.Export" );
  }

  public void actionPerformed(ActionEvent e) {
    AttributeSaverDialog.showNodeDialog();
  }
} // SaveAsGMLAction

