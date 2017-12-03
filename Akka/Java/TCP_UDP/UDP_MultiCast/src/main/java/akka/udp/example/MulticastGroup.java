package akka.udp.example;

import java.net.DatagramSocket;

import akka.io.Inet;
public class MulticastGroup extends Inet.AbstractSocketOptionV2	{

    private String address;
    private String interf;
	public MulticastGroup(String address, String interf) {
		super();
		this.address = address;
		this.interf = interf;
		
	}
    
    @Override
    public void afterBind(DatagramSocket s) {
    	
    }
	
}
