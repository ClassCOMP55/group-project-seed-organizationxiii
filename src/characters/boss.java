package characters;

public abstract class boss extends character {
	protected int thirdOfHealth;
	 protected int halfOfHealth;

	 public boss(int hp, int atk, int spd, String nm) {
	 super(hp, atk, spd, nm);

	 thirdOfHealth = maxHP / 3;
	 halfOfHealth = maxHP / 2;
	 }

	 @Override
	 public void revive() {
	 super.revive();
	 // boss-specific resets later
	 }

	 public abstract void takeTurn(character player); // AI behavior

	 public void startTurn() {
	 // optional reset mechanic
	 }

}
