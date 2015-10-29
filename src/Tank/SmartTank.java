package Tank;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

/**
 * @author Dranix
 *
 */
public class SmartTank extends TeamRobot {
	protected List<EnemyTank> enemyTankList;
	protected Phase phase;

	public void run() {
		initTank();
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		// Save enemy movement to local database and send to team mate
		saveEnemyMovements(this, e);
	}

	public void onMessageReceived(MessageEvent e) {
		try {
			if (e.getMessage() instanceof Message) {
				Message message = (Message) e.getMessage();
				switch (message.getMessageType()) {
				case SendEnemyInformation:
					EnemyTank receiveObject = (EnemyTank) message.getMessageObject();
					EnemyTank enemy = getEnemyTankByName(receiveObject.get_name());
					if (enemy == null) {
						enemyTankList.add(receiveObject);
					} else {
						enemy.updateFromTeamate(enemy);
					}
					break;

				default:
					break;
				}
			}

		} catch (Exception ex) {
			System.err.println("Smart Tank: " + ex.getMessage());
		}
	}

	public void onRobotDeath(RobotDeathEvent e) {
		for (EnemyTank enemy : enemyTankList) {
			if (e.getName().equals(enemy.get_name())) {
				enemyTankList.remove(enemy);
				break;
			}
		}
	}

	private void initTank() {
		setBodyColor(Color.black);
		setGunColor(Color.black);
		setRadarColor(Color.black);
		setScanColor(Color.black);
		setBulletColor(Color.black);

		enemyTankList = new ArrayList<EnemyTank>();
	}

	private void saveEnemyMovements(TeamRobot ourRobot, ScannedRobotEvent e) {
		if (isTeammate(e.getName())) {
			return;
		}

		// check if exist
		EnemyTank enemy = getEnemyTankByName(e.getName());
		if (enemy == null) {
			EnemyTank newEnemy = new EnemyTank(ourRobot, e);
			enemyTankList.add(newEnemy);
		} else {
			enemy.update(ourRobot, e);
			sendMessage(new Message(MessageType.SendEnemyInformation, enemy));
		}
	}

	protected void sendMessage(Message message) {
		try {
			broadcastMessage(message);
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
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

	protected EnemyTank getFocusTarget() {
		if (enemyTankList.size() == 0) {
			return null;
		}

		for (EnemyTank enemy : enemyTankList) {
			if (enemy.isFocus()) {
				return enemy;
			}
		}

		return enemyTankList.get(0);
	}

	protected EnemyTank getEnemyTankByName(String tankName) {
		if (enemyTankList != null && enemyTankList.size() != 0) {
			for (EnemyTank enemy : enemyTankList) {
				if (enemy.get_name().equals(tankName)) {
					return enemy;
				}
			}
		}

		return null;
	}

	protected void setFocusTarget(EnemyTank enemyTank) {
		for (EnemyTank tank : enemyTankList) {
			tank.setFocus(false);
		}
		enemyTank.setFocus(true);
	}
}
