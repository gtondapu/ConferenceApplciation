/**
 * Assignment 2:Conference Application
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Hashtable;

//This class has list of users with established connections
//and has methods to broadcast messages to clients.
public class Server implements Runnable {

	private boolean running;
	private ServerSocket serverSocket;
	private ArrayList<Channel> channels;
	private Hashtable<Channel,Integer> clients;
	private int channelID=1;
	private OnSocketListener onSocketListener;
	public Server(OnSocketListener onSocketListener)
	{
		this.onSocketListener=onSocketListener;
	}
	//start a server socket with given port that
	//accepts incoming client connections
	public void bind(int port) throws IOException
	{
		this.serverSocket=new ServerSocket(port);
	}
	//start a new server thread
	public void start()
	{
		Thread thread=new Thread(this);
		thread.start();
	}
	//disconnect the server
	public void stop() throws IOException
	{
		running=false;
		serverSocket.close();
	}
	//runs when a new server thread is started
	
	@Override
	public void run() 
	{
		channels=new ArrayList<Channel>();
		clients=new Hashtable<Channel,Integer>();  
		running=true;
		while(running)
		{
			try
			{
				Socket socket=serverSocket.accept();
				Channel channel=new Channel(socket,onSocketListener,channelID);
				channel.start();
				channels.add(channel);
				clients.put(channel, channelID);
				channelID++;
			}
			catch(SocketException ex)
			{
				break;
			}
			catch(IOException ex)
			{
				break;
			}
		}
		try 
		{
			for(Channel channel:channels)
			{
				channel.stop();
			} 
			channels.clear();
		}
		catch (IOException e) 
		{		
				e.printStackTrace();
		}
		
	}
	//This method is used to broadcast message 
	//to all the channels present in the list
	public void broadcast(String msg)
	{
		if(!running)
			return;
		for(Channel channel:channels)
		{
			channel.send(msg);
		}
	}
	//This method is used to send message 
	//to all the channels present in the list
	public void broadcast(String msg,Channel ch)
	{
		if(!running)
			return;
		
		for(Channel channel:channels)
		{
			if(ch!=channel)
				channel.send(msg);
		}
	}
	//This method is used to send message 
	//to specific channel in the list
	public void broadcast(String msg,int clientNum)
	{
		if(!running)
			return;
		for(Channel ch:clients.keySet())
		{
			if(clients.get(ch)==clientNum)
			{
				ch.send(msg);
			}
		}
	}
	//This method is used to remove a 
	//specific channel from the list
	public void remove(Channel channel)
	{
		if(!running)
			return;
		channels.remove(channel); 		 	
	}
	//This method is used to retrieve  
	//list of channels
	public ArrayList<Channel> getChannels()
	{
		return channels;	
	}
}