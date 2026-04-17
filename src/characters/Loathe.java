package characters;

public class Loathe extends boss {

	public Loathe(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		// TODO Auto-generated constructor stub
	}

	public void ramp() {
		attack += 15;
		System.out.println("Loathe gathers power!");
	}
	
	public void negativeBlast(Hueman player) {
		player.takeDamage(attack * 2);
		System.out.println("Loathe unleashed a negative blast");
	}
	
	public void fadingTouch(Hueman player) {
		player.lowerSuperMeter(30);
		System.out.println("Hueman's superpower wanes!");
	}
	
	@Override
	public void takeTurn(Hueman player) {
		
		if (currentHP <= halfOfHealth) { //behavior when half health
			
			if (Math.random() < 0.5) { //ramp branch
				ramp();
				
				if (Math.random() < 0.4) { //40% chance to take another turn
					System.out.println("Loathe takes another turn!");
					attackOpponent(player);
				}
				
				return;
			}
			
			if (Math.random() < 0.3) { //negblast branch
				negativeBlast(player);
				
				if (Math.random() < 0.4) {
					System.out.println("Loathe takes another turn!");
					ramp();
				}
				return;
			}
			
			attackOpponent(player);
			return;
			
		}
		
		//usual behavior
		if (Math.random() < 0.33) {
			ramp();
			return;
		}
		
		if (Math.random() < 0.33) {
			fadingTouch(player);
			return;
		}
		
		attackOpponent(player);
		
	}

}
