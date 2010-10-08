/*
 File: VisualMappingManager.java

 Copyright (c) 2006, The Cytoscape Consortium (www.cytoscape.org)

 The Cytoscape Consortium is:
 - Institute for Systems Biology
 - University of California San Diego
 - Memorial Sloan-Kettering Cancer Center
 - Institut Pasteur
 - Agilent Technologies

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
package cytoscape.visual;

import giny.model.Node;
import giny.view.EdgeView;
import giny.view.NodeView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cytoscape.CyEdge;
import cytoscape.CyNetwork;
import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMapDefinition;
import cytoscape.logger.CyLogger;
import cytoscape.view.CyNetworkView;
import cytoscape.visual.calculators.AbstractCalculator;
import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.GenericNodeCustomGraphicCalculator;
import cytoscape.visual.customgraphic.CustomGraphicsManager;
import cytoscape.visual.customgraphic.CyCustomGraphics;
import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;
import cytoscape.visual.mappings.RangeValueCalculator;
import cytoscape.visual.mappings.RangeValueCalculatorFactory;
import cytoscape.visual.mappings.RangeValueCalculatorFactoryImpl;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;
import cytoscape.visual.mappings.rangecalculators.ColorRangeValueCalculator;
import cytoscape.visual.mappings.rangecalculators.CustomGraphicsRangeValueCalculator;
import cytoscape.visual.mappings.rangecalculators.DoubleRangeValueCalculator;
import cytoscape.visual.mappings.rangecalculators.FloatRangeValueCalculator;
import cytoscape.visual.mappings.rangecalculators.NodeShapeRangeValueCalculator;
import cytoscape.visual.mappings.rangecalculators.StringRangeValueCalculator;
import ding.view.DGraphView;
import ding.view.DingCanvas;

/**
 * Top-level class for controlling the visual appearance of nodes and edges
 * according to data attributes, as well as some global visual attributes. This
 * class holds a reference to a NetworkView that displays the network, a
 * CalculatorCatalog that holds the set of known visual styles and calculators,
 * and a current VisualStyle that is used to determine the values of the visual
 * attributes. A Logger is also supplied to report errors.
 * <P>
 * 
 * Note that a null VisualStyle is not allowed; this class always provides at
 * least a default object.
 * <P>
 * 
 * The key methods are the apply* methods. These methods first recalculate the
 * visual appearances by delegating to the calculators contained in the current
 * visual style. The usual return value of these methods is an Appearance object
 * that contains the visual attribute values; these values are then applied to
 * the network by calling the appropriate set methods in the graph view API.
 * <P>
 */
public class VisualMappingManager extends SubjectBase {

	// Catalog of visual styles and calculators.
	// This is the actual object to store styles.
	private CalculatorCatalog catalog;

	private CyNetworkView networkView; // the object displaying the network
	private VisualStyle activeVS; // the currently active visual style

	// reusable appearance objects
	private final NodeAppearance myNodeApp = new NodeAppearance();
	private final EdgeAppearance myEdgeApp = new EdgeAppearance();
	private final GlobalAppearance myGlobalApp = new GlobalAppearance();

	// Default VS name.  This one cannot be removed.
	private static final String DEF_STYLE_NAME = "default";

	// New in Cytoscape 2.8: Pool of available custom graphics
	private final CustomGraphicsManager manager;

	// New in 2.8: Dynamically manage mappings
	private final RangeValueCalculatorFactory rvcFactory;

	/**
	 * Creates a new VisualMappingManager object.
	 * 
	 * @param networkView
	 *            DOCUMENT ME!
	 */
	public VisualMappingManager(final CyNetworkView networkView) {
		
		//TODO: Why this parameter is required to instantiate this manager?
		this.networkView = networkView;
		
		// Creates a repository of static images.
		manager = new CustomGraphicsManager();

		// New in 2.8: dynamically manages object mappings.
		rvcFactory = new RangeValueCalculatorFactoryImpl();
		registerDefaultRangeValueCalculators();
		
		loadCalculatorCatalog();

		// Try to find default style name from prop.
		String defStyle = CytoscapeInit.getProperties().getProperty(
				"defaultVisualStyle");

		if (defStyle == null)
			defStyle = DEF_STYLE_NAME;

		VisualStyle vs = catalog.getVisualStyle(defStyle);

		if (vs == null)
			vs = catalog.getVisualStyle(DEF_STYLE_NAME);

		setVisualStyle(vs);
	}
	

	/**
	 * Attempts to load a CalculatorCatalog object, using the information from
	 * the CytoscapeConfig object.
	 * 
	 * Does nothing if a catalog has already been loaded.
	 * 
	 * @see CalculatorCatalog
	 * @see CalculatorCatalogFactory
	 */
	public void loadCalculatorCatalog() {
		loadCalculatorCatalog(null);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param vizmapLocation
	 *            DOCUMENT ME!
	 */
	public void loadCalculatorCatalog(final String vizmapLocation) {
		if (catalog == null)
			catalog = CalculatorCatalogFactory.loadCalculatorCatalog();
		else if (vizmapLocation != null)
			catalog = CalculatorCatalogFactory.loadCalculatorCatalog();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param new_view
	 *            DOCUMENT ME!
	 */
	public void setNetworkView(final CyNetworkView new_view) {
		this.networkView = new_view;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CyNetworkView getNetworkView() {
		return networkView;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CyNetwork getNetwork() {
		return networkView.getNetwork();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public CalculatorCatalog getCalculatorCatalog() {
		return catalog;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public VisualStyle getVisualStyle() {
		return activeVS;
	}

	/**
	 * Sets a new visual style, and returns the old style. Also fires an event
	 * to attached listeners only if the visual style changes.
	 * 
	 * If the argument is null, the previous visual style is simply returned.
	 */
	public VisualStyle setVisualStyle(final VisualStyle vs) {

		if ((vs != null) && (vs != activeVS)) {
			VisualStyle tmp = activeVS;
			activeVS = vs;
			fireStateChanged();

			return tmp;
		} else
			return activeVS;
	}

	/**
	 * Sets a new visual style. Attempts to get the style with the given name
	 * from the catalog and pass that to setVisualStyle(VisualStyle). The return
	 * value is the old style.
	 * 
	 * If no visual style with the given name is found, no change is made, an
	 * error message is passed to the logger, and null is returned.
	 */
	public VisualStyle setVisualStyle(final String newVSName) {
		final VisualStyle vs = catalog.getVisualStyle(newVSName);

		if (vs != null)
			return setVisualStyle(vs);
		else
			return activeVS;
	}

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances() {
		applyNodeAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies all of the node appearances. The visual
	 * attributes are calculated by delegating to the NodeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyNodeAppearances(final CyNetwork network,
			final CyNetworkView network_view) {
		final NodeAppearanceCalculator nodeAppearanceCalculator = activeVS
				.getNodeAppearanceCalculator();

		List<VisualPropertyType> bypassedVPs = getBypassedVPs("NODE", Cytoscape
				.getNodeAttributes());

		final Iterator<NodeView> itr = network_view.getNodeViewsIterator();
		while (itr.hasNext()) {
			final NodeView nodeView = itr.next();
			final Node node = nodeView.getNode();			
			nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node,
					network, bypassedVPs);
			myNodeApp.applyAppearance(nodeView, activeVS.getDependency());
		}
		
		checkCustomGraphicsInUse();
	}
	
	/**
	 * Reset status of used Custom Graphics
	 */
	private void checkCustomGraphicsInUse() {

		// Set everything unused.
		final Collection<CyCustomGraphics> allCG = manager.getAll();
		for(CyCustomGraphics cg: allCG)
			manager.setUsedInCurrentSession(cg, false);
		
		final NodeAppearanceCalculator nac = activeVS.getNodeAppearanceCalculator();
		final VisualPropertyType[] allCustomGraphicsPropType = VisualPropertyType.getAllCustomGraphicsType();
		for(VisualPropertyType cgType: allCustomGraphicsPropType) {
			// First, check default mapping values.
			final Object value = nac.getDefaultAppearance().get(cgType);
			if(value instanceof CyCustomGraphics) {
				manager.setUsedInCurrentSession((CyCustomGraphics) value, true);
			}
			
			// Next, check mapping values.
			final Calculator cgCalc = nac.getCalculator(cgType);
			if(cgCalc == null)
				continue;
			
			final ObjectMapping cgMapping = cgCalc.getMapping(0);
			if(cgMapping == null)
				continue;
			
			if(cgMapping instanceof DiscreteMapping) {
				final Map allMapping = ((DiscreteMapping) cgMapping).getAll();
				Collection cgSet = allMapping.values();
				for(Object cg: cgSet)
					manager.setUsedInCurrentSession((CyCustomGraphics) cg, true);
			} else if(cgMapping instanceof ContinuousMapping) {
				final List<ContinuousMappingPoint> points = ((ContinuousMapping) cgMapping).getAllPoints();
				for(ContinuousMappingPoint point: points) {
					manager.setUsedInCurrentSession((CyCustomGraphics) point.getRange().equalValue, true);
					manager.setUsedInCurrentSession((CyCustomGraphics) point.getRange().greaterValue, true);
					manager.setUsedInCurrentSession((CyCustomGraphics) point.getRange().lesserValue, true);
				}
			} else if(cgMapping instanceof PassThroughMapping) {
				final RangeValueCalculator<?> rangeValueCalculator = ((PassThroughMapping) cgMapping).getRangeValueCalculator();
				if(rangeValueCalculator == null)
					return;
				
				final CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
				final String attrName = cgMapping.getControllingAttributeName();
				CountedIterator keys = nodeAttr.getMultiHashMap().getObjectKeys(attrName);
				while(keys.hasNext()) {
					Object key = keys.next();
					String attrValue = nodeAttr.getStringAttribute(key.toString(), attrName);
					Object cg = rangeValueCalculator.getRange(attrValue);
					if(cg instanceof CyCustomGraphics) {
						manager.setUsedInCurrentSession((CyCustomGraphics) cg, true);
					}
				}
				CyAttributesUtils.getAttributes(attrName, Cytoscape.getNodeAttributes());
			}
			
		}
	}

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances() {
		applyEdgeAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies all of the edge appearances. The visual
	 * attributes are calculated by delegating to the EdgeAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyEdgeAppearances(final CyNetwork network,
			final CyNetworkView network_view) {
		final EdgeAppearanceCalculator edgeAppearanceCalculator = activeVS
				.getEdgeAppearanceCalculator();

		EdgeView edgeView;

		List<VisualPropertyType> bypassedVPs = getBypassedVPs("EDGE", Cytoscape
				.getEdgeAttributes());

		for (Iterator i = network_view.getEdgeViewsIterator(); i.hasNext();) {
			edgeView = (EdgeView) i.next();

			if (edgeView == null)

				// WARNING: This is a hack, edgeView should not be null, but
				// for now do this! (iliana)
				continue;

			edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp,
					edgeView.getEdge(), network, bypassedVPs);
			myEdgeApp.applyAppearance(edgeView, activeVS.getDependency());
		}
	}

	private List<VisualPropertyType> getBypassedVPs(final String prefix,
			final CyAttributes attrs) {
		MultiHashMapDefinition mhmd = attrs.getMultiHashMapDefinition();
		List<VisualPropertyType> bypassAttrs = new ArrayList<VisualPropertyType>();
		for (VisualPropertyType vp : VisualPropertyType.values())
			if (vp.toString().startsWith(prefix)
					&& mhmd.getAttributeValueType(vp.getBypassAttrName()) >= 0)
				bypassAttrs.add(vp);

		return bypassAttrs;
	}

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 */
	public void applyGlobalAppearances() {
		applyGlobalAppearances(getNetwork(), getNetworkView());
	}

	/**
	 * Recalculates and reapplies the global visual attributes. The
	 * recalculation is done by delegating to the GlobalAppearanceCalculator
	 * member of the current visual style.
	 * 
	 * @param network
	 *            the network to apply to
	 * @param network_view
	 *            the view to apply to
	 */
	public void applyGlobalAppearances(CyNetwork network,
			CyNetworkView network_view) {
		GlobalAppearanceCalculator globalAppearanceCalculator = activeVS
				.getGlobalAppearanceCalculator();
		globalAppearanceCalculator.calculateGlobalAppearance(myGlobalApp,
				network);

		// setup proper background colors
		if (network_view instanceof DGraphView) {
			DingCanvas backgroundCanvas = ((DGraphView) network_view)
					.getCanvas(DGraphView.Canvas.BACKGROUND_CANVAS);
			backgroundCanvas.setBackground(myGlobalApp.getBackgroundColor());
		} else {
			CyLogger
					.getLogger()
					.info(
							"VisualMappingManager.applyGlobalAppearances() - DGraphView not found!");
			network_view.setBackgroundPaint(myGlobalApp.getBackgroundColor());
		}

		// will ignore sloppy & reverse selection color for now

		// Set selection colors
		Iterator nodeIt = network.nodesIterator();

		while (nodeIt.hasNext()) {
			network_view.getNodeView((CyNode) nodeIt.next()).setSelectedPaint(
					myGlobalApp.getNodeSelectionColor());
		}

		Iterator edgeIt = network.edgesIterator();

		while (edgeIt.hasNext())
			network_view.getEdgeView((CyEdge) edgeIt.next()).setSelectedPaint(
					myGlobalApp.getEdgeSelectionColor());
	}

	/**
	 * Recalculates and reapplies all of the node, edge, and global visual
	 * attributes. This method delegates to, in order, applyNodeAppearances,
	 * applyEdgeAppearances, and applyGlobalAppearances.
	 */
	public void applyAppearances() {
		/** first apply the node appearance to all nodes */
		applyNodeAppearances();
		/** then apply the edge appearance to all edges */
		applyEdgeAppearances();
		/** now apply global appearances */
		applyGlobalAppearances();
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param nodeView
	 *            DOCUMENT ME!
	 * @param network_view
	 *            DOCUMENT ME!
	 */
	public void vizmapNode(NodeView nodeView, CyNetworkView network_view) {
		CyNode node = (CyNode) nodeView.getNode();
		List<VisualPropertyType> bypassedVPs = getBypassedVPs("NODE", Cytoscape
				.getNodeAttributes());
		NodeAppearanceCalculator nodeAppearanceCalculator = activeVS
				.getNodeAppearanceCalculator();
		nodeAppearanceCalculator.calculateNodeAppearance(myNodeApp, node,
				network_view.getNetwork(), bypassedVPs);
		myNodeApp.applyAppearance(nodeView, activeVS.getDependency());
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param edgeView
	 *            DOCUMENT ME!
	 * @param network_view
	 *            DOCUMENT ME!
	 */
	public void vizmapEdge(EdgeView edgeView, CyNetworkView network_view) {
		CyEdge edge = (CyEdge) edgeView.getEdge();
		List<VisualPropertyType> bypassedVPs = getBypassedVPs("EDGE", Cytoscape
				.getEdgeAttributes());
		EdgeAppearanceCalculator edgeAppearanceCalculator = activeVS
				.getEdgeAppearanceCalculator();
		edgeAppearanceCalculator.calculateEdgeAppearance(myEdgeApp, edge,
				network_view.getNetwork(), bypassedVPs);
		myEdgeApp.applyAppearance(edgeView, activeVS.getDependency());
	}

	public CustomGraphicsManager getCustomGraphicsManager() {
		return manager;
	}
	
	
	public RangeValueCalculatorFactory getRangeValueCalculatorFactory() {
		return rvcFactory;
	}
	
	private void registerDefaultRangeValueCalculators() {
		this.rvcFactory.registerRVC(new StringRangeValueCalculator());
		this.rvcFactory.registerRVC(new DoubleRangeValueCalculator());
		this.rvcFactory.registerRVC(new ColorRangeValueCalculator());
		this.rvcFactory.registerRVC(new NodeShapeRangeValueCalculator());
		this.rvcFactory.registerRVC(new CustomGraphicsRangeValueCalculator());
		this.rvcFactory.registerRVC(new FloatRangeValueCalculator());
	}
	

}
