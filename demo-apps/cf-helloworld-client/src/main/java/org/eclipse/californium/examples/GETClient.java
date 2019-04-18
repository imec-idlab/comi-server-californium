/*******************************************************************************
 * Copyright (c) 2015 Institute for Pervasive Computing, ETH Zurich and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *    Matthias Kovatsch - creator and main architect
 ******************************************************************************/
package org.eclipse.californium.examples;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.MediaTypeRegistry;



public class GETClient {

	/*
	 * Application entry point.
	 * 
	 */	
	private static byte CBOR_UINT = 0x0;
	private static byte CBOR_NEGINT = 0x2;
	private static byte CBOR_BYTES = 0x4;
	private static byte CBOR_TEXT = 0x6;
	private static byte CBOR_ARRAY = 0x8;
	private static byte CBOR_MAP = 0xA;
	private static byte CBOR_TAG = 0xC;
	private static byte CBOR_7 = 0xE;
	
	private static byte CBOR_UINT8_FOLLOWS = 0x18;
	private static byte CBOR_UINT16_FOLLOWS = 0x19;
	
	private static int processLeaf(byte leaf[],int curser){
		int mycurser;
	
		if((leaf[curser]>>4 & 0x0e) == CBOR_UINT){
			if((leaf[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				System.out.print((leaf[curser+1])+" ");
				curser++;
			}
			else{
				System.out.print((leaf[curser] & 0x1f)+" ");
				curser++;
			}
		}else{
			System.out.println("error");
			return 1;
		}
		
		if((leaf[curser]>>4 & 0x0e) == CBOR_BYTES){
			int bytelength=(leaf[curser] & 0x1f);
			curser++;	
			for(int j=0;j<bytelength;j++){
				System.out.println(Integer.toHexString(leaf[curser+j]) + " ");
				curser++;
			}
		}
		else if((leaf[curser]>>4 & 0x0e) == CBOR_UINT){
			if((leaf[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				System.out.println((leaf[curser+1] & 0x0000ff));
				curser++;
			}
			else if((leaf[curser] & 0x1f)==CBOR_UINT16_FOLLOWS){
				System.out.println((leaf[curser+1] & 0x0000ff)*256+(leaf[curser+2] & 0x0000ff));
				curser++;
				curser++;
				
			}
			else{
				System.out.println((leaf[curser] & 0x1f));
			}
			curser++;
		}
		mycurser=curser;
		return mycurser;
	}
	
	public static void main(String args[]) {
		
		URI uri = null; // URI parameter of the request
	
		if (args.length > 0) {
			
			// input URI from command line arguments
			
			try {
				uri = new URI(args[0]);
				System.out.println(uri);
			} catch (URISyntaxException e) {
				System.err.println("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}

		} else {
			try {
				uri = new URI("coap://[bbbb:0:0:0:12:4b00:613:f4e]:5683/3303/0/5700");
				System.out.println(uri);
			} catch (URISyntaxException e) {
				System.err.println("Invalid URI: " + e.getMessage());
				System.exit(-1);
			}			
		}
		
		//Scanner scanner = new Scanner(System.in);
	    //System.out.print("Enter your name: ");
	   // // get their input as a String
	    //String username = scanner.next();
	    					
		CoapClient client = new CoapClient(uri);   
		
		client.observe(new CoapHandler() {
			
			@Override
			public void onLoad(CoapResponse response) {
				// TODO Auto-generated method stub
				System.out.println("deneme");
			}
			
			@Override
			public void onError() {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		CoapResponse response = client.get();
		
		if (response!=null) {
			
			//System.out.println(response.getCode());
			//System.out.println(response.getOptions());
			
			if(response.getOptions().getContentFormat()==MediaTypeRegistry.APPLICATION_CBOR){
				System.out.println("application/cbor");
				byte[] payload = response.getPayload();
				// System.out.println(payload.length);
				
				
				for(int i=0;i<payload.length;i++){
					if(((payload[i]>>4) & 0x0e) == CBOR_ARRAY){
						System.out.println("[" + (payload[i] & 0x1f));
					}
					else if(((payload[i]>>4) & 0x0e) == CBOR_MAP){
						int mapsize=(payload[i] & 0x1f);
						System.out.println("{" + mapsize);
						i++;
						for(int j=0;j<mapsize;j++){				
							i=processLeaf(payload,i);
						}
					}
					else{
						i=processLeaf(payload,i);
					}
				}
				
			}
			else{
				System.out.println(response.getResponseText());
			}
			
			System.out.println("\nADVANCED\n");
			// access advanced API with access to more details through .advanced()
			System.out.println(Utils.prettyPrint(response));	
			
		} else {
			System.out.println("No response received.");
		}
		
		
	}


}
