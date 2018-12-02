package api.project.ServerClient;

import api.project.Game.Coordinates;

public class GamePacket extends Packet{
	public enum Type {
		MESSAGE, SHOT;
	}
	public Type type;
	public String message;
	public Coordinates coords;
}
