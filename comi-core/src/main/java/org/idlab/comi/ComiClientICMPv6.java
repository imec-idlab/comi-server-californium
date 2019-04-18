package org.idlab.comi;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.Array;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.MajorType;
import org.idlab.comi.model.*;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;



public class ComiClientICMPv6{
	
	// SIDs
	public final int  SID_scheduler = 4000;
	public final int  SID_enabled = 4001;   

	public String c_serverAddress;
	public RouteList routeList;

	public ComiClientICMPv6(String serverAddress) {
		c_serverAddress=serverAddress;
		routeList = new RouteList();
	}	
	
/*
 * Route List
 * */	
	public class RouteList {
		private boolean isRouteObserving;
		private CoapObserveRelation routeObserver;
				
	    public RouteList() {

		}
		
	    public ArrayList<Route> getRouteList(){
			   				
			ArrayList<Route> routeList=new ArrayList<Route>();
			CoapResponse response = CoMIInterface.getResource( c_serverAddress, Route.SID_routetable);
			
			if (response!=null) {
				if(response.isSuccess()){
					byte[] payload = response.getPayload();
					ByteArrayInputStream bis=new ByteArrayInputStream(payload);
					//PARSING					
					CborDecoder cborDecoder = new CborDecoder(bis);
					List<DataItem> dataItems = null;
					try {
						dataItems = cborDecoder.decode();
					} catch (CborException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(dataItems.get(0).getMajorType()==MajorType.ARRAY){
						Array array = (Array) dataItems.get(0);
						List<DataItem> routeTableItem = array.getDataItems();

						java.util.ListIterator<DataItem> routeTableIterator = routeTableItem.listIterator();
						while(routeTableIterator.hasNext()){
							System.out.println(" ");
							Route route = new Route();
							route.deserializeRoute(routeTableIterator.next());
							routeList.add(route);
						}
					}
					else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						System.out.println(" ");
						Route route = new Route();
						route.deserializeRoute(dataItems.get(0));
						
						routeList.add(route);
					}									
				}
				else{
					System.out.println(response.getCode());
					return null;
				}
			} else {
				System.out.println("No response received.");
				return null;
			}
			return routeList;
		}
	    
	    public ArrayList<Route> observeRouteList() {
			
			ArrayList<Route> routeList=new ArrayList<Route>();


			if(isRouteObserving==false){
				URI uri = null;
				try {
					uri = new URI(c_serverAddress+"/"+CoMIInterface.getBase64(Route.SID_routetable));
				} catch (URISyntaxException f) {
					System.err.println("Invalid URI: " + f.getMessage());
					System.exit(-1);
				}
				CoapClient client = new CoapClient(uri);
				routeObserver=client.observe(new CoapHandler() {
					@Override
					public void onLoad(CoapResponse response) {
						if (response!=null) {
							if(response.isSuccess()){
								byte[] payload = response.getPayload();
								ByteArrayInputStream bis=new ByteArrayInputStream(payload);
								//PARSING					
								CborDecoder cborDecoder = new CborDecoder(bis);
								List<DataItem> dataItems = null;
								try {
									dataItems = cborDecoder.decode();
								} catch (CborException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								if(dataItems.get(0).getMajorType()==MajorType.ARRAY){
									Array array = (Array) dataItems.get(0);
									List<DataItem> routeListItem = array.getDataItems();

									java.util.ListIterator<DataItem> routeListIterator = routeListItem.listIterator();
									while(routeListIterator.hasNext()){
										System.out.println(" ");
										Route route = new Route();
										route.deserializeRoute(routeListIterator.next());
										routeList.add(route);
									}
								}
								else if(dataItems.get(0).getMajorType()==MajorType.MAP){
									System.out.println(" ");
									Route route = new Route();
									route.deserializeRoute(dataItems.get(0));
									routeList.add(route);
								}									

							}
							else{
								System.out.println(response.getCode());
							}
						}
					}

					@Override
					public void onError() {
						// TODO Auto-generated method stub

					}
				});
				isRouteObserving=true;
			}
			else{	
				System.out.println("Canceling Observing Route List of " + c_serverAddress);
				routeObserver.proactiveCancel();	
				isRouteObserving=false;
			}	
						
			return routeList;
		}
	    
	    
	    
	    public Route getRoute(short routeID){
				
	    	Route route=new Route();;
	    	String query=Integer.toString(routeID);
			CoapResponse response = CoMIInterface.getResource( c_serverAddress,  Route.SID_routetable,query);
			
			if (response!=null) {
				if(response.isSuccess()){
					byte[] payload = response.getPayload();
					ByteArrayInputStream bis=new ByteArrayInputStream(payload);
					
					//PARSING					
					CborDecoder cborDecoder = new CborDecoder(bis);
					List<DataItem> dataItems = null;
					try {
						dataItems = cborDecoder.decode();
					} catch (CborException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if(dataItems.get(0).getMajorType()==MajorType.ARRAY){
						System.err.println("wrong format: single route element expected, an array is received");
					}
					else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						System.out.println(" ");
						route.deserializeRoute(dataItems.get(0));
					}										
				}
				else{
					System.out.println(response.getCode());
				}
			} else {
				System.out.println("No response received.");
			}
			return route;
		}
	    		

	}
}


