package api.project.Game;

public class Coordinates {
	private int x;
	private int y;

	public Coordinates(int x, int y) {

		if (x >= 0 && x <= 9 && y >= 0 && y <= 9) {
			this.x = x;
			this.y = y;
		} else {
			throw new ShipsException("Coordinate out of bounds");
		}
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		Coordinates c = (Coordinates) obj;
		return (c.x == this.x && c.y == this.y);
	}
}
