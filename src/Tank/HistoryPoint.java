package Tank;

public class HistoryPoint implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	public HistoryPoint(int turn, Point point) {
		super();
		this.turn = turn;
		this.point = point;
	}

	private int turn;
	private Point point;

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

}
