package api.project.Game;

import java.util.ArrayList;

import api.project.Game.ShipsBoard.ShipState;
import api.project.Game.ShotsBoard.ShotState;
import api.project.ServerClient.GamePacket;
import api.project.ServerClient.GamePacket.Type;
import api.project.ServerClient.ServerConnection;

public class Player {
	public ServerConnection connection;
	private ServerConnection otherPlayer;
	private String username;
	public ShipsBoard shipsBoard; 
	public ShotsBoard shotsBoard;
	private ArrayList<Ship> ships = new ArrayList<>();
	private int credits = 30;
	
	public Player(ServerConnection sc) {
		connection = sc;
		username = sc.username;
		shipsBoard = new ShipsBoard();
		shotsBoard = new ShotsBoard();
	}
	
	public boolean setShip(int length, Coordinates start, Ship.Orientation orientation) {
		Ship ship;
		Coordinates[] coords;
		try {
			if(credits-length <= 0) throw new ShipsException("Not enough credits");
			ship = new Ship(length, start, orientation);
			coords = ship.getCoordinates();
			for(int i = 0; i < length; i++) {
				if(shipsBoard.getAt(coords[i].getX(), coords[i].getY()) != ShipState.BLANK) throw new ShipsException("This field is already taken");
			}
		} catch (ShipsException oobe) {
			oobe.printStackTrace();
			return false;
		}
		for(int i = 0; i < length; i++) {
			shipsBoard.setAt(coords[i].getX(), coords[i].getY(), ShipState.SHIP);
		}
		ships.add(ship);
		credits -= length;
		return true;
	}
	
	public void shoot(Coordinates coordinates) {
		if(shotsBoard.getAt(coordinates.getX(), coordinates.getY()) == ShotState.BLANK) {
			GamePacket gp = new GamePacket();
			gp.type = Type.SHOT;
			gp.coords = coordinates;
			connection.sendPacketToOtherPlayer(gp, otherPlayer);
		}
		else System.out.println("You have already tried that");
	}
}
