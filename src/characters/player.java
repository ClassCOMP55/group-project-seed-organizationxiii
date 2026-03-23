package characters;

public abstract class player extends character {

    public player(int hp, int atk, int spd, String nm) {
        super(hp, atk, spd, nm);
    }

    @Override
    public void revive() {
        super.revive();
        // player-specific resets later
    }

    public abstract boolean useAbility(int abilityID, character target);

}
