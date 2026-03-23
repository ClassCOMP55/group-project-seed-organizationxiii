package characters;

public class Hueman extends player {

    private final int maxSuperPowerMeter = 500;
    private int currentSuperPowerMeter = 0;

    public Hueman(int hp, int atk, int spd, String nm) {
        super(hp, atk, spd, nm);
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
}


//When Hueman gets hit:
//currentSuperPowerMeter increases by the damage taken
//if it goes above 500, it stays at 500
//then normal damage still happens through super.takeDamage(dmg)
//When Hueman uses ability 1:
//it deals damage equal to the stored meter
//then resets the meter back to 0