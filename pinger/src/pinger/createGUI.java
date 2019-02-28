package pinger;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class createGUI {
	public static JFrame frame;
	private static JPanel contentPanel = new JPanel();
	private static ColoredMenuBar menuBar;
	private JMenu exitMenu;
	private ColoredMenu closeMenu;
	private JMenuItem exitMi;
	private static int NUMBEROFPINGS = 2;
	public static JLabel[] pingLabels = new JLabel[NUMBEROFPINGS];
	public static JTextField[] pingFields = new JTextField[NUMBEROFPINGS];
	private static JLabel[] pingLabelsTemp = new JLabel[NUMBEROFPINGS];
	private static JTextField[] pingFieldsTemp = new JTextField[NUMBEROFPINGS];
	private final Dimension WINDOWDIMENSION = new Dimension(600, 250);
	private static int PINGFREQUENCY = 1;
	private static final String PINGFREQTEXT = "Ping Frequency: ";
	private static JLabel pingFreqLabel = new JLabel(PINGFREQTEXT + PINGFREQUENCY + " sec");
	
	static Point point = new Point();
	
	public static int getFrequency() {
		return PINGFREQUENCY;
	}
	
	public static void setFrequency(int freq) {
		PINGFREQUENCY = freq;
	}
	
	public static Color hex2Rgb(String colorStr) {
		return new Color(Integer.valueOf(colorStr.substring(1, 3), 16), Integer.valueOf(colorStr.substring(3, 5), 16), Integer.valueOf(colorStr.substring(5, 7), 16));
	}

	public createGUI() {
		frame = new JFrame("Pinger");
		
		try {
	        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    }catch(Exception ex) {
	        ex.printStackTrace();
	    }
		
		menuBar = new ColoredMenuBar();
		menuBar.setColor(hex2Rgb("#228388"));
		
		exitMenu = new JMenu("Exit");
		exitMenu.setForeground(Color.WHITE);
		exitMi = new JMenuItem("Exit");
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		exitMi.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	System.exit(0);
            }
	    });
		exitMi.setMnemonic(KeyEvent.VK_K);
		exitMi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, ActionEvent.ALT_MASK));
		
		closeMenu = new ColoredMenu("Kilépés");
		closeMenu.setForeground(Color.WHITE);
		closeMenu.setColor(hex2Rgb("#c75050"));
		closeMenu.addMouseListener(new MouseListener() {
			boolean isSelected = false;

			@Override
			public void mouseClicked(MouseEvent arg0) {}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				closeMenu.setColor(hex2Rgb("#e04343"));
				isSelected = true;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				closeMenu.setColor(hex2Rgb("#c75050"));
				isSelected = false;
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (isSelected) {
					System.exit(0);
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {}
			
		});
		menuBar.add(exitMenu);
		menuBar.add(Box.createHorizontalGlue());
		menuBar.add(closeMenu);

		exitMenu.add(exitMi);
		frame.setJMenuBar(menuBar);
		frame.setUndecorated(true);

		createContentPanel();
		
		frame.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
		    	point.x = e.getX();
		    	point.y = e.getY();
			}
		});
		frame.addMouseMotionListener(new MouseMotionAdapter() {
	    	public void mouseDragged(MouseEvent e) {
	    		Point p = frame.getLocation();
	    		frame.setLocation(p.x + e.getX() - point.x, p.y + e.getY() - point.y);
	    	}
	    });
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);    
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);
	}
	
	public void createContentPanel() {
		for (int i = 0; i < NUMBEROFPINGS; i++) {
			pingLabels[i] = new JLabel();
			pingFields[i] = new JTextField("", 30);
			pingLabels[i].setText(i + ": ");
			pingFields[i].setToolTipText("IP address, HTTP URL, or leave blank");
		}
		String defautlGateway = "192.168.0.1";
		if (System.getProperty("os.name").toLowerCase().contains("windows")) {
			defautlGateway = getDefaultGatewayIP().replaceAll("http://", "");
		} else if (System.getProperty("os.name").toLowerCase().contains("linux")) {
			defautlGateway = getDefaultAddress();
		} else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
			defautlGateway = getDefaultAddress();
		}
		
		pingFields[0].setText("8.8.8.8");
		pingFields[1].setText(defautlGateway);
		
		final DynamicGroupLayout dgl = new DynamicGroupLayout();
		contentPanel.setPreferredSize(WINDOWDIMENSION);
		contentPanel = dgl.create(NUMBEROFPINGS);
		JScrollPane jsp = new JScrollPane(contentPanel) {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public Dimension getPreferredSize() {
                return WINDOWDIMENSION;
            }
        };
        jsp.getVerticalScrollBar().addAdjustmentListener((AdjustmentEvent e) -> {
            e.getAdjustable().setValue(e.getAdjustable().getMaximum());
        });
        
        JPanel osPanel = new JPanel();
        JLabel osLabel = new JLabel("OS: " + System.getProperty("os.name") + "      DeafaultGateway: " + getDefaultAddress());
        osPanel.add(osLabel);
        
        JPanel controls = new JPanel();
        JRadioButton classicRadioButton = new JRadioButton("Classic", false);
        JRadioButton fxRadioButton = new JRadioButton("Fx", true);
        ButtonGroup group = new ButtonGroup();
        group.add(classicRadioButton);
        group.add(fxRadioButton);
        controls.add(classicRadioButton);
        controls.add(fxRadioButton);
        controls.add(new JButton(new AbstractAction("Add") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				if (NUMBEROFPINGS < 8) {
					resizeArrays();
	                dgl.add(pingLabels, pingFields);
	                contentPanel.validate();
				}
            }

			private void resizeArrays() {
				pingLabelsTemp = pingLabels;
				pingFieldsTemp = pingFields;
				
				NUMBEROFPINGS++;
				pingLabels = new JLabel[NUMBEROFPINGS];
				pingFields = new JTextField[NUMBEROFPINGS];
				
				for (int i = 0; i < NUMBEROFPINGS; i++) {
					pingLabels[i] = new JLabel();
					pingFields[i] = new JTextField("", 30);
					pingFields[i].setToolTipText("IP address, HTTP URL, or leave blank");
					if (i < NUMBEROFPINGS - 1) {
						pingLabels[i] = pingLabelsTemp[i];
						pingFields[i] = pingFieldsTemp[i];
					} else {
						pingLabels[i].setText(i + ": ");
					}
				}

				pingLabelsTemp = new JLabel[NUMBEROFPINGS];
				pingFieldsTemp = new JTextField[NUMBEROFPINGS];
			}
        }));
        controls.add(new JButton(new AbstractAction("Start") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				ArrayList<String> strArray = new ArrayList<String>();
				for (int i = 0; i < NUMBEROFPINGS; i++) {
					System.out.println(pingFields[i].getText());
					if (pingFields[i].getText().isEmpty()) {
						continue;
					}
					strArray.add(pingFields[i].getText());
				}
				if (strArray.size() > 0) {
					if (classicRadioButton.isSelected()) {
						new PingerWindow(strArray);
					} else if (fxRadioButton.isSelected()) {
						new PingerWindowFx(strArray);
					}
				}
            }

        }));
        controls.add(pingFreqLabel);
        controls.add(new JButton(new AbstractAction("+") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				PINGFREQUENCY++;
				pingFreqLabel.setText(PINGFREQTEXT + PINGFREQUENCY + " sec");
            }
        }));
        controls.add(new JButton(new AbstractAction("-") {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
            public void actionPerformed(ActionEvent e) {
				if (PINGFREQUENCY > 1) {
					PINGFREQUENCY--;
					pingFreqLabel.setText(PINGFREQTEXT + PINGFREQUENCY + " sec");
				}
            }
        }));
        frame.getContentPane().add(osPanel, BorderLayout.NORTH);
        frame.getContentPane().add(jsp);
        frame.getContentPane().add(controls, BorderLayout.SOUTH);
	}
	
	public static String getDefaultGatewayIP() {
		final String DEFAULT_GATEWAY = "Default Gateway";
		
        if (Desktop.isDesktopSupported()) {
            try {
                Process process = Runtime.getRuntime().exec("ipconfig");

                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()))) {

                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        if (line.trim().startsWith(DEFAULT_GATEWAY)) {
                            String ipAddress = line.substring(line.indexOf(":") + 1).trim(), routerURL = String.format("http://%s", ipAddress);
                            return routerURL;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
       return ""; 
    }
	
	private String getDefaultAddress() {
        String defaultAddress = "";
        try {
            Process result = Runtime.getRuntime().exec("netstat -rn");

            BufferedReader output = new BufferedReader(new InputStreamReader(
                    result.getInputStream()));

            String line = output.readLine();
            while (line != null) {
                if (line.contains("0.0.0.0")) {

                    StringTokenizer stringTokenizer = new StringTokenizer(line);
                    stringTokenizer.nextElement(); // first element is 0.0.0.0
                    stringTokenizer.nextElement(); // second element is 0.0.0.0
                    defaultAddress = (String) stringTokenizer.nextElement(); //gateway
                    break;
                } else if (line.startsWith("default")) {
                	StringTokenizer stringTokenizer = new StringTokenizer(line);
                    stringTokenizer.nextElement(); // first element is default
                    defaultAddress = (String) stringTokenizer.nextElement(); //gateway
                    break;
                }

                line = output.readLine();
            } // while
        } catch (IOException e) {
            e.printStackTrace();
        }

        return defaultAddress;

} // getDefaultAddress
}