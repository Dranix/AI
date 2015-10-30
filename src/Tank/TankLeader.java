package Tank;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collections;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

public class TankLeader extends SmartTank {
	private static final int distanceMin = 200;
	private static final int distanceMax = 300;
	private static final int energyRequireToFire = 10;
	private static final int energyRequireToSetFire = 50;

	private int fx;
	private int fy;
	private int moveDirection = 1;
	private double tankMemberEnergy;

	public void run() {
		super.run();
		phase = Phase.LeaderPhase1;

		while (true) {
			if (getFocusTarget() != null) {
				out.println(getFocusTarget().get_name());
			}

			switch (phase) {
			case LeaderPhase1:
				leaderTankPhase1();
				break;

			case LeaderPhase2:
				leaderTankPhase2();
				break;

			case LeaderPhase3:
				leaderTankPhase3();
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
	private void leaderTankPhase1() {
		setTurnRadarRight(1000);
		if (enemyTankList.size() > 1) {
			phase = Phase.LeaderPhase2;
		}
	}

	private void leaderTankPhase2() {
		// Decide the focus target base on location and energy
		calculateFocusTarget();

		// scan 360
		setTurnRadarRight(360);

		// Keep 200px distance
		moveAndKeepDistance(new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY()), 200);

		// Fire to the potencial target
		fire();

		if (enemyTankList.size() == 1) {
			calculateFocusTarget();
			if (this.getEnergy() > getFocusTarget().get_energy()) {
				phase = Phase.LeaderPhase3;
			}
		}

	}

	private void calculateFocusTarget() {
		Collections.sort(enemyTankList, new EnergyComparator());
		EnemyTank focus = enemyTankList.get(0);
		setFocusTarget(focus);

		sendMessage(new Message(MessageType.SendFocusTarget, focus.get_name()));
	}

	private void moveAndKeepDistance(Point2D point, int distance) {
		if (getFocusTarget().get_distance() > distanceMax) {
			setTurnRightRadians(normalRelativeAngleRadians(
					absoluteBearingRadians(getRobotLocation(), point) - getHeadingRadians()));
			setAhead(getRobotLocation().distance(point) - distance);
		} else if (getFocusTarget().get_distance() < distanceMin) {
			setTurnRightRadians(normalRelativeAngleRadians(
					getHeadingRadians() - absoluteBearingRadians(getRobotLocation(), point)));
			setAhead(distance - getRobotLocation().distance(point));
		} else {
			if (getVelocity() == 0)
				moveDirection *= -1;

			// circle our enemy
			setTurnRight(getFocusTarget().get_bearing() + 90);
			setAhead(300 * moveDirection);
		}
	}

	private void fire() {
		double firePower = Math.min(500 / getFocusTarget().get_distance(), 3);
		double bulletSpeed = 20 - firePower * 3;
		long time = (long) (getFocusTarget().get_distance() / bulletSpeed);
		fx = (int) getFocusTarget().getFutureXFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
		fy = (int) getFocusTarget().getFutureYFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
		fx = (int) Math.abs(Math.min(fx, getBattleFieldWidth()));
		fy = (int) Math.abs(Math.min(fy, getBattleFieldHeight()));

		double absDeg = Helper.absoluteBearing(getX(), getY(), fx, fy);
		setTurnGunRight(Helper.normalizeBearing(absDeg - getGunHeading()));

		if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
			if (getEnergy() > energyRequireToFire) {
				fire(firePower);
			}
		}

		if (getEnergy() > energyRequireToSetFire) {
			setFire(firePower);
		}
	}

	private void leaderTankPhase3() {
		setTurnRadarRight(360);
		Point2D enemyPoint = new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY());
		setTurnRightRadians(normalRelativeAngleRadians(
				absoluteBearingRadians(getRobotLocation(), enemyPoint) - getHeadingRadians()));
		setAhead(getRobotLocation().distance(enemyPoint) - 100);

		fire();
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

}
