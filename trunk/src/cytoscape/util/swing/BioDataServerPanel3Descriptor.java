package cytoscape.util.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import com.nexes.wizard.WizardPanelDescriptor;

public class BioDataServerPanel3Descriptor 
	extends WizardPanelDescriptor implements ActionListener{

	public static final String IDENTIFIER = "SERVER_CONNECT_PANEL";

	BioDataServerPanel3 panel3;
	
	String spName;
	

	public BioDataServerPanel3Descriptor() {

		panel3 = new BioDataServerPanel3();
		panel3.addSpComboBoxActionListener(this);
		panel3.addRadioButtonActionListener(this);
		panel3.addSetButtonActionListener(this);
		
		setPanelDescriptorIdentifier(IDENTIFIER);
		setPanelComponent(panel3);
		
		// Set default name
		spName = panel3.getSpNameFromComboBox();
	}

	public Object getNextPanelDescriptor() {
		return FINISH;
	}

	public Object getBackPanelDescriptor() {
		return BioDataServerPanel2Descriptor.IDENTIFIER;
	}

	public void aboutToDisplayPanel() {
		//System.out.println("Creating Manifest...");
		getWizard().setNextFinishButtonEnabled(true);
		getWizard().setBackButtonEnabled(true);
	}

	
	private void setNextButtonAccordingToSpecies() {
		String selection = (String)panel3.getRadioButtonSelected();
		if( selection.equals("Or, enter species:") ){
			spName = panel3.getSpNameFromTextBox();
		} else if( selection.equals("Please select the species for the data source:") ){
			spName = panel3.getSpNameFromComboBox();
		}
		//System.out.println("Finally, Sp name is : " + spName );
		
        if ( spName.equals("") ) {
           getWizard().setNextFinishButtonEnabled(false);
        } else {
           getWizard().setNextFinishButtonEnabled(true);
        }
   }
	
	public String getSpeciesName() {
		//System.out.println("Finally, Sp name is : " + spName );
		return spName;
	}

	
	public void actionPerformed(ActionEvent e) {
		//System.out.println("+Action! Command is: " + e.getActionCommand());
		//System.out.println("+Action! source is: " + e.getSource().toString() );
		
		if( e.getActionCommand().equals("comboBoxChanged") ) {
			spName = panel3.getSpNameFromComboBox();
			panel3.setCurrentSpBox( spName );
			
		} else if( e.getActionCommand().equals("Or, enter species:") ){
			panel3.setTextBoxState( true );
			panel3.setComboBoxState( false );
			spName = panel3.getSpNameFromTextBox();
			panel3.setCurrentSpBox( spName );
		} else if( e.getActionCommand().equals("Please select the species for the data source:") ){
			panel3.setTextBoxState( false );
			panel3.setComboBoxState( true );
			spName = panel3.getSpNameFromComboBox();
			panel3.setCurrentSpBox( spName );
		} else {
			spName = panel3.getSpNameFromTextBox();
			panel3.setCurrentSpBox( spName );
		}
		//System.out.println("Sp name is : " + spName );
	}
	
	public void aboutToHidePanel() {
		// Can do something here, but we've chosen not not.
	}
}
