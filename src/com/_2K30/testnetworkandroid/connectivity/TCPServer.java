package com._2K30.testnetworkandroid.connectivity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kko on 07.01.2015.
 */

//TODO try to connect to target over targetInternPort+X<=maxPort. if not try reverse way port+x<targetInternPort
public class TCPServer {

    private ServerSocket m_tcpServerSocket = null;

    private InetAddress m_internalAddress = null;
    private InetAddress m_externalAddress = null;


    /**
     *
     * @param internalAddress
     * @param externalAddress
     * @param port if port = 0 : socket would be created for default port
     * @throws IOException
     */
    public TCPServer(InetAddress internalAddress, InetAddress externalAddress, int port) throws IOException {

        this.fillMemeber(internalAddress, externalAddress, new ServerSocket(port,-1, internalAddress));
    }


    public TCPServer(InetAddress internalAddress, InetAddress externalAddress, ServerSocket server) {
        this.fillMemeber(internalAddress, externalAddress, server);
    }


    private void fillMemeber(InetAddress internalAddress, InetAddress externalAddress, ServerSocket serverSocket){
        this.m_tcpServerSocket = serverSocket;
        this.m_externalAddress = externalAddress;
        this.m_internalAddress = internalAddress;

    }

    public void tryToBindToTargetSocket(InetAddress targetAddress, int targetPort){

    }


    public void start() throws IOException {
        while(true) {
            Socket connectedClient = m_tcpServerSocket.accept();
            String s = "";
            s = "Acceped!";
        }
    }

    /**
     * Local port
     * @return
     */
    public int getLocalPort(){
        return this.m_tcpServerSocket.getLocalPort();
    }

    /**
     * Public address
     * @return
     */
    public InetAddress getExternalAddress(){
        return this.m_externalAddress;
    }

    /**
     * Private / Internal address
     * @return
     */
    public InetAddress getInternalAddress(){
        return this.m_internalAddress;
    }

}
