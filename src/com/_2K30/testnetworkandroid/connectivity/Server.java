package com._2K30.testnetworkandroid.connectivity;

import com._2K30.testnetworkadndroid.common.MyAndroidThread;
import com._2K30.testnetworkandroid.helper.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by 2K30 on 16.12.2014.
 * @author 2K30
 */
public class Server implements IClientServer {

    private DatagramSocket m_server = null;
    private ArrayList<DatagramSocket> m_listOfConnectedClients = null;
    private Method m_methodCallOnReceive = null;
    private Object m_methodCaller = null;
    private MyAndroidThread m_MyAndroidThread = null;
    private MyAndroidThread m_checkClientStatesThread = null;
    private Object lock = new Object();
    public void Server (int port, InetAddress address,Method methodOnReceive, Object caller) throws SocketException {
      this.initializeServer(port,address,methodOnReceive,caller);
    }

    public void Server(int port, InetAddress address) throws SocketException {

       this.initializeServer(port,address);
    }

    /**
     * Sends a given message
     * @param message message to send
     * @throws IOException
     */
    @Override
    public void sendMessage(String message) throws IOException {

    }

    /**
     * Sends default message
     * @throws IOException
     */
    @Override
    public void sendMessage() throws IOException {
        this.sendMessage(Constants.DEFAULT_MESSAGE_TO_SEND);
    }

    @Override
    public void stop() {
        this.m_server.close();
        this.m_MyAndroidThread.stop();
    }

    @Override
    public void startAsync() {

        if(m_MyAndroidThread == null) {

            m_MyAndroidThread = new MyAndroidThread(

            //create and start listening thread...
                    new Runnable() {

                        @Override
                        public void run() {

                            byte[] receiveData = new byte[1024];

                            while (!Thread.currentThread().isInterrupted()) {

                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                try {

                                    m_server.receive(receivePacket);

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
        DatagramSocket clientSocket = new DatagramSocket(clientPort, clientAddress);

        if (!m_listOfConnectedClients.contains(clientSocket)) {
            m_listOfConnectedClients.add(clientSocket);
        }
    }

    /**
     * Initialize server and other depended variables
     * @param port server port
     * @param address server address (public ip for accessibility from other networks or internet)
     * @throws SocketException
     */
    private void initializeServer(int port, InetAddress address) throws SocketException {
        this.initializeServer(port,address,null,null);
    }

    /**
     * Initialize server and other depended variables
     * @param port server port
     * @param address   server address (public ip for accessibility from other networks or internet)
     * @param methodOnReceive on that should call on receive
     * @param caller owner of method on receive
     * @throws SocketException
     */
    private void initializeServer(int port, InetAddress address, Method methodOnReceive, Object caller) throws SocketException {

        this.m_methodCaller = caller;
        this.m_methodCallOnReceive = methodOnReceive;

        m_server = new DatagramSocket(port, address);

        if (methodOnReceive != null && caller != null) {

            //set remote call enable
            m_methodCallOnReceive.setAccessible(true);
        }
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


}
