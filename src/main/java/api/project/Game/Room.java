package api.project.Game;

import java.util.ArrayList;
import api.project.ServerClient.ServerConnection;

public class Room {
	private ArrayList<ServerConnection> players = new ArrayList<>();
	private GameInstance game;
	public boolean addPlayer(ServerConnection se) {
		if(!isFull()) {
			players.add(se);
			return true;
		}
		else return false;
	}
	public boolean removePlayer(ServerConnection se) {
		for(int i = 0; i < players.size(); i++) {
			if(players.remove(se)) return true;
		}
		return false;
	}
	public int playersNumber() {
		return players.size();
	}
	public boolean isFull() {
		if(playersNumber() < 2) return false;
		else return true;
	}
	public void startGame() {
		if(isFull()) {
			game = new GameInstance(players);
			game.start();
		}
	}
}
