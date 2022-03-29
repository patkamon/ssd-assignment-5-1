import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.sound.sampled.Port;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;

public class ChatClient extends JFrame {

    public static final int PORT = 4444;
    public static final String IP = "127.0.0.1";

    private JTextField inputName;
    private JTextArea msgPane;
    private JTextField inputMsg;

    private Client client;

    public ChatClient(){

        client = new Client();
        client.getKryo().register(Message.class);
        client.addListener(new Listener(){
            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof Message){
                    Message msg = (Message) object;
                    msgPane.append(msg.senderName + ": " + msg.text + "\n");
                }
            }
        });
        initGuis();
    }

    public void start(){
        client.start();
        try {
            client.connect(4444, IP, PORT);
        }
        catch (IOException e){
            System.out.println(e);
        }
        setVisible(true);
    }


    private void initGuis(){
        setTitle("Chat Title");
        inputName = new JTextField("None");
        msgPane = new JTextArea();
        msgPane.setPreferredSize(new Dimension(300,300));
        msgPane.setEditable(false);
        msgPane.setBackground(Color.LIGHT_GRAY);
        inputMsg = new JTextField();

        inputMsg.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER) {

                    Message msg = new Message();
                    msg.text = inputMsg.getText();
                    msg.senderName = inputName.getText();
                    client.sendTCP(msg);
                    System.out.println(msg);


                    inputMsg.setText("");
                }
            }
        });

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(inputName, BorderLayout.NORTH);
        add(msgPane, BorderLayout.CENTER);
        add(inputMsg, BorderLayout.SOUTH);
        pack();
    }


    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient();
        chatClient.start();
    }
}
