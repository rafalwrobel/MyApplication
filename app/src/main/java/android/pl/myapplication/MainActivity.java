package android.pl.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    //public static int SERVERPORT;
    //public static String SERVER_IP;

    private ClientThread clientThread;
    private Thread thread;
    //private LinearLayout msgList;
    //private Handler handler;
    //private EditText edMessage;
    private EditText SERVER_IP;
    private EditText SERVERPORT;
    private Button connectButton;
    private Button volumeUpButton;
    private Button volumeDownButton;
    private Button muteButton;
    private Button previousButton;
    private Button nextButton;
    private Button playButton;
    private Button pauseButton;
    private Button shutdownButton;
    private Button shutdownCancelButton;
    private EditText time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client);

        //handler = new Handler();
        //msgList = findViewById(R.id.msgList);
        //edMessage = findViewById(R.id.edMessage);

        setTitle("Remote Controller - Client");
        connectButton = findViewById(R.id.connectButton);
        volumeUpButton = findViewById(R.id.volumeUPButton);
        volumeDownButton = findViewById(R.id.volumeDownButton);
        muteButton = findViewById(R.id.muteButton);
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        shutdownButton = findViewById(R.id.shutdownOnButton);
        shutdownCancelButton = findViewById(R.id.shutdownOffButton);

        SERVER_IP = findViewById(R.id.editTextIpAddress);
        SERVERPORT = findViewById(R.id.editTextPort);

        time = findViewById(R.id.editTextSetTime);

        connectButton.setOnClickListener(view -> {
            Toast.makeText(MainActivity.this, "Connecting to Server...", Toast.LENGTH_SHORT).show();
            clientThread = new ClientThread();
            thread = new Thread(clientThread);
            thread.start();

            Toast.makeText(MainActivity.this, "Connected to Server...", Toast.LENGTH_SHORT).show();

            SERVER_IP.setVisibility(View.GONE);
            SERVERPORT.setVisibility(View.GONE);
            connectButton.setVisibility(View.GONE);

            volumeDownButton.setVisibility(View.VISIBLE);
            muteButton.setVisibility(View.VISIBLE);
            volumeUpButton.setVisibility(View.VISIBLE);
            playButton.setVisibility(View.VISIBLE);
            previousButton.setVisibility(View.VISIBLE);
            pauseButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.VISIBLE);
            time.setVisibility(View.VISIBLE);
            shutdownButton.setVisibility(View.VISIBLE);
            shutdownCancelButton.setVisibility(View.VISIBLE);

        });



        volumeUpButton.setOnClickListener(view -> {
            clientThread.sendMessage("up");
            System.out.println("up");
        });

        volumeDownButton.setOnClickListener(view -> clientThread.sendMessage("down"));

        muteButton.setOnClickListener(view -> clientThread.sendMessage("mute"));

        previousButton.setOnClickListener(view -> clientThread.sendMessage("prev"));

        nextButton.setOnClickListener(view -> clientThread.sendMessage("next"));

        playButton.setOnClickListener(view -> clientThread.sendMessage("play"));

        pauseButton.setOnClickListener(view -> clientThread.sendMessage("pause"));

        shutdownButton.setOnClickListener(view -> clientThread.sendMessage(time.getText().toString()));

        shutdownCancelButton.setOnClickListener(view -> clientThread.sendMessage("cancel"));


    }

    /*public TextView textView(String message, int color) {
        if (null == message || message.trim().isEmpty()) {
            message = "<Empty Message>";
        }
        TextView tv = new TextView(this);
        tv.setTextColor(color);
        tv.setText(message + " [" + getTime() + "]");
        tv.setTextSize(20);
        tv.setPadding(0, 5, 0, 0);
        return tv;
    }*/

    /*public void showMessage(final String message, final int color) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                msgList.addView(textView(message, color));
            }
        });
    }*/



    class ClientThread implements Runnable {

        private Socket socket;
        private BufferedReader input;

        @Override
        public void run() {

            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP.getText().toString());
                socket = new Socket(serverAddr,Integer.parseInt(SERVERPORT.getText().toString()));
                //socket = new Socket(SERVER_IP,SERVERPORT);

                while (!Thread.currentThread().isInterrupted()) {

                    this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String message = input.readLine();
                    if (null == message || "Disconnect".contentEquals(message)) {
                        Thread.interrupted();
                        message = "Server Disconnected.";
                        // showMessage(message, Color.RED);
                        break;
                    }
                    //showMessage(message, Color.BLUE);
                }

            } catch (IOException e1) {
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