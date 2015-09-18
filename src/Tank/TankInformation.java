package Tank;

import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class TankInformation {
	public double Turn;
	public ScannedRobotEvent Event;
	public TeamRobot OurRobot;

	public TeamRobot getOurRobot() {
		return OurRobot;
	}

	public void setOurRobot(TeamRobot ourRobot) {
		OurRobot = ourRobot;
	}

	public ScannedRobotEvent getEvent() {
		return Event;
	}

	public void setEvent(ScannedRobotEvent event) {
		Event = event;
	}

	public double getTurn() {
		return Turn;
	}

	public void setTurn(int turn) {
		Turn = turn;
	}

	public TankInformation(double turn, TeamRobot ourRobot, ScannedRobotEvent event) {
		this.Turn = turn;
		this.OurRobot = ourRobot;
		this.Event = event;
	}

	public Point GetTankPoint() {
		double enemyBearing = OurRobot.getHeading() + Event.getBearing();
		double enemyX = OurRobot.getX() + Event.getDistance() * Math.sin(Math.toRadians(enemyBearing));
		double enemyY = OurRobot.getY() + Event.getDistance() * Math.cos(Math.toRadians(enemyBearing));

		return new Point(enemyX, enemyY);
	}

	public void Print() {
		System.out.println(this.getTurn() + " - "+Event.getName() + " - " + Event.getEnergy() + "(" + GetTankPoint().getX() +","+ GetTankPoint().getY()+")");
	}
}
