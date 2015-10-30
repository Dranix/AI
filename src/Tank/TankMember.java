package Tank;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import robocode.HitRobotEvent;
import robocode.HitWallEvent;
import robocode.MessageEvent;
import robocode.ScannedRobotEvent;
import robocode.util.Utils;

public class TankMember extends SmartTank {
	private int moveDirection = 1;
	private int fx, fy;

	@Override
	public void run() {
		super.run();
		phase = Phase.MemberPhase1;
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		sendMessage(new Message(MessageType.SendEnegerToLeader, getEnergy()));
		while (true) {
			switch (phase) {
			case MemberPhase1:
				memberTankPhase1();
				break;

			case LeaderPhase2:
				memberTankPhase2();
				break;

			case LeaderPhase3:
				memberTankPhase3();
				break;

			default:

			}
			execute();
		}
	}

	protected void memberTankPhase1() {
		setTurnRadarRight(1000);
	}

	protected void memberTankPhase2() {
		setTurnRadarLeftRadians(1000);
		doMove();
		fire();
	}

	protected void memberTankPhase3() {
		setTurnRadarLeftRadians(1000);
		goTo(new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY()));
		fire();
	}

	@Override
	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);

		double v1;
		setTurnGunRightRadians(Utils.normalRelativeAngle((v1 = getHeadingRadians() + e.getBearingRadians())
				+ Math.random() * e.getVelocity() / 13 * Math.sin(e.getHeadingRadians() - v1)
				- getGunHeadingRadians()));

		setFire(2);
	}

	@Override
	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
		try {

			if (e.getMessage() instanceof Message) {
				Message message = (Message) e.getMessage();
				switch (message.getMessageType()) {
				case SendFocusTarget:
					String focusName = (String) message.getMessageObject();
					EnemyTank focusTank = getEnemyTankByName(focusName);
					if (focusTank != null) {
						setFocusTarget(focusTank);
					}
					break;

				case SendPhaseToMember:
					phase = (Phase) message.getMessageObject();
					break;

				}
			}

		} catch (Exception ex) {
			System.err.println("Member Tank: " + ex.getMessage());
		}
	}

	public void doMove() {
		if (getFocusTarget() != null) {
			if (getFocusTarget().get_distance() > 300) {
				goTo(new Point2D.Double(getFocusTarget().getX(), getFocusTarget().getY()));
			} else {
				// always square off against our enemy
				setTurnRight(getFocusTarget().get_bearing() + 90);

				// strafe by changing direction every 20 ticks
				if (getTime() % 20 == 0) {
					moveDirection *= -1;
					setAhead(150 * moveDirection);
				}
			}
		}
	}

	private void fire() {
		if (getFocusTarget() != null && getTime() - getFocusTarget().getTurn() < 5) {
			double firePower = Math.min(500 / getFocusTarget().get_distance(), 3);
			double bulletSpeed = 20 - firePower * 3;
			long time = (long) (getFocusTarget().get_distance() / bulletSpeed);
			fx = (int) getFocusTarget()
					.getFutureXFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
			fy = (int) getFocusTarget()
					.getFutureYFromLastRecord((int) getTime() - getFocusTarget().getTurn() + (int) time);
			fx = (int) Math.abs(Math.min(fx, getBattleFieldWidth()));
			fy = (int) Math.abs(Math.min(fy, getBattleFieldHeight()));

			double absDeg = Helper.absoluteBearing(getX(), getY(), fx, fy);
			setTurnGunRight(Helper.normalizeBearing(absDeg - getGunHeading()));

			if (getGunHeat() == 0 && Math.abs(getGunTurnRemaining()) < 10) {
				fire(firePower);
			}
		}
	}

	@Override
	public void onPaint(Graphics2D g) {
		g.setColor(java.awt.Color.RED);
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
		moveDirection *= -1;
	}

	@Override
	public void onHitRobot(HitRobotEvent e) {
		moveDirection *= -1;
	}
}
