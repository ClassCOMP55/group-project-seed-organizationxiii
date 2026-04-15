package characters;

public class Effervena extends boss {

	public Effervena(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		// TODO Auto-generated constructor stub
	}
	
	private void loadSprites() {
		sprites.put("main", "effervena.png");
	}

	public void fadingTouch(Hueman player) {
		player.lowerSuperMeter(40);
		System.out.println("Hueman's superpower wanes!");
	}
	
	public void stormSurge(Hueman player) {
		System.out.println("Effervena summons a storm...");
		
		if (Math.random() < 0.33) {
			System.out.println("She missed!");
			return;
		}
		
		if (Math.random() < 0.33) {
			System.out.println("The storm didn't pull through!");
			player.takeDamage((int) (attack * 0.6));
			return;
		}
		
		if (Math.random() < 0.33) {
			System.out.println("The storm pulled through!");
			player.takeDamage( attack * 2);
			return;
		}
		
	}
	
	public boolean heartbreakPulse(Hueman player) {
	    System.out.println("Effervena unleashes Heartbreak Pulse!");

	    player.takeDamage((int)(attack * 0.5));

	    System.out.println("Hueman flinched!");
	    return true; // ALWAYS flinches
	}
	
	@Override
	public void takeTurn(Hueman player) {
		// TODO Auto-generated method stub
		
	}

}
