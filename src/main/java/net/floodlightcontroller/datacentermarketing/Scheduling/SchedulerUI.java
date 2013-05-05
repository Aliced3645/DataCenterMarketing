/**
 * 
 */
package net.floodlightcontroller.datacentermarketing.Scheduling;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import net.floodlightcontroller.datacentermarketing.FlowUI;
import net.floodlightcontroller.datacentermarketing.MarketManager;
import net.floodlightcontroller.datacentermarketing.logic.BiddingClock;

import javax.swing.JMenuBar;

/**
 * @author mininet
 * 
 */
public class SchedulerUI extends JDialog {

	public static volatile boolean showQueue = false;
	public static volatile boolean showPort = false;
	public static volatile boolean showSwitch = false;
	public static volatile JTextField info;

	// click position
	public static volatile int x;
	public static volatile int y;
	/*
	 * public static volatile Boolean captured = false; public static volatile
	 * Object highLighted = null;
	 */

	private final int width = 1000;

	private final int height = 10000;

	private int thick = 2;

	private JLabel st = new JLabel("start   ");

	private JLabel ed = new JLabel("end     ");

	public void doRepaint() {

		repaint();
	}

	/*
	 * public void step(String[] args) {
	 * 
	 * System.out.println("\n\n\t\t\33333333"); try {
	 * UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
	 * catch (Exception e) { e.printStackTrace(); } try { FlowUI dialog = new
	 * FlowUI(); dialog.setTitle("Datacenter Control Panel");
	 * dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	 * dialog.setVisible(true); } catch (Exception e) { e.printStackTrace(); } }
	 */

	long endTimeOffset = 10000;

	private class MainCanvas extends JPanel {

		public MainCanvas() {
			super();
			// TODO Auto-generated constructor stub
			setOpaque(false);
			setPreferredSize(new Dimension(width, height));
			setBackground(Color.white);
			setBorder(BorderFactory.createLineBorder(Color.yellow));
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			g.setColor(Color.black);
			g.fillRect(0, 0, width, height);

			st.setText("" + System.currentTimeMillis() /* / 1000 */);

			ed.setText("" + (System.currentTimeMillis() + endTimeOffset)/*
																		 * /
																		 * 1000
																		 */);

			Scheduler.getInstance()
					.visualize(
							g,
							width,
							endTimeOffset
									+ BiddingClock.getInstance()
											.getCurrentTime(), thick);

		}
	}

	public SchedulerUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		setBounds(150, 100, width, height);
		/*
		 * JMenuBar menuBar = new J(); menuBar.setToolTipText("Mwnu");
		 * menuBar.setBounds(0, 0, width, 10);
		 */
		JPanel menuBar = new JPanel();
		menuBar.setLayout(new BorderLayout(0, 0));
		info = new JTextField();
		info.setPreferredSize(new Dimension(width / 2, 30));
		info.setEditable(false);
		menuBar.add(info, BorderLayout.CENTER);
		menuBar.add(st, BorderLayout.WEST);
		menuBar.add(ed, BorderLayout.EAST);

		getContentPane().add(menuBar, BorderLayout.NORTH);

		MainCanvas mainFrame = new MainCanvas();

		mainFrame.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// here goes the code of calling setButtonText method of
				// ButtonPanel component on which the event had occurred

				x = e.getX();
				y = e.getY();
				info.setText(x + ": " + y);
				// captured = false;
				repaint();

			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub

			}
		});

		JScrollPane scrollPane = new JScrollPane(mainFrame,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

		getContentPane().add(scrollPane, BorderLayout.CENTER);

		// control
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

				showSwitch = !showSwitch;
				repaint();
			}
		});
		controlPane.add(swl);

		JRadioButton ptl = new JRadioButton("Show Port");
		ptl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showPort = !showPort;
				repaint();
			}
		});
		controlPane.add(ptl);

		JRadioButton ql = new JRadioButton("Show Queue");
		ql.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showQueue = !showQueue;
				repaint();
			}
		});
		controlPane.add(ql);

		final JSlider scale = new JSlider();
		scale.setValue(2);
		scale.setMaximum(10);
		scale.setMinimum(1);
		scale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				thick = scale.getValue();
				repaint();
			}
		});
		scale.setToolTipText("thick");
		controlPane.add(scale);

		final JSlider speed = new JSlider();
		speed.setValue(1);
		speed.setMinimum(1);
		speed.setMaximum(15);
		speed.setPaintTicks(true);
		speed.setPaintLabels(true);
		speed.setPaintLabels(true);
		speed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				endTimeOffset = speed.getValue() * 10000;
				repaint();
			}
		});
		speed.setToolTipText("scale");
		controlPane.add(speed);
	}
}
