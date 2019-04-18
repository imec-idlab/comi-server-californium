package org.idlab.comi;

import org.idlab.cbor.CborDecoder;
import org.idlab.cbor.CborEncoder;
import org.idlab.cbor.CborException;
import org.idlab.cbor.model.Array;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.MajorType;
import org.idlab.cbor.model.Map;
import org.idlab.comi.model.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;



public class ComiClient6TiSCH{
	
	// SIDs
	public final int  SID_scheduler = 4000;
	public final int  SID_enabled = 4001;   

	public String c_serverAddress;
	public Scheduler scheduler;
	public NeighborList neighborList;
	public CellList cellList;
	
	public ComiClient6TiSCH(String serverAddress) {
		c_serverAddress=serverAddress;
		scheduler = new Scheduler();
		neighborList = new NeighborList();
		cellList = new CellList();
	}	
	
	public class Scheduler {
		
		public Scheduler() {

		}
		
		public boolean getEnabled(){
			return true;
		} 

		public boolean setEnabled(boolean value){
			return true;
		} 
	}

	public class CellList {
		
		public ArrayList<Cell> getCellList(){
				
			ArrayList<Cell> cellList=new ArrayList<Cell>();
			CoapResponse response = CoMIInterface.getResource( c_serverAddress, Cell.SID_celllist);
			
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
						List<DataItem> cellListItem = array.getDataItems();

						java.util.ListIterator<DataItem> cellListIterator = cellListItem.listIterator();
						while(cellListIterator.hasNext()){
							System.out.println(" ");
							Cell cell = new Cell();
							cell.deserializeCell(cellListIterator.next());
							cellList.add(cell);
						}
					}
					else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						System.out.println(" ");
						Cell cell = new Cell();
						cell.deserializeCell(dataItems.get(0));
						cellList.add(cell);
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
			return cellList;
		}
		
		public void setCellList(boolean value){
			//ArrayList<Cell> cellList = new ArrayList(10);
		} 
		
		public Cell getCell(short cellID){
			 Cell cell=new Cell();
			 String query=Integer.toString(cellID);
			 CoapResponse response = CoMIInterface.getResource( c_serverAddress, Cell.SID_celllist,query);
				
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
						 System.err.println("wrong format: single neighor element expected, an array is received");
					 }
					 else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						 System.out.println(" ");
						 cell.deserializeCell(dataItems.get(0));
						}										
					}
					else{
						System.out.println(response.getCode());
					}
				} else {
					System.out.println("No response received.");
				}
				return cell;
			}
		    

		public void setCell(Cell cell){

			 String query=Integer.toString(cell.cellID);
			 ByteArrayOutputStream bos=new ByteArrayOutputStream();
			 CborEncoder cborEncoder = new CborEncoder(bos);
			 Map cellMap=cell.serializeCell();
			 try {
				cborEncoder.encode(cellMap);
			} catch (CborException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 
			 CoapResponse response = CoMIInterface.putResource( c_serverAddress,bos.toByteArray(), Cell.SID_celllist,query);
				
			 if (response!=null) {
				 if(response.isSuccess()){
					System.out.println("response:" + response.getCode());
				} else {
					System.out.println("response:" + response.getCode());
				}
			 }else{
					System.out.println("No response received.");
			 }
		} 
		
		public boolean getCellId(){
			return true;
		} 

		public void setCellId(boolean value){
			
		} 
		
		public boolean getSlotFrameId(){
			return true;
		} 

		public void setSlotFrameId(boolean value){
			
		} 
	    
		
		public boolean getSlotOffset(){
			return true;
		} 

		public void setSlotOffset(boolean value){
			
		} 
		
		public boolean getChannelOffset(){
			return true;
		} 

		public void setChannelOffset(boolean value){
			
		} 
		
		public boolean getLinkOption(){
			return true;
		} 

		public void setLinkOption(boolean value){
			
		} 
		
		public boolean getNodeAddress(){
			return true;
		} 

		public void setNodeAddress(boolean value){
			
		} 
		
		public boolean getStatistics(){
			return true;
		} 

		public boolean getCellAge(){
			return true;
		} 			
	}

	
	
/*
 * Neighbor List
 * */	
	public class NeighborList {
		private boolean isNeighborObserving;
		private CoapObserveRelation neighborObserver;
		
	    public NeighborList() {

		}
		
	    public ArrayList<Neighbor> getNeighborList(){
			   				
			ArrayList<Neighbor> neighborList=new ArrayList<Neighbor>();
			CoapResponse response = CoMIInterface.getResource( c_serverAddress, Neighbor.SID_neighborlist);
			
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
						List<DataItem> neighborListItem = array.getDataItems();

						java.util.ListIterator<DataItem> neighborListIterator = neighborListItem.listIterator();
						while(neighborListIterator.hasNext()){
							System.out.println(" ");
							Neighbor neighbor = new Neighbor();
							neighbor.deserializeNeighbor(neighborListIterator.next());
							neighborList.add(neighbor);
						}
					}
					else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						System.out.println(" ");
						Neighbor neighbor = new Neighbor();
						neighbor.deserializeNeighbor(dataItems.get(0));
						
						neighborList.add(neighbor);
					}									
					//cbor_handler.processPayload(payload, SID_neighborlist);		
	
				}
				else{
					System.out.println(response.getCode());
					return null;
				}
			} else {
				System.out.println("No response received.");
				return null;
			}
			return neighborList;
		}
	    
		public ArrayList<Neighbor> observeNeighborList() {
			
			ArrayList<Neighbor> neighborList=new ArrayList<Neighbor>();


			if(isNeighborObserving==false){
				URI uri = null;
				try {
					uri = new URI(c_serverAddress+"/"+CoMIInterface.getBase64(Neighbor.SID_neighborlist));
				} catch (URISyntaxException f) {
					System.err.println("Invalid URI: " + f.getMessage());
					System.exit(-1);
				}
				CoapClient client = new CoapClient(uri);
				neighborObserver=client.observe(new CoapHandler() {
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
									List<DataItem> neighborListItem = array.getDataItems();

									java.util.ListIterator<DataItem> neighborListIterator = neighborListItem.listIterator();
									while(neighborListIterator.hasNext()){
										System.out.println(" ");
										Neighbor neighbor = new Neighbor();
										neighbor.deserializeNeighbor(neighborListIterator.next());
										neighborList.add(neighbor);
									}
								}
								else if(dataItems.get(0).getMajorType()==MajorType.MAP){
									System.out.println(" ");
									Neighbor neighbor = new Neighbor();
									neighbor.deserializeNeighbor(dataItems.get(0));
									neighborList.add(neighbor);
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
				isNeighborObserving=true;
			}
			else{	
				System.out.println("Canceling Observing Neighbor List of " + c_serverAddress);
				neighborObserver.proactiveCancel();	
				isNeighborObserving=false;
			}	
						
			return neighborList;
		}

	    
	    public Neighbor getNeighbor(short neighborID){
				
	    	Neighbor neighbor=new Neighbor();;
	    	String query=Integer.toString(neighborID);
			CoapResponse response = CoMIInterface.getResource( c_serverAddress, Neighbor.SID_neighborlist,query);
			
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
						System.err.println("wrong format: single neighor element expected, an array is received");
					}
					else if(dataItems.get(0).getMajorType()==MajorType.MAP){
						System.out.println(" ");
						neighbor.deserializeNeighbor(dataItems.get(0));
					}										
				}
				else{
					System.out.println(response.getCode());
				}
			} else {
				System.out.println("No response received.");
			}
			return neighbor;
		}
	}
}
