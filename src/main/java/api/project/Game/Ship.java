package api.project.Game;

public class Ship {
	private int length;
	private Coordinates[] coordinates;
	private int[] hits;
	public enum Orientation {
		VERTICAL, HORIZONTAL;
	}
	
	public Ship(int length, Coordinates start, Orientation orientation) {
		this.length = length;
		hits = new int[length];
		coordinates = new Coordinates[length];
		switch (orientation) {
		case HORIZONTAL:
			for(int i = 0; i < length; i++) {
				coordinates[i] = new Coordinates(start.getX(), start.getY()+i);
			}
			break;
		case VERTICAL:
			for(int i = 0; i < length; i++) {
				coordinates[i] = new Coordinates(start.getX()+i, start.getY());
			}
			break;
		default:
			break;
		}
	}
	public Coordinates[] getCoordinates() {
		return coordinates;
	}
	public boolean isSunk() {
		int i = 0;
		while(i < length) {
			if(hits[i] == 0) return false;
			i++;
		}
		return true;
	}
	public boolean hit(Coordinates hit) {
		for(int i = 0; i < length; i++) {
			if(coordinates[i].equals(hit)) {
				hits[i] = 1;
				return true;
			}
		}
		return false;
	}
}
