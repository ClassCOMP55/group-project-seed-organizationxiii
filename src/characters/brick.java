package characters;

public class brick extends boss {

	
	private int powerStacks = 3;
	
	public brick(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		// TODO Auto-generated constructor stub
	}
	
	private void loadSprites() {
		sprites.put("main", "brick.png");
	}
	
	
	
	public void heal() {
		currentHP += (int) (maxHP * 0.15);
		System.out.println("Brick healed some health!");
	}
	
	@Override
	public void takeDamage(int dmg) {
		currentHP -= dmg;
		
		if (currentHP < halfOfHealth * 1.5) {
			System.out.println(name + " is losing power."); //change for labels
			powerStacks--;
		}
		
		if (currentHP < halfOfHealth) {
			System.out.println(name + " is losing power.");
			powerStacks--;
		}
		
		if (currentHP < halfOfHealth / 2) {
			System.out.println(name + " lost all power!");
			powerStacks--;
		}

		 if (currentHP < 0) {
		 currentHP = 0;
		 }

		 System.out.println(name + " took " + dmg + " damage!\n");
		
	} //test?

	@Override
	public int calculateDamage() {
		
		switch(powerStacks) {
		
		case 1:
			return (int) (attack * 1.25);
			
		case 2:
			return (int) (attack * 1.5);
			
		case 3:
			return (int) (attack * 2);
		
		default:
			return attack;
		}
		
	}

	@Override
	public void takeTurn(Hueman player) {
		
		if (Math.random() < 0.25) {
			heal();
			return;
		}
		
		attackOpponent(player);
		
	}

}
