package com._2K30.testnetworkandroid.connectivity;

import java.io.IOException;

/**
 * Created by 2K30 on 19.12.14.
 */
public interface IClientServer {

    void sendMessage(String message) throws IOException;

    void sendMessage() throws IOException;


    void stop();

    void startAsync();

}
