package api.project.Game;

import java.util.ArrayList;

import api.project.Game.Ship.Orientation;
import api.project.Game.ShipsBoard.ShipState;
import api.project.ServerClient.GamePacket;
import api.project.ServerClient.ServerConnection;
import api.project.ServerClient.GamePacket.Type;

public class Room {
	private ArrayList<ServerConnection> playersConnections = new ArrayList<>();
	private ArrayList<Player> players = new ArrayList<>();
	private GameInstance game;
	private int numberOfRounds = 0;
	private int currentPlayer = -1;
	private int opponent = -1;

	public boolean addPlayer(ServerConnection se) {
		if (!isFull()) {
			playersConnections.add(se);
			return true;
		} else
			return false;
	}

	public boolean removePlayer(ServerConnection se) {
		for (int i = 0; i < playersConnections.size(); i++) {
			if (playersConnections.remove(se))
				return true;
		}
		return false;
	}

	public int playersNumber() {
		return playersConnections.size();
	}

	public boolean isFull() {
		if (playersNumber() < 2)
			return false;
		else
			return true;
	}

	public void startGame() {
		if (isFull()) {
			GamePacket gp = new GamePacket();
			gp.type = Type.MESSAGE;
			gp.message = "The game has started. \n";
			players.add(new Player(playersConnections.get(0), playersConnections.get(1)));
			players.add(new Player(playersConnections.get(1), playersConnections.get(0)));
			players.get(0).connection.sendPacketToClient(gp);
			players.get(1).connection.sendPacketToClient(gp);
		}
	}

	public void setShip(ServerConnection sc, GamePacket gp) {
		Ship.Orientation orient = Orientation.HORIZONTAL;
		if (gp.orientation == 1) {
			orient = Orientation.VERTICAL;
		} else if (gp.orientation == 2) {
			orient = Orientation.HORIZONTAL;
		}
		getCurrentPlayer(sc);
		GamePacket board = new GamePacket();
		board.type = Type.MESSAGE;
		if (players.get(currentPlayer).setShip(gp.length, gp.coords, orient)) {
			board.message = players.get(currentPlayer).shipsBoard.display() + "\n";
		} else {
			board.message = "You cannot place a ship here!\n" + players.get(currentPlayer).shipsBoard.display() + "\n";
		}
		players.get(currentPlayer).connection.sendPacketToClient(board);
	}

	public void shot(ServerConnection sc, Coordinates coordinates) {
		GamePacket gp = new GamePacket();
		gp.type = Type.ANSWER;
		getCurrentPlayer(sc);

		if (players.get(currentPlayer).shoot(coordinates)) {
			ShipsBoard.ShipState result = players.get(opponent).shipsBoard.getAt(coordinates.getX(),
					coordinates.getY());
			if (result == ShipState.SHIP) {
				// hit the ship and change on shipsBoard of player2 and shotsBoard of player1
			}
			gp.answer = result;
			players.get(currentPlayer).connection.sendPacketToClient(gp);
		} else {
			gp.type = Type.MESSAGE;
			gp.message = "You cannot shoot there.\n";
			players.get(currentPlayer).connection.sendPacketToClient(gp);
		}

	}

	private void getCurrentPlayer(ServerConnection sc) {
		if (players.get(0).connection.equals(sc)) {
			currentPlayer = 0;
			opponent = 1;
		} else if (players.get(1).connection.equals(sc)) {
			currentPlayer = 1;
			opponent = 0;
		}
	}

}
