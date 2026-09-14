package com.projetofinal.backend.client.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.projetofinal.backend.client.ApiClient;
import com.projetofinal.backend.client.i18n.I18n;
import com.projetofinal.backend.entities.Employee;

public class EmployeePanel extends JPanel {

	private final ApiClient apiClient;
	private final Consumer<String> statusUpdater;

	private JTable table;
	private DefaultTableModel tableModel;
	private List<Employee> employeeList = new ArrayList<>();

	private TitledBorder formTitledBorder;
	private JPanel formPanel;

	private JLabel lblId;
	private JLabel lblName;
	private JLabel lblEmail;
	private JLabel lblPassword;
	private JLabel lblDepartment;

	private JTextField txtId;
	private JTextField txtName;
	private JTextField txtEmail;
	private JPasswordField txtPassword;
	private JTextField txtDepartment;

	private JButton btnNew;
	private JButton btnSave;
	private JButton btnDelete;
	private JButton btnRefresh;

	public EmployeePanel(ApiClient apiClient, Consumer<String> statusUpdater) {
		this.apiClient = apiClient;
		this.statusUpdater = statusUpdater != null ? statusUpdater : s -> {};

		setLayout(new BorderLayout(4, 4));
		setBackground(RetroComponents.COLOR_BG);
		setBorder(RetroComponents.createRaisedBevel());

		initComponents();
		updateLanguage();

		I18n.addListener(this::updateLanguage);
	}

	private void initComponents() {
		JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		toolBar.setBackground(RetroComponents.COLOR_BG);
		toolBar.setBorder(RetroComponents.createLoweredBevel());

		btnNew = RetroComponents.createButton(I18n.get("btn.new"));
		btnSave = RetroComponents.createButton(I18n.get("btn.save"));
		btnDelete = RetroComponents.createButton(I18n.get("btn.delete"));
		btnRefresh = RetroComponents.createButton(I18n.get("btn.refresh"));

		btnNew.addActionListener(e -> clearForm());
		btnSave.addActionListener(e -> saveEmployee());
		btnDelete.addActionListener(e -> deleteEmployee());
		btnRefresh.addActionListener(e -> loadEmployees());

		toolBar.add(btnNew);
		toolBar.add(btnSave);
		toolBar.add(btnDelete);
		toolBar.add(btnRefresh);

		add(toolBar, BorderLayout.NORTH);

		tableModel = new DefaultTableModel(getColumnIdentifiers(), 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		RetroComponents.styleTable(table);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int selectedRow = table.getSelectedRow();
				if (selectedRow >= 0 && selectedRow < employeeList.size()) {
					populateForm(employeeList.get(selectedRow));
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(RetroComponents.createLoweredBevel());
		scrollPane.getViewport().setBackground(RetroComponents.COLOR_WHITE);

		formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(RetroComponents.COLOR_BG);
		formTitledBorder = RetroComponents.createTitledGroupBorder(I18n.get("employee.group.title"));
		formPanel.setBorder(formTitledBorder);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 6, 4, 6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		lblId = RetroComponents.createLabel(I18n.get("employee.field.id"));
		formPanel.add(lblId, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtId = RetroComponents.createTextField(8);
		txtId.setEditable(false);
		txtId.setBackground(RetroComponents.COLOR_LIGHT_GRAY);
		formPanel.add(txtId, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		lblName = RetroComponents.createLabel(I18n.get("employee.field.name"));
		formPanel.add(lblName, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtName = RetroComponents.createTextField(25);
		formPanel.add(txtName, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		lblEmail = RetroComponents.createLabel(I18n.get("employee.field.email"));
		formPanel.add(lblEmail, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtEmail = RetroComponents.createTextField(25);
		formPanel.add(txtEmail, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		lblPassword = RetroComponents.createLabel(I18n.get("employee.field.password"));
		formPanel.add(lblPassword, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtPassword = RetroComponents.createPasswordField(20);
		formPanel.add(txtPassword, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		lblDepartment = RetroComponents.createLabel(I18n.get("employee.field.department"));
		formPanel.add(lblDepartment, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtDepartment = RetroComponents.createTextField(20);
		formPanel.add(txtDepartment, gbc);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formPanel);
		splitPane.setResizeWeight(0.65);
		splitPane.setDividerSize(6);
		splitPane.setBackground(RetroComponents.COLOR_BG);
		splitPane.setBorder(null);

		add(splitPane, BorderLayout.CENTER);
	}

	private Object[] getColumnIdentifiers() {
		return new Object[]{
				I18n.get("employee.col.id"),
				I18n.get("employee.col.name"),
				I18n.get("employee.col.email"),
				I18n.get("employee.col.department")
		};
	}

	public void updateLanguage() {
		btnNew.setText(I18n.get("btn.new"));
		btnSave.setText(I18n.get("btn.save"));
		btnDelete.setText(I18n.get("btn.delete"));
		btnRefresh.setText(I18n.get("btn.refresh"));

		lblId.setText(I18n.get("employee.field.id"));
		lblName.setText(I18n.get("employee.field.name"));
		lblEmail.setText(I18n.get("employee.field.email"));
		lblPassword.setText(I18n.get("employee.field.password"));
		lblDepartment.setText(I18n.get("employee.field.department"));

		formTitledBorder.setTitle(" " + I18n.get("employee.group.title") + " ");
		tableModel.setColumnIdentifiers(getColumnIdentifiers());

		formPanel.repaint();
		revalidate();
		repaint();
	}

	public void loadEmployees() {
		statusUpdater.accept(I18n.get("employee.msg.loading"));
		setButtonsEnabled(false);

		new Thread(() -> {
			try {
				List<Employee> employees = apiClient.getEmployees();
				SwingUtilities.invokeLater(() -> {
					employeeList = employees;
					tableModel.setRowCount(0);
					for (Employee emp : employees) {
						tableModel.addRow(new Object[]{
								emp.getId(),
								emp.getName(),
								emp.getEmail(),
								emp.getDepartment() != null ? emp.getDepartment() : ""
						});
					}
					statusUpdater.accept(I18n.get("employee.msg.loaded", employees.size()));
					setButtonsEnabled(true);
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
					RetroComponents.showErrorDialog(this, ex);
					setButtonsEnabled(true);
				});
			}
		}).start();
	}

	private void populateForm(Employee emp) {
		txtId.setText(emp.getId() != null ? emp.getId().toString() : "");
		txtName.setText(emp.getName() != null ? emp.getName() : "");
		txtEmail.setText(emp.getEmail() != null ? emp.getEmail() : "");
		txtPassword.setText("");
		txtDepartment.setText(emp.getDepartment() != null ? emp.getDepartment() : "");
	}

	private void clearForm() {
		table.clearSelection();
		txtId.setText("");
		txtName.setText("");
		txtEmail.setText("");
		txtPassword.setText("");
		txtDepartment.setText("");
		txtName.requestFocus();
		statusUpdater.accept(I18n.get("employee.msg.ready_new"));
	}

	private void saveEmployee() {
		String name = txtName.getText().trim();
		String email = txtEmail.getText().trim();
		String password = new String(txtPassword.getPassword()).trim();
		String department = txtDepartment.getText().trim();

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("employee.msg.val_name"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtName.requestFocus();
			return;
		}

		if (email.isEmpty() || !email.contains("@")) {
			JOptionPane.showMessageDialog(this, I18n.get("employee.msg.val_email"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtEmail.requestFocus();
			return;
		}

		String idText = txtId.getText().trim();
		boolean isUpdate = !idText.isEmpty();

		if (!isUpdate && password.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("employee.msg.val_password"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtPassword.requestFocus();
			return;
		}

		Employee employee = new Employee();
		employee.setName(name);
		employee.setEmail(email);
		if (!password.isEmpty()) {
			employee.setPassword(password);
		}
		employee.setDepartment(department);

		statusUpdater.accept(I18n.get("employee.msg.saving"));
		setButtonsEnabled(false);

		new Thread(() -> {
			try {
				if (isUpdate) {
					Integer id = Integer.parseInt(idText);
					apiClient.updateEmployee(id, employee);
				} else {
					apiClient.createEmployee(employee);
				}

				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(I18n.get("employee.msg.saved"));
					loadEmployees();
					clearForm();
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
					RetroComponents.showErrorDialog(this, ex);
					setButtonsEnabled(true);
				});
			}
		}).start();
	}

	private void deleteEmployee() {
		String idText = txtId.getText().trim();
		if (idText.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("employee.msg.select_delete"), I18n.get("dialog.validation_title"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(
				this,
				I18n.get("employee.msg.confirm_delete", idText, txtName.getText()),
				I18n.get("dialog.confirm_title"),
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE
		);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		setButtonsEnabled(false);

		new Thread(() -> {
			try {
				Integer id = Integer.parseInt(idText);
				apiClient.deleteEmployee(id);
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(I18n.get("employee.msg.deleted", id));
					loadEmployees();
					clearForm();
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
					RetroComponents.showErrorDialog(this, ex);
					setButtonsEnabled(true);
				});
			}
		}).start();
	}

	private void setButtonsEnabled(boolean enabled) {
		btnNew.setEnabled(enabled);
		btnSave.setEnabled(enabled);
		btnDelete.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
	}
}
