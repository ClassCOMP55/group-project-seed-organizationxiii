package characters;

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
    }
	
	public void thunderSlam(character other) {
		System.out.println("Decima is gathering energy..."); //change for labels
		other.takeDamage((int) (calculateDamage() * 1.5));
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
	public void takeTurn(character other) {
		
		switch(phase) {
		
		case FIRST: 
			
			if (Math.random() < 0.25) {
			thunderSlam(other);
			}
			
			attackOpponent(other);
			
			
		break;
		
		
		case FINAL:
		break;
		}
		
		
	}
}
