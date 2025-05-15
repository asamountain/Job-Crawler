package Crawler;


import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.*;
import javax.swing.table.*;

/**
 * @version 1.0 11/09/98
 */
public class C_ButtonEditor extends DefaultCellEditor {
	// The button displayed in the cell
	protected JButton button;
	// The label for the button
	private String label;
	// Used to track if the button was pushed
	private String isPushed;

	// Constructor: sets up the button and its action for opening URLs
	public C_ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel dtmb) {
		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					// Open the URL from the hidden column in the default browser
					Desktop.getDesktop()
						.browse(new URL((dtmb.getValueAt(table.getSelectedRow(), 1)).toString()).toURI());
				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	// Returns the button component for the cell
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if (isSelected) {
			button.setForeground(table.getSelectionForeground());
			button.setBackground(table.getSelectionBackground());
		} else {
			button.setForeground(table.getForeground());
			button.setBackground(table.getBackground());
			return button;
		}
		label = (value == null) ? "" : value.toString();
		button.setText(label);
		isPushed = "";
		return button;
	}

	// Opens the link if the button was pushed (not used in this context)
	public Object getCellEditorValue(String link) {
		if (isPushed == "") {
			try {
				Desktop.getDesktop().browse(new URL(link).toURI());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		isPushed = "";
		return new String(label);
	}

	// Resets the push state when editing stops
	public boolean stopCellEditing() {
		isPushed = "";
		return super.stopCellEditing();
	}

	// Notifies that editing has stopped
	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
