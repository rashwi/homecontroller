package com.shwinr.homie2;

import android.widget.TextView;
import android.os.AsyncTask;

import java.util.ArrayList;


/**
 * ConnectTask class built to separate AsyncTask
 * AsyncTask being used to run tcp/ip comms
 */

public class ConnectTask extends AsyncTask<String, String, TcpClient> {

    //AsyncResponse delegate
    public AsyncResponse delegate = null;

    @Override
    protected TcpClient doInBackground(String... message) {

        //we create a TCPClient object
        TcpClient mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
            }
        });
        mTcpClient.run(message[0]);

        return mTcpClient;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        //response received from server
        //process server response here....
        for(String line : values){
            System.out.println(String.format("<ASYNC> : %s", line));
        }


    }

    protected void onPostExecute(TcpClient... clients){
        ArrayList<String> mLog = clients[0].getLog();
        delegate.processFinish(mLog);
    }
}

