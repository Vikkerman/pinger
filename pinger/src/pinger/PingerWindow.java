package pinger;

import java.awt.Color;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Ping multiple IP addresses
 * 
 * @author Bulla Viktor
 *
 */
public class PingerWindow {
	private JDialog dialogWindow;
	
	private ColoredMenuBar menuBar;
	private JMenu exitMenu;
	private ColoredMenu closeMenu;
	private JMenuItem exitMi;
	
	private JPanel containerPanel;
	private JLabel[] textLabel;
	private JTextArea[] textArea;
	private JScrollPane[] scrollPane;
	private static ArrayList<String> pingAd = new ArrayList<String>();
	private static int AREANUMBERS;
	private static int BOXNUMBERS;
	private static Box[] boxes;
	
	private boolean run = false;
	
	static Point point = new Point();
	
	public PingerWindow(ArrayList<String> strList) {
		AREANUMBERS = strList.size();
		BOXNUMBERS = AREANUMBERS * 2;
		boxes = new Box[BOXNUMBERS];
		pingAd = strList;
		
		run = true;
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		
		dialogWindow = new JDialog(createGUI.frame, "Pinger");
		
		menuBar = new ColoredMenuBar();
		menuBar.setColor(createGUI.hex2Rgb("#228388"));
		
		exitMenu = new JMenu("Exit");
		exitMenu.setForeground(Color.WHITE);
		exitMi = new JMenuItem("Exit");
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		exitMi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	closeDialogWindow();
            }
	    });
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		
		closeMenu = new ColoredMenu("Kilépés");
		closeMenu.setForeground(Color.WHITE);
		closeMenu.setColor(createGUI.hex2Rgb("#c75050"));
		closeMenu.addMouseListener(new MouseListener() {
			boolean isSelected = false;

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				closeMenu.setColor(createGUI.hex2Rgb("#e04343"));
				isSelected = true;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				closeMenu.setColor(createGUI.hex2Rgb("#c75050"));
				isSelected = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (isSelected) {
					closeDialogWindow();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		menuBar.add(exitMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(closeMenu);

		exitMenu.add(exitMi);
		dialogWindow.setJMenuBar(menuBar);
		dialogWindow.setUndecorated(true);
		
		containerPanel = new JPanel();
		textArea = new JTextArea[AREANUMBERS];
		scrollPane = new JScrollPane[AREANUMBERS];
		textLabel = new JLabel[AREANUMBERS];
		
		LayoutManager layout = new BoxLayout(containerPanel, BoxLayout.Y_AXIS);
		initializeBoxes();
		containerPanel.setLayout(layout);
		initializeBoxContentsAndStartPings();
		
		dialogWindow.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		dialogWindow.addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseDragged(MouseEvent e) {
	    		Point p = dialogWindow.getLocation();
	    		dialogWindow.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
	    	}
	    });
		
		dialogWindow.getContentPane().add(containerPanel);
		dialogWindow.setResizable(false);
		dialogWindow.pack();
		dialogWindow.setLocationRelativeTo(null);
		dialogWindow.repaint();
		dialogWindow.setVisible(true);
	}

	protected void closeDialogWindow() {
		run = false;
		dialogWindow.getParent().setVisible(true);
		dialogWindow.dispose();
	}

	private void initializeBoxContentsAndStartPings() {
		for (int i = 0; i < textArea.length; i++) {
			textArea[i] = new JTextArea(10,50);
			textArea[i].setText("");
			textArea[i].setFont(new Font("Arial", Font.BOLD, 12));
			scrollPane[i] = new JScrollPane(textArea[i]);
			scrollPane[i].setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
			boxes[i*2].add(scrollPane[i]);
			
			textLabel[i] = new JLabel();
			textLabel[i].setText("Disconnections (" + pingAd.get(i) + "): " + 0);
			textLabel[i].setFont(new Font("Arial", Font.BOLD, 12));
			textLabel[i].setForeground(Color.RED);
			boxes[i*2 + 1].add(textLabel[i]);
			
			pingerThread(textArea[i], pingAd.get(i), textLabel[i]);
		}
	}

	@SuppressWarnings("static-access")
	private void initializeBoxes() {
		for (int i = 0; i < BOXNUMBERS; i++) {
			boxes[i] = Box.createHorizontalBox();
			boxes[i].createGlue();
			containerPanel.add(boxes[i]);
		}
	}

	private void pingerThread(JTextArea area, String pingAddress, JLabel tLabel) {
		new Thread() {
			private long ping;
			private int dc = 0;

			public void run() {
				while (run) {
					String str = "";
					str = new Date().toString();
					ping = ping(pingAddress).toMillis();
					if (ping > 10000) {
						updateText();
					}
					System.out.println(str += " (" + pingAddress + "): " + ping + " ms");
					try {
						Thread.sleep(createGUI.getFrequency()*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					str += ((ping == 86400000) ? " NOT CONNECTED" : "") + System.getProperty("line.separator").toString();
					area.setText("> " + str + area.getText().replaceAll(">", " "));
				}
			}

			private void updateText() {
				dc++;
				tLabel.setText("Disconnections (" + pingAddress + "): " + dc);
			}
		}.start();
	}

	public static Duration ping(String host) {
	    Instant startTime = Instant.now();
	    try {
	        InetAddress address = InetAddress.getByName(host);
	        if (address.isReachable(createGUI.getFrequency()*1000)) {
	            return Duration.between(startTime, Instant.now());
	        }
	    } catch (IOException e) {
	        // Host not available, nothing to do here
	    }
	    return Duration.ofDays(1);
	}
}
