package Tank;

import java.util.ArrayList;
import java.util.List;
import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class TankInformationList {
	private List<TankInformation> tankInformationList = new ArrayList<TankInformation>();

	public void AddInformation(TeamRobot ourRobot, ScannedRobotEvent event) {
		TankInformation information = new TankInformation(ourRobot.getTime(), ourRobot, event);
		if (!tankInformationList.contains(information)) {
			tankInformationList.add(information);
		}
	}

	public void PrintRecord() {
		for (TankInformation info : tankInformationList) {
			info.Print();
		}
	}

	public List<TankInformation> GetInformationByName(String tankName) {
		List<TankInformation> list = new ArrayList<TankInformation>(); 
		for (TankInformation info : tankInformationList) {
			if(info.getEvent().getName().equals(tankName)){
				list.add(info);
			}
		}
		
		return list;
	}
	
	public void DetectEnemyMovement(){
		
	}
}
