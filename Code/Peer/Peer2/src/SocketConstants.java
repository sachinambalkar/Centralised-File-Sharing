import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class SocketConstants 
{		  
  public static final String localIPaddress;
  public static final String PORT = "PORT";
  public static final String IPADDRESS = "IPADDRESS";
  public static final String ABSOLUTEFILEPATH = "ABSOLUTEFILEPATH";
  public static final String FILENAME = "FILENAME";
  public static final String FileServerSocketPort="FileServerSocketPort";
  public static final String SIZE = "SIZE";
  public static final int streamConstant= 65536;
  
  static {
	  localIPaddress=getLocalAddress().getHostAddress().toString();
  }
  private static InetAddress getLocalAddress(){
      try {
          Enumeration<NetworkInterface> b = NetworkInterface.getNetworkInterfaces();
          while( b.hasMoreElements()){
              for ( InterfaceAddress f : b.nextElement().getInterfaceAddresses())
                  if ( f.getAddress().isSiteLocalAddress())
                      return f.getAddress();
          }
      } catch (SocketException e) {
          e.printStackTrace();
      }
      return null;
  }
}
