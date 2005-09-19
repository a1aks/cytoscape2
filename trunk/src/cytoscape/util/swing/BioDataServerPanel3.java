package cytoscape.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.StringTokenizer;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;


import cytoscape.CytoscapeInit;

/*
 * Final Step for the new file format.
 * This panel creates the manifest file and load the biodataserver.
 */
public class BioDataServerPanel3 extends JPanel {

	private JLabel anotherBlankSpace;

	private JLabel blankSpace;

	private ButtonGroup connectorGroup;

	private JLabel jLabel1;

	private JPanel jPanel1;

	private JLabel progressDescription;

	private JProgressBar progressSent;

	private JLabel welcomeTitle;

	private JLabel yetAnotherBlankSpace1;

	private JPanel contentPanel;

	private JLabel iconLabel;

	private JSeparator separator;

	private JLabel textLabel;

	private JPanel titlePanel;

	private JLabel jLabel2;

	private JLabel jLabel3;

	private JComboBox spList;

	File start;
	
	String species;

	private JTextField taxonNameBox;

	private JButton setButton;
	
	private ButtonGroup selectionType;

	private JRadioButton comboBoxButton;

	private JRadioButton textBoxButton;

	private JPanel jPanel2;

	private JPanel jPanel3;

	private JTextField currentSpBox;
	
	private final String FS = System.getProperty("file.separator");
	
	// lookup table for the taxon number <-> name 
	public final String TAXON_FILE = "tax_report.txt";

	public BioDataServerPanel3() {

		super();

		species = null;
		start = CytoscapeInit.getMRUD();
		spList = new JComboBox();

		contentPanel = getContentPanel();
		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		textLabel = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();

		setLayout(new java.awt.BorderLayout());

		titlePanel.setLayout(new java.awt.BorderLayout());
		titlePanel.setBackground(Color.gray);

		textLabel.setBackground(Color.gray);
		textLabel.setFont(new Font("Sans Serif", Font.BOLD, 14));
		textLabel.setText("Set Species");
		textLabel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));
		textLabel.setOpaque(true);

		iconLabel.setBackground(Color.gray);
		if (icon != null)
			iconLabel.setIcon(icon);

		titlePanel.add(textLabel, BorderLayout.CENTER);
		titlePanel.add(iconLabel, BorderLayout.EAST);
		titlePanel.add(separator, BorderLayout.SOUTH);

		add(titlePanel, BorderLayout.NORTH);
		JPanel secondaryPanel = new JPanel();
		secondaryPanel.add(contentPanel, BorderLayout.NORTH);
		add(secondaryPanel, BorderLayout.WEST);

	}

	public void setProgressText(String s) {
		progressDescription.setText(s);
	}

	public void setProgressValue(int i) {
		progressSent.setValue(i);
	}
	
	public String getSpNameFromComboBox() {
		species = (String)(spList.getSelectedItem());
		return species;
	}
	
	public String getSpNameFromTextBox() {
		species = taxonNameBox.getText();
		return species;
	}
	
	public void addSpButtonActionListener( ActionListener l ) {
        setButton.addActionListener( l );
    }
	
	public void addSpComboBoxActionListener( ActionListener l ) {
        spList.addActionListener(l);
    }
	
	public void addSetButtonActionListener( ActionListener l ) {
        setButton.addActionListener(l);
    }
	
	public void addRadioButtonActionListener( ActionListener l ) {
		comboBoxButton.addActionListener( l );
        textBoxButton.addActionListener(l); 
    }
	
	public Object getRadioButtonSelected() {
		return selectionType.getSelection().getSelectedObjects();
	}
	
	public void setCurrentSpBox( String newSp ) {
		currentSpBox.setText( "Species will be set to " + newSp );
	}

	protected void setSpList(final BufferedReader rd) throws IOException {
		String curLine = null;
		String name1 = null;
		String name2 = null;

		// remove the first line, which is a title
		curLine = rd.readLine();

		while ((curLine = rd.readLine()) != null) {
			StringTokenizer st = new StringTokenizer(curLine, "|");
			st.nextToken();
			name1 = st.nextToken().trim();
			name2 = st.nextToken().trim();
			if (name2.length() > 1) {
				spList.addItem(name2);
			} else {
				spList.addItem(name1);
			}

		}
	}

private JPanel getContentPanel() {
	
	JPanel contentPanel1 = new JPanel();
	String filePath = start + FS +TAXON_FILE;
    File taxonFile = new File(filePath);
    
	if( taxonFile.canRead() == true ) {
		BufferedReader spListReader = null;
        try {
        	spListReader = new BufferedReader(
					new FileReader( filePath ));
        } catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        try {
			setSpList( spListReader );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} else {
		spList.addItem( "Sorry, could not found taxonomy table...");
		spList.addItem( "Please enter species in the box below.");
		
	}
        
		setButton = new JButton("Set Species");
	
        selectionType = new ButtonGroup();
        comboBoxButton = new JRadioButton();
        textBoxButton = new JRadioButton();
        selectionType.add( comboBoxButton );
        selectionType.add( textBoxButton );
        
        comboBoxButton.setText("Please select the species for the data source:");
        comboBoxButton.setName("combo");
        textBoxButton.setText("Or, enter species:");
        textBoxButton.setName("textbox");
        
        
        // Default selection is pull-down menu
        comboBoxButton.setSelected(true);
        
        welcomeTitle = new JLabel();
        jPanel1 = new JPanel();
        blankSpace = new JLabel();
        progressSent = new JProgressBar();
        progressDescription = new JLabel();
        anotherBlankSpace = new JLabel();
        yetAnotherBlankSpace1 = new JLabel();
        jLabel1 = new JLabel();
        jLabel2 = new JLabel();
        jLabel3 = new JLabel();
        
        jPanel2 = new JPanel();
        jPanel3 = new JPanel();
        
        taxonNameBox = new JTextField();
        taxonNameBox.setEnabled( false );
        
        currentSpBox = new JTextField(50);
        currentSpBox.setEditable( false );
        
        //test
        spList.setEnabled(true);
        species = (String)(spList.getSelectedItem());
        contentPanel1.setLayout(new java.awt.BorderLayout());

        welcomeTitle.setText("Specify the Species...");
        contentPanel1.add(welcomeTitle, java.awt.BorderLayout.NORTH);
        
        jLabel2.setText("Please select the species for the data source:");
        jLabel3.setText("Or, enter species:");
        
        jPanel1.setLayout(new java.awt.GridLayout(8, 0));
        
        /*
        jPanel2.add(comboBoxButton);
        jPanel2.add(jLabel2);
        jPanel3.add(textBoxButton);
        jPanel3.add(jLabel3);
        */
        
        currentSpBox.setText("Species will be set to " + spList.getSelectedItem() );
        
        jPanel1.add(blankSpace);
        jPanel1.add(comboBoxButton);
        jPanel1.add(spList);

        jPanel1.add(anotherBlankSpace);
        jPanel1.add(textBoxButton);
        jPanel1.add(taxonNameBox);
        jPanel1.add(setButton);
        jPanel1.add(currentSpBox);
        contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);

        jLabel1.setText("Please press the Finish button to load the files.");
        contentPanel1.add(jLabel1, java.awt.BorderLayout.SOUTH);
        
        return contentPanel1;
    }	

	public void setComboBoxState( boolean state ) {
		spList.setEnabled( state );
	}

	public void setTextBoxState( boolean state ) {
		taxonNameBox.setEnabled( state );
	}

	
	private ImageIcon getImageIcon() {
		return null;
	}

}
