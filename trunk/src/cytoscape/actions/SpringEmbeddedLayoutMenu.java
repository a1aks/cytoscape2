package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.foo.GraphConverter;
import cytoscape.graph.legacy.layout.algorithm.MutablePolyEdgeGraphLayout;
import cytoscape.graph.legacy.layout.impl.SpringEmbeddedLayouter2;
import cytoscape.task.Task;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;

public class SpringEmbeddedLayoutMenu extends JMenu
{

  private final JMenuItem m_wholeGraph;
  private final JMenuItem m_selectedNodesOnly;

  public SpringEmbeddedLayoutMenu()
  {
    super("Apply Spring Embedded Layout");
    m_wholeGraph = new JMenuItem(new SpringEmbeddedLayoutAction(false));
    add(m_wholeGraph);
    m_selectedNodesOnly = new JMenuItem(new SpringEmbeddedLayoutAction(true));
    add(m_selectedNodesOnly);
    addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e)
        {
          CyNetworkView graphView = Cytoscape.getCurrentNetworkView();
          if (graphView.getSelectedNodeIndices().length == 0)
            m_selectedNodesOnly.setEnabled(false);
          else
            m_selectedNodesOnly.setEnabled(true);
        } });
  }

  private static class SpringEmbeddedLayoutAction extends AbstractAction
  {

    private final boolean m_selectedNodesOnly;

    public SpringEmbeddedLayoutAction(boolean selectedNodesOnly)
    {
      super((selectedNodesOnly ? "Move Selected Nodes Only" :
             "Move All Nodes"));
      m_selectedNodesOnly = selectedNodesOnly;
    }

    public void actionPerformed(ActionEvent e)
    {
      final MutablePolyEdgeGraphLayout nativeGraph =
        GraphConverter.getGraphCopy(0.0d, false, m_selectedNodesOnly);
      Task task = new SpringEmbeddedLayouter2(nativeGraph);

      //////////////////////////////////////////////////////////
      // BEGIN: The thread and process related code starts here.
      //////////////////////////////////////////////////////////

      {
        task.setTaskMonitor(null);
        task.run();
      } // Whatever you do, make sure that task.run() is finished by the time
        // we exit out of this code block.  This may require a synchronized
        // block with Object.wait() if we call task.run() from another thread.

      //////////////////////////////////////////////////////
      // END: The thread and process related code ends here.
      //////////////////////////////////////////////////////

      GraphConverter.updateCytoscapeLayout(nativeGraph);
    }

  }

}
