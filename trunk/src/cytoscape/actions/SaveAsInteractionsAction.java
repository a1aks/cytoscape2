// $Revision$
// $Date$
// $Author$
package cytoscape.actions;

import cytoscape.Cytoscape;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.data.GraphObjAttributes;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * write out the current graph to the specified file, using the standard
 * interactions format:  nodeA edgeType nodeB.
 * for example: <code>
 * <p/>
 * YMR056C pp YLL013C
 * YCR107W pp YBR265W
 * <p/>
 * </code>
 */
public class SaveAsInteractionsAction extends CytoscapeAction {

    /**
     * Constructor.
     */
    public SaveAsInteractionsAction() {
        super("Graph as Interactions...");
        setPreferredMenu("File.Save");
    }

    /**
     * User-initiated Action.
     * @param e ActionEvent Object.
     */
    public void actionPerformed(ActionEvent e) {

        // get the file name
        File file = FileUtil.getFile("Save Graph as Interactions",
                    FileUtil.SAVE, new CyFileFilter[]{});

        if (file != null) {
            String fileName = file.getAbsolutePath();
            if (!fileName.endsWith(".sif"))
                fileName = fileName + ".sif";

            GraphObjAttributes nodeAttributes = Cytoscape.getNodeNetworkData();
            GraphObjAttributes edgeAttributes = Cytoscape.getEdgeNetworkData();

            //  Create LoadNetwork Task
            SaveToSifTask task = new SaveToSifTask(fileName,
                    nodeAttributes, edgeAttributes);

            //  Configure JTask Dialog Pop-Up Box
            JTaskConfig jTaskConfig = new JTaskConfig();
            jTaskConfig.setOwner(Cytoscape.getDesktop());
            jTaskConfig.displayCloseButton(true);
            jTaskConfig.displayStatus(true);
            jTaskConfig.setAutoDispose(false);

            //  Execute Task in New Thread;  pop open JTask Dialog Box.
            TaskManager.executeTask(task, jTaskConfig);
        }

    }
}

/**
 * Task to Save Interaction Data.
 */
class SaveToSifTask implements Task {
    private String fileName;
    private GraphObjAttributes nodeAttributes;
    private GraphObjAttributes edgeAttributes;
    private TaskMonitor taskMonitor;

    /**
     * Constructor.
     * @param fileName          Filename to save to
     * @param nodeAttributes    All Node Attributes
     * @param edgeAttributes    All Edge Attributes
     */
    SaveToSifTask (String fileName, GraphObjAttributes nodeAttributes,
        GraphObjAttributes edgeAttributes) {
        this.fileName = fileName;
        this.nodeAttributes = nodeAttributes;
        this.edgeAttributes = edgeAttributes;
    }

    /**
     * Executes the Task.
     */
    public void run() {
        taskMonitor.setStatus("Saving Interactions...");

        try {
            CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
            List nodeList = networkView.getNetwork().nodesList();

            if (nodeList.size() == 0) {
                throw new IllegalArgumentException ("Network is empty.");
            }
            saveInteractions();
            File file = new File (fileName);
            taskMonitor.setPercentCompleted (100);
            taskMonitor.setStatus("Graph successfully saved to:  "
                    + file.getAbsolutePath());
        } catch (IllegalArgumentException e) {
            taskMonitor.setException(e, "Network is Empty.  Cannot be saved.");
        } catch (IOException e) {
            taskMonitor.setException(e, "Unable to save interactions.");
        }
    }

    /**
     * Halts the Task:  Not Currently Implemented.
     */
    public void halt() {
        //   Task can not currently be halted.
    }

    /**
     * Sets the Task Monitor.
     *
     * @param taskMonitor TaskMonitor Object.
     */
    public void setTaskMonitor(TaskMonitor taskMonitor)
            throws IllegalThreadStateException {
        this.taskMonitor = taskMonitor;
    }

    /**
     * Gets the Task Title.
     *
     * @return Task Title.
     */
    public String getTitle() {
        return new String ("Saving Graph");
    }

    /**
     * Saves Interactions to File.
     * @throws IOException Error Writing to File.
     */
    private void saveInteractions() throws IOException {
        FileWriter fileWriter = new FileWriter(fileName);
        String lineSep = System.getProperty("line.separator");
        CyNetworkView networkView = Cytoscape.getCurrentNetworkView();
        List nodeList = networkView.getNetwork().nodesList();

        if (nodeList.size() == 0) {
            throw new IOException ("Network is empty.");
        }

        giny.model.Node[] nodes = (giny.model.Node[]) nodeList.toArray
                (new giny.model.Node[0]);
        for (int i = 0; i < nodes.length; i++) {

            //  Report on Progress
            double percent = ((double) i / nodes.length) * 100.0;
            taskMonitor.setPercentCompleted((int) percent);

            StringBuffer sb = new StringBuffer();
            giny.model.Node node = nodes[i];
            String canonicalName = nodeAttributes.getCanonicalName(node);
            List edges = networkView.getNetwork().getAdjacentEdgesList
                    (node, true, true, true);

            if (edges.size() == 0) {
                sb.append(canonicalName + lineSep);
            } else {
                Iterator it = edges.iterator();
                while (it.hasNext()) {
                    giny.model.Edge edge = (giny.model.Edge) it.next();
                    if (node == edge.getSource()) { //do only for outgoing edges
                        giny.model.Node target = edge.getTarget();
                        String canonicalTargetName =
                                nodeAttributes.getCanonicalName(target);
                        String edgeName = edgeAttributes.getCanonicalName(edge);
                        String interactionName =
                                (String) (edgeAttributes.getValue
                                ("interaction", edgeName));
                        if (interactionName == null) {
                            interactionName = "xx";
                        }
                        sb.append(canonicalName);
                        sb.append("\t");
                        sb.append(interactionName);
                        sb.append("\t");
                        sb.append(canonicalTargetName);
                        sb.append(lineSep);
                    }
                } // while
            } // else: this node has edges, write out one line for every
            // out edge (if any)
            fileWriter.write(sb.toString());
            //System.out.println(" WRITE: "+ sb.toString() );
        }  // for i
        fileWriter.close();
    }
}