package com.shwinr.homie2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import android.util.Log;


/**
 * TcpClient class to handle conversations over IP
 * File created by ashwin on 06/10/2019
 * HEAVILY borrowed from https://stackoverflow.com/questions/38162775/really-simple-tcp-client
 */

public class TcpClient {

    public static final String SERVER_IP = Consts.SERVER_IP; //server IP address
    public static final int SERVER_PORT = Consts.SERVER_PORT; //TCP/IP Port
    public static final int BUFFER_TIMEOUT = Consts.BUFFER_TIMEOUT; //Timeout for buffered read
    public static final String TAG = TcpClient.class.getSimpleName();

    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;
    //Log messages
    public ArrayList<String> mLog = new ArrayList<String>();



    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }


    /**
     * Return log lines when asked
     */
    public ArrayList<String> getLog(){
        return mLog;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    /**
     public void sendMessage(final String message) {
     Runnable runnable = new Runnable() {
    @Override
    public void run() {
    if (mBufferOut != null) {
    mLog.add(String.format("Sending: %s\n" , message));
    mBufferOut.println(message);
    mBufferOut.flush();
    }
    }
    };
     Thread thread = new Thread(runnable);
     thread.start();
     }*/

    /**
     * Close the connection and release the members
     */
    public void stopClient() {

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }


    private void addToLog(String message){
        mLog.add(message);
        mMessageListener.messageReceived(message);
        Log.d(TAG, message);
    }

    public void run(String message) {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
            addToLog("TCP Client Connecting...");


            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVER_PORT);

            try {

                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                if (mBufferOut != null) {
                    addToLog(String.format("Sending: %s\n" , message));
                    mBufferOut.println(message);
                    mBufferOut.flush();
                }
                else{
                    addToLog(String.format("Error sending to buffer! mBufferOut was empty!"));
                    throw new IOException("Error sending to Socket Output Stream! mBufferOut empty on creation!");
                }


                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                //in this while the client listens for the messages sent by the server
                int bufferIncrement= 0;
                do{
                    bufferIncrement +=1;

                    mServerMessage = mBufferIn.readLine();
                    //call the method messageReceived from MyActivity class
                    addToLog(String.format("<RECEIVED> : '%s'" , mServerMessage));


                }while(mServerMessage != null && mMessageListener != null && ! mServerMessage.equals(Consts.BUFFER_END));



            } catch (Exception e) {
                addToLog(String.format("Server: Error %s\n", e.toString()));

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
                addToLog(String.format("Closed Socket\n"));

            }

        } catch (Exception e) {
            addToLog(String.format("Client: Error %s\n", e.toString()));
        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the Activity
    //class at on AsyncTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }

}


