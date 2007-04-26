package cytoscape.visual.ui;

import com.l2fprod.common.propertysheet.DefaultProperty;
import com.l2fprod.common.propertysheet.Property;
import com.l2fprod.common.propertysheet.PropertyEditorRegistry;
import com.l2fprod.common.propertysheet.PropertyRendererRegistry;
import com.l2fprod.common.propertysheet.PropertySheetPanel;
import com.l2fprod.common.propertysheet.PropertySheetTable;
import com.l2fprod.common.propertysheet.PropertySheetTableModel.Item;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonUI;

import cytoscape.CyEdge;
import cytoscape.CyNetworkEvent;
import cytoscape.CyNetworkListener;
import cytoscape.CyNode;
import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;
import cytoscape.data.CyAttributesUtils;
import cytoscape.data.Semantics;

import cytoscape.util.SwingWorker;

import cytoscape.util.swing.DropDownMenuButton;

import cytoscape.visual.CalculatorCatalog;
import cytoscape.visual.EdgeAppearanceCalculator;
import cytoscape.visual.NodeAppearanceCalculator;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

import cytoscape.visual.calculators.Calculator;
import cytoscape.visual.calculators.CalculatorFactory;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.DiscreteMapping;
import cytoscape.visual.mappings.ObjectMapping;
import cytoscape.visual.mappings.PassThroughMapping;

import cytoscape.visual.ui.editors.continuous.ContinuousMappingEditorPanel;
import cytoscape.visual.ui.editors.continuous.ContinuousTrackRenderer;
import cytoscape.visual.ui.editors.continuous.CyGradientTrackRenderer;
import cytoscape.visual.ui.editors.continuous.DiscreteTrackRenderer;
import cytoscape.visual.ui.editors.continuous.GradientEditorPanel;
import cytoscape.visual.ui.editors.discrete.CyColorCellRenderer;
import cytoscape.visual.ui.editors.discrete.CyColorPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyComboBoxPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyDoublePropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyFontPropertyEditor;
import cytoscape.visual.ui.editors.discrete.CyStringPropertyEditor;
import cytoscape.visual.ui.editors.discrete.FontCellRenderer;
import cytoscape.visual.ui.editors.discrete.ShapeCellRenderer;
import cytoscape.visual.ui.icon.NodeFullDetailView;
import cytoscape.visual.ui.icon.NodeIcon;
import cytoscape.visual.ui.icon.VisualPropertyIcon;
import cytoscape.visual.ui.icon.VisualPropertyIconFactory;

import ding.view.DGraphView;

import giny.model.GraphObject;

import giny.view.GraphView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragGestureRecognizer;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;

import java.io.IOException;

import java.lang.reflect.Constructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


/**
 * New VizMapper UI main panel.
 *
 * This panel consists of 3 panels:
 * <ul>
 * <li>Global Control Panel
 * <li>Default editor panel
 * <li>Visual Mapping Browser
 * </ul>
 *
 * @version 0.5
 * @since Cytoscape 2.5
 * @author kono
 * @param <syncronized>
 */
public class VizMapperMainPanel extends JPanel
    implements PropertyChangeListener, CyNetworkListener {
    public enum DefaultEditor {NODE, EDGE, GLOBAL;
    }

    private static JPopupMenu menu;
    private static JMenuItem add;
    private static JMenuItem delete;
    private static JMenuItem randomize;
    private static JMenuItem editAll;
    private static JPopupMenu optionMenu;
    private static JMenuItem newVS;
    private static JMenuItem renameVS;
    private static JMenuItem deleteVS;
    private static JMenuItem duplicateVS;
    private static JMenuItem createLegend;

    /*
     * Icons used in this panel.
     */
    private static final ImageIcon optionIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_form-properties.png"));
    private static final ImageIcon delIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_delete-16.png"));
    private static final ImageIcon addIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_data-new-table-16.png"));
    private static final ImageIcon rndIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_filters-16.png"));
    private static final ImageIcon renameIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_redo-16.png"));
    private static final ImageIcon duplicateIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_slide-duplicate.png"));
    private static final ImageIcon legendIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_graphic-styles-16.png"));
    private static final ImageIcon editIcon = new ImageIcon(
            Cytoscape.class.getResource(
                "/cytoscape/images/ximian/stock_edit-16.png"));

    /*
     * This is a singleton.
     */
    private static VizMapperMainPanel panel;
    private static Map<VisualPropertyType, EditorDisplayer> handlers;

    static {
        /*
         * Make dummy network nodes & edges
         */
        final CyNode source = Cytoscape.getCyNode("Source", true);
        final CyNode target = Cytoscape.getCyNode("Target", true);
        final CyEdge edge = Cytoscape.getCyEdge(source, target,
                Semantics.INTERACTION, "Interaction", true, true);
    }

    /*
     * Visual mapping manager. All parameters should be taken from here.
     */
    private VisualMappingManager vmm;
    private JScrollPane noMapListScrollPane;
    private List<VisualPropertyType> mappingExist;
    private JPanel buttonPanel;
    private JButton addButton;
    private JButton deleteButton;
    private JPanel bottomPanel;
    private Map<VisualPropertyType, JDialog> editorWindowManager = new HashMap<VisualPropertyType, JDialog>();

    /** Creates new form AttributeOrientedPanel */
    private VizMapperMainPanel() {
        vmm = Cytoscape.getVisualMappingManager();
        setMenu();

        Cytoscape.getSwingPropertyChangeSupport()
                 .addPropertyChangeListener(this);

        initComponents();

        setVSSelector();
        initializePropertySheetPanel();

        registerCellEditorListeners();

        /*
         * Listener for sub Windows
         */
    }

    /*
     * Register listeners for editors.
     */
    private void registerCellEditorListeners() {
        nodeAttrEditor.addPropertyChangeListener(this);
        edgeAttrEditor.addPropertyChangeListener(this);

        colorCellEditor.addPropertyChangeListener(this);
        mappingTypeEditor.addPropertyChangeListener(this);
        fontCellEditor.addPropertyChangeListener(this);
        numberCellEditor.addPropertyChangeListener(this);
        shapeCellEditor.addPropertyChangeListener(this);
        stringCellEditor.addPropertyChangeListener(this);
    }

    /**
     * Get an instance of VizMapper UI panel. This is a singleton.
     *
     * @return
     */
    public static VizMapperMainPanel getVizMapperUI() {
        if (panel == null)
            panel = new VizMapperMainPanel();

        return panel;
    }

    /**
     * Setup menu items.
     *
     */
    private void setMenu() {
        /*
         * Option Menu
         */
        newVS = new JMenuItem("Create new Visual Style...");
        newVS.setIcon(addIcon);
        newVS.addActionListener(new NewStyleListener());

        deleteVS = new JMenuItem("Delete Visual Style...");
        deleteVS.setIcon(delIcon);
        deleteVS.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                }
            });

        renameVS = new JMenuItem("Rename Visual Style...");
        renameVS.setIcon(renameIcon);
        renameVS.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                }
            });

        duplicateVS = new JMenuItem("Copy existing Visual Style...");
        duplicateVS.setIcon(duplicateIcon);
        duplicateVS.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // TODO Auto-generated method stub
                }
            });

        createLegend = new JMenuItem("Create legend from current Visual Style");
        createLegend.setIcon(legendIcon);

        optionMenu = new JPopupMenu();
        optionMenu.add(newVS);
        optionMenu.add(deleteVS);
        optionMenu.add(renameVS);
        optionMenu.add(duplicateVS);
        optionMenu.add(createLegend);

        /*
         * Build right-click menu
         */
        add = new JMenuItem("Add new mapping");
        delete = new JMenuItem("Delete mapping");
        randomize = new JMenuItem("Set randomized values (Discrete Only)");
        editAll = new JMenuItem("Edit selected values at once...");

        add.setIcon(addIcon);
        delete.setIcon(delIcon);
        randomize.setIcon(rndIcon);
        editAll.setIcon(editIcon);

        delete.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    removeMapping();
                }
            });
        editAll.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent arg0) {
                    editSelectedCells();
                }
            });
        // add.addActionListener(l)
        // select.setIcon(vmIcon);
        menu = new JPopupMenu();
        menu.add(add);
        menu.add(delete);
        menu.add(new JSeparator());
        menu.add(randomize);
        menu.add(editAll);
    }

    public static void apply(Object newValue, VisualPropertyType type) {
        if (newValue != null)
            VizUIUtilities.setDefault(
                Cytoscape.getVisualMappingManager().getVisualStyle(),
                type,
                newValue);
    }

    public static Object showValueSelectDialog(VisualPropertyType type,
        Component caller)
        throws Exception {
        return type.showDiscreteEditor();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        mainSplitPane = new javax.swing.JSplitPane();
        listSplitPane = new javax.swing.JSplitPane();

        bottomPanel = new javax.swing.JPanel();

        defaultAppearencePanel = new javax.swing.JPanel();
        visualPropertySheetPanel = new PropertySheetPanel();
        visualPropertySheetPanel.setTable(new PropertySheetTable());

        vsSelectPanel = new javax.swing.JPanel();
        vsNameComboBox = new javax.swing.JComboBox();
        // optionButton = new javax.swing.JButton();
        buttonPanel = new javax.swing.JPanel();

        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        buttonPanel.setLayout(gridbag);
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = GridBagConstraints.REMAINDER;

        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();

        addButton.setIcon(
            new javax.swing.ImageIcon(
                "/cellar/users/kono/docs/cytoscape25Mock/images/ximian/stock_up-16.png"));
        addButton.setText("Add");
        addButton.setMargin(new java.awt.Insets(2, 2, 2, 2));

        addButton.setPreferredSize(new java.awt.Dimension(70, 20));

        deleteButton.setIcon(
            new javax.swing.ImageIcon(
                "/cellar/users/kono/docs/cytoscape25Mock/images/ximian/stock_down-16.png"));
        deleteButton.setText("Delete");
        deleteButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        deleteButton.setPreferredSize(new java.awt.Dimension(70, 20));

        addButton.setUI(new BlueishButtonUI());
        deleteButton.setUI(new BlueishButtonUI());

        gridbag.setConstraints(addButton, constraints);
        buttonPanel.add(addButton);

        constraints.gridx = 2;
        constraints.gridy = 0;
        gridbag.setConstraints(deleteButton, constraints);
        buttonPanel.add(deleteButton);

        defaultAppearencePanel.addMouseListener(new DefaultMouseListener());

        mainSplitPane.setDividerLocation(120);
        mainSplitPane.setDividerSize(4);
        listSplitPane.setDividerLocation(400);
        listSplitPane.setDividerSize(5);
        listSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        noMapListScrollPane = new javax.swing.JScrollPane();
        noMapListScrollPane.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Unused Visual Properties",
                javax.swing.border.TitledBorder.CENTER,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 12)));
        noMapListScrollPane.setToolTipText(
            "To Create New Mapping, Drag & Drop List Item to Browser.");

        org.jdesktop.layout.GroupLayout bottomPanelLayout = new org.jdesktop.layout.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(noMapListScrollPane,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 272,
                Short.MAX_VALUE).add(buttonPanel,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                bottomPanelLayout.createSequentialGroup().add(buttonPanel,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(noMapListScrollPane,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 135,
                    Short.MAX_VALUE)));

        listSplitPane.setLeftComponent(mainSplitPane);
        listSplitPane.setRightComponent(bottomPanel);

        mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        defaultAppearencePanel.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Defaults",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 12),
                java.awt.Color.darkGray));
        // defaultTabbedPane
        // .setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        // defaultTabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        mainSplitPane.setLeftComponent(defaultAppearencePanel);

        visualPropertySheetPanel.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Visual Mapping Browser",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 12),
                java.awt.Color.darkGray));

        mainSplitPane.setRightComponent(visualPropertySheetPanel);

        vsSelectPanel.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Current Visual Style",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 12),
                java.awt.Color.darkGray));

        vsNameComboBox.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    vsNameComboBoxActionPerformed(evt);
                }
            });

        optionButton = new DropDownMenuButton(
                new AbstractAction() {
                    public void actionPerformed(ActionEvent ae) {
                        DropDownMenuButton b = (DropDownMenuButton) ae.getSource();
                        optionMenu.show(
                            b,
                            0,
                            b.getHeight());
                    }
                });

        optionButton.setToolTipText("Options...");
        optionButton.setIcon(optionIcon);
        optionButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        optionButton.setComponentPopupMenu(optionMenu);

        org.jdesktop.layout.GroupLayout vsSelectPanelLayout = new org.jdesktop.layout.GroupLayout(vsSelectPanel);
        vsSelectPanel.setLayout(vsSelectPanelLayout);
        vsSelectPanelLayout.setHorizontalGroup(
            vsSelectPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                vsSelectPanelLayout.createSequentialGroup().addContainerGap().add(vsNameComboBox,
                    0, 146, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(optionButton,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addContainerGap()));
        vsSelectPanelLayout.setVerticalGroup(
            vsSelectPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                vsSelectPanelLayout.createSequentialGroup().add(
                    vsSelectPanelLayout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.BASELINE).add(vsNameComboBox,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(optionButton)) // .addContainerGap(
                                                                                           // org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                                                                                           // Short.MAX_VALUE)
        ));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(vsSelectPanel,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(mainSplitPane,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 280,
                Short.MAX_VALUE));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(vsSelectPanel,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(mainSplitPane,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 510,
                    Short.MAX_VALUE)));
    } // </editor-fold>

    // Variables declaration - do not modify
    private JPanel defaultAppearencePanel;
    private javax.swing.JSplitPane mainSplitPane;
    private javax.swing.JSplitPane listSplitPane;
    private DropDownMenuButton optionButton;
    private PropertySheetPanel visualPropertySheetPanel;
    private javax.swing.JComboBox vsNameComboBox;
    private javax.swing.JPanel vsSelectPanel;

    /*
     * Renderer and Editors for the cells
     */

    // For general values (string & number)
    private DefaultTableCellRenderer defCellRenderer = new DefaultTableCellRenderer();

    // For String values
    private CyStringPropertyEditor stringCellEditor = new CyStringPropertyEditor();

    // For colors
    private CyColorCellRenderer collorCellRenderer = new CyColorCellRenderer();
    private CyColorPropertyEditor colorCellEditor = new CyColorPropertyEditor();

    // For shapes
    private ShapeCellRenderer shapeCellRenderer = new ShapeCellRenderer(VisualPropertyType.NODE_SHAPE);
    private CyComboBoxPropertyEditor shapeCellEditor = new CyComboBoxPropertyEditor();

    // For Lines
    private ShapeCellRenderer lineCellRenderer = new ShapeCellRenderer(VisualPropertyType.EDGE_LINETYPE);
    private CyComboBoxPropertyEditor lineCellEditor = new CyComboBoxPropertyEditor();

    // For sizes
    private CyDoublePropertyEditor numberCellEditor = new CyDoublePropertyEditor();

    // For font faces
    private CyFontPropertyEditor fontCellEditor = new CyFontPropertyEditor();
    private FontCellRenderer fontCellRenderer = new FontCellRenderer();
    private DefaultTableCellRenderer emptyBoxRenderer = new DefaultTableCellRenderer();
    private DefaultTableCellRenderer filledBoxRenderer = new DefaultTableCellRenderer();
    private DefaultTableCellRenderer gradientRenderer = new DefaultTableCellRenderer();
    private DefaultTableCellRenderer continuousRenderer = new DefaultTableCellRenderer();
    private DefaultTableCellRenderer discreteRenderer = new DefaultTableCellRenderer();
    private CyComboBoxPropertyEditor nodeAttrEditor = new CyComboBoxPropertyEditor();
    private CyComboBoxPropertyEditor edgeAttrEditor = new CyComboBoxPropertyEditor();
    private CyComboBoxPropertyEditor mappingTypeEditor = new CyComboBoxPropertyEditor();
    private static final Map<Object, Icon> nodeShapeIcons = VisualPropertyIconFactory.getIconSet(VisualPropertyType.NODE_SHAPE);
    private static final Map<Object, Icon> arrowShapeIcons = VisualPropertyIconFactory.getIconSet(VisualPropertyType.EDGE_SRCARROW_SHAPE);
    private static final Map<Object, Icon> lineTypeIcons = VisualPropertyIconFactory.getIconSet(VisualPropertyType.EDGE_LINETYPE);
    private PropertyRendererRegistry pr = new PropertyRendererRegistry();
    private PropertyEditorRegistry regr = new PropertyEditorRegistry();

    // End of variables declaration
    private void vsNameComboBoxActionPerformed(java.awt.event.ActionEvent evt) {
        final String vsName = (String) vsNameComboBox.getSelectedItem();

        System.out.println("---Got VS Change: " + vsName);
        // visualPropertySheetPanel = new PropertySheetPanel();
        setPropertyTable();

        setDefaultPanel(DefaultAppearenceBuilder.getDefaultView());

        // visualPropertySheetPanel.repaint();
    }

    private static final String CATEGORY_NODE = "Node Attributes";
    private static final String CATEGORY_EDGE = "Edge Attributes";
    private static final String CATEGORY_UNUSED = "Unused Properties";
    private static final String GRAPHICAL_MAP_VIEW = "Graphical View";
    private static final String NODE_VISUAL_MAPPING = "Node Visual Mapping";
    private static final String EDGE_VISUAL_MAPPING = "Edge Visual Mapping";
    private static final int VS_ORIENTED = 1;
    private static final int ATTR_ORIENTED = 2;
    private JButton addMappingButton;

    /*
     * Set Visual Style selector combo box.
     */
    private void setVSSelector() {
        Set<String> vsNames = vmm.getCalculatorCatalog()
                                 .getVisualStyleNames();

        vsNameComboBox.removeAllItems();

        for (String name : vsNames)
            vsNameComboBox.addItem(name);
    }

    private void initializePropertySheetPanel() {
        Component[] comps = visualPropertySheetPanel.getComponents();

        for (Component comp : comps) {
            if (comp.getClass() == JPanel.class) {
                addMappingButton = new JButton();
                addMappingButton.setToolTipText("Create New Mapping");
                addMappingButton.setIcon(addIcon);
                addMappingButton.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent arg0) {
                            // final NewMappingDialog newDialog = new
                            // NewMappingDialog(
                            // Cytoscape.getDesktop(), true);
                            // newDialog.setVisible(true);
                        }
                    });
                // addMappingButton.setOpaque(false);
                addMappingButton.setUI(new BlueishButtonUI());
                ((JPanel) comp).add(addMappingButton);

                /*
                 * Some experimental buttons
                 */
                JButton deleteMapping = new JButton();
                deleteMapping.setToolTipText("Delete Selected Mapping");
                deleteMapping.setIcon(
                    new ImageIcon(
                        Cytoscape.class.getResource(
                            "/cytoscape/images/ximian/stock_delete-16.png")));
                deleteMapping.setUI(new BlueishButtonUI());
                ((JPanel) comp).add(deleteMapping);

                JButton randomize = new JButton();
                randomize.setToolTipText(
                    "Randomize Values (Discrete Mapping Only)");
                randomize.setIcon(
                    new ImageIcon(
                        Cytoscape.class.getResource(
                            "/cytoscape/images/ximian/stock_filters-16.png")));
                randomize.setUI(new BlueishButtonUI());
                ((JPanel) comp).add(randomize);

                JToggleButton newButton = new JToggleButton();
                newButton.setToolTipText(
                    "Switch View (Attribute-Oriented <--> Visual Property Oriented)");
                newButton.setIcon(
                    new ImageIcon(
                        Cytoscape.class.getResource(
                            "images/ximian/stock_refresh-16.png")));
                // newButton.setOpaque(false);
                newButton.setUI(new BlueishButtonUI());
                ((JPanel) comp).add(newButton);

                comp.repaint();
                repaint();
            }
        }

        visualPropertySheetPanel.setToolBarVisible(true);
    }

    //	private void setDefaultPanel(int viewType) {
    //		CyAttributes nodeAttr = Cytoscape.getNodeAttributes();
    //
    //		NodeAppearanceCalculator nac = Cytoscape.getVisualMappingManager()
    //				.getVisualStyle().getNodeAppearanceCalculator();
    //
    //		List<Calculator> calc = nac.getCalculators();
    //		System.out.println("NAC number = " + calc.size());
    //
    //		for (int i = 0; i < VisualPropertyType.values().length; i++) {
    //			String name = CalculatorFactory.getTypeName(VisualPropertyType
    //					.values()[i].getType());
    //
    //			VizMapperProperty calcProp = new VizMapperProperty();
    //
    //			calcProp.setCategory("Node Defaults");
    //			calcProp.setDisplayName(name);
    //			calcProp.setValue(nac.getDefaultAppearance().getLabel());
    //
    //			// propertySheetPanel2.addProperty(calcProp);
    //		}
    //	}
    private void setPropertySheetAppearence() {
        /*
         * Set popup menu
         */

        /*
         * Set Tooltiptext for the table.
         */
        visualPropertySheetPanel.setTable(
            new PropertySheetTable() {
                public String getToolTipText(MouseEvent me) {
                    final Point pt = me.getPoint();
                    final int row = rowAtPoint(pt);

                    if (row < 0)
                        return null;
                    else {
                        final Property prop = ((Item) getValueAt(row, 0)).getProperty();

                        final Color fontColor;

                        if ((prop != null) && (prop.getValue() != null) &&
                                (prop.getValue()
                                         .getClass() == Color.class))
                            fontColor = (Color) prop.getValue();
                        else
                            fontColor = Color.DARK_GRAY;

                        final String colorString = Integer.toHexString(
                                fontColor.getRGB());

                        /*
                         * Edit
                         */
                        if (prop == null)
                            return null;

                        if (prop.getDisplayName()
                                    .equals(GRAPHICAL_MAP_VIEW))
                            return "Click to edit this mapping...";

                        if ((prop.getDisplayName() == "Controlling Attribute") ||
                                (prop.getDisplayName() == "Mapping Type"))
                            return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#" +
                            colorString.substring(2, 8) + "\"><strong>" +
                            prop.getDisplayName() + " = " + prop.getValue() +
                            "</font></strong></body></html>";
                        else if ((prop.getSubProperties() == null) ||
                                (prop.getSubProperties().length == 0))
                            return "<html><Body BgColor=\"white\"><font Size=\"4\" Color=\"#" +
                            colorString.substring(2, 8) + "\"><strong>" +
                            prop.getDisplayName() +
                            "</font></strong></body></html>";

                        return null;
                    }
                }
            });

        visualPropertySheetPanel.getTable()
                                .getColumnModel()
                                .addColumnModelListener(
            new TableColumnModelListener() {
                public void columnAdded(TableColumnModelEvent arg0) {
                    // TODO Auto-generated method stub
                }

                public void columnMarginChanged(ChangeEvent e) {
                    final PropertySheetTable table = visualPropertySheetPanel.getTable();
                    Property shownProp = null;
                    int rowCount = table.getRowCount();

                    for (int i = 0; i < rowCount; i++) {
                        shownProp = ((Item) visualPropertySheetPanel.getTable()
                                                                    .getValueAt(i,
                                0)).getProperty();

                        if ((shownProp != null) &&
                                shownProp.getDisplayName()
                                             .equals(GRAPHICAL_MAP_VIEW)) {
                            final Property parent = shownProp.getParentProperty();

                            final Object type = ((VizMapperProperty) parent).getHiddenObject();

                            if (type instanceof VisualPropertyType) {
                                ObjectMapping mapping;

                                if (((VisualPropertyType) type).isNodeProp())
                                    mapping = vmm.getVisualStyle()
                                                 .getNodeAppearanceCalculator()
                                                 .getCalculator(((VisualPropertyType) type))
                                                 .getMapping(0);
                                else
                                    mapping = vmm.getVisualStyle()
                                                 .getEdgeAppearanceCalculator()
                                                 .getCalculator(((VisualPropertyType) type))
                                                 .getMapping(0);

                                if (mapping instanceof ContinuousMapping) {
                                    table.setRowHeight(i, 80);

                                    int wi = table.getCellRect(0, 1, true).width;

                                    switch ((VisualPropertyType) type) {
                                    case NODE_FILL_COLOR:
                                    case NODE_BORDER_COLOR:

                                        final GradientEditorPanel gre = new GradientEditorPanel(((VisualPropertyType) type));
                                        gradientRenderer.setIcon(
                                            CyGradientTrackRenderer.getTrackGraphicIcon(
                                                wi, 70,
                                                (ContinuousMapping) mapping));
                                        pr.registerRenderer(shownProp,
                                            gradientRenderer);

                                        break;

                                    case NODE_SIZE:
                                        continuousRenderer.setIcon(
                                            ContinuousTrackRenderer.getTrackGraphicIcon(
                                                wi, 70,
                                                (ContinuousMapping) mapping,
                                                (VisualPropertyType) type));
                                        pr.registerRenderer(shownProp,
                                            continuousRenderer);

                                        break;

                                    default:
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    repaint();
                    visualPropertySheetPanel.repaint();
                }

                public void columnMoved(TableColumnModelEvent e) {
                    // TODO Auto-generated method stub
                }

                public void columnRemoved(TableColumnModelEvent e) {
                    // TODO Auto-generated method stub
                }

                public void columnSelectionChanged(ListSelectionEvent e) {
                    // TODO Auto-generated method stub
                }
            });

        /*
         * By default, show category.
         */
        visualPropertySheetPanel.setMode(PropertySheetPanel.VIEW_AS_CATEGORIES);

        visualPropertySheetPanel.getTable()
                                .setComponentPopupMenu(menu);

        visualPropertySheetPanel.getTable()
                                .addMouseListener(
            new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    processMouseClick(e);
                }
            });

        PropertySheetTable table = visualPropertySheetPanel.getTable();

        //
        // table.setDefaultRenderer(Object.class, mainCat);
        table.setRowHeight(20);
        // table.getRendererFactory().createTableCellRenderer(String.class);
        // com.l2fprod.common.swing.renderer.DefaultCellRenderer rend3 = new
        // DefaultCellRenderer();
        //		
        // rend3.setFont(new Font("SansSerif", Font.BOLD, 32));
        //		
        // TableCellRenderer rend =
        // ((PropertyRendererRegistry)table.getRendererFactory()).getRenderer(Object.class);
        // System.out.print("!!!!!!!!!! Renderer = " + rend);
        //		
        //		
        // pr.registerRenderer(Object.class, rend3);
        //		
        //		
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        // table.setCategoryBackground(new Color(0, 0, 200, 70));
        table.setSelectionBackground(Color.white);
        table.setSelectionForeground(Color.blue);
        // table.setForeground(Color.black);

        /*
         * Set editors
         */
        collorCellRenderer.setForeground(Color.DARK_GRAY);
        collorCellRenderer.setOddBackgroundColor(new Color(150, 150, 150, 20));
        collorCellRenderer.setEvenBackgroundColor(Color.white);

        // rend2.setIcon(vmIcon);
        gradientRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
        gradientRenderer.setVerticalAlignment(SwingConstants.CENTER);

        emptyBoxRenderer.setHorizontalTextPosition(SwingConstants.CENTER);
        emptyBoxRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        emptyBoxRenderer.setBackground(new Color(0, 200, 255, 20));
        emptyBoxRenderer.setForeground(Color.red);
        emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

        filledBoxRenderer.setBackground(Color.white);
        filledBoxRenderer.setForeground(Color.blue);

        // emptyBoxRenderer.setFont(new Font("SansSerif", Font.BOLD, 12));

        // rend2.setFont(new Font("SansSerif", Font.BOLD, 38));
        // rend2.setBackground(Color.white);
        // rend2.setForeground(Color.red);
        final Object[] nodeAttrNames = Cytoscape.getNodeAttributes()
                                                .getAttributeNames();
        Arrays.sort(nodeAttrNames);

        final Object[] edgeAttrNames = Cytoscape.getEdgeAttributes()
                                                .getAttributeNames();
        Arrays.sort(edgeAttrNames);
        nodeAttrEditor.setAvailableValues(nodeAttrNames);
        edgeAttrEditor.setAvailableValues(edgeAttrNames);

        final Set mappingTypes = Cytoscape.getVisualMappingManager()
                                          .getCalculatorCatalog()
                                          .getMappingNames();

        mappingTypeEditor.setAvailableValues(mappingTypes.toArray());

        VisualPropertyIcon newIcon;

        List<Icon> iconList = new ArrayList();
        iconList.addAll(nodeShapeIcons.values());

        Icon[] iconArray = new Icon[iconList.size()];
        String[] shapeNames = new String[iconList.size()];
        Set nodeShapes = nodeShapeIcons.keySet();

        for (int i = 0; i < iconArray.length; i++) {
            newIcon = ((NodeIcon) iconList.get(i)).clone();
            newIcon.setIconHeight(16);
            newIcon.setIconWidth(16);
            iconArray[i] = newIcon;
            shapeNames[i] = newIcon.getName();
        }

        shapeCellEditor.setAvailableValues(nodeShapes.toArray());
        shapeCellEditor.setAvailableIcons(iconArray);

        iconList = new ArrayList();
        iconList.addAll(lineTypeIcons.values());
        iconArray = new Icon[iconList.size()];
        shapeNames = new String[iconList.size()];

        Set lineTypes = lineTypeIcons.keySet();

        for (int i = 0; i < iconArray.length; i++) {
            newIcon = (VisualPropertyIcon) (iconList.get(i));
            newIcon.setIconHeight(16);
            newIcon.setIconWidth(16);
            iconArray[i] = newIcon;
            shapeNames[i] = newIcon.getName();
        }

        lineCellEditor.setAvailableValues(lineTypes.toArray());
        lineCellEditor.setAvailableIcons(iconArray);
    }

    private void processMouseClick(MouseEvent e) {
        final PropertySheetTable table = visualPropertySheetPanel.getTable();

        int selected = visualPropertySheetPanel.getTable()
                                               .getSelectedRow();

        Property shownProp = null;

        /*
         * Adjust height if it's an legend icon.
         */
        for (int i = 0; i < table.getModel()
                                     .getRowCount(); i++) {
            shownProp = ((Item) visualPropertySheetPanel.getTable()
                                                        .getValueAt(i, 0)).getProperty();

            if ((shownProp != null) &&
                    shownProp.getDisplayName()
                                 .equals(GRAPHICAL_MAP_VIEW))
                table.setRowHeight(i, 80);
        }

        visualPropertySheetPanel.repaint();

        if (SwingUtilities.isRightMouseButton(e)) {
            /*
             * Popup menu
             */
            final int col = visualPropertySheetPanel.getTable()
                                                    .columnAtPoint(
                    e.getPoint());
            final int row = visualPropertySheetPanel.getTable()
                                                    .rowAtPoint(e.getPoint());

            if (row >= 0) {
                final Property prop = ((Item) visualPropertySheetPanel.getTable()
                                                                      .getValueAt(selected,
                        0)).getProperty();
                String controllerName = (String) prop.getValue();
                CyAttributes selectedAttr = Cytoscape.getNodeAttributes();
            }
        } else {
            /*
             * Left click.
             */
            if (0 <= selected) {
                final Item item = (Item) visualPropertySheetPanel.getTable()
                                                                 .getValueAt(selected,
                        0);

                final Property curProp = item.getProperty();

                if (curProp == null)
                    return;

                /*
                 * Create new mapping if double-click on unused
                 * val.
                 */
                String category = curProp.getCategory();

                if ((e.getClickCount() == 2) && (category != null) &&
                        category.equalsIgnoreCase("Unused Properties")) {
                    ((VizMapperProperty) curProp).setEditable(true);

                    VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) curProp).getHiddenObject();
                    createNewMapping(type);
                    visualPropertySheetPanel.removeProperty(curProp);

                    return;
                } else if ((e.getClickCount() == 1) && (category == null)) {
                    /*
                     * Single left-click
                     */
                    System.out.println("---------got Single click");

                    VisualPropertyType type = null;

                    if ((curProp.getParentProperty() == null) &&
                            ((VizMapperProperty) curProp).getHiddenObject() instanceof VisualPropertyType)
                        type = (VisualPropertyType) ((VizMapperProperty) curProp).getHiddenObject();
                    else if (curProp.getParentProperty() != null)
                        type = (VisualPropertyType) ((VizMapperProperty) curProp.getParentProperty()).getHiddenObject();
                    else

                        return;

                    final ObjectMapping selectedMapping;

                    if (type.isNodeProp())
                        selectedMapping = vmm.getVisualStyle()
                                             .getNodeAppearanceCalculator()
                                             .getCalculator(type)
                                             .getMapping(0);
                    else
                        selectedMapping = vmm.getVisualStyle()
                                             .getEdgeAppearanceCalculator()
                                             .getCalculator(type)
                                             .getMapping(0);

                    if (selectedMapping instanceof ContinuousMapping) {
                        /*
                             * Need to check other windows.
                             */
                        if (editorWindowManager.containsKey(type)) {
                            // This means editor is already on display.
                            editorWindowManager.get(type)
                                               .requestFocus();

                            return;
                        } else {
                            try {
                                ((JDialog) type.showContinuousEditor()).addPropertyChangeListener(this);
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Set property sheet panel.
     */
    private void setPropertyTable() {
        setPropertySheetAppearence();

        /*
         * Clean up sheet
         */
        for (Property item : visualPropertySheetPanel.getProperties())
            visualPropertySheetPanel.removeProperty(item);

        final NodeAppearanceCalculator nac = Cytoscape.getVisualMappingManager()
                                                      .getVisualStyle()
                                                      .getNodeAppearanceCalculator();

        final EdgeAppearanceCalculator eac = Cytoscape.getVisualMappingManager()
                                                      .getVisualStyle()
                                                      .getEdgeAppearanceCalculator();

        final List<Calculator> nacList = nac.getCalculators();
        final List<Calculator> eacList = eac.getCalculators();

        regr.registerDefaults();

        /*
         * Add properties to the browser.
         */
        setPropertyFromCalculator(nacList, NODE_VISUAL_MAPPING);
        setPropertyFromCalculator(eacList, EDGE_VISUAL_MAPPING);

        /*
         * Finally, build undef list
         */
        buildList();
        // noMapList = new DSourceList(mappingExist.toArray());
        //
        // noMapList
        // .setToolTipText("To Create New Mapping, Drag & Drop List Item to
        // Browser.");
        // noMapListScrollPane.setViewportView(noMapList);

        /*
         * Set Unused
         */
        setUnused();
    }

    /*
     * Add unused visual properties to the property sheet
     *
     */
    private void setUnused() {
        for (VisualPropertyType type : mappingExist) {
            VizMapperProperty prop = new VizMapperProperty();
            prop.setCategory(CATEGORY_UNUSED);
            prop.setDisplayName(type.getName());
            prop.setHiddenObject(type);
            prop.setValue("Double-Click to create...");
            prop.setEditable(false);
            // regr.registerEditor(prop, newMappingTypeEditor);
            visualPropertySheetPanel.addProperty(prop);
        }
    }

    /*
     * Set value, title, and renderer for each property in the category.
     */
    private void setDiscreteProps(VisualPropertyType type, Map discMapping,
        Set<Object> attrKeys, PropertyEditor editor, TableCellRenderer rend,
        DefaultProperty parent) {
        if (attrKeys == null)
            return;

        Object val;
        VizMapperProperty valProp;

        for (Object key : attrKeys) {
            valProp = new VizMapperProperty();
            valProp.setDisplayName(key.toString());
            valProp.setName(key.toString());
            valProp.setParentProperty(parent);
            val = discMapping.get(key);

            if (val != null)
                valProp.setType(val.getClass());

            parent.addSubProperty(valProp);
            pr.registerRenderer(valProp, rend);
            regr.registerEditor(valProp, editor);

            valProp.setValue(discMapping.get(key));
        }
    }

    private void setPropertyFromCalculator(List<Calculator> calcList,
        String rootCategory) {
        VisualPropertyType type;

        for (Calculator calc : calcList) {
            final VizMapperProperty calculatorTypeProp = new VizMapperProperty();
            type = calc.getVisualPropertyType();

            /*
             * Set one calculator
             */
            calculatorTypeProp.setCategory(rootCategory);
            calculatorTypeProp.setType(String.class);
            calculatorTypeProp.setDisplayName(type.getName());
            calculatorTypeProp.setHiddenObject(type);

            // calculatorTypeProp.setEditable(false);
            // calculatorTypeProp.setValue(calcType);

            /*
             * Mapping 0 is always currently used mapping.
             */
            final ObjectMapping firstMap = calc.getMapping(0);

            String attrName;

            if (firstMap != null) {
                final VizMapperProperty mappingHeader = new VizMapperProperty();

                attrName = firstMap.getControllingAttributeName();

                if (attrName == null) {
                    calculatorTypeProp.setValue("Please select a value!");
                    pr.registerRenderer(calculatorTypeProp, emptyBoxRenderer);
                } else {
                    calculatorTypeProp.setValue(attrName);
                    pr.registerRenderer(calculatorTypeProp, filledBoxRenderer);
                }

                mappingHeader.setDisplayName("Mapping Type");
                mappingHeader.setHiddenObject(firstMap.getClass());

                if (firstMap.getClass() == DiscreteMapping.class)
                    mappingHeader.setValue("Discrete Mapping");
                else if (firstMap.getClass() == ContinuousMapping.class)
                    mappingHeader.setValue("Continuous Mapping");
                else
                    mappingHeader.setValue("Passthrough Mapping");

                mappingHeader.setHiddenObject(firstMap);

                mappingHeader.setParentProperty(calculatorTypeProp);
                calculatorTypeProp.addSubProperty(mappingHeader);
                regr.registerEditor(mappingHeader, mappingTypeEditor);

                final CyAttributes attr;
                final Iterator it;

                if (calc.getVisualPropertyType()
                            .isNodeProp()) {
                    attr = Cytoscape.getNodeAttributes();
                    it = Cytoscape.getCurrentNetwork()
                                  .nodesIterator();
                    regr.registerEditor(calculatorTypeProp, nodeAttrEditor);
                } else {
                    attr = Cytoscape.getEdgeAttributes();
                    it = Cytoscape.getCurrentNetwork()
                                  .edgesIterator();
                    regr.registerEditor(calculatorTypeProp, edgeAttrEditor);
                }

                /*
                 * Discrete Mapping
                 */
                if ((firstMap.getClass() == DiscreteMapping.class) &&
                        (attrName != null)) {
                    final Map discMapping = ((DiscreteMapping) firstMap).getAll();
                    final Set<String> keyset = discMapping.keySet();

                    Set<Object> attrSet = loadKeys(attrName, attr, firstMap);

                    switch (type) {
                    /*
                     * Color calculators
                     */
                    case NODE_FILL_COLOR:
                    case NODE_BORDER_COLOR:
                    case EDGE_COLOR:
                    case EDGE_SRCARROW_COLOR:
                    case EDGE_TGTARROW_COLOR:
                    case NODE_LABEL_COLOR:
                    case EDGE_LABEL_COLOR:
                        setDiscreteProps(type, discMapping, attrSet,
                            colorCellEditor, collorCellRenderer,
                            calculatorTypeProp);

                        break;

                    case NODE_LINETYPE:
                    case EDGE_LINETYPE:
                        setDiscreteProps(type, discMapping, attrSet,
                            lineCellEditor, lineCellRenderer, calculatorTypeProp);

                        break;

                    /*
                     * Shape property
                     */
                    case NODE_SHAPE:
                        setDiscreteProps(type, discMapping, attrSet,
                            shapeCellEditor, defCellRenderer, calculatorTypeProp);

                        break;

                    /*
                     * Arrow Head Shapes
                     */
                    case EDGE_SRCARROW_SHAPE:
                    case EDGE_TGTARROW_SHAPE:
                        break;

                    case NODE_LABEL:
                    case EDGE_LABEL:
                    case NODE_TOOLTIP:
                    case EDGE_TOOLTIP:
                        setDiscreteProps(type, discMapping, attrSet,
                            stringCellEditor, defCellRenderer,
                            calculatorTypeProp);

                        // for (String key : keyset) {
                        // final VizMapperProperty valProp = new
                        // VizMapperProperty();
                        // valProp.setDisplayName(key);
                        // valProp.setValue(discMapping.get(key));
                        // valProp.setParentProperty(calculatorTypeProp);
                        // calculatorTypeProp.addSubProperty(valProp);
                        // regr.registerEditor(valProp, stringCellEditor);
                        // }
                        break;

                    /*
                     * Font props
                     */
                    case NODE_FONT_FACE:
                    case EDGE_FONT_FACE:
                        setDiscreteProps(type, discMapping, attrSet,
                            fontCellEditor, fontCellRenderer, calculatorTypeProp);

                        break;

                    /*
                     * Size-related props
                     */
                    case NODE_FONT_SIZE:
                    case EDGE_FONT_SIZE:
                    case NODE_SIZE:
                    case NODE_WIDTH:
                    case NODE_HEIGHT:
                    case NODE_LINE_WIDTH:
                    case EDGE_LINE_WIDTH:
                        setDiscreteProps(type, discMapping, attrSet,
                            numberCellEditor, defCellRenderer,
                            calculatorTypeProp);

                        break;

                    /*
                     * Node Label Position. Needs special editor
                     */
                    case NODE_LABEL_POSITION:
                        setDiscreteProps(type, discMapping, attrSet,
                            stringCellEditor, defCellRenderer,
                            calculatorTypeProp);

                        break;

                    default:
                        break;
                    }
                } else if ((firstMap.getClass() == ContinuousMapping.class) &&
                        (attrName != null)) {
                    // final List<ContinuousMappingPoint> contMapping =
                    // ((ContinuousMapping) firstMap)
                    // .getAllPoints();
                    //
                    // final List<VizMapperProperty> propList = new
                    // ArrayList<VizMapperProperty>();
                    //
                    // final ContinuousMappingPoint firstPoint = contMapping
                    // .get(0);
                    // VizMapperProperty valProp = new VizMapperProperty();
                    // valProp.setEditable(false);
                    // valProp.setDisplayName("Below " + firstPoint.getValue());
                    // valProp.setValue(firstPoint.getRange().lesserValue);
                    // valProp.setParentProperty(calculatorTypeProp);
                    // calculatorTypeProp.addSubProperty(valProp);
                    // propList.add(valProp);
                    //
                    // final VizMapperProperty valProp2 = new
                    // VizMapperProperty();
                    // valProp2.setEditable(false);
                    // valProp2.setDisplayName(((Double) firstPoint.getValue())
                    // .toString());
                    // valProp2.setValue(firstPoint.getRange().equalValue);
                    // valProp2.setParentProperty(calculatorTypeProp);
                    // calculatorTypeProp.addSubProperty(valProp2);
                    // propList.add(valProp2);
                    //
                    // if (contMapping.size() == 1) {
                    // final VizMapperProperty valProp3 = new
                    // VizMapperProperty();
                    // valProp3.setEditable(false);
                    // valProp3.setDisplayName("Above "
                    // + firstPoint.getValue());
                    // valProp3.setValue(firstPoint.getRange().greaterValue);
                    // valProp3.setParentProperty(calculatorTypeProp);
                    // calculatorTypeProp.addSubProperty(valProp3);
                    // propList.add(valProp3);
                    //
                    // // pr.registerRenderer(valProp3, cr);
                    // // regr.registerEditor(
                    // // valProp3,
                    // // new ColorPropertyEditor());
                    // break;
                    // }
                    //
                    // ContinuousMappingPoint value;
                    //
                    // for (int i = 1; i < contMapping.size(); i++) {
                    // value = contMapping.get(i);
                    // valProp = new VizMapperProperty();
                    // valProp.setEditable(false);
                    // valProp.setDisplayName(((Double) value.getValue())
                    // .toString());
                    // valProp.setValue(value.getRange().equalValue);
                    // valProp.setParentProperty(calculatorTypeProp);
                    // calculatorTypeProp.addSubProperty(valProp);
                    // propList.add(valProp);
                    //
                    // if (i == (contMapping.size() - 1)) {
                    // valProp = new VizMapperProperty();
                    // valProp.setEditable(false);
                    // valProp.setDisplayName("Above " + value.getValue());
                    // valProp.setValue(value.getRange().greaterValue);
                    // valProp.setParentProperty(calculatorTypeProp);
                    // calculatorTypeProp.addSubProperty(valProp);
                    // propList.add(valProp);
                    //
                    // break;
                    // }
                    // }
                    gradientRenderer.setForeground(Color.white);
                    gradientRenderer.setBackground(Color.white);

                    int wi = this.visualPropertySheetPanel.getTable()
                                                          .getCellRect(0, 1,
                            true).width;

                    // int h = this.visualPropertySheetPanel.getTable()
                    // .getCellRect(0, 1, true).height;
                    int h = 80;

                    VizMapperProperty graphicalView = new VizMapperProperty();
                    graphicalView.setDisplayName(GRAPHICAL_MAP_VIEW);
                    graphicalView.setParentProperty(calculatorTypeProp);
                    calculatorTypeProp.addSubProperty(graphicalView);

                    switch (type) {
                    /*
                     * Color-related calcs.
                     */
                    case NODE_FILL_COLOR:
                    case NODE_BORDER_COLOR:
                    case EDGE_COLOR:
                    case EDGE_SRCARROW_COLOR:
                    case EDGE_TGTARROW_COLOR:
                        graphicalView.setName("Color Mapping");
                        gradientRenderer.setIcon(
                            CyGradientTrackRenderer.getTrackGraphicIcon(wi, 70,
                                (ContinuousMapping) firstMap));
                        pr.registerRenderer(graphicalView, gradientRenderer);

                        // for (VizMapperProperty curProp : propList) {
                        // pr.registerRenderer(curProp, cr);
                        // regr.registerEditor(curProp, ce);
                        // }
                        break;

                    /*
                     * Size/Width related calcs.
                     */
                    case NODE_LINE_WIDTH:
                    case NODE_SIZE:
                    case NODE_WIDTH:
                    case NODE_FONT_SIZE:
                    case NODE_HEIGHT:
                    case EDGE_LINE_WIDTH:
                    case EDGE_FONT_SIZE:
                        graphicalView.setName("CC Mapping");
                        continuousRenderer.setIcon(
                            ContinuousTrackRenderer.getTrackGraphicIcon(wi, 70,
                                (ContinuousMapping) firstMap, type));
                        pr.registerRenderer(graphicalView, continuousRenderer);

                        break;

                    /*
                     * Fixed value calcs
                     */
                    case NODE_FONT_FACE:
                    case EDGE_FONT_FACE:

                        // for (VizMapperProperty curProp : propList)
                        // curProp.setType(Font.class);
                        break;

                    case NODE_SHAPE:
                    case NODE_LINETYPE:
                    case NODE_LABEL:
                    case NODE_LABEL_POSITION:
                    case EDGE_LINETYPE:
                    case EDGE_SRCARROW_SHAPE:
                    case EDGE_TGTARROW_SHAPE:
                    case EDGE_LABEL:
                        discreteRenderer.setIcon(
                            DiscreteTrackRenderer.getTrackGraphicIcon(wi, 70,
                                (ContinuousMapping) firstMap));
                        pr.registerRenderer(graphicalView, discreteRenderer);

                        break;

                    default:
                        break;
                    }
                } else if ((firstMap.getClass() == PassThroughMapping.class) &&
                        (attrName != null)) {
                    /*
                     * Passthrough
                     */
                    String id;
                    String value;
                    VizMapperProperty oneProperty;

                    while (it.hasNext()) {
                        id = ((GraphObject) it.next()).getIdentifier();
                        value = attr.getStringAttribute(id, attrName);

                        oneProperty = new VizMapperProperty();

                        if (attrName.equals("ID"))
                            oneProperty.setValue(id);
                        else
                            oneProperty.setValue(value);

                        // This prop. should not be editable!
                        oneProperty.setEditable(false);

                        oneProperty.setParentProperty(calculatorTypeProp);
                        oneProperty.setDisplayName(id);
                        oneProperty.setType(String.class);

                        calculatorTypeProp.addSubProperty(oneProperty);
                    }
                }
            }

            visualPropertySheetPanel.addProperty(calculatorTypeProp);
            visualPropertySheetPanel.setRendererFactory(pr);
            visualPropertySheetPanel.setEditorFactory(regr);
        }
    }

    private Set<Object> loadKeys(final String attrName,
        final CyAttributes attrs, final ObjectMapping mapping) {
        Map mapAttrs;
        mapAttrs = CyAttributesUtils.getAttribute(attrName, attrs);

        if ((mapAttrs == null) || (mapAttrs.size() == 0))
            return null;

        List acceptedClasses = Arrays.asList(mapping.getAcceptedDataClasses());
        Class mapAttrClass = CyAttributesUtils.getClass(attrName, attrs);

        if ((mapAttrClass == null) ||
                !(acceptedClasses.contains(mapAttrClass)))
            return null;

        return loadKeySet(mapAttrs);
    }

    /**
     * Loads the Key Set.
     */
    private Set<Object> loadKeySet(final Map mapAttrs) {
        final Set<Object> mappedKeys = new TreeSet<Object>();

        final Iterator keyIter = mapAttrs.values()
                                         .iterator();

        Object o = null;

        while (keyIter.hasNext()) {
            o = keyIter.next();

            if (o instanceof List) {
                List list = (List) o;

                for (int i = 0; i < list.size(); i++) {
                    Object vo = list.get(i);

                    if (!mappedKeys.contains(vo))
                        mappedKeys.add(vo);
                }
            } else {
                if (!mappedKeys.contains(o))
                    mappedKeys.add(o);
            }
        }

        return mappedKeys;
    }

    public void setDefaultPanel(JPanel panel) {
        /*
         * Clean panel
         */
        defaultAppearencePanel.setMinimumSize(new Dimension(100, 100));
        defaultAppearencePanel.setPreferredSize(
            new Dimension(
                mainSplitPane.getWidth(),
                this.mainSplitPane.getDividerLocation()));
        defaultAppearencePanel.setSize(
            defaultAppearencePanel.getPreferredSize());

        Dimension panelSize = this.defaultAppearencePanel.getSize();
        System.out.println("----------- panelSize: " + panelSize);

        GraphView view = ((NodeFullDetailView) panel).getView();

        if (view != null) {
            ((DGraphView) view).getCanvas()
             .setSize((int) panelSize.getWidth() - 40,
                (int) panelSize.getHeight() - 40);
            view.fitContent();

            defaultAppearencePanel.setLayout(null);

            Component canvas = view.getComponent();
            canvas.setLocation(20, 20);

            defaultAppearencePanel.removeAll();
            canvas.addMouseListener(new DefaultMouseListener());
            defaultAppearencePanel.add(canvas);

            repaint();
        }
    }

    class DefaultMouseListener extends MouseAdapter {
        public void mouseClicked(MouseEvent e) {
            if (javax.swing.SwingUtilities.isLeftMouseButton(e)) {
                final JPanel panel = DefaultAppearenceBuilder.showDialog(null);
                setDefaultPanel(panel);
            }
        }
    }

    /*
     * Update Visual Style Name Combobox when Session is loaded.
     *
     * (non-Javadoc)
     *
     * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
     */

    /**
     * Handle propeaty change events.
     *
     * @param e
     *            DOCUMENT ME!
     */
    public void propertyChange(PropertyChangeEvent e) {
//        System.out.println("Got Signal: " + e.getOldValue() + ", " +
//            e.getNewValue() + ", SOURCE = " + e.getSource());

        /*
         * Managing editor windows.
         */
        if (e.getPropertyName() == ContinuousMappingEditorPanel.EDITOR_WINDOW_OPENED) {
            this.editorWindowManager.put((VisualPropertyType) e.getNewValue(),
                (JDialog) e.getSource());

            return;
        } else if (e.getPropertyName() == ContinuousMappingEditorPanel.EDITOR_WINDOW_CLOSED) {
            this.editorWindowManager.remove((VisualPropertyType) e.getNewValue());

            return;
        }

        /*
         * Get global siginal
         */
        if (e.getPropertyName()
                 .equals(Cytoscape.SESSION_LOADED)) {
            setVSSelector();
            vsNameComboBox.setSelectedItem(vmm.getVisualStyle().getName());
            System.out.println("Visual Style Switched: " +
                vmm.getVisualStyle().getName());

            return;
        }

        /*
         * Ignore if same signal
         */
        if (e.getNewValue() == e.getOldValue())
            return;

        final PropertySheetTable table = visualPropertySheetPanel.getTable();
        final int selected = table.getSelectedRow();

        /*
         * Do nothing if not selected.
         */
        if (selected < 0)
            return;

        Item selectedItem = (Item) visualPropertySheetPanel.getTable()
                                                           .getValueAt(selected,
                0);
        VizMapperProperty prop = (VizMapperProperty) selectedItem.getProperty();

        final VisualPropertyType type;
        String ctrAttrName = null;

        if ((prop.getParentProperty() == null) &&
                e.getNewValue() instanceof String) {
            /*
             * This is a controlling attr name.
             */
            type = (VisualPropertyType) ((VizMapperProperty) prop).getHiddenObject();
            ctrAttrName = (String) e.getNewValue();
        } else
            type = (VisualPropertyType) ((VizMapperProperty) prop.getParentProperty()).getHiddenObject();

        /*
         * Mapping type changed
         */
        if (prop.getHiddenObject() instanceof ObjectMapping) {
            System.out.println("Mapping type changed: " +
                prop.getHiddenObject().toString());

            if (e.getNewValue() == null)
                return;

            switchMapping(
                prop,
                e.getNewValue().toString());

            /*
             * restore expanded props.
             */
            expandLastSelectedItem(type.getName());

            return;
        }

        /*
         * Extract calculator
         */
        final ObjectMapping mapping;

        if (type.isNodeProp())
            mapping = vmm.getVisualStyle()
                         .getNodeAppearanceCalculator()
                         .getCalculator(type)
                         .getMapping(0);
        else
            mapping = vmm.getVisualStyle()
                         .getEdgeAppearanceCalculator()
                         .getCalculator(type)
                         .getMapping(0);

        /*
         * This is a attribute name change.
         */
        if (ctrAttrName != null) {
            mapping.setControllingAttributeName(
                ctrAttrName,
                vmm.getNetwork(),
                false);
            vmm.getNetworkView()
               .redrawGraph(false, true);

            setPropertyTable();

            expandLastSelectedItem(type.getName());

            return;
        }

        if (mapping instanceof ContinuousMapping ||
                mapping instanceof PassThroughMapping)
            return;

        Object key = ((Item) visualPropertySheetPanel
                      .getTable()
                      .getValueAt(selected, 0)).getProperty()
                      .getDisplayName();
        ((DiscreteMapping) mapping).putMapValue(
            key,
            e.getNewValue());

        /*
         * Update table and current network view.
         */
        table.repaint();
        vmm.getNetworkView()
           .redrawGraph(false, true);
    }

    /**
     * Switching between mapppings. Each calcs has 3 mappings. The first one
     * (getMapping(0)) is the current mapping used by calculator.
     *
     */
    private void switchMapping(VizMapperProperty prop, String newMapName) {
        final VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) prop.getParentProperty()).getHiddenObject();
        final String newCalcName = type.getName() + "-" + newMapName;

        // Extract target calculator
        Calculator newCalc = vmm.getCalculatorCatalog()
                                .getCalculator(type, newCalcName);

        Calculator oldCalc = null;

        if (type.isNodeProp())
            oldCalc = vmm.getVisualStyle()
                         .getNodeAppearanceCalculator()
                         .getCalculator(type);
        else
            oldCalc = vmm.getVisualStyle()
                         .getEdgeAppearanceCalculator()
                         .getCalculator(type);

        /*
         * If not exist, create new one.
         */
        if (newCalc == null) {
            System.out.println("====== Need to create: " + newCalcName);
            vmm.getCalculatorCatalog()
               .addCalculator(getNewCalculator(type, newMapName, newCalcName));
        }

        newCalc = vmm.getCalculatorCatalog()
                     .getCalculator(type, newCalcName);

        if (type.isNodeProp())
            vmm.getVisualStyle()
               .getNodeAppearanceCalculator()
               .setCalculator(newCalc);
        else
            vmm.getVisualStyle()
               .getEdgeAppearanceCalculator()
               .setCalculator(newCalc);

        /*
         * If old calc is not standard name, rename it.
         */
        if (oldCalc != null) {
            final String oldMappingTypeName;

            if (oldCalc.getMapping(0) instanceof DiscreteMapping)
                oldMappingTypeName = "Discrete Mapper";
            else if (oldCalc.getMapping(0) instanceof ContinuousMapping)
                oldMappingTypeName = "Continuous Mapper";
            else if (oldCalc.getMapping(0) instanceof PassThroughMapping)
                oldMappingTypeName = "Passthrough Mapper";
            else
                oldMappingTypeName = null;

            System.out.println("Initial Mapper = " + oldMappingTypeName + ", " +
                oldCalc);

            final String oldCalcName = type.getName() + "-" +
                oldMappingTypeName;

            if (vmm.getCalculatorCatalog()
                       .getCalculator(type, oldCalcName) == null)
                vmm.getCalculatorCatalog()
                   .addCalculator(
                    getNewCalculator(type, oldMappingTypeName, oldCalcName));
        }

        setPropertyTable();
    }

    private void expandLastSelectedItem(String name) {
        final PropertySheetTable table = visualPropertySheetPanel.getTable();
        Item item = null;

        for (int i = 0; i < table.getRowCount(); i++) {
            item = (Item) table.getValueAt(i, 0);

            Property curProp = item.getProperty();

            if ((curProp != null) && (curProp.getDisplayName() == name)) {
                visualPropertySheetPanel.getTable()
                                        .setRowSelectionInterval(i, i);

                break;
            }
        }

        visualPropertySheetPanel.getTable()
                                .getActionMap()
                                .get("toggle")
                                .actionPerformed(null);
    }

    private Calculator getNewCalculator(final VisualPropertyType type,
        final String newMappingName, final String newCalcName) {
        System.out.println("Mapper = " + newMappingName);

        final CalculatorCatalog catalog = vmm.getCalculatorCatalog();

        Class mapperClass = catalog.getMapping(newMappingName);

        // create the selected mapper
        Class[] conTypes = { Object.class, byte.class };
        Constructor mapperCon;

        try {
            mapperCon = mapperClass.getConstructor(conTypes);
        } catch (NoSuchMethodException exc) {
            // Should not happen...
            System.err.println("Invalid mapper " + mapperClass.getName());

            return null;
        }

        // create the mapper
        final byte mapType; // node or edge calculator

        if (type.isNodeProp())
            mapType = ObjectMapping.NODE_MAPPING;
        else
            mapType = ObjectMapping.EDGE_MAPPING;

        final Object defaultObj = VizUIUtilities.getDefault(
                vmm.getVisualStyle(),
                type);
        final Object[] invokeArgs = { defaultObj, new Byte(mapType) };
        ObjectMapping mapper = null;

        try {
            mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
        } catch (Exception exc) {
            System.err.println("Error creating mapping");

            return null;
        }

        return CalculatorFactory.newDefaultCalculator(type, newCalcName, mapper);
    }

    /**
     * DOCUMENT ME!
     *
     * @param vsName
     *            DOCUMENT ME!
     */
    public void setCurrentVS(String vsName) {
        vsNameComboBox.setSelectedItem(vsName);
    }

    /**
     * DOCUMENT ME!
     *
     * @param event
     *            DOCUMENT ME!
     */
    public void onCyNetworkEvent(CyNetworkEvent event) {
        System.out.println("||||||||||||||| CNEVENT  !");
    }

    private void buildList() {
        mappingExist = new ArrayList<VisualPropertyType>();

        final VisualStyle vs = vmm.getVisualStyle();
        final NodeAppearanceCalculator nac = vs.getNodeAppearanceCalculator();
        final EdgeAppearanceCalculator eac = vs.getEdgeAppearanceCalculator();

        ObjectMapping mapping = null;

        for (VisualPropertyType type : VisualPropertyType.values()) {
            Calculator calc = nac.getCalculator(type);

            if (calc == null) {
                calc = eac.getCalculator(type);

                if (calc != null)
                    mapping = calc.getMapping(0);
            } else
                mapping = calc.getMapping(0);

            if (mapping == null)
                mappingExist.add(type);

            mapping = null;
        }

        System.out.println("Undef = " + mappingExist.size());
    }

    /*
     * Actions for option menu
     */
    protected class LegendListener extends AbstractAction {
        JDialog parent;

        public LegendListener(JDialog p) {
            parent = p;
        }

        public void actionPerformed(ActionEvent e) {
            final SwingWorker worker = new SwingWorker() {
                    public Object construct() {
                        LegendDialog ld = new LegendDialog(parent,
                                vmm.getVisualStyle());
                        ld.setVisible(true);

                        return null;
                    }
                };

            worker.start();
        }
    }

    protected class NewStyleListener extends AbstractAction {
        public void actionPerformed(ActionEvent e) {
            // just create a new style with all mappers set to none
            // get a name for the new calculator
            String name = getStyleName(null);

            if (name == null)
                return;

            // create the new style
            final VisualStyle currentStyle = new VisualStyle(name);
            // add it to the catalog
            vmm.getCalculatorCatalog()
               .addVisualStyle(currentStyle);
            // resetStyles(); // rebuild the combo box
            // set the new style in VMM, which will trigger an update
            // to the current selection in the combo box
            vmm.setVisualStyle(currentStyle);

            Cytoscape.getNetworkView(
                Cytoscape.getCurrentNetwork().getIdentifier())
                     .setVisualStyle(currentStyle.getName());

            // this applies the new style to the graph
            vmm.getNetworkView()
               .redrawGraph(false, true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param s
     *            DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStyleName(VisualStyle s) {
        String suggestedName = null;

        if (s != null)
            suggestedName = vmm.getCalculatorCatalog()
                               .checkVisualStyleName(s.getName());

        // keep prompting for input until user cancels or we get a valid
        // name
        while (true) {
            String ret = (String) JOptionPane.showInputDialog(
                    Cytoscape.getDesktop(),
                    "Name for new visual style",
                    "Visual Style Name Input",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    suggestedName);

            if (ret == null)
                return null;

            String newName = vmm.getCalculatorCatalog()
                                .checkVisualStyleName(ret);

            if (newName.equals(ret))
                return ret;

            int alt = JOptionPane.showConfirmDialog(
                    Cytoscape.getDesktop(),
                    "Visual style with name " + ret +
                    " already exists,\nrename to " + newName + " okay?",
                    "Duplicate visual style name",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null);

            if (alt == JOptionPane.YES_OPTION)
                return newName;
        }
    }

    // /**
    // * Rename a Visual Style<br>
    // *
    // */
    // protected class RenameStyleListener extends AbstractAction {
    // public void actionPerformed(ActionEvent e) {
    // String oldName = currentStyle.getName();
    // String name = getStyleName(currentStyle);
    //
    // // System.out.println("******** Old VS name = " + oldName + ",
    // // New VS name = " + name);
    //
    // if (name == null) {
    // return;
    // }
    //
    // // no need to inform the VMM, since only the name changed <--
    // // This is VERY wrong!
    // // We need to update calatog AND VMM.
    // currentStyle.setName(name);
    //
    // catalog.removeVisualStyle(oldName);
    // catalog.addVisualStyle(currentStyle);
    // resetStyles(); // rebuild the combo box
    //
    // VMM.setVisualStyle(currentStyle);
    // }
    // }
    //
    // protected class RemoveStyleListener extends AbstractAction {
    // public void actionPerformed(ActionEvent e) {
    // if (styles.size() == 1) {
    // JOptionPane.showMessageDialog(myself,
    // "There must be at least one visual style",
    // "Cannot remove style", JOptionPane.ERROR_MESSAGE);
    // return;
    // }
    // // make sure the user really wants to do this
    // String styleName = currentStyle.getName();
    // String checkString = "Are you sure you want to permanently delete"
    // + " the visual style named '" + styleName + "'?";
    // int ich = JOptionPane.showConfirmDialog(myself, checkString,
    // "Confirm Delete Style", JOptionPane.YES_NO_OPTION);
    // if (ich == JOptionPane.YES_OPTION) {
    // catalog.removeVisualStyle(currentStyle.getName());
    // // try to switch to the default style
    // currentStyle = catalog.getVisualStyle("default");
    // if (currentStyle == null) {// not found, pick the first
    // // valid style
    // currentStyle = (VisualStyle) styles.iterator().next();
    // }
    // resetStyles(); // rebuild the combo box
    // // set the new style in VMM, which will trigger an update
    // // to the current selection in the combo box
    // VMM.setVisualStyle(currentStyle);
    // // this applies the new style to the graph
    // VMM.getNetworkView().redrawGraph(false, true);
    // }
    // }
    // }
    //
    // protected class DupeStyleListener extends AbstractAction {
    // public void actionPerformed(ActionEvent e) {
    // VisualStyle clone = null;
    // try {
    // clone = (VisualStyle) currentStyle.clone();
    // } catch (CloneNotSupportedException exc) {
    // System.err.println("Clone not supported exception!");
    // }
    // // get new name for clone
    // String newName = getStyleName(clone);
    // if (newName == null) {
    // return;
    // }
    // clone.setName(newName);
    // // add new style to the catalog
    // catalog.addVisualStyle(clone);
    // currentStyle = clone;
    // resetStyles(); // rebuild the combo box
    // // set the new style in VMM, which will trigger an update
    // // to the current selection in the combo box
    // VMM.setVisualStyle(currentStyle);
    // // this applies the new style to the graph
    // VMM.getNetworkView().redrawGraph(false, true);
    // }
    // }

    /*
     * Create new calculator (mapping)
     */
    private void createNewMapping(VisualPropertyType type) {
        // get available mappings
        final CalculatorCatalog catalog = vmm.getCalculatorCatalog();
        final Set mapperNames = catalog.getMappingNames();

        // convert to array for JOptionPane
        // Object[] mapperArray = mapperNames.toArray();

        // get a name for the new calculator
        final String defaultMapperName = "Discrete Mapper";
        String calcName = type.getName() + "-" + defaultMapperName;

        // create the new calculator
        // get the selected mapper
        Class mapperClass = catalog.getMapping(defaultMapperName);

        // create the selected mapper
        Class[] conTypes = { Object.class, byte.class };
        Constructor mapperCon;

        try {
            mapperCon = mapperClass.getConstructor(conTypes);
        } catch (NoSuchMethodException exc) {
            // Should not happen...
            System.err.println("Invalid mapper " + mapperClass.getName());

            return;
        }

        // create the mapper
        final byte mapType; // node or edge calculator

        if (type.isNodeProp())
            mapType = ObjectMapping.NODE_MAPPING;
        else
            mapType = ObjectMapping.EDGE_MAPPING;

        final Object defaultObj = VizUIUtilities.getDefault(
                vmm.getVisualStyle(),
                type);
        final Object[] invokeArgs = { defaultObj, new Byte(mapType) };
        ObjectMapping mapper = null;

        try {
            mapper = (ObjectMapping) mapperCon.newInstance(invokeArgs);
        } catch (Exception exc) {
            System.err.println("Error creating mapping");

            return;
        }

        Calculator calc = vmm.getCalculatorCatalog()
                             .getCalculator(type, calcName);

        if (calc == null) {
            calc = CalculatorFactory.newDefaultCalculator(type, calcName, mapper);

            vmm.getCalculatorCatalog()
               .addCalculator(calc);
        }

        if (type.isNodeProp())
            vmm.getVisualStyle()
               .getNodeAppearanceCalculator()
               .setCalculator(calc);
        else
            vmm.getVisualStyle()
               .getEdgeAppearanceCalculator()
               .setCalculator(calc);

        vsNameComboBoxActionPerformed(null);

        visualPropertySheetPanel.getTable()
                                .setColumnSelectionAllowed(false);
        visualPropertySheetPanel.getTable()
                                .setRowSelectionInterval(2, 5);

        // visualPropertySheetPanel.repaint();
    }

    /**
     * Remove a mapping from current visual style.
     *
     */
    private void removeMapping() {
        final int selected = visualPropertySheetPanel.getTable()
                                                     .getSelectedRow();

        if (0 <= selected) {
            Item item = (Item) visualPropertySheetPanel.getTable()
                                                       .getValueAt(selected, 0);

            Property curProp = item.getProperty();

            if (curProp instanceof VizMapperProperty) {
                VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) curProp).getHiddenObject();

                String[] message = {
                        "The Mapping for " + type.getName() +
                        " will be removed.", "Proceed？"
                    };
                int value = JOptionPane.showConfirmDialog(
                        Cytoscape.getDesktop(),
                        message,
                        "Remove Mapping",
                        JOptionPane.YES_NO_OPTION);

                if (value == JOptionPane.YES_OPTION) {
                    /*
                     * First, remove from property sheet.
                     */

                    // visualPropertySheetPanel.removeProperty(curProp);
                    /*
                     * Then, remove from calculator & redraw
                     */
                    if (type.isNodeProp())
                        vmm.getVisualStyle()
                           .getNodeAppearanceCalculator()
                           .removeCalculator(type);
                    else
                        vmm.getVisualStyle()
                           .getEdgeAppearanceCalculator()
                           .removeCalculator(type);

                    vmm.getNetworkView()
                       .redrawGraph(false, true);

                    /*
                     * Finally, move the visual property to "unused list"
                     */

                    // mappingExist.add(type);
                    vsNameComboBoxActionPerformed(null);
                }
            }
        }
    }

    /**
     * Edit all selected cells at once.
     *
     * This is for Discrete Mapping only.
     *
     */
    private void editSelectedCells() {
        final PropertySheetTable table = visualPropertySheetPanel.getTable();
        final int[] selected = table.getSelectedRows();

        Item item = null;

        /*
         * Test with the first selected item
         */
        item = (Item) visualPropertySheetPanel.getTable()
                                              .getValueAt(selected[0], 0);

        VizMapperProperty prop = (VizMapperProperty) item.getProperty();
        final VisualPropertyType type = (VisualPropertyType) ((VizMapperProperty) prop.getParentProperty()).getHiddenObject();

        /*
         * Extract calculator
         */
        final ObjectMapping mapping;

        if (type.isNodeProp())
            mapping = vmm.getVisualStyle()
                         .getNodeAppearanceCalculator()
                         .getCalculator(type)
                         .getMapping(0);
        else
            mapping = vmm.getVisualStyle()
                         .getEdgeAppearanceCalculator()
                         .getCalculator(type)
                         .getMapping(0);

        if (mapping instanceof ContinuousMapping ||
                mapping instanceof PassThroughMapping)
            return;

        Object newValue = null;

        try {
            newValue = type.showDiscreteEditor();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        String key = null;

        for (int i = 0; i < selected.length; i++) {
            /*
             * First, update property sheet
             */
            ((Item) visualPropertySheetPanel
             .getTable()
             .getValueAt(selected[i], 0)).getProperty()
             .setValue(newValue);
            /*
             * Then update backend.
             */
            key = ((Item) visualPropertySheetPanel
                   .getTable()
                   .getValueAt(selected[i], 0)).getProperty()
                   .getDisplayName();
            ((DiscreteMapping) mapping).putMapValue(key, newValue);
        }

        /*
         * Update table and current network view.
         */
        table.repaint();
        vmm.getNetworkView()
           .redrawGraph(false, true);
    }

    private void applyChange(VisualPropertyType type, int[] selected) {
    }

    private void switchCalculator(Calculator calc, VisualPropertyType type) {
        // do nothing if the new calculator is the same as the current one
        // if ((calc != null) && calc.equals(this.currentCalculator)) {
        // return;
        // }

        // setCurrentCalculator(calc); // handles listeners

        // tell the respective appearance calculators
        // this method doesn't fire an event to come back to us
        // VizUIUtilities.setCurrentCalculator(vmm.getVisualStyle(), type,
        // calc);
        //
        // // get the view of the new calculator
        // refreshUI();

        // Commented out to prevent auto-updates
        // VMM.getNetworkView().redrawGraph(false, true);
    }

    class DSourceList extends JList
        implements Transferable, DragGestureListener {
        public DSourceList(Object[] list) {
            super(list);

            DragSource dragSource = new DragSource();
            DragGestureRecognizer dgr = dragSource.createDefaultDragGestureRecognizer(this,
                    DnDConstants.ACTION_COPY, this);
        }

        public Object getTransferData(DataFlavor arg0)
            throws UnsupportedFlavorException, IOException {
            // TODO Auto-generated method stub
            return "AAA";
        }

        public DataFlavor[] getTransferDataFlavors() {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isDataFlavorSupported(DataFlavor flavor) {
            // TODO Auto-generated method stub
            return false;
        }

        public void dragGestureRecognized(DragGestureEvent e) {
            // TODO Auto-generated method stub
            System.out.println("From.dragGestureRecognized()\n " + e);

            // Copy/Moveのアクションならドラッグを開始する
            if ((e.getDragAction() | DnDConstants.ACTION_COPY_OR_MOVE) != 0)
                e.startDrag(DragSource.DefaultCopyDrop, this, null);
        }
    }
}
