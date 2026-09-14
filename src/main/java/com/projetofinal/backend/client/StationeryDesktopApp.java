package com.projetofinal.backend.client;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.UIManager;

import com.projetofinal.backend.client.i18n.I18n;
import com.projetofinal.backend.client.ui.EmailPanel;
import com.projetofinal.backend.client.ui.EmployeePanel;
import com.projetofinal.backend.client.ui.POSPanel;
import com.projetofinal.backend.client.ui.ProductPanel;
import com.projetofinal.backend.client.ui.RetroComponents;

public class StationeryDesktopApp extends JFrame {

	private final ApiClient apiClient;

	private JMenuBar menuBar;
	private JMenu menuFile;
	private JMenuItem itemSettings;
	private JMenuItem itemTestConn;
	private JMenuItem itemExit;

	private JMenu menuModules;
	private JMenuItem itemPOS;
	private JMenuItem itemProducts;
	private JMenuItem itemEmployees;
	private JMenuItem itemEmail;

	private JMenu menuOptions;
	private JMenu menuLanguage;
	private JRadioButtonMenuItem itemLangPt;
	private JRadioButtonMenuItem itemLangEn;

	private JMenu menuHelp;
	private JMenuItem itemAbout;

	private JTabbedPane tabbedPane;
	private POSPanel posPanel;
	private ProductPanel productPanel;
	private EmployeePanel employeePanel;
	private EmailPanel emailPanel;

	private JLabel lblStatusMessage;
	private JLabel lblConnectedUser;
	private JLabel lblClock;

	public StationeryDesktopApp() {
		this.apiClient = new ApiClient();

		initializeFrame();
		initializeMenuBar();
		initializeContent();
		initializeStatusBar();
		initializeTimer();

		updateLanguage();
		I18n.addListener(this::updateLanguage);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				checkInitialConnection();
			}
		});
	}

	private void initializeFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(960, 680);
		setMinimumSize(new Dimension(840, 560));
		setLocationRelativeTo(null);
		getContentPane().setBackground(RetroComponents.COLOR_BG);
		setLayout(new BorderLayout(0, 0));
	}

	private void initializeMenuBar() {
		menuBar = new JMenuBar();
		menuBar.setBackground(RetroComponents.COLOR_BG);
		menuBar.setBorder(RetroComponents.createRaisedBevel());

		menuFile = new JMenu();
		menuFile.setFont(RetroComponents.FONT_PLAIN);

		itemSettings = new JMenuItem();
		itemSettings.setFont(RetroComponents.FONT_PLAIN);
		itemSettings.addActionListener(e -> showServerSettingsDialog());

		itemTestConn = new JMenuItem();
		itemTestConn.setFont(RetroComponents.FONT_PLAIN);
		itemTestConn.addActionListener(e -> testBackendConnection());

		itemExit = new JMenuItem();
		itemExit.setFont(RetroComponents.FONT_PLAIN);
		itemExit.addActionListener(e -> System.exit(0));

		menuFile.add(itemSettings);
		menuFile.add(itemTestConn);
		menuFile.addSeparator();
		menuFile.add(itemExit);

		menuModules = new JMenu();
		menuModules.setFont(RetroComponents.FONT_PLAIN);

		itemPOS = new JMenuItem();
		itemPOS.setFont(RetroComponents.FONT_PLAIN);
		itemPOS.addActionListener(e -> tabbedPane.setSelectedIndex(0));

		itemProducts = new JMenuItem();
		itemProducts.setFont(RetroComponents.FONT_PLAIN);
		itemProducts.addActionListener(e -> tabbedPane.setSelectedIndex(1));

		itemEmployees = new JMenuItem();
		itemEmployees.setFont(RetroComponents.FONT_PLAIN);
		itemEmployees.addActionListener(e -> tabbedPane.setSelectedIndex(2));

		itemEmail = new JMenuItem();
		itemEmail.setFont(RetroComponents.FONT_PLAIN);
		itemEmail.addActionListener(e -> tabbedPane.setSelectedIndex(3));

		menuModules.add(itemPOS);
		menuModules.add(itemProducts);
		menuModules.add(itemEmployees);
		menuModules.add(itemEmail);

		menuOptions = new JMenu();
		menuOptions.setFont(RetroComponents.FONT_PLAIN);

		menuLanguage = new JMenu();
		menuLanguage.setFont(RetroComponents.FONT_PLAIN);

		ButtonGroup langGroup = new ButtonGroup();
		itemLangPt = new JRadioButtonMenuItem();
		itemLangPt.setFont(RetroComponents.FONT_PLAIN);
		itemLangPt.setSelected(true);
		itemLangPt.addActionListener(e -> I18n.setLocale(I18n.LOCALE_PT));

		itemLangEn = new JRadioButtonMenuItem();
		itemLangEn.setFont(RetroComponents.FONT_PLAIN);
		itemLangEn.addActionListener(e -> I18n.setLocale(I18n.LOCALE_EN));

		langGroup.add(itemLangPt);
		langGroup.add(itemLangEn);
		menuLanguage.add(itemLangPt);
		menuLanguage.add(itemLangEn);

		menuOptions.add(menuLanguage);

		menuHelp = new JMenu();
		menuHelp.setFont(RetroComponents.FONT_PLAIN);

		itemAbout = new JMenuItem();
		itemAbout.setFont(RetroComponents.FONT_PLAIN);
		itemAbout.addActionListener(e -> showAboutDialog());

		menuHelp.add(itemAbout);

		menuBar.add(menuFile);
		menuBar.add(menuModules);
		menuBar.add(menuOptions);
		menuBar.add(menuHelp);

		setJMenuBar(menuBar);
	}

	private void initializeContent() {
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(RetroComponents.FONT_BOLD);
		tabbedPane.setBackground(RetroComponents.COLOR_BG);
		tabbedPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

		posPanel = new POSPanel(apiClient, this::setStatusMessage);
		productPanel = new ProductPanel(apiClient, this::setStatusMessage);
		employeePanel = new EmployeePanel(apiClient, this::setStatusMessage);
		emailPanel = new EmailPanel(apiClient, this::setStatusMessage);

		tabbedPane.addTab(I18n.get("tab.pos"), posPanel);
		tabbedPane.addTab(I18n.get("tab.products"), productPanel);
		tabbedPane.addTab(I18n.get("tab.employees"), employeePanel);
		tabbedPane.addTab(I18n.get("tab.email"), emailPanel);

		tabbedPane.addChangeListener(e -> {
			int index = tabbedPane.getSelectedIndex();
			if (index == 0) {
				posPanel.refreshCatalog();
			} else if (index == 1) {
				productPanel.loadProducts();
			} else if (index == 2) {
				employeePanel.loadEmployees();
			}
		});

		add(tabbedPane, BorderLayout.CENTER);
	}

	private void initializeStatusBar() {
		JPanel statusBar = new JPanel(new BorderLayout(4, 0));
		statusBar.setBackground(RetroComponents.COLOR_BG);
		statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

		lblStatusMessage = new JLabel(I18n.get("status.starting"));
		lblStatusMessage.setFont(RetroComponents.FONT_PLAIN);
		JPanel pnlStatus = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
		pnlStatus.setBackground(RetroComponents.COLOR_BG);
		pnlStatus.setBorder(RetroComponents.createLoweredBevel());
		pnlStatus.add(lblStatusMessage);

		lblConnectedUser = new JLabel(I18n.get("status.user_prefix") + " admin@email.com");
		lblConnectedUser.setFont(RetroComponents.FONT_PLAIN);
		JPanel pnlUser = RetroComponents.createStatusPanel(lblConnectedUser, 200);

		lblClock = new JLabel("00:00:00");
		lblClock.setFont(RetroComponents.FONT_PLAIN);
		JPanel pnlClock = RetroComponents.createStatusPanel(lblClock, 140);

		JPanel rightContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
		rightContainer.setBackground(RetroComponents.COLOR_BG);
		rightContainer.add(pnlUser);
		rightContainer.add(pnlClock);

		statusBar.add(pnlStatus, BorderLayout.CENTER);
		statusBar.add(rightContainer, BorderLayout.EAST);

		add(statusBar, BorderLayout.SOUTH);
	}

	private void initializeTimer() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy");
		Timer timer = new Timer(1000, e -> {
			lblClock.setText(LocalDateTime.now().format(formatter));
		});
		timer.start();
	}

	public void updateLanguage() {
		setTitle(I18n.get("app.title"));

		menuFile.setText(I18n.get("menu.file"));
		itemSettings.setText(I18n.get("menu.file.settings"));
		itemTestConn.setText(I18n.get("menu.file.test_conn"));
		itemExit.setText(I18n.get("menu.file.exit"));

		menuModules.setText(I18n.get("menu.modules"));
		itemPOS.setText(I18n.get("menu.modules.pos"));
		itemProducts.setText(I18n.get("menu.modules.products"));
		itemEmployees.setText(I18n.get("menu.modules.employees"));
		itemEmail.setText(I18n.get("menu.modules.email"));

		menuOptions.setText(I18n.get("menu.options"));
		menuLanguage.setText(I18n.get("menu.options.language"));
		itemLangPt.setText(I18n.get("menu.options.lang_pt"));
		itemLangEn.setText(I18n.get("menu.options.lang_en"));

		menuHelp.setText(I18n.get("menu.help"));
		itemAbout.setText(I18n.get("menu.help.about"));

		tabbedPane.setTitleAt(0, I18n.get("tab.pos"));
		tabbedPane.setTitleAt(1, I18n.get("tab.products"));
		tabbedPane.setTitleAt(2, I18n.get("tab.employees"));
		tabbedPane.setTitleAt(3, I18n.get("tab.email"));

		lblConnectedUser.setText(I18n.get("status.user_prefix") + " admin@email.com");

		boolean isPt = I18n.getLocale().equals(I18n.LOCALE_PT);
		itemLangPt.setSelected(isPt);
		itemLangEn.setSelected(!isPt);

		repaint();
	}

	public void setStatusMessage(String message) {
		SwingUtilities.invokeLater(() -> lblStatusMessage.setText(message));
	}

	private void checkInitialConnection() {
		setStatusMessage(I18n.get("status.connecting", apiClient.getBaseUrl()));
		new Thread(() -> {
			boolean connected = apiClient.testConnection();
			SwingUtilities.invokeLater(() -> {
				if (connected) {
					setStatusMessage(I18n.get("status.connected", apiClient.getBaseUrl()));
					posPanel.refreshCatalog();
					productPanel.loadProducts();
				} else {
					setStatusMessage(I18n.get("status.offline", apiClient.getBaseUrl()));
					int choice = JOptionPane.showConfirmDialog(
							this,
							I18n.get("dialog.offline.msg", apiClient.getBaseUrl()),
							I18n.get("dialog.offline.title"),
							JOptionPane.YES_NO_OPTION,
							JOptionPane.WARNING_MESSAGE
					);
					if (choice == JOptionPane.YES_OPTION) {
						showServerSettingsDialog();
					}
				}
			});
		}).start();
	}

	private void testBackendConnection() {
		setStatusMessage(I18n.get("dialog.conn_test.ping"));
		new Thread(() -> {
			boolean success = apiClient.testConnection();
			SwingUtilities.invokeLater(() -> {
				if (success) {
					setStatusMessage(I18n.get("status.connected", apiClient.getBaseUrl()));
					JOptionPane.showMessageDialog(this,
							I18n.get("dialog.conn_test.ok", apiClient.getBaseUrl()),
							I18n.get("dialog.conn_test.ok_title"),
							JOptionPane.INFORMATION_MESSAGE);
				} else {
					setStatusMessage(I18n.get("status.offline", apiClient.getBaseUrl()));
					JOptionPane.showMessageDialog(this,
							I18n.get("dialog.conn_test.fail", apiClient.getBaseUrl()),
							I18n.get("dialog.conn_test.fail_title"),
							JOptionPane.ERROR_MESSAGE);
				}
			});
		}).start();
	}

	private void showServerSettingsDialog() {
		JTextField txtUrl = RetroComponents.createTextField(25);
		txtUrl.setText(apiClient.getBaseUrl());

		JTextField txtUser = RetroComponents.createTextField(25);
		txtUser.setText("admin@email.com");

		JPasswordField txtPass = RetroComponents.createPasswordField(25);
		txtPass.setText("admin123");

		JPanel dialogPanel = new JPanel(new GridLayout(3, 2, 8, 8));
		dialogPanel.setBackground(RetroComponents.COLOR_BG);
		dialogPanel.add(RetroComponents.createLabel(I18n.get("dialog.server_settings.url")));
		dialogPanel.add(txtUrl);
		dialogPanel.add(RetroComponents.createLabel(I18n.get("dialog.server_settings.user")));
		dialogPanel.add(txtUser);
		dialogPanel.add(RetroComponents.createLabel(I18n.get("dialog.server_settings.pass")));
		dialogPanel.add(txtPass);

		int option = JOptionPane.showConfirmDialog(
				this,
				dialogPanel,
				I18n.get("dialog.server_settings.title"),
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
		);

		if (option == JOptionPane.OK_OPTION) {
			apiClient.setBaseUrl(txtUrl.getText().trim());
			apiClient.setCredentials(txtUser.getText().trim(), new String(txtPass.getPassword()).trim());
			lblConnectedUser.setText(I18n.get("status.user_prefix") + " " + txtUser.getText().trim());
			checkInitialConnection();
		}
	}

	private void showAboutDialog() {
		JOptionPane.showMessageDialog(
				this,
				I18n.get("app.about.text"),
				I18n.get("app.about.title"),
				JOptionPane.INFORMATION_MESSAGE
		);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (Exception ignored) {
		}

		SwingUtilities.invokeLater(() -> {
			StationeryDesktopApp app = new StationeryDesktopApp();
			app.setVisible(true);
		});
	}
}
