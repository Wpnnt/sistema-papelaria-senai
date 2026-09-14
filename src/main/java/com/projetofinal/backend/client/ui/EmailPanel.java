package com.projetofinal.backend.client.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import com.projetofinal.backend.client.ApiClient;
import com.projetofinal.backend.client.i18n.I18n;

public class EmailPanel extends JPanel {

	private final ApiClient apiClient;
	private final Consumer<String> statusUpdater;

	private TitledBorder headerTitledBorder;
	private TitledBorder bodyTitledBorder;
	private JPanel fieldsPanel;
	private JPanel bodyPanel;

	private JLabel lblFrom;
	private JLabel lblTo;
	private JLabel lblSubject;

	private JTextField txtFrom;
	private JTextField txtTo;
	private JTextField txtSubject;
	private JTextArea txtBody;

	private JButton btnSend;
	private JButton btnClear;

	public EmailPanel(ApiClient apiClient, Consumer<String> statusUpdater) {
		this.apiClient = apiClient;
		this.statusUpdater = statusUpdater != null ? statusUpdater : s -> {};

		setLayout(new BorderLayout(8, 8));
		setBackground(RetroComponents.COLOR_BG);
		setBorder(RetroComponents.createRaisedBevel());

		initComponents();
		updateLanguage();

		I18n.addListener(this::updateLanguage);
	}

	private void initComponents() {
		fieldsPanel = new JPanel(new GridBagLayout());
		fieldsPanel.setBackground(RetroComponents.COLOR_BG);
		headerTitledBorder = RetroComponents.createTitledGroupBorder(I18n.get("email.group.header"));
		fieldsPanel.setBorder(headerTitledBorder);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 8, 4, 8);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		lblFrom = RetroComponents.createLabel(I18n.get("email.field.from"));
		fieldsPanel.add(lblFrom, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtFrom = RetroComponents.createTextField(30);
		txtFrom.setText("noreply@stationery-system.com");
		fieldsPanel.add(txtFrom, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		lblTo = RetroComponents.createLabel(I18n.get("email.field.to"));
		fieldsPanel.add(lblTo, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtTo = RetroComponents.createTextField(30);
		fieldsPanel.add(txtTo, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		lblSubject = RetroComponents.createLabel(I18n.get("email.field.subject"));
		fieldsPanel.add(lblSubject, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtSubject = RetroComponents.createTextField(30);
		fieldsPanel.add(txtSubject, gbc);

		add(fieldsPanel, BorderLayout.NORTH);

		bodyPanel = new JPanel(new BorderLayout(4, 4));
		bodyPanel.setBackground(RetroComponents.COLOR_BG);
		bodyTitledBorder = RetroComponents.createTitledGroupBorder(I18n.get("email.group.body"));
		bodyPanel.setBorder(bodyTitledBorder);

		txtBody = new JTextArea();
		txtBody.setFont(RetroComponents.FONT_MONO);
		txtBody.setBackground(RetroComponents.COLOR_WHITE);
		txtBody.setForeground(RetroComponents.COLOR_TEXT);
		txtBody.setLineWrap(true);
		txtBody.setWrapStyleWord(true);
		txtBody.setBorder(RetroComponents.createFieldBorder());

		JScrollPane scrollPane = new JScrollPane(txtBody);
		scrollPane.setBorder(RetroComponents.createLoweredBevel());
		bodyPanel.add(scrollPane, BorderLayout.CENTER);

		add(bodyPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
		buttonPanel.setBackground(RetroComponents.COLOR_BG);
		buttonPanel.setBorder(RetroComponents.createLoweredBevel());

		btnSend = RetroComponents.createButton(I18n.get("btn.send"));
		btnClear = RetroComponents.createButton(I18n.get("btn.clear"));

		btnSend.addActionListener(e -> sendEmail());
		btnClear.addActionListener(e -> clearForm());

		buttonPanel.add(btnClear);
		buttonPanel.add(btnSend);

		add(buttonPanel, BorderLayout.SOUTH);
	}

	public void updateLanguage() {
		btnSend.setText(I18n.get("btn.send"));
		btnClear.setText(I18n.get("btn.clear"));

		lblFrom.setText(I18n.get("email.field.from"));
		lblTo.setText(I18n.get("email.field.to"));
		lblSubject.setText(I18n.get("email.field.subject"));

		headerTitledBorder.setTitle(" " + I18n.get("email.group.header") + " ");
		bodyTitledBorder.setTitle(" " + I18n.get("email.group.body") + " ");

		fieldsPanel.repaint();
		bodyPanel.repaint();
		revalidate();
		repaint();
	}

	private void clearForm() {
		txtTo.setText("");
		txtSubject.setText("");
		txtBody.setText("");
		txtTo.requestFocus();
		statusUpdater.accept(I18n.get("email.msg.cleared"));
	}

	private void sendEmail() {
		String from = txtFrom.getText().trim();
		String to = txtTo.getText().trim();
		String subject = txtSubject.getText().trim();
		String body = txtBody.getText().trim();

		if (from.isEmpty() || !from.contains("@")) {
			JOptionPane.showMessageDialog(this, I18n.get("email.msg.val_from"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtFrom.requestFocus();
			return;
		}

		if (to.isEmpty() || !to.contains("@")) {
			JOptionPane.showMessageDialog(this, I18n.get("email.msg.val_to"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtTo.requestFocus();
			return;
		}

		if (subject.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("email.msg.val_subject"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtSubject.requestFocus();
			return;
		}

		if (body.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("email.msg.val_body"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtBody.requestFocus();
			return;
		}

		btnSend.setEnabled(false);
		btnClear.setEnabled(false);
		statusUpdater.accept(I18n.get("email.msg.sending", to));

		new Thread(() -> {
			try {
				apiClient.sendEmail(from, to, subject, body);
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(I18n.get("email.msg.sent", to));
					JOptionPane.showMessageDialog(this,
							I18n.get("email.msg.sent", to),
							I18n.get("email.msg.sent_title"),
							JOptionPane.INFORMATION_MESSAGE);
					clearForm();
					btnSend.setEnabled(true);
					btnClear.setEnabled(true);
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
					RetroComponents.showErrorDialog(this, ex);
					btnSend.setEnabled(true);
					btnClear.setEnabled(true);
				});
			}
		}).start();
	}
}
