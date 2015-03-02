package com._2K30.testnetworkandroid.connectivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by 2K30 on 07.01.2015.
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
            m_mySocket = new Socket(m_internalAddress,4444);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tryToConnectToTargetAddress() throws IOException {
        try {
            InetSocketAddress address = new InetSocketAddress(m_serverDestination, m_serverPort);
            if(m_mySocket == null){
                m_mySocket = new Socket(m_serverDestination,m_serverPort);
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void tryToConnectToTargetAddress(InetAddress Inetaddress,int port) throws IOException {
            InetSocketAddress address = new InetSocketAddress(Inetaddress, port);
            if(m_mySocket == null){
                m_mySocket = new Socket(Inetaddress,port);
            }
    }

    public void tryToConnetOnAllPorts(InetAddress address){
        int localPort = -1;
        try {
            ServerSocket ss = new ServerSocket(0,0,this.m_internalAddress);
            localPort = ss.getLocalPort();
            ss.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 1; i < 65536; i++){
            try {
                m_mySocket = new Socket(address,i,this.m_internalAddress,localPort);
                Thread.sleep(100);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }
        return;
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
        if (this.m_mySocket == null) {
            return;
        }
        this.m_mySocket.close();
        this.m_mySocket.notify();
    }

}
