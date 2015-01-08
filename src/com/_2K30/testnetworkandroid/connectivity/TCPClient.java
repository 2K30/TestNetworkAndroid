package com._2K30.testnetworkandroid.connectivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kko on 07.01.2015.
 */

//TODO try to connect to target over targetInternPort+X<=maxPort. if not try reverse way port+x<targetInternPort
public class TCPClient {

    private InetAddress m_serverDestination = null;
    private InetAddress m_internalAddress = null;
    private InetAddress m_externalAddress = null;
    private int m_serverPort = -1;
    private Socket m_mySocket = null;

    public TCPClient(InetAddress internalAddress, InetAddress publicAddress, InetAddress serverDestination, int destinationPort) {
        this.m_internalAddress = internalAddress;
        this.m_externalAddress = publicAddress;
        this.m_serverDestination = serverDestination;
        this.m_serverPort = destinationPort;
        //create socket
        try {
            m_mySocket = new Socket(m_internalAddress,0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void tryToConnectToTargetAddress() throws IOException {
        m_mySocket.connect(new InetSocketAddress(m_serverDestination,m_serverPort));
    }


    public void startBindingToServer(){
        //
    }

    public InetAddress getInternalAddress(){
        return this.m_internalAddress;
    }

    public InetAddress getExternalAddress(){
        return  this.m_externalAddress;
    }

    public int getLocalPort(){
       return this.m_mySocket.getLocalPort();
    }

    public void closeClient() throws IOException {
        this.m_mySocket.close();
        this.m_mySocket.notify();
    }

}
