package browser.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.text.Keymap;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Document;
import javax.swing.text.Keymap;
import javax.swing.text.TextAction;
import javax.swing.text.JTextComponent;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;

import java.util.EventObject;

import browser.ValidatedObjectAndEditString;


public class MultiLineTableCellEditor extends AbstractCellEditor implements TableCellEditor,
                                                                            ActionListener
{
	private ResizableTextArea textArea;

	/**
	 * Creates a new MultiLineTableCellEditor object.
	 */
	public MultiLineTableCellEditor() {
		textArea = new ResizableTextArea(this);
		textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textArea.setLineWrap(true);
		textArea.setWrapStyleWord(true);
	}

	public Object getCellEditorValue() {
		return textArea.getText();
	}

	protected int clickCountToStart = 2;

	public int getClickCountToStart() {
		return clickCountToStart;
	}

	public void setClickCountToStart(int clickCountToStart) {
		this.clickCountToStart = clickCountToStart;
	}

	public boolean isCellEditable(EventObject e) {
		return !(e instanceof MouseEvent)
		       || (((MouseEvent) e).getClickCount() >= clickCountToStart);
	}

	public void actionPerformed(ActionEvent ae) {
		stopCellEditing();
	}

	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
	                                             final int row, final int column)
	{
		final String text = (value != null) ? ((ValidatedObjectAndEditString)value).getEditString() : "";
		textArea.setTable(table);
		textArea.setText(text);

		return textArea;
	}

	public static final String UPDATE_BOUNDS = "UpdateBounds";

	class ResizableTextArea extends JTextArea implements KeyListener {
		private JTable table;
		private MultiLineTableCellEditor parent;

		ResizableTextArea(final MultiLineTableCellEditor parent) {
			super();
			this.parent = parent;
			addKeyListener(this);
		}

		public void setTable(JTable t) {
			table = t;
		}

		public void setText(String text) {
			super.setText(text);
			updateBounds();
		}

		public void setBounds(int x, int y, int width, int height) {
			if (Boolean.TRUE.equals(getClientProperty(UPDATE_BOUNDS)))
				super.setBounds(x, y, width, height);
		}

		public void addNotify() {
			super.addNotify();
			getDocument().addDocumentListener(listener);
		}

		public void removeNotify() {
			getDocument().removeDocumentListener(listener);
			super.removeNotify();
		}

		DocumentListener listener = new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				updateBounds();
			}

			public void removeUpdate(DocumentEvent e) {
				updateBounds();
			}

			public void changedUpdate(DocumentEvent e) {
				updateBounds();
			}
		};

		private void updateBounds() {
			if (table == null) {
				System.err.println("table is null");
				return;
			}

			if (table.isEditing()) {
				Rectangle cellRect = table.getCellRect(table.getEditingRow(),
				                                       table.getEditingColumn(), false);
				Dimension prefSize = getPreferredSize();
				putClientProperty(UPDATE_BOUNDS, Boolean.TRUE);
				setBounds(getX(), getY(), Math.min(cellRect.width, prefSize.width),
				          Math.max(cellRect.height + prefSize.height, prefSize.height));
				putClientProperty(UPDATE_BOUNDS, Boolean.FALSE);
				validate();
			}
		}

		//
		// KeyListener Interface
		//

		public void keyTyped(KeyEvent e) {}

		public void keyReleased(KeyEvent e) {}

		public void keyPressed(final KeyEvent event) {
			if (event.getKeyCode() != KeyEvent.VK_ENTER)
				return;

			final int modifiers = event.getModifiers();

			// We want to move to the next cell if Enter and no modifiers have been pressed:
			if (modifiers == 0) {
				parent.stopCellEditing();
				this.transferFocus();
				return;
			}

			// We want to move to the previous cell if Shift+Enter have been pressed:
			if (modifiers == KeyEvent.VK_SHIFT) {
				parent.stopCellEditing();
				this.transferFocusBackward();
				return;
			}

			// We want to insert a newline if Enter+Alt or Enter+Alt+Meta have been pressed:
			final int OPTION_AND_COMMAND = 12; // On Mac to emulate Excel.
			if (modifiers == KeyEvent.VK_ALT || modifiers == OPTION_AND_COMMAND) {
				final int caretPosition = this.getCaretPosition();
				final StringBuilder text = new StringBuilder(this.getText());
				this.setText(text.insert(caretPosition, '\n').toString());
				this.setCaretPosition(caretPosition + 1);
			}
		}
	}
}
