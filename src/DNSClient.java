/**
 * Author: Kevin Leehan
 *   Date: 2/6/18
 * Brief
 * Description: DNS client program sends a URL to server.
 * 				Receives reply from server
 * 				calculates time taken to get reply
 * 				Prints reply, calculated round trip, then exits
 *  Parameters: [ServerIP]:[Port#] URL
 */


import java.io.*;
import java.net.*;

public class DNSClient {

	// port number to connect
	private static int DEST_PORT;
	private static String SERVER_NAME;
    
	public static void main(String[] args) { 
		if(args.length!=2) {
			System.out.println("Incorrect number of argruments!");
			System.out.println("Example: [ServerIP]:[Port#] www.wiu.edu");
			System.exit(1);
		}
		
		String [] serverParts= new String[2];
		serverParts = args[0].split(":");
		SERVER_NAME = serverParts[0];
		DEST_PORT = Integer.parseInt(serverParts[1]);
		String URL = args[1];
		
		try
		{
			//Create IP address object from IP address of destination 
			InetAddress dstIP;
			dstIP = InetAddress.getByName(SERVER_NAME);
			
			//Create a timeStamp to measure time taken to resolve IP address
			String timeStamp = ""+System.currentTimeMillis();
			
			//Prepare packet format
			//DNS:[URL]:[TIMESTAMP]
			URL = "DNS"+ ":" + URL + ":" + timeStamp;
			byte[] sendBytes = URL.getBytes();
			
			// Create  UDP socket and UDP packet. Since there is no
			// connection between the devices, the destination address
			// and port number must be specified in the packet.
			DatagramPacket sendPkt = new DatagramPacket(sendBytes, URL.length(), dstIP, DEST_PORT);
			DatagramSocket clientSocket = new DatagramSocket();
			
			//Send packet to server 
			clientSocket.send(sendPkt);
			
			//Buffer for reply packet
			byte[] receiveData = new byte[120]; 
			
			//DatagramPacket object must be created for receiving data from server
			DatagramPacket recvPacket = new DatagramPacket(receiveData, receiveData.length);
			
			//Receive reply from server
			clientSocket.receive(recvPacket);
			
			String recvString = new String(recvPacket.getData(), 0, recvPacket.getLength());
			
			//Records time server reply is received
			long returnTime = System.currentTimeMillis();
			
			//Split server reply for calculating round trip time and printing
			String[] temp= recvString.split(":");
			long timeTaken = returnTime - Long.parseLong(temp[2]);
			String time = "" + timeTaken;
			System.out.println(temp[0]+" "+temp[1]+" "+(time)+"ms");
			
			//Close socket before exiting
			clientSocket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
