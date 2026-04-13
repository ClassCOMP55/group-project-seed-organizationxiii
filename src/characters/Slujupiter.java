package characters;

public class Slujupiter extends boss{
	
	private short defenseStacks = 0;
	private boolean usedSS = false;
	private boolean enraged = false;

	public Slujupiter(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		
		loadSprites();
	}

	private void loadSprites() {
		sprites.put("main", "slujupiter.png");
	}
	
	public void stickySlam(Hueman other) {
		float tracker = 0.0f;
		
		other.takeDamage((int) (attack * 0.7));
		
		if (!usedSS) {
			other.applySpeedModifier(0.8f);
			usedSS = true;
			tracker += 0.1f;
			System.out.println("Hueman's speed decreased!");
		}
		else {
			tracker += 0.1f;
			other.applySpeedModifier(0.8f + tracker);
			System.out.println("Hueman's speed decreased!");
		}
		
	}
	
	public void resetDefense() {
		defenseStacks = 0;
	}
	
	public void raiseDefense() {
		if (defenseStacks < 3) {
			defenseStacks++;
			System.out.println("Slujupiter's defense rose");
		}
		else {
			System.out.println("Couldn't increase defense further!");
		}
		
		
	}
	
	
	@Override
	public int calculateDamage() {
		if (enraged) {
			return (int) (attack * 1.5);
		}
		else {
			return attack;
		}
	}
	
	@Override
	public void takeDamage(int dmg) {
		switch(defenseStacks) {
		
		case 0:
			currentHP -= dmg;
			break;
		case 1:
			currentHP -= (dmg * 0.8);
			break;
		case 2:
			currentHP -= (dmg * 0.7);
			break;
		case 3:
			currentHP -= (dmg * 0.6);
			break;
		
		}
	}
	
	@Override
	public void takeTurn(Hueman player) {
		
		if (currentHP == halfOfHealth) {
			enraged = true;
			resetDefense();
		}
		
		if (enraged) {
			attackOpponent(player);
		}
		
		else {
			
			if (Math.random() < 0.33) {
				stickySlam(player);
			}
			if (Math.random() < 0.33) {
				raiseDefense();
			}
			attackOpponent(player);
			
		}
		
		
	}

}
