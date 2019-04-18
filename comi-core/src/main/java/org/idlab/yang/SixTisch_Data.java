package org.idlab.yang;

public class SixTisch_Data {
	

	public static int maxCellPerNode=20;
	private static int maxNeighborPerNode=10;
	
	private Scheduler scheduler;
	private NeighborList neighborList;
	
	
	public SixTisch_Data() {
		scheduler=new Scheduler();
		neighborList=new NeighborList();
	}
	
	public class Scheduler {
		
	    public YangModule.Leaf<Boolean> enabled;
	    public Cell[] cellList;
		
	    public Scheduler() {
			cellList=new Cell[maxCellPerNode];
			enabled=new YangModule.Leaf<Boolean>(true) ;
		}
	    
		class Cell {
		    public YangModule.Leaf<Short> slotFrameID;
		    public YangModule.Leaf<Short> slotOffset;
		    public YangModule.Leaf<Short> channelOffset;
		    public YangModule.Leaf<YangModule.Bits> linkoption;
		    public YangModule.Leaf<Byte> nodeAddress;
		    public YangModule.Leaf<Integer> stats;
		    public YangModule.Leaf<Integer> cellAge;	    
		}
	}
		
	
	public class NeighborList {
		
	    public YangModule.Leaf<Boolean> enabled;
	    public Neighbor[] neighborList;
		
	    public NeighborList() {
	    	neighborList=new Neighbor[maxNeighborPerNode];
		}
	    
		class Neighbor {
		    public YangModule.Leaf<Short> neighborID;
		    public YangModule.Leaf<Byte> nodeAddress;
		    public YangModule.Leaf<Short> rssi;
		    public YangModule.Leaf<Short> linkQ;
		    public YangModule.Leaf<Integer> neighborAge;
		}	
	}
	

}
