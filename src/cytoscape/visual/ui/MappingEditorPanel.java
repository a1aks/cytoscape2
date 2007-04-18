package cytoscape.visual.ui;

import cytoscape.Cytoscape;

import cytoscape.data.CyAttributes;

import cytoscape.data.attr.CountedIterator;
import cytoscape.data.attr.MultiHashMap;

import cytoscape.visual.VisualPropertyType;

import cytoscape.visual.calculators.Calculator;

import cytoscape.visual.mappings.ContinuousMapping;
import cytoscape.visual.mappings.continuous.ContinuousMappingPoint;

import org.jdesktop.swingx.JXMultiThumbSlider;

import java.util.ArrayList;

import javax.swing.JPanel;


/**
 * DOCUMENT ME!
 *
 * @author $author$
  */
public abstract class MappingEditorPanel extends JPanel {
    protected VisualPropertyType type;
    protected double maxValue;
    protected double minValue;
    protected ArrayList<ContinuousMappingPoint> allPoints;

    /** Creates new form ContinuousMapperEditorPanel */
    public MappingEditorPanel(VisualPropertyType type) {
        this.type = type;
        initComponents();
        setVisualPropLabel();

        setAttrComboBox();
    }

    protected void setVisualPropLabel() {
        this.visualPropertyLabel.setText("Visual Property: " + type.getName());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */

    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">
    private void initComponents() {
        rangeSettingPanel = new javax.swing.JPanel();
        pivotLabel = new javax.swing.JLabel();
        addButton = new javax.swing.JButton();
        deleteButton = new javax.swing.JButton();
        valueSpinner = new javax.swing.JSpinner();
        colorButton = new javax.swing.JButton();
        rangeEditorPanel = new javax.swing.JPanel();
        slider = new org.jdesktop.swingx.JXMultiThumbSlider();
        attrNameLabel = new javax.swing.JLabel();
        attributeComboBox = new javax.swing.JComboBox();
        iconPanel = new javax.swing.JPanel();
        visualPropertyLabel = new javax.swing.JLabel();

        rotaryEncoder = new JXMultiThumbSlider();

        rangeSettingPanel.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Range Setting",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 10),
                new java.awt.Color(0, 0, 0)));
        pivotLabel.setFont(new java.awt.Font("SansSerif", 1, 12));
        pivotLabel.setForeground(java.awt.Color.darkGray);
        pivotLabel.setText("Pivot:");

        addButton.setText("Add");
        addButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        addButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    addButtonActionPerformed(evt);
                }
            });

        deleteButton.setText("Delete");
        deleteButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        deleteButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    deleteButtonActionPerformed(evt);
                }
            });

        colorButton.setBackground(new java.awt.Color(0, 0, 255));
        colorButton.setForeground(new java.awt.Color(255, 51, 102));
        colorButton.setBorder(
            javax.swing.BorderFactory.createMatteBorder(
                4,
                4,
                4,
                4,
                new java.awt.Color(204, 204, 204)));
        colorButton.setOpaque(true);
        colorButton.setPreferredSize(new java.awt.Dimension(34, 25));
        colorButton.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    colorButtonActionPerformed(evt);
                }
            });

        org.jdesktop.layout.GroupLayout rangeSettingPanelLayout = new org.jdesktop.layout.GroupLayout(rangeSettingPanel);
        rangeSettingPanel.setLayout(rangeSettingPanelLayout);
        rangeSettingPanelLayout.setHorizontalGroup(
            rangeSettingPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                org.jdesktop.layout.GroupLayout.TRAILING,
                rangeSettingPanelLayout.createSequentialGroup().addContainerGap().add(
                    rangeSettingPanelLayout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.TRAILING).add(org.jdesktop.layout.GroupLayout.LEADING,
                        addButton,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62,
                        Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING,
                        colorButton,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62,
                        Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING,
                        valueSpinner,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62,
                        Short.MAX_VALUE).add(org.jdesktop.layout.GroupLayout.LEADING,
                        pivotLabel).add(org.jdesktop.layout.GroupLayout.LEADING,
                        deleteButton,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 62,
                        Short.MAX_VALUE)).addContainerGap()));
        rangeSettingPanelLayout.setVerticalGroup(
            rangeSettingPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                rangeSettingPanelLayout.createSequentialGroup().add(pivotLabel).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(valueSpinner,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(colorButton,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 23,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(addButton).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(deleteButton).addContainerGap(14,
                    Short.MAX_VALUE)));

        rangeEditorPanel.setBorder(
            javax.swing.BorderFactory.createTitledBorder(
                null,
                "Range Editor",
                javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                javax.swing.border.TitledBorder.DEFAULT_POSITION,
                new java.awt.Font("SansSerif", 1, 10),
                new java.awt.Color(0, 0, 0)));
        slider.setMaximumValue(100.0F);
        rotaryEncoder.setMaximumValue(100.0F);

        org.jdesktop.layout.GroupLayout sliderLayout = new org.jdesktop.layout.GroupLayout(slider);
        slider.setLayout(sliderLayout);
        sliderLayout.setHorizontalGroup(
            sliderLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 486,
                Short.MAX_VALUE));
        sliderLayout.setVerticalGroup(
            sliderLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 116,
                Short.MAX_VALUE));

        attrNameLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
        attrNameLabel.setForeground(java.awt.Color.darkGray);
        attrNameLabel.setText("Attribute Name");

        attributeComboBox.addActionListener(
            new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    attributeComboBoxActionPerformed(evt);
                }
            });

        org.jdesktop.layout.GroupLayout iconPanelLayout = new org.jdesktop.layout.GroupLayout(iconPanel);
        iconPanel.setLayout(iconPanelLayout);
        iconPanelLayout.setHorizontalGroup(
            iconPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING,
                rotaryEncoder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                84, Short.MAX_VALUE));
        iconPanelLayout.setVerticalGroup(
            iconPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(org.jdesktop.layout.GroupLayout.TRAILING,
                rotaryEncoder, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                146, Short.MAX_VALUE));

        org.jdesktop.layout.GroupLayout jXMultiThumbSlider1Layout = new org.jdesktop.layout.GroupLayout(rotaryEncoder);
        rotaryEncoder.setLayout(jXMultiThumbSlider1Layout);
        jXMultiThumbSlider1Layout.setHorizontalGroup(
            jXMultiThumbSlider1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 84,
                Short.MAX_VALUE));
        jXMultiThumbSlider1Layout.setVerticalGroup(
            jXMultiThumbSlider1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 65,
                Short.MAX_VALUE));

        visualPropertyLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
        visualPropertyLabel.setForeground(java.awt.Color.darkGray);

        org.jdesktop.layout.GroupLayout rangeEditorPanelLayout = new org.jdesktop.layout.GroupLayout(rangeEditorPanel);
        rangeEditorPanel.setLayout(rangeEditorPanelLayout);
        rangeEditorPanelLayout.setHorizontalGroup(
            rangeEditorPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                rangeEditorPanelLayout.createSequentialGroup().add(
                    rangeEditorPanelLayout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.LEADING).add(
                        rangeEditorPanelLayout.createSequentialGroup().addContainerGap().add(attrNameLabel).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(attributeComboBox,
                            0, 149, Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(visualPropertyLabel,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 273,
                            Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)).add(
                        rangeEditorPanelLayout.createSequentialGroup().add(iconPanel,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                            org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(slider,
                            org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 486,
                            Short.MAX_VALUE))).addContainerGap()));
        rangeEditorPanelLayout.setVerticalGroup(
            rangeEditorPanelLayout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(
                rangeEditorPanelLayout.createSequentialGroup().add(
                    rangeEditorPanelLayout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.BASELINE).add(attrNameLabel).add(attributeComboBox,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.PREFERRED_SIZE).add(visualPropertyLabel)).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(
                    rangeEditorPanelLayout.createParallelGroup(
                        org.jdesktop.layout.GroupLayout.LEADING).add(slider,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 116,
                        Short.MAX_VALUE).add(iconPanel,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                        Short.MAX_VALUE))));

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(
                layout.createSequentialGroup().add(rangeEditorPanel,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    Short.MAX_VALUE).addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).add(rangeSettingPanel,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
                    org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                    org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)));
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(rangeEditorPanel,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).add(rangeSettingPanel,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
                org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
    } // </editor-fold>               

    abstract protected void attributeComboBoxActionPerformed(
        java.awt.event.ActionEvent evt);

    abstract protected void deleteButtonActionPerformed(
        java.awt.event.ActionEvent evt);

    abstract protected void addButtonActionPerformed(
        java.awt.event.ActionEvent evt);

    abstract protected void colorButtonActionPerformed(
        java.awt.event.ActionEvent evt);

    private void setAttrComboBox() {
        // Reset
        attributeComboBox.removeAllItems();

        final CyAttributes attr;
        final Calculator calc;

        if (type.isNodeProp()) {
            attr = Cytoscape.getNodeAttributes();
            calc = Cytoscape.getVisualMappingManager()
                            .getVisualStyle()
                            .getNodeAppearanceCalculator()
                            .getCalculator(type);
        } else {
            attr = Cytoscape.getEdgeAttributes();
            calc = Cytoscape.getVisualMappingManager()
                            .getVisualStyle()
                            .getEdgeAppearanceCalculator()
                            .getCalculator(type);
        }

        final String[] names = attr.getAttributeNames();

        byte attrType;

        for (String name : names) {
            attrType = attr.getType(name);

            if ((attrType == CyAttributes.TYPE_FLOATING) ||
                    (attrType == CyAttributes.TYPE_INTEGER))
                attributeComboBox.addItem(name);
        }

        // Assume this calc only returns cont. mapping.
        if (calc.getMapping(0)
                    .getClass() == ContinuousMapping.class) {
            ContinuousMapping mapping = (ContinuousMapping) calc.getMapping(0);
            final String controllingAttrName = mapping.getControllingAttributeName();

            attributeComboBox.setSelectedItem(controllingAttrName);

            final MultiHashMap mhm = attr.getMultiHashMap();

            final CountedIterator it = mhm.getObjectKeys(controllingAttrName);
            Object key;
            maxValue = Double.NEGATIVE_INFINITY;
            minValue = Double.POSITIVE_INFINITY;

            while (it.hasNext()) {
                key = it.next();

                Double val = (Double) mhm.getAttributeValue((String) key,
                        controllingAttrName, null);

                if (val > maxValue)
                    maxValue = val;

                if (val < minValue)
                    minValue = val;

                //System.out.println("************ MHM OBJ= " + val);
            }

            System.out.println("----------- min max = " + minValue + ", " +
                maxValue);

            allPoints = mapping.getAllPoints();
        }
    }

    // Variables declaration - do not modify
    private javax.swing.JButton addButton;
    private javax.swing.JLabel attrNameLabel;
    private javax.swing.JComboBox attributeComboBox;
    protected javax.swing.JButton colorButton;
    private javax.swing.JButton deleteButton;
    protected javax.swing.JPanel iconPanel;
    private javax.swing.JLabel pivotLabel;
    private javax.swing.JPanel rangeEditorPanel;
    private javax.swing.JPanel rangeSettingPanel;
    protected org.jdesktop.swingx.JXMultiThumbSlider slider;
    private javax.swing.JSpinner valueSpinner;
    private javax.swing.JLabel visualPropertyLabel;
    protected JXMultiThumbSlider rotaryEncoder;

    // End of variables declaration
}
