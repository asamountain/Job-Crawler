package Crawler;


import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * @version 1.0 11/09/98
 */
public class C_ButtonRenderer extends JButton implements TableCellRenderer {

	// Constructor: sets the button to be opaque for proper rendering
	public C_ButtonRenderer() {
		setOpaque(true);
	}

	// Returns the button component for the cell, with appropriate colors for selection
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(UIManager.getColor("Button.background"));
		}
		return this;
	}
}
