package com.projetofinal.backend.client.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class RetroComponents {

	public static final Color COLOR_BG = new Color(192, 192, 192);
	public static final Color COLOR_WHITE = new Color(255, 255, 255);
	public static final Color COLOR_LIGHT_GRAY = new Color(223, 223, 223);
	public static final Color COLOR_SHADOW = new Color(128, 128, 128);
	public static final Color COLOR_DARK_SHADOW = new Color(0, 0, 0);
	public static final Color COLOR_NAVY = new Color(0, 0, 128);
	public static final Color COLOR_SELECTION = new Color(0, 0, 128);
	public static final Color COLOR_TEXT = Color.BLACK;

	public static final Font FONT_PLAIN = new Font("Tahoma", Font.PLAIN, 11);
	public static final Font FONT_BOLD = new Font("Tahoma", Font.BOLD, 11);
	public static final Font FONT_TITLE = new Font("Tahoma", Font.BOLD, 12);
	public static final Font FONT_MONO = new Font("Courier New", Font.PLAIN, 12);

	public static Border createRaisedBevel() {
		return BorderFactory.createBevelBorder(
				BevelBorder.RAISED,
				COLOR_WHITE,
				COLOR_LIGHT_GRAY,
				COLOR_DARK_SHADOW,
				COLOR_SHADOW
		);
	}

	public static Border createLoweredBevel() {
		return BorderFactory.createBevelBorder(
				BevelBorder.LOWERED,
				COLOR_WHITE,
				COLOR_LIGHT_GRAY,
				COLOR_DARK_SHADOW,
				COLOR_SHADOW
		);
	}

	public static Border createFieldBorder() {
		return BorderFactory.createCompoundBorder(
				createLoweredBevel(),
				BorderFactory.createEmptyBorder(2, 4, 2, 4)
		);
	}

	public static Border createButtonBorder(boolean pressed) {
		Border bevel = pressed ? createLoweredBevel() : createRaisedBevel();
		return BorderFactory.createCompoundBorder(
				bevel,
				BorderFactory.createEmptyBorder(3, 10, 3, 10)
		);
	}

	public static TitledBorder createTitledGroupBorder(String title) {
		Border etched = BorderFactory.createEtchedBorder(COLOR_WHITE, COLOR_SHADOW);
		TitledBorder titled = BorderFactory.createTitledBorder(etched, " " + title + " ");
		titled.setTitleFont(FONT_BOLD);
		titled.setTitleColor(COLOR_TEXT);
		return titled;
	}

	public static JButton createButton(String text) {
		JButton button = new JButton(text);
		button.setFont(FONT_BOLD);
		button.setBackground(COLOR_BG);
		button.setForeground(COLOR_TEXT);
		button.setFocusPainted(false);
		button.setMargin(new Insets(2, 8, 2, 8));
		button.setBorder(createButtonBorder(false));

		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if (button.isEnabled()) {
					button.setBorder(createButtonBorder(true));
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (button.isEnabled()) {
					button.setBorder(createButtonBorder(false));
				}
			}
		});

		return button;
	}

	public static JTextField createTextField(int columns) {
		JTextField field = new JTextField(columns);
		field.setFont(FONT_PLAIN);
		field.setBackground(COLOR_WHITE);
		field.setForeground(COLOR_TEXT);
		field.setCaretColor(COLOR_TEXT);
		field.setBorder(createFieldBorder());
		return field;
	}

	public static JPasswordField createPasswordField(int columns) {
		JPasswordField field = new JPasswordField(columns);
		field.setFont(FONT_PLAIN);
		field.setBackground(COLOR_WHITE);
		field.setForeground(COLOR_TEXT);
		field.setCaretColor(COLOR_TEXT);
		field.setBorder(createFieldBorder());
		return field;
	}

	public static JLabel createLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(FONT_PLAIN);
		label.setForeground(COLOR_TEXT);
		return label;
	}

	public static JLabel createBoldLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(FONT_BOLD);
		label.setForeground(COLOR_TEXT);
		return label;
	}

	public static JPanel createStatusPanel(JLabel label, int preferredWidth) {
		JPanel panel = new JPanel();
		panel.setBackground(COLOR_BG);
		panel.setBorder(createLoweredBevel());
		if (preferredWidth > 0) {
			panel.setPreferredSize(new Dimension(preferredWidth, 22));
		}
		label.setFont(FONT_PLAIN);
		panel.add(label);
		return panel;
	}

	public static void styleTable(JTable table) {
		table.setFont(FONT_PLAIN);
		table.setBackground(COLOR_WHITE);
		table.setForeground(COLOR_TEXT);
		table.setSelectionBackground(COLOR_SELECTION);
		table.setSelectionForeground(COLOR_WHITE);
		table.setRowHeight(20);
		table.setShowGrid(true);
		table.setGridColor(new Color(210, 210, 210));

		JTableHeader header = table.getTableHeader();
		header.setFont(FONT_BOLD);
		header.setBackground(COLOR_BG);
		header.setForeground(COLOR_TEXT);
		header.setReorderingAllowed(false);

		DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				JLabel label = (JLabel) super.getTableCellRendererComponent(
						table, value, isSelected, hasFocus, row, column);
				label.setFont(FONT_BOLD);
				label.setBackground(COLOR_BG);
				label.setForeground(COLOR_TEXT);
				label.setBorder(BorderFactory.createCompoundBorder(
						createRaisedBevel(),
						BorderFactory.createEmptyBorder(2, 4, 2, 4)
				));
				return label;
			}
		};
		header.setDefaultRenderer(headerRenderer);
	}

	public static void applyRetroRecursively(Container container) {
		container.setBackground(COLOR_BG);
		for (Component comp : container.getComponents()) {
			if (comp instanceof JPanel) {
				comp.setBackground(COLOR_BG);
				applyRetroRecursively((Container) comp);
			}
		}
	}

	public static String formatErrorMessage(Throwable ex) {
		if (ex == null) {
			return "Unknown error";
		}
		if (ex instanceof com.projetofinal.backend.client.ApiException apiEx) {
			return apiEx.getFormattedDetails();
		}
		if (ex.getCause() instanceof com.projetofinal.backend.client.ApiException apiEx) {
			return apiEx.getFormattedDetails();
		}
		if (ex instanceof java.net.ConnectException) {
			return com.projetofinal.backend.client.i18n.I18n.get("error.connection_refused", "localhost:8080");
		}
		return ex.getMessage() != null ? ex.getMessage() : ex.toString();
	}

	public static void showErrorDialog(Component parent, Throwable ex) {
		String msg = formatErrorMessage(ex);
		javax.swing.JOptionPane.showMessageDialog(
				parent,
				msg,
				com.projetofinal.backend.client.i18n.I18n.get("dialog.error_title"),
				javax.swing.JOptionPane.ERROR_MESSAGE
		);
	}
}
