package org.idlab.comi;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;

public class ResourceDirectory {
	
	private static int maxComiServer=20;
	String rdAddress;

	public ResourceDirectory(String resourcedirectory_Address) {
		rdAddress=resourcedirectory_Address;
	}
		
	public InetSocketAddress[] getComiServers(){
		
		CoapResponse coapResponse=getResources(); 								// Retrieving all resources
		String response = coapResponse.getResponseText();
		InetSocketAddress[] ComiServers = new InetSocketAddress[maxComiServer];
		
		int startpos=0; 
		int endpos=0; 
		int i=0;
		while(startpos<response.length() && i<maxComiServer){
			startpos= response.indexOf('<',startpos);
			endpos= response.indexOf('>',startpos);		
			if(startpos<endpos && startpos>-1 && endpos>-1){
				startpos++;
				endpos++;
				int startrt=response.indexOf("rt=\"",endpos); 
				int endrt=response.indexOf('"',startrt+4);
				
				if(-1<startrt && -1<endrt && (response.indexOf('<',startpos) < 0 || startrt < response.indexOf('<',startpos))){
					String rttext = response.substring(startrt+4, endrt);
					//System.out.print(resource + "\t");
					//System.out.println("rt=" + rttext);
					if(rttext.equals("core.c")){
						int startaddress=response.indexOf('[',startpos); 
						int endaddress=response.indexOf(']',startaddress);
						if(-1<startaddress && -1<endaddress){
							//System.out.println(response.substring(startaddress, endaddress+1));
							int startPort=response.indexOf(':',endaddress); 
							int endPort=response.indexOf('/',startPort);
							if(-1<startPort && -1<endPort){
								//System.out.println(response.substring(startPort+1, endPort));
								InetSocketAddress sockaddr = new InetSocketAddress(response.substring(startaddress, endaddress+1), Integer.valueOf(response.substring(startPort+1, endPort)));
								ComiServers[i]=sockaddr;
								i++;
							}
						}
					}
				}				
			}
			else{
				break;
			}		
		}
		return ComiServers;
	}
	
	private CoapResponse getResources(){
		CoapClient rdClient;
		// input URI from command line arguments
		URI rduri = null; // URI of resource directory
		try {
			rduri =  new URI(rdAddress+":5683/rd-lookup/res?rt=core.c");
			System.out.println("Retrieving resources on RD"+rduri);
		} catch (URISyntaxException f) {
			System.err.println("Invalid URI: " + f.getMessage());
			System.exit(-1);
		}
		rdClient = new CoapClient(rduri);
		CoapResponse response = rdClient.get();
		
		if (response!=null) {
			System.out.println("Resources are received from RD.");
		} else {
			System.out.println("Resources are not received.");
		}

		return response;
	}
	
	public int getLength(InetSocketAddress comiServers[]){
	    int count = 0;
	    while (count<comiServers.length && comiServers[count]!=null){
	    	count++;
	    }
	    return count;
	}

}
