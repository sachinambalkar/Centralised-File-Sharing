import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.awt.List;
import java.io.*;

import javax.swing.plaf.SliderUI;

public class PeerClient
{
	private	static ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> fileDetails;
	static ArrayList<ConcurrentHashMap<String, String>> filelist=new ArrayList<ConcurrentHashMap<String, String>>();  			
	public static String localIpandPortAddress;
	static Random randomGenerator = new Random();  
	public static int indexServerPort;
	public static int clientLocalServerPort;
	public static int localfileServerPort;
	
   public static void main(String [] args)
   {	   
	  
	    Properties property=new Properties();
	 	try {
			property.load(new FileInputStream(new File("config.property")));
             indexServerPort=Integer.parseInt(property.getProperty("indexServerPort"));
      		 clientLocalServerPort=Integer.parseInt(property.getProperty("clientLocalServerPort"));
      		localfileServerPort=Integer.parseInt(property.getProperty("localfileServerPort"));			
	 	} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    fileDetails=new ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>>();
	    int indexServerPort=Integer.parseInt(property.getProperty("indexServerPort"));	    
	    /*Start Fileserver to receive update from indexserver
	      when new peers add at IndexServer*/
	      PeerFileServer pfs = new PeerFileServer();
	      Thread pfs_thread = new Thread(pfs, "Peer's Local fileserver");	        
	      pfs_thread.start();
	      
	      
	      PeerServer peerServer = new PeerServer();
	      Thread peerServer_thread = new Thread(peerServer, "Peer's Local Server");	        
	      peerServer_thread.start();

	      
	      
	  try
      {
         System.out.println("Connecting to " + property.getProperty("indexServerIPaddress") + " on port " + indexServerPort);
         Socket client = new Socket(property.getProperty("indexServerIPaddress"), indexServerPort);
         System.out.println("Just connected to " + client.getRemoteSocketAddress());                  

     	localIpandPortAddress=  client.getLocalSocketAddress()+"";

//     	System.out.println("localIpandPortAddress=> "+localIpandPortAddress);
         updateIndexfile();
         OutputStream outToServer = client.getOutputStream();         
         ObjectOutputStream oos=new ObjectOutputStream(outToServer);	
         oos.writeObject(fileDetails);

         //Providing LocalFileServer port to IndexServerPort
         oos.writeObject(localfileServerPort);

   		InputStream is = client.getInputStream();
   		ObjectInputStream ois = new ObjectInputStream(is);
   		ConcurrentHashMap<String, String> receivedFileDetails=new ConcurrentHashMap();
   		ArrayList<ConcurrentHashMap<String, String>> receivedFileDetailsarray=new ArrayList<ConcurrentHashMap<String, String>>();

         Scanner reader = new Scanner(System.in);
         boolean peerIsAlive=true;
         while(peerIsAlive){
         System.out.println("\n\n\t\tMenu"); 
         System.out.println("(Local Server IP: "+localIpandPortAddress.split(":")[0]+":"+clientLocalServerPort+")");	 
         System.out.println("1. Show available files");
         System.out.println("2. Enter file name to download");
         System.out.println("3. Exit");         
         System.out.println("Enter your choice : ");
         String choice,filename;
         choice = reader.next();
         
         if(choice.equals("1")){
        	 oos.writeObject(choice);
		  	fileDetails=(ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>>)ois.readObject();
            showAllFileName();       	 
         }
         else if(choice.equals("2"))
         {
        	 System.out.println("Enter Filename : ");
        	 filename=reader.next();
        	 oos.writeObject(filename);
        	 receivedFileDetailsarray=(ArrayList<ConcurrentHashMap<String, String>>)ois.readObject();
        	 
        	 if(receivedFileDetailsarray!=null){
        		 if(receivedFileDetailsarray.size()==1){
        			
	             		receivedFileDetails=receivedFileDetailsarray.get(0);
	    	         	if(receivedFileDetails!=null)
	    	         	{
	    	         		String fpath=receivedFileDetails.get(SocketConstants.ABSOLUTEFILEPATH);
	    	         		String port=receivedFileDetails.get(SocketConstants.PORT);
	    	         		String ip=receivedFileDetails.get(SocketConstants.IPADDRESS);
	    	         		String fname=receivedFileDetails.get(SocketConstants.FILENAME);
	    	         		String fsize=receivedFileDetails.get(SocketConstants.SIZE);
	    		 	         System.out.println(" Available at "+ip+":"+port);
	    		 		     System.out.println(" Location: "+fpath);
	    		 		     System.out.println(" Size: "+fsize+" bytes");
	    	    		 	 System.out.println(" File is downloading...");
	    	            	 long startTime = System.nanoTime();    
	    	            	 System.out.println("Started time : " + startTime);
	    		 		     downloadFile(ip,port,fpath,fname,Integer.parseInt(fsize)); 		     
	    		        	 long endtime=System.nanoTime();
	    		        	 System.out.println("Ended at "+endtime);
	    		        	 long estimatedTime = System.nanoTime() - startTime;
	    		        	 System.out.println("Time required for download (Nanosecond): "+estimatedTime);

	    	         	}
	    	         	else
	    	         		System.out.println("Error occured.");        			 
        		 }
        		 else
        		 {
              		System.out.println("\nFile is available at multiple location.");
                 	for(int iter=0;iter<receivedFileDetailsarray.size();iter++)
                 	{
                 		receivedFileDetails=receivedFileDetailsarray.get(iter);
        	         	if(receivedFileDetails!=null)
        	         	{
        	         		String fpath=receivedFileDetails.get(SocketConstants.ABSOLUTEFILEPATH);
        	         		String port=receivedFileDetails.get(SocketConstants.PORT);
        	         		String ip=receivedFileDetails.get(SocketConstants.IPADDRESS);
        	         		String fname=receivedFileDetails.get(SocketConstants.FILENAME);
        	         		String fsize=receivedFileDetails.get(SocketConstants.SIZE);
        		 	         System.out.println((iter+1)+". Available at "+ip+":"+port);
        		 		     System.out.println("   Size: "+fsize+" bytes");
        	         	}
        	         	else
        	         		System.out.println("File Not Found.");
                 	}
    	           	 System.out.println("Select location to start download : ");
    	        	 String selected=reader.next();
    	        	 int convertInt =Integer.parseInt(selected);
    	        	 int index=convertInt-1;
    	        	 
    	        	 if(index<receivedFileDetailsarray.size()){
    	             		receivedFileDetails=receivedFileDetailsarray.get(index);
    	    	         	if(receivedFileDetails!=null)
    	    	         	{
    	    	         		String fpath=receivedFileDetails.get(SocketConstants.ABSOLUTEFILEPATH);
    	    	         		String port=receivedFileDetails.get(SocketConstants.PORT);
    	    	         		String ip=receivedFileDetails.get(SocketConstants.IPADDRESS);
    	    	         		String fname=receivedFileDetails.get(SocketConstants.FILENAME);
    	    	         		String fsize=receivedFileDetails.get(SocketConstants.SIZE);
    	    		 	         System.out.println(" Available at "+ip+":"+port);
    	    		 		     System.out.println(" Location: "+fpath);
    	    		 		     System.out.println(" Size: "+fsize+" bytes");
    	    	    		 	 System.out.println(" File is downloading...");
    	    	            	 long startTime = System.nanoTime();    
    	    	            	 System.out.println("Started time : " + startTime);
    	    		 		     downloadFile(ip,port,fpath,fname,Integer.parseInt(fsize)); 		     
    	    		        	 long endtime=System.nanoTime();
    	    		        	 System.out.println("Ended at "+endtime);
    	    		        	 long estimatedTime = System.nanoTime() - startTime;
    	    		        	 System.out.println("Time required for download : "+estimatedTime);
    	    	         	}
    	    	         	else
    	    	         		System.out.println("Error occured.");
    	        	 }
    	        	 else
    	        		 System.out.println("Invalid Choice.");        			 
        		 }
        	 }
        	        		  
         }
         else if(choice.equals("3")){
        	 peerIsAlive=false;
        	 oos.writeObject("3");
        	 oos.writeObject(localIpandPortAddress.split(":")[0]+":"+clientLocalServerPort);
         }
         else{
        	 System.out.println("Invalid Choice.");
         }
      }
         
 /*       do{
		     System.out.println("\nEnter file name : ");
	         filename = reader.next();
	         oos.writeObject(filename);         
        }while(filename.equals("exit")==false);         
*/         client.close();
      }catch(IOException e)
      {
         e.printStackTrace();
      } catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }

   
   
	 public static void downloadFile(String ip,String port,String filepath,String fname,int fsize){

		    Socket sock;
			try {
				sock = new Socket(ip,Integer.parseInt(port));
				
				//Sending File name to server which peer want to download				
				OutputStream outToServer = sock.getOutputStream();         
		        ObjectOutputStream oos=new ObjectOutputStream(outToServer);	
		        oos.writeObject(filepath);
				
		      //DownloadedFiles => Folder name file will get download  
		  	  String FolderName="DownloadedFiles"; 
			  Path currentRelativePath = Paths.get("");
			  String s = currentRelativePath.toAbsolutePath().toString();
			  String pathName=s+"/"+FolderName+"/"+fname;
			  	
			  
			  /////////////////////////////////////////////////////////			  
				File severDirectory = new File(s+"/"+FolderName);	
				// if the directory does not exist, create it
				if (!severDirectory.exists()) {
				    boolean result = false;				
				    try{
				    	severDirectory.mkdir();
				        result = true;
				    } 
				    catch(SecurityException se){
				        //handle it
				    }        
				    if(result) {    
				        System.out.println(FolderName+" directory created");  
				    }
				}

			  ///////////////////////////////////////////////////////
			  
			/*Check file is already available at DownloadedFiles.
			  If present then modify file name*/
				File f = new File(pathName);
				if(f.exists()){
					String justFilename=fname.split("\\.")[0];
					String extension=fname.split("\\.")[1];
					int randomInt = randomGenerator.nextInt(100);
					pathName=s+"/"+FolderName+"/"+justFilename+"_"+randomInt+"."+extension;
				}
				
				
				
			    InputStream is = sock.getInputStream();
			    FileOutputStream fos = new FileOutputStream(pathName);
			    BufferedOutputStream bos = new BufferedOutputStream(fos);    
			        
			    int convertByteIntoMB=1024*1024;
			    float filesizeByte=fsize;
			    float fileSizeMB=filesizeByte/convertByteIntoMB;
			    float fileSizeKB=filesizeByte/1024;
			    boolean showSizeInMB;// = true;
			    int divideConstant;
			    float filesize;
			    if(fileSizeMB>1)
			    {	System.out.println("FileSize: "+ fileSizeMB+" mb");
			    	showSizeInMB=true;
			    	divideConstant=1024*1024;	
			    	filesize=fileSizeMB;
			    }
			    else{
			    	showSizeInMB=false;
			    	System.out.println("FileSize: "+ fileSizeKB+" kb");
			    	divideConstant=1024;
			    	filesize=fileSizeKB;
			    }
			    int count=1;
			    int dataSize;
			    if(fsize<SocketConstants.streamConstant)
			    	dataSize=fsize;
			    else
			    	dataSize=SocketConstants.streamConstant;
			              
			    byte[] mybytearray = new byte[dataSize];
			    int bytesRead;
			    
			    do{
				    bytesRead = is.read(mybytearray, 0,dataSize);
				    if(bytesRead>1)
				    bos.write(mybytearray, 0, bytesRead);
//				    if(bytesRead>=dataSize)
	//				    System.out.print((dataSize*count)/divideConstant+" ");
				    count++;
			    }while(bytesRead>0);
			    System.out.println("\nDownloaded Successfully");
			    bos.close();
			    sock.close();
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 
	 }
   
  public static void updateIndexfile(){
	  
	  String FolderName="ServerFiles"; 
	  Path currentRelativePath = Paths.get("");
	  String s = currentRelativePath.toAbsolutePath().toString();
	  System.out.println("Current relative path is: " + s);
	  
	  String foldername=s+"/"+FolderName;

		File severDirectory = new File(foldername);	
		// if the directory does not exist, create it
		if (!severDirectory.exists()) {
		    boolean result = false;
		
		    try{
		    	severDirectory.mkdir();
		        result = true;
		    } 
		    catch(SecurityException se){
		        //handle it
		    }        
		    if(result) {    
		        System.out.println(FolderName+" directory created");  
		    }
		}


	  
/*	  System.out.println("getting file name");
	  URL location = PeerClient.class.getProtectionDomain().getCodeSource().getLocation();
	  System.out.println("Got file name");
*/
//      System.out.println(location.getFile());
	 // final File folder = new File(s);
	  listFilesForFolder(severDirectory);  		  
  }
  
  public static void listFilesForFolder(final File folder) 
  {
      for (final File fileEntry : folder.listFiles()) {
          if (fileEntry.isDirectory()) {
              listFilesForFolder(fileEntry);
          } else {
        	ConcurrentHashMap<String, String> filedetail=new ConcurrentHashMap<String, String>();
    		filedetail.put(SocketConstants.FILENAME, fileEntry.getName());
    		filedetail.put(SocketConstants.ABSOLUTEFILEPATH,fileEntry.getPath());
        	filedetail.put(SocketConstants.SIZE,fileEntry.length()+"");
    		filelist.add(filedetail);
    		//            System.out.println("FilePath: "+fileEntry.getPath()+" FileName: "+fileEntry.getName());
          }
      }
      fileDetails.put(SocketConstants.localIPaddress+":"+clientLocalServerPort, filelist);
  }

	public static void showAllFileName()
	{				
		String PeerLocalIpAndPort=localIpandPortAddress.split(":")[0]+":"+clientLocalServerPort;
		for (String name: fileDetails.keySet()){
            String key =name.toString();
//            System.out.println("Key: "+key+" ip:"+localIpandPortAddress);
            if(!("/"+key).equals(PeerLocalIpAndPort))
            {
               System.out.println("\nIP and PORT : "+key);
                ArrayList<ConcurrentHashMap<String, String>> value = fileDetails.get(name);  
                for(int i=0;i<value.size();i++)
                	System.out.println((i+1)+" "+(value.get(i)).get("FILENAME")+"\tSize : " + value.get(i).get("SIZE"));                
            }              
		} 
	}   
}