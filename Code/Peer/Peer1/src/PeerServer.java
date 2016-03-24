import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

public class PeerServer extends Thread
{
  @Override
	public void run() {
		super.run();
	    Properties property=new Properties();
		try {
			property.load(new FileInputStream(new File("config.property")));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
       int clientLocalServerPort=Integer.parseInt(property.getProperty("clientLocalServerPort"));
  		
		try{
		  ServerSocket servsock = new ServerSocket(clientLocalServerPort);      
		    while (true) {
		      Socket sock = servsock.accept();   
		      PeerServer_Instance r = new PeerServer_Instance(sock);
		      Thread t1 = new Thread(r, "Thread A");	        
		      t1.start();	
		    }
		}catch(Exception e){
			e.printStackTrace();
		}

	}
}