package com._2K30.testnetworandroid;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;

import com._2K30.testnetworkandroid.helper.Constants;
import com._2K30.testnetworkandroid.helper.MyNetworkHelper;
import com._2K30.testnetworkandroid.helper.NetworkHelperException;
import com.example.testnetworandroid.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.*;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;


public class MainActivity extends Activity {
	
	private RelativeLayout m_mainLayout;
	private RelativeLayout m_preloadedRelativeLayout;
	
	private ConnectivityManager m_connectivityManager;
	
	private MyNetworkHelper m_myNetworkHelper;
	
	private NetworkInterface wifiNetworkInterface = null;
	private NetworkInterface mobileDataNetworkInterface = null;
	
	private ArrayList<NetworkInterface> m_listOfNetworkInterfaces;
	
	private boolean m_showDialog;
	
	private String m_externalIpOfClient = "";
	private String m_externalIpOfServer = "";
	
	private boolean m_processEnded = false;
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       this.initialize();
       
      this.showHideLoadingProcess(true);
       
       new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					executeStartLogic();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NetworkHelperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
    }

    
    @SuppressLint("CutPasteId") @SuppressWarnings("deprecation")
	private void executeStartLogic() throws IOException, NetworkHelperException{
    	
    	try {
			
    		m_myNetworkHelper.enableMobileData(true, this.m_connectivityManager, this);
			
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	//set TYPE_MOBILE to HIPRI
    	this.m_connectivityManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
    	
    	//need to wait one or better two seconds. until the mobile data has been activated...
    	try {
			Thread.sleep(3000);
		} 
    	catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	 //mobile
        State mobile = m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        //WIFI
        State wifiState = m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        
        if(mobile == android.net.NetworkInfo.State.DISCONNECTED || mobile == android.net.NetworkInfo.State.DISCONNECTING)
        {
      	  //mobile
      	 runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				 TextView txt = (TextView)findViewById(R.id.server_text);
				 txt.setText("IP of Server: DISCONNECTED!");
			}
		});
            
        }
        else if (wifiState == android.net.NetworkInfo.State.CONNECTED || wifiState == android.net.NetworkInfo.State.CONNECTING) 
        {
            //WIFI
      	  
        }
        else if(wifiState == android.net.NetworkInfo.State.DISCONNECTED || wifiState == android.net.NetworkInfo.State.DISCONNECTING)
        {
      	  //WFIF
      	    runOnUiThread(new Runnable() {
    			
    			@Override
    			public void run() {
    				TextView txt = (TextView)findViewById(R.id.client_text);
    	            txt.setText("IP of Client: DISCONNECTED!");
    			}
    		});
        } 
        
        try {
			this.m_listOfNetworkInterfaces = this.m_myNetworkHelper.getAvailableNetworkInterfaces();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if(this.m_listOfNetworkInterfaces == null){
        	//TODO: error handle!!!!!
        	return;
        }
        
    
       
       for(NetworkInterface netInterface : this.m_listOfNetworkInterfaces){
			if(netInterface.getName().equals(Constants.WIFI)){
				wifiNetworkInterface = netInterface;
			}
			else if(netInterface.getName().equals(Constants.MOBILE_DATA)){
				mobileDataNetworkInterface = netInterface;
			}
		}
       
      this.m_externalIpOfServer = m_myNetworkHelper.getExternalIpOfInterface(mobileDataNetworkInterface);
      this.m_externalIpOfClient = m_myNetworkHelper.getExternalIpOfInterface(wifiNetworkInterface);
       
       runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				 TextView txtServerInternal = (TextView)findViewById(R.id.server_text);
			       TextView txtClientInternal = (TextView)findViewById(R.id.client_text);
			       
			       //check does WIFI network interface has two addresses:
			       
			       txtClientInternal.setText("IP of Client: "+m_myNetworkHelper.getIpV4AddressOfNetworkInterface(wifiNetworkInterface));
			       txtServerInternal.setText("IP of Server: "+m_myNetworkHelper.getIpV4AddressOfNetworkInterface(mobileDataNetworkInterface));
			       
			       TextView txtServerExternalIp = (TextView)findViewById(R.id.client_external_ip_text);
			       TextView txtClientExternalIp = (TextView)findViewById(R.id.server_external_ip);
			       
			       txtServerExternalIp.setText("External IP of Client: "+m_externalIpOfClient);
			       txtClientExternalIp.setText("External IP of Server: "+m_externalIpOfServer);
			}
		});

       this.showHideLoadingProcess(false);
    }
    
    /**
     * Show the loading dialog or not
     * @param show
     */
    private synchronized void showHideLoadingProcess(boolean show){
    	this.m_showDialog = show;
		runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
		    	 ((Button)findViewById(R.id.button1)).setEnabled(!m_showDialog);
		         ((Button)findViewById(R.id.btn_check_serverIP)).setEnabled(!m_showDialog);
		         ((EditText)findViewById(R.id.txt_client_to_server)).setEnabled(!m_showDialog);
		    	
		         m_preloadedRelativeLayout.setVisibility((m_showDialog ? View.VISIBLE : View.INVISIBLE));
		         m_preloadedRelativeLayout.setBackgroundResource(Color.TRANSPARENT);
		         
		         
		         if(m_showDialog){
		        	 m_preloadedRelativeLayout.bringToFront();
		         }else{
		        	 m_mainLayout.bringToFront();
		         }
					}
		});
    }
    
    /**
     * Initialize members etc. ...
     */
    private void initialize(){
    	 m_mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
    	 m_connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	 m_myNetworkHelper = MyNetworkHelper.getInstance();
         m_preloadedRelativeLayout = (RelativeLayout)findViewById(R.id.preload_layout);
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}
