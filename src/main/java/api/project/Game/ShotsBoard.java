package api.project.Game;

import api.project.Game.ShipsBoard.ShipState;

public class ShotsBoard {
	enum ShotState {
		BLANK, HIT, MISS
	}
	private ShotState[][] fields;
	
	public ShotsBoard() {
		fields = new ShotState[10][10];
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				fields[i][j] = ShotState.BLANK;
			}
		}
	}
	public ShotState getAt(int x, int y) {
		return fields[x][y];
	}
	public void setAt(int x, int y, ShotState state) {
		fields[x][y] = state;
	}
	public void display() {
		String row = "";
		for(int i = 0; i < 10; i++) {
			for(int j = 0; j < 10; j++) {
				if(fields[i][j] == ShotState.HIT) {
					row += " | X";
				}
				if(fields[i][j] == ShotState.MISS) {
					row += " | -";
				}
				if(fields[i][j] == ShotState.BLANK) {
					row += " |  ";
				}
				
			}
			row += " |";
			System.out.println(row);
			row = "";
		}
	}
}
