// AnnotationGui.java:  the user interface for cytoscape annotations
//----------------------------------------------------------------------------------------
// $Revision$
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.data.annotation;
//----------------------------------------------------------------------------------------
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.tree.*;
import javax.swing.JOptionPane;

import java.io.*;
import java.util.*;

import y.base.*;
import y.view.*;

import cytoscape.data.annotation.*;
import cytoscape.data.servers.*;
import cytoscape.layout.*;
import cytoscape.*;
//----------------------------------------------------------------------------------------
/**
 */
public class AnnotationGui extends AbstractAction {
  protected CytoscapeWindow cytoscapeWindow;
  protected GraphObjAttributes nodeAttributes, edgeAttributes;
  protected BioDataServer dataServer;
  protected String defaultSpecies;
  protected Properties props;
  AnnotationDescription [] annotationDescriptions;
  JTree availableAnnotationsTree;
  JTree currentAnnotationsTree;
  JList actionListBox;
  DefaultListModel actionListModel;
  int actionListBoxCurrentSelection;
  TreePath annotationPath;
  String currentAnnotationCategory;
  AttributeLayout attributeLayouter;
  Graph2D graph;

  JButton annotateNodesButton;
  JButton layoutByAnnotationButton;
  JButton addSharedAnnotationEdgesButton;

//----------------------------------------------------------------------------------------
public AnnotationGui (CytoscapeWindow cytoscapeWindow)
{
  super ("Annotation...");
  dataServer = cytoscapeWindow.getBioDataServer ();
  if (dataServer != null)
    annotationDescriptions = dataServer.getAnnotationDescriptions ();

  for (int i=0; i < annotationDescriptions.length; i++)
    System.out.println (annotationDescriptions [i]);

  this.cytoscapeWindow = cytoscapeWindow;
  defaultSpecies = cytoscapeWindow.getDefaultSpecies ();

} // ctor
//----------------------------------------------------------------------------------------
public void actionPerformed (ActionEvent e) 
{
  if (dataServer == null) {
    JOptionPane.showMessageDialog (null, "No annotations are available.",
                                   "Error!", JOptionPane.ERROR_MESSAGE);
    return;
    }
    
  cytoscapeWindow.setInteractivity (false);
  this.graph = cytoscapeWindow.getGraph ();
  this.nodeAttributes = cytoscapeWindow.getNodeAttributes ();
  this.edgeAttributes = cytoscapeWindow.getEdgeAttributes ();
  this.attributeLayouter = new AttributeLayout (cytoscapeWindow);
  JDialog dialog = new Gui ("Annotation");
  dialog.pack ();
  dialog.setLocationRelativeTo (cytoscapeWindow.getMainFrame ());
  dialog.setVisible (true);
  cytoscapeWindow.setInteractivity (true);

} // actionPerformed
//----------------------------------------------------------------------------------------
protected class Gui extends JDialog {

Gui (String title) 
{
  super (cytoscapeWindow.getMainFrame (), false);
  setTitle (title);
  setContentPane (createWidgets ());
}
//------------------------------------------------------------------------------
private JPanel createWidgets ()
{
  JPanel mainPanel = new JPanel ();
  mainPanel.setLayout (new BorderLayout ());

  JPanel topPanel = new JPanel ();
  topPanel.setLayout (new GridLayout(0, 2));

  availableAnnotationsTree = createAvailableAnnotationsTree ();
  currentAnnotationsTree = createNodeSelectionTree ();

  availableAnnotationsTree.addTreeSelectionListener (new AddAnnotationTreeSelectionListener ());
  currentAnnotationsTree.addTreeSelectionListener (new SelectNodesTreeSelectionListener ());


  JScrollPane chooserScrollPane = new JScrollPane (availableAnnotationsTree);
  JPanel chooserPanel = new JPanel ();
  chooserPanel.setLayout (new BorderLayout ());
  chooserPanel.add (chooserScrollPane, BorderLayout.CENTER);
  annotateNodesButton = new JButton ("Apply Annotation to All Nodes");
  annotateNodesButton.setEnabled (false);
  chooserPanel.add (annotateNodesButton, BorderLayout.SOUTH);
  chooserPanel.setBorder (BorderFactory.createCompoundBorder (
                          BorderFactory.createTitledBorder(
                         "Avaliable"),
                          BorderFactory.createEmptyBorder(10,10,10,10)));

  annotateNodesButton.addActionListener (new ApplyAnnotationAction ());
  topPanel.add (chooserPanel);  

  JPanel currentAnnotationsButtonPanel = new JPanel ();
  currentAnnotationsButtonPanel.setLayout (new GridLayout (0,2));
  layoutByAnnotationButton = new JButton ("Layout");
  layoutByAnnotationButton.setEnabled (false);
  currentAnnotationsButtonPanel.add (layoutByAnnotationButton);
  layoutByAnnotationButton.addActionListener (new LayoutByAnnotationAction ());
  addSharedAnnotationEdgesButton = new JButton ("Add Edges");
  currentAnnotationsButtonPanel.add (addSharedAnnotationEdgesButton);
  addSharedAnnotationEdgesButton.setEnabled (false);
  addSharedAnnotationEdgesButton.addActionListener (new DrawSharedEdgesAnnotationAction ());

  JScrollPane currentChoicesScrollPane = new JScrollPane (currentAnnotationsTree);
  currentChoicesScrollPane.setPreferredSize (chooserScrollPane.getPreferredSize ());
  JPanel panel3 = new JPanel ();
  panel3.setLayout (new BorderLayout ());
  panel3.add (currentChoicesScrollPane, BorderLayout.CENTER);
  panel3.setBorder (BorderFactory.createCompoundBorder (
                          BorderFactory.createTitledBorder(
                         "Current Annotations"),
                          BorderFactory.createEmptyBorder(10,10,10,10)));

  panel3.add (currentAnnotationsButtonPanel, BorderLayout.SOUTH);
  topPanel.add (panel3);

  mainPanel.add (topPanel, BorderLayout.CENTER);
  JPanel dismissButtonPanel = new JPanel ();
  JButton dismissButton = new JButton ("Dismiss");
  dismissButton.addActionListener (new DismissAction ());
  dismissButtonPanel.add (dismissButton);
  mainPanel.add (dismissButtonPanel, BorderLayout.SOUTH);

  return mainPanel;

} // createWidgets
//------------------------------------------------------------------------------
private void expandAll (JTree tree, TreePath parent, boolean expand) 
{
  TreeNode node = (TreeNode) parent.getLastPathComponent ();
  if (node.getChildCount() >= 0) {
    for (Enumeration e=node.children(); e.hasMoreElements(); ) {
      TreeNode n = (TreeNode) e.nextElement();
      TreePath path = parent.pathByAddingChild (n);
      expandAll (tree, path, expand);
      } // for
    } // if

  if (expand)
    tree.expandPath(parent);
  else
    tree.collapsePath(parent);

} // expandAll
//------------------------------------------------------------------------------
protected JTree createAvailableAnnotationsTree ()
{
  DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Available Annotations");
  createTreeNodes (root, annotationDescriptions);
  JTree tree = new JTree (root);
  tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
  tree.setRootVisible (false);
  tree.setShowsRootHandles (true);
  // expandAll (tree, new TreePath (root), true);
  return tree;
}
//------------------------------------------------------------------------------
protected JTree createNodeSelectionTree ()
{
  DefaultMutableTreeNode root = new DefaultMutableTreeNode ("Annotations Categories");
  JTree tree = new JTree (root);
  //tree.getSelectionModel().setSelectionMode (TreeSelectionModel.SINGLE_TREE_SELECTION);
  tree.setRootVisible (false);
  tree.setShowsRootHandles (true);
  expandAll (tree, new TreePath (root), true);
  return tree;

} // createNodeSelectionTree
//------------------------------------------------------------------------------
class AddAnnotationTreeSelectionListener implements TreeSelectionListener {

  public void valueChanged (TreeSelectionEvent e) {
    DefaultMutableTreeNode node = 
      (DefaultMutableTreeNode) availableAnnotationsTree.getLastSelectedPathComponent ();
    if (node == null) return;
    if (!node.isLeaf ()) return;
    annotationPath = availableAnnotationsTree.getSelectionPaths()[0];
    Object nodeInfo = node.getUserObject ();
    annotateNodesButton.setEnabled (true);
    }

} // inner class AddAnnotationTreeSelectionListener
//-----------------------------------------------------------------------------------
class SelectNodesTreeSelectionListener implements TreeSelectionListener {

  public void valueChanged (TreeSelectionEvent e) {
    DefaultMutableTreeNode node = 
        (DefaultMutableTreeNode) currentAnnotationsTree.getLastSelectedPathComponent ();
    if (node == null) return;
    layoutByAnnotationButton.setEnabled (!node.isLeaf ());
    addSharedAnnotationEdgesButton.setEnabled (!node.isLeaf ());
    if (!node.isLeaf ()) return;

    TreePath [] selectedPaths = currentAnnotationsTree.getSelectionPaths();
    HashMap selectionHash = extractAnnotationsFromSelection (selectedPaths);
    String [] annotationNames = (String []) selectionHash.keySet().toArray (new String [0]);
    for (int i=0; i < annotationNames.length; i++) {
      String name = annotationNames [i];
      Vector list = (Vector) selectionHash.get (name);
      String [] categories = (String []) list.toArray (new String [0]);
      Node [] nodesInCategory = getNodesByAttributeValues (name, categories);
      boolean clearPreviousSelections = false;
      if (i == 0) clearPreviousSelections = true;
      cytoscapeWindow.selectNodes (nodesInCategory, clearPreviousSelections);
      cytoscapeWindow.redrawGraph ();
      }

    } // valueChanged

//-----------------------------------------------------------------------------
/**
 * create a hashmap, <String, String []>: 
 *
 *   "KEGG Metabolic Pathway (level 1)" -> (Genetic Information Processing)
 *   "KEGG Metabolic Pathway (level 2)" -> (Amino Acid Metabolism, Nucleotide Metabolism)
 *
 * this method is brittle, and will fail if the structure of the tree changes:
 * it expects 
 *    level 0: root
 *    level 1: a standard annotation name (see above)
 *    level 2: standard annotation category name (also see above)
 */
protected HashMap extractAnnotationsFromSelection (TreePath [] paths)
{
  HashMap hash = new HashMap ();

  for (int i=0; i < paths.length; i++) {
    String annotationName  = paths[i].getPathComponent (1).toString ();
    String annotationValue = paths[i].getPathComponent (2).toString ();
    Vector list;
    if (!hash.containsKey (annotationName)) {
      list = new Vector ();
      hash.put (annotationName, list);
      }
    list = (Vector) hash.get (annotationName);
    list.add (annotationValue);
     } // for i

  String [] keys = (String []) hash.keySet().toArray (new String [0]);

  return hash;

} // extractAnnotationsFromSelection
//-----------------------------------------------------------------------------
protected Node [] getNodesByAttributeValues (String attributeName, String [] targetValues)
{
  Vector collector = new Vector ();
  Node nodes [] = graph.getNodeArray ();
  for (int i=0; i < nodes.length; i++) {
    String canonicalName = cytoscapeWindow.getCanonicalNodeName (nodes [i]);
    if (canonicalName == null) continue;
    Object attributeValue = (Object) nodeAttributes.getValue (attributeName, canonicalName);
    if (attributeValue == null) continue;
    String [] parsedCategories = unpackPossiblyCompoundStringAttributeValue (attributeValue);
    for (int c=0; c < parsedCategories.length; c++) 
      for (int t=0; t < targetValues.length; t++)
        if (targetValues [t].equals (parsedCategories [c]))
          collector.add (nodes [i]);
    } // for i

  return (Node []) collector.toArray (new Node [0]);

} // getNodesByAttributeVAlues
//----------------------------------------------------------------------------------------
public String [] unpackPossiblyCompoundStringAttributeValue (Object value)
{
  String [] result = new String [0];
  try {
    if (value.getClass () == Class.forName ("java.lang.String")) {
      result = new String [1];
      result [0] = (String) value;
      }    
    else if (value.getClass () == Class.forName ("[Ljava.lang.String;")) {
      result = (String []) value; 
      }
    } // try
  catch (ClassNotFoundException ignore) {
    ignore.printStackTrace ();
    }

  return result;

} // unpackPossiblyCompoundAttributeValue
//----------------------------------------------------------------------------------------
} // inner class SelectNodesTreeSelectionListener
//-----------------------------------------------------------------------------------
protected void createTreeNodes (DefaultMutableTreeNode root, 
                                AnnotationDescription [] descriptions)
// for each of the descriptions, and only if the description is of a species in the 
// current graph:  create a 'topLevelName' which will be the branch of the JTree,
// and a set of leaves for each logical level in that description's ontology
{
  if (descriptions == null || descriptions.length == 0) return;

  ArrayList speciesInGraph =  new ArrayList (Arrays.asList (cytoscapeWindow.getAllSpecies ()));

  DefaultMutableTreeNode branch = null;
  DefaultMutableTreeNode leaf = null;
  Vector topLevelNamesList = new Vector ();

  for (int i=0; i < descriptions.length; i++) {
    String species = descriptions[i].getSpecies ();
    if (!speciesInGraph.contains (species)) {
      continue;
      }
    topLevelNamesList.add (descriptions[i].getCurator () + ", " +
                           descriptions[i].getType () + ", " +
                           descriptions[i].getSpecies ());
    branch = new DefaultMutableTreeNode (descriptions [i]);
    Annotation annotation = dataServer.getAnnotation (descriptions [i]);
    int maxDepth = annotation.maxDepth ();
    for (int level=0; level < maxDepth; level++) 
      branch.add (new DefaultMutableTreeNode (new Integer (level + 1)));
    root.add (branch);
    }

} // createTreeNodes
//-----------------------------------------------------------------------------------
public class DismissAction extends AbstractAction 
{
  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    Gui.this.dispose ();
    }

} // DismissAction
//-----------------------------------------------------------------------------
public class LayoutByAnnotationAction  extends AbstractAction 
{
  LayoutByAnnotationAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    attributeLayouter.doCallback (currentAnnotationCategory, AttributeLayout.DO_LAYOUT);
    }

} // LayoutByAnnotationAction
//-----------------------------------------------------------------------------
public class DrawSharedEdgesAnnotationAction extends AbstractAction 
{
  DrawSharedEdgesAnnotationAction  () {super ("");}

  public void actionPerformed (ActionEvent e) {
    attributeLayouter.doCallback (currentAnnotationCategory, AttributeLayout.CREATE_EDGES);
    }

} // DrawSharedEdgesAnnotationAction
//-----------------------------------------------------------------------------
} // inner class Gui
//-----------------------------------------------------------------------------------
public String addAnnotationToNodes (AnnotationDescription aDesc, int level)
{
     // something like "GO biological process" or "KEGG metabolic pathway"
  String baseAnnotationName = aDesc.getCurator () + " " + aDesc.getType ();
  String annotationNameAtLevel = baseAnnotationName  + " (level " + level + ")";
  String annotationNameForLeafIDs = baseAnnotationName  + " leaf IDs";
     // make a fresh start
  nodeAttributes.deleteAttribute (annotationNameAtLevel);
  nodeAttributes.deleteAttribute (annotationNameForLeafIDs);


  cytoscapeWindow.setInteractivity (false);
  HashMap nodeNameMap = nodeAttributes.getNameMap ();
  String [] canonicalNodeNames = (String []) nodeNameMap.values().toArray(new String [0]);

  int unAnnotatedNodeCount = 0;
  for (int i=0; i < canonicalNodeNames.length; i++) {
    String [][] fullAnnotations = dataServer.getAllAnnotations (aDesc, canonicalNodeNames [i]);
    if (fullAnnotations.length == 0)
      unAnnotatedNodeCount++;
    else {
      String [] uniqueAnnotationsAtLevel = collapseToUniqueAnnotationsAtLevel (fullAnnotations, level);
      nodeAttributes.set (annotationNameAtLevel, canonicalNodeNames [i], uniqueAnnotationsAtLevel);
      int [] annotationIDs = dataServer.getClassifications (aDesc, canonicalNodeNames [i]);
      Integer [] integerArray = new Integer [annotationIDs.length];
      for (int j=0; j < annotationIDs.length; j++)
        integerArray [j] = new Integer (annotationIDs [j]);
      //nodeAttributes.add (annotationNameForLeafIDs, canonicalNodeNames [i], integerArray);
      } // else: this node is annotated
    } // for i

  nodeAttributes.setCategory (annotationNameAtLevel, "annotation");
  cytoscapeWindow.setInteractivity (true);

  return annotationNameAtLevel;

} // addAnnotationToNodes
//----------------------------------------------------------------------------------------
/**
 *  return only the unique categories (typically representing gene annotations) 
 *  which exist at the specified level.
 *  genes (for example) may be annotated as belonging to two or more different categories,
 *  but at a higher logical level, these different categories may merge.  for example,
 *  the halobacterium gene VNG0623G is classified by KEGG, in its 3-level ontology
 *  as belonging to two categories at the most specific (third) level
 *  <ol>
 *    <li> Valine, leucine and isoleucine degradation
 *    <li> Propanoate metabolism
 *  <ol>
 *  At level two, the annotation is:
 *  <ol>
 *    <li> Amino Acid Metabolism
 *    <li> Carbohydrate Metabolism
 *  <ol>
 *  At level one, the annotation is simply
 *  <ol>
 *    <li>Metabolism
 *  <ol>
 *  This method calculates and returns the unique annotations for a biological entity
 *  at the specified level.
 */
private String [] collapseToUniqueAnnotationsAtLevel (String [][] fullAnnotations, int level)
{
  Vector collector = new Vector ();

  for (int i=0; i < fullAnnotations.length; i++) {
    int indexOfClosestAvailableLevel = level - 1;
    if (indexOfClosestAvailableLevel > (fullAnnotations [i].length - 1))
      indexOfClosestAvailableLevel = fullAnnotations [i].length - 1;
    String annotationAtLevel = fullAnnotations [i][indexOfClosestAvailableLevel];
    if (!collector.contains (annotationAtLevel))
      collector.add (annotationAtLevel);
    } // for i

  return (String []) collector.toArray (new String [0]);

} // collapseToUniqueAnnotationsAtLevel
//----------------------------------------------------------------------------------------
class ApplyAnnotationAction extends AbstractAction {
  ApplyAnnotationAction () {
    super (""); 
    }

  public void actionPerformed (ActionEvent e) {
    int max = annotationPath.getPathCount ();
    for (int i=0; i < max; i++) {
      DefaultMutableTreeNode node = (DefaultMutableTreeNode) annotationPath.getPathComponent (i);
      Object userObj = node.getUserObject ();
      }

    DefaultMutableTreeNode node1 = (DefaultMutableTreeNode) annotationPath.getPathComponent (1);
    AnnotationDescription aDesc = (AnnotationDescription) node1.getUserObject ();
    DefaultMutableTreeNode node2 = (DefaultMutableTreeNode) annotationPath.getPathComponent (2);
    int level = ((Integer) node2.getUserObject ()).intValue ();
    if (aDesc == null) return;
    cytoscapeWindow.setInteractivity (false);
    currentAnnotationCategory = addAnnotationToNodes (aDesc, level);
    Object [] uniqueAnnotationValues = nodeAttributes.getUniqueValues (currentAnnotationCategory);
    if (uniqueAnnotationValues != null && 
        uniqueAnnotationValues.length > 0 && 
        uniqueAnnotationValues [0].getClass() == "string".getClass ()) {
      java.util.Arrays.sort (uniqueAnnotationValues, String.CASE_INSENSITIVE_ORDER);
      appendToSelectionTree (currentAnnotationCategory, uniqueAnnotationValues);
      }
    cytoscapeWindow.setInteractivity (true);
    }

//--------------------------------------------------------------------------------------
protected void appendToSelectionTree (String currentAnnotationCategory, 
                                      Object [] uniqueAnnotationValues)
{
  DefaultMutableTreeNode branch = new DefaultMutableTreeNode (currentAnnotationCategory);

  for (int i=0; i < uniqueAnnotationValues.length; i++)
    branch.add (new DefaultMutableTreeNode (uniqueAnnotationValues [i]));

  DefaultMutableTreeNode root = (DefaultMutableTreeNode) currentAnnotationsTree.getModel().getRoot();
  DefaultTreeModel model = (DefaultTreeModel) currentAnnotationsTree.getModel ();
  model.insertNodeInto (branch, root, root.getChildCount ());
  currentAnnotationsTree.scrollPathToVisible (new TreePath (branch.getPath ()));
  model.reload ();

} // appendToSelectionTree
//-----------------------------------------------------------------------------
} // inner class ApplyAnnotationAction
class ActionListBoxSelectionListener implements ListSelectionListener {

  public void valueChanged (ListSelectionEvent e) {
    if (e.getValueIsAdjusting ()) return;
    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
    Vector selectedCategoryNames = new Vector ();
    if (!lsm.isSelectionEmpty()) {
      int minIndex = lsm.getMinSelectionIndex ();
      int maxIndex = lsm.getMaxSelectionIndex ();
      for (int i = minIndex; i <= maxIndex; i++) {
        if (lsm.isSelectedIndex (i)) {
          Object obj = actionListModel.elementAt (i);
          currentAnnotationCategory = (String) obj;
          }
        } // for i
      } // if !empty
    } // valueChanged

} // inner class AttributeListSelectionListener
//-----------------------------------------------------------------------------
} // class AnnotationGui
