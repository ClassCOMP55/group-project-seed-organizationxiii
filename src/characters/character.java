package characters;

import java.util.HashMap;

import acm.graphics.GImage;

public abstract class character {

	 protected int maxHP;
	 protected int currentHP;
	 protected int attack;
	 protected int speed;
	 protected String name;

	 protected float dmgMult = 1.0f;
	 protected float healMult = 1.0f;
	 protected float speedMult = 1.0f;
	 
	 protected HashMap<String, String> sprites; //new change
	 
	 public GImage getSprite(String key) {
	        return new GImage(sprites.get(key));
	    }

	 public character(int hp, int atk, int spd, String nm) {
	 maxHP = hp;
	 currentHP = hp;
	 attack = atk;
	 speed = spd;
	 name = nm;
	 
	 sprites = new HashMap<>();
	 }

	 public void takeDamage(int dmg) {
	 currentHP -= dmg;

	 if (currentHP < 0) {
	 currentHP = 0;
	 }

	 System.out.println(name + " took " + dmg + " damage!\n");
	 }

	 public int calculateDamage() {
		 return attack;
	 }

	 public void revive() {
	 currentHP = maxHP;
	 }

	 public void applyDamageModifier(float mult) {
	 dmgMult = mult;
	 }

	 public void clearDamageModifier() {
	 dmgMult = 1.0f;
	 }

	 public void applyHealModifier(float mult) {
	 healMult = mult;
	 }

	 public void clearHealModifier() {
	 healMult = 1.0f;
	 }
	 
	 public void applySpeedModifier(float mult) {
	    	speedMult = mult;
	    }

	 public void takeFixedDamage(int dmg) {
	 currentHP -= dmg;

	 if (currentHP < 0) {
	 currentHP = 0;
	 }

	 System.out.println(name + " took " + dmg + " damage!\n");
	 }

	 public String getName() {
	 return name;
	 }

	 public int getBaseAttack() {
	 return attack;
	 }

	 public void attackOpponent(character other) {
	 other.takeDamage(calculateDamage());
	 }

	 public boolean isAlive() {
	 return currentHP > 0;
	 }

	 public int getHP() {
	 return currentHP;
	 }

	 public int calculateSpeed() {
	 return speed;
	 }

	 public boolean isFaster(character other) {
	 return calculateSpeed() > other.calculateSpeed();
	 }
	}
