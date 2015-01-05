package com._2K30.testnetworkandroid.connectivity;

import android.net.ConnectivityManager;

import com._2K30.testnetworkadndroid.common.MyAndroidThread;
import com._2K30.testnetworkandroid.helper.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;

/**
 * Created by 2K30 on 16.12.2014.
 * @author 2K30
 */
public class Server {

    private DatagramSocket m_server = null;
    private InetAddress m_internalAddress = null;
    public DatagramSocket m_serverReceiverSocket = null;
    private ArrayList<DatagramSocket> m_listOfConnectedClients = null;
    private Method m_methodCallOnReceive = null;
    private Object m_methodCaller = null;
    private MyAndroidThread m_MyAndroidThread = null;
    private MyAndroidThread m_checkClientStatesThread = null;
    private Object lock = new Object();
    private InetAddress m_publicAddress;
    public int portToTest = 0;
    public ConnectivityManager conManager;
    public boolean finished = false;
    private Client client;
    public Server (int port, InetAddress address,Method methodOnReceive, Object caller, InetAddress publicAddress) throws IOException, InterruptedException {
      this.initializeServer(port,address,methodOnReceive,caller,publicAddress);
    }

    public Server(int port, InetAddress address,InetAddress publicAddress) throws IOException, InterruptedException {

       this.initializeServer(port,address,publicAddress);
    }

    /**
     * Sends a given message
     * @param message message to send
     * @throws IOException
     */
    public synchronized void sendMessage(String message, Client client) throws IOException {
        this.client = client;
        InetAddress clientAddress = client.getExternelAddress();
        //this.conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
        int clientPort = client.getClientSocket().getLocalPort();//client.getClientSocket().getPort();
        //for( clientPort = 10000; clientPort <65535;clientPort++) {
        //portToTest = client.m_ServerPort;
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, clientAddress, (portToTest==0?clientPort:portToTest));
            this.m_server.send(sendPacket);
        finished = true;
        //}
        //client.sendMessage();
    }

    public void initReceiver(Client client) throws IOException {
        InetAddress clientAddress = client.getExternelAddress();
        //this.conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_MOBILE, "enableHIPRI");
        int clientPort = client.getClientSocket().getLocalPort();//client.getClientSocket().getPort();
        for( clientPort = 1; clientPort <65535;clientPort++) {
            DatagramPacket sendPacket = new DatagramPacket(Constants.DEFAULT_MESSAGE_TO_SEND.getBytes(), Constants.DEFAULT_MESSAGE_TO_SEND.getBytes().length, clientAddress, (portToTest==0?clientPort:portToTest));
            this.m_serverReceiverSocket.send(sendPacket);
        }
        //client.sendMessage();
    }



    /**
     * Sends default message
     * @throws IOException
     */
    public void sendMessage(Client client) throws IOException {
        this.sendMessage(Constants.DEFAULT_MESSAGE_TO_SEND, client);
    }

    public void stop() {
        this.m_server.close();
        this.m_MyAndroidThread.stop();
    }

    public void startAsync() {

        if(m_MyAndroidThread == null) {

            m_MyAndroidThread = new MyAndroidThread(

            //create and start listening thread...
                    new Runnable() {

                        @Override
                        public void run() {

                            byte[] receiveData = new byte[65508];

                            while (!Thread.currentThread().isInterrupted()) {

                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                try {

                                    m_server.receive(receivePacket);
                                    //client.m_ServerPort = receivePacket.getPort();
                                    //call given method for receive only if both elements(method and owner) are not equal null
                                    if (m_methodCallOnReceive != null && m_methodCaller != null) {

                                        try {
                                            m_methodCallOnReceive.invoke(m_methodCaller, receivePacket);
                                        } catch (IllegalAccessException e) {
                                            e.printStackTrace();
                                        } catch (InvocationTargetException e) {
                                            e.printStackTrace();
                                        }
                                        analyseIncomingPacketAndSaveSendingClient(receivePacket);
                                    }//if methodCallOnReceive != null
                                    else {
                                        analyseIncomingPacketAndSaveSendingClient(receivePacket);
                                    }//else
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }//end while
                        }//void run()
                    }
            ); 
            m_MyAndroidThread.start();
        }
        else{
            if(m_MyAndroidThread.getState() == Thread.State.WAITING || m_MyAndroidThread.getState() == Thread.State.TIMED_WAITING){
                return;
            }else{
                //in other case restart the main receive thread
                m_MyAndroidThread.start();
            }
        }
    }

    /**
     * Analyzed the package and remember connected client
     * @param receivePacket packet
     * @throws SocketException
     */
    private void analyseIncomingPacketAndSaveSendingClient(DatagramPacket receivePacket) throws SocketException {
        InetAddress clientAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();
        //DatagramSocket clientSocket = new DatagramSocket(clientPort, clientAddress);
        String message = new String(receivePacket.getData(),0,receivePacket.getLength());
        /*if (!m_listOfConnectedClients.contains(clientSocket)) {
            m_listOfConnectedClients.add(clientSocket);
        }*/
    }

    /**
     * Initialize server and other depended variables
     * @param port server port
     * @param address server address (public ip for accessibility from other networks or internet)
     * @throws SocketException
     */
    private void initializeServer(int port, InetAddress address, InetAddress publicAddress) throws IOException, InterruptedException {
        this.initializeServer(port,address,null,null,publicAddress);
    }

    /**
     * Initialize server and other depended variables
     * @param port server port
     * @param address   internal address
     * @param methodOnReceive on that should call on receive
     * @param caller owner of method on receive
     * @throws SocketException
     */
    private void initializeServer(int port, InetAddress address, Method methodOnReceive, Object caller,InetAddress publicAddress) throws IOException, InterruptedException {

        this.m_methodCaller = caller;
        this.m_methodCallOnReceive = methodOnReceive;
        m_server = new DatagramSocket(port, address);
        m_internalAddress = address;
        this.m_publicAddress = publicAddress;
        Thread.sleep(500);
        if (methodOnReceive != null && caller != null) {

            //set remote call enable
            m_methodCallOnReceive.setAccessible(true);
        }
        m_serverReceiverSocket = new DatagramSocket(0,address);
    }

    private synchronized void checkForStatesOfCliets(){
      //TODO: implement logic for tests of connected clients. Some thing like thread for client 10 clients, running 10 check threads.
    }

    private synchronized void actionOnClietDisconnected(){

    }


    public DatagramSocket getServerSocket(){
        return this.m_server;
    }

    public boolean isConnected(){
        return this.m_server.isConnected();
    }

    public InetAddress getExternalAddress(){return this.m_publicAddress;}

    public InetAddress getInternalAddress(){return this.m_internalAddress;}

}
