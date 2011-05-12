
package dhcpserver;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class DHCPPacket {

    HashMap<String, byte[]> data = new HashMap<String, byte[]>();

    public static byte[] extractBytes(byte[] buffer, int from, int to)
    {
        byte[] result = new byte[to - from];
        System.arraycopy(buffer, index, result, 0, to - from);
        return result;
    }

    public int readMessage (byte[] buffer, int length)
    {
        data.put("xid", extractBytes(buffer, 4, 4));
        data.put("chaddr", extractBytes(buffer, 28, 16));

        int msgType = 0; // invalid 
        byte[] options = extractBytes(buffer, 140, length - 140);
        for (int i = 0; i < options.length; i++)
        {
            byte[] value = extractBytes(options, i+2, i+2 + options[i+1]);
            
            switch (options[i])
            {
                case 53:
                    msgType = value[0];
                break;
            }

            i += options[i+1] + 2;
        }

        return msgType;
    }


    public static byte[] getByteArray(int[] bytes)
    {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            result[i] = (byte) bytes[i];
        return result;
    }

    static int index;
    public static void addBytes(byte[] bytes, byte[] buffer)
    {
        System.arraycopy(bytes, 0, buffer, index, bytes.length);
        index += bytes.length;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{ (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };
    }

    public void writeOffer (byte[] buffer)
    {
        byte[] ip = getByteArray(new int[] {134, 134, 134, 134});

        try {

        InetAddress myIP = InetAddress.getLocalHost();

        index = 0;
        addBytes(getByteArray(new int[] {2}), buffer);
        addBytes(getByteArray(new int[] {1}), buffer);
        addBytes(getByteArray(new int[] {6}), buffer);
        addBytes(getByteArray(new int[] {0}), buffer);
        addBytes(data.get("xid"), buffer);
        addBytes(data.get("secs"), buffer);
        addBytes(data.get("flags"), buffer);
        addBytes(data.get("ciaddr"), buffer);
        addBytes(ip, buffer);
        addBytes(myIP.getAddress(), buffer);
        addBytes(data.get("giaddr"), buffer);
        addBytes(data.get("chaddr"), buffer);
        addBytes(data.get("chaddr"), buffer);
        addBytes(data.get("file"), buffer);

        // options
        
        // Message type = DHCPOFFER
        addBytes(getByteArray(new int[] {53, 1, 2}), buffer); 
        
        // DHCP Server Identifier
        addBytes(getByteArray(new int[] {54, 4}), buffer);
        addBytes(myIP.getAddress(), buffer);

        // Subnet Mask = 255.255.255.0
        addBytes(getByteArray(new int[] {1, 4, 255,255,255,0}), buffer);
        
        // IP Address Lease Time = 1 day
        addBytes(getByteArray(new int[] {51, 4}), buffer);
        addBytes(intToByteArray(24 * 3600), buffer);

        } catch (Exception e) { System.out.println(e.getMessage());}
    }
}
