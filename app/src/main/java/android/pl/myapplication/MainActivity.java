package android.pl.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private ClientThread clientThread;
    private Thread thread;

    private EditText SERVER_IP;
    private EditText SERVERPORT;
    private Button connectButton;
    private Button volumeUpButton;
    private Button volumeDownButton;
    private Button muteButton;
    private Button shutdownNowButton;
    private Button shutdownAfterButton;
    private Button shutdownAfterOffButton;
    private EditText time;
    private int isMute = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);


        setTitle("Pilot PC - Client");
        connectButton = findViewById(R.id.connectButton);
        volumeUpButton = findViewById(R.id.volumeUPButton);
        volumeDownButton = findViewById(R.id.volumeDownButton);
        muteButton = findViewById(R.id.muteButton);
        shutdownNowButton = findViewById(R.id.shutdownNowButton);
        shutdownAfterButton = findViewById(R.id.shutdownAfterButton);
        shutdownAfterOffButton = findViewById(R.id.shutdownAfterOffButton);

        SERVER_IP = findViewById(R.id.editTextIpAddress);
        SERVERPORT = findViewById(R.id.editTextPort);

        time = findViewById(R.id.editTextSetTime);

        connectButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Connecting to Server...", Toast.LENGTH_SHORT).show();
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();

            Toast.makeText(MainActivity.this, "Connected to Server! :) ", Toast.LENGTH_SHORT).show();

            SERVER_IP.setVisibility(View.GONE);
            SERVERPORT.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);

            volumeDownButton.setVisibility(View.VISIBLE);
            muteButton.setVisibility(View.VISIBLE);
            volumeUpButton.setVisibility(View.VISIBLE);
            shutdownNowButton.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            shutdownAfterButton.setVisibility(View.VISIBLE);
            shutdownAfterOffButton.setVisibility(View.VISIBLE);

                //Toast.makeText(MainActivity.this, "Problem with connection! :(", Toast.LENGTH_SHORT).show();



        });



        volumeUpButton.setOnClickListener(view -> clientThread.sendMessage("up"));

        volumeDownButton.setOnClickListener(view -> clientThread.sendMessage("down"));

        muteButton.setOnClickListener(view -> {

            if(isMute == 1)
            {
                clientThread.sendMessage("mute");
                muteButton.setText("Wył. wyciszenie");
                isMute = 2;
            }
            else
            {
                clientThread.sendMessage("mute");
                muteButton.setText("Wł. wyciszenie");
                isMute = 1;
            }
    });

        shutdownNowButton.setOnClickListener(view -> clientThread.sendMessage("now"));

        shutdownAfterButton.setOnClickListener(view -> clientThread.sendMessage(time.getText().toString()));

        shutdownAfterOffButton.setOnClickListener(view -> clientThread.sendMessage("cancel"));


    }



    class ClientThread implements Runnable {

        private Socket socket;

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP.getText().toString());
                socket = new Socket(serverAddr,Integer.parseInt(SERVERPORT.getText().toString()));

            } catch (IOException e1) {
                Thread.interrupted();
                e1.printStackTrace();
            }

        }

        void sendMessage(final String message) {
            new Thread(() -> {
                try {
                    if (null != socket) {
                        PrintWriter out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                                true);
                        out.println(message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != clientThread) {
            clientThread.sendMessage("Disconnect");
            clientThread = null;
        }
    }
}