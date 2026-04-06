package characters;

import acm.graphics.GImage;
;

public class MintSentinel extends boss {
	
	private short defenseStacks = 0;
	
	public MintSentinel(int hp, int atk, int spd, String nm) {
        super(hp, atk, spd, nm);
        
        loadSprites();
    }
	
	private void loadSprites() {
		sprites.put("main", "Mint.png");
	}
	
	private void raiseDefense() {
		if (defenseStacks < 3) {
			defenseStacks++;
			System.out.println("Mint sentinel's defense rose");
		}
		else {
			System.out.println("Couldn't increase defense further!");
		}
		
		
	}

	@Override
	public void takeTurn(character player) {
		if (Math.random() < 0.33) {
			raiseDefense();
		}
		System.out.println("Mint Sentinel used Ice Beam!");
		attackOpponent(player);
		
	}
}
