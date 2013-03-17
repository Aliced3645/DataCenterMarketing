package net.floodlightcontroller.UI;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Font;
import javax.swing.JTextField;
import java.awt.Insets;
import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.JTextArea;

public class FlowUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField sourceHostInput;
	private JTextField destHostInput;
	private JTextField bandwidthInput;
	private JTextField delayInput;
	private JTextField inputBidPrice;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FlowUI dialog = new FlowUI();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public FlowUI() {
		setBounds(100, 100, 758, 447);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{35, 89, 114, 0, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{65, 0, 19, 0, 117, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblDatacenterMarketingControl = new JLabel("Datacenter Marketing Control Panel");
			GridBagConstraints gbc_lblDatacenterMarketingControl = new GridBagConstraints();
			gbc_lblDatacenterMarketingControl.anchor = GridBagConstraints.WEST;
			gbc_lblDatacenterMarketingControl.fill = GridBagConstraints.VERTICAL;
			gbc_lblDatacenterMarketingControl.insets = new Insets(0, 0, 5, 5);
			gbc_lblDatacenterMarketingControl.gridx = 1;
			gbc_lblDatacenterMarketingControl.gridy = 0;
			contentPanel.add(lblDatacenterMarketingControl, gbc_lblDatacenterMarketingControl);
		}
		{
			JLabel lblFlowDesignation = new JLabel("Flow Designation");
			lblFlowDesignation.setForeground(Color.RED);
			lblFlowDesignation.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			GridBagConstraints gbc_lblFlowDesignation = new GridBagConstraints();
			gbc_lblFlowDesignation.anchor = GridBagConstraints.WEST;
			gbc_lblFlowDesignation.insets = new Insets(0, 0, 5, 5);
			gbc_lblFlowDesignation.gridx = 1;
			gbc_lblFlowDesignation.gridy = 1;
			contentPanel.add(lblFlowDesignation, gbc_lblFlowDesignation);
		}
		{
			JLabel lblSourceHost = new JLabel("Source Host");
			lblSourceHost.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblSourceHost = new GridBagConstraints();
			gbc_lblSourceHost.insets = new Insets(0, 0, 5, 5);
			gbc_lblSourceHost.gridx = 1;
			gbc_lblSourceHost.gridy = 2;
			contentPanel.add(lblSourceHost, gbc_lblSourceHost);
		}
		{
			sourceHostInput = new JTextField();
			GridBagConstraints gbc_sourceHostInput = new GridBagConstraints();
			gbc_sourceHostInput.insets = new Insets(0, 0, 5, 5);
			gbc_sourceHostInput.anchor = GridBagConstraints.WEST;
			gbc_sourceHostInput.gridx = 2;
			gbc_sourceHostInput.gridy = 2;
			contentPanel.add(sourceHostInput, gbc_sourceHostInput);
			sourceHostInput.setColumns(10);
		}
		{
			JLabel lblDestinationHost = new JLabel("Destination Host");
			lblDestinationHost.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblDestinationHost = new GridBagConstraints();
			gbc_lblDestinationHost.insets = new Insets(0, 0, 5, 5);
			gbc_lblDestinationHost.anchor = GridBagConstraints.EAST;
			gbc_lblDestinationHost.gridx = 4;
			gbc_lblDestinationHost.gridy = 2;
			contentPanel.add(lblDestinationHost, gbc_lblDestinationHost);
		}
		{
			destHostInput = new JTextField();
			GridBagConstraints gbc_destHostInput = new GridBagConstraints();
			gbc_destHostInput.insets = new Insets(0, 0, 5, 0);
			gbc_destHostInput.anchor = GridBagConstraints.WEST;
			gbc_destHostInput.gridx = 5;
			gbc_destHostInput.gridy = 2;
			contentPanel.add(destHostInput, gbc_destHostInput);
			destHostInput.setColumns(10);
		}
		{
			JLabel lblNetworkAvailability = new JLabel("Network Availability");
			lblNetworkAvailability.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			lblNetworkAvailability.setForeground(Color.RED);
			GridBagConstraints gbc_lblNetworkAvailability = new GridBagConstraints();
			gbc_lblNetworkAvailability.anchor = GridBagConstraints.WEST;
			gbc_lblNetworkAvailability.insets = new Insets(0, 0, 5, 5);
			gbc_lblNetworkAvailability.gridx = 1;
			gbc_lblNetworkAvailability.gridy = 3;
			contentPanel.add(lblNetworkAvailability, gbc_lblNetworkAvailability);
		}
		{
			JTextArea textArea = new JTextArea();
			GridBagConstraints gbc_textArea = new GridBagConstraints();
			gbc_textArea.gridwidth = 5;
			gbc_textArea.insets = new Insets(0, 0, 5, 0);
			gbc_textArea.fill = GridBagConstraints.BOTH;
			gbc_textArea.gridx = 1;
			gbc_textArea.gridy = 4;
			contentPanel.add(textArea, gbc_textArea);
		}
		{
			JLabel lblRequirements = new JLabel("Requirements:");
			lblRequirements.setForeground(Color.RED);
			lblRequirements.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			GridBagConstraints gbc_lblRequirements = new GridBagConstraints();
			gbc_lblRequirements.anchor = GridBagConstraints.WEST;
			gbc_lblRequirements.insets = new Insets(0, 0, 5, 5);
			gbc_lblRequirements.gridx = 1;
			gbc_lblRequirements.gridy = 5;
			contentPanel.add(lblRequirements, gbc_lblRequirements);
		}
		{
			JLabel lblBandwidth = new JLabel("Bandwidth ");
			lblBandwidth.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblBandwidth = new GridBagConstraints();
			gbc_lblBandwidth.anchor = GridBagConstraints.SOUTH;
			gbc_lblBandwidth.insets = new Insets(0, 0, 5, 5);
			gbc_lblBandwidth.gridx = 1;
			gbc_lblBandwidth.gridy = 6;
			contentPanel.add(lblBandwidth, gbc_lblBandwidth);
		}
		{
			bandwidthInput = new JTextField();
			GridBagConstraints gbc_bandwidthInput = new GridBagConstraints();
			gbc_bandwidthInput.anchor = GridBagConstraints.WEST;
			gbc_bandwidthInput.insets = new Insets(0, 0, 5, 5);
			gbc_bandwidthInput.gridx = 2;
			gbc_bandwidthInput.gridy = 6;
			contentPanel.add(bandwidthInput, gbc_bandwidthInput);
			bandwidthInput.setColumns(10);
		}
		{
			JLabel lblDelayWithin = new JLabel("Delay");
			lblDelayWithin.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblDelayWithin = new GridBagConstraints();
			gbc_lblDelayWithin.insets = new Insets(0, 0, 5, 5);
			gbc_lblDelayWithin.gridx = 4;
			gbc_lblDelayWithin.gridy = 6;
			contentPanel.add(lblDelayWithin, gbc_lblDelayWithin);
		}
		{
			delayInput = new JTextField();
			GridBagConstraints gbc_delayInput = new GridBagConstraints();
			gbc_delayInput.insets = new Insets(0, 0, 5, 0);
			gbc_delayInput.fill = GridBagConstraints.HORIZONTAL;
			gbc_delayInput.gridx = 5;
			gbc_delayInput.gridy = 6;
			contentPanel.add(delayInput, gbc_delayInput);
			delayInput.setColumns(10);
		}
		{
			JLabel lblBid = new JLabel("Your Bid");
			lblBid.setForeground(Color.RED);
			lblBid.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			GridBagConstraints gbc_lblBid = new GridBagConstraints();
			gbc_lblBid.anchor = GridBagConstraints.WEST;
			gbc_lblBid.insets = new Insets(0, 0, 5, 5);
			gbc_lblBid.gridx = 1;
			gbc_lblBid.gridy = 7;
			contentPanel.add(lblBid, gbc_lblBid);
		}
		{
			JLabel lblBidPrice = new JLabel("Bid Price");
			lblBidPrice.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblBidPrice = new GridBagConstraints();
			gbc_lblBidPrice.insets = new Insets(0, 0, 5, 5);
			gbc_lblBidPrice.gridx = 1;
			gbc_lblBidPrice.gridy = 8;
			contentPanel.add(lblBidPrice, gbc_lblBidPrice);
		}
		{
			inputBidPrice = new JTextField();
			GridBagConstraints gbc_inputBidPrice = new GridBagConstraints();
			gbc_inputBidPrice.insets = new Insets(0, 0, 5, 5);
			gbc_inputBidPrice.fill = GridBagConstraints.HORIZONTAL;
			gbc_inputBidPrice.gridx = 2;
			gbc_inputBidPrice.gridy = 8;
			contentPanel.add(inputBidPrice, gbc_inputBidPrice);
			inputBidPrice.setColumns(10);
		}
		{
			JLabel lblRe = new JLabel("Balance");
			lblRe.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblRe = new GridBagConstraints();
			gbc_lblRe.insets = new Insets(0, 0, 5, 5);
			gbc_lblRe.gridx = 4;
			gbc_lblRe.gridy = 8;
			contentPanel.add(lblRe, gbc_lblRe);
		}
		{
			JLabel lblNewLabel = new JLabel("Unknown");
			GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
			gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
			gbc_lblNewLabel.gridx = 5;
			gbc_lblNewLabel.gridy = 8;
			contentPanel.add(lblNewLabel, gbc_lblNewLabel);
		}
		{
			JButton btnBidFlow = new JButton("Bid Flow");
			btnBidFlow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			GridBagConstraints gbc_btnBidFlow = new GridBagConstraints();
			gbc_btnBidFlow.gridx = 5;
			gbc_btnBidFlow.gridy = 9;
			contentPanel.add(btnBidFlow, gbc_btnBidFlow);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton generateFlowButton = new JButton("Generate Flow (Test)");
				generateFlowButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						
					}
				});
				buttonPane.setLayout(new GridLayout(0, 2, 0, 0));
				generateFlowButton.setActionCommand("OK");
				buttonPane.add(generateFlowButton);
				getRootPane().setDefaultButton(generateFlowButton);
			}
			{
				JButton queryCondition = new JButton("Query Path Availability");
				queryCondition.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
					}
				});
				queryCondition.setActionCommand("Query");
				buttonPane.add(queryCondition);
			}
		}
	}

}
