package com._2K30.testnetworandroid;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.lang.reflect.Method;
import com._2K30.testnetworkadndroid.common.*;
import com._2K30.testnetworkadndroid.common.MyRunnable;
import com._2K30.testnetworkandroid.connectivity.*;
import com._2K30.testnetworkandroid.helper.Constants;
import com._2K30.testnetworkandroid.helper.MyNetworkHelper;
import com._2K30.testnetworkandroid.helper.NetworkHelperException;
import com.example.testnetworandroid.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    //======== layouts ===========
	private RelativeLayout m_mainLayout;
	private RelativeLayout m_preloadedRelativeLayout;
    private AbsoluteLayout m_boolLoadingLayout;
    //
	private ConnectivityManager m_connectivityManager;

    private Context mainContext = this;

	private MyNetworkHelper m_myNetworkHelper;

    //======= network objects ========
	private NetworkInterface wifiNetworkInterface = null;
	private NetworkInterface mobileDataNetworkInterface = null;

	private Activity mainactivity = null;

    private boolean m_enableToUsePreload = false;
	private ArrayList<NetworkInterface> m_listOfNetworkInterfaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainContext = this;
        setContentView(R.layout.activity_main);

        SurfaceView view = (SurfaceView)findViewById(R.id.surfaceView);
        GifRunCommon g = new GifRunCommon();
        g.LoadGiff(view,mainContext,R.drawable.bootloading);

        this.initialize();
        this.showBootAnimation(true);

      //this.showHideLoadingProcess(true);
       
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
				} catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
		}).start();
    }


	private void executeStartLogic() throws IOException, NetworkHelperException, InterruptedException {

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


        if (m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == android.net.NetworkInfo.State.DISCONNECTED || m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == android.net.NetworkInfo.State.DISCONNECTING) {
            //mobile
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView txt = (TextView) findViewById(R.id.server_text);
                    txt.setText("IP of UDPServer: DISCONNECTED!");
                }
            });

        } else if (m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.CONNECTED || m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.CONNECTING) {
            //WIFI

        } else if (m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.DISCONNECTED || m_connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == android.net.NetworkInfo.State.DISCONNECTING) {
            //WIFI
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    TextView txt = (TextView) findViewById(R.id.client_text);
                    txt.setText("IP of UDPClient: DISCONNECTED!");
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
                                                            this,
                                                            m_myNetworkHelper.getIpV4AddressOfNetworkInterface(wifiNetworkInterface).getHostAddress(),
                                                            m_myNetworkHelper.getIpV4AddressOfNetworkInterface(mobileDataNetworkInterface).getHostAddress(),
                                                            m_myNetworkHelper.getExternalIpOfInterface(wifiNetworkInterface),
                                                            m_myNetworkHelper.getExternalIpOfInterface(mobileDataNetworkInterface));
    //run on ui thread for update text...
        runOnUiThread(myRunnableSetText);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showBootAnimation(false);

                showHideLoadingProcess(false);
            }
        });

        //now create connection between UDPServer and UDPClient
        final UDPServer UDPServer = new UDPServer(0,m_myNetworkHelper.getIpV4AddressOfNetworkInterface(mobileDataNetworkInterface),Common.getMethodFromClass(this.getClass(),"onDataReceiveServer")[0],this,InetAddress.getByName(m_myNetworkHelper.getExternalIpOfInterface(mobileDataNetworkInterface)));
        final UDPClient UDPClient = new UDPClient(m_myNetworkHelper.getIpV4AddressOfNetworkInterface(wifiNetworkInterface),0, UDPServer,/*Common.getMethodFromClass(this.getClass(),"onDataReceiveServer")[0],this,*/InetAddress.getByName(m_myNetworkHelper.getExternalIpOfInterface(wifiNetworkInterface)));
        UDPServer.conManager = this.m_connectivityManager;
        UDPClient.conManager = this.m_connectivityManager;

        final UDPServer UDPServerReceive = new UDPServer(0,m_myNetworkHelper.getIpV4AddressOfNetworkInterface(mobileDataNetworkInterface),Common.getMethodFromClass(this.getClass(),"onDataReceiveServer")[0],this,InetAddress.getByName(m_myNetworkHelper.getExternalIpOfInterface(mobileDataNetworkInterface)));
        final UDPClient UDPClientSender = new UDPClient(m_myNetworkHelper.getIpV4AddressOfNetworkInterface(wifiNetworkInterface),0, UDPServerReceive,/*Common.getMethodFromClass(this.getClass(),"onDataReceiveServer")[0],this,*/InetAddress.getByName(m_myNetworkHelper.getExternalIpOfInterface(wifiNetworkInterface)));


        MyRunnable keepConnectivity = new MyRunnable(Common.getMethodFromClass(MyNetworkHelper.class,"keepInterfaceAllive")[0],m_myNetworkHelper,mobileDataNetworkInterface,this.m_connectivityManager);
        MyAndroidThread keepAliveConnectivityThread  = new MyAndroidThread(keepConnectivity);
        keepAliveConnectivityThread.start();

        MyNetworkHelper.ConnectClientToServer(UDPClient, UDPServer);



        //MyNetworkHelper.ConnectServerToClient(UDPClientSender,UDPServerReceive);
                ((Button) findViewById(R.id.button1)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            String message = ((EditText) findViewById(R.id.txt_client_to_server)).getText().toString();
                            //UDPClientSender.sendMessage(message);
                            //UDPClient.sendMessage(message);
                            //UDPServer.sendMessage(message, UDPClient);
                            UDPClient.SendspecialMessage(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    private void setTextsInTheGui(String internalIpOfClient,String internalIpOfServer,String externalIpOfClient, String externalIpOfServer){
            TextView txtServerInternal = (TextView) findViewById(R.id.server_text);
            TextView txtClientInternal = (TextView) findViewById(R.id.client_text);

            //check does WIFI network interface has two addresses:

            txtClientInternal.setText("IP of UDPClient: " + internalIpOfClient);
            txtServerInternal.setText("IP of UDPServer: " + internalIpOfServer);

            TextView txtServerExternalIp = (TextView) findViewById(R.id.client_external_ip_text);
            TextView txtClientExternalIp = (TextView) findViewById(R.id.server_external_ip);

            txtServerExternalIp.setText("External IP of UDPClient: " + externalIpOfClient);
            txtClientExternalIp.setText("External IP of UDPServer: " + externalIpOfServer);
    }

    private void showBootAnimation(boolean show){
        this.m_enableToUsePreload = !show;
        this.m_boolLoadingLayout.setVisibility((show ? View.VISIBLE : View.INVISIBLE));
        this.m_preloadedRelativeLayout.setVisibility((!show) ? View.VISIBLE : View.INVISIBLE);
        if(show){
            m_boolLoadingLayout.bringToFront();
        }else{
           m_mainLayout.bringToFront();
        }
    }


    /**
     * Show the loading dialog or not
     * @param show true for show loading, false for hide
     */
    private synchronized void showHideLoadingProcess(boolean show){

        if(!this.m_enableToUsePreload){return;}


         Button btn1 = (Button)findViewById(R.id.button1);
         btn1.setEnabled(!show);

         btn1 = (Button)findViewById(R.id.btn_check_serverIP);
         btn1.setEnabled(!show);

         EditText txt = (EditText)findViewById(R.id.txt_client_to_server);
         txt.setEnabled(!show);

         m_preloadedRelativeLayout.setVisibility((show ? View.VISIBLE : View.INVISIBLE));
         m_preloadedRelativeLayout.setBackgroundResource(Color.TRANSPARENT);


         if(show){
             m_preloadedRelativeLayout.bringToFront();
         }else{

             m_mainLayout.bringToFront();

         }

    }


    public void onDataReceiveServer(final DatagramPacket receivePacket){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String message = new String(receivePacket.getData(),0,receivePacket.getLength());
                //Toast.makeText(mainactivity,message,Toast.LENGTH_LONG);
                ((EditText)findViewById(R.id.editText2)).setText(message);
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
        m_boolLoadingLayout = (AbsoluteLayout)findViewById(R.id.bootLoadingLayout);
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

        switch (id){
            case R.id.action_settings:
                return true;
            case R.id.turn_on_gps:
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                return true;
            case R.id.start_stream:
                return true;
            case R.id.turn_off_gps:
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy(){
        this.m_myNetworkHelper.keepInterfaceAllive = false;
        super.onDestroy();
    }
}
