package net.floodlightcontroller.datacentermarketing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.swing.JTextArea;
import javax.swing.JList;

import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.Main;
import net.floodlightcontroller.devicemanager.IDevice;
import net.floodlightcontroller.devicemanager.internal.Device;
import net.floodlightcontroller.devicemanager.internal.DeviceManagerImpl;
import net.floodlightcontroller.routing.Route;



public class FlowUI extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField sourceHostInput;
	private JTextField destHostInput;
	private JTextField bandwidthInput;
	private JTextField delayInput;
	private JTextField inputBidPrice;
	private JTextArea hostsTextArea;
	private JTextArea switchesTextArea;
	private JTextArea availablePathsTextArea;
	
	private MarketManager marketManager;
	
	//maintain a local hashset: name - switch
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			FlowUI dialog = new FlowUI();
			dialog.setTitle("Datacenter Control Panel");
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MarketManager getMarketManager() {
		return marketManager;
	}

	public void setMarketManager(MarketManager marketManager) {
		this.marketManager = marketManager;
	}
	
	public void updateHostsTextArea(){
		
		//print all devices
		marketManager.updateDevices();
		
		//manipulate from marketManager
		Map<Long, IDevice> devices = marketManager.getDevices();
		Set<Entry<Long, IDevice>> set = devices.entrySet();
		Iterator<Entry<Long,IDevice>> iterator = set.iterator();
		while(iterator.hasNext()){
			IDevice device = iterator.next().getValue();
			hostsTextArea.append(device.toString() + "\n\n");
		}
	}
	
	public void updateSwitchesTextArea() throws IOException, InterruptedException, ExecutionException{
		//refresh: get the switches and host information
		
		marketManager.updateSwitches();
		Map<Long, IOFSwitch> switches = marketManager.getSwitches();
		Set<Entry<Long, IOFSwitch>> s = switches.entrySet();
		Iterator<Entry<Long, IOFSwitch>> it = s.iterator();
		while(it.hasNext()){
			Entry<Long, IOFSwitch> entry = it.next();
			IOFSwitch ofSwitch = entry.getValue();
			Long longID = entry.getKey();
			switchesTextArea.append(longID + ": " + ofSwitch.getStringId() + "\n");
		}
		
		return;
		
	}
	
	
	/**
	 * Create the dialog.
	 */
	public FlowUI() {
		setBounds(100, 100, 982, 1206);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.WEST);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{35, 175, 204, 0, 126, 202, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{65, 0, 19, 0, 0, 0, 0, 0, 0, 0, 0, 0, 188, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblDatacenterMarketingControl = new JLabel("Control Panel");
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
			gbc_sourceHostInput.fill = GridBagConstraints.HORIZONTAL;
			gbc_sourceHostInput.insets = new Insets(0, 0, 5, 5);
			gbc_sourceHostInput.gridx = 2;
			gbc_sourceHostInput.gridy = 2;
			contentPanel.add(sourceHostInput, gbc_sourceHostInput);
			sourceHostInput.setColumns(10);
		}
		{
			JLabel lblDestinationHost = new JLabel("Dest Host");
			lblDestinationHost.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblDestinationHost = new GridBagConstraints();
			gbc_lblDestinationHost.insets = new Insets(0, 0, 5, 5);
			gbc_lblDestinationHost.gridx = 4;
			gbc_lblDestinationHost.gridy = 2;
			contentPanel.add(lblDestinationHost, gbc_lblDestinationHost);
		}
		{
			destHostInput = new JTextField();
			GridBagConstraints gbc_destHostInput = new GridBagConstraints();
			gbc_destHostInput.fill = GridBagConstraints.HORIZONTAL;
			gbc_destHostInput.insets = new Insets(0, 0, 5, 5);
			gbc_destHostInput.gridx = 5;
			gbc_destHostInput.gridy = 2;
			contentPanel.add(destHostInput, gbc_destHostInput);
			destHostInput.setColumns(10);
		}
		{
			JLabel lblNetworkElements = new JLabel("Network Elements");
			lblNetworkElements.setForeground(Color.RED);
			lblNetworkElements.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			GridBagConstraints gbc_lblNetworkElements = new GridBagConstraints();
			gbc_lblNetworkElements.anchor = GridBagConstraints.WEST;
			gbc_lblNetworkElements.insets = new Insets(0, 0, 5, 5);
			gbc_lblNetworkElements.gridx = 1;
			gbc_lblNetworkElements.gridy = 3;
			contentPanel.add(lblNetworkElements, gbc_lblNetworkElements);
		}
		{
			JLabel lblHosts = new JLabel("Hosts");
			lblHosts.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblHosts = new GridBagConstraints();
			gbc_lblHosts.insets = new Insets(0, 0, 5, 5);
			gbc_lblHosts.gridx = 1;
			gbc_lblHosts.gridy = 4;
			contentPanel.add(lblHosts, gbc_lblHosts);
		}
		{
			hostsTextArea = new JTextArea();
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			hostsTextArea.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			GridBagConstraints gbc_hostsTextArea = new GridBagConstraints();
			gbc_hostsTextArea.gridheight = 7;
			gbc_hostsTextArea.insets = new Insets(0, 0, 5, 5);
			gbc_hostsTextArea.fill = GridBagConstraints.BOTH;
			gbc_hostsTextArea.gridx = 2;
			gbc_hostsTextArea.gridy = 4;
			JScrollPane scroll = new JScrollPane (hostsTextArea);
			contentPanel.add(scroll, gbc_hostsTextArea);
		}
		{
			JLabel lblSwitches = new JLabel("Switches");
			lblSwitches.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblSwitches = new GridBagConstraints();
			gbc_lblSwitches.insets = new Insets(0, 0, 5, 5);
			gbc_lblSwitches.gridx = 4;
			gbc_lblSwitches.gridy = 4;
			contentPanel.add(lblSwitches, gbc_lblSwitches);
		}
		{
			
			
			switchesTextArea = new JTextArea();
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			switchesTextArea.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			GridBagConstraints gbc_switchesTextArea = new GridBagConstraints();
			gbc_switchesTextArea.gridheight = 7;
			gbc_switchesTextArea.insets = new Insets(0, 0, 5, 5);
			gbc_switchesTextArea.fill = GridBagConstraints.BOTH;
			gbc_switchesTextArea.gridx = 5;
			gbc_switchesTextArea.gridy = 4;
			contentPanel.add(switchesTextArea, gbc_switchesTextArea);
		}
		{
			JLabel lblNetworkAvailability = new JLabel("Path Availability");
			lblNetworkAvailability.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			lblNetworkAvailability.setForeground(Color.RED);
			GridBagConstraints gbc_lblNetworkAvailability = new GridBagConstraints();
			gbc_lblNetworkAvailability.anchor = GridBagConstraints.WEST;
			gbc_lblNetworkAvailability.insets = new Insets(0, 0, 5, 5);
			gbc_lblNetworkAvailability.gridx = 1;
			gbc_lblNetworkAvailability.gridy = 11;
			contentPanel.add(lblNetworkAvailability, gbc_lblNetworkAvailability);
		}
		{
			availablePathsTextArea = new JTextArea();
			availablePathsTextArea.setLineWrap(true);
			GridBagConstraints gbc_availablePathsText = new GridBagConstraints();
			Border border = BorderFactory.createLineBorder(Color.BLACK);
			availablePathsTextArea.setBorder(BorderFactory.createCompoundBorder(border, 
			            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
			gbc_availablePathsText.gridwidth = 5;
			gbc_availablePathsText.insets = new Insets(0, 0, 5, 5);
			gbc_availablePathsText.fill = GridBagConstraints.BOTH;
			gbc_availablePathsText.gridx = 1;
			gbc_availablePathsText.gridy = 12;
			contentPanel.add(availablePathsTextArea, gbc_availablePathsText);
		}
		{
			JLabel lblRequirements = new JLabel("Requirements:");
			lblRequirements.setForeground(Color.RED);
			lblRequirements.setFont(new Font("DejaVu Sans Condensed", Font.BOLD | Font.ITALIC, 14));
			GridBagConstraints gbc_lblRequirements = new GridBagConstraints();
			gbc_lblRequirements.anchor = GridBagConstraints.WEST;
			gbc_lblRequirements.insets = new Insets(0, 0, 5, 5);
			gbc_lblRequirements.gridx = 1;
			gbc_lblRequirements.gridy = 13;
			contentPanel.add(lblRequirements, gbc_lblRequirements);
		}
		{
			JLabel lblBandwidth = new JLabel("Bandwidth ");
			lblBandwidth.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblBandwidth = new GridBagConstraints();
			gbc_lblBandwidth.anchor = GridBagConstraints.SOUTH;
			gbc_lblBandwidth.insets = new Insets(0, 0, 5, 5);
			gbc_lblBandwidth.gridx = 1;
			gbc_lblBandwidth.gridy = 14;
			contentPanel.add(lblBandwidth, gbc_lblBandwidth);
		}
		{
			bandwidthInput = new JTextField();
			GridBagConstraints gbc_bandwidthInput = new GridBagConstraints();
			gbc_bandwidthInput.fill = GridBagConstraints.HORIZONTAL;
			gbc_bandwidthInput.insets = new Insets(0, 0, 5, 5);
			gbc_bandwidthInput.gridx = 2;
			gbc_bandwidthInput.gridy = 14;
			contentPanel.add(bandwidthInput, gbc_bandwidthInput);
			bandwidthInput.setColumns(10);
		}
		{
			JLabel lblDelayWithin = new JLabel("Delay");
			lblDelayWithin.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblDelayWithin = new GridBagConstraints();
			gbc_lblDelayWithin.insets = new Insets(0, 0, 5, 5);
			gbc_lblDelayWithin.gridx = 4;
			gbc_lblDelayWithin.gridy = 14;
			contentPanel.add(lblDelayWithin, gbc_lblDelayWithin);
		}
		{
			delayInput = new JTextField();
			GridBagConstraints gbc_delayInput = new GridBagConstraints();
			gbc_delayInput.insets = new Insets(0, 0, 5, 5);
			gbc_delayInput.fill = GridBagConstraints.HORIZONTAL;
			gbc_delayInput.gridx = 5;
			gbc_delayInput.gridy = 14;
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
			gbc_lblBid.gridy = 15;
			contentPanel.add(lblBid, gbc_lblBid);
		}
		{
			JLabel lblBidPrice = new JLabel("Bid Price");
			lblBidPrice.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblBidPrice = new GridBagConstraints();
			gbc_lblBidPrice.insets = new Insets(0, 0, 5, 5);
			gbc_lblBidPrice.gridx = 1;
			gbc_lblBidPrice.gridy = 16;
			contentPanel.add(lblBidPrice, gbc_lblBidPrice);
		}
		{
			inputBidPrice = new JTextField();
			GridBagConstraints gbc_inputBidPrice = new GridBagConstraints();
			gbc_inputBidPrice.insets = new Insets(0, 0, 5, 5);
			gbc_inputBidPrice.fill = GridBagConstraints.HORIZONTAL;
			gbc_inputBidPrice.gridx = 2;
			gbc_inputBidPrice.gridy = 16;
			contentPanel.add(inputBidPrice, gbc_inputBidPrice);
			inputBidPrice.setColumns(10);
		}
		{
			JLabel lblRe = new JLabel("Balance");
			lblRe.setFont(new Font("DejaVu Serif", Font.BOLD, 13));
			GridBagConstraints gbc_lblRe = new GridBagConstraints();
			gbc_lblRe.insets = new Insets(0, 0, 5, 5);
			gbc_lblRe.gridx = 4;
			gbc_lblRe.gridy = 16;
			contentPanel.add(lblRe, gbc_lblRe);
		}
		{
			JLabel CurrentBalance = new JLabel("Unknown");
			GridBagConstraints gbc_CurrentBalance = new GridBagConstraints();
			gbc_CurrentBalance.insets = new Insets(0, 0, 5, 5);
			gbc_CurrentBalance.gridx = 5;
			gbc_CurrentBalance.gridy = 16;
			contentPanel.add(CurrentBalance, gbc_CurrentBalance);
		}
		{
			JButton btnBidFlow = new JButton("Bid Flow");
			btnBidFlow.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
				}
			});
			{
				JButton btnGetRoutes = new JButton("Get routes");
				btnGetRoutes.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent arg0) {
						
						
						availablePathsTextArea.setText("");
						
						//get the input 
						String sourceString = sourceHostInput.getText();
						String destString = destHostInput.getText();
						if(sourceString.length() == 0 || destString.length() == 0)
							return;
						long sourceID = Long.parseLong(sourceString);
						long destID = Long.parseLong(destString);
						
						ArrayList<Route> routes = null;
						try {
							routes = marketManager.getNonLoopPaths(sourceID, destID);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						//show them!
						for(Route route : routes){
							availablePathsTextArea.append(route.toString() + "\n");
						}
						
						
					}
				});
				
				GridBagConstraints gbc_btnGetRoutes = new GridBagConstraints();
				gbc_btnGetRoutes.fill = GridBagConstraints.HORIZONTAL;
				gbc_btnGetRoutes.insets = new Insets(0, 0, 5, 0);
				gbc_btnGetRoutes.gridx = 6;
				gbc_btnGetRoutes.gridy = 16;
				contentPanel.add(btnGetRoutes, gbc_btnGetRoutes);
			}
			GridBagConstraints gbc_btnBidFlow = new GridBagConstraints();
			gbc_btnBidFlow.anchor = GridBagConstraints.EAST;
			gbc_btnBidFlow.insets = new Insets(0, 0, 0, 5);
			gbc_btnBidFlow.gridx = 2;
			gbc_btnBidFlow.gridy = 17;
			contentPanel.add(btnBidFlow, gbc_btnBidFlow);
		}
		{
			JButton btnRefresh = new JButton("Refresh");
			btnRefresh.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					
					hostsTextArea.setText("");
					switchesTextArea.setText("");
					
					updateHostsTextArea();
					try {
						updateSwitchesTextArea();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					//System.out.println("abc");
				}
			});
			GridBagConstraints gbc_btnRefresh = new GridBagConstraints();
			gbc_btnRefresh.gridx = 6;
			gbc_btnRefresh.gridy = 17;
			contentPanel.add(btnRefresh, gbc_btnRefresh);
		}
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton generateFlowButton = new JButton("Generate Flow (Test)");
				generateFlowButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						System.out.println(sourceHostInput.getText() + " " + destHostInput.getText());
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
