import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
public class IndexFile 
{
    private	static ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> fileDetails=new ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>>();
    private	static ConcurrentHashMap<String, ArrayList<String>> fileIpAddessNew=new ConcurrentHashMap<String, ArrayList<String>>();
    private	static ConcurrentHashMap<String, Integer> peerFileServerAddress=new ConcurrentHashMap<String, Integer>();
	public void showAllFileName()
	{				
		System.out.println("\nFiles available at Index server: \n");
		for (String name: fileDetails.keySet()){
            String key =name.toString();
            System.out.println("\n"+"IP: "+key);
            ArrayList<ConcurrentHashMap<String, String>> value = fileDetails.get(name);  
            for(int i=0;i<value.size();i++)
            {	
            	System.out.println((i+1)+". "+
            (value.get(i)).get(SocketConstants.FILENAME));	
            }              
		} 
	}
		
	public void registryNew(ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> newdetails)
	{
		
		for (String name: newdetails.keySet())
		{
	        String ipAndport =name.toString();
			fileDetails.put(ipAndport,newdetails.get(ipAndport));				
	        System.out.println("got ipport: "+ipAndport);
	        ArrayList<ConcurrentHashMap<String, String>> value = newdetails.get(name);  
	        for(int i=0;i<value.size();i++)
	        {	
	        	String filenameNew=(value.get(i)).get(SocketConstants.FILENAME);
	               if(fileIpAddessNew.get(filenameNew)==null){
	            	   ArrayList<String> newdata=new ArrayList<String>();
	            	   newdata.add(ipAndport);
	            	   fileIpAddessNew.put(filenameNew,newdata);
	               }
	               else
	            	   {
	            	   ArrayList<String> data=fileIpAddessNew.get(filenameNew);
	            	   data.add(ipAndport);
	            	   fileIpAddessNew.put(filenameNew,data);
	            	   }
	        }	              
	} 
					
	}

	public ArrayList<ConcurrentHashMap<String, String>> getFileNew(String fileName){						
		ArrayList<String> iparray=fileIpAddessNew.get(fileName);
		ArrayList<ConcurrentHashMap<String, String>> requiredFileDetailsArray =new ArrayList<ConcurrentHashMap<String, String>>();
		System.out.println("Server checking "+fileName+"...");
		boolean flag = false;
		if(iparray!=null)
		{			
			
			for(String fileIP: iparray){
	            ArrayList<ConcurrentHashMap<String, String>> value = fileDetails.get(fileIP);
	            for(int i=0;i<value.size();i++)
	            {	
	            	if(fileName.equals((value.get(i)).get(SocketConstants.FILENAME).toString()))
	            	{	     
	            		flag=true;
	            		ConcurrentHashMap<String, String> requiredFileDetails=new ConcurrentHashMap();
	            		requiredFileDetails=value.get(i);
	            		requiredFileDetails.put(SocketConstants.PORT,fileIP.split(":")[1]);
	            		requiredFileDetails.put(SocketConstants.IPADDRESS, fileIP.split(":")[0]);
	            		requiredFileDetailsArray.add(requiredFileDetails);
	            	}
	            }	              
			}
		}		
		if(flag)
			System.out.println("File Found at server. Sending details..");
		else
			System.out.println("File not Found at server.");

		return requiredFileDetailsArray;
	}

	
	
	
	
	public ConcurrentHashMap<String,ArrayList<ConcurrentHashMap<String,String>>> getAllFileNames(){
		return fileDetails;
	}
	
	public void registerFileServer(String ip,int port){
		peerFileServerAddress.put(ip+":"+port, port);
	}
	 public ConcurrentHashMap<String,Integer> getRegisteredFileServer(){
		 return peerFileServerAddress;
	 }
	 
	 public void removerIp(String ipport){		
		 System.out.println("Removed "+ ipport);
		 fileDetails.remove(ipport);
		 //System.out.println(fileDetails);
	 }
	 
	 
}
