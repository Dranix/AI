package Tank;

import java.awt.Color;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class SmartTank extends TeamRobot {
	protected List<EnemyTank> enemyTankList;

	public void run() {
		InitTank();
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		// Save enemy movement to local database and send to team mate
		SaveEnemyMovements(this, e);
	}

	public void onMessageReceived(MessageEvent e) {
		if (e.getMessage() instanceof Message) {
			Message message = (Message) e.getMessage();
			switch (message.getMessageType()) {
			case SendEnemyInformation:
				EnemyTank enemy = (EnemyTank) message.getMessageObject();
				for (EnemyTank eTank : enemyTankList) {
					if (eTank.get_name().equals(enemy.get_name())) {
						eTank.updateFromTeamate(enemy);
					}
				}
				break;

			default:
				break;
			}
		}
	}

	public void onPaint(Graphics2D g) {

	}

	private void InitTank() {
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setScanColor(Color.black);
		setBulletColor(Color.black);

		enemyTankList = new ArrayList<EnemyTank>();
	}

	private void SaveEnemyMovements(TeamRobot ourRobot, ScannedRobotEvent e) {
		if (isTeammate(e.getName())) {
			return;
		}

		// check if exist
		if (enemyTankList.size() == 0) {
			EnemyTank newEnemy = new EnemyTank(ourRobot, e);

			enemyTankList.add(newEnemy);
		} else {
			for (EnemyTank enemy : enemyTankList) {
				if (enemy == null || !e.getName().equals(enemy.get_name())) {
					EnemyTank newEnemy = new EnemyTank(ourRobot, e);

					enemyTankList.add(newEnemy);
				} else if (e.getName().equals(enemy.get_name())) {
					enemy.update(ourRobot, e);
					SendMessage(new Message(MessageType.SendEnemyInformation, enemy));
				}
			}
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
