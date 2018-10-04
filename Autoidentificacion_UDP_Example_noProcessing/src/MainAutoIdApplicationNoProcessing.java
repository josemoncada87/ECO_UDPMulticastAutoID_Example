public class MainAutoIdApplicationNoProcessing{
	
	private static NetworkControl nc;
	
	public static void main(String[] args) {
		nc = new NetworkControl();
		Thread t = new Thread(nc);
		t.start();		
	}		
	
}
