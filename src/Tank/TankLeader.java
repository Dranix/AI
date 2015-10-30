package Tank;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collections;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.RobotDeathEvent;
import robocode.ScannedRobotEvent;

public class TankLeader extends SmartTank {
	private static final int distanceMin = 250;
	private static final int distanceMax = 350;
	private static final int energyRequireToFire = 10;
	private static final int energyRequireToSetFire = 50;

	private int fx;
	private int fy;
	private int moveDirection = 1;
	private double tankMemberEnergy;

	@Override
	public void run() {
		super.run();
		phase = Phase.LeaderPhase1;

		while (true) {
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
			sendMessage(new Message(MessageType.SendPhaseToMember, Phase.LeaderPhase2));
		}
	}

	private void leaderTankPhase2() {
		// Decide the focus target base on location and energy
		calculateFocusTarget();

		// scan 360
		setTurnRadarRight(360);

		// Keep 200px distance
		moveAndKeepDistance(new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY()), distanceMin);

		// Fire to the potencial target
		fire();

		if (enemyTankList.size() == 1) {
			calculateFocusTarget();

			switchPhase23();
		}
	}

	private void switchPhase23() {
		if (tankMemberEnergy == 0) {
			if (this.getEnergy() > getFocusTarget().get_energy()) {
				phase = Phase.LeaderPhase3;
			} else {
				phase = Phase.LeaderPhase2;
			}
		} else {
			if (getEnergy() > tankMemberEnergy && getEnergy() > getFocusTarget().get_energy()) {
				phase = Phase.LeaderPhase3;
			} else if (tankMemberEnergy > getEnergy() && tankMemberEnergy > getFocusTarget().get_energy()) {
				sendMessage(new Message(MessageType.SendPhaseToMember, Phase.MemberPhase3));
			} else {
				phase = Phase.LeaderPhase2;
				sendMessage(new Message(MessageType.SendPhaseToMember, Phase.MemberPhase2));
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
		if (getFocusTarget().get_distance() > distanceMax && !getFocusTarget().isStand()) {
			setTurnRightRadians(normalRelativeAngleRadians(
					absoluteBearingRadians(getRobotLocation(), point) - getHeadingRadians()));
			setAhead(getRobotLocation().distance(point) - distance);
		} else if (getFocusTarget().get_distance() < distanceMin && !getFocusTarget().isStand()) {
			setTurnRightRadians(normalRelativeAngleRadians(
					getHeadingRadians() - absoluteBearingRadians(getRobotLocation(), point)));
			setAhead(distance - getRobotLocation().distance(point));
		} else {
			if (getVelocity() == 0)
				moveDirection *= -1;

			// circle our enemy
			setTurnRight(getFocusTarget().get_bearing() + 90);
			setAhead(150 * moveDirection);
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
		calculateFocusTarget();
		switchPhase23();
		setTurnRadarRight(360);
		Point2D enemyPoint = new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY());
		setTurnRightRadians(normalRelativeAngleRadians(
				absoluteBearingRadians(getRobotLocation(), enemyPoint) - getHeadingRadians()));
		setAhead(getRobotLocation().distance(enemyPoint) - 100);

		fire();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
	}

	@Override
	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
		try {
			if (e.getMessage() instanceof Message) {
				Message message = (Message) e.getMessage();
				switch (message.getMessageType()) {
				case SendEnegerToLeader:
					tankMemberEnergy = (double) message.getMessageObject();
					break;

				default:
					break;
				}
			}
		} catch (Exception ex) {
			System.err.println("Leader Tank: " + ex.getMessage());
		}
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(java.awt.Color.GREEN);
		// Show target
		if (getFocusTarget() != null) {
			g.fillRect((int) getFocusTarget().getX(), (int) getFocusTarget().getY(), 40, 40);
		}

		for (EnemyTank enemy : enemyTankList) {
			// enemy.printHistoryPoint(g);
			// g.setColor(java.awt.Color.RED);
			// enemy.printFuturePointFromLastRecord(g, (int) getTime() -
			// enemy.getTurn() + 3);
			// g.fillRect((int) enemy.getX(), (int) enemy.getY(), 40, 40);
		}
	}

	@Override
	public void onHitWall(HitWallEvent e) {
		// moveDirection *= -1;
	}

	@Override
	public void onHitRobot(HitRobotEvent e) {
		moveDirection *= -1;
	}

	@Override
	public void onRobotDeath(RobotDeathEvent e) {
		super.onRobotDeath(e);
		if (isTeammate(e.getName())) {
			tankMemberEnergy = 0;
		}
	}

}
