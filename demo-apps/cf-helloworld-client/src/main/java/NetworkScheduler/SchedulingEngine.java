package NetworkScheduler;

import java.util.ArrayList;
import java.util.Iterator;

import org.idlab.comi.ComiAgent;
import org.idlab.comi.model.Cell;
import org.idlab.comi.model.Route;

public class SchedulingEngine {
	CoMIScheduler comiScheduler;
	private static final int maxSlotOffset = 21;
	private static final short maxCell = 20;
	
	public SchedulingEngine(CoMIScheduler newComiScheduler) {
		comiScheduler=newComiScheduler;
	}
	
    public void generateSchedulesForNode(int nodeID){        	
		// Retrieving the path from source to border router
		ComiAgent hopSrc=comiScheduler.ComiServers.get(nodeID);
		String bbrLastByte=comiScheduler.borderRouterLLAddress.subSequence(comiScheduler.borderRouterLLAddress.indexOf(']')-2,comiScheduler.borderRouterLLAddress.indexOf(']')).toString();
		ArrayList<Route> srcRouteList;
		ArrayList<Cell> srcCellList;		
		
		if(!hopSrc.retrieveNodeRoutes()||!hopSrc.retrieveNodeSchedule()){
			return;
		}
		srcRouteList =hopSrc.readRouteList();
		srcCellList =hopSrc.readScheduleList();

		boolean isArriveBorderRouter=false;
		
		while(!isArriveBorderRouter){
			Iterator<ComiAgent> serversIterator=comiScheduler.ComiServers.iterator();
			String  destID = Integer.toHexString(srcRouteList.get(0).parentAddress[0] & 0xff);
			String  srcID=hopSrc.c_serverAddress.subSequence(hopSrc.c_serverAddress.indexOf(']')-2,hopSrc.c_serverAddress.indexOf(']')).toString();
			String  serverAddress="";
			ComiAgent hopDest = null;
			
			if(destID.equals(bbrLastByte)){
				isArriveBorderRouter=true;
				hopDest=comiScheduler.ComiServers.get(comiScheduler.ComiServers.size()-1);
			}
			else{
				while(!serverAddress.equals(destID) && serversIterator.hasNext()){
					hopDest=serversIterator.next();
					serverAddress = hopDest.c_serverAddress.subSequence(hopDest.c_serverAddress.indexOf(']')-2,hopDest.c_serverAddress.indexOf(']')).toString();
				}
				if(!serversIterator.hasNext()){
					System.out.println("An unknown next hop!");
					return;
				}
			}
			if(hopDest!=null){
				ArrayList<Cell> destCellList=null;
				
				if(!hopDest.retrieveNodeSchedule()){
					return;
				}
				destCellList =hopDest.readScheduleList();
				
				short firstAvailableSlot=getFirstAvailableSlot(srcCellList,destCellList);
				short srcAvailableCell=getAnAvailableCell(srcCellList);
				short destAvailableCell=getAnAvailableCell(destCellList);
				short channelOffset= (short)(Math.random()*15);
				byte[] emptyByte=null;
				if(firstAvailableSlot<maxSlotOffset && srcAvailableCell<maxCell && destAvailableCell<maxCell){							
					System.out.println("Installing schedule to "+ hopSrc.c_serverAddress+ " to cell "+ srcAvailableCell + " @slotoffset " + firstAvailableSlot + " @channelOffset " + channelOffset );
					hopSrc.setSchedule(srcAvailableCell,firstAvailableSlot,channelOffset,132,(Integer.parseInt(destID,16) & 0xff));
					Cell newSrcCell = new Cell(srcAvailableCell,(short)1,firstAvailableSlot, channelOffset,emptyByte,emptyByte,0,0);
					srcCellList.add(newSrcCell);
					
					System.out.println("Installing schedule to "+ hopDest.c_serverAddress+ " to cell "+ destAvailableCell + " @slotoffset " + firstAvailableSlot + " @channelOffset " + channelOffset );
					hopDest.setSchedule(destAvailableCell,firstAvailableSlot,channelOffset,68,(Integer.parseInt(srcID,16) & 0xff));
					Cell newDestCell = new Cell(destAvailableCell,(short)1,firstAvailableSlot, channelOffset,emptyByte,emptyByte,0,0);
					destCellList.add(newDestCell);
				}
				else{
					System.out.println("No available timeslot or cell: ");
					return;
				}
				
				hopSrc=hopDest;
				if(!hopDest.retrieveNodeRoutes()){
					return;
				}
				srcRouteList =hopDest.readRouteList();
				srcCellList =destCellList;
			}
			else{
				System.out.println("Destination Hop is Null!");
			}
		}
		/*
		ComiAgent borderRouterCoMIAgent = ComiServers.get(ComiServers.size()-1);		
			ComiServers.get(5).setSchedule(9,10,6,76,42); //76
			ComiServers.get(4).setSchedule(8,10,6,140,34); //140
			Thread.sleep(500);
			ComiServers.get(0).setSchedule(10,11,7,76,34);
			ComiServers.get(5).setSchedule(8,11,7,140,139);
			Thread.sleep(500);
			borderRouterCoMIAgent.setSchedule(8,14,2,76,139);
			ComiServers.get(0).setSchedule(8,14,2,140,56);
			Thread.sleep(500);
			borderRouterCoMIAgent.setSchedule(9,6,11,140,190);
			ComiServers.get(1).setSchedule(8,6,11,76,56);
		 */
	
    }
    
    public short getAnAvailableCell(ArrayList<Cell> cellList){      
    	short cellID=0;
    	Iterator<Cell> cellIterator = cellList.iterator();
    	while(cellIterator.hasNext()){
    		if(cellID < cellIterator.next().cellID)
    		{
    			return cellID;
    		}
    		cellID++;
    	}
    	return cellID;
    }
    
    public short getFirstAvailableSlot(ArrayList<Cell> sourceCellList,ArrayList<Cell> destCellList){      
    	short slotOffset=0;
    	while(slotOffset<maxSlotOffset){
        	Iterator<Cell> srcCellIterator = sourceCellList.iterator();
         	Iterator<Cell> destCellIterator = destCellList.iterator();
	    	while(srcCellIterator.hasNext()){
	    		if(slotOffset==srcCellIterator.next().slotOffset)
	    		{
	    			slotOffset++;
	    			break;
	    		}
	    	}
	    	while(destCellIterator.hasNext()){
	    		if(slotOffset==destCellIterator.next().slotOffset)
	    		{
	    			slotOffset++;
	    			break;
	    		}
	    	}
	    	if(!srcCellIterator.hasNext()&&!destCellIterator.hasNext()){
	    		return slotOffset;
	    	}
    	}
    	return slotOffset;
    }
    
    
}
