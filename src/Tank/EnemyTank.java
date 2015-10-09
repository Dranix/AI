package Tank;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import robocode.ScannedRobotEvent;
import robocode.TeamRobot;

public class EnemyTank implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public EnemyTank(TeamRobot robot, ScannedRobotEvent e) {
		super();

		this.absBearingDegree = (robot.getHeading() + e.getBearing() % 360);
		this.x = (int) (robot.getX() + Math.sin(Math.toRadians(absBearingDegree)) * distance);
		this.y = (int) (robot.getY() + Math.cos(Math.toRadians(absBearingDegree)) * distance);
		this.bearing = e.getBearing();
		this.distance = e.getDistance();
		this.energy = e.getEnergy();
		this.heading = e.getHeading();
		this.name = e.getName();
		this.velocity = e.getVelocity();
		this.ourX = (int) robot.getX();
		this.ourY = (int) robot.getY();
		this.turn = (int) e.getTime();
		history = new ArrayList<HistoryPoint>();
	}

	private int x;
	private int y;
	private double bearing;
	private double distance;
	private double energy;
	private double heading;
	private String name;
	private double velocity;
	private double absBearingDegree;
	private int ourX;
	private int ourY;
	private int turn;
	private List<HistoryPoint> history;

	double getX() {
		return x;
	}

	void setX(int x) {
		this.x = x;
	}

	double getY() {
		return y;
	}

	void setY(int y) {
		this.y = y;
	}

	public double get_velocity() {
		return velocity;
	}

	public void set_velocity(double _velocity) {
		this.velocity = _velocity;
	}

	public String get_name() {
		return name;
	}

	public void set_name(String _name) {
		this.name = _name;
	}

	public double get_heading() {
		return heading;
	}

	public void set_heading(double _heading) {
		this.heading = _heading;
	}

	public double get_energy() {
		return energy;
	}

	public void set_energy(double _energy) {
		this.energy = _energy;
	}

	public double get_distance() {
		return distance;
	}

	public void set_distance(double _distance) {
		this.distance = _distance;
	}

	public double get_bearing() {
		return bearing;
	}

	public void set_bearing(double _bearing) {
		this.bearing = _bearing;
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public List<HistoryPoint> getHistory() {
		return history;
	}

	public void setHistory(List<HistoryPoint> history) {
		this.history = new ArrayList<>(history);
	}

	public double getAbsBearingDegree() {
		return absBearingDegree;
	}

	public void setAbsBearingDegree(double absBearingDegree) {
		this.absBearingDegree = absBearingDegree;
	}

	public static double getFutureX(TeamRobot robot, ScannedRobotEvent e, int when) {

		double absBearingDeg = (robot.getHeading() + e.getBearing());
		if (absBearingDeg < 0)
			absBearingDeg += 360;
		double x = robot.getX() + Math.sin(Math.toRadians(absBearingDeg)) * e.getDistance();

		return x + Math.sin(Math.toRadians(e.getHeading())) * e.getVelocity() * when;
	}

	public static double getFutureY(TeamRobot robot, ScannedRobotEvent e, int when) {
		double absBearingDeg = (robot.getHeading() + e.getBearing());
		if (absBearingDeg < 0)
			absBearingDeg += 360;
		double y = robot.getY() + Math.cos(Math.toRadians(absBearingDeg)) * e.getDistance();

		return y + Math.cos(Math.toRadians(e.getHeading())) * e.getVelocity() * when;
	}

	public double getFutureXFromLastRecord(int when) {
		double fx = ourX + Math.sin(Math.toRadians(absBearingDegree)) * distance;
		return fx + Math.sin(Math.toRadians(heading)) * velocity * when;
	}

	public double getFutureYFromLastRecord(int when) {
		double fy = ourY + Math.cos(Math.toRadians(absBearingDegree)) * distance;
		return fy + Math.cos(Math.toRadians(heading)) * velocity * when;
	}

	public void update(TeamRobot robot, ScannedRobotEvent e) {
		this.absBearingDegree = (robot.getHeading() + e.getBearing()) % 360;
		this.x = (int) (robot.getX() + Math.sin(Math.toRadians(absBearingDegree)) * e.getDistance());
		this.y = (int) (robot.getY() + Math.cos(Math.toRadians(absBearingDegree)) * e.getDistance());
		this.bearing = e.getBearing();
		this.distance = e.getDistance();
		this.energy = e.getEnergy();
		this.heading = e.getHeading();
		this.velocity = e.getVelocity();
		this.turn = (int) e.getTime();
		this.ourX = (int) robot.getX();
		this.ourY = (int) robot.getY();
		HistoryPoint hp = new HistoryPoint((int) e.getTime(), new Point(this.x, this.y));
		history.add(hp);
	}

	public void updateFromTeamate(EnemyTank enemy) {
		this.x = enemy.x;
		this.y = enemy.y;
		this.bearing = enemy.bearing;
		this.distance = enemy.distance;
		this.energy = enemy.energy;
		this.heading = enemy.heading;
		this.velocity = enemy.velocity;
		this.absBearingDegree = enemy.absBearingDegree;
		HistoryPoint point = enemy.history.get(history.size() - 1);
		if (!this.history.contains(point)) {
			this.history.add(point);
		}
	}

	public void printHistoryPoint(Graphics2D g) {
		for (HistoryPoint p : history) {
			g.setColor(java.awt.Color.red);
			g.fillRect((int) p.getPoint().getX(), (int) p.getPoint().getY(), 5, 5);
			System.out.println(p.getTurn());
		}
	}

	public void printFuturePointFromLastRecord(Graphics2D g, int when) {

		int fy = (int) getFutureYFromLastRecord(when);
		int fx = (int) getFutureXFromLastRecord(when);
		g.fillRect(fx, fy, 40, 40);
	}

	public int getOurX() {
		return ourX;
	}

	public void setOurX(int ourX) {
		this.ourX = ourX;
	}

	public int getOurY() {
		return ourY;
	}

	public void setOurY(int ourY) {
		this.ourY = ourY;
	}
}
