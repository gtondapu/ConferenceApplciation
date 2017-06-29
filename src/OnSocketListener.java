/**
 * Assignment 2:Conference Application
 */
//This interface has the behaviors 
//for communication between client and server
public interface OnSocketListener {
	void onConnected(Channel channel);
	void onDisconnected(Channel channel);
	void onReceived(Channel channel,String msg);
}
