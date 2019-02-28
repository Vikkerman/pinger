package pinger;

import javax.swing.SwingUtilities;

/**
 * Ping multiple IP addresses
 * 
 * @author Bulla Viktor
 *
 */
public class pinger {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new createGUI();
            }
	    });
	}
}