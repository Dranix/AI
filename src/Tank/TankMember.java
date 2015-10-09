package Tank;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

public class TankMember extends SmartTank {
	public void run() {
		super.run();

		ahead(1000);
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
	}

	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
	}
}
