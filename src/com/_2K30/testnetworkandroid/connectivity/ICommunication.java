package com._2K30.testnetworkandroid.connectivity;

/**
 * Created by 2K30 on 27.02.2015.
 */
public interface ICommunication {

    public void fireForGivenPorts(int[] ports);

    public void startToListen();

    public void startToListen(int[] ports);

    public void startAsync();

    public void stop();

    public void sendMessage();

    public void sendMessage(String message);

}
