package com._2K30.testnetworkandroid.connectivity;

import com._2K30.testnetworkadndroid.common.MyAndroidThread;
import com._2K30.testnetworkandroid.helper.Constants;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.SocketChannel;

/**
 * Created by 2K30 on 16.12.2014.
 * @author 2K30
 */
public class Client{


    private InetAddress m_clientInetAddress = null;
    private Server m_serverConnectedTo = null;
    private int m_clientPort = 0;

    private DatagramSocket m_clientSocket = null;

    private Method m_methodCallOnReceive = null;
    private Object m_methodCaller = null;

    private MyAndroidThread m_MyAndroidThread = null;

    private InetAddress m_publicAddress;

    /**
     * Initialize client object with given address and port
     * @param address Internet address
     * @param port port
     */
    public Client(InetAddress address, int port, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address,port,null,null,null,publicAddress);
    }

    /**
     * Initialize client object with given address, port and Server connecting to
     * @param address Internet address
     * @param port port
     * @param server Server connecting to
     */
    public Client(InetAddress address, int port, Server server, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address,port,server,null,null,publicAddress);
    }

    /**
     * Initialize client object with given address, port, server connecting to and method which should be called on data received from server, owner of receive method
     * @param address Internet address
     * @param port Port
     * @param server Server connecting to
     * @param methodToCall on message receive from server
     * @param owner owner of method which should be called on received data
     */
    public Client(InetAddress address, int port, Server server, Method methodToCall, Object owner, InetAddress publicAddress) throws SocketException, InterruptedException {
        this.initializeClient(address,port,server,methodToCall,owner,publicAddress);
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

                            byte[] receiveData = new byte[1024];

                            while(!Thread.currentThread().isInterrupted()){
                                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                                try {
                                    m_clientSocket.receive(receivePacket);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    continue;
                                }

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
    public void sendMessage(String message) throws IOException {
        InetAddress serverAddress = this.m_serverConnectedTo.getExternalAddress();
        int serverPort = 8888;//this.m_serverConnectedTo.getServerSocket().getPort();
        DatagramPacket sendPacket = new DatagramPacket(message.getBytes(),message.getBytes().length,serverAddress,serverPort);
        this.m_clientSocket.send(sendPacket);
    }

    /**
     * Sends default message
     * @throws IOException
     */
    public void sendMessage() throws IOException {this.sendMessage(Constants.DEFAULT_MESSAGE_TO_SEND);}

    /**
     * Return the Client socket
     * @return
     */
    public DatagramSocket getClientSocket(){return this.m_clientSocket;}

    /**
     * Default handle on receive
     * @param receivedPacket
     */
    private void handleDefaultReceive(DatagramPacket receivedPacket){
        //TODO:
    }

    public InetAddress getExternelAddress(){return this.m_publicAddress;}

    private void initializeClient(InetAddress address,int port, Server server, Method method, Object methodOwner,InetAddress publicAddress) throws SocketException, InterruptedException {

        this.m_clientInetAddress = address;
        this.m_clientSocket = new DatagramSocket(8888, address);
        SocketAddress socketAddress = new InetSocketAddress(address,8888);
        this.m_publicAddress = publicAddress;
        Thread.sleep(500);
        //m_clientSocket.bind(socketAddress);
        this.m_clientPort = 8091;
        this.m_serverConnectedTo = server;

        this.m_methodCallOnReceive = method;
        this.m_methodCaller = methodOwner;


        if(this.m_methodCallOnReceive != null){
            this.m_methodCallOnReceive.setAccessible(true);
        }
    }

}
