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



public class CoAPClient {


	private static String SERV.10.129.167:5690/3360/0";

	
	public static void main(String args[]) {

		URI uriRD = null; // URI parameter of the request
		try {
			uriRD = new URI(SERVER_URI);
			System.out.println(uriRD);
		} catch (URISyntaxException e) {
			System.err.println("Invalid URI: " + e.getMessage());
			System.exit(-1);
		}			
		CoapClient clientRD = new CoapClient(uriRD);   

		while(true){
			System.out.println("Retrieving Position!");
			clientRD.get(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse resp) {
					System.out.println(resp.getCode());
					if(resp.isSuccess()){
						String content = resp.getResponseText();
						System.out.println("EPs: " + content);
					}					
				}
				@Override
				public void onError() {
					// TODO Auto-generated method stub
	
				}
			});
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
	