package com.projetofinal.backend.client.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

import com.projetofinal.backend.client.ApiClient;
import com.projetofinal.backend.client.i18n.I18n;
import com.projetofinal.backend.dtos.SaleItemRequestDTO;
import com.projetofinal.backend.dtos.SaleRequestDTO;
import com.projetofinal.backend.entities.Product;
import com.projetofinal.backend.entities.Sale;
import com.projetofinal.backend.enums.PaymentMethod;

public class POSPanel extends JPanel {

	private final ApiClient apiClient;
	private final Consumer<String> statusUpdater;

	private List<Product> catalog = new ArrayList<>();
	private final List<CartItem> cartItems = new ArrayList<>();

	private JComboBox<ProductItemWrapper> cmbProducts;
	private JTextField txtQuantity;
	private JLabel lblAvailableStock;
	private JLabel lblUnitPrice;
	private JButton btnAddItem;

	private JTable cartTable;
	private DefaultTableModel cartTableModel;
	private JButton btnRemoveItem;

	private JLabel lblTotalDisplay;
	private JTextField txtDiscount;
	private JComboBox<PaymentMethodWrapper> cmbPaymentMethod;
	private JTextField txtAmountPaid;
	private JLabel lblChangeDisplay;
	private JButton btnCheckout;
	private JButton btnCancel;

	private TitledBorder selectBorder;
	private TitledBorder itemsBorder;
	private TitledBorder checkoutBorder;
	private JPanel selectPanel;
	private JPanel cartContainerPanel;
	private JPanel checkoutPanel;

	private JLabel lblProductPrompt;
	private JLabel lblQtyPrompt;
	private JLabel lblDiscountPrompt;
	private JLabel lblPaymentPrompt;
	private JLabel lblPaidPrompt;

	public POSPanel(ApiClient apiClient, Consumer<String> statusUpdater) {
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
		// Top Product Selection Panel
		selectPanel = new JPanel(new GridBagLayout());
		selectPanel.setBackground(RetroComponents.COLOR_BG);
		selectBorder = RetroComponents.createTitledGroupBorder(I18n.get("pos.group.select"));
		selectPanel.setBorder(selectBorder);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 6, 4, 6);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		lblProductPrompt = RetroComponents.createLabel(I18n.get("pos.field.product"));
		selectPanel.add(lblProductPrompt, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		cmbProducts = new JComboBox<>();
		cmbProducts.setFont(RetroComponents.FONT_PLAIN);
		cmbProducts.setBackground(RetroComponents.COLOR_WHITE);
		cmbProducts.addActionListener(e -> onProductSelected());
		selectPanel.add(cmbProducts, gbc);

		gbc.gridx = 2;
		gbc.weightx = 0;
		lblQtyPrompt = RetroComponents.createLabel(I18n.get("pos.field.quantity"));
		selectPanel.add(lblQtyPrompt, gbc);

		gbc.gridx = 3;
		txtQuantity = RetroComponents.createTextField(6);
		txtQuantity.setText("1");
		selectPanel.add(txtQuantity, gbc);

		gbc.gridx = 4;
		lblUnitPrice = RetroComponents.createBoldLabel("R$ 0,00");
		selectPanel.add(lblUnitPrice, gbc);

		gbc.gridx = 5;
		lblAvailableStock = RetroComponents.createLabel(I18n.get("pos.field.stock", 0));
		selectPanel.add(lblAvailableStock, gbc);

		gbc.gridx = 6;
		btnAddItem = RetroComponents.createButton(I18n.get("pos.btn.add"));
		btnAddItem.addActionListener(e -> addItemToCart());
		selectPanel.add(btnAddItem, gbc);

		add(selectPanel, BorderLayout.NORTH);

		// Center Cart Table
		cartContainerPanel = new JPanel(new BorderLayout(4, 4));
		cartContainerPanel.setBackground(RetroComponents.COLOR_BG);
		itemsBorder = RetroComponents.createTitledGroupBorder(I18n.get("pos.group.items"));
		cartContainerPanel.setBorder(itemsBorder);

		cartTableModel = new DefaultTableModel(getColumnIdentifiers(), 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		cartTable = new JTable(cartTableModel);
		RetroComponents.styleTable(cartTable);
		cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		JScrollPane scrollPane = new JScrollPane(cartTable);
		scrollPane.setBorder(RetroComponents.createLoweredBevel());
		scrollPane.getViewport().setBackground(RetroComponents.COLOR_WHITE);
		cartContainerPanel.add(scrollPane, BorderLayout.CENTER);

		JPanel cartToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 4));
		cartToolbar.setBackground(RetroComponents.COLOR_BG);
		btnRemoveItem = RetroComponents.createButton(I18n.get("pos.btn.remove"));
		btnRemoveItem.addActionListener(e -> removeSelectedItem());
		cartToolbar.add(btnRemoveItem);
		cartContainerPanel.add(cartToolbar, BorderLayout.SOUTH);

		// Right Checkout Panel
		checkoutPanel = new JPanel(new GridBagLayout());
		checkoutPanel.setBackground(RetroComponents.COLOR_BG);
		checkoutBorder = RetroComponents.createTitledGroupBorder(I18n.get("pos.group.checkout"));
		checkoutPanel.setBorder(checkoutBorder);
		checkoutPanel.setPreferredSize(new Dimension(340, 0));

		GridBagConstraints cgbc = new GridBagConstraints();
		cgbc.insets = new Insets(6, 8, 6, 8);
		cgbc.fill = GridBagConstraints.HORIZONTAL;
		cgbc.anchor = GridBagConstraints.WEST;

		// Digital Total Display
		cgbc.gridx = 0;
		cgbc.gridy = 0;
		cgbc.gridwidth = 2;
		lblTotalDisplay = new JLabel("TOTAL: R$ 0,00", JLabel.CENTER);
		lblTotalDisplay.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTotalDisplay.setOpaque(true);
		lblTotalDisplay.setBackground(new Color(0, 40, 0));
		lblTotalDisplay.setForeground(new Color(0, 255, 64));
		lblTotalDisplay.setBorder(RetroComponents.createLoweredBevel());
		lblTotalDisplay.setPreferredSize(new Dimension(280, 50));
		checkoutPanel.add(lblTotalDisplay, cgbc);

		cgbc.gridwidth = 1;

		// Discount
		cgbc.gridx = 0;
		cgbc.gridy = 1;
		lblDiscountPrompt = RetroComponents.createLabel(I18n.get("pos.field.discount"));
		checkoutPanel.add(lblDiscountPrompt, cgbc);

		cgbc.gridx = 1;
		txtDiscount = RetroComponents.createTextField(10);
		txtDiscount.setText("0.00");
		txtDiscount.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateTotals();
			}
		});
		checkoutPanel.add(txtDiscount, cgbc);

		// Payment Method
		cgbc.gridx = 0;
		cgbc.gridy = 2;
		lblPaymentPrompt = RetroComponents.createLabel(I18n.get("pos.field.payment_method"));
		checkoutPanel.add(lblPaymentPrompt, cgbc);

		cgbc.gridx = 1;
		cmbPaymentMethod = new JComboBox<>(getPaymentMethodWrappers());
		cmbPaymentMethod.setFont(RetroComponents.FONT_PLAIN);
		cmbPaymentMethod.setBackground(RetroComponents.COLOR_WHITE);
		checkoutPanel.add(cmbPaymentMethod, cgbc);

		// Amount Paid
		cgbc.gridx = 0;
		cgbc.gridy = 3;
		lblPaidPrompt = RetroComponents.createLabel(I18n.get("pos.field.amount_paid"));
		checkoutPanel.add(lblPaidPrompt, cgbc);

		cgbc.gridx = 1;
		txtAmountPaid = RetroComponents.createTextField(10);
		txtAmountPaid.setText("0.00");
		txtAmountPaid.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				updateTotals();
			}
		});
		checkoutPanel.add(txtAmountPaid, cgbc);

		// Change
		cgbc.gridx = 0;
		cgbc.gridy = 4;
		cgbc.gridwidth = 2;
		lblChangeDisplay = new JLabel("TROCO: R$ 0,00", JLabel.CENTER);
		lblChangeDisplay.setFont(RetroComponents.FONT_BOLD);
		lblChangeDisplay.setOpaque(true);
		lblChangeDisplay.setBackground(RetroComponents.COLOR_LIGHT_GRAY);
		lblChangeDisplay.setBorder(RetroComponents.createLoweredBevel());
		lblChangeDisplay.setPreferredSize(new Dimension(280, 26));
		checkoutPanel.add(lblChangeDisplay, cgbc);

		// Action Buttons
		cgbc.gridx = 0;
		cgbc.gridy = 5;
		cgbc.gridwidth = 2;
		JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
		actionPanel.setBackground(RetroComponents.COLOR_BG);

		btnCheckout = RetroComponents.createButton(I18n.get("pos.btn.checkout"));
		btnCancel = RetroComponents.createButton(I18n.get("pos.btn.cancel"));

		btnCheckout.addActionListener(e -> executeCheckout());
		btnCancel.addActionListener(e -> cancelSale());

		actionPanel.add(btnCancel);
		actionPanel.add(btnCheckout);
		checkoutPanel.add(actionPanel, cgbc);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, cartContainerPanel, checkoutPanel);
		splitPane.setResizeWeight(0.68);
		splitPane.setDividerSize(6);
		splitPane.setBackground(RetroComponents.COLOR_BG);
		splitPane.setBorder(null);

		add(splitPane, BorderLayout.CENTER);
	}

	private Object[] getColumnIdentifiers() {
		return new Object[]{
				I18n.get("pos.col.item"),
				I18n.get("pos.col.code"),
				I18n.get("pos.col.desc"),
				I18n.get("pos.col.qty"),
				I18n.get("pos.col.unit_price"),
				I18n.get("pos.col.subtotal")
		};
	}

	private PaymentMethodWrapper[] getPaymentMethodWrappers() {
		return new PaymentMethodWrapper[]{
				new PaymentMethodWrapper(PaymentMethod.DINHEIRO, I18n.get("pos.pay.cash")),
				new PaymentMethodWrapper(PaymentMethod.CARTAO_DEBITO, I18n.get("pos.pay.debit")),
				new PaymentMethodWrapper(PaymentMethod.CARTAO_CREDITO, I18n.get("pos.pay.credit")),
				new PaymentMethodWrapper(PaymentMethod.PIX, I18n.get("pos.pay.pix")),
				new PaymentMethodWrapper(PaymentMethod.FATURADO, I18n.get("pos.pay.invoice"))
		};
	}

	public void updateLanguage() {
		selectBorder.setTitle(" " + I18n.get("pos.group.select") + " ");
		itemsBorder.setTitle(" " + I18n.get("pos.group.items") + " ");
		checkoutBorder.setTitle(" " + I18n.get("pos.group.checkout") + " ");

		lblProductPrompt.setText(I18n.get("pos.field.product"));
		lblQtyPrompt.setText(I18n.get("pos.field.quantity"));
		lblDiscountPrompt.setText(I18n.get("pos.field.discount"));
		lblPaymentPrompt.setText(I18n.get("pos.field.payment_method"));
		lblPaidPrompt.setText(I18n.get("pos.field.amount_paid"));

		btnAddItem.setText(I18n.get("pos.btn.add"));
		btnRemoveItem.setText(I18n.get("pos.btn.remove"));
		btnCheckout.setText(I18n.get("pos.btn.checkout"));
		btnCancel.setText(I18n.get("pos.btn.cancel"));

		cartTableModel.setColumnIdentifiers(getColumnIdentifiers());

		PaymentMethod currentPm = null;
		if (cmbPaymentMethod.getSelectedItem() instanceof PaymentMethodWrapper) {
			currentPm = ((PaymentMethodWrapper) cmbPaymentMethod.getSelectedItem()).getMethod();
		}
		cmbPaymentMethod.setModel(new DefaultComboBoxModel<>(getPaymentMethodWrappers()));
		if (currentPm != null) {
			for (int i = 0; i < cmbPaymentMethod.getItemCount(); i++) {
				if (cmbPaymentMethod.getItemAt(i).getMethod() == currentPm) {
					cmbPaymentMethod.setSelectedIndex(i);
					break;
				}
			}
		}

		onProductSelected();
		updateTotals();

		revalidate();
		repaint();
	}

	public void refreshCatalog() {
		new Thread(() -> {
			try {
				List<Product> products = apiClient.getProducts();
				SwingUtilities.invokeLater(() -> {
					catalog = products;
					DefaultComboBoxModel<ProductItemWrapper> model = new DefaultComboBoxModel<>();
					for (Product p : products) {
						model.addElement(new ProductItemWrapper(p));
					}
					cmbProducts.setModel(model);
					onProductSelected();
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
				});
			}
		}).start();
	}

	private void onProductSelected() {
		ProductItemWrapper selected = (ProductItemWrapper) cmbProducts.getSelectedItem();
		if (selected != null && selected.getProduct() != null) {
			Product p = selected.getProduct();
			lblUnitPrice.setText(String.format("R$ %.2f", p.getPrice() != null ? p.getPrice() : 0.0f));
			int stock = p.getQuantity() != null ? p.getQuantity() : 0;
			lblAvailableStock.setText(I18n.get("pos.field.stock", stock));
		} else {
			lblUnitPrice.setText("R$ 0,00");
			lblAvailableStock.setText(I18n.get("pos.field.stock", 0));
		}
	}

	private void addItemToCart() {
		ProductItemWrapper selected = (ProductItemWrapper) cmbProducts.getSelectedItem();
		if (selected == null || selected.getProduct() == null) {
			JOptionPane.showMessageDialog(this, I18n.get("pos.msg.val_product"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		Product product = selected.getProduct();
		int quantity;
		try {
			quantity = Integer.parseInt(txtQuantity.getText().trim());
			if (quantity <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, I18n.get("pos.msg.val_qty"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtQuantity.requestFocus();
			return;
		}

		int currentInCart = 0;
		for (CartItem item : cartItems) {
			if (item.getProduct().getId().equals(product.getId())) {
				currentInCart += item.getQuantity();
			}
		}

		int available = product.getQuantity() != null ? product.getQuantity() : 0;
		if (currentInCart + quantity > available) {
			JOptionPane.showMessageDialog(this,
					I18n.get("pos.msg.insufficient_stock", product.getName(), available),
					I18n.get("dialog.validation_title"),
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		boolean existing = false;
		for (CartItem item : cartItems) {
			if (item.getProduct().getId().equals(product.getId())) {
				item.setQuantity(item.getQuantity() + quantity);
				existing = true;
				break;
			}
		}

		if (!existing) {
			cartItems.add(new CartItem(product, quantity));
		}

		rebuildCartTable();
		txtQuantity.setText("1");
		updateTotals();
	}

	private void removeSelectedItem() {
		int selectedRow = cartTable.getSelectedRow();
		if (selectedRow >= 0 && selectedRow < cartItems.size()) {
			cartItems.remove(selectedRow);
			rebuildCartTable();
			updateTotals();
		}
	}

	private void rebuildCartTable() {
		cartTableModel.setRowCount(0);
		int itemIndex = 1;
		for (CartItem item : cartItems) {
			float price = item.getProduct().getPrice() != null ? item.getProduct().getPrice() : 0.0f;
			float subtotal = price * item.getQuantity();
			cartTableModel.addRow(new Object[]{
					itemIndex++,
					item.getProduct().getId(),
					item.getProduct().getName(),
					item.getQuantity(),
					String.format("%.2f", price),
					String.format("%.2f", subtotal)
			});
		}
	}

	private void updateTotals() {
		float grossTotal = 0.0f;
		for (CartItem item : cartItems) {
			float price = item.getProduct().getPrice() != null ? item.getProduct().getPrice() : 0.0f;
			grossTotal += price * item.getQuantity();
		}

		float discount = 0.0f;
		try {
			discount = Float.parseFloat(txtDiscount.getText().trim().replace(",", "."));
		} catch (NumberFormatException ignored) {}

		float finalTotal = Math.max(0.0f, grossTotal - discount);
		lblTotalDisplay.setText(I18n.get("pos.field.total", String.format("%.2f", finalTotal)));

		float paid = finalTotal;
		try {
			paid = Float.parseFloat(txtAmountPaid.getText().trim().replace(",", "."));
		} catch (NumberFormatException ignored) {}

		float change = Math.max(0.0f, paid - finalTotal);
		lblChangeDisplay.setText(I18n.get("pos.field.change") + " " + String.format("%.2f", change));
	}

	private void executeCheckout() {
		if (cartItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, I18n.get("pos.msg.empty_cart"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

		float grossTotal = 0.0f;
		for (CartItem item : cartItems) {
			float price = item.getProduct().getPrice() != null ? item.getProduct().getPrice() : 0.0f;
			grossTotal += price * item.getQuantity();
		}

		float discount = 0.0f;
		try {
			discount = Float.parseFloat(txtDiscount.getText().trim().replace(",", "."));
		} catch (NumberFormatException e) {
			discount = 0.0f;
		}

		float finalTotal = Math.max(0.0f, grossTotal - discount);

		float amountPaid = finalTotal;
		try {
			amountPaid = Float.parseFloat(txtAmountPaid.getText().trim().replace(",", "."));
		} catch (NumberFormatException e) {
			amountPaid = finalTotal;
		}

		PaymentMethodWrapper pmw = (PaymentMethodWrapper) cmbPaymentMethod.getSelectedItem();
		PaymentMethod pm = pmw != null ? pmw.getMethod() : PaymentMethod.DINHEIRO;

		if (pm == PaymentMethod.DINHEIRO && amountPaid < finalTotal) {
			JOptionPane.showMessageDialog(this, I18n.get("pos.msg.val_paid"), I18n.get("dialog.validation_title"), JOptionPane.WARNING_MESSAGE);
			txtAmountPaid.requestFocus();
			return;
		}

		SaleRequestDTO request = new SaleRequestDTO();
		request.setPaymentMethod(pm);
		request.setDiscountAmount(discount);
		request.setAmountPaid(amountPaid);

		List<SaleItemRequestDTO> items = new ArrayList<>();
		for (CartItem ci : cartItems) {
			items.add(new SaleItemRequestDTO(ci.getProduct().getId(), ci.getQuantity()));
		}
		request.setItems(items);

		btnCheckout.setEnabled(false);
		btnCancel.setEnabled(false);
		statusUpdater.accept("Processing sale transaction...");

		final float finalAmountPaid = amountPaid;
		final float finalDiscount = discount;
		final float finalGrossTotal = grossTotal;
		final List<CartItem> completedItems = new ArrayList<>(cartItems);

		new Thread(() -> {
			try {
				Sale createdSale = apiClient.createSale(request);
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept("Sale #" + createdSale.getId() + " finalized successfully.");
					showReceiptDialog(createdSale, completedItems, finalGrossTotal, finalDiscount, finalAmountPaid);
					cartItems.clear();
					rebuildCartTable();
					txtDiscount.setText("0.00");
					txtAmountPaid.setText("0.00");
					updateTotals();
					refreshCatalog();
					btnCheckout.setEnabled(true);
					btnCancel.setEnabled(true);
				});
			} catch (Exception ex) {
				SwingUtilities.invokeLater(() -> {
					statusUpdater.accept(RetroComponents.formatErrorMessage(ex));
					RetroComponents.showErrorDialog(this, ex);
					btnCheckout.setEnabled(true);
					btnCancel.setEnabled(true);
				});
			}
		}).start();
	}

	private void cancelSale() {
		if (cartItems.isEmpty()) {
			return;
		}
		int confirm = JOptionPane.showConfirmDialog(this,
				I18n.get("pos.msg.confirm_cancel"),
				I18n.get("dialog.confirm_title"),
				JOptionPane.YES_NO_OPTION);
		if (confirm == JOptionPane.YES_OPTION) {
			cartItems.clear();
			rebuildCartTable();
			txtDiscount.setText("0.00");
			txtAmountPaid.setText("0.00");
			updateTotals();
			statusUpdater.accept("Sale cancelled.");
		}
	}

	private void showReceiptDialog(Sale sale, List<CartItem> items, float gross, float discount, float paid) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
		StringBuilder sb = new StringBuilder();
		sb.append(I18n.get("pos.receipt.header")).append("\n");
		sb.append(String.format("VENDA: #%06d           DATA: %s\n", sale.getId(), LocalDateTime.now().format(dtf)));
		sb.append("------------------------------------------------\n");
		sb.append(String.format("%-4s %-24s %4s %7s %7s\n", "ITEM", "DESCRICAO", "QTD", "UNIT", "TOTAL"));
		sb.append("------------------------------------------------\n");

		int idx = 1;
		for (CartItem ci : items) {
			float p = ci.getProduct().getPrice() != null ? ci.getProduct().getPrice() : 0.0f;
			float sub = p * ci.getQuantity();
			String name = ci.getProduct().getName();
			if (name.length() > 24) {
				name = name.substring(0, 21) + "...";
			}
			sb.append(String.format("%02d   %-24s %4d %7.2f %7.2f\n", idx++, name, ci.getQuantity(), p, sub));
		}

		sb.append("------------------------------------------------\n");
		sb.append(String.format("SUBTOTAL:                               R$ %7.2f\n", gross));
		if (discount > 0) {
			sb.append(String.format("DESCONTO:                               R$ %7.2f\n", discount));
		}
		sb.append(String.format("TOTAL DA VENDA:                         R$ %7.2f\n", sale.getTotalAmount()));
		sb.append(String.format("PAGAMENTO (%s):               R$ %7.2f\n", sale.getPaymentMethod(), paid));
		sb.append(String.format("TROCO:                                  R$ %7.2f\n", sale.getChangeAmount() != null ? sale.getChangeAmount() : 0.0f));
		sb.append(I18n.get("pos.receipt.footer")).append("\n");

		JTextArea receiptArea = new JTextArea(sb.toString());
		receiptArea.setFont(RetroComponents.FONT_MONO);
		receiptArea.setEditable(false);
		receiptArea.setBackground(RetroComponents.COLOR_WHITE);
		receiptArea.setBorder(RetroComponents.createFieldBorder());

		JScrollPane sp = new JScrollPane(receiptArea);
		sp.setPreferredSize(new Dimension(460, 420));
		sp.setBorder(RetroComponents.createLoweredBevel());

		JOptionPane.showMessageDialog(this, sp, I18n.get("pos.msg.receipt_title"), JOptionPane.INFORMATION_MESSAGE);
	}

	private static class CartItem {
		private final Product product;
		private int quantity;

		public CartItem(Product product, int quantity) {
			this.product = product;
			this.quantity = quantity;
		}

		public Product getProduct() {
			return product;
		}

		public int getQuantity() {
			return quantity;
		}

		public void setQuantity(int quantity) {
			this.quantity = quantity;
		}
	}

	private static class ProductItemWrapper {
		private final Product product;

		public ProductItemWrapper(Product product) {
			this.product = product;
		}

		public Product getProduct() {
			return product;
		}

		@Override
		public String toString() {
			if (product == null) {
				return "";
			}
			return String.format("[%d] %s (R$ %.2f)",
					product.getId(),
					product.getName(),
					product.getPrice() != null ? product.getPrice() : 0.0f);
		}
	}

	private static class PaymentMethodWrapper {
		private final PaymentMethod method;
		private final String label;

		public PaymentMethodWrapper(PaymentMethod method, String label) {
			this.method = method;
			this.label = label;
		}

		public PaymentMethod getMethod() {
			return method;
		}

		@Override
		public String toString() {
			return label;
		}
	}
}
