/*
 * DHCPServerApp.java
 */

package dhcpserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class DHCPServerApp extends SingleFrameApplication {

    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() {
        show(new DHCPServerView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of DHCPServerApp
     */
    public static DHCPServerApp getApplication() {
        return Application.getInstance(DHCPServerApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) {
        launch(DHCPServerApp.class, args);

        System.out.println("dhcp server launched");

        byte buffer[] = new byte[1000];

        try {
            DatagramSocket socket = new DatagramSocket(67);
            System.out.println("waiting for discover packet");

            while (true) { try {
                // receive
                for (int i = 0; i < 1000; i++) buffer[i] = 0;
                DatagramPacket datagram = new DatagramPacket(buffer, buffer.length);
                socket.receive(datagram);

                // read
                DHCPPacket packet = new DHCPPacket();
                packet.readMessage(buffer, datagram.getLength());

                // write
                for (int i = 0; i < 1000; i++) buffer[i] = 0;
                packet.writeOffer(buffer);

                // send
                DatagramPacket sendPacket = new DatagramPacket(buffer, DHCPPacket.index, InetAddress.getByName("255.255.255.255"), 68);
                socket.send(sendPacket);

                } catch (Exception e) { System.out.println( e.getMessage()); }
            }
        } catch (Exception e) { System.out.println( e.getMessage()); }
    }
}
