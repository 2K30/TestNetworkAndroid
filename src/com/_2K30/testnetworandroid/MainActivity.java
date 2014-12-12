package com._2K30.testnetworandroid;

import java.lang.reflect.InvocationTargetException;

import com._2K30.testnetworkandroid.helper.MyNetworkHelper;
import com.example.testnetworandroid.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;


public class MainActivity extends Activity {
	
	private RelativeLayout m_mainLayout;
	private ConnectivityManager m_connectivityManager;
	private MyNetworkHelper m_myNetworkHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
       this.initialize();
       
       new Thread(new Runnable() {
			
			@Override
			public void run() {
				executeStartLogic();
			}
		}).start();
    }

    
    private void executeStartLogic(){
    	
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
    	
    	
    	
    	
    }
    
    /**
     * Initialize members etc. ...
     */
    private void initialize(){
    	 m_mainLayout = (RelativeLayout)findViewById(R.id.mainLayout);
    	 m_connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
    	 m_myNetworkHelper = MyNetworkHelper.getInstance();
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
