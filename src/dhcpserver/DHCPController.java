
package dhcpserver;

import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class DHCPController {

    HashMap<String, byte[]> data;

    public static byte[] extractBytes(byte[] buffer, int from, int length)
    {
        byte[] result = new byte[length];
        System.arraycopy(buffer, from, result, 0, length);
        return result;
    }

    public int readMessage (byte[] buffer, int length)
    {
        data = new HashMap<String, byte[]>();

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

                case 50:
                    //msgType = 3;
                break;
            }

            i += options[i+1] + 1;
        }

        
        // controll part
        if (msgType == 1)
            writeResponse(2);
        else if (msgType == 3)
            writeResponse(5);
        
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

    public void addResponseBytes(byte[] bytes)
    {
        System.arraycopy(bytes, 0, response, index, bytes.length);
        index += bytes.length;
    }

    public static final byte[] intToByteArray(int value) {
        return new byte[]{ (byte)(value >>> 24), (byte)(value >> 16 & 0xff), (byte)(value >> 8 & 0xff), (byte)(value & 0xff) };
    }

    public int index;
    public byte[] response;
    public void writeResponse (int msgType)
    {
        response = new byte[1000];
        for (int i = 0; i < response.length; i++) response[i] = 0;

        byte[] ip = getByteArray(new int[] {192, 168, 134, 199});

        try {

        InetAddress myIP = InetAddress.getLocalHost();

        index = 0;
        addResponseBytes(new byte[] {2}); // op
        addResponseBytes(new byte[] {1}); // htype
        addResponseBytes(new byte[] {6}); // hlen
        addResponseBytes(new byte[] {0}); // hops
        addResponseBytes(data.get("xid"));
        addResponseBytes(new byte[] {0, 0}); // secs
        addResponseBytes(new byte[] {(byte)128, 0}); // flags
        addResponseBytes(new byte[] {0, 0, 0, 0}); // ciaddr
        addResponseBytes(ip);
        addResponseBytes(myIP.getAddress());
        addResponseBytes(new byte[] {0, 0, 0, 0}); // giaddr
        addResponseBytes(data.get("chaddr"));
        index += 64; // sname
        index += 128; // file

        addResponseBytes(intToByteArray(0x63825363)); // magic cookie

        // options
       
        // Message type
        addResponseBytes(new byte[] {53, 1, (byte)msgType});
        
        // DHCP Server Identifier
        addResponseBytes(new byte[] {54, 4}); addResponseBytes(myIP.getAddress());

        // Subnet Mask = 255.255.255.0
        addResponseBytes(new byte[] {1, 4, (byte)255, (byte)255, (byte)255, (byte)0});

        // Default Gateway
        addResponseBytes(new byte[] {3, 4, (byte)192, (byte)168, (byte)134, (byte)1});

        // DNS Server
        addResponseBytes(new byte[] {6, 4, (byte)4, (byte)2, (byte)2, (byte)4});

        // IP Address Lease Time = 1 day
        addResponseBytes(new byte[] {51, 4}); addResponseBytes(intToByteArray(24 * 3600));
        // Rebinding Time = 0.75 day
        addResponseBytes(new byte[] {59, 4}); addResponseBytes(intToByteArray(18 * 3600));
        // Renewal Time = 0.5 day
        addResponseBytes(new byte[] {58, 4}); addResponseBytes(intToByteArray(12 * 3600));

        } catch (Exception e) { System.out.println(e.getMessage());}
    }
}
