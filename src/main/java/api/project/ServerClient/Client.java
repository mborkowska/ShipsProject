package api.project.ServerClient;

import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import api.project.Game.Coordinates;
import api.project.Game.Ship;
import api.project.ServerClient.GamePacket.Type;

public class Client implements ActionListener {
	JFrame frame;
	JTextArea textArea = new JTextArea();
	JTextField textField = new JTextField(25);
	JButton joinButton = new JButton("Join");
	JButton playButton = new JButton("Play");
	JButton setShipButton = new JButton("Set Ship");
	JButton shotButton = new JButton("Shoot");
	JFrame gameFrame;
	JTextArea gameTextArea = new JTextArea();
	JTextField gameTextField = new JTextField(25);
	int room = 0;

	Socket s;
	ObjectInputStream oin;
	ObjectOutputStream oout;
	public String username;
	Boolean shouldRun = true;

	public static void main(String[] args) {
		new Client();
	}

	public Client() {
		String username = JOptionPane.showInputDialog("Input the username:");
		this.username = username;
		setUpWindow();
		connect();
	}

	private void setUpWindow() {
		// main window
		frame = new JFrame(username);
		frame.setSize(500, 450);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				System.out.println("window closing.");
			}
		});
		Panel p = new Panel();
		joinButton.addActionListener(this);
		textArea.setEditable(false);
		JScrollPane areaScrollPane = new JScrollPane(textArea);
		areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		areaScrollPane.setPreferredSize(new Dimension(430, 275));
		p.add(areaScrollPane);
		p.add(textField);
		p.add(joinButton);
		frame.add(p);
		frame.setVisible(true);
		// game window
		gameFrame = new JFrame(username + "'s game");
		gameFrame.setSize(500, 450);
		gameFrame.setLocationRelativeTo(null);
		gameFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				InfoPacket p = new InfoPacket();
				p.type = InfoPacket.Type.EXIT;
				p.roomNumber = room;
				try {
					oout.writeObject(p);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		Panel gameP = new Panel();
		playButton.addActionListener(this);
		shotButton.addActionListener(this);
		setShipButton.addActionListener(this);
		gameTextArea.setEditable(false);
		JScrollPane gameAreaScrollPane = new JScrollPane(gameTextArea);
		gameAreaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		gameAreaScrollPane.setPreferredSize(new Dimension(430, 275));
		gameP.add(gameAreaScrollPane);
		gameP.add(gameTextField);
		gameP.add(playButton);
		gameP.add(setShipButton);
		gameP.add(shotButton);
		gameFrame.add(gameP);

	}

	private void connect() {
		try {
			s = new Socket("localhost", 3333);
			oout = new ObjectOutputStream(s.getOutputStream());
			oin = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Cannot connect to server");
			return;
		}
		InfoPacket p = new InfoPacket();
		p.type = InfoPacket.Type.CONNECT;
		p.username = username;
		try {
			oout.writeObject(p);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new listenForInput().start();
	}

	public void actionPerformed(ActionEvent e) {
		InfoPacket p = new InfoPacket();
		if (e.getSource() == joinButton) {
			p.type = InfoPacket.Type.JOIN;
			p.roomNumber = Integer.parseInt(textField.getText()); // here should be validation - user inputs a valid
																	// room number(but server checks whether this room
																	// is full
			try {
				oout.writeObject(p);
				oout.flush();
				System.out.println();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			textField.setText("");
		}
		if (e.getSource() == playButton) {
			p.type = InfoPacket.Type.PLAY;
			try {
				oout.writeObject(p);
				oout.flush();
				System.out.println();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		if (e.getSource() == shotButton) {
			String input = gameTextField.getText();
			String[] subString = input.split(",");
			gameTextArea.append("You shoots at: " + input + "\n");
			GamePacket gp = new GamePacket();
			gp.type = GamePacket.Type.SHOT;
			gp.coords = new Coordinates(Integer.parseInt(subString[0]), (Integer.parseInt(subString[1])));
			try {
				oout.writeObject(gp);
				oout.flush();
				System.out.println();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			gameTextField.setText(null);
		}
		if(e.getSource() == setShipButton) {
			String input = gameTextField.getText();
			String[] subString = input.split(",");
			GamePacket gp = new GamePacket();
			gp.type = Type.SET_SHIP;
			gp.length = Integer.parseInt(subString[0]);
			gp.coords = new Coordinates(Integer.parseInt(subString[1]), (Integer.parseInt(subString[2])));
			gp.orientation = Integer.parseInt(subString[3]);
			try {
				oout.writeObject(gp);
				oout.flush();
				System.out.println();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			gameTextField.setText(null);
		}
	}

	public class listenForInput extends Thread {
		public void run() {
			while (shouldRun) {
				try {
					Packet p = (Packet) oin.readObject();
					if (p instanceof InfoPacket) {
						InfoPacket ip = (InfoPacket) p;
						if (ip.type == InfoPacket.Type.MESSAGE) {
							textArea.setText(null);
							textArea.append(ip.message + "\n");
						}
						if (ip.type == InfoPacket.Type.JOIN) {
							textArea.setText(null);
							if (ip.joinSuccess) {
								if (ip.username.equals(username)) {
									gameTextArea.setText(null);
									room = ip.roomNumber;
									gameFrame.setVisible(true);
									if (ip.playersNumber == 2) {
										gameTextArea.append("Number of players in this room: " + ip.playersNumber
												+ ". You can start the game.\n");
									} else
										gameTextArea.append("Number of players in this room: " + ip.playersNumber
												+ ". You have to wait for another player.\n");
								} else {
									if (ip.roomNumber == room) {
										gameTextArea.setText(null);
										gameTextArea.append("Number of players in this room: " + ip.playersNumber
												+ ". You can start the game.\n");
									}
								}
							}
							textArea.append(ip.message);
						}
					}
					if (p instanceof GamePacket) {
						GamePacket gp = (GamePacket) p;
						if(gp.type == GamePacket.Type.MESSAGE) {
							gameTextArea.append(gp.message);
						}	
						if(gp.type == GamePacket.Type.SHOT) {
							gameTextArea.append("You opponent shots at: " + gp.coords.getX() + ", " + gp.coords.getY() + "\n");
						}	
						if(gp.type == Type.ANSWER) {
							gameTextArea.append("Result of your shot: " + gp.answer + "\n");
						}
					}

				} catch (ClassNotFoundException | IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
