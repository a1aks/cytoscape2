package cytoscape.data.servers.ui;

import static cytoscape.data.servers.ui.enums.ImportDialogColorTheme.ATTRIBUTE_NAME_COLOR;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ByteLookupTable;
import java.awt.image.LookupOp;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.jdesktop.layout.GroupLayout;

import cytoscape.Cytoscape;
import cytoscape.data.CyAttributes;
import cytoscape.util.URLUtil;
import cytoscape.util.swing.ColumnResizer;

/**
 * General purpose preview table panel.
 * 
 * @author kono
 * 
 */
public class PreviewTablePanel extends JPanel {

	public static final int ATTRIBUTE_PREVIEW = 1;
	public static final int NETWORK_PREVIEW = 2;

	private static final String DEF_MESSAGE = "Table header will be used as attribute names.  (Left click = enable/disable, Right click = edit)";

	private static final String DEF_TAB_MESSAGE = "Data File Preview Window";

	private static final String EXCEL_EXT = ".xls";

	private final ImageIcon spreadsheetIcon = new ImageIcon(Cytoscape.class
			.getResource("images/ximian/stock_new-spreadsheet.png"));
	private final ImageIcon textFileIcon = new ImageIcon(Cytoscape.class
			.getResource("images/ximian/stock_new-text-32.png"));

	private final String message;
	
	private boolean loadFlag = false;

	// Tracking attribute data type.
	private Byte[] dataTypes;

	private JLabel messageLabel;
	private JLabel rightArrowLabel;
	private JLabel fileTypeLabel;
	private JScrollPane previewScrollPane;
	private JTable previewTable;

	private Map<String, JTable> previewTables;

	private JTabbedPane tableTabbedPane;
	private JScrollPane keyPreviewScrollPane;

	private JList keyPreviewList;
	//private FilterField filterField;

	private DefaultTableModel model;
	private DefaultListModel keyListModel;

	private PropertyChangeSupport changes = new PropertyChangeSupport(this);

	private int panelType;

	public PreviewTablePanel() {
		this(DEF_MESSAGE, ATTRIBUTE_PREVIEW);
	}

	public PreviewTablePanel(String message) {
		this(message, ATTRIBUTE_PREVIEW);
	}

	public PreviewTablePanel(String message, int panelType) {
		this.message = message;
		this.panelType = panelType;

		initComponents();

		hideUnnecessaryComponents();
	}

	private void hideUnnecessaryComponents() {

		fileTypeLabel.setVisible(false);

		if (panelType == NETWORK_PREVIEW) {
			keyPreviewScrollPane.setVisible(false);
			rightArrowLabel.setVisible(false);

			repaint();
		}

	}

	public void setKeyAttributeList(Set data) {

		keyPreviewScrollPane.setBackground(Color.white);
		keyListModel.clear();

		for (Object item : data) {
			keyListModel.addElement(item);
		}
		keyPreviewList.repaint();
	}

	public void addPropertyChangeListener(PropertyChangeListener l) {
		changes.addPropertyChangeListener(l);
	}

	public void removePropertyChangeListener(PropertyChangeListener l) {
		changes.removePropertyChangeListener(l);
	}

	private void initComponents() {

		messageLabel = new JLabel();
		previewScrollPane = new JScrollPane();
		rightArrowLabel = new JLabel();
		tableTabbedPane = new JTabbedPane();
		keyListModel = new DefaultListModel();
		keyPreviewList = new JList(keyListModel);
		keyPreviewScrollPane = new JScrollPane();

		// model = new DefaultTableModel();

		previewTables = new HashMap<String, JTable>();
		previewTable = new JTable();
		previewTable.setOpaque(false);
		previewTable.setBackground(Color.white);

		fileTypeLabel = new JLabel();
		fileTypeLabel.setIcon(spreadsheetIcon);
		fileTypeLabel.setText("Excel Workbook");
		fileTypeLabel.setFont(new Font("Sans-Serif", Font.BOLD, 14));
		keyPreviewScrollPane.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Key Attributes"));

		keyPreviewList.setOpaque(false);
		keyPreviewList.setCellRenderer(new KeyAttributeListRenderer());
		keyPreviewScrollPane.setViewportView(keyPreviewList);

		previewScrollPane.setOpaque(false);
		previewScrollPane.setViewportView(previewTable);
		previewScrollPane.setBackground(Color.WHITE);

		BufferedImage datasourceImage = getFilteredImage(Cytoscape.class
				.getResource("images/ximian/data_sources_trans.png"));

		BufferedImage bi = getFilteredImage(Cytoscape.class
				.getResource("images/icon100_trans.png"));

		tableTabbedPane.setBackground(Color.white);
		tableTabbedPane
				.addChangeListener(new javax.swing.event.ChangeListener() {
					public void stateChanged(javax.swing.event.ChangeEvent evt) {
						tableTabbedPaneStateChanged(evt);
					}
				});

		previewScrollPane.getViewport().setOpaque(false);
		previewScrollPane.setViewportBorder(new CentredBackgroundBorder(
				datasourceImage));
		keyPreviewScrollPane.getViewport().setOpaque(false);
		keyPreviewScrollPane.setViewportBorder(new CentredBackgroundBorder(bi));

		tableTabbedPane.addTab(DEF_TAB_MESSAGE, previewScrollPane);
		// tableTabbedPane.setBorder(new CentredBackgroundBorder(bi));

		rightArrowLabel
				.setIcon(new javax.swing.ImageIcon(
						"/cellar/users/kono/workspace/FreshSVN2/images/ximian/stock_right-16.png"));

		JTableHeader hd = previewTable.getTableHeader();
		hd.setReorderingAllowed(false);
		hd
				.setDefaultRenderer(new HeaderRenderer(hd.getDefaultRenderer(),
						null));

		/*
		 * Setting table properties
		 */
		previewTable.setCellSelectionEnabled(false);
		previewTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		previewTable.setDefaultEditor(Object.class, null);

		this
				.setBorder(BorderFactory
						.createTitledBorder(
								javax.swing.BorderFactory
										.createBevelBorder(javax.swing.border.BevelBorder.RAISED),
								"Preview",
								javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
								javax.swing.border.TitledBorder.DEFAULT_POSITION,
								new java.awt.Font("Dialog", 1, 11)));

		messageLabel.setFont(new java.awt.Font("SansSerif", 1, 14));
		messageLabel.setForeground(new java.awt.Color(102, 102, 255));
		messageLabel.setText(message);

		GroupLayout previewPanelLayout = new GroupLayout(this);
		this.setLayout(previewPanelLayout);

		previewPanelLayout
				.setHorizontalGroup(previewPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								previewPanelLayout
										.createSequentialGroup()
										.add(
												tableTabbedPane,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												685, Short.MAX_VALUE)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(rightArrowLabel)
										.addPreferredGap(
												org.jdesktop.layout.LayoutStyle.RELATED)
										.add(
												previewPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING,
																false)

														.add(
																keyPreviewScrollPane,
																org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																194,
																Short.MAX_VALUE)))
						.add(
								previewPanelLayout
										.createSequentialGroup()
										.add(fileTypeLabel)
										.add(18, 18, 18)
										.add(
												messageLabel,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												754, Short.MAX_VALUE)
										.addContainerGap()));
		previewPanelLayout
				.setVerticalGroup(previewPanelLayout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(
								previewPanelLayout
										.createSequentialGroup()
										.add(
												previewPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.BASELINE)
														.add(fileTypeLabel)
														.add(messageLabel))
										.add(
												previewPanelLayout
														.createParallelGroup(
																org.jdesktop.layout.GroupLayout.LEADING)
														.add(
																previewPanelLayout
																		.createSequentialGroup()
																		.add(
																				97,
																				97,
																				97)
																		.add(
																				rightArrowLabel)
																		.addContainerGap())
														.add(
																previewPanelLayout
																		.createSequentialGroup()
																		.addPreferredGap(
																				org.jdesktop.layout.LayoutStyle.RELATED)
																		.add(
																				previewPanelLayout
																						.createParallelGroup(
																								org.jdesktop.layout.GroupLayout.LEADING)
																						.add(
																								previewPanelLayout
																										.createSequentialGroup()

																										.addPreferredGap(
																												org.jdesktop.layout.LayoutStyle.RELATED)
																										.add(
																												keyPreviewScrollPane,
																												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																												195,
																												Short.MAX_VALUE))
																						.add(
																								tableTabbedPane,
																								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
																								216,
																								Short.MAX_VALUE))))));

	}

	public JTable getPreviewTable() {
		JScrollPane selected = (JScrollPane) tableTabbedPane
				.getSelectedComponent();
		return (JTable) selected.getViewport().getComponent(0);
	}
	
	public int getTableCount() {
		return tableTabbedPane.getTabCount();
	}
	
	public String getSheetName(int index) {
		return tableTabbedPane.getTitleAt(index);
	}
	
	/**
	 * Get selected tab name.
	 * 
	 * @return name of the selected tab (i.e., sheet name)
	 */
	public String getSelectedSheetName() {
		return tableTabbedPane.getTitleAt(tableTabbedPane.getSelectedIndex());
	}
	
	public JTable getPreviewTable(int index) {
		JScrollPane selected = (JScrollPane) tableTabbedPane.getComponentAt(index);
		return (JTable) selected.getViewport().getComponent(0);
	}

	private void tableTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {
		System.out.println("tab cnahged!");
		if ( tableTabbedPane
				.getSelectedComponent() != null && ((JScrollPane) tableTabbedPane
				.getSelectedComponent()).getViewport().getComponent(0) != null && loadFlag == true) {

			changes.firePropertyChange(ImportTextTableDialog.SHEET_CHANGED,
					null, null);
		}
	}

	private BufferedImage getFilteredImage(URL url) {
		BufferedImage image;
		try {
			image = ImageIO.read(url);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
		}
		BufferedImage dest = new BufferedImage(image.getWidth(), image
				.getHeight(), BufferedImage.TYPE_INT_RGB);
		byte[] b = new byte[256];
		for (int i = 0; i < 256; i++)
			b[i] = (byte) (i * 0.2f);
		BufferedImageOp op = new LookupOp(new ByteLookupTable(0, b), null);
		// op.filter(image, dest);
		return image;
	}

	/**
	 * Generate preview table.<br>
	 * 
	 * <p>
	 * </p>
	 * 
	 * @throws IOException
	 */
	public void setPreviewTable(URL sourceURL, List<String> delimiters,
			TableCellRenderer renderer, int size) throws IOException {

		PreviewTablePanel curPanel = new PreviewTablePanel();

		for (int i = 0; i < tableTabbedPane.getTabCount(); i++) {
			System.out.println(tableTabbedPane.getTitleAt(i));

			tableTabbedPane.removeTabAt(i);
		}

		if (sourceURL.toString().endsWith(EXCEL_EXT)) {
			fileTypeLabel.setIcon(spreadsheetIcon);
			fileTypeLabel.setText("Excel Workbook");
			parseExcel(sourceURL, size, renderer);
		} else {
			fileTypeLabel.setIcon(textFileIcon);
			fileTypeLabel.setText("Text file");

			final BufferedReader bufRd = new BufferedReader(
					new InputStreamReader(URLUtil.getInputStream(sourceURL)));
			String line;

			/*
			 * Generate reg. exp. for delimiter.
			 */
			StringBuffer delimiterBuffer = new StringBuffer();

			if (delimiters.size() != 0) {
				delimiterBuffer.append("[");
				for (String delimiter : delimiters) {
					delimiterBuffer.append(delimiter);
				}
				delimiterBuffer.append("]");
			}

			/*
			 * Read & extract one line at a time. The line can be Tab delimited,
			 */
			boolean importAll = false;
			if (size == -1) {
				importAll = true;
			}

			int counter = 0;
			int maxColumn = 0;
			String[] parts;
			Vector data = new Vector();

			final String delimiterRegEx = delimiterBuffer.toString();

			while ((line = bufRd.readLine()) != null) {

				if (line.startsWith("!") || line.trim().length() == 0) {
					// ignore
				} else {
					Vector row = new Vector();
					if (delimiterRegEx.length() == 0) {
						parts = new String[1];
						parts[0] = line;
					} else {
						parts = line.split(delimiterRegEx);
					}
					for (String entry : parts) {
						row.add(entry);
					}
					if (parts.length > maxColumn) {
						maxColumn = parts.length;
					}
					data.add(row);
				}
				counter++;
				if (importAll == false && counter >= size) {
					break;
				}
			}

			bufRd.close();

			final Vector colNames = new Vector();
			for (int i = 0; i < maxColumn; i++) {
				colNames.add("Column " + (i + 1));
			}

			model = new DefaultTableModel(data, colNames);
			previewTable.setModel(model);
			String[] urlParts = sourceURL.toString().split("/");
			tableTabbedPane.add(urlParts[urlParts.length - 1],
					previewScrollPane);

			/*
			 * Initialize data type atrray. By default, everything is a String.
			 */
			dataTypes = new Byte[model.getColumnCount()];
			for (int i = 0; i < model.getColumnCount(); i++) {
				dataTypes[i] = CyAttributes.TYPE_STRING;
			}

			fileTypeLabel.setVisible(true);

			previewTable.setDefaultRenderer(Object.class, renderer);

			previewTable.getTableHeader().setForeground(
					ATTRIBUTE_NAME_COLOR.getColor());

			previewTable.getTableHeader().addMouseListener(
					new TableHeaderListener());

			ColumnResizer.adjustColumnPreferredWidths(previewTable);
			previewTable.revalidate();
			repaint();
		}

		loadFlag = true;
	}

	private void parseExcel(URL sourceURL, int size, TableCellRenderer renderer)
			throws IOException {
		// Load Excel workbook
		POIFSFileSystem excelIn = new POIFSFileSystem(sourceURL.openStream());
		HSSFWorkbook wb = new HSSFWorkbook(excelIn);

		int maxCol = 0;

		// Load all sheets in the table
		for (int i = 0; i < wb.getNumberOfSheets(); i++) {

			HSSFSheet sheet = wb.getSheetAt(i);
			Vector data = new Vector();

			int rowCount = 0;
			HSSFRow row;
			while ((row = sheet.getRow(rowCount)) != null && rowCount < size) {
				Iterator it = row.cellIterator();
				
				Vector rowVector = new Vector();
				for(short j=0; j<row.getPhysicalNumberOfCells(); j++) {
					HSSFCell cell = row.getCell(j);
					if(cell == null) {
						rowVector.add(null);
					} else if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
						rowVector.add(cell.getStringCellValue());
					} else if(cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
						rowVector.add(cell.getNumericCellValue());
					} else if(cell.getCellType() == HSSFCell.CELL_TYPE_BOOLEAN) {
						rowVector.add(cell.getBooleanCellValue());
					} else if(cell.getCellType() == HSSFCell.CELL_TYPE_BLANK || cell.getCellType() == HSSFCell.CELL_TYPE_ERROR) {
						rowVector.add(null);
					} else {
						rowVector.add(null);
					}
					
				}
				

				if (rowVector.size() > maxCol) {
					maxCol = rowVector.size();
				}
				data.add(rowVector);
				rowCount++;
			}

			System.out.println("** rows = " + data.size() + ", Cols = "
					+ maxCol);
			final Vector colNames = new Vector();
			for (int j = 0; j < maxCol; j++) {
				colNames.add("Column " + (j + 1));
			}

			TableModel newModel = new DefaultTableModel(data, colNames);
			JTable newTable = new JTable(newModel);
			previewTables.put(wb.getSheetName(i), newTable);
			JScrollPane newScrollPane = new JScrollPane();
			newScrollPane.setViewportView(newTable);
			newScrollPane.setBackground(Color.WHITE);

			tableTabbedPane.add(wb.getSheetName(i), newScrollPane);

			/*
			 * Initialize data type atrray. By default, everything is a String.
			 */
			dataTypes = new Byte[newModel.getColumnCount()];
			for (int j = 0; j < newModel.getColumnCount(); j++) {
				dataTypes[j] = CyAttributes.TYPE_STRING;
			}

			JTableHeader hd = newTable.getTableHeader();
			hd.setReorderingAllowed(false);
			hd.setDefaultRenderer(new HeaderRenderer(hd.getDefaultRenderer(),
					null));

			/*
			 * Setting table properties
			 */
			newTable.setCellSelectionEnabled(false);
			newTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			newTable.setDefaultEditor(Object.class, null);
			newTable.setDefaultRenderer(Object.class, renderer);

			newTable.getTableHeader().setForeground(
					ATTRIBUTE_NAME_COLOR.getColor());

			newTable.getTableHeader().addMouseListener(
					new TableHeaderListener());

			ColumnResizer.adjustColumnPreferredWidths(newTable);
			newTable.revalidate();
			newTable.repaint();

			// model = (DefaultTableModel) newModel;
			// previewTable = newTable;
		}

	}

	public int checkKeyMatch(int targetColumn) {
//		final List fileKeyList = new ArrayList();
//		int matched = 0;
//
//		TableModel curModel = getPreviewTable().getModel();
//		for (int i = 0; i < curModel.getRowCount(); i++) {
//			fileKeyList.add(curModel.getValueAt(i, targetColumn));
//		}
//
//		for (int i = 0; i < keyPreviewList.getModel().getSize(); i++) {
//			if (fileKeyList.contains(keyPreviewList.getModel().getElementAt(i))) {
//				matched++;
//			}
//		}

		return 0;
	}

	public void setAliasColumn(int column, boolean flag) {

		AttributePreviewTableCellRenderer rend = (AttributePreviewTableCellRenderer) getPreviewTable()
				.getCellRenderer(0, column);
		rend.setAliasFlag(column, flag);
		// rend.setImportFlag(column, !rend.getImportFlag(column));
		getPreviewTable().getTableHeader().resizeAndRepaint();
		getPreviewTable().repaint();
	}

	private final class TableHeaderListener implements MouseListener {
		public void mouseClicked(MouseEvent e) {

			JTable targetTable = getPreviewTable();

			final int column = targetTable.getColumnModel().getColumnIndexAtX(
					e.getX());

			if (SwingUtilities.isRightMouseButton(e)) {
				/*
				 * Right click: This action pops up an dialog to edit the
				 * attribute type and name.
				 */

				AttributeTypeDialog atd = new AttributeTypeDialog(Cytoscape
						.getDesktop(), true, targetTable.getColumnModel()
						.getColumn(column).getHeaderValue().toString(),
						dataTypes[column], column);

				atd.setLocationRelativeTo(targetTable.getParent());
				atd.setVisible(true);

				final String name = atd.getName();
				final byte newType = atd.getType();

				if (name != null) {
					targetTable.getColumnModel().getColumn(column)
							.setHeaderValue(name);
					targetTable.getTableHeader().resizeAndRepaint();

					if (newType == CyAttributes.TYPE_SIMPLE_LIST) {
						// listDelimiter = atd.getListDelimiterType();
						System.out.println("Fireing: "
								+ ImportTextTableDialog.LIST_DELIMITER_CHANGED);
						changes.firePropertyChange(
								ImportTextTableDialog.LIST_DELIMITER_CHANGED,
								null, atd.getListDelimiterType());
					}
					final Vector keyValPair = new Vector();
					keyValPair.add(column);
					keyValPair.add(newType);
					changes.firePropertyChange(
							ImportTextTableDialog.ATTR_DATA_TYPE_CHANGED, null,
							keyValPair);

					final Vector colNamePair = new Vector();
					colNamePair.add(column);
					colNamePair.add(name);
					changes.firePropertyChange(
							ImportTextTableDialog.ATTRIBUTE_NAME_CHANGED, null,
							colNamePair);
					dataTypes[column] = newType;

					targetTable.getTableHeader().setDefaultRenderer(
							new HeaderRenderer(targetTable.getTableHeader()
									.getDefaultRenderer(), dataTypes));
				}
			} else if (SwingUtilities.isLeftMouseButton(e)
					&& e.getClickCount() == 1) {
				AttributePreviewTableCellRenderer rend = (AttributePreviewTableCellRenderer) targetTable
						.getCellRenderer(0, column);
				rend.setImportFlag(column, !rend.getImportFlag(column));
				targetTable.getTableHeader().resizeAndRepaint();
				targetTable.repaint();
			}
		}

		public void mouseEntered(MouseEvent arg0) {
		}

		public void mouseExited(MouseEvent arg0) {
		}

		public void mousePressed(MouseEvent arg0) {
		}

		public void mouseReleased(MouseEvent arg0) {
		}
	}
}

class KeyAttributeListRenderer extends JLabel implements ListCellRenderer {

	private static final Font KEY_LIST_FONT = new Font("Sans-Serif", Font.BOLD,
			16);
	private static final Color FONT_COLOR = Color.BLACK;

	public KeyAttributeListRenderer() {
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {

		setFont(KEY_LIST_FONT);
		setForeground(FONT_COLOR);
		setText(value.toString());

		// setIcon(new ImageIcon(Cytoscape.class
		// .getResource("images/ximian/stock_form-checkbox-16.png")));
		this.setOpaque(false);
		return this;

	}
}

class CentredBackgroundBorder implements Border {
	private final BufferedImage image;

	public CentredBackgroundBorder(BufferedImage image) {
		this.image = image;
	}

	public void paintBorder(Component c, Graphics g, int x, int y, int width,
			int height) {
		x += (width - image.getWidth()) / 2;
		y += (height - image.getHeight()) / 2;
		((Graphics2D) g).drawRenderedImage(image, AffineTransform
				.getTranslateInstance(x, y));
	}

	public Insets getBorderInsets(Component c) {
		return new Insets(0, 0, 0, 0);
	}

	public boolean isBorderOpaque() {
		return true;
	}
}
