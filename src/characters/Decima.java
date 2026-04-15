package characters;

import acm.graphics.GImage;

public class Decima extends boss {

	public enum Phase {
        FIRST,
        FINAL
    }
	
	private int powerStacks = 3;
	

    private Phase phase;

    public Decima(int hp, int atk, int spd, String nm, Phase ph) {
        super(hp, atk, spd, nm);
        phase = ph;
        
        loadSprites();
    }
	
	public void thunderSlam(character other) {
		System.out.println("Decima is gathering energy..."); //change for labels
		other.takeDamage((int) (calculateDamage() * 1.5));
	}
	
	
	public void heal() {
		currentHP += (int) (maxHP * 0.25);
		System.out.println("Decima healed some health!");
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
	
	public GImage getSprite(String key) {
        return new GImage(sprites.get(key));
    }
	
	
	private void loadSprites() {
	   sprites.put("main", "Decima.png");
	}
	
	@Override
	public void takeTurn(Hueman other) {
		
		switch(phase) {
		
		case FIRST: 
			
			if (Math.random() < 0.25) {
			thunderSlam(other);
			}
			
			attackOpponent(other);
			
			
		break;
		
		
		case FINAL:
			if (Math.random() < 0.30) {
				thunderSlam(other);
			}
			
			if (Math.random() < 0.20) {
				heal();
			}
				
				attackOpponent(other);
		break;
		}
		
		
	}
}
