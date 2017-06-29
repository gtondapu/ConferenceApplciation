/**
 * Assignment 2:Conference Application
 */
//This class is for user to send messages to other users via the server
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
public class ConfUser implements OnSocketListener{
	private int channelID;
	public static void main(String[] args) throws UnknownHostException,IOException
	{
		ConfUser start=new ConfUser();
		start.start();
	}
	
	@Override
	public void onConnected(Channel channel) {
		System.out.println("connected...");
	}
	
	@Override
	public void onDisconnected(Channel channel) {
		System.out.println("disconnected...");
	}
	
	@Override
	public void onReceived(Channel channel, String msg) {
		 System.out.println(msg);
	}
	public void start() throws UnknownHostException,IOException
	{
		Scanner scanner =new Scanner(System.in);
		
		System.out.println("name : ");
		String name=scanner.nextLine();
		Socket socket=null;
		try
		{
			System.out.println("IP : ");
			String ip=scanner.nextLine();
			System.out.println("Port : ");
			int port=Integer.parseInt(scanner.nextLine());		
		    socket=new Socket(ip,port);
		
		//start a new channel to receive messages
		Channel channel=new Channel(socket, this,channelID);
		channel.start();
	
		//send messages
		while(true)
		{
			String msg=scanner.nextLine();
			if(msg.isEmpty()||msg.equals("quit"))
			{
				System.exit(0);
				break;
			}
			if(msg.startsWith("whisper"))
			{		
				String[] arr=msg.split(" ");
				channel.send(name,msg,Integer.parseInt(arr[1]));
			}	
			else channel.send(name+">>"+msg);
		}
		scanner.close();
		channel.stop();		
		System.out.println("closed");
		}
		catch(Exception ex)
		{
			System.out.println("check ur port");
		}
	}
}