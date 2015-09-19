package Tank;

import robocode.ScannedRobotEvent;

public class TankInformation implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	public double Turn;
	public ScannedRobotEvent Event;
	public Point EnemyLocation;
	
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

	public TankInformation(double turn, Point enemyLocation, ScannedRobotEvent event) {
		this.Turn = turn;
		this.EnemyLocation = enemyLocation;
		this.Event = new ScannedRobotEvent(event.getName(), event.getEnergy(), event.getBearing(), event.getDistance(), event.getHeading(), event.getVelocity(), event.isSentryRobot());
	}

	public void Print() {
		System.out.println(this.getTurn() + " - "+Event.getName() + " - " + Event.getEnergy() + "(" + EnemyLocation.getX() +","+ EnemyLocation.getY()+")");
	}
}
