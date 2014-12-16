package com._2K30.testnetworkandroid.connectivity;

import com._2K30.testnetworkadndroid.common.MyAndroidThread;
import com._2K30.testnetworkadndroid.common.MyRunnable;

import java.io.IOException;
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
public class Server {

    private DatagramSocket m_server;
    private ArrayList<DatagramSocket> m_listOfConnectedClients;
    private Method m_methodCallOnReceive;
    private Object m_methodCaller;
    private MyAndroidThread m_MyAndroidThread;

    public void Server (int port, InetAddress address,Method methodOnReceive, Object caller) throws SocketException {
      this.initializeServer(port,address,methodOnReceive,caller);
    }

    public void Server(int port, InetAddress address) throws SocketException {

       this.initializeServer(port,address,null,null);
    }

    private void startAsync(){
        byte[] sendData = new byte[1024];
        byte[] receiveData = new byte[1024];

    }

    private void initializeServer(int port, InetAddress address, Method methodOnReceive, Object caller) throws SocketException {
        this.m_methodCaller = caller;
        this.m_methodCallOnReceive = methodOnReceive;
        m_server = new DatagramSocket(port,address);
        if(methodOnReceive != null && caller != null) {
            MyRunnable runnable = new MyRunnable(this.m_methodCallOnReceive, this.m_methodCaller, null);
            m_MyAndroidThread = new MyAndroidThread(runnable);
            m_MyAndroidThread.start();
        }else{
            new Thread(new Runnable() {
                @Override
                public void run() {
                    byte[] sendData = new byte[1024];
                    byte[] receiveData = new byte[1024];
                    while (true){

                        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                        try {
                            m_server.receive(receivePacket);
                            InetAddress clientAddress = receivePacket.getAddress();
                            int clientPort = receivePacket.getPort();
                            DatagramSocket clientSocket = new DatagramSocket(clientPort,clientAddress);
                            if(m_listOfConnectedClients.contains(clientSocket)){
                                continue;
                            }
                            else{
                                m_listOfConnectedClients.add(clientSocket);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }



}
