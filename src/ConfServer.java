/**
 * Assignment 2:Conference Application
 */
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
//This class monitors messages for the list
//of users with established connections
public class ConfServer implements OnSocketListener {
	private Server server;
	public static void main(String[] args) throws IOException
	{
	ConfServer serverPgm=new ConfServer();
	serverPgm.start();
}

	@Override
	public void onConnected(Channel channel) {
		Socket socket=channel.getSocket();
		String hostName=socket.getInetAddress().getHostName();
		int port=socket.getPort();
		String msg="client connected from "+hostName+": " +port;
		System.out.println(msg);		
		for(Channel c:server.getChannels())
		{
			if(c!=channel)
			{			
				c.send(msg);
			}
		}
	}

	@Override
	public void onDisconnected(Channel channel) {
		server.remove(channel);
		Socket socket=channel.getSocket();
		String hostName=socket.getInetAddress().getHostName();
		int port=socket.getPort();
		String msg="client disconnected from "+hostName+" :"+port;
		System.out.println(msg);
		server.broadcast(msg,channel);		
	}

	@Override
	public void onReceived(Channel channel, String msg) {
		
		if(msg.contains(("whisper")))
		{
			String[] arr=msg.split(" ");
			String mmm="";
			for(int i=3;i<arr.length;i++)
				mmm=mmm+" "+arr[i];
			System.out.println(arr[0]+">>"+mmm);
			server.broadcast(arr[0]+">>"+mmm,Integer.parseInt(arr[2]));
		}
		else
		{
			System.out.println(msg);
			server.broadcast(msg,channel);
		}
	}
	//start an instance of server to broadcast messages
	public void start() throws IOException
	{
		Scanner scanner=new Scanner(System.in);
		System.out.println("port : ");
		int port=0;
		try
		{
			port=Integer.parseInt(scanner.nextLine());
			server =new Server(this);
			server.bind(port);
			server.start();
			System.out.println("server has started..");
		}
		catch(Exception ex)
		{
			System.out.println("Please check your port number..");
		}
		//broadcasting messages
		while(true)
		{
			String msg=scanner.nextLine();
			if(msg.isEmpty()||msg.equals("quit"))
			{
				System.exit(0);
				break;
			}
			if(msg.contains("whisper"))
			{
				String[] arr=msg.split(" ");
				server.broadcast("server>> "+arr[2],Integer.parseInt(arr[1]));
			}
			else
				 server.broadcast("server>> "+msg);
		}	
		scanner.close();
		server.stop();
		System.out.println("server has closed..");
	}	
}