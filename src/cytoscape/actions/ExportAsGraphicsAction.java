package cytoscape.actions;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.event.MenuEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import javax.swing.JOptionPane;

import cytoscape.Cytoscape;
import cytoscape.view.CyNetworkView;
import cytoscape.view.InternalFrameComponent;
import cytoscape.util.CytoscapeAction;

import cytoscape.util.CyFileFilter;

import cytoscape.task.ui.JTaskConfig;
import cytoscape.task.Task;
import cytoscape.task.TaskMonitor;
import cytoscape.task.util.TaskManager;

import cytoscape.util.export.Exporter;
import cytoscape.util.export.BitmapExporter;
import cytoscape.util.export.PDFExporter;
import cytoscape.util.export.SVGExporter;
import cytoscape.util.export.PSExporter;
import cytoscape.dialogs.ExportBitmapOptionsDialog;
import cytoscape.dialogs.ExportAsGraphicsFileChooser;

import cytoscape.logger.CyLogger;

import cytoscape.visual.VisualStyle;
import cytoscape.visual.calculators.Calculator;


/**
 * Action for exporting a network view to bitmap or vector graphics.
 * @author Samad Lotia
 */
public class ExportAsGraphicsAction extends CytoscapeAction
{
	private static ExportFilter BMP_FILTER = new BitmapExportFilter("bmp", "BMP");
	private static ExportFilter JPG_FILTER = new BitmapExportFilter("jpg", "JPEG");
	private static ExportFilter PDF_FILTER = new PDFExportFilter();
	private static ExportFilter PNG_FILTER = new BitmapExportFilter("png", "PNG");
	private static ExportFilter SVG_FILTER = new SVGExportFilter();
	private static ExportFilter EPS_FILTER = new PSExportFilter("eps", "EPS");
	private static ExportFilter[] FILTERS = { PDF_FILTER, SVG_FILTER, EPS_FILTER, JPG_FILTER, PNG_FILTER, BMP_FILTER };
	protected static CyLogger logger = CyLogger.getLogger(ExportAsGraphicsAction.class);

	private static String TITLE = "Network View as Graphics";

	public ExportAsGraphicsAction()
	{
		super(TITLE + "...");
		setPreferredMenu("File.Export");
		setAcceleratorCombo(KeyEvent.VK_P, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK);
	}

	public void menuSelected(MenuEvent e)
	{
		enableForNetworkAndView();
	}

	public void actionPerformed(ActionEvent e)
	{

		final CyNetworkView v = Cytoscape.getCurrentNetworkView();
		if ( v == null || v == Cytoscape.getNullNetworkView() ) {
			logger.error("No network view exists to export!");
			return;
		}


		final ExportAsGraphicsFileChooser chooser = new ExportAsGraphicsFileChooser(FILTERS);

		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent event)
			{
				ExportFilter filter = (ExportFilter) chooser.getSelectedFormat();
				filter.setExportTextAsFont(chooser.getExportTextAsFont());
				
				File file = chooser.getSelectedFile();
				
				chooser.dispose();

				FileOutputStream stream = null;
				try
				{
					stream = new FileOutputStream(file);
				}
				catch (Exception exp)
				{
					JOptionPane.showMessageDialog(	Cytoscape.getDesktop(),
														"Could not create file " + file.getName()
														+ "\n\nError: " + exp.getMessage());
					return;
				}
				CyNetworkView view = Cytoscape.getCurrentNetworkView();
				filter.export(view, stream);
			}
		};
		chooser.addActionListener(listener);
		chooser.setVisible(true);
	}	
}


class ExportTask
{
	public static void run(	final String title,
				final Exporter exporter,
				final CyNetworkView view,
				final FileOutputStream stream)
	{
		// Create the Task
		Task task = new Task()
		{
			TaskMonitor monitor;

			public String getTitle()
			{
				return title;
			}

			public void setTaskMonitor(TaskMonitor monitor)
			{
				this.monitor = monitor;
			}

			public void halt()
			{
			}

			public void run()
			{
                try {
                    try {
                        exporter.export(view, stream);
                    }
                    catch (Exception e) {
                        monitor.setException(e, "Could not complete export of network");
                    }
                }
                finally {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (IOException ioe) {
                            ExportAsGraphicsAction.logger.warn("Unable to close the stream: "+ioe.getMessage(), ioe);
                        }
                    }
                }
			}
		};
		
		// Execute the task
		JTaskConfig jTaskConfig = new JTaskConfig();
		jTaskConfig.displayCancelButton(false);
		jTaskConfig.displayCloseButton(false);
		jTaskConfig.displayStatus(false);
		jTaskConfig.displayTimeElapsed(true);
		jTaskConfig.displayTimeRemaining(false);
		jTaskConfig.setAutoDispose(true);
		jTaskConfig.setModal(true);
		jTaskConfig.setOwner(Cytoscape.getDesktop());
		TaskManager.executeTask(task, jTaskConfig);
	}
}

abstract class ExportFilter extends CyFileFilter
{
	protected boolean exportTextAsFont = false;
	public ExportFilter(String extension, String description)
	{
		super(extension, description);
	}

	public boolean isExtensionListInDescription()
	{
		return true;
	}

	public String toString()
	{
		return getDescription();
	}

	public void setExportTextAsFont(boolean pExportTextAsFont) {
		exportTextAsFont = pExportTextAsFont;
	}
	
	public boolean getExportTextAsFont() {
		return exportTextAsFont;
	}
	
	public abstract void export(CyNetworkView view, FileOutputStream stream);
}

class PDFExportFilter extends ExportFilter
{
	public PDFExportFilter()
	{
		super("pdf", "PDF");
	}
	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		PDFExporter exporter = new PDFExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to PDF", exporter, view, stream);
	}
}

class BitmapExportFilter extends ExportFilter
{
	private String extension;

	public BitmapExportFilter(String extension, String description)
	{
		super(extension, description);
		this.extension = extension;
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		final InternalFrameComponent ifc = Cytoscape.getDesktop().getNetworkViewManager().getInternalFrameComponent(view);
		final ExportBitmapOptionsDialog dialog = new ExportBitmapOptionsDialog(ifc.getWidth(), ifc.getHeight());
		ActionListener listener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				BitmapExporter exporter = new BitmapExporter(extension, dialog.getZoom());
				dialog.dispose();
				ExportTask.run("Exporting to " + extension, exporter, view, stream);
			}
		};
		dialog.addActionListener(listener);
		dialog.setVisible(true);
	}
}

class SVGExportFilter extends ExportFilter
{
	public SVGExportFilter()
	{
		super("svg", "SVG");
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		SVGExporter exporter = new SVGExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to SVG", exporter, view, stream);
	}
}

class PSExportFilter extends ExportFilter
{
	public PSExportFilter(String extension, String description)
	{
		super(extension, description);
	}

	public void export(final CyNetworkView view, final FileOutputStream stream)
	{
		PSExporter exporter = new PSExporter();
		exporter.setExportTextAsFont(this.getExportTextAsFont());
		ExportTask.run("Exporting to EPS", exporter, view, stream);
	}
}

