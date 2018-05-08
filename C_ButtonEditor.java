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
	protected JButton button;
	private String label;
	private String isPushed;

	public C_ButtonEditor(JCheckBox checkBox, JTable table, DefaultTableModel dtmb) {

		super(checkBox);
		button = new JButton();
		button.setOpaque(true);
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop()
							.browse(new URL((dtmb.getValueAt(table.getSelectedRow(), 1)).toString()).toURI());
					/*
					 * 중요파트 : 선택된 첫번째 행과 첫번째 열에서 값을 얻고, 그 값을 스트링으로 변환한 값 = 다시말해 숨겨놓았던 테이블의 url값들
					 */

				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}

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

	public Object getCellEditorValue(String link) {
		if (isPushed == "") {
			try {
				Desktop.getDesktop().browse(new URL(link).toURI());
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		isPushed = "";
		return new String(label);
	}

	public boolean stopCellEditing() {
		isPushed = "";
		return super.stopCellEditing();
	}

	protected void fireEditingStopped() {
		super.fireEditingStopped();
	}
}
