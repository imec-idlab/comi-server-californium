package org.idlab.comi.model;

import java.math.BigInteger;
import java.util.Collection;

import org.idlab.cbor.model.ByteString;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.Map;
import org.idlab.cbor.model.UnsignedInteger;

public class Cell {

	public final static int  SID_celllist = 4002;
	public final static int  SID_cellid = 4003;
	public final static int  SID_slotframeid = 4004;
	public final static int  SID_slotoffset = 4005;
	public final static int  SID_channeloffset = 4006;
	public final static int  SID_linkoption = 4007;
	public final static int  SID_nodeaddress = 4008;
	public final static int  SID_statistics = 4009;
	public final static int  SID_cellage = 4010;

	public short cellID;
	public short slotFrameID;
	public short slotOffset;
	public short channelOffset;
	public byte[] linkoption;
	public byte[] nodeAddress;
	public int statistics;
	public int cellAge;	    

	public Cell(){
		
	}
	
	public Cell(short cell_ID,short slotFrame_ID,short slot_Offset,short channel_Offset,byte[] link_option,byte[] node_Address,int link_statistics,int cell_Age){
		cellID=cell_ID;
		slotFrameID=slotFrame_ID;
		slotOffset=slot_Offset;
		channelOffset=channel_Offset;
		linkoption=link_option;
		nodeAddress=node_Address;
		statistics=link_statistics;
		cellAge=cell_Age;		
	}
	
	public void deserializeCell(DataItem cellItem){
		Map cellMap = (Map) cellItem;
		Collection<DataItem> keyList = cellMap.getKeys();
		for(DataItem key : keyList){
			BigInteger id = ((org.idlab.cbor.model.Number)key).getValue();						
			switch(id.intValue()+SID_celllist){
			case SID_cellid:
				cellID=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("Cell ID: " + cellID);
				break;
			case SID_slotframeid:
				slotFrameID=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("SlotFrameID: " + slotFrameID);
				break;
			case SID_slotoffset:
				slotOffset=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("Slot Offset: " + slotOffset);
				break;
			case SID_channeloffset:
				channelOffset=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("Channel Offset: " + channelOffset);
				break;
			case SID_linkoption:
				linkoption=((org.idlab.cbor.model.ByteString)cellMap.get(key)).getBytes();
				System.out.println("Link Option: " + Integer.toBinaryString(linkoption[0]& 0xff));
				break;
			case SID_nodeaddress:
				nodeAddress=((org.idlab.cbor.model.ByteString)cellMap.get(key)).getBytes();
				if(nodeAddress.length>0){
					System.out.println("Node Address: " + Integer.toHexString(nodeAddress[0] & 0xff));
				}
				break;
			case SID_statistics:
				statistics=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("Statistics: " + statistics);
				break;
			case SID_cellage:
				cellAge=(((org.idlab.cbor.model.Number)cellMap.get(key)).getValue()).shortValue();
				System.out.println("Cell Age: " + cellAge);
				break;					
			default:
				break;
			}
		}
	}

	public Map serializeCell(){
		Map cellMap = new Map(8);

		UnsignedInteger id= new UnsignedInteger(SID_cellid-SID_celllist);
		UnsignedInteger value=new UnsignedInteger(cellID);
		cellMap.put(id, value);

		id= new UnsignedInteger(SID_slotframeid-SID_celllist);
		value=new UnsignedInteger(slotFrameID);
		cellMap.put(id, value);

		id= new UnsignedInteger(SID_slotoffset-SID_celllist);
		value=new UnsignedInteger(slotOffset);
		cellMap.put(id, value);

		id= new UnsignedInteger(SID_channeloffset-SID_celllist);
		value=new UnsignedInteger(channelOffset);
		cellMap.put(id, value);

		id= new UnsignedInteger(SID_linkoption-SID_celllist);
		ByteString linkOptionsItem=new ByteString(linkoption);
		cellMap.put(id, linkOptionsItem);

		id= new UnsignedInteger(SID_nodeaddress-SID_celllist);
		ByteString nodeAddressItem=new ByteString(nodeAddress);
		cellMap.put(id, nodeAddressItem);

//		id= new UnsignedInteger(SID_statistics-SID_celllist);
//		value=new UnsignedInteger(statistics);
//		cellMap.put(id, value);

//		id= new UnsignedInteger(SID_cellage-SID_celllist);
//		value=new UnsignedInteger(cellAge);
//		cellMap.put(id, value);			

		return cellMap;
	}


}