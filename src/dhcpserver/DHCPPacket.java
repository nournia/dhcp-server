
package dhcpserver;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class DHCPPacket {

    HashMap<String, byte[]> data = new HashMap<String, byte[]>();

    public static byte[] extractBytes(byte[] buffer, int from, int length)
    {
        byte[] result = new byte[length];
        System.arraycopy(buffer, from, result, 0, length);
        return result;
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
        return new byte[]{
            (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };
    }

    public void readDiscover (byte[] buffer)
    {
//        data.put("op", extractBytes(buffer, 0, 1));
//        data.put("htype", extractBytes(buffer, 1, 1));
//        data.put("hlen", extractBytes(buffer, 2, 1));
//        data.put("hops", extractBytes(buffer, 3, 1));
        data.put("xid", extractBytes(buffer, 4, 4));
        data.put("secs", extractBytes(buffer, 8, 2));
        data.put("flags", extractBytes(buffer, 10, 2));
        data.put("ciaddr", extractBytes(buffer, 12, 4));
        data.put("yiaddr", extractBytes(buffer, 16, 4));
        data.put("siaddr", extractBytes(buffer, 20, 4));
        data.put("giaddr", extractBytes(buffer, 24, 4));
        data.put("chaddr", extractBytes(buffer, 28, 16));
        data.put("sname", extractBytes(buffer, 44, 64));
        data.put("file", extractBytes(buffer, 108, 128));
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
        addBytes(myIP.getAddress(), buffer);

        // Subnet Mask = 255.255.255.0
        addBytes(getByteArray(new int[] {1, 4, 255,255,255,0}), buffer);
        
        // IP Address Lease Time = 1 day
        addBytes(getByteArray(new int[] {51, 4}), buffer);
        addBytes(intToByteArray(24 * 3600), buffer);

        } catch (Exception e) { System.out.println(e.getMessage());}
    }
}
