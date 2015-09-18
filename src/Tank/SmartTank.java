package Tank;

import robocode.*;

import java.awt.Color;
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

			setTurnRadarRight(360);
			fire(2);
			execute();
		}
	}

	/**
	 * onScannedRobot: What to do when you see another robot
	 */
	public void onScannedRobot(ScannedRobotEvent e) {
		// Save enemy movement to local database and send to team mate
		SaveEnemyMovements(e, true);

		// Don't fire on teammates
		if (isTeammate(e.getName())) {
			return;
		} else {
			// TODO: Add avoid bullet mechanism

			if (e.getDistance() > 100) {
				turnRight(e.getBearing());
				ahead(150);
				fire(1);
			} else if (e.getDistance() < 100) {
				turnRight(e.getBearing());
				fire(2);
				ahead(50);
			} else if (e.getDistance() < 50) {
				turnRight(e.getBearing());
				fire(3);
			}
		}

		execute();
	}

	public void onMessageReceived(MessageEvent e) {
		if (e.getMessage() instanceof Message) {
			Message message = (Message) e.getMessage();
			switch (message.getMessageType()) {
			case SendEnemyInformation:
				SaveEnemyMovements((ScannedRobotEvent) message.getMessageObject(), false);
				break;

			default:
				break;
			}
		}
	}

	private void InitTank() {
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setScanColor(Color.black);
		setBulletColor(Color.black);

		informationList = new TankInformationList();
	}

	private void SaveEnemyMovements(ScannedRobotEvent e, boolean isBroadcast) {
		if (isTeammate(e.getName())) {
			return;
		}
		
		// Save to database
		informationList.AddInformation(this, e);

		if (isBroadcast) {
			SendMessage(new Message(MessageType.SendEnemyInformation, e));
		}
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
