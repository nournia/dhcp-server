package dhcpserver;

import java.io.Serializable;
import java.util.Date;

public class DHCPRecord implements Serializable
{
    byte[] chaddr = new byte[16];
    byte[] ip = new byte[4];
    Date ackTime;
    Date reserveTime;
    String hostName;
}
