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

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import static java.util.concurrent.TimeUnit.*;

public class GETTemp {

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private URI uri = null; // URI parameter of the request
	private CoapClient tempClient;
	private CoapClient neighClient;
	
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
	private JFrame frame;
	private JTextField UriField;
	private int isObserved=0;
	private JTextArea textArea;
	

	private int processLeaf(byte leaf[],int curser, int baseSID){
		int mycurser;
	
		if((leaf[curser]>>4 & 0x0e) == CBOR_UINT){
			if((leaf[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				//printOUT(" ");
				curser++;
			}
			else{
				//printOUT(" ");
				curser++;
			}
		}else{
			printOUT("error");
			return 1;
		}
		
		if((leaf[curser]>>4 & 0x0e) == CBOR_BYTES){
			int bytelength=(leaf[curser] & 0x1f);
			curser++;	
			for(int j=0;j<bytelength;j++){
				printOUT(Integer.toHexString(leaf[curser+j] & 0x0000ff) + " ");
				curser++;
			}
		}
		else if((leaf[curser]>>4 & 0x0e) == CBOR_UINT){
			if((leaf[curser] & 0x1f)==CBOR_UINT8_FOLLOWS){
				printOUT((leaf[curser+1] & 0x0000ff) + " ");
				curser++;
			}
			else if((leaf[curser] & 0x1f)==CBOR_UINT16_FOLLOWS){
				printOUT((leaf[curser+1] & 0x0000ff)*256+(leaf[curser+2] & 0x0000ff) + " ");
				curser++;
				curser++;
				
			}
			else{
				printOUT((leaf[curser] & 0x00001f) + " ");
			}
			curser++;
		}
		mycurser=curser;
		return mycurser;
	}
	

	private void printOUT(String message){
		System.out.print(message);
	}
				
	public static void main(String args[]) {

		GETTemp tempcontrol = new GETTemp();	
		try {
			tempcontrol.uri = new URI("coap://[bbbb:0:0:0:12:4b00:613:1522]:5683/3303/0/5700");
			System.out.println(tempcontrol.uri);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}		
		tempcontrol.tempClient = new CoapClient(tempcontrol.uri);  
		
		try {
			tempcontrol.uri = new URI("coap://[bbbb:0:0:0:12:4b00:613:1522]:5683/c/+r");
			System.out.println(tempcontrol.uri);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}		
		tempcontrol.neighClient = new CoapClient(tempcontrol.uri);   
		
		final Runnable beeper = new Runnable() {
			public void run() { 


				long reqtime=System.currentTimeMillis();
				CoapResponse response = tempcontrol.tempClient.get();


				if (response!=null) {
					System.out.println(System.currentTimeMillis()-reqtime);
					System.out.println(response.getResponseText());
				} else {
					System.out.println("No response received.");
				}}
		};
		
		final Runnable neighTableReader = new Runnable() {
			public void run() { 
				CoapResponse response = tempcontrol.neighClient.get();
				if (response!=null) {

					if(response.getOptions().getContentFormat()==MediaTypeRegistry.APPLICATION_CBOR){
						System.out.println("application/cbor");
						byte[] payload = response.getPayload();
						// System.out.println(payload.length);
						int baseSID=4000;

						for(int i=0;i<payload.length;){
							if(((payload[i]>>4) & 0x0e) == CBOR_ARRAY){
								tempcontrol.printOUT("[" + (payload[i] & 0x1f) + " ");
								i++;
							}
							else if(((payload[i]>>4) & 0x0e) == CBOR_MAP){
								int mapsize=(payload[i] & 0x1f);
								tempcontrol.printOUT("{" + mapsize+ " ");
								i++;
								for(int j=0;j<mapsize;j++){				
									i=tempcontrol.processLeaf(payload,i,baseSID);
								}
							}
							else{
								i=tempcontrol.processLeaf(payload,i,baseSID);
							}
							tempcontrol.printOUT("\n");
						}

					}
					else{
						tempcontrol.printOUT(response.getResponseText());
					}
				}
			}
		};
		tempcontrol.scheduler.scheduleAtFixedRate(beeper, 100, 1000, MILLISECONDS);
		//tempcontrol.scheduler.scheduleAtFixedRate(neighTableReader, 1,10, SECONDS);
		
	}
	   
}

