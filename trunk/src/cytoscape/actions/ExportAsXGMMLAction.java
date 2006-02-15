package cytoscape.actions;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.bind.JAXBException;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.writers.XGMMLWriter;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.util.TaskManager;
import cytoscape.util.CyFileFilter;
import cytoscape.util.CytoscapeAction;
import cytoscape.util.FileUtil;
import cytoscape.view.CyNetworkView;

public class ExportAsXGMMLAction extends CytoscapeAction {

	public ExportAsXGMMLAction() {
		super("Network and attributes as XGMML...");
		setPreferredMenu("File.Export");
	}

	public ExportAsXGMMLAction(boolean label) {
		super();
	}

	public void actionPerformed(ActionEvent e) {

		// XGMML file name
		String name;

		try {
			name = FileUtil.getFile("Export Network and Attributes as XGMML",
					FileUtil.SAVE, new CyFileFilter[] {}).toString();
		} catch (Exception exp) {
			// this is because the selection was canceled
			return;
		}

		if (!name.endsWith(".xgmml"))
			name = name + ".xgmml";

		// Get Current Network and View
		CyNetwork network = Cytoscape.getCurrentNetwork();
		CyNetworkView view = Cytoscape.getNetworkView(network.getIdentifier());

		// Create Task
		ExportAsXGMMLTask task = new ExportAsXGMMLTask(name, network, view);

		// Configure JTask Dialog Pop-Up Box
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		jTaskConfig.displayCloseButton(true);
		jTaskConfig.displayStatus(true);
		jTaskConfig.setAutoDispose(false);

		// Execute Task in New Thread; pop open JTask Dialog Box.
		TaskManager.executeTask(task, jTaskConfig);
	}
} // SaveAsGMLAction

/**
 * Task to Save Graph Data to XGMML Format.
 */
class ExportAsXGMMLTask implements Task {
	private String fileName;
	private CyNetwork network;
	private CyNetworkView view;
	private TaskMonitor taskMonitor;

	/**
	 * Constructor.
	 * 
	 * @param network
	 *            Network Object.
	 * @param view
	 *            Network View Object.
	 */
	public ExportAsXGMMLTask(String fileName, CyNetwork network,
			CyNetworkView view) {
		this.fileName = fileName;
		this.network = network;
		this.view = view;
	}

	/**
	 * Executes Task
	 * 
	 * @throws Exception
	 */
	public void run() {
		taskMonitor.setStatus("Exporting Network and Attributes...");
		taskMonitor.setPercentCompleted(-1);

		int numNodes = network.getNodeCount();
		if (numNodes == 0) {
			throw new IllegalArgumentException("Network is empty.");
		}

		try {
			saveGraph();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		taskMonitor.setPercentCompleted(100);
		taskMonitor
				.setStatus("Network and attributes are successfully saved to:  "
						+ fileName);

	}

	/**
	 * Halts the Task: Not Currently Implemented.
	 */
	public void halt() {
		// Task can not currently be halted.
	}

	/**
	 * Sets the Task Monitor.
	 * 
	 * @param taskMonitor
	 *            TaskMonitor Object.
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
		return new String("Exporting Network and Attributes");
	}

	/**
	 * Saves Graph to File.
	 * 
	 * @throws IOException
	 * @throws JAXBException
	 */
	private void saveGraph() throws IOException, JAXBException {

		FileWriter fileWriter = new FileWriter(fileName);
		XGMMLWriter writer = new XGMMLWriter(network, view);

		writer.write(fileWriter);
		fileWriter.close();

		// MLC: 09/19/05 BEGIN:
		// // AJK: 09/14/05 BEGIN
		// Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, network);
		// // AJK: 09/14/05 END
		Object[] ret_val = new Object[3];
		ret_val[0] = network;
		ret_val[1] = new File(fileName).toURI();
		ret_val[2] = new Integer(Cytoscape.FILE_XGMML);
		Cytoscape.firePropertyChange(Cytoscape.NETWORK_SAVED, null, ret_val);
		// MLC: 09/19/05 END.
	}
}