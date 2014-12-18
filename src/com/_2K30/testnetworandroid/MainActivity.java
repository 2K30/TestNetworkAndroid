package com._2K30.testnetworandroid;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.lang.reflect.Method;

import com._2K30.testnetworkadndroid.common.Common;
import com._2K30.testnetworkadndroid.common.MyRunnable;
import com._2K30.testnetworkandroid.helper.Constants;
import com._2K30.testnetworkandroid.helper.MyNetworkHelper;
import com._2K30.testnetworkandroid.helper.NetworkHelperException;
import com.example.testnetworandroid.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.*;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    //======== layouts ===========
	private RelativeLayout m_mainLayout;
	private RelativeLayout m_preloadedRelativeLayout;

    //
	private ConnectivityManager m_connectivityManager;


	private MyNetworkHelper m_myNetworkHelper;

    //======= network objects ========
	private NetworkInterface wifiNetworkInterface = null;
	private NetworkInterface mobileDataNetworkInterface = null;

	private Activity mainactivity = null;


	private ArrayList<NetworkInterface> m_listOfNetworkInterfaces;
	
	
	
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
					// trace error! and break up
                    e.printStackTrace();
                    System.exit(0);
                } catch (NetworkHelperException e) {
					// trace error and break up!
					e.printStackTrace();
                    System.exit(0);
				}
			}
		}).start();
    }


	private void executeStartLogic() throws IOException, NetworkHelperException {

        try {

            m_myNetworkHelper.enableMobileData(true, this.m_connectivityManager, this);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
           e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //set TYPE_MOBILE to HIPRI
        this.m_connectivityManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");

        //need to wait one or better two seconds. until the mobile data has been activated...
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //mobile
        State mobile = m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        //WIFI
        State wifiState = m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();

        if (mobile == android.net.NetworkInfo.State.DISCONNECTED || mobile == android.net.NetworkInfo.State.DISCONNECTING) {
            //mobile
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView txt = (TextView) findViewById(R.id.server_text);
                    txt.setText("IP of Server: DISCONNECTED!");
                }
            });

        } else if (wifiState == android.net.NetworkInfo.State.CONNECTED || wifiState == android.net.NetworkInfo.State.CONNECTING) {
            //WIFI

        } else if (wifiState == android.net.NetworkInfo.State.DISCONNECTED || wifiState == android.net.NetworkInfo.State.DISCONNECTING) {
            //WIFI
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView txt = (TextView) findViewById(R.id.client_text);
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

        if (this.m_listOfNetworkInterfaces == null) {
            Log.e("No network interface","No network interfaces have been found!");
            System.exit(0);
            return;
        }


        for (NetworkInterface netInterface : this.m_listOfNetworkInterfaces) {
            if (netInterface.getName().equals(Constants.WIFI)) {
                wifiNetworkInterface = netInterface;
            } else if (netInterface.getName().equals(Constants.MOBILE_DATA)) {
                mobileDataNetworkInterface = netInterface;
            }
        }

        if(wifiNetworkInterface == null || mobileDataNetworkInterface == null){
            mainactivity = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //now wifi connection, not ok for out test case!!! show toast and break up
                    Toast t = Toast.makeText(mainactivity,"No WIFI!!!",Toast.LENGTH_LONG);
                    t.show();

                    showHideLoadingProcess(false);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            mainactivity.finish();
                            System.exit(0);
                        }
                    }
                    ).start();
                }
            });
            return;
        }

            Method[] methods = Common.getMethodFromClass(MainActivity.class,"setTextsInTheGui");

            if(methods.length < 1){
                //fatal error!!!!
                Log.d("FATAL ERROR","Method [setTextsInTheGui] not found!");
                System.exit(0);
                return;
            }

        //create a overwritten runnable object, with action method on calling run().
            MyRunnable myRunnableSetText = new MyRunnable(methods[0],
                                                            this,m_myNetworkHelper.getIpV4AddressOfNetworkInterface(wifiNetworkInterface).getHostAddress(),
                                                            m_myNetworkHelper.getIpV4AddressOfNetworkInterface(mobileDataNetworkInterface).getHostAddress(),
                                                            m_myNetworkHelper.getExternalIpOfInterface(wifiNetworkInterface),
                                                            m_myNetworkHelper.getExternalIpOfInterface(mobileDataNetworkInterface));
    //run on ui thread for update text...
        runOnUiThread(myRunnableSetText);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showHideLoadingProcess(false);
            }
        });
    }

    private void setTextsInTheGui(String internalIpOfClient,String internalIpOfServer,String externalIpOfClient, String externalIpOfServer){

        TextView txtServerInternal = (TextView) findViewById(R.id.server_text);
        TextView txtClientInternal = (TextView) findViewById(R.id.client_text);

        //check does WIFI network interface has two addresses:

        txtClientInternal.setText("IP of Client: " + internalIpOfClient);
        txtServerInternal.setText("IP of Server: " + internalIpOfServer);

        TextView txtServerExternalIp = (TextView) findViewById(R.id.client_external_ip_text);
        TextView txtClientExternalIp = (TextView) findViewById(R.id.server_external_ip);

        txtServerExternalIp.setText("External IP of Client: " + externalIpOfClient);
        txtClientExternalIp.setText("External IP of Server: " + externalIpOfServer);
    }

    /**
     * Show the loading dialog or not
     * @param show true for show loading, false for hide
     */
    private synchronized void showHideLoadingProcess(boolean show){

         ((Button)findViewById(R.id.button1)).setEnabled(!show);
         ((Button)findViewById(R.id.btn_check_serverIP)).setEnabled(!show);
         ((EditText)findViewById(R.id.txt_client_to_server)).setEnabled(!show);

         m_preloadedRelativeLayout.setVisibility((show ? View.VISIBLE : View.INVISIBLE));
         m_preloadedRelativeLayout.setBackgroundResource(Color.TRANSPARENT);


         if(show){
             m_preloadedRelativeLayout.bringToFront();
         }else{
             m_mainLayout.bringToFront();
         }

    }


    public void onDataReceiveServer(DatagramPacket receivePacket){

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
