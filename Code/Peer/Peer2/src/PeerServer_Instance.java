import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerServer_Instance extends Thread
{
	  Socket sock;
	  public PeerServer_Instance(Socket server){
	    this.sock=server;
	  }
	  public void run()
	  {
		  InputStream is=null;
//		  System.out.println("Instance1");
		  String filename=new String();
		  String size=new String();
		  try {
			is = sock.getInputStream();
			  ObjectInputStream ois = new ObjectInputStream(is);
			  filename=(String)ois.readObject();			  
//	    	  System.out.println("Processing file :"+filename);
			  if(filename!=null){				  
	    				File file = new File(filename);  		   
	//				    System.out.println("path: "+ file.getAbsolutePath());
					    File myFile = new File(file.getAbsolutePath());
					    int convertByteIntoMB=1024*1024;
					    float filesizeByte=(float)myFile.length();
		//			    System.out.println("byte: "+filesizeByte);
					    float fileSizeMB=filesizeByte/convertByteIntoMB;
					    float fileSizeKB=filesizeByte/1024;
					    boolean showSizeInMB;// = true;
					    int divideConstant;
					    float filesize;
					    if(fileSizeMB>1)
					    {	
			//		    	System.out.println("FileSize: "+ fileSizeMB+" mb");
					    	showSizeInMB=true;
					    	divideConstant=1024*1024;
					    	filesize=fileSizeMB;
					    }
					    else{
					    	showSizeInMB=false;
				//	    	System.out.println("FileSize: "+ fileSizeKB+" kb");
					    	divideConstant=1024;
					    	filesize=fileSizeKB;
					    }

					      int count=1;
					      boolean uploadProgress=true;
					      
						    int dataSize;
						    if(filesizeByte<SocketConstants.streamConstant)
						    	dataSize=(int)filesizeByte;
						    else
						    	dataSize=SocketConstants.streamConstant;
						              
						    byte[] mybytearray;// = new byte[dataSize];

						  BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
					      OutputStream os = sock.getOutputStream();
					      int read;
					      while(uploadProgress){
					    	  mybytearray=new byte[dataSize];
						      read=bis.read(mybytearray, 0,dataSize);	            
						      os.write(mybytearray, 0, mybytearray.length);
						    //  os.flush();	      
						//      System.out.println("uploaded : "+(dataSize*count)/divideConstant+" of "+filesize);
						      count++;
						      if(read<0||dataSize<SocketConstants.streamConstant)
						    	  uploadProgress=false;						      
					      }      
					      sock.close();
					//      System.out.println("Uploaded file "+filesize);
			  }

		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		  
	  }	 	  
}