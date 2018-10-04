import processing.core.PApplet;

public class MainAutoIdApplication extends PApplet{
	
	private NetworkControl nc;
	private int globalID;
	
	public static void main(String[] args) {
		PApplet.main("MainAutoIdApplication");
	}
	
	@Override
	public void settings() {
		size(300,300);
		globalID = -1;
		
	}
	
	@Override
	public void setup() {
		textSize(80);
		textAlign(CENTER, CENTER);
		nc = new NetworkControl();
		Thread t = new Thread(nc);
		t.start();		
	}
	
	@Override
	public void draw() {
		background(0);
		fill(255);
		text(globalID, width/2, height/2);
		
		globalID = nc.getId();
	}
	
}
