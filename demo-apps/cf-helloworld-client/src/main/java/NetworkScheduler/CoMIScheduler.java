package NetworkScheduler;

/**
 * This is the 6TiSCH Network Scheduler based on CoMI Network Manager Protocol.
 *  This platform is created by Abdulkadir Karaagac (abdulkadir.karaagac@ugent.be).
 * */

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import org.idlab.comi.*;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.server.resources.CoapExchange;

import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.awt.event.ActionEvent;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import java.awt.Dimension;
import javax.swing.border.BevelBorder;

public class CoMIScheduler {
	
	// IPv6 Address of Resource Directory which will be used to retrieve the CoMI Servers 
	private static String rdAddress="coap://[2001:6a8:1d80:1128:20d:b9ff:fe40:3114]"; 	
	// IPv6 Address of the Border Router (Necessary in order to configure via OpenVisualizer
	public String borderRouterAddress="[bbbb::1]"; 	
	public String borderRouterLLAddress="[fe80::1538]"; //defines the last byte of border router 	

	// Port for the CoAP Server for 6TiSCH Scheduler
	private static final int serverPort = 5687;

	
	private JFrame frame;
	private JTextArea textArea;
	private JTextField choffset;
	private JTextField cellid;
	
	private int numServer;	// Number of Servers discovered via RD and still reachable
	private ResourceDirectory resourceDirectory;
	private SchedulingEngine schedulingEngine;
	ArrayList<ComiAgent> ComiServers;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CoMIScheduler window = new CoMIScheduler();
					window.frame.setVisible(true);					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
 	    
	/**
	 * Create the CoMIScheduler application.
	 */
	public CoMIScheduler() {

		//Initializing Scheduling Coap Server at given UDP port
		CoapServer scheduleCoapServer = new CoapServer(serverPort);
		//Registering Schedule Resource which will be used in order to receive scheduling requests
		scheduleCoapServer.add(new ScheduleResource());
		//Starting CoAP Server
		scheduleCoapServer.start();
		//The list of CoMI enabled devices. They will be discovered via Resource Directory
		schedulingEngine= new SchedulingEngine(this);
		ComiServers=new ArrayList<ComiAgent>();	
		initialize();
	}
			
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 650, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel panel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(frame.getContentPane());
		groupLayout.setHorizontalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(groupLayout.createSequentialGroup()
					.addContainerGap()
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 612, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(26, Short.MAX_VALUE))
		);
		groupLayout.setVerticalGroup(
			groupLayout.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, groupLayout.createSequentialGroup()
					.addContainerGap(23, Short.MAX_VALUE)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 499, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		JScrollPane scrollPane = new JScrollPane();
		textArea = new JTextArea();
		textArea.setEditable(false);
			
		scrollPane.setViewportView(textArea);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		JPanel panel_2 = new JPanel();
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.TRAILING)
				.addGroup(Alignment.LEADING, gl_panel.createSequentialGroup()
					.addGap(41)
					.addGroup(gl_panel.createParallelGroup(Alignment.LEADING)
						.addComponent(panel_1, GroupLayout.DEFAULT_SIZE, 566, Short.MAX_VALUE)
						.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 534, GroupLayout.PREFERRED_SIZE)
						.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 554, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addGap(37)
					.addComponent(scrollPane, GroupLayout.PREFERRED_SIZE, 247, GroupLayout.PREFERRED_SIZE)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel_2, GroupLayout.PREFERRED_SIZE, 90, GroupLayout.PREFERRED_SIZE)
					.addGap(33)
					.addComponent(panel_1, GroupLayout.PREFERRED_SIZE, 62, GroupLayout.PREFERRED_SIZE)
					.addGap(24))
		);
		
		JLabel lblMoteAddress = new JLabel("Mote Address:");
		panel_2.add(lblMoteAddress);
		
		JComboBox<String> ServerList = new JComboBox<String>();
		ServerList.setPreferredSize(new Dimension(400, 24));
		ServerList.setMaximumSize(new Dimension(450, 50));
		ServerList.setMinimumSize(new Dimension(100, 24));
		ServerList.setEditable(true);
		panel_2.add(ServerList);
		
		//Discovering CoMI EndPoints
		textArea.setText(textArea.getText() + "CoMI Endpoints in your network!...\n");	
		discoverCoMIEndpoints();
		
		// Addition of CoMI Servers into Selection Panel
		for(int i=0; i<this.numServer;i++){
			textArea.setText(textArea.getText() + ComiServers.get(i).c_serverAddress + "\n");
			ServerList.addItem(ComiServers.get(i).c_serverAddress);
		}		
		
		// Schedules GET Button
		JButton btnGetSchedules = new JButton("GET Sch");
		panel_2.add(btnGetSchedules);
		btnGetSchedules.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComiServers.get(ServerList.getSelectedIndex()).retrieveNodeSchedule();
			}
		});	

		// Neighbors GET Button
		JButton buttonGetNeughborTable = new JButton("GET Neigh.");
		panel_2.add(buttonGetNeughborTable);
		buttonGetNeughborTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComiServers.get(ServerList.getSelectedIndex()).retrieveNodeNeighbors();
			}
		});

		// Neighbors Observe Button
		JButton buttonObserveNeighborTable = new JButton("OBS. Neigh.");
		panel_2.add(buttonObserveNeighborTable);
		buttonObserveNeighborTable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComiServers.get(ServerList.getSelectedIndex()).observeNeighborList();
			}
		});
		
		JButton btnGetRoutes = new JButton("GET Routes");
		panel_2.add(btnGetRoutes);

		btnGetRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ComiServers.get(ServerList.getSelectedIndex()).retrieveNodeRoutes();
			}
		});
				
		// Send the Schedule specified slotoffset and channeloffset
		JButton btnSetSch = new JButton("SET SCH");
		panel_2.add(btnSetSch);
		
		cellid = new JTextField();
		cellid.setMinimumSize(new Dimension(4, 10));
		panel_2.add(cellid);
		cellid.setColumns(10);

		JTextField slotoffset = new JTextField();
		panel_2.add(slotoffset);
		slotoffset.setColumns(10);
		
		choffset = new JTextField();
		panel_2.add(choffset);
		choffset.setColumns(10);
		
		btnSetSch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(!slotoffset.getText().isEmpty() && !choffset.getText().isEmpty()){
					ComiServers.get(ServerList.getSelectedIndex()).setSchedule(Integer.valueOf(cellid.getText()),Integer.valueOf(slotoffset.getText()), Integer.valueOf(choffset.getText()),128, 56);
				}
				else{
					System.out.println("Please specify the slotoffset and channeloffset that you want to add tx cell");
				}
			}
		});
		
		// Installing Pre-Calculated Schedules
		JButton btnScheduling = new JButton("SCHEDULE");
		btnScheduling.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				schedulingEngine.generateSchedulesForNode(ServerList.getSelectedIndex());
			}
		});
		panel_1.add(btnScheduling);

		panel.setLayout(gl_panel);
		frame.getContentPane().setLayout(groupLayout);
	}
	
	 /*
     * Definition of the Scheduling Resource
     */
    class ScheduleResource extends CoapResource {
        
        public ScheduleResource() {
            
            // set resource identifier
            super("scheduling");
            
            // set display name
            getAttributes().setTitle("6TiSCH-Scheduling Resource");
        }

        @Override
        public void handlePUT(CoapExchange exchange) {
            
        	String destination = exchange.getRequestText();
        	String message = "calculating schedule for: " + destination;
			System.out.println(message);
			
            // respond to the request
            exchange.respond(ResponseCode.CHANGED,message);  
        }
    }
    
	/** 
	 * Reading the Available CoMI Endpoints which have registered the specified Resource Directory
	 */
	private void discoverCoMIEndpoints(){
		this.resourceDirectory = new ResourceDirectory(rdAddress);
		InetSocketAddress[] serverList=this.resourceDirectory.getComiServers(); 	// Get CoMI Servers from the response of RD
		int numEndPoint=this.resourceDirectory.getLength(serverList);				// # of Addresses with CoMI Resources "/c"
		for(int i=0;i<numEndPoint;i++){

				// Checking if the endPoint is still reachable
				if(isEndpointReachable(serverList[i])){
					// Creating ComiAgents for each CoMI Server
					ComiServers.add(new ComiAgent("coap://["+serverList[i].getAddress().getHostAddress()+"]:"+serverList[i].getPort()+"/c","cafe"));
					this.numServer++;
					System.out.println("CoMI Agent created: "+serverList[i].getAddress().getHostAddress());
				}
				else{
					System.out.println("Destination is not Reachable: "+serverList[i].getAddress().getHostAddress());
				}

		}
		
		InetAddress borderRouter;
		try {
			borderRouter = InetAddress.getByName(borderRouterAddress); 
			if(borderRouter.isReachable(500)){
				ComiServers.add(new ComiAgent("coap://["+borderRouter.getHostAddress()+"]:5683/c","cafe")); 		// Addition of Border Router
				System.out.println("CoMI Agent created: "+borderRouter.getHostAddress());
				this.numServer++;
			}
			else{
				System.out.println("Border Router is not Reachable: "+borderRouter.getHostAddress());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}
	
	/** 
	 * Checking if the address is Reachable by sending a CoAP request to /c resource
	 */
	private boolean isEndpointReachable(InetSocketAddress serverAddress){
	
		CoapClient rdClient;
		// input URI from command line arguments
		URI rduri = null; // URI of resource directory
		try {
			rduri =  new URI("coap://["+serverAddress.getAddress().getHostAddress()+"]:"+serverAddress.getPort()+"/c");
		} catch (URISyntaxException f) {
			System.err.println("Invalid URI: " + f.getMessage());
			System.exit(-1);
		}
		
		//System.out.println("checking reachability"+rduri);
		rdClient = new CoapClient(rduri);
		rdClient.setTimeout(500);
		CoapResponse response = rdClient.get();
		
		if (response==null) {
		//	System.out.println("No response received.");
			return false;
		}
		return true;
	}
}


