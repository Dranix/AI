package Tank;

import robocode.*;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;

import Tank.Point;

public class SmartTank extends TeamRobot {
	private TankInformationList informationList;

	public void run() {
		InitTank();

		while (true) {
			try {
				// Send enemy position to team mates
				broadcastMessage(new Point(getX(), getY()));
			} catch (IOException ex) {
				out.println("Unable to send order: ");
				ex.printStackTrace(out);
			}
		
			informationList.PrintRecord();
			
			execute();
		}

	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Save enemy movement to local database and send to team mate
		SaveEnemyMovements(this, e);
	}

	public void onMessageReceived(MessageEvent e) {
		if (e.getMessage() instanceof Message) {
			Message message = (Message) e.getMessage();
			switch (message.getMessageType()) {
			case SendEnemyInformation:
				informationList.AddInformation((TankInformation) message.getMessageObject());
				break;

			default:
				break;
			}
		}
	}

	public void onPaint(Graphics2D g) {
		g.setColor(java.awt.Color.GREEN);
		informationList.PrintLocation(g);
	}

	private void InitTank() {
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setScanColor(Color.black);
		setBulletColor(Color.black);

		informationList = new TankInformationList();
	}

	private void SaveEnemyMovements(TeamRobot ourRobot, ScannedRobotEvent e) {
		if (isTeammate(e.getName())) {
			return;
		}

		// Save to database
		TankInformation info = informationList.AddInformation(ourRobot, e);
		SendMessage(new Message(MessageType.SendEnemyInformation, info));
	}

	private void SendMessage(Message message) {
		try {
			broadcastMessage(message);
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}
}
