package org.idlab.comi;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Base64;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

public final class CoMIInterface{
	
	public static CoapResponse getResource(String address,int sid){
		URI uri = null;
		try {
			uri = new URI(address+"/"+getBase64(sid));
		} catch (URISyntaxException f) {
			System.err.println("Invalid URI: " + f.getMessage());
			System.exit(-1);
		}
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.get();
		return response;
	}
	
	public static CoapResponse getResource(String address,int sid, String query){
		URI uri = null;
		try {
			uri = new URI(address+"/"+getBase64(sid)+"?k="+query);
		} catch (URISyntaxException f) {
			System.err.println("Invalid URI: " + f.getMessage());
			System.exit(-1);
		}
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.get();
		return response;
	}
	
	
	public static CoapResponse putResource(String address,byte[] payload, int sid, String query){
		URI uri = null;
		try {
			uri = new URI(address+"/"+getBase64(sid)+"?k="+query);
		} catch (URISyntaxException f) {
			System.err.println("Invalid URI: " + f.getMessage());
			System.exit(-1);
		}
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.put(payload,MediaTypeRegistry.APPLICATION_CBOR);
		return response;
	}
	
	public static String getBase64(int sid){
		byte[] bytes = new byte[3];
    	bytes[0]=(byte) ((sid>>16)&0xff);
    	bytes[1]=(byte) ((sid>>8)&0xff);
    	bytes[2]=(byte) ((sid)&0xff);
    	String code= Base64.getEncoder().encodeToString(bytes);
		return code.substring(code.length()-2);
	}
	
}
