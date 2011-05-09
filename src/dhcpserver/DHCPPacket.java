
package dhcpserver;

import java.util.HashMap;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class DHCPPacket {

    HashMap<String, byte[]> data = new HashMap<String, byte[]>();

    private byte[] extractBytes(byte[] buffer, int from, int to)
    {
        byte[] result = new byte[to - from + 1];
        System.arraycopy(buffer, from, result, 0, from - to + 1);
        return result;
    }

    public void setData (byte[] buffer)
    {
        data.put("op", extractBytes(buffer, 0, 7));
        data.put("htype", extractBytes(buffer, 8, 15));
        

    }

}
