/*
 Copyright (c) 2006, 2007, 2010 The Cytoscape Consortium (www.cytoscape.org)

 This library is free software; you can redistribute it and/or modify it
 under the terms of the GNU Lesser General Public License as published
 by the Free Software Foundation; either version 2.1 of the License, or
 any later version.

 This library is distributed in the hope that it will be useful, but
 WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 documentation provided hereunder is on an "as is" basis, and the
 Institute for Systems Biology and the Whitehead Institute
 have no obligations to provide maintenance, support,
 updates, enhancements or modifications.  In no event shall the
 Institute for Systems Biology and the Whitehead Institute
 be liable to any party for direct, indirect, special,
 incidental or consequential damages, including lost profits, arising
 out of the use of this software and its documentation, even if the
 Institute for Systems Biology and the Whitehead Institute
 have been advised of the possibility of such damage.  See
 the GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with this library; if not, write to the Free Software Foundation,
 Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
*/
package browser;


import static browser.DataObjectType.NETWORK;
import giny.model.GraphObject;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.cytoscape.equations.Equation;

import browser.ui.CyAttributeBrowserTable;
import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.GlobalAppearanceCalculator;


/**
 *
 * Actual data manipulation is implemented here.<br>
 *
 * @author kono
 * @author xmas
 * @author ruschein
 */
public class DataTableModel extends DefaultTableModel implements SortTableModel {
	
	private static final long serialVersionUID = -8721633060696752335L;
	
	public static final String LS = System.getProperty("line.separator");
	private static final Boolean DEFAULT_FLAG = false;

	// Property for this browser. One for each panel.
	private Properties props;

	// Type of the object
	private final DataObjectType objectType;

	// Target CyAttributes
	private final CyAttributes data;

	// Currently selected data objects
	private List<GraphObject> graphObjects;

	// Ordered list of attribute names shown as column names.
	private List<String> attributeNames;
	private final GlobalAppearanceCalculator gac = Cytoscape.getVisualMappingManager()
	                                                        .getVisualStyle()
	                                                        .getGlobalAppearanceCalculator();

	/*
	 * Selected nodes & edges color
	 */
	private Color selectedNodeColor;
	private Color selectedEdgeColor;

	// will be used by internal selection.
	private Map<String, Boolean> internalSelection = new HashMap<String, Boolean>();

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param attributeNames  DOCUMENT ME!
	 * @param type  DOCUMENT ME!
	 */
	public DataTableModel(final List<String> attributeNames, final DataObjectType type) {
		this(null, attributeNames, type);
	}

	/**
	 * Creates a new DataTableModel object.
	 *
	 * @param graph_objects  DOCUMENT ME!
	 * @param attributeNames  DOCUMENT ME!
	 * @param type  DOCUMENT ME!
	 */
	public DataTableModel(final List<GraphObject> graph_objects, final List<String> attributeNames,
	                      final DataObjectType type)
	{
		this.data = type.getAssociatedAttributes();
		this.graphObjects = graph_objects;
		this.attributeNames = attributeNames;
		this.objectType = type;

		props = new Properties();
		props.setProperty("colorSwitch", "off");
		setSelectedColor(CyAttributeBrowserTable.SELECTED_NODE);
		setSelectedColor(CyAttributeBrowserTable.SELECTED_EDGE);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 */
	public void setSelectedColor(final int type) {
		switch (type) {
			case CyAttributeBrowserTable.SELECTED_NODE:
				selectedNodeColor = gac.getDefaultNodeSelectionColor();

				break;

			case CyAttributeBrowserTable.SELECTED_EDGE:
				selectedEdgeColor = gac.getDefaultEdgeSelectionColor();

				break;

			default:
				break;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param type DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Color getSelectedColor(final int type) {
		final Color newColor;

		switch (type) {
			case CyAttributeBrowserTable.SELECTED_NODE:
				newColor = gac.getDefaultNodeSelectionColor();
				break;
			case CyAttributeBrowserTable.SELECTED_EDGE:
				newColor = gac.getDefaultEdgeSelectionColor();
				break;
			default:
				newColor = null;
				break;
		}

		return newColor;
	}

	protected void setColorSwitch(final boolean on) {
		props.setProperty("colorSwitch", on ? "on" : "off");
	}

	protected boolean getColorSwitch() {
		return props.getProperty("colorSwitch").equals("on");
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param key DOCUMENT ME!
	 * @param flag DOCUMENT ME!
	 */
	public void setSelectionArray(final String key, final boolean flag) {
		internalSelection.put(key, new Boolean(flag));
	}

	/**
	 *  DOCUMENT ME!
	 */
	public void resetSelectionFlags() {
		if (this.objectType != NETWORK) {
			for (GraphObject gObj : graphObjects) {
				internalSelection.put(gObj.getIdentifier(), DEFAULT_FLAG);
			}
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getObjects() {
		return graphObjects;
	}

	
	public void setTableData(List cellData, List<String> attributes) {
		if (attributes != null)
			this.attributeNames = attributes;

		if (cellData != null)
			graphObjects = cellData;

		if (objectType != NETWORK)
			setTableData();
		else
			setNetworkTable();
	}

	protected void setNetworkTable() {
		if (Cytoscape.getCurrentNetwork() == null) {
			return;
		}

		final int att_length = attributeNames.size();

		// Attribute names will be the row id, and num. of column is always
		Object[][] data_vector = new Object[att_length][2];
		Object[] column_names = new Object[2];

		column_names[0] = "Network Attribute Name";
		column_names[1] = "Value";

		for (int i = 0; i < att_length; i++) {
			final String attributeName = (String) attributeNames.get(i);
			data_vector[i][0] = new ValidatedObjectAndEditString(attributeName);
			data_vector[i][1] = getValidatedObjectAndEditString(data.getType(attributeName),
									    Cytoscape.getCurrentNetwork().getIdentifier(),
									    attributeName);
		}

		setDataVector(data_vector, column_names);
	}

	protected void setAllNetworkTable() {
		int att_length = attributeNames.size() + 1;
		int networkCount = Cytoscape.getNetworkSet().size();

		Object[][] data_vector = new Object[networkCount][att_length];
		Object[] column_names = new Object[att_length];
		column_names[0] = AttributeBrowser.ID;

		internalSelection = new HashMap();

		Iterator it = Cytoscape.getNetworkSet().iterator();
		int k = 0;

		while (it.hasNext()) {
			CyNetwork network = (CyNetwork) it.next();
			String id = network.getIdentifier();

			data_vector[k][0] = new ValidatedObjectAndEditString(id);
			k++;
		}

		// Set actual data
		for (int idx = 0; idx < attributeNames.size(); ++idx) {
			int i = idx + 1;
			column_names[i] = attributeNames.get(idx);

			String attributeName = (String) attributeNames.get(idx);

			byte type = data.getType(attributeName);
			it = Cytoscape.getNetworkSet().iterator();

			int j = 0;

			while (it.hasNext()) {
				CyNetwork network = (CyNetwork) it.next();
				Object value = getValidatedObjectAndEditString(type, network.getIdentifier(), attributeName);

				data_vector[j][i] = value;
				j++;
			}
		}

		setDataVector(data_vector, column_names);
	}

	/**
	 *  Method to fill in table cells.
	 */
	public void setTableData() {
		if (graphObjects == null)
			return;

		internalSelection = new HashMap<String, Boolean>();

		NodeView nv;
		EdgeView edgeView;
		final CyNetworkView netView = Cytoscape.getCurrentNetworkView();

		if (Cytoscape.getCurrentNetworkView() != Cytoscape.getNullNetworkView()) {
			for (GraphObject obj : graphObjects) {
				internalSelection.put(obj.getIdentifier(), DEFAULT_FLAG);
			}
		}

		// Selected attributes + ID
		final int att_length = attributeNames.size();

		// Number of selected objects.
		final int go_length = graphObjects.size();

		Object[][] data_vector;
		Object[] column_names;

		String attributeName;
		byte type;

		// ID only.
		if (att_length == 0) {
			data_vector = new Object[go_length][1];
			column_names = new Object[1];
			column_names[0] = AttributeBrowser.ID;

			for (int j = 0; j < go_length; ++j)
				data_vector[j][0] = new ValidatedObjectAndEditString(graphObjects.get(j).getIdentifier());

			setDataVector(data_vector, column_names);
			//			Cytoscape.getDesktop().getSwingPropertyChangeSupport()
			//			         .firePropertyChange(CyAttributeBrowserTable.RESTORE_COLUMN, null, null);
			AttributeBrowser.getPropertyChangeSupport()
			                      .firePropertyChange(AttributeBrowser.RESTORE_COLUMN, null,
			                                          objectType);

			return;
		} else if (attributeNames.contains(AttributeBrowser.ID) == false) {
			data_vector = new Object[go_length][att_length + 1];
			column_names = new Object[att_length + 1];

			column_names[0] = AttributeBrowser.ID;

			for (int j = 0; j < go_length; ++j)
				data_vector[j][0] = new ValidatedObjectAndEditString(graphObjects.get(j).getIdentifier());

			for (int i1 = 0; i1 < att_length; ++i1) {
				column_names[i1 + 1] = attributeNames.get(i1);
				attributeName = attributeNames.get(i1);
				type = data.getType(attributeName);

				for (int j = 0; j < go_length; ++j) {
					data_vector[j][i1 + 1] = getValidatedObjectAndEditString(type,
					                                                         graphObjects.get(j).getIdentifier(),
					                                                         attributeName);
				}
			}
		} else {
			data_vector = new Object[go_length][att_length];
			column_names = new Object[att_length];

			for (int i1 = 0; i1 < att_length; ++i1) {
				column_names[i1] = attributeNames.get(i1);
				attributeName = (String) attributeNames.get(i1);
				type = data.getType(attributeName);
				for (int j = 0; j < go_length; ++j) {
					if (attributeName.equals(AttributeBrowser.ID))
						data_vector[j][i1] = new ValidatedObjectAndEditString(graphObjects.get(j).getIdentifier());
					else
						data_vector[j][i1] =
							getValidatedObjectAndEditString(type, graphObjects.get(j).getIdentifier(),
						                                        attributeName);
				}
			}
		}

		setDataVector(data_vector, column_names);

		AttributeBrowser.getPropertyChangeSupport()
		                      .firePropertyChange(AttributeBrowser.RESTORE_COLUMN, null, objectType);
	}

	/**
	 *  Returns a validated object and edit string which is a data structure used to display values or error messages in a
	 *  browser cell.
	 *
	 * @param type      the expected data type for the attribute
	 * @param id        the key representing the particular  node/edge/network
	 * @param attrName  which attribute we're dealing with
	 *
	 * @return  DOCUMENT ME!
	 */
	public ValidatedObjectAndEditString getValidatedObjectAndEditString(final byte type, final String id, final String attrName) {
		final Object attribValue = data.getAttribute(id, attrName);
		final Equation equation = data.getEquation(id, attrName);
		if (attribValue == null && equation == null)
			return null;

		final String equationFormula = equation == null ? null : equation.toString();
		String errorMessage = data.getLastEquationError();
		if (errorMessage != null)
			errorMessage = "#ERROR(" + errorMessage + ")";

		switch (type) {
		case CyAttributes.TYPE_INTEGER:
		case CyAttributes.TYPE_FLOATING:
		case CyAttributes.TYPE_STRING:
		case CyAttributes.TYPE_BOOLEAN:
		case CyAttributes.TYPE_SIMPLE_LIST:
			return new ValidatedObjectAndEditString(attribValue, equationFormula, errorMessage);
		case CyAttributes.TYPE_SIMPLE_MAP:
			return new ValidatedObjectAndEditString(attribValue);
		default:
			return null;
		}
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public List getGraphObjects() {
		return graphObjects;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param colName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Class getObjectTypeAt(String colName) {
		return CyAttributesUtils.getClass(colName, data);
	}

	public byte getObjectTypeAt(int col) {
		String columnName = getColumnName(col);
		return data.getType(columnName);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isSortable(int col) {
		return true;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param col DOCUMENT ME!
	 * @param ascending DOCUMENT ME!
	 */
	public void sortColumn(int col, boolean ascending) {
		Collections.sort(getDataVector(), new ColumnComparator(col, getObjectTypeAt(col), ascending));
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param rowIndex DOCUMENT ME!
	 * @param colIndex DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public boolean isCellEditable(int rowIndex, int colIndex) {
		if (!data.getUserEditable(getColumnName(colIndex)))
			return false;

		Class objectType = null;
		Object selectedObj = this.getValueAt(rowIndex, colIndex);

		if ((selectedObj == null) && (colIndex != 0)) {
			return true;
		} else if (selectedObj != null) {
			objectType = this.getValueAt(rowIndex, colIndex).getClass();
		}

		if (objectType != null) {
			if (colIndex == 0)
				return false;
			else
				return true;
		} else
			return false;
	}

	/**
	 * Instead of using a listener, just overwrite this method to save time and
	 * write to the temp object
	 */
	public void setValueAt(final Object newValue, int rowIdx, int colIdx) {
		final int keyIndex = getKeyIndex();
		if (keyIndex == -1)
			return;

		final DataEditAction edit = updateCell(keyIndex, rowIdx, colIdx, newValue);
		if (edit != null)
			cytoscape.util.undo.CyUndo.getUndoableEditSupport().postEdit(edit);
	}

	public CyAttributes getCyAttributes() { return data; }

	/**
	 *  Updates an entire column.
	 *
	 *  @param newValue    the new value to be set
	 *  @param colIdx      the index of the column that will be updated
	 *  @param skipRowIdx  a row with matching this index will not be updated
	 *
	 */
	public void updateColumn(final Object newValue, final int colIdx, final int skipRowIdx) {
		final int keyIndex = getKeyIndex();
		if (keyIndex == -1)
			return;

		for (int rowIdx = 0; rowIdx < getRowCount(); ++rowIdx) {
			if (rowIdx == skipRowIdx)
				continue;

			final DataEditAction edit = updateCell(keyIndex, rowIdx, colIdx, newValue);
			if (edit != null)
				cytoscape.util.undo.CyUndo.getUndoableEditSupport().postEdit(edit);
		}
	}

	public String getRowId(final int rowIndex) {
		final int keyIndex = getKeyIndex();
		if (keyIndex == -1)
			return null;

		final ValidatedObjectAndEditString objectAndEditString = (ValidatedObjectAndEditString)getValueAt(rowIndex, keyIndex);
		return (String)objectAndEditString.getValidatedObject();
	}

	private int getKeyIndex() {
		int keyIndex = -1;
		int columnOffset = 0;

		if (attributeNames.contains(AttributeBrowser.ID) == false) {
			// The ID is not in our attribute list, so it must be in the first column
			// We will need to offset our index into the attribute names list to get
			// the correct value.  Note that this is only safe because AttributeBrowser.ID
			// is not editable
			keyIndex = 0;
			columnOffset = 1;
		} else {
			for (int i = 0; i < columnIdentifiers.size(); i++) {
				if (columnIdentifiers.get(i).equals(AttributeBrowser.ID)) {
					keyIndex = i;
					break;
				}
			}
		}

		return keyIndex;
	}

	private DataEditAction updateCell(final int keyIndex, final int rowIdx, final int colIdx, final Object newValue) {
		final DataEditAction edit;
		if (this.objectType != NETWORK) {
			// This edit is for node or edge.
			final ValidatedObjectAndEditString objectAndEditString = (ValidatedObjectAndEditString)getValueAt(rowIdx, keyIndex);
			if (objectAndEditString == null)
				return null;

			final Object validatedObject = objectAndEditString.getValidatedObject();
			if (validatedObject == null)
				return null;

			edit = new DataEditAction(this, validatedObject.toString(), getColumnName(colIdx),
			                          getValueAt(rowIdx, colIdx), newValue, objectType);
		} else {
			final ValidatedObjectAndEditString objectAndEditString = (ValidatedObjectAndEditString)getValueAt(rowIdx, 0);
			if (objectAndEditString == null)
				return null;

			final Object validatedObject = objectAndEditString.getValidatedObject();
			if (validatedObject == null)
				return null;

			edit = new DataEditAction(this, Cytoscape.getCurrentNetwork().getIdentifier(),
			                          validatedObject.toString(), getValueAt(rowIdx, colIdx),
			                          newValue, objectType);
		}

		final boolean editIsValid = edit.isValid();

		final Vector rowVector = (Vector) dataVector.elementAt(rowIdx);
		rowVector.setElementAt(edit.getValidatedObjectAndEditString(), colIdx);
		if (this.objectType != NETWORK)
			setDataTableRow(rowIdx, colIdx);
		else
			setDataTableColumn();

		return editIsValid ? edit : null;
	}

	/**
	 *  Helper method for updateCell().
	 */
	void setDataTableRow(final int rowIdx, final int skipIdx) {
		final Vector rowVector = (Vector) dataVector.elementAt(rowIdx);
		final int noOfColumns = attributeNames.contains(AttributeBrowser.ID) ? attributeNames.size() : attributeNames.size() + 1;
		final String id = graphObjects.get(rowIdx).getIdentifier();
		for (int colIdx = 0; colIdx < noOfColumns; ++colIdx) {
			if (colIdx == skipIdx)
				continue;

			final String attribName = (String)columnIdentifiers.get(colIdx);
			if (attribName.equals(AttributeBrowser.ID))
				continue;

			final byte type = data.getType(attribName);
			final ValidatedObjectAndEditString objectAndEditString = getValidatedObjectAndEditString(type, id, attribName);
			if (objectAndEditString != null) {
				final int actualColIdx = findColumn(attribName);
				rowVector.setElementAt(objectAndEditString, actualColIdx);
			}
		}
	}

	/**
	 *  Helper method for updateCell().
	 */
	void setDataTableColumn() {
		final int noOfRows = attributeNames.size();
		final String id = Cytoscape.getCurrentNetwork().getIdentifier();
		for (int rowIdx = 1; rowIdx <= noOfRows; ++rowIdx) {
			final String attribName = attributeNames.get(rowIdx - 1);
			final byte type = data.getType(attribName);
			final ValidatedObjectAndEditString objectAndEditString = getValidatedObjectAndEditString(type, id, attribName);
			if (objectAndEditString != null) {
				final Vector rowVector = (Vector) dataVector.elementAt(rowIdx - 1);
				rowVector.setElementAt(objectAndEditString, 1 /* Always the 2nd column! */);
			}
		}
	}
}
