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

import com._2K30.testnetworkandroid.connectivity.*;

/**
 * Contains most useful methods for network (this application) 
 * @author 2K30
 *
 */
public class MyNetworkHelper {

	private static MyNetworkHelper s_instanceOfMyNetworkHelper;
	
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
	
	
	/* ============================================================
	 * ==================== STATIC SECTION ========================
	 * ============================================================
	 */
	
	
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

    /**
     * Create connection between server and client
     * @param client Client which should connected to server
     * @param server Server
     * @throws NetworkHelperException
     */
    public static void ConnectClientToServer(Client client, Server server) throws NetworkHelperException {

        if(client == null || server == null){
            throw new NetworkHelperException("Client or server is NULL!! Can not connect server and client!");
        }



    }
}
