import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatServer extends JFrame {

    private Server server;
    private JTextArea screen;

    public List msgs;

    public ChatServer(){
        msgs = new ArrayList<Message>();
        server = new Server();
        server.getKryo().register(Message.class);
        server.addListener(new Listener(){
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                screen.append("New Client connected\n");
                for (Object msg: msgs){
                    server.sendToTCP(connection.getID(), msg);
                }

            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                screen.append("Client Disconnected\n");
            }

            @Override
            public void received(Connection connection, Object object) {
                super.received(connection, object);
                if (object instanceof Message){
                    Message msg = (Message) object;
                    screen.append(msg.senderName + ": " + msg.text + "\n");
                    server.sendToAllTCP(msg);
                    msgs.add(msg);
                }
            }

        });
        initGuis();
    }

    public void start(){
        setVisible(true);
        screen.append("Server started\n");

        server.start();
        try {
            server.bind(4444);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void initGuis(){
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        screen = new JTextArea();
        screen.setPreferredSize(new Dimension(480,480));
        screen.setBackground(Color.black);
        screen.setForeground(Color.green);
        screen.setEditable(false);
        add(screen);
        pack();
    }

    public static void main(String[] args) {
        ChatServer chatServer = new ChatServer();
        chatServer.start();

    }

}
