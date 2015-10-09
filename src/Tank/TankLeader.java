package Tank;

import java.awt.Graphics2D;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

public class TankLeader extends SmartTank {
	private int fx;
	private int fy;

	public void run() {
		super.run();

		// turnLeft(getHeading() % 90);

		while (true) {
			setTurnRadarRight(10000);
			ahead(100);
			back(100);
			execute();
		}

		/*
		 * while (true) { try { // Send enemy position to team mates
		 * broadcastMessage(new Point(getX(), getY())); } catch (IOException ex)
		 * { out.println("Unable to send order: "); ex.printStackTrace(out); }
		 * 
		 * informationList.PrintRecord();
		 * 
		 * 
		 * 
		 * }
		 */

	}

	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);

		fx = (int) EnemyTank.getFutureX(this, e, 3);
		fy = (int) EnemyTank.getFutureY(this, e, 3);
	}

	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);

	}

	public void onPaint(Graphics2D g) {
		g.setColor(java.awt.Color.GREEN);
		g.fillRect(fx, fy, 40, 40);
		for (EnemyTank enemy : enemyTankList) {
			// enemy.printHistoryPoint(g);
			g.setColor(java.awt.Color.RED);
			enemy.printFuturePointFromLastRecord(g, (int) getTime() - enemy.getTurn() + 3);
			// g.fillRect((int) enemy.getX(), (int) enemy.getY(), 40, 40);
		}
	}
}
