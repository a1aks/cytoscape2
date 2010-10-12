/*
 Copyright (c) 2006, 2007, 2010, The Cytoscape Consortium (www.cytoscape.org)

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


import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;

import cytoscape.data.attr.MultiHashMapDefinitionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


public class AttributeModel implements ListModel, ComboBoxModel, MultiHashMapDefinitionListener, PropertyChangeListener {
	private final AttributeBrowser attribBrowser;
	private Vector listeners = new Vector();
	private final CyAttributes attributes;
	private List<String> attributeNames;
	private Object selection = null;
	private Set<Byte> validAttrTypes;
	private Set<String> newAttributeNames = new HashSet<String>();

	/**
	 * Creates a new AttributeModel object.
	 *
	 * @param data  DOCUMENT ME!
	 */
	public AttributeModel(final CyAttributes data, final AttributeBrowser attribBrowser, final Set<Byte> validAttrTypes) {
		this.attribBrowser = attribBrowser;
		this.attributes = data;
		this.validAttrTypes = validAttrTypes;
		data.getMultiHashMapDefinition().addDataDefinitionListener(this);
		sortAttributes();

		Cytoscape.getPropertyChangeSupport().addPropertyChangeListener(Cytoscape.ATTRIBUTES_CHANGED, this);
	}

	@SuppressWarnings("unchecked") public AttributeModel(final CyAttributes data, final AttributeBrowser attribBrowser) {
		this(data, attribBrowser,
		     new TreeSet<Byte>((List<Byte>)(Arrays.asList(new Byte[] {
			CyAttributes.TYPE_BOOLEAN,
			CyAttributes.TYPE_COMPLEX,
			CyAttributes.TYPE_FLOATING,
			CyAttributes.TYPE_INTEGER,
			CyAttributes.TYPE_SIMPLE_LIST,
			CyAttributes.TYPE_SIMPLE_MAP,
			CyAttributes.TYPE_STRING
			}))));
	}

	public void propertyChange(PropertyChangeEvent e) {
		// This will handle the case for the change of attribute userVisibility
		if (e.getPropertyName() == Cytoscape.ATTRIBUTES_CHANGED) {
			sortAttributes();
			if (attribBrowser != null)
				attribBrowser.refresh();
		}
	}
	
	/**
	 *  Sets "attributeNames" to the sorted list of user-visible attribute names with supported data types.
	 */
	public void sortAttributes() {
		final Set<String> oldAttributeNames = (attributeNames == null) ? new HashSet<String>() : new HashSet<String>(attributeNames);
		newAttributeNames = new HashSet<String>();

		attributeNames = new ArrayList<String>();
		for (final String attrName : CyAttributesUtils.getVisibleAttributeNames(attributes)) {
			if (validAttrTypes.contains(attributes.getType(attrName))) {
				attributeNames.add(attrName);
				if (!oldAttributeNames.contains(attrName))
					newAttributeNames.add(attrName);
			}
		}
		Collections.sort(attributeNames);

		notifyListeners(new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0,
		                                  attributeNames.size()));
	}

	public Set<String> getNewAttributeNames() {
		return newAttributeNames;
	}

	/**
	 *  @return the i-th attribute name
	 */
	public Object getElementAt(int i) {
		if (i > attributeNames.size())
			return null;

		return attributeNames.get(i);
	}

	/**
	 *  @return the number of attribute names
	 */
	public int getSize() {
		return attributeNames.size();
	}

	// implements ComboBoxModel
	/**
	 *  DOCUMENT ME!
	 *
	 * @param anItem DOCUMENT ME!
	 */
	public void setSelectedItem(Object anItem) {
		selection = anItem;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	public Object getSelectedItem() {
		return selection;
	}

	// implements CyDataDefinitionListener
	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeName DOCUMENT ME!
	 */
	public void attributeDefined(String attributeName) {
		sortAttributes();
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributeName DOCUMENT ME!
	 */
	public void attributeUndefined(String attributeName) {
		sortAttributes();
	}

	// implements ListModel
	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param l DOCUMENT ME!
	 */
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param e DOCUMENT ME!
	 */
	public void notifyListeners(ListDataEvent e) {
		for (Iterator listenIt = listeners.iterator(); listenIt.hasNext();) {
			if (e.getType() == ListDataEvent.CONTENTS_CHANGED) {
				((ListDataListener) listenIt.next()).contentsChanged(e);
			} else if (e.getType() == ListDataEvent.INTERVAL_ADDED) {
				((ListDataListener) listenIt.next()).intervalAdded(e);
			} else if (e.getType() == ListDataEvent.INTERVAL_REMOVED) {
				((ListDataListener) listenIt.next()).intervalRemoved(e);
			}
		}
	}
}
