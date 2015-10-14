package Tank;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TankLeader extends SmartTank {
	private int fx;
	private int fy;
	private int moveDirection = 1;
	private double tankMemberEnergy;

	public void run() {
		super.run();
		phase = Phase.LeaderPhase1;

		while (true) {
			switch (phase) {
			case LeaderPhase1:
				LeaderTankPhase1();
				break;

			case LeaderPhase2:
				LeaderTankPhase2();
				break;

			case LeaderPhase3:
				LeaderTankPhase3();
				break;

			case Test:
				setTurnRadarRight(1000);

			default:

			}
			execute();
		}
	}

	// Scan 360
	// Send ScannedEvent to teammate
	// Calculate and plan the strategy
	private void LeaderTankPhase1() {
		setTurnRadarRight(1000);
		if (enemyTankList.size() > 1) {
			phase = Phase.LeaderPhase2;
		}
	}

	private void LeaderTankPhase2() {
		// Decide the focus target base on location and energy
		CalculateFocusTarget();

		// scan 360
		setTurnRadarRight(360);

		// Keep 200px distance
		MoveAndKeepDistance(new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY()), 200);

		// Fire to the potencial target
		Fire();

		if (enemyTankList.size() == 1) {
			if (this.getEnergy() > focusTarget.get_energy()) {
				phase = Phase.LeaderPhase3;
			}

			focusTarget = enemyTankList.get(0);
		}

	}

	private void CalculateFocusTarget() {
		focusTarget = enemyTankList.get(0);
		for (EnemyTank enemy : enemyTankList) {
			if (focusTarget.get_distance() > enemy.get_distance()) {
				focusTarget = enemy;
			}
		}
	}

	private void MoveAndKeepDistance(Point2D point, int distance) {
		if (getFocusTarget().get_distance() > 300) {
			setTurnRightRadians(normalRelativeAngleRadians(
					absoluteBearingRadians(getRobotLocation(), point) - getHeadingRadians()));
			setAhead(getRobotLocation().distance(point) - distance);
		} else if (getFocusTarget().get_distance() < 200) {
			setTurnRightRadians(normalRelativeAngleRadians(
					getHeadingRadians() - absoluteBearingRadians(getRobotLocation(), point)));
			setAhead(distance - getRobotLocation().distance(point));
		} else {
			if (getVelocity() == 0)
				moveDirection *= -1;

			// circle our enemy
			setTurnRight(getFocusTarget().get_bearing() + 90);
			setAhead(1000 * moveDirection);
		}
	}

	private void Fire() {
		double firePower = Math.min(500 / getFocusTarget().get_distance(), 3);
		double bulletSpeed = 20 - firePower * 3;
		long time = (long) (getFocusTarget().get_distance() / bulletSpeed);
		fx = (int) getFocusTarget().getFutureXFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
		fy = (int) getFocusTarget().getFutureYFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
		fx = (int) Math.abs(Math.min(fx, getBattleFieldWidth()));
		fy = (int) Math.abs(Math.min(fy, getBattleFieldHeight()));

		double absDeg = Helper.absoluteBearing(getX(), getY(), fx, fy);
		setTurnGunRight(Helper.normalizeBearing(absDeg - getGunHeading()));

		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 1) {
			fire(firePower);
		}

		setFire(firePower);
	}

	private void LeaderTankPhase3() {
		Point2D enemyPoint = new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY());
		setTurnRightRadians(normalRelativeAngleRadians(
				absoluteBearingRadians(getRobotLocation(), enemyPoint) - getHeadingRadians()));
		setAhead(getRobotLocation().distance(enemyPoint) - 100);

		Fire();
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
		//
		// switch (phase) {
		// case LeaderPhase1:
		// break;
		//
		// case LeaderPhase2:
		// break;
		//
		// case LeaderPhase3:
		// break;
		// case LeaderPhase4:
		// break;
		// default:
		//
		// }
	}

	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
	}

	public void onPaint(Graphics2D g) {
		g.setColor(java.awt.Color.GREEN);
		// Show target
		g.fillRect((int) getFocusTarget().getX(), (int) getFocusTarget().getY(), 40, 40);

		for (EnemyTank enemy : enemyTankList) {
			// enemy.printHistoryPoint(g);
			// g.setColor(java.awt.Color.RED);
			// enemy.printFuturePointFromLastRecord(g, (int) getTime() -
			// enemy.getTurn() + 3);
			// g.fillRect((int) enemy.getX(), (int) enemy.getY(), 40, 40);
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
}
