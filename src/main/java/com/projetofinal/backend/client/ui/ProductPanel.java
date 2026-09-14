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
import com.projetofinal.backend.entities.Product;

public class ProductPanel extends JPanel {

	private final ApiClient apiClient;
	private final Consumer<String> statusUpdater;

	private JTable table;
	private DefaultTableModel tableModel;
	private List<Product> productList = new ArrayList<>();

	private TitledBorder formTitledBorder;
	private JPanel formPanel;

	private JLabel lblId;
	private JLabel lblName;
	private JLabel lblCategory;
	private JLabel lblQuantity;
	private JLabel lblPrice;

	private JTextField txtId;
	private JTextField txtName;
	private JTextField txtCategory;
	private JTextField txtQuantity;
	private JTextField txtPrice;

	private JButton btnNew;
	private JButton btnSave;
	private JButton btnDelete;
	private JButton btnRefresh;

	public ProductPanel(ApiClient apiClient, Consumer<String> statusUpdater) {
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
		btnSave.addActionListener(e -> saveProduct());
		btnDelete.addActionListener(e -> deleteProduct());
		btnRefresh.addActionListener(e -> loadProducts());

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
				if (selectedRow >= 0 && selectedRow < productList.size()) {
					populateForm(productList.get(selectedRow));
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(table);
		scrollPane.setBorder(RetroComponents.createLoweredBevel());
		scrollPane.getViewport().setBackground(RetroComponents.COLOR_WHITE);

		formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(RetroComponents.COLOR_BG);
		formTitledBorder = RetroComponents.createTitledGroupBorder(I18n.get("product.group.title"));
		formPanel.setBorder(formTitledBorder);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 6, 4, 6);
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		lblId = RetroComponents.createLabel(I18n.get("product.field.id"));
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
		lblName = RetroComponents.createLabel(I18n.get("product.field.name"));
		formPanel.add(lblName, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtName = RetroComponents.createTextField(25);
		formPanel.add(txtName, gbc);

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		lblCategory = RetroComponents.createLabel(I18n.get("product.field.category"));
		formPanel.add(lblCategory, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtCategory = RetroComponents.createTextField(25);
		formPanel.add(txtCategory, gbc);

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		lblQuantity = RetroComponents.createLabel(I18n.get("product.field.quantity"));
		formPanel.add(lblQuantity, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtQuantity = RetroComponents.createTextField(10);
		formPanel.add(txtQuantity, gbc);

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		lblPrice = RetroComponents.createLabel(I18n.get("product.field.price"));
		formPanel.add(lblPrice, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		txtPrice = RetroComponents.createTextField(10);
		formPanel.add(txtPrice, gbc);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, formPanel);
		splitPane.setResizeWeight(0.65);
		splitPane.setDividerSize(6);
		splitPane.setBackground(RetroComponents.COLOR_BG);
		splitPane.setBorder(null);

		add(splitPane, BorderLayout.CENTER);
	}

	private Object[] getColumnIdentifiers() {
		return new Object[]{
				I18n.get("product.col.id"),
				I18n.get("product.col.name"),
				I18n.get("product.col.category"),
				I18n.get("product.col.quantity"),
				I18n.get("product.col.price")
		};
	}

	public void updateLanguage() {
		btnNew.setText(I18n.get("btn.new"));
		btnSave.setText(I18n.get("btn.save"));
		btnDelete.setText(I18n.get("btn.delete"));
		btnRefresh.setText(I18n.get("btn.refresh"));

		lblId.setText(I18n.get("product.field.id"));
		lblName.setText(I18n.get("product.field.name"));
		lblCategory.setText(I18n.get("product.field.category"));
		lblQuantity.setText(I18n.get("product.field.quantity"));
		lblPrice.setText(I18n.get("product.field.price"));

		formTitledBorder.setTitle(" " + I18n.get("product.group.title") + " ");
		tableModel.setColumnIdentifiers(getColumnIdentifiers());

		formPanel.repaint();
		revalidate();
		repaint();
	}

	public void loadProducts() {
		statusUpdater.accept(I18n.get("product.msg.loading"));
		setButtonsEnabled(false);

		new Thread(() -> {
			try {
				List<Product> products = apiClient.getProducts();
				SwingUtilities.invokeLater(() -> {
					productList = products;
					tableModel.setRowCount(0);
					for (Product p : products) {
						tableModel.addRow(new Object[]{
								p.getId(),
								p.getName(),
								p.getCategory(),
								p.getQuantity(),
								p.getPrice() != null ? String.format("%.2f", p.getPrice()) : "0.00"
						});
					}
					statusUpdater.accept(I18n.get("product.msg.loaded", products.size()));
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

	private void populateForm(Product product) {
		txtId.setText(product.getId() != null ? product.getId().toString() : "");
		txtName.setText(product.getName() != null ? product.getName() : "");
		txtCategory.setText(product.getCategory() != null ? product.getCategory() : "");
		txtQuantity.setText(product.getQuantity() != null ? product.getQuantity().toString() : "");
		txtPrice.setText(product.getPrice() != null ? String.format("%.2f", product.getPrice()).replace(",", ".") : "");
	}

	private void clearForm() {
		table.clearSelection();
		txtId.setText("");
		txtName.setText("");
		txtCategory.setText("");
		txtQuantity.setText("");
		txtPrice.setText("");
		txtName.requestFocus();
		statusUpdater.accept(I18n.get("product.msg.ready_new"));
	}

	private void saveProduct() {
		String name = txtName.getText().trim();
		String category = txtCategory.getText().trim();
		String qtyText = txtQuantity.getText().trim();
		String priceText = txtPrice.getText().trim().replace(",", ".");

		if (name.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("product.msg.val_name"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtName.requestFocus();
			return;
		}

		int quantity;
		try {
			quantity = Integer.parseInt(qtyText);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, I18n.get("product.msg.val_qty"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtQuantity.requestFocus();
			return;
		}

		float price;
		try {
			price = Float.parseFloat(priceText);
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(this, I18n.get("product.msg.val_price"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtPrice.requestFocus();
			return;
		}

		Product product = new Product();
		product.setName(name);
		product.setCategory(category);
		product.setQuantity(quantity);
		product.setPrice(price);

		String idText = txtId.getText().trim();
		boolean isUpdate = !idText.isEmpty();

		statusUpdater.accept(I18n.get("product.msg.saving"));
		setButtonsEnabled(false);

		new Thread(() -> {
			try {
				if (isUpdate) {
					Integer id = Integer.parseInt(idText);
					apiClient.updateProduct(id, product);
				} else {
					apiClient.createProduct(product);
				}

				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(I18n.get("product.msg.saved"));
					loadProducts();
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

	private void deleteProduct() {
		String idText = txtId.getText().trim();
		if (idText.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("product.msg.select_delete"), I18n.get("dialog.validation_title"), JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(
				this,
				I18n.get("product.msg.confirm_delete", idText, txtName.getText()),
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
				apiClient.deleteProduct(id);
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(I18n.get("product.msg.deleted", id));
					loadProducts();
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
