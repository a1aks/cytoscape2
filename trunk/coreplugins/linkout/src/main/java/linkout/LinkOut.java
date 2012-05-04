/*$Id$*/
package linkout;

import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;

import cytoscape.data.CyAttributes;
import cytoscape.logger.CyLogger;

import cytoscape.util.OpenBrowser;

import giny.model.Edge;
import giny.model.Node;

import giny.view.EdgeView;
import giny.view.NodeView;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;


/**
* Generates links to external web pages specified in the cytoscape.props file.
* Nodes can be linked to external web pages by specifying web resources in the linkout.properties file
* The format for a weblink is in the form of a <key> = <value> pair where <key> is the name of the
* website (e.g. NCBI, E!, PubMed, SGD,etc) and <value> is the URL. The key name must be preceded by the keyword "url." to distinguish
* this property from other properties.
* In the URL string the placeholder %ID% will be replaced with the node label that is visible on the node.
* If no label is visible, the node identifier (far left of attribute browser) will be used.
* It is the users responsibility
* to ensure that the URL is correct and the node's name will match the required query value.
* Examples:
*    url.NCBI=http\://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd\=Search&db\=Protein&term\=%ID%&doptcmdl\=GenPept
*    url.SGD=http\://db.yeastgenome.org/cgi-bin/locus.pl?locus\=%ID%
*    url.E\!Ensamble=http\://www.ensembl.org/Homo_sapiens/textview?species\=all&idx\=All&q\=%ID%
*    url.Pubmed=http\://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd\=Search&db\=PubMed&term\=%ID%
*
*/
public class LinkOut {
	//keyword that marks properties that should be added to LinkOut
	private static final String nodeMarker = "nodelinkouturl.";
	private static final String edgeMarker = "edgelinkouturl.";
	private static final String linkMarker = "Linkout.externalLinkName";
	private static String externalLinksAttribute = "Linkout.ExternalLinks";
	private Properties props;
	private static final Font TITLE_FONT = new Font("sans-serif", Font.BOLD, 14);
	private static CyLogger logger = null;

	//null constractor
	/**
	 * Creates a new LinkOut object.
	 */
	public LinkOut() {
		if (LinkOut.logger == null)
			LinkOut.logger = CyLogger.getLogger(LinkOut.class);
	}

	/**
	 * Generates URL links with node name and places them in hierarchical JMenu list
	 * @param node the NodeView.
	 * @return JMenuItem
	 */
	public JMenuItem addLinks(NodeView node) {
		// System.out.println("linkout.addLinks called with node "
		//                    + ((NodeView) node).getLabel().getText());
		readProperties();

		final JMenu top_menu = new JMenu("LinkOut");
		final JMenuItem source = new JMenuItem("Database");
		source.setBackground(Color.white);
		source.setFont(TITLE_FONT);
		source.setEnabled(false);
		top_menu.add(source);

		//iterate through properties list
		try {
			CyAttributes na = Cytoscape.getNodeAttributes();
			final NodeView mynode = (NodeView) node;

			// Get the set of attribute names for this node
			Node n = mynode.getNode();
			String nodeId = n.getIdentifier();

			for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
				String propKey = (String) e.nextElement();
				int p = propKey.lastIndexOf(nodeMarker);

				if (p == -1)
					continue;

				p = p + nodeMarker.length();

				//the URL
				String url = props.getProperty(propKey);

				if (url == null) {
					url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
					      + "http://www.cytoscape.org/";
				}

				String fUrl = subsAttrs(url, na, nodeId, "ID", "");

				//the link name
				String[] temp = ((String) propKey.substring(p)).split("\\.");
				ArrayList keys = new ArrayList(Arrays.asList(temp));

				//Generate the menu path
				generateLinks(keys, top_menu, fUrl);
			}

			// Now, see if the user has specified their own URL to add to linkout
			generateExternalLinks(na, nodeId, top_menu);

			//if no links specified insert a default message
			if (top_menu.getMenuComponentCount() == 0) {
				String url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
				             + "http://www.cytoscape.org/";
				top_menu.add(new JMenuItem(url));
			}

			// For debugging
			// printMenu(top_menu);
		} catch (NullPointerException e) {
			String url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
			             + "http://www.cytoscape.org/";
			top_menu.add(new JMenuItem(url));
			logger.error("NullPointerException: " + e.getMessage());
		}

		return top_menu;
	}

	/**
	 * Perform variable substitution on a url using attribute values.
	 *
	 * Given a url, look for attribute names within that url specified by %AttributeName%
	 * If our current node or edge (as specified by graphObjId) has an attribute name that
	 * matches the string in the URL, replace the string with the value of the attribute.
	 *
	 * Special cases:
	 * 1. For backwards compatibility, if the attribute name given by idKeyword is found
	 *    substitute the value of the attribute id as given by graphObjId.
	 * 2. When looking for attribute names, we can specify that they must have a prefix added
	 *    to the beginning.  This allows us to specify a "from" or "to" prefix in edge attributes
	 *    so that from.AttributeName and to.AttributeName can select different properties.
	 *
	 * @param url the url to perform replacements on.
	 * @param attrs  a set of node or edge attributes.
	 * @param graphObjId the id of the node or edge that is selected.
	 * @param idKeyword a special attribute keyword, that if found, should be replaced by graphObjId
	 * @param prefix a prefix to prepend to attribute names.
	 * @return modified url after variable substitution
	 */
	private String subsAttrs(String url, CyAttributes attrs, String graphObjId, String idKeyword,
	                         String prefix) {
		Set<String> validAttrs = new HashSet<String>();

		for (String attrName : attrs.getAttributeNames()) {
			if (attrs.hasAttribute(graphObjId, attrName)) {
				validAttrs.add(prefix + attrName);
			}
		}

		// Replace %ATTRIBUTE.NAME% mark with the value of the attribute
		final String REGEX = "%.*?%";
		Pattern pat = Pattern.compile(REGEX);
		Matcher mat = pat.matcher(url);

		while (mat.find()) {
			String attrName = url.substring(mat.start() + 1, mat.end() - 1);
			// backwards compatibility, old keywords were %ID%, %ID1%, %ID2%.
			if (attrName.equals(idKeyword)) {
				String attrValue = graphObjId;
				url = url.replace("%" + idKeyword + "%", attrValue);
				mat = pat.matcher(url);
			} else if (validAttrs.contains(attrName)) {
                String prefixlessAttrName = attrName.substring(prefix.length());
				String attrValue = attrToString(attrs, graphObjId, prefixlessAttrName);
				url = url.replace("%" + attrName + "%", attrValue);
				mat = pat.matcher(url);
			}
		}

		return url;
	}

	/**
	 *  DOCUMENT ME!
	 *
	 * @param attributes DOCUMENT ME!
	 * @param id DOCUMENT ME!
	 * @param attributeName DOCUMENT ME!
	 *
	 * @return  DOCUMENT ME!
	 */
	private String attrToString(CyAttributes attributes, String id, String attributeName) {
		Object value = null;
		byte attrType = attributes.getType(attributeName);

		if (attrType == CyAttributes.TYPE_BOOLEAN) {
			value = attributes.getBooleanAttribute(id, attributeName);
		} else if (attrType == CyAttributes.TYPE_FLOATING) {
			value = attributes.getDoubleAttribute(id, attributeName);
		} else if (attrType == CyAttributes.TYPE_INTEGER) {
			value = attributes.getIntegerAttribute(id, attributeName);
		} else if (attrType == CyAttributes.TYPE_STRING) {
			value = attributes.getStringAttribute(id, attributeName);
		}

		if (value != null)
			return value.toString();
		else

			return "N/A";
	}

	/**
	 * Generate URL links with edge property and places them in hierarchical JMenu list
	 * @param edge edgeView object
	 * @return JMenuItem
	 */
	public JMenuItem addLinks(EdgeView edge) {
		readProperties();

		JMenu top_menu = new JMenu("LinkOut");

		//iterate through properties list
		try {
			final EdgeView myedge = (EdgeView) edge;

			// Replace edge attributes with values
			Edge ed = myedge.getEdge();

			for (Enumeration e = props.propertyNames(); e.hasMoreElements();) {
				String propKey = (String) e.nextElement();

				int p = propKey.lastIndexOf(edgeMarker);

				if (p == -1)
					continue;

				p = p + edgeMarker.length();

				//the URL
				String url = props.getProperty(propKey);

				if (url == null) {
					url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
					      + "http://www.cytoscape.org/";
				}

				//add edge label to the URL
				String edgelabel;

				CyAttributes nodeAtts = Cytoscape.getNodeAttributes();
                CyAttributes edgeAtts = Cytoscape.getEdgeAttributes();
				String sourceId = ed.getSource().getIdentifier();
				String targetId = ed.getTarget().getIdentifier();
                String edgeId = ed.getIdentifier();
//				String fUrl = subsAttrs(url, attrs, sourceId, "ID%1", "source.");
//				fUrl = subsAttrs(fUrl, attrs, targetId, "ID%2", "target.");
		String fUrl = subsAttrs(url, nodeAtts, sourceId, "ID1", "source.");
		fUrl = subsAttrs(fUrl, nodeAtts, targetId, "ID2", "target.");
		fUrl = subsAttrs(fUrl, edgeAtts, edgeId, "ID", "");

				// System.out.println(fUrl);

				//the link name
				String[] temp = ((String) propKey.substring(p)).split("\\.");
				ArrayList keys = new ArrayList(Arrays.asList(temp));

				//Generate the menu path
				generateLinks(keys, top_menu, fUrl);
			}

			CyAttributes edgeAttributes = Cytoscape.getEdgeAttributes();

			// Now, see if the user has specified their own URL to add to linkout
			generateExternalLinks(edgeAttributes, ed.getIdentifier(), top_menu);

			//if no links specified insert a default message
			if (top_menu.getMenuComponentCount() == 0) {
				String url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
				             + "http://www.cytoscape.org/";
				top_menu.add(new JMenuItem(url));
			}

			// For debugging 
			// printMenu(top_menu);
		} catch (NullPointerException e) {
			String url = "<html><small><i>empty- no links<br> See documentation</i></small></html>"
			             + "http://www.cytoscape.org/";
			top_menu.add(new JMenuItem(url));
			logger.error("NullPointerException: " + e.getMessage());
		}

		return top_menu;
	}

	/**
	 * Recursive method that expands the current menu list
	 * The list of keys mark the current path of sub-menus
	 * @param keys ArrayList
	 * @param j JMenu the curren JMenu object
	 * @param url String the url to link the node
	 */
	private void generateLinks(ArrayList keys, JMenu j, final String url) {
		//Get the sub-menu
		JMenuItem jmi = getMenuItem((String) keys.get(0), j);

		//if its null and this is the last key generate a new JMenuItem
		if ((jmi == null) && (keys.size() == 1)) {
			final String s = (String) keys.get(0);
			JMenuItem new_jmi = new JMenuItem(new AbstractAction((String) keys.get(0)) {
					public void actionPerformed(ActionEvent e) {
						SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									// System.out.println("Opening link: "+url);
									OpenBrowser.openURL(url);
								}
							});
					}
				}); //end of AbstractAction class

			j.add(new_jmi);

			return;

			//if its a JMenuItem and this is the last key then there
			//is a duplicate of keys in the file. i.e two url with the exact same manu path
		} else if (jmi instanceof JMenuItem && (keys.size() == 1)) {
			logger.error("Duplicate URL specified for " + (String) keys.get(0));

			return;

			//if not null create a new JMenu  with current key
			// remove key from the keys ArrayList and call generateLinks
		} else if (jmi == null) {
			JMenu new_jm = new JMenu((String) keys.get(0));

			keys.remove(0);
			generateLinks(keys, new_jm, url);
			j.add(new_jm);

			return;

			//Remove key from top of the list and call generateLinks with new JMenu
		} else {
			keys.remove(0);

			generateLinks(keys, (JMenu) jmi, url);
		}

		return;
	}

	/**
	 * Search for an existing JmenuItem that is nested within a higher level JMenu
	 * @param name String the name of the jmenu item
	 * @param menu JMenu the parent JMenu to search in
	 * @return JMenuItem if found, null otherwise
	 */
	private JMenuItem getMenuItem(String name, JMenu menu) {
		int count = menu.getMenuComponentCount();

		if (count == 0) {
			return null;
		}

		//Skip over all JMenu components that are not JMenu or JMenuItem
		for (int i = 0; i < count; i++) {
			if (!menu.getItem(i).getClass().getName().equals("javax.swing.JMenu")
			    && !menu.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")) {
				continue;
			}

			JMenuItem jmi = menu.getItem(i);

			if (jmi.getText().equalsIgnoreCase(name)) {
				return jmi;
			}
		}

		return null;
	}

	/**
	 * Print menu items - for debugging
	 */
	private void printMenu(JMenu jm) {
		int count = jm.getMenuComponentCount();

		for (int i = 0; i < count; i++) {
			if (jm.getItem(i).getClass().getName().equals("javax.swing.JMenuItem")) {
				logger.debug(jm.getItem(i).getText());

				continue;
			} else {
				logger.debug(jm.getItem(i).getText() + "--");
				printMenu((JMenu) jm.getItem(i));
			}
		}
	}

	/**
	 * Read properties values from linkout.props file included in the
	 * linkout.jar file and apply those properties to the base Cytoscape
	 * properties.  Only apply the properties from the jar file if
	 * NO linkout properties already exist.
	 * This allows linkout properties to be specified  on the command line,
	 * editted in the preferences dialog, and to be saved with other
	 * properties.
	 */
	private void readProperties() {
		// Set the properties to be Cytoscape's properties.
		// This allows the linkout properties to be edited in
		// the preferences editor.
		//System.out.println(CytoscapeInit.getPropertiesLocation());
		props = CytoscapeInit.getProperties();

		// Loop over the default props and see if any
		// linkout urls have been specified.  We don't want to
		// override or even supplement what was set from the
		// command line. Only use the defaults if nothing
		// else can be found.
		boolean linkoutFound = false;
		boolean externalLinkNameFound = false;
		Enumeration names = props.propertyNames();

		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			if (name.compareToIgnoreCase(linkMarker) == 0) {
				externalLinkNameFound = true;
				externalLinksAttribute = props.getProperty(linkMarker);
				continue;
			}
			int p = name.lastIndexOf(nodeMarker);
			int q = name.lastIndexOf(edgeMarker);

			if (p != -1 || q != -1) {
				linkoutFound = true;

				break;
			}
		}

		// If we don't have any linkout properties, load the defaults.
		if (!linkoutFound) {
			try {
				logger.info("loading defaults");

				ClassLoader cl = LinkOut.class.getClassLoader();
				props.load(cl.getResource("linkout.props").openStream());
				if (!externalLinkNameFound) {
					externalLinksAttribute = props.getProperty(linkMarker);
				}
			} catch (Exception e) {
				logger.error("Couldn't load default linkout props", e);
			}
		}
	}

	/**
 	 * If we have an ExternalLinks attribute, see if it's formatted as a LinkOut,
 	 * and if so, add that to the menu.  A LinkOut may be one of:
 	 * 	String: name=URL
 	 * 	List: [name1=URL1,name2=URL2,etc.]
 	 * where the name will be used as the menu label and the URL will be what we
 	 * actually hand off to the browser.
 	 *
 	 * @param attributes the attribute map we are currently using
 	 * @param id the ID of the object we are currently linking out from
 	 * @param menu the menu to add links to
 	 */
	private void generateExternalLinks(CyAttributes attributes, String id, JMenu menu) {
		if (attributes.hasAttribute(id, externalLinksAttribute)) {
			// Maybe.....
			byte attrType = attributes.getType(externalLinksAttribute);
			if (attrType == CyAttributes.TYPE_STRING) {
				// Single title=url pair
				String attr = attributes.getStringAttribute(id, externalLinksAttribute);
				addExternalLink(attr, menu);
			} else if (attrType == CyAttributes.TYPE_SIMPLE_LIST) {
				// List of title=url pairs
				List attrList = attributes.getListAttribute(id, externalLinksAttribute);
				for (String attr: (List<String>)attrList) {
					addExternalLink(attr, menu);
				}
			}
		}
		return;
	}

	private void addExternalLink(String attr, JMenu menu) {
		if (attr == null || attr.length() < 9)
			return;
		String[] pair = attr.split("=",2);
		if (pair.length != 2) return;
		if (!pair[1].startsWith("http://")) {
			return;
		}
		ArrayList<String>key = new ArrayList();
		key.add("ExternalLinks");
		key.add(pair[0]);
		generateLinks(key, menu, pair[1]);
	}
}
