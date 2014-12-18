package com._2K30.testnetworkandroid.connectivity;

import com._2K30.testnetworkadndroid.common.MyAndroidThread;

import java.lang.reflect.Method;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by 2K30 on 16.12.2014.
 * @author 2K30
 */
public class Client {

    private InetAddress m_clientInetAddress = null;
    private Server m_serverConnectedTo = null;
    private int m_clientPort = 0;
    private DatagramSocket m_clientSocket = null;

    private Method m_methodCallOnReceive = null;
    private Object m_methodCaller = null;

    private MyAndroidThread m_MyAndroidThread = null;

    /**
     * Initialize client object with given address and port
     * @param address Internet address
     * @param port port
     */
    public Client(InetAddress address, int port) throws SocketException {
        this.initializeClient(address,port,null,null,null);
    }

    /**
     * Initialize client object with given address, port and Server connecting to
     * @param address Internet address
     * @param port port
     * @param server Server connecting to
     */
    public Client(InetAddress address, int port, Server server) throws SocketException {
        this.initializeClient(address,port,server,null,null);
    }

    /**
     * Initialize client object with given address, port, server connecting to and method which should be called on data received from server, owner of receive method
     * @param address Internet address
     * @param port Port
     * @param server Server connecting to
     * @param methodToCall on message receive from server
     * @param owner owner of method which should be called on received data
     */
    public Client(InetAddress address, int port, Server server, Method methodToCall, Object owner) throws SocketException {
        this.initializeClient(address,port,server,methodToCall,owner);
    }


    public void startAsync(){

    }

    private void initializeClient(InetAddress address,int port, Server server, Method method, Object methodOwner) throws SocketException {

        this.m_clientSocket = new DatagramSocket(port, address);

        this.m_serverConnectedTo = server;

        this.m_methodCallOnReceive = method;
        this.m_methodCaller = methodOwner;


    }

}
