package api.project.Game;

public class ShipsBoard {
	public enum ShipState {
		SHIP, HIT, BLANK
	}

	private ShipState[][] fields;

	public ShipsBoard() {
		fields = new ShipState[10][10];
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				fields[i][j] = ShipState.BLANK;
			}
		}
	}
	public ShipState getAt(int x, int y) {
		return fields[x][y];
	}
	public void setAt(int x, int y, ShipState state) {
		fields[x][y] = state;
	}
	public String display() {
		String result = "";
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(fields[i][j] == ShipState.SHIP) {
					result += " | S";
				}
				if(fields[i][j] == ShipState.HIT) {
					result += " | X";
				}
				if(fields[i][j] == ShipState.BLANK) {
					result += " |  ";
				}
				
			}
			result += " |\n";
		}
		return result;
	}
}
