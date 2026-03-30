package characters;

public class Decima extends boss {

	public enum Phase {
        FIRST,
        FINAL
    }

    private Phase phase;

    public Decima(int hp, int atk, int spd, String nm, Phase ph) {
        super(hp, atk, spd, nm);
        phase = ph;
    }
	
	

	@Override
	public void takeTurn(character other) {
		
		switch(phase) {
		
		case FIRST: 
			
			
			
			attackOpponent(other);
			
			
			
		break;
		
		
		case FINAL:
		break;
		}
		
		
	}
}
