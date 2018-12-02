package api.project.ServerClient;

public class InfoPacket extends Packet{
	public enum Type {
		CONNECT, DISCONNECT, MESSAGE, JOIN, PLAY, EXIT;
	}
	public Type type;
	public String username;
	public String message;
	public int roomNumber;
	public boolean joinSuccess;
	public int playersNumber;
}
