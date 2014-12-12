package com._2K30.testnetworkandroid.helper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

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
	
	public ArrayList<NetworkInterface> getAvailableNetworkInterfaces(){
		
		ArrayList<NetworkInterface> listNetworkInterfaces = new ArrayList<NetworkInterface>();
		
		
		
		return listNetworkInterfaces;
		
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
	
}
