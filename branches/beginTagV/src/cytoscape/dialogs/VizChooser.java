// VizChooser
//----------------------------------------------------------------------------------------
// $Revision$ 
// $Date$
// $Author$
//----------------------------------------------------------------------------------------
package cytoscape.dialogs;
//----------------------------------------------------------------------------------------
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

import java.util.HashMap;

import cytoscape.vizmap.*;
import cytoscape.VizChooserClient;
import cytoscape.GraphObjAttributes;
import cytoscape.data.*;
//-------------------------------------------------------------------------------------
public class VizChooser extends JDialog {
  VizChooserClient theClient;
  VizChooser thisChooser;
  NodeViz nodeViz;
  GraphObjAttributes nodeAttributes;
  //GraphObjAttributes edgeAttributes;
  ExpressionData expressionData;
  JColorChooser colorChooser;
  JTabbedPane tabbedPane;
    Frame parent;
  final String MINCOLOR    = "  Minimum Color...";
  final String MAXCOLOR    = "  Maximum Color...";
  final String ZEROCOLOR   = "Threshold Color...";
  final String BINID       = "Bins per Color Scale";
  final String MAXID       = "Max Value";
  final String MINID       = "Min Value";
  final String EDGEID      = "Edge Thickness";
  final String THRESHOLDID = "Threshold";


//-------------------------------------------------------------------------------------
public VizChooser(){super();}

public VizChooser (VizChooserClient theClient, Frame parent, 
                   NodeViz nodeViz,
                   GraphObjAttributes nodeAttributes,
                   //GraphObjAttributes edgeAttributes,
                   ExpressionData expressionData) 
{
  super (parent, false);
  setTitle ("VizChooser");
  thisChooser = this;
  this.parent = parent;
  this.theClient = theClient;
  this.nodeViz = nodeViz;
  this.nodeAttributes = nodeAttributes;
  //this.edgeAttributes = edgeAttributes;
  this.expressionData = expressionData;

  JPanel panel = new JPanel ();
  panel.setLayout (new BorderLayout ());
  tabbedPane = new JTabbedPane ();

  panel.add (tabbedPane);

  if (expressionData!=null)
      tabbedPane.addTab("Summary", createSummaryPanel("expression"));
  else
      tabbedPane.addTab("Summary", createSummaryPanel("default"));

  if(expressionData != null)
      tabbedPane.addTab("Expression Settings", createExpressionSettingsPanel());

  for (int i=0; i < nodeAttributes.size (); i++) {
    String attributeName = nodeAttributes.getAttributeNames()[i];
    HashMap attributeMap = nodeAttributes.getAttribute (attributeName);
    tabbedPane.addTab (attributeName, createAttributeRenderingChooser (
                                            attributeName, attributeMap));
    }
  
  /****************
  for (int i=0; i<edgeAttributes.size();i++) {
    String edgeAttributeName = edgeAttributes.getAttributeNames()[i];
    HashMap edgeAttributeMap = edgeAttributes.getAttribute(edgeAttributeName);
    Class attributeClass = getAttributeValueClass (edgeAttributeMap);
    try {
      boolean numericalData = (attributeClass == (Class.forName ("java.lang.Double")) ||
                               attributeClass == (Class.forName ("java.lang.Integer")));
      if (numericalData) 
         tabbedPane.addTab(edgeAttributeName, createEdgeAttributeRenderingChooser(
                           edgeAttributeName, edgeAttributeMap));
      }
    catch (ClassNotFoundException ignore) {}
    // todo (pshannon, 22 feb 2002): with signficant refactoring along the way,
    //      create an edgeAttribute controller for non-numerical data.
    }
  ****************/

  tabbedPane.addTab ("Default Settings", createDefaultSettingsPanel());
  setContentPane (panel);
  pack();
  Dimension originalDimension = tabbedPane.getPreferredSize();
  int heightTotal = tabbedPane.getHeight();
  tabbedPane.setPreferredSize(new Dimension(2000,1000));//larger than necessary
  pack();
  int tabCounter = tabbedPane.getTabCount();
  int widthTotal = 0;
  for (int i=0;i<tabCounter;i++){
      Rectangle tempRect = tabbedPane.getBoundsAt(i);
      widthTotal +=  tempRect.width; 
  }
  if (originalDimension.getWidth()<widthTotal&&widthTotal<1000){
      tabbedPane.setPreferredSize(new Dimension(widthTotal+10, heightTotal));//added 10 to make the width big enough to fit on one line
  }
  else{
      tabbedPane.setPreferredSize(originalDimension);
  }
  this.setLocation(parent.getLocationOnScreen());
  pack();
  setVisible (true);
} // ctor
//-------------------------------------------------------------------------------------
private JPanel createAttributeRenderingChooser (String attribute, HashMap attributeMap)
{
  final JPanel mainPanel = new JPanel ();
  mainPanel.setLayout (new BorderLayout ());

  JTabbedPane tabPane = new JTabbedPane ();
  mainPanel.add (tabPane, BorderLayout.CENTER);

  String attributeName = attribute;
  double minSliderValue, zeroSliderValue, maxSliderValue, binSliderValue;

  double [] minMax = findMinMax (attributeMap);


  String valueRangeMessage = attributeName + " values range from " +  minMax [0] + 
                                             " to " + minMax [1] + ".";
  JLabel textValues = new JLabel(valueRangeMessage);
  mainPanel.add(textValues,  BorderLayout.NORTH);


  JPanel colorPanel = new JPanel ();
  colorPanel.setLayout (new BorderLayout ());

    //-----------------------------------------------------
    // add Node Color tab
    //-----------------------------------------------------

  tabPane.addTab ("Node Color", colorPanel);
  JPanel binPanel  = new JPanel();
  JPanel minPanel  = new JPanel();
  JPanel zeroPanel = new JPanel();
  JPanel maxPanel  = new JPanel();

  JPanel sliderPanel = new JPanel ();
  sliderPanel.setLayout(new GridLayout(0,1));
  NewSlider binSlider = new NewSlider(attributeName,BINID, 2, 40, 20);
  binPanel.add(binSlider);
  ColorPopupButton minButton = new ColorPopupButton(attributeName,MINCOLOR, Color.red);
  NewSlider lowSlider = new NewSlider(attributeName,MINID, minMax[0], minMax[1], minMax[0], 4); 
  minPanel.add(minButton);
  minPanel.add(lowSlider);
  
  ColorPopupButton zeroButton = new ColorPopupButton(attributeName,ZEROCOLOR, Color.white);

  int dataRange = (int)(minMax[1]-minMax[0]);
  int currentValue= (int)(minMax[0]+(dataRange/4)); 
  NewSlider zeroSlider= new NewSlider (attributeName,THRESHOLDID, minMax[0], minMax[1],
                                       currentValue, 4);
  zeroPanel.add(zeroButton);
  zeroPanel.add(zeroSlider);

  ColorPopupButton maxButton = new ColorPopupButton(attributeName,MAXCOLOR, Color.green);
  NewSlider highSlider = new NewSlider(attributeName,MAXID,minMax[0], minMax[1], minMax[1], 4);
  maxPanel.add(maxButton);
  maxPanel.add(highSlider);

  sliderPanel.add(binPanel);
  sliderPanel.add(minPanel);
  sliderPanel.add(zeroPanel);
  sliderPanel.add(maxPanel);

  colorPanel.add (sliderPanel, BorderLayout.CENTER);

  JPanel buttonPanel = new JPanel ();
  JButton applyButton = new JButton ("Apply");
  JButton dismissButton = new JButton ("Dismiss");
  applyButton.addActionListener (new ApplyNodeColorAction (attributeName, 
                                                           minMax [0], minMax [1],
                                                           binSlider,
                                                           lowSlider, minButton,
                                                           zeroSlider, zeroButton,
                                                           highSlider, maxButton));
  dismissButton.addActionListener (new DismissAction ());
  buttonPanel.add (applyButton, BorderLayout.CENTER);
  buttonPanel.add (dismissButton, BorderLayout.SOUTH);//make this EAST?
  colorPanel.add (buttonPanel, BorderLayout.SOUTH);

    //-----------------------------------------------------
    // add Border Color/Thickness tab
    //-----------------------------------------------------

  JPanel borderPanel = new JPanel ();
  borderPanel.setLayout(new BorderLayout());

  tabPane.addTab ("Node Border Thickness", borderPanel);  

  JPanel borderBinPanel   = new JPanel();
  JPanel borderThickPanel = new JPanel();
  JPanel borderMinPanel   = new JPanel();
  JPanel borderZeroPanel  = new JPanel();
  JPanel borderMaxPanel   = new JPanel();

  JPanel borderSliderPanel = new JPanel();
  borderSliderPanel.setLayout(new GridLayout(0,1));
  NewSlider borderBinSlider = new NewSlider(attributeName, BINID, 2, 40, 20);
  borderBinPanel.add(borderBinSlider);
  NewSlider borderThickSlider = new NewSlider(attributeName, EDGEID, 1,7,2);
  borderThickPanel.add(borderThickSlider);
  
  ColorPopupButton borderMinColor = new ColorPopupButton(attributeName,MINCOLOR, nodeViz.getDefaultBorderColor());
  NewSlider borderLowSlider = new NewSlider(attributeName,MINID, minMax[0], minMax[1], minMax[0], 4);
  borderMinPanel.add(borderMinColor);
  borderMinPanel.add(borderLowSlider);

  ColorPopupButton borderMaxColor = new ColorPopupButton(attributeName,MAXCOLOR, nodeViz.getDefaultBorderColor());
  NewSlider borderHighSlider = new NewSlider(attributeName,MAXID,minMax[0], minMax[1], minMax[1], 4);
  borderMaxPanel.add(borderMaxColor);
  borderMaxPanel.add(borderHighSlider);  

  ColorPopupButton borderZeroColor = new ColorPopupButton(attributeName,ZEROCOLOR, nodeViz.getDefaultBorderColor());

  int borderCurrentValue = (int)(minMax[0]+((minMax[1]-minMax[0])/4));
  NewSlider borderZeroSlider= new NewSlider(attributeName,THRESHOLDID, minMax[0], minMax[1],borderCurrentValue, 4);
  borderZeroPanel.add(borderZeroColor);
  borderZeroPanel.add(borderZeroSlider);

  borderSliderPanel.add(borderBinPanel);
  borderSliderPanel.add(borderThickPanel);
  borderSliderPanel.add(borderMinPanel);
  borderSliderPanel.add(borderZeroPanel);
  borderSliderPanel.add(borderMaxPanel);

  borderPanel.add (borderSliderPanel, BorderLayout.CENTER);

  JPanel borderButtonPanel = new JPanel ();
  JButton borderApplyButton = new JButton ("Apply");
  JButton borderDismissButton = new JButton ("Dismiss");
 borderApplyButton.addActionListener (new ApplyBorderColorAction (attributeName, minMax[0], minMax[1],
                                                                   borderBinSlider, borderThickSlider,
                                                                   borderLowSlider, borderMinColor,
                                                                   borderZeroSlider,borderZeroColor,
                                                                   borderHighSlider,borderMaxColor));
  borderDismissButton.addActionListener (new DismissAction ());
  borderButtonPanel.add (borderApplyButton, BorderLayout.CENTER);
  borderButtonPanel.add (borderDismissButton, BorderLayout.EAST);//is EAST correct?
  borderPanel.add (borderButtonPanel, BorderLayout.SOUTH);

  return mainPanel;

} // createAttributeRenderingChooser
//------------------------------------------------------------------------------------
/****************
private JPanel createEdgeAttributeRenderingChooser (String attributeName, HashMap attributeMap)
{
  final JPanel mainPanel = new JPanel ();
  mainPanel.setLayout (new BorderLayout ());

  JTabbedPane tabPane = new JTabbedPane ();
  mainPanel.add (tabPane, BorderLayout.CENTER);

  double minSliderValue, zeroSliderValue, maxSliderValue, binSliderValue;

  double [] minMax = findMinMax (attributeMap);
  String valueRangeMessage = attributeName + " values range from " +  minMax [0] + 
                                             " to " + minMax [1] + ".";
  JLabel textValues = new JLabel(valueRangeMessage);
  mainPanel.add(textValues,  BorderLayout.NORTH);


  JPanel colorPanel = new JPanel ();
  colorPanel.setLayout (new BorderLayout ());

    //-----------------------------------------------------
    // add Edge Tab
    //-----------------------------------------------------

  JPanel edgePanel = new JPanel ();
  edgePanel.setLayout(new BorderLayout());

  tabPane.addTab ("Edge Properties", edgePanel);  

  JPanel edgeBinPanel   = new JPanel();
  JPanel edgeThickPanel = new JPanel();
  JPanel edgeMinPanel   = new JPanel();
  JPanel edgeZeroPanel  = new JPanel();
  JPanel edgeMaxPanel   = new JPanel();

  JPanel edgeSliderPanel = new JPanel();
  edgeSliderPanel.setLayout(new GridLayout(0,1));
  NewSlider edgeBinSlider = new NewSlider(attributeName, BINID, 2, 40, 20);
  edgeBinPanel.add(edgeBinSlider);

  NewSlider edgeThickSlider = new NewSlider(attributeName, EDGEID, 1,7,2);
  edgeThickPanel.add(edgeThickSlider);
  
  ColorPopupButton edgeMinColor = new ColorPopupButton(attributeName,MINCOLOR, Color.red);
  NewSlider edgeLowSlider = new NewSlider(attributeName,MINID, minMax[0], minMax[1], minMax[0], 4);
  edgeMinPanel.add(edgeMinColor);
  edgeMinPanel.add(edgeLowSlider);

  ColorPopupButton edgeZeroColor = new ColorPopupButton(attributeName,ZEROCOLOR, Color.white);
  NewSlider edgeZeroSlider= new NewSlider(attributeName,THRESHOLDID, minMax[0], minMax[1],(minMax[0]+minMax[1])/4, 4);
  edgeZeroPanel.add(edgeZeroColor);
  edgeZeroPanel.add(edgeZeroSlider);

  ColorPopupButton edgeMaxColor = new ColorPopupButton(attributeName,MAXCOLOR, Color.green);
  NewSlider edgeHighSlider = new NewSlider(attributeName,MAXID,minMax[0], minMax[1], minMax[1], 4);
  edgeMaxPanel.add(edgeMaxColor);
  edgeMaxPanel.add(edgeHighSlider);  

  edgeSliderPanel.add(edgeBinPanel);
  edgeSliderPanel.add(edgeThickPanel);
  edgeSliderPanel.add(edgeMinPanel);
  edgeSliderPanel.add(edgeZeroPanel);
  edgeSliderPanel.add(edgeMaxPanel);

  edgePanel.add (edgeSliderPanel, BorderLayout.CENTER);

  JPanel edgeButtonPanel = new JPanel ();
  JButton edgeApplyButton = new JButton ("Apply");
  JButton edgeDismissButton = new JButton ("Dismiss");
  edgeApplyButton.addActionListener (new ApplyEdgeColorAction (attributeName, minMax[0], minMax[1],
                                                                 edgeBinSlider, edgeThickSlider,
                                                                 edgeLowSlider, edgeMinColor,
                                                                 edgeZeroSlider,edgeZeroColor,
                                                                 edgeHighSlider,edgeMaxColor));
  edgeDismissButton.addActionListener (new DismissAction ());
  edgeButtonPanel.add (edgeApplyButton, BorderLayout.CENTER);
  edgeButtonPanel.add (edgeDismissButton, BorderLayout.EAST);//is EAST correct?
  edgePanel.add (edgeButtonPanel, BorderLayout.SOUTH);

  return mainPanel;

} // createEdgeAttributeRenderingChooser
****************/
//------------------------------------------------------------------------------------
private JPanel createSummaryPanel(String controller){
    String controlString = controller;
    String nodeTitle     = nodeViz.getNodeAttributeWhichControlsFillColor();
    String borderTitle   = nodeViz.getBorderAttributeWhichControlsFillColor();
    //String edgeTitle     = nodeViz.getEdgeAttributeWhichControlsFillColor();
    Color nodeMinColor, nodeZeroColor, nodeMaxColor;
    Color borderMinColor, borderZeroColor, borderMaxColor;
    //Color edgeMinColor, edgeZeroColor, edgeMaxColor;
    double [][] nodeBins;
    double [][] borderBins;
    //double [][] edgeBins;
    if (controlString == "expression"){
        nodeBins        = nodeViz.getExpressionColorBins();
        nodeMinColor    = nodeViz.getExpressionRatioMinColor();
        nodeZeroColor   = nodeViz.getExpressionRatioZeroColor();
        nodeMaxColor    = nodeViz.getExpressionRatioMaxColor();
        borderBins      = nodeViz.getSignificanceColorBins();
        borderMinColor  = nodeViz.getExpressionSigMinColor();
        borderZeroColor = nodeViz.getExpressionSigZeroColor();
        borderMaxColor  = nodeViz.getExpressionSigMaxColor();
        //edgeBins      = nodeViz.getDefaultEdgeColorBins();
        //edgeMinColor  = nodeViz.getDefaultEdgeColor();
        //edgeZeroColor = nodeViz.getDefaultEdgeColor();
        //edgeMaxColor  = nodeViz.getDefaultEdgeColor();
    }
    else if (controlString == "default"){
        nodeBins        = nodeViz.getDefaultNodeColorBins();
        nodeMinColor    = nodeViz.getDefaultNodeColor();
        nodeZeroColor   = nodeViz.getDefaultNodeColor();
        nodeMaxColor    = nodeViz.getDefaultNodeColor();
        borderBins      = nodeViz.getDefaultBorderColorBins();
        borderMinColor  = nodeViz.getDefaultBorderColor();
        borderZeroColor = nodeViz.getDefaultBorderColor();
        borderMaxColor  = nodeViz.getDefaultBorderColor();
        //edgeBins      = nodeViz.getDefaultEdgeColorBins();
        //edgeMinColor  = nodeViz.getDefaultEdgeColor();
        //edgeZeroColor = nodeViz.getDefaultEdgeColor();
        //edgeMaxColor  = nodeViz.getDefaultEdgeColor();    
    }
    else{
        nodeBins        = nodeViz.getNodeColorBins();
        nodeMinColor    = nodeViz.getNodeMinColor();
        nodeZeroColor   = nodeViz.getNodeZeroColor();
        nodeMaxColor    = nodeViz.getNodeMaxColor();
        borderBins      = nodeViz.getBorderColorBins();
        borderMinColor  = nodeViz.getBorderMinColor();
        borderZeroColor = nodeViz.getBorderZeroColor();
        borderMaxColor  = nodeViz.getBorderMaxColor();
        //edgeBins      = nodeViz.getEdgeColorBins();
        //edgeMinColor  = nodeViz.getEdgeMinColor();
        //edgeZeroColor = nodeViz.getEdgeZeroColor();
        //edgeMaxColor  = nodeViz.getEdgeMaxColor();
    }
    
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());

    JPanel subPanel  = new JPanel();
    subPanel.setLayout(new GridLayout(0,1));

    JPanel nodeColorPanel = createSummarySubPanel("Node",
                                                  nodeTitle,
                                                  nodeMinColor,
                                                  nodeZeroColor,
                                                  nodeMaxColor, 
                                                  nodeBins);
    JPanel borderColorPanel = createSummarySubPanel("Node Border",
                                                    borderTitle,
                                                    borderMinColor,
                                                    borderZeroColor,
                                                    borderMaxColor,
                                                    borderBins);
    //JPanel edgeColorPanel = createSummarySubPanel("Edge",
    //                                              edgeTitle,
    //                                              edgeMinColor,
    //                                              edgeZeroColor,
    //                                              edgeMaxColor,
    //                                              edgeBins);
    subPanel.add(nodeColorPanel);
    subPanel.add(borderColorPanel);
    // subPanel.add(edgeColorPanel);
    mainPanel.add(subPanel, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel ();
    JButton dismissButton = new JButton ("Dismiss");
    dismissButton.addActionListener (new DismissAction ());
    buttonPanel.add (dismissButton);
    mainPanel.add (buttonPanel, BorderLayout.SOUTH);
    
    return mainPanel;
}

private JPanel createSummarySubPanel(String idTitle,String attribute,
                                     Color minC,Color zeroC, Color maxC,
                                     double[][] bins){
    JPanel returnPanel = new JPanel();
    returnPanel.setLayout(new BorderLayout());
    
    JPanel returnSubPanel = new JPanel();

    String criteria = new String(idTitle+" Color Determined by:  ");
    criteria += attribute;
    JLabel criteriaLabel = new JLabel(criteria);

    JPanel extremePanel = new JPanel();
    extremePanel.setLayout(new GridLayout(0,1));

    JPanel minSubPanel  = new JPanel();
    JPanel zeroSubPanel = new JPanel();
    JPanel maxSubPanel  = new JPanel();

    JLabel minTitle = new JLabel(" Minimum Color:  ");
    JLabel zeroTitle= new JLabel("Threshold Color:  ");
    JLabel maxTitle = new JLabel(" Maximum Color:  ");
    JLabel minColor = new JLabel("  ");
    JLabel zeroColor= new JLabel("  ");
    JLabel maxColor = new JLabel("  ");
    minColor.setBackground(minC);
    zeroColor.setBackground(zeroC);
    maxColor.setBackground(maxC);
    minColor.setOpaque(true);
    zeroColor.setOpaque(true);
    maxColor.setOpaque(true);

    minSubPanel.add(minTitle);
    minSubPanel.add(minColor);

    zeroSubPanel.add(zeroTitle);
    zeroSubPanel.add(zeroColor);

    maxSubPanel.add(maxTitle);
    maxSubPanel.add(maxColor);

    extremePanel.add(minSubPanel);
    extremePanel.add(zeroSubPanel);
    extremePanel.add(maxSubPanel);
    ColorScaleTable myColorScaleTable = new ColorScaleTable(100,100, bins);

    returnSubPanel.add(extremePanel);
    returnSubPanel.add(myColorScaleTable);
    returnPanel.add(criteriaLabel, BorderLayout.NORTH);    
    returnPanel.add(returnSubPanel, BorderLayout.CENTER);
    return returnPanel;
}

//------------------------------------------------------------------------------------
private JPanel createExpressionSettingsPanel(){
    double [][] extremeValues = nodeViz.getExtremeValues();
    double ratioMin, ratioMax, sigMin, sigMax;
    ratioMin = extremeValues[0][0];
    ratioMax = extremeValues[0][1];
    sigMin   = extremeValues[1][0];
    sigMax   = extremeValues[1][1];   


    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BorderLayout());
   
    String instructionString = new String("Specify Settings for Displaying Data:");
    instructionString += "\n"+ "Each time a new data set is viewed, these settings will be applied.";
    JTextArea instructions = new JTextArea(instructionString);
    mainPanel.add(instructions, BorderLayout.NORTH);

    JTabbedPane tabs = new JTabbedPane();
    JPanel ratioSubPanel  = new JPanel();
    JPanel sigSubPanel    = new JPanel(); 

    JPanel ratioMinPanel  = new JPanel();
    JPanel ratioZeroPanel = new JPanel();
    JPanel ratioMaxPanel  = new JPanel();

    JPanel sigMinPanel    = new JPanel();
    JPanel sigZeroPanel   = new JPanel();
    JPanel sigMaxPanel    = new JPanel();    
    
    ratioSubPanel.setLayout(new GridLayout(0,1));
    sigSubPanel.setLayout(new GridLayout(0,1));
    NewSlider ratioBinSlider = new NewSlider("Bins", 2, 40,nodeViz.getExpressionRatioBinValue());
    NewSlider sigBinSlider   = new NewSlider("Bins", 2, 40,nodeViz.getExpressionSigBinValue());



    NewSlider thicknessSlider = new NewSlider("Border Thickness", 2,7,nodeViz.getDefaultBorderThickness());
   
    NewSlider ratioMinSlider = new NewSlider(MINID,ratioMin,ratioMax,nodeViz.getExpressionRatioMinValue(),4);
    NewSlider sigMinSlider   = new NewSlider(MINID,sigMin  ,sigMax  ,nodeViz.getExpressionSigMinValue() ,4);
    ColorPopupButton ratioMinColor = new ColorPopupButton(MINCOLOR,nodeViz.getExpressionRatioMinColor());
    ColorPopupButton sigMinColor   = new ColorPopupButton(MINCOLOR,nodeViz.getExpressionSigMinColor());
    ratioMinPanel.add(ratioMinColor);
    ratioMinPanel.add(ratioMinSlider);
    sigMinPanel.add(sigMinColor);
    sigMinPanel.add(sigMinSlider);
    
    NewSlider ratioZeroSlider = new NewSlider(THRESHOLDID,ratioMin,ratioMax,nodeViz.getExpressionRatioZeroValue(),4);
    NewSlider sigZeroSlider   = new NewSlider(THRESHOLDID,sigMin  ,sigMax  ,nodeViz.getExpressionSigZeroValue(),4);
    ColorPopupButton ratioZeroColor = new ColorPopupButton(ZEROCOLOR,nodeViz.getExpressionRatioZeroColor());
    ColorPopupButton sigZeroColor   = new ColorPopupButton(ZEROCOLOR,nodeViz.getExpressionSigZeroColor());
    ratioZeroPanel.add(ratioZeroColor);
    ratioZeroPanel.add(ratioZeroSlider);
    sigZeroPanel.add(sigZeroColor);
    sigZeroPanel.add(sigZeroSlider);    
    
    NewSlider ratioMaxSlider = new NewSlider(MAXID,ratioMin,ratioMax,nodeViz.getExpressionRatioMaxValue(),4);
    NewSlider sigMaxSlider   = new NewSlider(MAXID,sigMin  ,sigMax  ,nodeViz.getExpressionSigMaxValue(),4);
    ColorPopupButton ratioMaxColor = new ColorPopupButton(MAXCOLOR,nodeViz.getExpressionRatioMaxColor());
    ColorPopupButton sigMaxColor   = new ColorPopupButton(MAXCOLOR,nodeViz.getExpressionSigMaxColor());
    ratioMaxPanel.add(ratioMaxColor);
    ratioMaxPanel.add(ratioMaxSlider);
    sigMaxPanel.add(sigMaxColor);
    sigMaxPanel.add(sigMaxSlider);


    ratioSubPanel.add(ratioBinSlider);
    ratioSubPanel.add(ratioMinPanel);
    ratioSubPanel.add(ratioZeroPanel);
    ratioSubPanel.add(ratioMaxPanel);

    sigSubPanel.add(sigBinSlider);
    sigSubPanel.add(thicknessSlider);
    sigSubPanel.add(sigMinPanel);
    sigSubPanel.add(sigZeroPanel);
    sigSubPanel.add(sigMaxPanel);    
    
    tabs.addTab("Ratio/Node Color", ratioSubPanel);
    tabs.addTab("Significance/Border Color", sigSubPanel);

    mainPanel.add(tabs, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel ();
    JButton applyButton = new JButton ("Apply");
    JButton dismissButton = new JButton ("Dismiss");
    
    applyButton.addActionListener (new ApplyExpressionColorAction (ratioBinSlider,
                                                                   ratioMinSlider,  ratioMinColor,
                                                                   ratioZeroSlider, ratioZeroColor,
                                                                   ratioMaxSlider,  ratioMaxColor,
                                                                   sigBinSlider,
                                                                   sigMinSlider,  sigMinColor,
                                                                   sigZeroSlider, sigZeroColor,
                                                                   sigMaxSlider,  sigMaxColor,
                                                                   thicknessSlider));
    buttonPanel.add (applyButton, BorderLayout.CENTER);
    dismissButton.addActionListener (new DismissAction ());
    buttonPanel.add (dismissButton, BorderLayout.SOUTH);
    mainPanel.add (buttonPanel, BorderLayout.SOUTH);
    
    return mainPanel;
}//createExpressionSettingsPanel
//------------------------------------------------------------------------------------
/**
 * present choosers for the default color of node, node border, edge, and background.
 * the apply button causes all current values to be passed to the VizChooserClient.
 * <br>
 * <b> todo: </b> <i>(pshannon, 26 feb 2002)</i>:  allow user to assign and apply 
 * any one value (for instance, a new background color)  without causing the
 * VizChooserClient to apply the other defaults (node color, node border color, edge 
 * color). 
 * @see VizChooserClient
 */
private JPanel createDefaultSettingsPanel()
{
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayout(0,1));
    
    String tempString = new String("Default Settings:");
    tempString += "\n"+"Click on desired default setting to change its color.";
    JTextArea instructions = new JTextArea(tempString);

    ColorPopupButton nodeDefault =
        new ColorPopupButton("Default Node Color...", nodeViz.getDefaultNodeColor());
    ColorPopupButton borderDefault = 
       new ColorPopupButton("Default Border Color...", nodeViz.getDefaultBorderColor());
    //ColorPopupButton edgeDefault = 
    //   new ColorPopupButton("Default Edge Color...", nodeViz.getDefaultEdgeColor());
    ColorPopupButton backDefault = 
       new ColorPopupButton("Background Color...", nodeViz.getDefaultBackgroundColor());

    JPanel buttonPanel = new JPanel();
    JButton applyButton = new JButton ("Apply");
    JButton dismissButton = new JButton ("Dismiss");
    applyButton.addActionListener (new ApplyAllDefaultColorsAction (nodeDefault,
                                                                    borderDefault,
                                                                    // edgeDefault,
                                                                    backDefault));
    dismissButton.addActionListener (new DismissAction ());
    buttonPanel.add (applyButton, BorderLayout.EAST);
    buttonPanel.add (dismissButton, BorderLayout.WEST);
    
    mainPanel.add(instructions);
    mainPanel.add(nodeDefault);
    mainPanel.add(borderDefault);
    // mainPanel.add(edgeDefault);
    mainPanel.add(backDefault);
    mainPanel.add(buttonPanel);
    return mainPanel;

} // createDefaultSettingsPanel
//------------------------------------------------------------------------------------
public class ApplyExpressionColorAction extends AbstractAction
{
    NewSlider ratioBin,ratioMin,ratioZero,ratioMax;
    NewSlider sigBin,  sigMin,  sigZero,  sigMax, thickSlider;
    ColorPopupButton ratioMinColor,ratioZeroColor,ratioMaxColor;
    ColorPopupButton sigMinColor,  sigZeroColor,  sigMaxColor;
    ApplyExpressionColorAction (NewSlider rBin,
                                NewSlider rMin,  ColorPopupButton rMinC,
                                NewSlider rZero, ColorPopupButton rZeroC,
                                NewSlider rMax,  ColorPopupButton rMaxC,
                                NewSlider sBin,
                                NewSlider sMin,  ColorPopupButton sMinC,
                                NewSlider sZero, ColorPopupButton sZeroC,
                                NewSlider sMax,  ColorPopupButton sMaxC,
                                NewSlider thickness)
{
  ratioBin  = rBin;
  ratioMin  = rMin;
  ratioZero = rZero;
  ratioMax  = rMax;
        
  sigBin  = sBin;
  sigMin  = sMin;
  sigZero = sZero;
  sigMax  = sMax;
  thickSlider = thickness;
        
  ratioMinColor  = rMinC;
  ratioZeroColor = rZeroC;
  ratioMaxColor  = rMaxC;
        
   sigMinColor  = sMinC;
   sigZeroColor = sZeroC;
   sigMaxColor  = sMaxC;

} // inner class ApplyExpressionColorAction ctor
//--------------------------------------------------------------------------------
public void actionPerformed(ActionEvent e)
{
  if (ratioMin.getDoubleValue() >ratioZero.getDoubleValue()||
      ratioZero.getDoubleValue()>ratioMax.getDoubleValue()){
      JFrame errorFrame = new JFrame();
      String errorMessage = "\n"+"Error: Threshold Value"+"\n"
                                +"must be between Min Value"+"\n"
                                +"and Max Value" +"\n";
     JOptionPane.showMessageDialog (errorFrame, errorMessage, "Threshold Error for Ratio", 
                                   JOptionPane.ERROR_MESSAGE);
     errorFrame.setVisible(true);
     return; 
     } // if

  if (sigMin.getDoubleValue() >sigZero.getDoubleValue()||
      sigZero.getDoubleValue()>sigMax.getDoubleValue())  {
    JFrame errorFrame = new JFrame();
    String errorMessage = "\n"+"Error: Threshold Value"+"\n"
                              +"must be between Min Value"+"\n"
                              +"and Max Value" +"\n";
    JOptionPane.showMessageDialog (errorFrame, errorMessage, "Threshold Error for Signficance", 
                                   JOptionPane.ERROR_MESSAGE);
    errorFrame.setVisible(true);
    return; 
    } // if

  nodeViz.setExpressionInformation (ratioBin.getIntegerValue(),
                                    ratioMin.getDoubleValue(),
                                    ratioZero.getDoubleValue(),
                                    ratioMax.getDoubleValue(),
                                    ratioMinColor.getColor(),
                                    ratioZeroColor.getColor(),
                                    ratioMaxColor.getColor(),        
                                    sigBin.getIntegerValue(),
                                    sigMin.getDoubleValue(),
                                    sigZero.getDoubleValue(),
                                    sigMax.getDoubleValue(),
                                    sigMinColor.getColor(),
                                    sigZeroColor.getColor(),
                                    sigMaxColor.getColor(),
                                    thickSlider.getIntegerValue());
   updateSummaryPanel("expression");
   theClient.applyExpressionVizMappings (nodeViz);

} // inner class ApplyExpresionColorAction.actionPerfomred
//-----------------------------------------------------------------------------------
} // inner class ApplyExpressionSettingsAction
//------------------------------------------------------------------------------------
public void updateSummaryPanel(String control)
{
    int index = tabbedPane.getSelectedIndex();
    tabbedPane.remove(0);
    JPanel tempPanel = createSummaryPanel(control);
    tabbedPane.add(tempPanel, 0);
    tabbedPane.setTitleAt(0, "Summary");
    tabbedPane.setSelectedIndex(index);
}
//------------------------------------------------------------------------------------
public class ApplyAllDefaultColorsAction extends AbstractAction 
{
    ColorPopupButton node, border,edge, background;
    ApplyAllDefaultColorsAction (ColorPopupButton nodeDefault,
                                 ColorPopupButton borderDefault,
                                 //ColorPopupButton edgeDefault,
                                 ColorPopupButton backDefault){
        node       = nodeDefault;
        border     = borderDefault;
        //edge       = edgeDefault;
        background = backDefault;
    }
  public void actionPerformed (ActionEvent e) {
      nodeViz.setDefaultSettings(node.getColor(), border.getColor(),
                                       // edge.getColor(), 
                                       background.getColor());
      updateSummaryPanel("default");
      //theClient.applyDefaultNodeViz(nodeViz);
        theClient.applyAllVizMappings (nodeViz);
  }
}// ApplyAllDefaultColorsAction
//------------------------------------------------------------------------------------
/**
 * 
 */
public class ApplyNodeColorAction extends AbstractAction 
{
    String attributeName;
    double min, max, minColorValue, zeroColorValue, maxColorValue;
    NewSlider binSlider, minSlider, zeroSlider, maxSlider;
    ColorPopupButton minColor,zeroColor, maxColor; 

    ApplyNodeColorAction (String tabName, double min, double max,
                          NewSlider binSlide,
                          NewSlider minSlide, ColorPopupButton minC,
                          NewSlider zeroSlide, ColorPopupButton zeroC,
                          NewSlider maxSlide, ColorPopupButton maxC) {
        super ("");
        attributeName = tabName;
        this.min  = min;
        this.max  = max;
        binSlider = binSlide;
        minSlider = minSlide;
        zeroSlider= zeroSlide;
        maxSlider = maxSlide;
        minColor  = minC;
        zeroColor = zeroC;
        maxColor  = maxC;
    }

    public void actionPerformed (ActionEvent e) {
        minColorValue  = minSlider.getDoubleValue();
        zeroColorValue = zeroSlider.getDoubleValue();
        maxColorValue  = maxSlider.getDoubleValue();
        if (minColorValue>zeroColorValue || zeroColorValue>maxColorValue){
            JFrame errorFrame = new JFrame();
            String errorMessage = "\n"+"Error: Threshold Value"+"\n"
                                         +"must be between Min Value"+"\n"
                                         +"and Max Value" +"\n";
            JOptionPane.showMessageDialog(errorFrame, errorMessage, "Threshold Error", 
                                          JOptionPane.ERROR_MESSAGE);
            errorFrame.setVisible(true);
            return; 
        }
        nodeViz.setNodeAttributeWhichControlsFillColor(attributeName);
        nodeViz.setNodeColorCalculationFactors (attributeName, 
                                                      minColor.getColor(),
                                                      zeroColor.getColor(), 
                                                      maxColor.getColor(), 
                                                      binSlider.getIntegerValue(), 
                                                      min, 
                                                      max, 
                                                      minColorValue, 
                                                      zeroColorValue,
                                                      maxColorValue);
        updateSummaryPanel(attributeName);
        //theClient.applyNodeNodeViz (nodeViz);
        theClient.applyAllVizMappings (nodeViz);
       } // actionPerformed

} // ApplyNodeColorAction
//------------------------------------------------------------------------------------
/************************
public class ApplyEdgeColorAction extends AbstractAction 
{
    String attributeName;
    double min, max, minColorValue, zeroColorValue, maxColorValue;
    NewSlider binSlider, minSlider, zeroSlider, maxSlider, thickSlider;
    ColorPopupButton minColor,zeroColor, maxColor; 
    
    ApplyEdgeColorAction (String tabName, double min, double max,
                          NewSlider binSlide, NewSlider thickSlide,
                          NewSlider minSlide, ColorPopupButton minC,
                          NewSlider zeroSlide, ColorPopupButton zeroC,
                          NewSlider maxSlide, ColorPopupButton maxC) {
        super ("");
        attributeName = tabName;
        this.min    = min;
        this.max    = max;
        binSlider   = binSlide;
        thickSlider = thickSlide;
        minSlider   = minSlide;
        zeroSlider  = zeroSlide;
        maxSlider   = maxSlide;
        minColor    = minC;
        zeroColor   = zeroC;
        maxColor    = maxC;
    }

    public void actionPerformed (ActionEvent e) {
        minColorValue  = minSlider.getDoubleValue();
        zeroColorValue = zeroSlider.getDoubleValue();
        maxColorValue  = maxSlider.getDoubleValue();
        
        if (minColorValue>zeroColorValue || zeroColorValue>maxColorValue){
            JFrame errorFrame = new JFrame();
            String errorMessage = "\n"+"Error: Threshold Value"+"\n"
                                         +"must be between Min Value"+"\n"
                                         +"and Max Value" +"\n";
            JOptionPane.showMessageDialog (errorFrame, errorMessage, "Threshold Error", 
                                           JOptionPane.ERROR_MESSAGE);
            errorFrame.setVisible(true);
            return; 
        }
        nodeViz.setEdgeAttributeWhichControlsFillColor(attributeName);
        nodeViz.setEdgeColorCalculationFactors (attributeName, 
                                                      minColor.getColor(),
                                                      zeroColor.getColor(), 
                                                      maxColor.getColor(), 
                                                      binSlider.getIntegerValue(), 
                                                      min, 
                                                      max, 
                                                      minColorValue, 
                                                      zeroColorValue,
                                                      maxColorValue,
                                                      thickSlider.getIntegerValue());
        updateSummaryPanel(attributeName);
        //theClient.applyEdgeNodeViz (nodeViz);
        theClient.applyAllVizMappings (nodeViz);
    }
} // ApplyEdgeColorAction
************************/
//------------------------------------------------------------------------------------
public class ApplyBorderColorAction extends AbstractAction 
{
    String attributeName;
    double min, max;
    NewSlider tempBinSlider, tempThickSlider;
    NewSlider tempMinSlider, tempZeroSlider, tempMaxSlider;
    ColorPopupButton tempMinC, tempZeroC, tempMaxC; 
    ApplyBorderColorAction (String tabName, double min, double max,
                            NewSlider binSlider, NewSlider thickSlider,
                            NewSlider minSlider, ColorPopupButton minC,
                            NewSlider zeroSlider,ColorPopupButton zeroC,
                            NewSlider maxSlider, ColorPopupButton maxC) {
        super ("");
        attributeName = tabName;
        this.min = min;
        this.max = max;

        tempBinSlider = binSlider;
        tempThickSlider=thickSlider;
        tempMinSlider = minSlider;
        tempZeroSlider= zeroSlider;
        tempMaxSlider = maxSlider;
        tempMinC = minC;
        tempZeroC= zeroC;
        tempMaxC = maxC;
    }
    public void actionPerformed (ActionEvent e) {
        double minColorValue = tempMinSlider.getDoubleValue();
        double zeroColorValue= tempZeroSlider.getDoubleValue();
        double maxColorValue = tempMaxSlider.getDoubleValue();
        //error checking step to ensure min<=zero<=max
        if (minColorValue>zeroColorValue || zeroColorValue>maxColorValue){
            JFrame error = new JFrame("Error");
            String errorMessage = "\n"+"Error: Threshold Value"+"\n"
                                         +"must be between Min Value"+"\n"
                                         +"and Max Value" +"\n";
            JTextArea message = new JTextArea(errorMessage);
            JLabel filler = new JLabel ("    ");
            message.setBackground (Color.red);
            message.setOpaque(true);
            error.getContentPane().add(message);
            error.pack();
            error.setVisible(true);
            return;
        }
        nodeViz.setBorderAttributeWhichControlsFillColor(attributeName);
        nodeViz.setBorderColorCalculationFactors(attributeName,
                                                       tempMinC.getColor(),
                                                       tempZeroC.getColor(),
                                                       tempMaxC.getColor(),
                                                       tempBinSlider.getIntegerValue(),
                                                       min,
                                                       max,
                                                       minColorValue,
                                                       zeroColorValue,
                                                       maxColorValue,
                                                       tempThickSlider.getIntegerValue());
        updateSummaryPanel(attributeName);
        //theClient.applyBorderNodeViz (nodeViz);
        theClient.applyAllVizMappings (nodeViz);
    }

} // ApplyBorderColorAction

//------------------------------------------------------------------------------------
public class DismissAction extends AbstractAction 
{

  DismissAction () {super ("");}

  public void actionPerformed (ActionEvent e) {
    thisChooser.dispose ();
    }

} // DismissAction
//-----------------------------------------------------------------------------------
double [] findMinMax (HashMap attributeMap)
{
  String [] keys = (String []) attributeMap.keySet().toArray (new String [0]);
  //for (int i=0; i < keys.length; i++)
  //  System.out.println ("key " + i + ": " +  keys [i]);

  Object [] oValues = attributeMap.values().toArray ();
  //System.out.println ("number of object values in attributeMap: " + oValues.length);
  //System.out.println ("class type: " + oValues [0].getClass ());
  try {
    Class expectedClass = Class.forName ("java.lang.Double");
    Class actualClass = oValues [0].getClass ();
    if (!actualClass.equals (expectedClass)) {
      double result [] = new double [2];
      result [0] = 0.0;
      result [1] = 1.0;
      return result;
      }
    } 
  catch (ClassNotFoundException e) {
    double result [] = new double [2];
    result [0] = 0.0;
    result [1] = 1.0;
    return result;
    }

  Double [] values = (Double []) attributeMap.values ().toArray (new Double [0]); 

  double min = Double.MAX_VALUE;
  double max = Double.MIN_VALUE;

  for (int i=0; i < values.length; i++) {
    double current = values [i].doubleValue ();
    if (current < min) min = current;
    if (current > max) max = current;
    }
     
  double result [] = new double [2];
  result [0] = min;
  result [1] = max;

  return result;

}// findMinMax
//-----------------------------------------------------------------------------------
private Class getAttributeValueClass (HashMap attributeMap)
{
  Object [] oValues = attributeMap.values().toArray ();
  // System.out.println ("class type: " + oValues [0].getClass ());
  return oValues [0].getClass();

}// getAttributeValueClass
//-----------------------------------------------------------------------------------
} // class VizChooserPopup
