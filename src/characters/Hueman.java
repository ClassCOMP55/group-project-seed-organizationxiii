package characters;

import acm.graphics.GImage;

public class Hueman extends player {
	
	private ColorType color;

    private final int maxSuperPowerMeter = 500;
    private int currentSuperPowerMeter = 0;
    
    public int getSuperMeter() {
        return currentSuperPowerMeter;
    }

    public int getMaxSuperMeter() {
        return maxSuperPowerMeter;
    }

    public ColorType getColor() {
        return color;
    }

    public Hueman(int hp, int atk, int spd, String nm, ColorType color) {
    	super(hp, atk, spd, nm);
    	
    	this.color = color;

        loadSprites(); // important
        //test
    }

    private void loadSprites() {
        switch(color) {
            case RED:
                sprites.put("back", "redback.png");
                sprites.put("overworld", "redow.png");
                break;

            case BLUE:
            	sprites.put("back", "blueback.png");
                sprites.put("overworld", "blueow.png");
                break;

            case GREEN:
            	sprites.put("back", "greenback.png");
                sprites.put("overworld", "greenow.png");
                break;
        }
    }
    
    public GImage getSprite(String key) {
        return new GImage(sprites.get(key));
    }
    
    @Override
    public void takeDamage(int dmg) {
        currentSuperPowerMeter += dmg;

        if (currentSuperPowerMeter > maxSuperPowerMeter) {
            currentSuperPowerMeter = maxSuperPowerMeter;
        }

        super.takeDamage(dmg);
    }

    @Override
    public boolean useAbility(int abilityID, character target) {
        if (abilityID == 1) {
            int damage = currentSuperPowerMeter;

            target.takeDamage(damage);
            currentSuperPowerMeter = 0;

            return true;
        }

        return false;
    }
    
    public void printSummary() {
        System.out.println("=== Hueman Summary ===");
        System.out.println("Name: " + getName());
        System.out.println("Color: " + color);

        System.out.println("\n--- Stats ---");
        System.out.println("HP: " + getHP());
        System.out.println("ATK: " + getBaseAttack());
        System.out.println("SPD: " + calculateSpeed());

        System.out.println("\n--- Super Power ---");
        System.out.println("Meter: " + currentSuperPowerMeter + " / " + maxSuperPowerMeter);

        System.out.println("\n--- Ability ---");
        System.out.println("Ability 1: Super Power");
        System.out.println("Deals damage equal to stored Super Power Meter");
        System.out.println("Resets meter to 0 after use.");

        System.out.println("======================\n");
    }
    
}

//When Hueman gets hit:
//currentSuperPowerMeter increases by the damage taken
//if it goes above 500, it stays at 500
//then normal damage still happens through super.takeDamage(dmg)
//When Hueman uses ability 1:
//it deals damage equal to the stored meter
//then resets the meter back to 0