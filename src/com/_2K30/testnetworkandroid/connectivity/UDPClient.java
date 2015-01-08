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
import java.net.SocketException;

/**
 * Created by 2K30 on 16.12.2014.
 * @author 2K30
 */
public class UDPClient {


    private InetAddress m_clientInetAddress = null;
    private UDPServer m_UDP_serverConnectedTo = null;
    private int m_clientPort = 0;
    private UDPClient UDPClient = this;
    private DatagramSocket m_clientSocket = null;
    private DatagramSocket m_clientSendSocket = null;
    public boolean finished = false;
    private Method m_methodCallOnReceive = null;
    private Object m_methodCaller = null;
    public ConnectivityManager conManager;
    private MyAndroidThread m_MyAndroidThread = null;
    public int m_ServerPort = 0;
    private int portTotest = 0;
    private InetAddress m_publicAddress;
    private boolean breakUp;
    /**
     * Initialize UDPClient object with given address and port
     * @param address Internet address
     * @param port port
     */
    public UDPClient(InetAddress address, int port, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address,port,null,null,null,publicAddress);
    }

    /**
     * Initialize UDPClient object with given address, port and UDPServer connecting to
     * @param address Internet address
     * @param port port
     * @param UDPServer UDPServer connecting to
     */
    public UDPClient(InetAddress address, int port, UDPServer UDPServer, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address,port, UDPServer,null,null,publicAddress);
    }

    /**
     * Initialize UDPClient object with given address, port, UDPServer connecting to and method which should be called on data received from UDPServer, owner of receive method
     * @param address Internet address
     * @param port Port
     * @param UDPServer UDPServer connecting to
     * @param methodToCall on message receive from UDPServer
     * @param owner owner of method which should be called on received data
     */
    public UDPClient(InetAddress address, int port, UDPServer UDPServer, Method methodToCall, Object owner, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address, port, UDPServer, methodToCall, owner, publicAddress);
    }


    public void stop(){
        this.m_clientSocket.disconnect();
        this.m_clientSocket.close();
        this.m_MyAndroidThread.stop();
    }


    public void startAsync(){
        if(m_MyAndroidThread == null) {

            m_MyAndroidThread = new MyAndroidThread(

                    //create and start listening thread...
                    new Runnable() {

                        @Override
                        public void run() {

                            byte[] receiveData = new byte[65508];
                            int counter = 0;
                            while(!Thread.currentThread().isInterrupted()){
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                try {
                                    m_clientSocket.receive(receivePacket);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    continue;
                                }
                                m_ServerPort = receivePacket.getPort();
                                breakUp = true;
                                if(m_methodCaller != null && m_methodCallOnReceive != null){
                                    try {
                                        m_methodCallOnReceive.invoke(m_methodCaller,receivePacket);
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    } catch (InvocationTargetException e) {
                                        e.printStackTrace();
                                    }
                                }//if method and owner != null
                                else{
                                    handleDefaultReceive(receivePacket);
                                }//else
                            }//while receiving (always true)

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
     * Sends a given message
     * @param message message to send
     * @throws IOException
     */
    public synchronized void sendMessage(String message) throws IOException {
        //this.conManager.startUsingNetworkFeature(ConnectivityManager.TYPE_WIFI, "enableHIPRI");
        InetAddress serverAddress = this.m_UDP_serverConnectedTo.getExternalAddress();
        //if(m_ServerPort == 0) {
            int serverPort = this.m_UDP_serverConnectedTo.getServerSocket().getLocalPort();//this.m_UDP_serverConnectedTo.getServerSocket().getPort();
           // for (serverPort = 1; serverPort < 65536; serverPort++) {
                portTotest = serverPort;
                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, serverPort);
                    this.m_clientSocket.send(sendPacket);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(breakUp){
              //      break;
                }
            //}
        //}else{
            //this.sendMessageAsSender(message,serverAddress);
        //}
        this.finished = true;
    }

    public synchronized void sendMessageForAllPorts(String message){
        InetAddress serverAddress = this.m_UDP_serverConnectedTo.getExternalAddress();
        int serverPort = this.m_UDP_serverConnectedTo.getServerSocket().getLocalPort();

        for(int i = serverPort; i < 65536; i++){
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, i);
            try {
                this.m_clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //send reverse
        for(int i = 1; i < serverPort; i++){
            DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, i);
            try {
                this.m_clientSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void SendspecialMessage(String message) throws IOException {
        InetAddress serverAddress = this.m_UDP_serverConnectedTo.getExternalAddress();
        //if(m_ServerPort == 0) {
        int serverPort = m_ServerPort;//this.m_UDP_serverConnectedTo.getServerSocket().getLocalPort();//this.m_UDP_serverConnectedTo.getServerSocket().getPort();
         //for (serverPort = m_ServerPort; serverPort < 65536; serverPort++) {
                portTotest = serverPort;
                DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, serverPort);
                this.m_clientSocket.send(sendPacket);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(breakUp){
                    //      break;
                }
        //}
        //}else{
        //this.sendMessageAsSender(message,serverAddress);
        //}
        this.finished = true;
    }

    private void sendMessageAsSender(String message,InetAddress serverAddress) throws IOException {
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(), message.getBytes().length, serverAddress, m_ServerPort);
        this.m_clientSocket.send(sendPacket);
    }

    /**
     * Sends default message
     * @throws IOException
     */
    public void sendMessage() throws IOException {this.sendMessage(Constants.DEFAULT_CLIENT_MESSAGE);}

    public InetAddress getInternalAddress(){return this.m_clientInetAddress;}

    /**
     * Return the UDPClient socket
     * @return
     */
    public DatagramSocket getClientSocket(){return this.m_clientSocket;}

    public DatagramSocket getClientReceiverSocket(){return this.m_clientSendSocket;}

    /**
     * Default handle on receive
     * @param receivedPacket
     */
    private void handleDefaultReceive(DatagramPacket receivedPacket){
        //TODO:
        String text = new String(receivedPacket.getData(),0,receivedPacket.getLength());
    }

    public InetAddress getExternelAddress(){return this.m_publicAddress;}

    private void initializeClient(InetAddress address,int port, UDPServer UDPServer, Method method, Object methodOwner,InetAddress publicAddress) throws SocketException, InterruptedException {

        this.m_clientInetAddress = address;
        this.m_clientSocket = new DatagramSocket(port, address);
        this.m_publicAddress = publicAddress;
        Thread.sleep(500);
        //m_clientSocket.bind(socketAddress);
        this.m_clientPort = this.m_clientSocket.getLocalPort();
        this.m_UDP_serverConnectedTo = UDPServer;

        this.m_methodCallOnReceive = method;
        this.m_methodCaller = methodOwner;

        this.m_clientSendSocket = new DatagramSocket(0,address);

        if(this.m_methodCallOnReceive != null){
            this.m_methodCallOnReceive.setAccessible(true);
        }
    }

}
