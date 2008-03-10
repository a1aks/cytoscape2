// vim: set ts=2: */
package cytoscape.layout;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Color;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import javax.swing.BoxLayout;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;


/**
 * The Tunable class provides a convenient way to encapsulate
 * CyLayoutAlgorithm property and settings values.  Each Tunable
 * has a name, which corresponds to the property name, a description,
 * which is used as the label in the settings dialog, a type, a
 * value, and information about the value, such as a list of options
 * or the lower and upper bounds for the value.  These are meant
 * to be used as part of the LayoutSettingsDialog (see getPanel).
 */
public class Tunable {
	private String name;
	private String desc;
	private int type = STRING;
	private int flag = 0;
	private Object value;
	private Object lowerBound;
	private Object upperBound;
	private JComponent inputField;
	private boolean valueChanged = true;
	
	private boolean immutable = false;

	/**
	 * Types
	 */
	final public static int INTEGER = 0;

	/**
	 *
	 */
	final public static int DOUBLE = 1;

	/**
	 *
	 */
	final public static int BOOLEAN = 2;

	/**
	 *
	 */
	final public static int STRING = 3;

	/**
	 *
	 */
	final public static int NODEATTRIBUTE = 4;

	/**
	 *
	 */
	final public static int EDGEATTRIBUTE = 5;

	/**
	 *
	 */
	final public static int LIST = 6;

	/**
	 *
	 */
	final public static int GROUP = 7;

	/**
	 * Flags
	 */
	final public static int NOINPUT = 0x1;

	/**
	 *
	 */
	final public static int NUMERICATTRIBUTE = 0x2;

	/**
	 * Constructor to create a Tunable with no bounds
	 * information, and no flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable
	 */
	public Tunable(String name, String desc, int type, Object value) {
		this(name, desc, type, value, null, null, 0);
	}

	/**
	 * Constructor to create a Tunable with no bounds
	 * information, but with a flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable
	 * @param flag The initial value of the flag.  This can be
	 *             used to indicate that this tunable is not user
	 *             changeable (e.g. debug), or to indicate if there
	 *             is a specific type for the attributes.
	 */
	public Tunable(String name, String desc, int type, Object value, int flag) {
		this(name, desc, type, value, null, null, flag);
	}

	/**
	 * Constructor to create a Tunable with bounds
	 * information as well as a flag.
	 *
	 * @param name The name of the Tunable
	 * @param desc The description of the Tunable
	 * @param type Integer value that represents the type of
	 *             the Tunable.  The type not only impact the
	 *             way that the value is interpreted, but also
	 *             the component used for the LayoutSettingsDialog
	 * @param value The initial (default) value of the Tunable.  This
	 *             is a String in the case of an EDGEATTRIBUTE or
	 *             NODEATTRIBUTE tunable, it is an Integer index
	 *             a LIST tunable.
	 * @param lowerBound An Object that either represents the lower
	 *             bounds of a numeric Tunable or an array of values
	 *             for an attribute (or other type of) list.
	 * @param upperBound An Object that represents the upper bounds
	 *             of a numeric Tunable.
	 * @param flag The initial value of the flag.  This can be
	 *             used to indicate that this tunable is not user
	 *             changeable (e.g. debug), or to indicate if there
	 *             is a specific type for the attributes.
	 */
	public Tunable(String name, String desc, int type, Object value, Object lowerBound,
	               Object upperBound, int flag) {
		this(name, desc, type, value, lowerBound, upperBound, flag, false);
	}
	
	public Tunable(String name, String desc, int type, Object value, Object lowerBound,
            Object upperBound, int flag, boolean immutable) {
		this.name = name;
		this.desc = desc;
		this.type = type;
		this.value = value;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
		this.flag = flag;
		this.immutable = immutable;
		// System.out.println("Tunable "+desc+" has value "+value);
	}

	/**
	 * This method can be used to set a flag for this Tunable
	 *
	 * @param flag integer value the contains the flag to set.
	 */
	public void setFlag(int flag) {
		this.flag |= flag;
	}

	/**
	 * This method can be used to clear a flag for this Tunable
	 *
	 * @param flag integer value the contains the flag to be cleared.
	 */
	public void clearFlag(int flag) {
		this.flag &= ~flag;
	}

	/**
	 * This method is used to set the value for this Tunable.  If
	 * this is an INTEGER, DOUBLE, or BOOLEAN Tunable, then value
	 * is assumed to be a String.  This also sets the "changed" state
	 * of the value to "true".
	 *
	 * @param value Object (usually String) containing the value to be set
	 */
	public void setValue(Object value) {
		// Did the user hand us a string representation?
		if (value.getClass() == String.class) {
			switch (type) {
				case INTEGER:
					// System.out.println("Setting Integer tunable "+desc+" value to "+value);
					this.value = new Integer((String) value);
					break;

				case DOUBLE:
					// System.out.println("Setting Double tunable "+desc+" value to "+value);
					this.value = new Double((String) value);
					break;

				case BOOLEAN:
					// System.out.println("Setting Boolean tunable "+desc+" value to "+value);
					this.value = new Boolean((String) value);
					break;

				case LIST:
					// System.out.println("Setting List tunable "+desc+" value to "+value);
					this.value = new Integer((String) value);
					return;

				case GROUP:
					// System.out.println("Setting Group tunable "+desc+" value to "+value);
					this.value = new Integer((String) value);
					return;

				default:
					// System.out.println("Setting String tunable "+desc+" value to "+value);
					this.value = value;
					break;
			}
		} else {
			this.value = value;
		}

		valueChanged = true;
	}

	/**
	 * This method returns the current value.  This method
	 * also resets the state of the value to indicate that
	 * it has not been changed since the last "get".
	 *
	 * @return Object that contains the value for this Tunable
	 */
	public Object getValue() {
		valueChanged = false;

		return value;
	}

	/**
	 * Returns the changed state of the value.  If true,
	 * the value has been changed since it was last retrieved.
	 *
	 * @return boolean value of the changed state.
	 */
	public boolean valueChanged() {
		return valueChanged;
	}

	/**
	 * Method to set the lowerBound for this Tunable.  This might be used to change a Tunable
	 * based on changes in the plugin environment.
	 *
	 * @param lowerBound the new lowerBound for the tunable
	 */
	public void setLowerBound(Object lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Method to get the lowerBound for this Tunable.
	 *
	 * @return the lowerBound the tunable
	 */
	public Object getLowerBound() {
		return this.lowerBound;
	}

	/**
	 * Method to set the upperBound for this Tunable.  This might be used to change a Tunable
	 * based on changes in the plugin environment.
	 *
	 * @param upperBound the new upperBound for the tunable
	 */
	public void setUpperBound(Object upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Method to get the upperBound for this Tunable.
	 *
	 * @return the upperBound the tunable
	 */
	public Object getUpperBound() {
		return this.upperBound;
	}

	/**
	 * Method to return a string representation of this Tunable,
	 * which is essentially its name.
	 *
	 * @return String value of the name of the Tunable.
	 */
	public String toString() {
		return name;
	}

	/**
	 * Method to return a string representation of this Tunable,
	 * which is essentially its name.
	 *
	 * @return String value of the name of the Tunable.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Method to return the type of this Tunable.
	 *
	 * @return Tunable type
	 */
	public int getType() {
		return type;
	}

	/**
	 * Method to return the description for this Tunable.
	 *
	 * @return Tunable description
	 */
	public String getDescription() {
		return desc;
	}

	/**
	 * This method returns a JPanel suitable for inclusion in the
	 * LayoutSettingsDialog to represent this Tunable.  Note that
	 * while the type of the widgets used to represent the Tunable
	 * are customized to represent the type, no ActionListeners are
	 * included.  The dialog must call updateSettings to set the
	 * value of the Tunable from the user input data.
	 *
	 * @return JPanel that can be used to enter values for this Tunable
	 */
	public JPanel getPanel() {
		if ((flag & NOINPUT) != 0)
			return null;

		if (type == GROUP) {
			JPanel tunablesPanel = new JPanel();
			BoxLayout box = new BoxLayout(tunablesPanel, BoxLayout.Y_AXIS);
			tunablesPanel.setLayout(box);

			// Special case for groups
			Border refBorder = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
			TitledBorder titleBorder = BorderFactory.createTitledBorder(refBorder, this.desc);
			titleBorder.setTitlePosition(TitledBorder.LEFT);
			titleBorder.setTitlePosition(TitledBorder.TOP);
			tunablesPanel.setBorder(titleBorder);

			return tunablesPanel;
		}

		JPanel tunablePanel = new JPanel(new BorderLayout(0, 1));
		tunablePanel.add(new JLabel(desc), BorderLayout.LINE_START);

		if ((type == DOUBLE) || (type == INTEGER)) {
			JTextField field = new JTextField(value.toString(), 8);
			field.setHorizontalAlignment(JTextField.RIGHT);
			inputField = field;
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == BOOLEAN) {
			JCheckBox box = new JCheckBox();
			box.setSelected(((Boolean) value).booleanValue());
			inputField = box;
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == NODEATTRIBUTE) {
			CyAttributes nodeAttributes = Cytoscape.getNodeAttributes();
			inputField = getAttributePanel(nodeAttributes);
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == EDGEATTRIBUTE) {
			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();
			inputField = getAttributePanel(edgeAttributes);
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == LIST) {
			inputField = getListPanel((Object[]) lowerBound);
			tunablePanel.add(inputField, BorderLayout.LINE_END);
		} else if (type == STRING) {
			JTextField field = new JTextField(value.toString(), 20);
			field.setHorizontalAlignment(JTextField.RIGHT);
			inputField = field;
		}

		// Added by kono
		// This allow immutable value.
		if(immutable) {
			inputField.setEnabled(false);
		}
		inputField.setBackground(Color.white);
		return tunablePanel;
	}

	/**
	 * This method is used by getPanel to construct a JComboBox that
	 * contains a list of node or edge attributes that the user can
	 * choose from for doing attribute-dependent layouts.
	 *
	 * @param attributes CyAttributes of the appropriate (edge or node) type
	 * @return a JComboBox with an entry for each attribute
	 */
	private JComboBox getAttributePanel(CyAttributes attributes) {
		final String[] attList = attributes.getAttributeNames();
		final List<Object> list = new ArrayList<Object>();

		// See if we have any initial attributes (mapped into lowerBound)
		if (lowerBound != null) {
			list.addAll((List) lowerBound);
		}

		for (int i = 0; i < attList.length; i++) {
			// Is this attribute user visible?
			if (!attributes.getUserVisible(attList[i]))
				continue;

			byte type = attributes.getType(attList[i]);

			if (((flag & NUMERICATTRIBUTE) == 0)
			    || ((type == CyAttributes.TYPE_FLOATING) 
			    || (type == CyAttributes.TYPE_INTEGER))) {
				list.add(attList[i]);
			}
		}

		// Set our current value as selected
		JComboBox box = new JComboBox(list.toArray());
		box.setSelectedItem((String) value);

		return box;
	}

	/**
	 * This method is used by getPanel to construct a JComboBox that
	 * contains a list of values the user can choose from.
	 *
	 * @param list Array of Objects containing the list
	 * @return a JComboBox with an entry for each item on the list
	 */
	private JComboBox getListPanel(Object[] list) {
		// Set our current value as selected
		JComboBox box = new JComboBox(list);
		box.setSelectedIndex(((Integer) value).intValue());

		return box;
	}

	/**
	 * This method is called to extract the user-entered data from the
	 * JPanel and store it as our value.
	 */
	public void updateValue() {
		Object newValue;

		if (inputField == null || type == GROUP)
			return;

		if (type == DOUBLE) {
			newValue = new Double(((JTextField) inputField).getText());
		} else if (type == INTEGER) {
			newValue = new Integer(((JTextField) inputField).getText());
		} else if (type == BOOLEAN) {
			newValue = new Boolean(((JCheckBox) inputField).isSelected());
		} else if (type == LIST) {
			newValue = new Integer(((JComboBox) inputField).getSelectedIndex());
		} else if ((type == NODEATTRIBUTE) || (type == EDGEATTRIBUTE)) {
			newValue = (String) ((JComboBox) inputField).getSelectedItem();
		} else {
			newValue = ((JTextField) inputField).getText();
		}

		if (!value.equals(newValue)) {
			valueChanged = true;
		}

		value = newValue;
	}
}
