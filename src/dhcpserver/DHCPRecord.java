package dhcpserver;

import java.util.Date;

public class DHCPRecord
{
    byte[] chaddr = new byte[16];
    byte[] ip = new byte[4];
    Date ackTime;
    String hostName;
}
