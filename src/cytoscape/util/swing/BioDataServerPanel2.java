package cytoscape.util.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.JLabel;

import cytoscape.CytoscapeInit;


public class BioDataServerPanel2 extends JPanel {

	private javax.swing.JLabel anotherBlankSpace;

	private javax.swing.JLabel blankSpace;

	private javax.swing.ButtonGroup connectorGroup;


	private javax.swing.JCheckBox jCheckBox1;

	private javax.swing.JLabel jLabel1;

	private javax.swing.JPanel jPanel1;
	private javax.swing.JPanel jPanel2;

	private javax.swing.JLabel panelTitle;

	private javax.swing.JRadioButton wirelessRadioButton;

	private javax.swing.JLabel yetAnotherBlankSpace1;

	private JPanel contentPanel;

	private JLabel iconLabel;

	private JSeparator separator;

	private JLabel textLabel;

	private JPanel titlePanel;
	
	private JFileChooser jc1;
	
	private JTextField oboFileName;
	private JTextField gaFileName;
	private JButton selectOboFile;
	private JButton selectGaFile;

	private Container jPanel3;

	private JFileChooser jc2;

	private JCheckBox flipCheck;
	
	File start;
	
	/* 
	 * This panel is for old biodataserver format.
	 * Old files always cpntains species in the file, we do not need to
	 * care about it.
	 */
	public BioDataServerPanel2() {
		super();

		// get default dir of cytoscape.
		start = CytoscapeInit.getMRUD();
		
		contentPanel = getContentPanel();
		contentPanel.setBorder(new EmptyBorder(new Insets(10, 10, 10, 10)));

		ImageIcon icon = getImageIcon();

		titlePanel = new javax.swing.JPanel();
		textLabel = new javax.swing.JLabel();
		iconLabel = new javax.swing.JLabel();
		separator = new javax.swing.JSeparator();

		setLayout(new java.awt.BorderLayout());

		titlePanel.setLayout(new java.awt.BorderLayout());
		titlePanel.setBackground(Color.gray);

		textLabel.setBackground(Color.gray);
		textLabel.setFont(new Font("Sans Serif", Font.BOLD, 15));
		textLabel.setText("Select Ontology And Annotation File");
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

	public void addButtonActionListener( ActionListener l ) {
        selectOboFile.addActionListener(l);
        selectGaFile.addActionListener(l);
    }
	
	public void addCheckBoxActionListener( ActionListener l ) {
        flipCheck.addActionListener(l);
    }
	
	private JPanel getContentPanel() {

		JPanel contentPanel1 = new JPanel();
		
		oboFileName = new JTextField(20);
		
		// Button to load the Obo file.  This becomes the command in the
		// action listner.
		selectOboFile = new JButton("Obo");
		
		gaFileName = new JTextField(20);
		
		// Button to load the Gene Assiciation file.
		selectGaFile = new JButton("Gene Association");
		
		panelTitle = new javax.swing.JLabel();
		
		blankSpace = new javax.swing.JLabel();
		
		// This is for check flip or not.
		flipCheck = new JCheckBox();
		flipCheck.setText("Flip DB Object Symbol <-> DB Object Synonym?");
		//flipCheck.setHorizontalTextPosition( JCheckBox.CENTER );
		
		
		jPanel1 = new JPanel();
		jPanel2 = new JPanel();
		jPanel3 = new JPanel();
		
		jPanel1.setLayout( new BorderLayout() );
		jPanel2.setLayout( new GridLayout(2, 0) );
		jPanel3.setLayout( new GridLayout(2, 0) );
		//jc1.addChoosableFileFilter( oboFilter );

		anotherBlankSpace = new javax.swing.JLabel();

		contentPanel1.setLayout(new java.awt.BorderLayout());

		panelTitle
				.setText("Please push the button to select ontology and annotation file:");
		jPanel2.add(oboFileName);
		jPanel3.add(selectOboFile);
		jPanel2.add(gaFileName);
		jPanel3.add(selectGaFile);
		
		jPanel1.add(jPanel2, BorderLayout.WEST );
		jPanel1.add(jPanel3, BorderLayout.CENTER );
		jPanel1.add( anotherBlankSpace, BorderLayout.SOUTH );
		
		contentPanel1.add(panelTitle, java.awt.BorderLayout.NORTH);
		contentPanel1.add(jPanel1, java.awt.BorderLayout.CENTER);
		contentPanel1.add(flipCheck, java.awt.BorderLayout.SOUTH);
		//contentPanel1.add(jc1, java.awt.BorderLayout.CENTER);
		
		return contentPanel1;
	}
	
	public String getOboTextField() {
		return oboFileName.getText();
	}
	
	public String getGaTextField() {
		return gaFileName.getText();
	}
	
	/*
	 * Create file choosers for obo and gene association files.
	 */
	public void createOboFileChooser() {
		jc1 = new JFileChooser( start );
		jc1.setDialogTitle("Select OBO File");
	}
	
	public void createGaFileChooser() {
		jc2 = new JFileChooser( start );
		jc2.setDialogTitle("Select Gene Association File");
	}
	
	/*
	 * Display file chooser and return name of the OBO file.
	 */
	public File getOboFile( boolean show ) {
		File targetFile = null;
		if( show == true ) {
			int result = jc1.showOpenDialog(null);
		}
		targetFile = jc1.getSelectedFile();
		return targetFile;
	}
	
	public File getGaFile( boolean show ) {
		File targetFile = null;
		if( show == true ) {
			int result = jc2.showOpenDialog(null);
		}
		targetFile = jc2.getSelectedFile();
		return targetFile;
	}
	
	
	public boolean getCheckBoxStatus() {
		return flipCheck.isSelected();
	}
	
	public void setOboFileName( String fileName ) {
		oboFileName.setText( fileName );
	}
	
	public void setGaFileName( String fileName ) {
		gaFileName.setText( fileName );
	}
	
	
	
	
	private ImageIcon getImageIcon() {

		// Icon to be placed in the upper right corner.

		return null;
	}

}
