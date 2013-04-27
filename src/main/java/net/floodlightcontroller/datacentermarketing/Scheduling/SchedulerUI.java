/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JTextField;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.UIManager;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import net.floodlightcontroller.datacentermarketing.FlowUI;

/**
 * @author mininet
 * 
 */
public class SchedulerUI extends JDialog {

    public void main(String[] args) {
	try {
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	} catch (Exception e) {
	    e.printStackTrace();
	}
	try {
	    FlowUI dialog = new FlowUI();
	    dialog.setTitle("Datacenter Control Panel");
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setVisible(true);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    public SchedulerUI() {

	JScrollPane scrollPane = new JScrollPane();
	getContentPane().add(scrollPane, BorderLayout.CENTER);

	JPanel mainFrame = new JPanel();
	scrollPane.add(mainFrame);

	JPanel controlPane = new JPanel();
	getContentPane().add(controlPane, BorderLayout.SOUTH);

	JComboBox showClient = new JComboBox();
	showClient.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent arg0) {
	    }
	});
	showClient.setToolTipText("Choose the client to highlight");
	showClient.setModel(new DefaultComboBoxModel(new String[] { "0", "1",
		"2", "3", "4" }));
	showClient.setSelectedIndex(0);
	showClient.setEditable(true);
	controlPane.add(showClient);

	JRadioButton swl = new JRadioButton("Show Switch");
	swl.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	    }
	});
	controlPane.add(swl);

	JRadioButton ptl = new JRadioButton("Show Port");
	ptl.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	    }
	});
	controlPane.add(ptl);

	JRadioButton ql = new JRadioButton("Show Queue");
	ql.addActionListener(new ActionListener() {
	    public void actionPerformed(ActionEvent e) {
	    }
	});
	controlPane.add(ql);

	JSlider scale = new JSlider();
	scale.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent arg0) {
	    }
	});
	scale.setToolTipText("scale");
	controlPane.add(scale);

	JSlider speed = new JSlider();
	speed.addChangeListener(new ChangeListener() {
	    public void stateChanged(ChangeEvent e) {
	    }
	});
	speed.setToolTipText("spped");
	controlPane.add(speed);
    }

}
