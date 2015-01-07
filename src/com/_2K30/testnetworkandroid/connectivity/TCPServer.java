package com._2K30.testnetworkandroid.connectivity;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by kko on 07.01.2015.
 */
public class TCPServer {

    private ServerSocket m_tcpServerSocket = null;

    public TCPServer(InetAddress internalAddress, InetAddress externalAddress, int port) throws IOException {

        m_tcpServerSocket = new ServerSocket(443,0,internalAddress);

    }

}
