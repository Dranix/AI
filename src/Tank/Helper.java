package Tank;

public class Helper {
	public static Point CalculateEnemyPoint(double heading, double bearing, double x, double y, double distance) {
		double angle = Math.toRadians((heading + bearing) % 360);
		double scannedX = (int) (x + Math.sin(angle) * distance);
		double scannedY = (int) (y + Math.cos(angle) * distance);
		return new Point(scannedX, scannedY);
	}
}
