package Tank;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class SmartTank extends TeamRobot {
	protected List<EnemyTank> enemyTankList;
	protected Phase phase;
	protected EnemyTank focusTarget;

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
					break;
				} else if (e.getName().equals(enemy.get_name())) {
					enemy.update(ourRobot, e);
					SendMessage(new Message(MessageType.SendEnemyInformation, enemy));
				}
			}
		}

	}

	protected void SendMessage(Message message) {
		try {
			broadcastMessage(message);
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}

	protected EnemyTank getFocusTarget() {
		for (EnemyTank enemy : enemyTankList) {
			if (enemy.isFocus()) {
				return enemy;
			}
		}

		return enemyTankList.get(0);
	}

	protected void goTo(Point2D point) {
		setTurnRightRadians(
				normalRelativeAngleRadians(absoluteBearingRadians(getRobotLocation(), point) - getHeadingRadians()));
		setAhead(getRobotLocation().distance(point));
	}

	protected double absoluteBearingRadians(Point2D source, Point2D target) {
		return Math.atan2(target.getX() - source.getX(), target.getY() - source.getY());
	}

	protected double normalRelativeAngleRadians(double angle) {
		return Math.atan2(Math.sin(angle), Math.cos(angle));
	}

	protected Point2D getRobotLocation() {
		return new Point2D.Double(getX(), getY());
	}
}
