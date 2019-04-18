package org.idlab.comi.model;

import java.math.BigInteger;
import java.util.Collection;
import org.idlab.cbor.model.DataItem;
import org.idlab.cbor.model.Map;

public class Route {

	public final static int  SID_routetable= 4021;
	public final static int  SID_routeid = 4022;
	public final static int  SID_parentaddr = 4023;
	public final static int  SID_dagrank = 4024;
	public final static int  SID_rankrincrease = 4025;
	public final static int  SID_routeoption = 4026;

    public short routeID;
    public byte[] parentAddress;
    public long dagRank;
    public long rankIncrease;
    public byte[] routeOption;


	public Route(){
		
	}
	
	public Route(short route_ID,byte[] parent_Address,long dag_Rank,long rank_Increase,byte[] route_Option){
		routeID=route_ID;
		parentAddress=parent_Address;
		dagRank=dag_Rank;
		rankIncrease=rank_Increase;
		routeOption=route_Option;
	}
	
	public void deserializeRoute(DataItem routeItem){
		
		Map routeMap = (Map) routeItem;
		Collection<DataItem> keyList = routeMap.getKeys();
		for(DataItem key : keyList){
			BigInteger id = ((org.idlab.cbor.model.Number)key).getValue();						
			switch(id.intValue()+SID_routetable){
				case SID_routeid:
					routeID=(((org.idlab.cbor.model.Number)routeMap.get(key)).getValue()).shortValue();
					System.out.println("Route ID: " + routeID);
					break;
				case SID_parentaddr:
					parentAddress=((org.idlab.cbor.model.ByteString)routeMap.get(key)).getBytes();
					System.out.println("Parent address: " + (Integer.toHexString(parentAddress[0]& 0xff)));
					break;
				case SID_dagrank:
					dagRank=(((org.idlab.cbor.model.Number)routeMap.get(key)).getValue()).shortValue();
					System.out.println("Dag rank: " + dagRank);
					break;
				case SID_rankrincrease:
					rankIncrease=(((org.idlab.cbor.model.Number)routeMap.get(key)).getValue()).shortValue();
					System.out.println("Rank increase: " + rankIncrease);
					break;
				case SID_routeoption:
					routeOption=((org.idlab.cbor.model.ByteString)routeMap.get(key)).getBytes();
					System.out.println("Route option: " + (Integer.toHexString(routeOption[0]& 0xff)));
					break;
				default:
					break;
			}
		}
		return;
	}

}