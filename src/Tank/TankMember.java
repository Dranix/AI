package Tank;

import robocode.MessageEvent;
import robocode.ScannedRobotEvent;

public class TankMember extends SmartTank {
	public void run() {
		super.run();

		while (true) {
			if (getFocusTarget() != null) {
				out.println(getFocusTarget().get_name());
			}

			execute();
		}
	}

	public void onScannedRobot(ScannedRobotEvent e) {
		super.onScannedRobot(e);
	}

	public void onMessageReceived(MessageEvent e) {
		super.onMessageReceived(e);
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

			default:
				break;
			}
		}
	}
}
