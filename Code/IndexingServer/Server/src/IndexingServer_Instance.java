import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class IndexingServer_Instance extends Thread
{
      IndexFile indexFile;  	
	  Socket server;
	  int peerFileServerPort;
	  String PeerIPNeeeded;
	  public IndexingServer_Instance(Socket server){
	    this.server=server;
	  }
	  public void run(){
		  indexFile=new IndexFile();
		  performOperationNew(server);	  
	  }	 	  
	 	  
	   public void performOperationNew(Socket server){
			try {				
				InputStream is = server.getInputStream();
				ObjectInputStream ois = new ObjectInputStream(is);
				
				/*Get all peer file details =>*/
				ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> getAllFileDetails = 
						(ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>>)ois.readObject();

				
	        	
		         OutputStream outToServer = server.getOutputStream();
		         ObjectOutputStream oos=new ObjectOutputStream(outToServer);	
                 //oos.writeObject("Ack");
				
	
		    	/*Get peer's fileServerPort port from peer.
		    	 * This port number is used in updateAllPeerFilesNames*/				
					    peerFileServerPort=(int)ois.readObject();
					    PeerIPNeeeded=	((server.getRemoteSocketAddress().toString()).split(":")[0]).split("/")[1];
					    

				

		/*saveAllFileName(getAllFileDetails) => Save all file details from peer into 
		  										Indexing server's database(HashMap)*/		 								
				saveAllFileName(getAllFileDetails);
				
				
	/*indexFile.showAllFileName()=> Show all files names available at Indexing server*/				
				indexFile.showAllFileName();
				
				
         /*updateAllPeerFileNames()=> Update file names of All 
								peers that are connected to servers*/				
				updateAllPeerFileNames();
				
				
				System.out.println("Just connected to "+ server.getRemoteSocketAddress());
				
				
				//Get required fileName input from Peer 
			    String fileNameReceived;//=(String)ois.readObject();
	    //     	System.out.println("\nFilename received from Peer: "+fileNameReceived);

			    boolean serverAlive=true;
	         	do{
		        	//Get required fileName input from Peer
				    fileNameReceived=(String)ois.readObject();
		         	System.out.println("\nData received from Peer: "+fileNameReceived);
		         	/* If received filename is "exit" 
		         	 * then loop will terminate and will close
		         	 * server connection with peer.
		         	 */			         	
		         	if(fileNameReceived.equals("1")){
/*		    			Socket clientSocket = new Socket(PeerIPNeeeded,peerFileServerPort);
				         OutputStream outToServerFS = clientSocket.getOutputStream();
				         ObjectOutputStream oosFS=new ObjectOutputStream(outToServerFS);	
				         oosFS.writeObject(indexFile.getAllFileNames());
			     		clientSocket.close();
*/		         	     oos.writeObject(indexFile.getAllFileNames());
		         	}
		         	else if(fileNameReceived.equals("3")){
		         		serverAlive=false;
		         		 String ipAndServerPort=(String)ois.readObject();				         	
		         		indexFile.removerIp(ipAndServerPort.split("/")[1]);
		         	}else{
		         		
				    	 /*
				    	  * indexFile.getFile(fileNameReceived) 
				    	  * => Gives fileNameReceived details available at Indexserver Hasmap 
				    	  * 	If fileNameReceived is not available at Indexserver
				    	  *     then it will return NULL value to Peer.		
				    	  */		    	 
		         		  oos.writeObject(indexFile.getFileNew(fileNameReceived));
		         	}
			        	
			         }while(serverAlive);
	         System.out.println("One Peer went offline");
	           server.close();	   
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	   }	 

	   
	   public void updateAllPeerFileNames(){		  
		   try{
			   /*indexFile.getAllFileNames()=>
			    * Get all filename details available at indexing server			    
			   */
				   ConcurrentHashMap<String,Integer> peerFileServerIPandPort=indexFile.getRegisteredFileServer();
				   String IPneeded;
					for (String ipFS: peerFileServerIPandPort.keySet()){						
				         @SuppressWarnings("resource")
						Socket clientSocket = new Socket(ipFS.split(":")[0],peerFileServerIPandPort.get(ipFS));
				         OutputStream outToServer = clientSocket.getOutputStream();
				         ObjectOutputStream oos=new ObjectOutputStream(outToServer);	
				         oos.writeObject(indexFile.getAllFileNames());
				         System.out.println("\nUpdated "+PeerIPNeeeded+":"+peerFileServerPort);
					}
		   }
		   catch(Exception e){
			   e.printStackTrace();
		   }
	   }
	   	   
	   /*saveAllFileName=>
	    * Save file details received from peer into
	    * indexing server's DB (HashMap)
	    */
	  	   
	   public void saveAllFileName(ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> fileDetails)
		{		
/*			for (String name: fileDetails.keySet()){
	            String ipaddress =name.toString();
	            System.out.println("saving IP: "+ipaddress);
	            ArrayList<ConcurrentHashMap<String, String>> value = fileDetails.get(name);  
	            for(int i=0;i<value.size();i++)
	            {	
	 		       indexFile.registry(ipaddress,value.get(i).get(SocketConstants.PORT),
	 		    		   value.get(i).get(SocketConstants.ABSOLUTEFILEPATH),
	 		    		   (value.get(i)).get(SocketConstants.FILENAME),
	 		    		   (value.get(i)).get(SocketConstants.SIZE),
	 		    		  (value.get(i)).get(SocketConstants.FileServerSocketPort)	 		    		   
	 		    		   );
	            }	              
			} 
*/			
		   indexFile.registryNew(fileDetails);
		   indexFile.registerFileServer(PeerIPNeeeded,peerFileServerPort);
		}
	   
}