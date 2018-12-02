package api.project.ServerClient;

import api.project.Game.Coordinates;
import api.project.Game.Ship;
import api.project.Game.ShipsBoard;

public class GamePacket extends Packet{
	public enum Type {
		MESSAGE, SHOT, ANSWER, SET_SHIP;
	}
	public Type type;
	public String message;
	public Coordinates coords;
	public int length;
	public int orientation;
	public ShipsBoard.ShipState answer;
}
