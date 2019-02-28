package pinger;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @see https://stackoverflow.com/a/41926375/230513
 * @see https://stackoverflow.com/a/14858272/230513
 * @see https://stackoverflow.com/a/8504753/230513
 * @see https://stackoverflow.com/a/14011536/230513
 */
public class DynamicGroupLayout {

    private GroupLayout layout;
    private GroupLayout.ParallelGroup parallel;
    private GroupLayout.SequentialGroup sequential;
    private int i;

    JPanel create(int NUM) {
    	System.out.println(NUM);
        JPanel panel = new JPanel();
        layout = new GroupLayout(panel);
        panel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        parallel = layout.createParallelGroup();
        layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(parallel));
        sequential = layout.createSequentialGroup();
        layout.setVerticalGroup(sequential);
        for (int i = 0; i < NUM; i++) {
            add(createGUI.pingLabels, createGUI.pingFields);
        }
        return panel;
    }

    void add(JLabel[] pingLabels, JTextField[] pingFileds) {
    	System.out.println("i: " + i);
        pingLabels[i].setLabelFor(pingFileds[i]);
        parallel.addGroup(layout.createSequentialGroup().addComponent(pingLabels[i]).addComponent(pingFileds[i]));
        sequential.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(pingLabels[i]).addComponent(pingFileds[i]));
        i++;
    }
}