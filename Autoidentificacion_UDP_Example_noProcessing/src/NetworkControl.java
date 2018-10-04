import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class NetworkControl implements Runnable {
	
	private MulticastSocket ms;
	private InetAddress groupIp;
	private int groupPort;
	private int id;
	private boolean identified;
	
	public NetworkControl() {		
		try {
			id = -1;
			identified = false;
			groupPort = 5000;
			ms = new MulticastSocket(groupPort);
			groupIp = InetAddress.getByName("228.5.6.8"); // 224.0.0.0 to 239.255.255.255 CLASS D IP
			ms.joinGroup(groupIp);				
			toIdentificate();				
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void toIdentificate() throws IOException {
		ms.setSoTimeout(600);
		byte[] buf = "id-new".getBytes();
		DatagramPacket hiPacket = new DatagramPacket(buf, buf.length, groupIp, groupPort);
		ms.send(hiPacket);
		System.out.println("hi! im new");
		while(!identified) {
			try{
			byte[] tempBuf = new byte[32];
			DatagramPacket externalResponsePacket = new DatagramPacket(tempBuf, tempBuf.length);			
			ms.receive(externalResponsePacket);
			System.out.print("receive new message");
			String message = new String(externalResponsePacket.getData(), 0, externalResponsePacket.getLength()).trim();
			System.out.println("---->" + message);
			if(message.contains("id-im:")) {
				int incomingID = Integer.parseInt((message.split(":")[1]));
				if(incomingID >= id) {
					id = incomingID+1;
				}
			}
			}catch (SocketTimeoutException e) {
				System.out.println("no more messages");
				if(id == -1) {
					id = 0;
				}
				ms.setSoTimeout(0);
				identified = true;
				System.out.println("I am : " + id);				
			}
		}		
	}

	@Override
	public void run() {
		while (true) {					
			try {
				if(identified == true){
					receivePacket();
				}
				Thread.sleep(8);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
		}		
	}

	private void receivePacket() throws IOException {
		byte[] buf = new byte[128];
		DatagramPacket p = new DatagramPacket(buf, buf.length);			
		ms.receive(p);		
		String message = new String(p.getData(), 0, p.getLength()).trim();
		MessageControl(message);
	}

	private void MessageControl(String message) {
		if(message.contains("id-new")) {
			// new user
			sendPacket("id-im:"+id);
		}
		// ... new types
	}

	private void sendPacket(String outgoingMessage) {		
		try {
			byte[] buf = outgoingMessage.getBytes();
			DatagramPacket outgoingPacket = new DatagramPacket(buf, buf.length, groupIp, groupPort);
			ms.send(outgoingPacket);
		} catch (IOException e) {			
			e.printStackTrace();
		}		
	}
	
	public int getId() {
		return id;
	}

}
