import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


public class PeerFileServer extends Thread
{
	
	private ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> fileDetails;
	String localIpandPortAddress;
	int	clientLocalServerPort;
	
		public PeerFileServer() {
			// TODO Auto-generated constructor stub
		}
		 @Override
		public void run() {
			// TODO Auto-generated method stub
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
	//         indexServerPort=Integer.parseInt(property.getProperty("indexServerPort"));
           clientLocalServerPort=Integer.parseInt(property.getProperty("clientLocalServerPort"));
	  		int localfileServerPort=Integer.parseInt(property.getProperty("localfileServerPort"));			

			  ServerSocket fileserver;
			try {
				fileserver = new ServerSocket(localfileServerPort);
			    while (true) {
				      Socket sock = fileserver.accept();   
				      localIpandPortAddress=sock.getLocalSocketAddress()+"";	      				      
				  		InputStream is = sock.getInputStream();
				  		ObjectInputStream ois = new ObjectInputStream(is);
				  		fileDetails=(ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>>)ois.readObject();
				  	//	System.out.println(fileDetails);
				  	
				  		showAllFileName();
				    }
			} catch (IOException | ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}      
		}
 
			public void showAllFileName()
			{		
				Boolean dataPresent=false;
				String PeerLocalIpAndPort=localIpandPortAddress.split(":")[0]+":"+clientLocalServerPort;
				for (String name: fileDetails.keySet()){
		            String key =name.toString();
//		            System.out.println("kKey: "+key+" ip:  . "+PeerLocalIpAndPort);
		            if(!("/"+key).equals(PeerLocalIpAndPort))
		            {
		            	dataPresent=true;
		               System.out.println("\nIP and PORT : "+key);
		                ArrayList<ConcurrentHashMap<String, String>> value = fileDetails.get(name);  
		                for(int i=0;i<value.size();i++)
		                	System.out.println((i+1)+" "+(value.get(i)).get("FILENAME")+"\tSize : " + value.get(i).get("SIZE"));                
		            }              
				} 
				if(dataPresent)
				{
			         System.out.println("\n\n\t\tMenu"); 
			         System.out.println("(Local Server IP: "+localIpandPortAddress.split(":")[0]+":"+clientLocalServerPort+")");
			         System.out.println("1. Show available files");
			         System.out.println("2. Enter file name to download");
			         System.out.println("3. Exit");         
			         System.out.println("Enter your choice : ");
				}
			}   


}
