package api.project;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.net.Socket;

import org.junit.Test;
import api.project.Game.*;
import api.project.Game.Ship.Orientation;
import api.project.Game.ShipsBoard.ShipState;
import api.project.ServerClient.Server;
import api.project.ServerClient.ServerConnection;

public class GameLogicTests {
	//@Test
	public void ShipConstructorTest() {
		Ship ship1 = new Ship(4, new Coordinates(0, 0), Orientation.HORIZONTAL);
		Coordinates[] actual = ship1.getCoordinates();
		Coordinates[] expected = new Coordinates[4];
		expected[0] = new Coordinates(0, 0);
		expected[1] = new Coordinates(0, 1);
		expected[2] = new Coordinates(0, 2);
		expected[3] = new Coordinates(0, 3);
		for (int i = 0; i < 4; i++) {
			assertTrue(expected[i].equals(actual[i]));
		}
		Ship ship2 = new Ship(4, new Coordinates(0, 0), Orientation.VERTICAL);
		actual = ship2.getCoordinates();
		expected[0] = new Coordinates(0, 0);
		expected[1] = new Coordinates(1, 0);
		expected[2] = new Coordinates(2, 0);
		expected[3] = new Coordinates(3, 0);
		for (int i = 0; i < 4; i++) {
			assertTrue(expected[i].equals(actual[i]));
		}
	}

	//@Test
	public void ShipHitTest() {
		Ship ship = new Ship(2, new Coordinates(0, 0), Orientation.HORIZONTAL);
		assertFalse(ship.hit(new Coordinates(1, 1)));
		assertTrue(ship.hit(new Coordinates(0, 0)));
		assertFalse(ship.isSunk());
		assertTrue(ship.hit(new Coordinates(0, 1)));
		assertTrue(ship.isSunk());
	}
	
	//@Test
	public void CoordinatesConstructorTest() {
		Coordinates coords = new Coordinates(1, 2);
	}
	
	/*@Test
	public void setShipTest() {
		Player player = new Player();
		assertFalse(player.setShip(30, new Coordinates(1, 2), Orientation.HORIZONTAL));
		
		player.shipsBoard.setAt(0, 0, ShipState.SHIP);
		assertFalse(player.setShip(5, new Coordinates(0, 0), Orientation.HORIZONTAL));
		player.setShip(5, new Coordinates(2, 2), Orientation.VERTICAL);
		player.shipsBoard.display();
	}*/
}
