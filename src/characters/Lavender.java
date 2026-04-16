package characters;

public class Lavender extends boss {
	
	private boolean usedFakeOut = false;
	private boolean enraged = false;
	

	public Lavender(int hp, int atk, int spd, String nm) {
		super(hp, atk, spd, nm);
		usedFakeOut = false;
		loadSprites();
	}
	
	public boolean fakeOut(Hueman player) {
	    System.out.println("Lavender used Fake Out!");
	    player.takeDamage((int)(attack * 0.5));
	    System.out.println("Hueman flinched!");
	    return true;
	}
	
	
	private void loadSprites() {
		sprites.put("main", "lavender.png");
	}
	
	public void pollenBurst(Hueman player) {
		System.out.print("Lavender used Pollen Burst!");
		player.takeDamage((int)(calculateDamage() * 0.8));
	}
	
	public void lavenderStorm(Hueman player) {
		System.out.println("Lavender summoned a storm!");
		player.takeDamage(calculateDamage());
	}
	
	@Override
	public int calculateDamage() {
		if (enraged) {
			return (int) (attack * 1.4);
		} else {
			return attack;
		}
	}
	
	@Override
	public void takeDamage(int dmg) {
		currentHP -= dmg;
		
		if (currentHP < halfOfHealth && !enraged) {
			enraged = true;
			System.out.println(name + " became enraged!");
		}
		
		if (currentHP < 0) {
			currentHP = 0;
		}
		
		System.out.println(name + " took " + dmg + " damage!");
	}
	
	@Override
	public void takeTurn(Hueman player) {
		if (!usedFakeOut) {
			usedFakeOut = true;
			fakeOut(player);
			return;
		}
		
		if (enraged) {
			if (Math.random() < 0.4) {
				pollenBurst(player);
			} else {
				lavenderStorm(player);
			}
		} else {
			if (Math.random() < 0.33) {
				pollenBurst(player);
			} else {
				lavenderStorm(player);
			}
		}
	}
}