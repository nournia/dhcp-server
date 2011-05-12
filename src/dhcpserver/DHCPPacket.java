
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

    public int readMessage (byte[] buffer, int length)
    {
        data.put("xid", extractBytes(buffer, 4, 4));
        data.put("chaddr", extractBytes(buffer, 28, 16));

        int msgType = 0; // invalid 
        byte[] options = extractBytes(buffer, 240, length - 240);
        for (int i = 0; i < options.length - 1; i++)
        {
            byte[] value = extractBytes(options, i+2, options[i+1]);
            
            switch (options[i])
            {
                case 53:
                    msgType = value[0];
                break;
            }

            i += options[i+1] + 1;
        }

        System.out.println(msgType);
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
        addBytes(getByteArray(new int[] {2}), buffer); // op
        addBytes(getByteArray(new int[] {1}), buffer); // htype
        addBytes(getByteArray(new int[] {6}), buffer); // hlen
        addBytes(getByteArray(new int[] {0}), buffer); // hops
        addBytes(data.get("xid"), buffer);
        addBytes(getByteArray(new int[] {0, 0}), buffer); // secs
        addBytes(getByteArray(new int[] {128, 0}), buffer); // flags
        addBytes(getByteArray(new int[] {0, 0, 0, 0}), buffer); // ciaddr
        addBytes(ip, buffer);
        addBytes(myIP.getAddress(), buffer);
        addBytes(getByteArray(new int[] {0, 0, 0, 0}), buffer); // giaddr
        addBytes(data.get("chaddr"), buffer);
        index += 64; // sname
        index += 128; // file

        // options
        
        // Message type = DHCPOFFER
        addBytes(getByteArray(new int[] {53, 1, 2}), buffer); 
        
        // DHCP Server Identifier
        addBytes(getByteArray(new int[] {54, 4}), buffer); addBytes(myIP.getAddress(), buffer);

        // Subnet Mask = 255.255.255.0
        addBytes(getByteArray(new int[] {1, 4, 255,255,255,0}), buffer);
        
        // IP Address Lease Time = 1 day
        addBytes(getByteArray(new int[] {51, 4}), buffer); addBytes(intToByteArray(24 * 3600), buffer);
        // Rebinding Time = 0.75 day
        addBytes(getByteArray(new int[] {59, 4}), buffer); addBytes(intToByteArray(18 * 3600), buffer);
        // Renewal Time = 0.5 day
        addBytes(getByteArray(new int[] {58, 4}), buffer); addBytes(intToByteArray(12 * 3600), buffer);

        } catch (Exception e) { System.out.println(e.getMessage());}
    }
}
