package characters;

public class lavender extends boss {

	public lavender(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		// TODO Auto-generated constructor stub
	}
	
	public boolean fakeOut(Hueman player) {
	    System.out.println("Lavender used Fake Out!");

	    player.takeDamage((int)(attack * 0.5));

	    System.out.println("Hueman flinched!");
	    return true; // ALWAYS flinches
	}
	
	
	private void loadSprites() {
		sprites.put("main", "lavender.png");
	}
	
	
	@Override
	public void takeTurn(Hueman player) {
		
		if (Math.random() < 0.33) {
			fakeOut(player);
			return;
		}
		
		System.out.println("Lavender summoned a storm!");
		attackOpponent(player);
		
	}

}
