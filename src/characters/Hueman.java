package characters;

import acm.graphics.GImage;

public class Hueman extends player {
	
	private ColorType color;

    private final int maxSuperPowerMeter = 200;
    private int currentSuperPowerMeter = 0;
    
    private short debilitateStacks = 0;
    private short attackStacks = 0;
    
    private float speedMult = 1.0f;
    
    
    
    @Override
    public int calculateSpeed() {
    	return (int) (speed * speedMult);
    }
    
    @Override
    public void attackOpponent(character other) {
    	switch(attackStacks) {
    	
    	case 0:
    		other.takeDamage(attack);
    		break;
    	
    	case 1:
    		other.takeDamage((int) (attack * 1.5));
    		break;
    	
    	case 2:
    		other.takeDamage((int) (attack * 1.75));
    		break;
    		
    	case 3:
    		other.takeDamage(attack * 2);
    		break;
    	
    	}
    }
    
    public void debilitate(boss other) { //after slujupiter battle
    	
    	System.out.println("Hueman used debilitate!");
    	
    	switch(debilitateStacks) {
    	
    	case 0:
    		other.applyDamageModifier(0.8f);
    		other.applySpeedModifier(0.8f);
    		attackStacks++;
    		debilitateStacks++;
    		System.out.println("The boss is crippled!");
    		break;
    	
    	case 1:
    		other.applyDamageModifier(0.7f);
    		other.applySpeedModifier(0.7f);
    		attackStacks++;
    		debilitateStacks++;
    		System.out.println("The boss is crippled further!");
    		break;
    		
    	case 2:
    		other.applyDamageModifier(0.6f);
    		other.applySpeedModifier(0.6f);
    		attackStacks++;
    		debilitateStacks++;
    		System.out.println("The boss is crippled further!");
    		break;
    		
    	case 3:
    		other.applyDamageModifier(0.5f);
    		other.applySpeedModifier(0.5f);
    		attackStacks++;
    		debilitateStacks++;
    		System.out.println("The boss is crippled!");
    		break;
    	
    	default:
    		System.out.println("The boss can't be crippled further!");
    		break;
    		
    	}
    }
    
    
    
    public void clearSpeedModifier() {
    	speedMult = 1.0f;
    }
    
    public int getSuperMeter() {
        return currentSuperPowerMeter;
    }
    
    public void setSuperMeter(int value) {
        currentSuperPowerMeter = value;
    }
    
    public void lowerSuperMeter(int n) {
    	currentSuperPowerMeter -= n;
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
            int damage = (int) (currentSuperPowerMeter * 2.5);

            target.takeDamage(damage);
            currentSuperPowerMeter = 0;

            return true;
        }

        return false;
    }
    
    public int getStatScore() {
    	return getHP() + getBaseAttack() + calculateSpeed() + getSuperMeter();
    }
    
    public String getStatLabel() {
    	int score = getStatScore();
    	
    	if (score < 150) {
    		return "Starter";
    	} else if (score < 250) {
    		return "Advanced";
    	} else if (score < 400) {
    		return "Elite";
    	} else {
    		return "Mythic";
    	}
    }
    
    public void printSummary() {
        System.out.println("=== Hueman Summary ===");
        System.out.println("Name: " + getName());
        System.out.println("Color: " + color);

        System.out.println("\n--- Stats ---");
        System.out.println("HP: " + getHP());
        System.out.println("ATK: " + getBaseAttack());
        System.out.println("SPD: " + calculateSpeed());
        System.out.println("Stat Score: " + getStatScore());
        System.out.println("Label: " + getStatLabel());

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