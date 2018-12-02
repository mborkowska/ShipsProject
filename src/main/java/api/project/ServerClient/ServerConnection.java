package api.project.ServerClient;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import api.project.Game.Coordinates;
import api.project.ServerClient.GamePacket.Type;

public class ServerConnection extends Thread {

	private final JTextArea textArea;
	Socket socket;
	Server server;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	boolean shouldRun = true;
	public String username;
	public Coordinates coords;
	int room;

	public ServerConnection(Socket socket, Server server) {
		super("ServerConnectionThred");
		this.socket = socket;
		this.server = server;
		this.textArea = server.textArea;
	}

	public void sendPacketToClient(Packet packet) {
		try {
			oout.writeObject(packet);
			oout.flush();
			System.out.println();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendPacketToOtherClients(Packet packet) {
		for (int index = 0; index < server.lobby.size(); index++) {
			ServerConnection sc = server.lobby.get(index);
			if (sc.shouldRun == true && sc != this) {
				sc.sendPacketToClient(packet);
			}
		}
	}
	
	public void sendPacketToOtherPlayer(Packet packet, ServerConnection sc) {
		sc.sendPacketToClient(packet);
	}

	// opening ObjectInputstram, ObjectOutputStream for clients
	public void run() {
		try {
			oout = new ObjectOutputStream(socket.getOutputStream());
			oin = new ObjectInputStream(socket.getInputStream());
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					InfoPacket returnPacket = new InfoPacket();
					if(p instanceof InfoPacket) {
						InfoPacket ip = (InfoPacket) p;
						if (ip.type == InfoPacket.Type.CONNECT) {
							returnPacket.type = InfoPacket.Type.MESSAGE;
							this.username = ip.username;
							returnPacket.message = this.username + " connected.\n";
							textArea.append(returnPacket.message);
							returnPacket.message += server.roomsToString();
							sendPacketToClient(returnPacket);
							sendPacketToOtherClients(returnPacket);
						}
						if (ip.type == InfoPacket.Type.DISCONNECT) {
							returnPacket.type = InfoPacket.Type.MESSAGE;
							returnPacket.message = this.username + " disconnected.\n";
							textArea.append(returnPacket.message);
							sendPacketToClient(returnPacket);
							sendPacketToOtherClients(returnPacket);
						}
						if (ip.type == InfoPacket.Type.MESSAGE) {
							textArea.append(this.username + ": " + ip.message);
						}
						if (ip.type == InfoPacket.Type.JOIN) {
							returnPacket.type = InfoPacket.Type.JOIN;
							if (!server.rooms.get(ip.roomNumber).isFull()) {
								room = ip.roomNumber;
								server.rooms.get(room).addPlayer(this);
								returnPacket.message = "You were successfully added to room " + room + "\n";
								returnPacket.playersNumber = server.rooms.get(room).playersNumber();
								returnPacket.joinSuccess = true;
								returnPacket.username = this.username;
								returnPacket.roomNumber = room;
								textArea.append(this.username + " joined room " + room + "\n"
										+ "current number of players: " + server.rooms.get(room).playersNumber() + "\n");
								returnPacket.message += server.roomsToString();
								sendPacketToClient(returnPacket);
								returnPacket.message = server.roomsToString();
								sendPacketToOtherClients(returnPacket);
							} else {
								returnPacket.message = "This room is already full\n";
								returnPacket.message += server.roomsToString();
								returnPacket.joinSuccess = false;
								sendPacketToClient(returnPacket);
							}
						}
						if (ip.type == InfoPacket.Type.PLAY) {
							server.rooms.get(room).startGame();
						}
						if (ip.type == InfoPacket.Type.EXIT) {
							returnPacket.type = InfoPacket.Type.MESSAGE;
							if (server.rooms.get(ip.roomNumber).removePlayer(this)) {
								returnPacket.message = "You were successfully removed from room " + ip.roomNumber + "\n";
								textArea.append(
										"user " + username + " successfully removed from room " + ip.roomNumber + "\n");
								returnPacket.message += server.roomsToString();
								sendPacketToClient(returnPacket);
								returnPacket.message = server.roomsToString();
								sendPacketToOtherClients(returnPacket);
							} else {
								returnPacket.message = "Something went wrong\n";
								textArea.append(returnPacket.message);
								sendPacketToClient(returnPacket);
							}
						}
					}
					if(p instanceof GamePacket) {
						GamePacket gp = (GamePacket) p;
						if(gp.type == Type.SHOT) {
							server.rooms.get(room).shot(this, gp.coords);;
						}
						if(gp.type == Type.SET_SHIP) {
							server.rooms.get(room).setShip(this, gp);
						}
					}
					
				} catch (SocketException se) {
					shouldRun = false;
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			oin.close();
			oout.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
