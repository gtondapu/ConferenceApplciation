/**
 * Assignment 2:Conference Application
 */
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

//This class has starts threads for clients(channels) 
//and has methods that help to communicate with server
public class Channel implements Runnable{	
	private Socket socket;
	private Scanner reader;
	private PrintWriter writer;
	private boolean running;
	private OnSocketListener onSocketListener;
	private int channelID;
	
	public Channel(Socket socket,OnSocketListener onSocketListener,int channelID)
	{
		this.channelID=channelID;
		this.socket=socket;
		this.onSocketListener=onSocketListener;
	}
	//This method starts a new thread for the client
	public void start()
	{
		Thread thread=new Thread(this);
		thread.start();
	}
	//This method stops the current client 
	//connection with server and other clients
	public void stop() throws IOException
	{
		running=false;
		reader.close();
		socket.close();
	}
	//runs when a new channel thread is started
	@Override
	public void run() {
		try
		{
			OutputStream outputStream=socket.getOutputStream();
			writer=new PrintWriter(outputStream);	
			InputStream inputStream=socket.getInputStream();
			reader=new Scanner(inputStream);	 
			if(null!=onSocketListener)
				onSocketListener.onConnected(this);
			running=true;
			while(running)
			{
				try
				{
					String msg=reader.nextLine();
					if(null!=onSocketListener)
						onSocketListener.onReceived(this, msg);
				}
				catch(NoSuchElementException ex)
				{
					break;
				}
			}
			if(null!=onSocketListener)
				onSocketListener.onDisconnected(this);	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	//This method is used to send a message to the server
	public void send(String msg)
	{
		Channel ch=new Channel(this.socket, this.onSocketListener, this.channelID);
		try 
		{
			PrintWriter w=new PrintWriter(ch.getSocket().getOutputStream());		
			w.println(msg);
			w.flush();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	//This method is used to send a message to the server
	//when user wants to send message to specific client using "whisper"
	public void send(String name,String msg,int chID)
	{
		Channel ch=new Channel(this.socket, this.onSocketListener, this.channelID);
		try 
		{
			PrintWriter w=new PrintWriter(ch.getSocket().getOutputStream());
			w.println(name+" "+msg);
			w.flush();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}	
	}
	//This method retrieves the current socket
	public Socket getSocket() 
	{
		return socket;
	}
}