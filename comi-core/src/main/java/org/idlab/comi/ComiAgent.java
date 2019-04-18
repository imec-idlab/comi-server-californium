package org.idlab.comi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.idlab.comi.model.*;

public class ComiAgent {
	public String c_serverAddress;
	public String c_panID;
	
	ComiClientICMPv6 comiClientICMPv6;
	ComiClient6TiSCH comiClient6TiSCH;
	ArrayList<Cell> currentCellList=null;
	ArrayList<Neighbor> currentNeighborList=null;
	ArrayList<Route> currentRouteList=null;
	
	public ComiAgent(String serverAddress, String panID) {
		c_serverAddress=serverAddress;
		c_panID=panID;
		comiClientICMPv6=new ComiClientICMPv6(c_serverAddress);
		comiClient6TiSCH=new ComiClient6TiSCH(c_serverAddress);
	}
	
	public boolean retrieveNodeSchedule() {
		System.out.println("Retrieving Cell List of " + c_serverAddress);
		currentCellList = comiClient6TiSCH.cellList.getCellList();
		if(currentCellList==null){
			System.out.println("Could not read Schedule List!");
			return false;
		}
		return true;
	}
	
	public ArrayList<Cell> readScheduleList() {
		return currentCellList;
	}
	
	public boolean retrieveNodeNeighbors() {
		System.out.println("Retrieving Neighbor List of " + c_serverAddress);
		currentNeighborList = comiClient6TiSCH.neighborList.getNeighborList();
		if(currentNeighborList==null){
			System.out.println("Could not read Neighbor Table!");
			return false;
		}
		return true;
	}
	
	public ArrayList<Neighbor> readNeighborList() {
		return currentNeighborList;
	}
	
	public boolean retrieveNodeRoutes() {
		System.out.println("Retrieving Routing Table of " + c_serverAddress);
		currentRouteList = comiClientICMPv6.routeList.getRouteList();
		if(currentRouteList==null){
			System.out.println("Could not read Routing Table!");
			return false;
		}
		return true;
	}

	public ArrayList<Route> readRouteList() {
		return currentRouteList;
	}
	
	
	public void setSchedule(int cellID, int slotOffset, int channelOffset, int linkopt, int nodeAddr) {
		byte[] linkoption= new byte[1];
		linkoption[0]=(byte)linkopt;
		byte[] nodeAddress= new byte[1];
		nodeAddress[0]=(byte)nodeAddr;
		Cell cell= new Cell((short)cellID,(short)1,(short)slotOffset,(short)channelOffset,linkoption,nodeAddress,(short)0,(short)0);
		comiClient6TiSCH.cellList.setCell(cell);
	}
		
	public ArrayList<Neighbor> observeNeighborList() {
		System.out.println("Observing Neighbor List of " + c_serverAddress);
		return comiClient6TiSCH.neighborList.observeNeighborList();
		//Neighbor neighbor=comiClient6TiSCH.neighborList.observeNeighbor((short)0);
	}
	
	public ArrayList<Route> observeRouteList() {
		System.out.println("Observing Route List of " + c_serverAddress);
		return comiClientICMPv6.routeList.observeRouteList();
		//Neighbor neighbor=comiClient6TiSCH.neighborList.observeNeighbor((short)0);
	}
	
}
