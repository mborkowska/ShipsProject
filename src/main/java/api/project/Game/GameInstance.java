package api.project.Game;

import java.util.ArrayList;
import api.project.ServerClient.GamePacket;
import api.project.ServerClient.ServerConnection;

public class GameInstance extends Thread {
	private Player player1;
	private Player player2;
	public GameInstance(ArrayList<ServerConnection> players) {
		player1 = new Player(players.get(0));
		player2 = new Player(players.get(1));
	}
	
	@Override
	public void run() {
		GamePacket p = new GamePacket();
		p.type = GamePacket.Type.MESSAGE;
		p.message = "Game has started\n";
		player1.connection.sendPacketToClient(p);
		player2.connection.sendPacketToClient(p);
	}
}
