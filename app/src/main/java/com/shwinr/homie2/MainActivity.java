package com.shwinr.homie2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements AsyncResponse{



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        //Redirect stdout
        final TextView editText = findViewById(R.id.textStdout);

        System.setOut(new PrintStream(new OutputStream() {

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            @Override public void write(int oneByte) throws IOException {
                outputStream.write(oneByte);

                editText.setText(new String(outputStream.toByteArray()));
            }
        }));

        editText.setMovementMethod(new ScrollingMovementMethod());

        //find seekbar and initialize
        SeekBar dimmerA = findViewById(R.id.dimmerA);

        dimmerA.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            int progress = 0;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progresValue, boolean fromUser) {
                progress = progresValue;
                System.out.println("Changing seekbar's progress");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                System.out.println("Started tracking seekbar");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                commandBulb2(progress);

            }
        });
    }





    @Override
    public void processFinish(ArrayList<String> logMessages){
        System.out.println("TCP Task Log:");
        for(String line : logMessages){
            System.out.println(line);
        }

    }


    public void turnOn(View view){
        TextView textOut = findViewById(R.id.textStatus);
        textOut.setText("On button Pressed!");
        //Testing the System.out stream
        System.out.println("On button Pressed!");

        // Get address for the local server
        System.out.println("Trying to connect...");

        //create a TCP/IP connection and send ON command
        ConnectTask TcpTask = new ConnectTask();
        //Connect this task to TcpTask
        TcpTask.delegate = this;
        TcpTask.execute(Consts.ON);
        System.out.println("Async Task kicked off.");


    }

    public void commandBulb1(int brightness){
        TextView textOut = findViewById(R.id.textStatus);
        textOut.setText(String.format("Brightness : %d", brightness));

        System.out.println(String.format("Setting %s brightness to %d", Consts.BULB_1, brightness));
        ConnectTask TcpTask = new ConnectTask();
        TcpTask.delegate = this;
        TcpTask.execute(String.format("%s %d", Consts.BULB_1, brightness));
        System.out.println("Async task kicked off");

    }

    public void commandBulb2(int brightness){
        TextView textOut = findViewById(R.id.textStatus);
        textOut.setText(String.format("Brightness : %d", brightness));

        System.out.println(String.format("Setting %s brightness to %d", Consts.BULB_2, brightness));
        ConnectTask TcpTask = new ConnectTask();
        TcpTask.delegate = this;
        TcpTask.execute(String.format("%s %d", Consts.BULB_2, brightness));
        System.out.println("Async task kicked off");

    }

    public void turnOff(View view){
        //Code to turn off light
        TextView textOut = findViewById(R.id.textStatus);
        textOut.setText("Off button Pressed!");
        //Testing the System.out stream
        System.out.println("Off button Pressed!");


        System.out.println("Trying to connect...");
        //create a TCP/IP connection and send ON command
        ConnectTask TcpTask = new ConnectTask();
        //Connect this task to TcpTask
        TcpTask.delegate = this;
        TcpTask.execute(Consts.OFF);
        System.out.println("Async Task kicked off.");
    }

}
