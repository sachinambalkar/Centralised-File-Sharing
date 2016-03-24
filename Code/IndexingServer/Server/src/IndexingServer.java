import java.net.*;
import java.util.Properties;
import java.io.*;

public class IndexingServer
{
   public static void main(String []args) throws IOException
   {
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
      int indexServerPort=Integer.parseInt(property.getProperty("indexServerPort"));
      ServerSocket indexServerSocket = new ServerSocket(indexServerPort);      
      /*
       * While loop => To continuously provide service to new Peer 
       */      
      while(true)
      {
         try
         {
            System.out.println("Waiting "+SocketConstants.hostname +" for client on port " +
            		indexServerSocket.getLocalPort() + "...");
            Socket serverInstance = indexServerSocket.accept();                      
            /* newPeerThread => Creates thread when new peer arrives.*/    
	        System.out.println("Just connected to "+ serverInstance.getRemoteSocketAddress());
	        IndexingServer_Instance r = new IndexingServer_Instance(serverInstance);
	        Thread newPeerThread = new Thread(r, "New Peer");	        
	        newPeerThread.start();            
         }catch(SocketTimeoutException s)
         {
            System.out.println("Socket timed out!");
            break;
         }catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
   }     
}