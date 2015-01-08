package com._2K30.testnetworkandroid.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import com._2K30.testnetworkadndroid.common.Common;
import com._2K30.testnetworkadndroid.common.MyRunnable;
import com._2K30.testnetworkandroid.connectivity.*;

/**
 * Contains most useful methods for network (this application) 
 * @author 2K30
 *
 */
public class MyNetworkHelper {

	private static MyNetworkHelper s_instanceOfMyNetworkHelper;
	public boolean keepInterfaceAllive = true;
	/**
	 * Invisible instance of this class
	 */
	private MyNetworkHelper(){
		//TODO: insert code here
	}
	
	
	
	
	/**
	 * Sets the availability of mobile data service
	 * @param mobileDataEnable
	 * @param conManager
	 * @param activity
	 * @throws NoSuchFieldException
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 */
	public void enableMobileData(boolean mobileDataEnable, ConnectivityManager conManager, Activity activity) 
			throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, InvocationTargetException {
		
		conManager = (ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        this.enableMobileConnectionData(mobileDataEnable,conManager);

	}


    private void enableMobileConnectionData(boolean mobileDataEnable,ConnectivityManager conManager) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Class<?> conmanClass = Class.forName(conManager.getClass().getName());
        Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");

        iConnectivityManagerField.setAccessible(true);

        Object iConnectivityManager = iConnectivityManagerField.get(conManager);

        Class<?> iConnectivityManagerClass = Class.forName(iConnectivityManager.getClass().getName());

        Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);

        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, mobileDataEnable);
    }

	/**
	 * Gets available network interface(s)
	 * @return List of network interfaces
	 * @throws SocketException
	 */
	public ArrayList<NetworkInterface> getAvailableNetworkInterfaces() throws SocketException{
		
		ArrayList<NetworkInterface> listNetworkInterfaces = new ArrayList<NetworkInterface>();
		
		//loop over all connected network interfaces
		for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ){
			NetworkInterface netInterface = en.nextElement();
			for(Enumeration<InetAddress> enumIpAddr = netInterface.getInetAddresses(); enumIpAddr.hasMoreElements();){
				if(!enumIpAddr.nextElement().isLoopbackAddress()){
					listNetworkInterfaces.add(netInterface);
					break;
				}
			}
		}
		
		return listNetworkInterfaces;
		
	}
	
	
	public InetAddress getIpV4AddressOfNetworkInterface(NetworkInterface networkInterface){
		InetAddress ipV4Address = null;
		
		for(Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements();){
			InetAddress address = addresses.nextElement();
			if(InetAddressUtils.isIPv4Address(address.getHostAddress().toString())){
				ipV4Address = address;
				break;
			}
		}
		
		return ipV4Address;
	}
	
	public NetworkInterface getOneNetworkInterfaceForInterfaceName(ArrayList<NetworkInterface> listOfNetworkInterfaces,String name){
		NetworkInterface result = null;
		for(NetworkInterface netInterface : listOfNetworkInterfaces){
			if(netInterface.getName().equals(name)){
				result = netInterface;
				break;
			}
		}
		return result;
	}
	
	/**
	 * get the public (external IP of given network interface)
	 * @param networkInterface 
	 * @return the public IP address
	 * @throws IOException
	 * @throws NetworkHelperException 
	 */
	public String getExternalIpOfInterface(NetworkInterface networkInterface) throws IOException, NetworkHelperException{
		
		if(networkInterface == null){
			throw new NetworkHelperException("Can not determine public ip of interface wich is null!");
		}
		
		Process process = null;

        //process = Runtime.getRuntime().exec("apt-get install hping3");



		String command = "curl --interface "+networkInterface.getName()+" http://ipecho.net/plain";
		
		process = Runtime.getRuntime().exec(command);

		BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
		StringBuilder stringBuilder = new StringBuilder();
		
		char[] chars = new char[1024];
		int i;
		
		//build the result...
		while((i=reader.read(chars))>=0){
			stringBuilder.append(chars,0,i);
		}
		process = null;
		return stringBuilder.toString();
	}

    /**
     * Keep the given interface alive. It is necessary to hold connection. Every reconnect leads to new public/internal address
     * @param networkInterface network interface for holding connection
     * @throws IOException
     * @throws NetworkHelperException
     */
    public synchronized void keepInterfaceAllive(NetworkInterface networkInterface, ConnectivityManager connectivityManager) throws IOException, NetworkHelperException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        //this is the same as ping one internet site
        while(keepInterfaceAllive) {
            this.enableMobileConnectionData(true,connectivityManager);
            connectivityManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
            this.getExternalIpOfInterface(networkInterface);
            try {
                Thread.sleep(1200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

	/* ============================================================
	 * ==================== STATIC SECTION ========================
	 * ============================================================
	 */
	
	public  static  int getOpenPort(InetAddress address){
        return -1;
    }


	/**
	 * Creates / gets the instance of MyNetworkHelper class
	 * @return MyNetworkHelper instance
	 */
	public static MyNetworkHelper getInstance(){
		if(s_instanceOfMyNetworkHelper == null){
			s_instanceOfMyNetworkHelper = new MyNetworkHelper();
		}
		return s_instanceOfMyNetworkHelper;
	}


    public static void ConnectTCPClientToTCPServer(final TCPClient wifiClient, final TCPClient mobileDataClient, final UDPClient udpClient,final UDPServer udpServer) throws NetworkHelperException {
        //this kind of punching not working!!! in case if udp ports are blocked, no one package will go out and come in!
        // possible solution, use extra server with public ip and one open port. connect via TCP Socket. After connection transmit both ips and ports to each client for p2p connection.
        //maybe best solution use ssl port on server to come always out through firewall.

       /* if (wifiClient == null || mobileDataClient == null) {
            throw new NetworkHelperException("TCPClient client is null!!!");
        }
        else if(udpClient == null || udpServer == null){
            throw new NetworkHelperException("UDP client or UDPServer is null!!!");
        }

        udpClient.startAsync();
        udpServer.startAsync();

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    udpServer.sendMessage(udpClient);
                    udpClient.sendMessageForAllPorts("tadaaa");
                } catch (IOException e) {
                    e.printStackTrace();
                }

                final int wifiPort = udpClient.getClientSocket().getLocalPort();
                final int mobilePort = udpServer.getServerSocket().getLocalPort();



            }
        }).start();*/

    }

    /**
     * Create connection between UDPServer and UDPClient (skype like http://www.heise.de/security/artikel/Klinken-putzen-271494.html)
     * This method works only if UDP connection enable in this network! Otherwise this method is useless
     * @param UDPClient UDPClient which should connected to UDPServer
     * @param UDPServer UDPServer
     * @throws NetworkHelperException
     */
    public static void ConnectUDPClientUDPToServer(final UDPClient UDPClient,final UDPServer UDPServer) throws NetworkHelperException, IOException {

        if(UDPClient == null || UDPServer == null){
            throw new NetworkHelperException("UDPClient or UDPServer is NULL!! Can not connect UDPServer and UDPClient!");
        }

        UDPServer.startAsync();
        UDPClient.startAsync();

        new Thread(new MyRunnable(Common.getMethodFromClass(UDPClient.class,"sendMessage")[0], UDPClient)).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!UDPClient.finished){
                    //...
                }
                MyRunnable sendAsync = new MyRunnable(Common.getMethodFromClass(UDPServer.class,"sendMessage")[0], UDPServer, UDPClient);
                new Thread(sendAsync).start();
                while(!UDPServer.finished){
                    //...
                }


                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UDPClient.SendspecialMessage(Constants.DEFAULT_CLIENT_MESSAGE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                //new Thread(new MyRunnable(Common.getMethodFromClass(UDPClient.class,"SendspecialMessage")[0],UDPClient,Constants.DEFAULT_CLIENT_MESSAGE)).start();
                //create UDPClient for mobile data and send message to UDPServer, and back

            }
        }).start();
    }
}
