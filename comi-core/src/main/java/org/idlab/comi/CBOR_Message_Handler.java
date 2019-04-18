package org.idlab.comi;

public class CBOR_Message_Handler {
	public static byte CBOR_UINT = 0x0;
	public static byte CBOR_NEGINT = 0x2;
	public static byte CBOR_BYTES = 0x4;
	public static byte CBOR_TEXT = 0x6;
	public static byte CBOR_ARRAY = 0x8;
	public static byte CBOR_MAP = 0xA;
	public static byte CBOR_TAG = 0xC;
	public static byte CBOR_7 = 0xE;

	public static byte CBOR_UINT8_FOLLOWS = 0x18;
	public static byte CBOR_UINT16_FOLLOWS = 0x19;
	
	
	public int getSize(byte payload[]){
			if(((payload[0]>>4) & 0x0e) == CBOR_ARRAY){
				int arraysize=(payload[0] & 0x1f);
				return arraysize;
			}
			else if(((payload[0]>>4) & 0x0e) == CBOR_MAP){
				int mapsize=(payload[0] & 0x1f);
				return mapsize;
			}
			return 1;
	}
	
	public int getNextObject(byte payload[]){
		if(((payload[0]>>4) & 0x0e) == CBOR_ARRAY){
			int arraysize=(payload[0] & 0x1f);
			return arraysize;
		}
		else if(((payload[0]>>4) & 0x0e) == CBOR_MAP){
			int mapsize=(payload[0] & 0x1f);
			return mapsize;
		}
		return 1;
}
	
	
	
	public void processPayload(byte payload[], int baseSID){
		int numObject;
		for(int curser=0;curser<payload.length;){
			if(((payload[curser]>>4) & 0x0e) == CBOR_ARRAY){
				int arraysize=(payload[curser] & 0x1f);
				 System.out.println("[");
				 curser++;
				 curser=processArray( payload, arraysize,  curser,  baseSID);
				 System.out.println("]");
			}
			else if(((payload[curser]>>4) & 0x0e) == CBOR_MAP){
				int mapsize=(payload[curser] & 0x1f);
				 System.out.println("{");
				 curser++;
				 curser=processMap(payload,mapsize, curser,baseSID);
				 System.out.println("}");
			}
			else{
				curser=processLeaf(payload,curser,baseSID);
			}
		}
	}
	
	private int processArray(byte payload[],int numElement, int curser, int baseSID){
		int mycurser;
		int elementCounter=0;
		while(elementCounter<numElement){
			if(((payload[curser]>>4) & 0x0e) == CBOR_ARRAY){
				int arraysize=(payload[curser] & 0x1f);
				 System.out.println("[");
				 curser++;
				 curser=processArray(payload,arraysize,curser,baseSID);
				 System.out.println("]");
			}
			else if(((payload[curser]>>4) & 0x0e) == CBOR_MAP){
				int mapsize=(payload[curser] & 0x1f);
				 System.out.println("{");
				 curser++;
				 curser=processMap(payload,mapsize,curser,baseSID);
				 System.out.println("}");
			}
			else{
				curser=processLeaf(payload,curser,baseSID);
			}	
			elementCounter++;
		}
		
		mycurser=curser;
		return mycurser;
	}
	
	
	private int processMap(byte payload[],int numElement, int curser, int baseSID){
		int mycurser;
		int elementCounter=0;
		while(elementCounter<numElement){
			if(((payload[curser]>>4) & 0x0e) == CBOR_ARRAY){
				int arraysize=(payload[curser] & 0x1f);
				 System.out.println("[");
				 curser++;
				 curser=processArray(payload,arraysize,curser,baseSID);
				 System.out.println("]");
			}
			else if(((payload[curser]>>4) & 0x0e) == CBOR_MAP){
				int mapsize=(payload[curser] & 0x1f);
				 System.out.println("{");
				 curser++;
				 curser=processMap(payload,mapsize,curser,baseSID);
				 System.out.println("}");
			}
			else{
				curser=processLeaf(payload,curser,baseSID);
			}	
			elementCounter++;
		}
		
		mycurser=curser;
		return mycurser;
	}
	
	
	private int processLeaf(byte payload[],int curser, int baseSID){
		int mycurser;
	
		if((payload[curser]>>4 & 0x0e) == CBOR_UINT){
			if((payload[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				curser++;
				System.out.print(baseSID+(payload[curser])+" ");

			}
			else{
				System.out.print(baseSID+(payload[curser] & 0x1f)+" ");
				curser++;
			}
		}else{
			System.out.println("error");
			return 1;
		}
		
		if((payload[curser]>>4 & 0x0e) == CBOR_BYTES){
			int bytelength=(payload[curser] & 0x1f);
			curser++;	
			for(int j=0;j<bytelength;j++){
				System.out.print(Integer.toHexString(payload[curser+j] & 0x0000ff) + "\n");
				curser++;
			}
		}
		else if((payload[curser]>>4 & 0x0e) == CBOR_UINT){
			if((payload[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				curser++;
				System.out.print((payload[curser] & 0x0000ff) + "\n");

			}
			else if((payload[curser] & 0x1f)==CBOR_UINT16_FOLLOWS){

				System.out.print((payload[curser+1] & 0x0000ff)*256+(payload[curser+2] & 0x0000ff) + "\n");
				curser++;
				curser++;
				
			}
			else{
				System.out.print((payload[curser] & 0x00001f) + "\n");
			}
			curser++;
		}
		mycurser=curser;
		return mycurser;
	}
}
