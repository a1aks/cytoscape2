package cytoscape.dialogs.preferences;


import cytoscape.init.CyPropertiesReader;
import cytoscape.CytoscapeInit;

import java.net.URL;
import java.io.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;

public class PreferencesDialog extends JDialog {

    String[] pluginTypes = {"Local", "Remote/URL"};
	
    int [] selection = null;
	
    JScrollPane prefsTablePane   = new JScrollPane();
    JScrollPane pluginsTablePane  = new JScrollPane();
    JTable      pluginsTable      = new JTable();
    JTable      prefsTable       = new JTable();
    JPanel      buttonPane  = new JPanel(new FlowLayout());
    JPanel      buttonPaneA = new JPanel(new FlowLayout());
    JPanel      okButtonPane = new JPanel(new FlowLayout());
    JComboBox   pluginTypesComboBox  = new JComboBox(pluginTypes);
    JButton     addButton   = new JButton("Add");
    JButton     modifyButton= new JButton("Modify");
    JButton     deleteButton   = new JButton("Delete");
    JButton     okButton = new JButton("OK");
    JButton     cancelButton = new JButton("Cancel");
	
    //private Set pluginURLs = new HashSet();

    public PreferenceTableModel prefsTM = null;
    public PluginsTableModel pluginsTM = null;
    private ListSelectionModel lsm = null;
    private ListSelectionModel lsmA = null;
	

    public void setParameter(TableModel tm, String preferenceName,
					String preferenceValue) {
	    // plugins
	    if (tm == pluginsTM) {
		// catch at table - don't allow duplicate values for
		// name/value pair
		if (!inTable(pluginsTM,"Plugin",preferenceValue)) {
	        	pluginsTM.addPlugin(preferenceValue);
		} else {
		// popup info dialog
			JOptionPane.showMessageDialog(this,
				"Plugin already included","Information",
				JOptionPane.INFORMATION_MESSAGE);
		}
	
	    // preferences/properties
	    } else if (tm == prefsTM) {
                prefsTM.setProperty(preferenceName, preferenceValue);
	    }
	        
	    refresh();

	    // reset state of Modify and Delete buttons to inactive
	    // since update of parameter will clear any selections
	    modifyButton.setEnabled(false);
	    deleteButton.setEnabled(false);
    }

   /*
    * check for presence of name/value pair in table/table model
    */
    public boolean inTable(PluginsTableModel tm, String name, String value) {
      int numRows = tm.getRowCount();
      for (int i = 0; i < numRows; i++) {
        if ( ((String)tm.getValueAt(i,0)).equals(value))
            return true;
      }
      return false;
    }



    public void refresh() {
	prefsTM.loadProperties();
        pluginsTM.loadProperties();
        
        prefsTable.setModel(prefsTM);
        pluginsTable.setModel(pluginsTM);

        prefsTable.clearSelection();
	prefsTable.revalidate();
	prefsTable.repaint();
        pluginsTable.clearSelection();
        pluginsTable.revalidate();
        pluginsTable.repaint();
    }
	
	private void initButtonPane() {
		pluginTypesComboBox.setSelectedIndex(0);
		buttonPaneA.add(pluginTypesComboBox); 
		buttonPaneA.add(addButton);
		buttonPane.add(modifyButton);
		buttonPaneA.add(deleteButton);
		okButtonPane.add(okButton);
		okButtonPane.add(cancelButton);

		modifyButton.setEnabled(false);
		deleteButton.setEnabled(false);
		addButton.addActionListener(new AddPropertyListener(this));
		modifyButton.addActionListener(new ModifyPropertyListener(this));
		deleteButton.addActionListener(new DeletePropertyListener(this));
		okButton.addActionListener(new OkButtonListener(this));
		cancelButton.addActionListener(new CancelButtonListener(this));
	}
	
	public PreferenceTableModel getPTM() {
        return prefsTM;
	}
    
	public PluginsTableModel getPTMA() {
        return pluginsTM;
    }
    
    private void initTable() {
	prefsTM = new PreferenceTableModel();
        pluginsTM = new PluginsTableModel();
		
	prefsTable.setAutoCreateColumnsFromModel(false);
        pluginsTable.setAutoCreateColumnsFromModel(false);
	prefsTable.setRowSelectionAllowed(true);
        pluginsTable.setRowSelectionAllowed(true);
	lsm = prefsTable.getSelectionModel( );
	lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lsmA = pluginsTable.getSelectionModel( );
	lsmA.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	lsm.addListSelectionListener(new TableListener(this, lsm));
        lsmA.addListSelectionListener(new TableListenerA(this, lsmA));
        
	
	prefsTable.setModel(prefsTM);
        pluginsTable.setModel(pluginsTM);
		
	for (int i=0;i<PreferenceTableModel.columnHeader.length;i++) {
		DefaultTableCellRenderer renderer = new
			DefaultTableCellRenderer();
		renderer.setHorizontalAlignment(
			PreferenceTableModel.alignment[i]);

		TableColumn Column = new TableColumn(i,
			PreferenceTableModel.columnWidth[i], renderer, null);
		Column.setIdentifier(PreferenceTableModel.columnHeader[i]);
		prefsTable.addColumn(Column);
	}
	for (int i=0;i<PluginsTableModel.columnHeader.length;i++) {
            DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
            renderer.setHorizontalAlignment(PluginsTableModel.alignment[i]);
            TableColumn Column = new TableColumn(i, 
			PreferenceTableModel.columnWidth[i], renderer, null);
            pluginsTable.addColumn(Column);
        }
    }
	
    public PreferencesDialog(Frame owner) {
	super(owner);
	initButtonPane();
	initTable();
	try {
		prefPopupInit();
	} catch (Exception e) {
		e.printStackTrace();
	}

        this.setTitle("Cytoscape Preferences Editor");
	pack();
	//  set location relative to owner/parent
	this.setLocationRelativeTo(owner);
        this.setVisible(true);
    }

    private void prefPopupInit() throws Exception {

	Box outerBox = Box.createVerticalBox();
	outerBox.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

	Box modTableBox = Box.createVerticalBox();
	prefsTablePane.setBorder(BorderFactory.createEmptyBorder(2,9,4,9));
        prefsTablePane.getViewport().add(prefsTable, null);
	prefsTable.setPreferredScrollableViewportSize(new Dimension(500,150));
	modTableBox.add(prefsTablePane);
	modTableBox.add(Box.createVerticalStrut(10));
        modTableBox.add(buttonPane);
	modTableBox.setBorder(BorderFactory.createTitledBorder(
					"Properties"));
	outerBox.add(modTableBox);
	outerBox.add(Box.createVerticalStrut(10));

	Box addTableBox = Box.createVerticalBox();
	pluginsTablePane.setBorder(BorderFactory.createEmptyBorder(2,9,4,9));
        pluginsTablePane.getViewport().add(pluginsTable, null);
	pluginsTable.setPreferredScrollableViewportSize(new Dimension(500,100));
        addTableBox.add(pluginsTablePane);
	addTableBox.add(Box.createVerticalStrut(10));
        addTableBox.add(buttonPaneA);
	addTableBox.setBorder(BorderFactory.createTitledBorder(
					"Plugins"));
	outerBox.add(addTableBox);

	outerBox.add(Box.createVerticalStrut(10));
	JTextArea textArea = new JTextArea(
	  "Note: Changes to these properties and plugins will be saved on " +
	  "application exit and available on next start. To use these new " +
	  "settings now, please exit and restart Cytoscape.");
	textArea.setBackground(outerBox.getBackground());
	textArea.setEditable(false);
	textArea.setDragEnabled(false);
	textArea.setLineWrap(true);
	textArea.setWrapStyleWord(true);
	outerBox.add(textArea);
	outerBox.add(Box.createVerticalStrut(10));
        outerBox.add(okButtonPane);

        this.getContentPane().add(outerBox, BorderLayout.CENTER);
    }

    class AddPropertyListener implements ActionListener {
	PreferencesDialog callerRef = null;
	public AddPropertyListener(PreferencesDialog caller) {
		super();
		callerRef = caller;
	}
		
	public void actionPerformed(ActionEvent e) {
        	PreferenceValueDialog pd = new PreferenceValueDialog(
			PreferencesDialog.this,
			pluginTypes[pluginTypesComboBox.getSelectedIndex()],
			callerRef, pluginsTM);
	}
    }

    class ModifyPropertyListener implements ActionListener {
	PreferencesDialog callerRef = null;
	public ModifyPropertyListener(PreferencesDialog caller) {
		super();
		callerRef = caller;
	}
		
	public void actionPerformed(ActionEvent e) {
	    for (int i=0;i<selection.length;i++) {
		String name = new String((String)(prefsTM.getValueAt(
						selection[i],0)));
		String value = new String((String)(prefsTM.getValueAt(
						selection[i],1)));
	  	PreferenceValueDialog pd = new PreferenceValueDialog(
			PreferencesDialog.this, name, value,
			callerRef, prefsTM);
	    }
	}
    }

    class DeletePropertyListener implements ActionListener {
	PreferencesDialog callerRef = null;
		
	public DeletePropertyListener(PreferencesDialog caller) {
		super();
		callerRef = caller;
	}
		
	public void actionPerformed(ActionEvent e) {
		String plugins[] = new String[selection.length];
		for (int i=0;i<selection.length;i++) {
			plugins[i] = (new String((String)
				(pluginsTM.getValueAt(selection[i],0))));
		}
    		callerRef.pluginsTM.deletePlugins(plugins);

		callerRef.pluginsTable.clearSelection();
		callerRef.deleteButton.setEnabled(false);
		refresh();
	}
    }
	
	class OkButtonListener implements ActionListener {
		PreferencesDialog callerRef = null;
		public OkButtonListener(PreferencesDialog caller) {
			super();
			callerRef = caller;
		}

		public void actionPerformed(ActionEvent e) {
		// just saving via putAll() doesn't handle deletes...
		// therefore use TableModel's putAll() into new Properties obj
		// then clear Cytoscape's properties and
		    Properties newProps = new Properties();
		    callerRef.pluginsTM.save(newProps);
		    callerRef.prefsTM.save(newProps);
		    CytoscapeInit.getProperties().clear();
		    CytoscapeInit.getProperties().putAll(newProps);

		    callerRef.dispose();
		}
	}
	class CancelButtonListener implements ActionListener {
		PreferencesDialog callerRef = null;
		public CancelButtonListener(PreferencesDialog caller) {
		    super();
		    callerRef = caller;
		}
		public void actionPerformed(ActionEvent e) {
		    callerRef.dispose();
		}
	}
	
	class TableListenerA implements ListSelectionListener {
		private ListSelectionModel model = null;
		private PreferencesDialog motherRef = null;

		public TableListenerA(PreferencesDialog mother,
						ListSelectionModel lsm) {
		    motherRef = mother;
			model = lsm;
		}

		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				StringBuffer buf = new StringBuffer();
				selection = getSelectedIndices(
					model.getMinSelectionIndex(),
					model.getMaxSelectionIndex());
				if (selection.length == 0) {
				}
				else {
					int isPlugin = 1;
                    deleteButton.setEnabled(true);
				}
			}
		}

		protected int[] getSelectedIndices(int start, int stop) {
			if ((start == -1) || (stop == -1)) {
				return new int[0];
			}

			int guesses[] = new int [stop-start+1];
			int index = 0;

			for (int i=start;i<=stop;i++) {
				if (model.isSelectedIndex(i)) {
					guesses[index++] = i;
				}
			}
			int realthing[] = new int[index];
			System.arraycopy(guesses, 0, realthing, 0 , index);
			return realthing;
		}
		
		public void actionPerformed(ActionEvent e) {
		}
	}
    
	class TableListener implements ListSelectionListener {
		private ListSelectionModel model = null;
		private PreferencesDialog motherRef = null;

		public TableListener(PreferencesDialog mother,
						ListSelectionModel lsm) {
		    motherRef = mother;
			model = lsm;
		}

		public void valueChanged(ListSelectionEvent lse) {
			if (!lse.getValueIsAdjusting()) {
				StringBuffer buf = new StringBuffer();
				selection = getSelectedIndices(model.getMinSelectionIndex(), model.getMaxSelectionIndex());
				if (selection.length == 0) {
				}
				else {
					modifyButton.setEnabled(true);
				}
			}
		}

		protected int[] getSelectedIndices(int start, int stop) {
			if ((start == -1) || (stop == -1)) {
				return new int[0];
			}

			int guesses[] = new int [stop-start+1];
			int index = 0;

			for (int i=start;i<=stop;i++) {
				if (model.isSelectedIndex(i)) {
					guesses[index++] = i;
				}
			}
			int realthing[] = new int[index];
			System.arraycopy(guesses, 0, realthing, 0 , index);
			return realthing;
		}
		
		public void actionPerformed(ActionEvent e) {
		}
	}
	
}
