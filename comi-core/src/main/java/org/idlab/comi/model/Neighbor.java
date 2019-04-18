package org.idlab.comi.model;

import java.math.BigInteger;
import java.util.Collection;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.Map;

public class Neighbor {

	public final static int  SID_neighborlist = 4011;
	public final static int  SID_neighborid = 4012;
	public final static int  SID_neighborAddr = 4013;
	public final static int  SID_rssi = 4014;
	public final static int  SID_lqi = 4015;
	public final static int  SID_neighborage = 4016;

    public short neighborID;
    public byte[] nodeAddress;
    public short rssi;
    public short linkQ;
    public int neighborAge;


	public Neighbor(){
		
	}
	
	public Neighbor(short neighbor_ID,byte[] node_Address,short rssi_value,short link_Q,int neighbor_Age){
		neighborID=neighbor_ID;
		nodeAddress=node_Address;
		rssi=rssi_value;
		linkQ=link_Q;
		neighborAge=neighbor_Age;
	}
	
	public void deserializeNeighbor(DataItem neighborItem){
		
		Map neighborMap = (Map) neighborItem;
		Collection<DataItem> keyList = neighborMap.getKeys();
		for(DataItem key : keyList){
			BigInteger id = ((org.idlab.cbor.model.Number)key).getValue();						
			switch(id.intValue()+SID_neighborlist){
				case SID_neighborid:
					neighborID=(((org.idlab.cbor.model.Number)neighborMap.get(key)).getValue()).shortValue();
					System.out.println("Neighbor ID: " + neighborID);
					break;
				case SID_neighborAddr:
					nodeAddress=((org.idlab.cbor.model.ByteString)neighborMap.get(key)).getBytes();
					System.out.println("Neighbor address: " + (Integer.toHexString(nodeAddress[0]& 0xff)));
					break;
				case SID_rssi:
					rssi=(((org.idlab.cbor.model.Number)neighborMap.get(key)).getValue()).shortValue();
					System.out.println("Neighbor rssi: " + rssi);
					break;
				case SID_lqi:
					linkQ=(((org.idlab.cbor.model.Number)neighborMap.get(key)).getValue()).shortValue();
					System.out.println("Neighbor lqi: " + linkQ);
					break;
				case SID_neighborage:
					neighborAge=(((org.idlab.cbor.model.Number)neighborMap.get(key)).getValue()).intValue();
					System.out.println("Neighbor age: " + neighborAge);
					break;
				default:
					break;
			}
		}
		return;
	}

}