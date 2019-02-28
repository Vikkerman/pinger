package pinger;

import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

/**
 * Ping multiple IP addresses
 * 
 * @author Bulla Viktor
 *
 */
public class PingerWindowFx {
	private JDialog dialogWindowFx;
	
	private ColoredMenuBar menuBar;
	private JMenu exitMenu;
	private ColoredMenu closeMenu;
	private JMenuItem exitMi;
	
	private JPanel containerPanel;
	private JLabel[] textLabel;
	private FxPanel[] fxpanels;
	
	private ArrayList<String> pingAd = new ArrayList<String>();
	private int AREANUMBERS;
	private int BOXNUMBERS;
	private Box[] boxes;
	
	static Point point = new Point();
	
	private boolean run = false;
	
	public PingerWindowFx(ArrayList<String> strList) {
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
		
		dialogWindowFx = new JDialog(createGUI.frame, "Pinger");

		menuBar = new ColoredMenuBar();
		menuBar.setColor(createGUI.hex2Rgb("#228388"));
		
		exitMenu = new JMenu("Exit");
		exitMenu.setForeground(Color.WHITE);
		exitMi = new JMenuItem("Exit");
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		exitMi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	closedialogWindowFx();
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
					closedialogWindowFx();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		menuBar.add(exitMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(closeMenu);

		exitMenu.add(exitMi);
		dialogWindowFx.setJMenuBar(menuBar);
		dialogWindowFx.setUndecorated(true);
		
		containerPanel = new JPanel();
		fxpanels = new FxPanel[AREANUMBERS];
		textLabel = new JLabel[AREANUMBERS];
		
		LayoutManager layout = new BoxLayout(containerPanel, BoxLayout.Y_AXIS);
		initializeBoxes();
		containerPanel.setLayout(layout);
		
		initializeBoxContentsAndStartPings();
		
		dialogWindowFx.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				point.x = e.getX();
				point.y = e.getY();
			}
		});
		dialogWindowFx.addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseDragged(MouseEvent e) {
	    		Point p = dialogWindowFx.getLocation();
	    		dialogWindowFx.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
	    	}
	    });
		
		dialogWindowFx.getContentPane().add(containerPanel);
		dialogWindowFx.setResizable(false);
		dialogWindowFx.pack();
		dialogWindowFx.setLocationRelativeTo(null);
		dialogWindowFx.repaint();
		dialogWindowFx.setVisible(true);
	}

	protected void closedialogWindowFx() {
		run = false;
		dialogWindowFx.getParent().setVisible(true);
		dialogWindowFx.dispose();
	}

	private void initializeBoxContentsAndStartPings() {
		for (int i = 0; i < AREANUMBERS; i++) {
			fxpanels[i] = new FxPanel();
			fxpanels[i].setSize(new Dimension(600, 300));
			boxes[i*2].add(fxpanels[i]);
			boxes[i*2].setPreferredSize(new Dimension(600, 200));
			
			textLabel[i] = new JLabel();
			textLabel[i].setText("Disconnections (" + pingAd.get(i) + "): " + 0);
			textLabel[i].setFont(new Font("Arial", Font.BOLD, 12));
			textLabel[i].setForeground(Color.RED);
			boxes[i*2 + 1].add(textLabel[i]);

			pingerThread(i, pingAd.get(i), textLabel[i]);
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

	private void pingerThread(int index, String pingAddress, JLabel tLabel) {
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
					str += " (" + pingAddress + "): " + ping + " ms";
					System.out.println(str);
					try {
						Thread.sleep(createGUI.getFrequency()*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					str = str.replaceAll("86400000", "NOT CONNECTED");
					fxpanels[index].setTextFlow(str, str.contains("NOT") ? "red" : "white");
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

