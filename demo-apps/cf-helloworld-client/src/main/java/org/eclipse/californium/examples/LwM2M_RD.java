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

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;

import NetworkScheduler.CoMIScheduler;
import jdk.nashorn.internal.ir.RuntimeNode.Request;



public class LwM2M_RD {

	//private static String RD_URI ="coap://10.10.129.7:5683/rd-lookup/ep";
	////private static String RES_URI ="coap://10.10.129.7:5683/rd-lookup/res";
	//private static String PHONE_URI ="coap://10.10.130.192:5683/rd"; 
	//private static String LESHAN_URI ="coap://10.10.129.7:5685/rd"; 

	private static String RD_URI ="coap://[::1]:5683/rd-lookup/ep";
	private static String RES_URI ="coap://[::1]:5683/rd-lookup/ep";
	private static String PHONE_URI ="coap://10.10.130.192:5683/rd"; 
	private static String LESHAN_URI ="coap://127.0.1.1:5685/rd";
	
	
	public static void main(String args[]) {

		URI uriRD = null; // URI parameter of the request
		try {
			uriRD = new URI(RD_URI);
			System.out.println(uriRD);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}			
		CoapClient clientRD = new CoapClient(uriRD);   

		while(true){
			System.out.println("Retrieving Endpoints!");
			clientRD.get(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse resp) {
					if(resp.isSuccess()){
						String content = resp.getResponseText();
						System.out.println("EPs: " + content);
	
						ArrayList<Endpoint> endPointList= getEndpointList(content);
						System.out.println("EPs number: " + endPointList.size());
						for(int i=0;i<endPointList.size();i++){
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
	
							URI uriRES = null; // URI parameter of the request
							try {
								uriRES = new URI(RES_URI);
								System.out.println(uriRES);
							} catch (URISyntaxException e) {
								System.err.println("Invalid URI: " + e.getMessage());
								System.exit(-1);
							}			
							
							CoapClient clientRES = new CoapClient(uriRES);  
							clientRES.get(new CoapHandler() {
								@Override
								public void onLoad(CoapResponse resp) {
									System.out.println("Retrieving resources on RD");
									if(resp.isSuccess()){
										String content = resp.getResponseText();
										System.out.println("RESs: " + content);
										//	registerPhone(resp);
									}
								}
								@Override
								public void onError() {
									// TODO Auto-generated method stub
	
								}
							});	
							System.out.println("Installing Resources");
							String resources="<3/0>,<12345/0/12345>,<12345/0/12346>,<12345/0/12347>,<12345/0/5530>";						
							registerLeshan(endPointList.get(i).endpoint,endPointList.get(i).address,endPointList.get(i).port,resources);
							//registerPhone(endPointList.get(i).endpoint,endPointList.get(i).address,endPointList.get(i).port,resources);
						}
					}					
				}
				@Override
				public void onError() {
					// TODO Auto-generated method stub
	
				}
			});
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
	public static void registerLeshan(String ep, String address, String port, String resources)
	{
		URI uriLeshan=null;
		try {
			uriLeshan = new URI(LESHAN_URI);
			System.out.println("registering on Leshan: "+uriLeshan);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}

		CoapClient clientLeshan = new CoapClient(uriLeshan);   		
		org.eclipse.californium.core.coap.Request request = new org.eclipse.californium.core.coap.Request(CoAP.Code.POST);
		OptionSet optionSet = new OptionSet();
		optionSet.addOption(new Option(15, ("ep="+ep)));
		optionSet.addOption(new Option(15, "ca="+address));
		optionSet.addOption(new Option(15, "cp="+port));
		request.setOptions(optionSet);
		request.setPayload(resources);
		clientLeshan.advanced(new CoapHandler() {
			@Override

			public void onLoad(CoapResponse resp) {
				if (resp!=null) {
					System.out.println(resp.getResponseText());
					System.out.println("\nADVANCED\n");
					// access advanced API with access to more details through .advanced()
					System.out.println(Utils.prettyPrint(resp));	

				} else {
					System.out.println("No response received.");
				}		
			}
			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		},request); // or async version
	}

	public static void registerPhone(String ep, String address, String port, String resources)
	{
		URI uriPhone=null;
		try {
			uriPhone = new URI(PHONE_URI);
			System.out.println(uriPhone);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}			

		CoapClient clientPhone = new CoapClient(uriPhone);   		
		org.eclipse.californium.core.coap.Request request = new org.eclipse.californium.core.coap.Request(CoAP.Code.POST);
		OptionSet optionSet = new OptionSet();
		optionSet.addOption(new Option(15, ("ep="+ep)));
		optionSet.addOption(new Option(15, "ca="+address));
		optionSet.addOption(new Option(15, "cp="+port));
		request.setOptions(optionSet);
		request.setPayload(resources);
		clientPhone.advanced(new CoapHandler() {
			@Override

			public void onLoad(CoapResponse resp) {
				if (resp!=null) {
					System.out.println(resp.getResponseText());
					System.out.println("\nADVANCED\n");
					// access advanced API with access to more details through .advanced()
					System.out.println(Utils.prettyPrint(resp));	

				} else {
					System.out.println("No response received.");
				}		
			}
			@Override
			public void onError() {
				// TODO Auto-generated method stub

			}
		},request); // or async version
	}

	public static ArrayList<Endpoint> getEndpointList(String content){

		List<String> items = Arrays.asList(content.split("\\s*,\\s*"));
		ArrayList<Endpoint> endPointList= new ArrayList<Endpoint>();
		for(int i=0; i<items.size(); i++){
			String ep=items.get(i);
			int startpos=0; 
			int endpos=0; 
			while(startpos<ep.length()){
				startpos= ep.indexOf('<',startpos);
				endpos= ep.indexOf('>',startpos);
				String eptext=null;
				String port=null;
				String address=null;
				if(startpos<endpos && startpos>-1 && endpos>-1){	
					startpos++;
					String addressPort=ep.substring(startpos, endpos);
					port = addressPort.substring(addressPort.length()-4);
					address = addressPort.substring(addressPort.indexOf("://")+3,addressPort.length()-5);
					int startrt=ep.indexOf("ep=\"",endpos); 
					int endrt=ep.indexOf('"',startrt+4);
					if(-1<startrt && -1<endrt){
						eptext = ep.substring(startrt+4, endrt);
					}		
					Endpoint endpoint = new Endpoint(eptext,address,port);
					endPointList.add(endpoint);
				}
				else{
					break;
				}		
			}

		}

		return endPointList;
	}

	public static class Endpoint{
		public String endpoint;
		public String port;
		public String address;

		public Endpoint(String ep, String a, String p) {
			endpoint = ep;
			address=a;
			port=p;
		}
	} 
}
