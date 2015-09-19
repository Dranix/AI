package Tank;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class TankInformationList {
	private List<TankInformation> tankInformationList = new ArrayList<TankInformation>();

	public TankInformation AddInformation(TeamRobot ourRobot, ScannedRobotEvent event) {
		//Calculate enemy location
		double angle = Math.toRadians((ourRobot.getHeading() + event.getBearing()) % 360);
		double scannedX = (int) (ourRobot.getX() + Math.sin(angle) * event.getDistance());
		double scannedY = (int) (ourRobot.getY() + Math.cos(angle) * event.getDistance());
		Point enemyLocation = new Point(scannedX, scannedY);

		TankInformation information = new TankInformation(ourRobot.getTime(), enemyLocation, event);
		AddInformation(information);
		
		return information;
	}

	public void AddInformation(TankInformation info) {
		if (!tankInformationList.contains(info)) {
			tankInformationList.add(info);
		}
	}
	
	public void PrintRecord() {
		for (TankInformation info : tankInformationList) {
			info.Print();
		}
	}

	public void PrintLocation(Graphics2D g) {
		for (TankInformation info : tankInformationList) {
			g.fillRect((int) info.EnemyLocation.getX(), (int) info.EnemyLocation.getY(), 10, 10);
		}
	}

	public List<TankInformation> GetInformationByName(String tankName) {
		List<TankInformation> list = new ArrayList<TankInformation>();
		for (TankInformation info : tankInformationList) {
			if (info.getEvent().getName().equals(tankName)) {
				list.add(info);
			}
		}

		return list;
	}
}
