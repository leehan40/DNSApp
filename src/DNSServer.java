/**
 * Author: Kevin Leehan
 * 	 Date: 2/6/18
 * Brief
 * Description: DNS Server program receives a URL from client.
 * 				Checks a host.txt file for the given URL, if a match is found the server replies with the associated IP address
 * 				If no match is found the server uses InetAddress.getByName to acquire an IP address
 * 				If no IP address is found the server replies with a message stating an IP address could not be located
 *  Parameters: port number
 */


import java.io.*; 
import java.net.*;
import java.util.Scanner; 


public class DNSServer {
	

	// port number to listen to
	private static int DEST_PORT;
	
	public static void main(String[] args) {
		if(args.length>1) {
			System.out.println("Too many argruments, \nThe only argument should be the port number.");
			System.exit(1);
		}
		DEST_PORT = Integer.parseInt(args[0]);
		try
		{
			//Create local port and bind to DEST_PORT
			DatagramSocket serverSocket = new DatagramSocket(DEST_PORT); 	  
			
			//Server always listens for incoming connections
			while(true) 
	        {
				byte[] receiveData = new byte[120]; 
				
				//Create UDP packet object to receive packet from client
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); 
				System.out.println("Waiting for Client");
				
				// Blocking receive
				serverSocket.receive(receivePacket); 
				System.out.println("Packet Received");
				String packet = new String(receivePacket.getData());
				if(packet.startsWith("DNS")) {
					packet=packet.trim(); //remove extra buffer space
					
					packet = packet.substring(4);
					//Split packet remaining packet
					String[] temp = packet.split(":");
					
					//Gets URL from client and removes extra blank space
					String url = checkURL(temp[0]);
					String IP = getIP(url);
					String timeStamp = temp[1];
					
					String returnPacket = (url + ":" + IP +":"+timeStamp);
					
					//Client packet contains source IP address and port number.
					//Get these values to reply back to client
					InetAddress clientIP = receivePacket.getAddress();
					int clientPort = receivePacket.getPort();
									
					//Create reply packet to send back to client
					DatagramPacket sendPacket = new DatagramPacket(returnPacket.getBytes(), returnPacket.length(), clientIP, clientPort);
					
					//Send packet to client
					serverSocket.send(sendPacket);
					System.out.println("Reply sent\n");
				}	
			} 
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}	
	}
	
	/*	    Name: checkURL()
	*   Method
	*   Overview: checks if the URL is a fully qualified domain name, if not it is modified 
	* Parameters: url string captured from the received packet
	*    Results: String containing a fully qualified domain name
	*/
	public static String checkURL(String url) {
		String [] temp = url.split("\\.");
		if(temp.length==3 && (temp[0].equals("www") && temp[1]!=null && temp[2]!=null))
			return url;
		else if (temp.length==2)
			return "www.".concat(url);
		else if(!url.contains("."))
			return url.concat(".wiu.edu");
		return url;
	}
	
	/*     Name: getIP()
	*  Method 
	*  Overview: Checks if the URL received from the packet is located in the hosts.txt file line by line
	* 	 		 if located: returns the associated IP address
	*	 		 if not located:	InetAddress.getByName is used to acquire the IP address
	*	 		 if the above fails, an error message is displayed to the user	
	* Parameter: the string url string captured from the received packet and verified by checkURL()
	*   Returns: String containing either an IP address or error message
	*/
	public static String getIP(String url) throws FileNotFoundException {
		String [] a = new String[3];
		Scanner sc = new Scanner(new File("hosts.txt"));
		do{
			a = sc.nextLine().split(" ");
			if(a[0].equals(url))
				url=a[1];					
		}while(sc.hasNextLine() && url!=a[1]);
		
		if(url!=a[1])
		try {
			url = InetAddress.getByName(url).toString().substring(url.length()+1);
		}catch(Exception e) {
			url="ERROR! An IP Address could not be located.";
		}
		
		sc.close();
		return url;
	}
}
