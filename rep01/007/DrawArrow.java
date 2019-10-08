import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;

public class DrawArrow extends Path2D.Float {
	enum Side {
		LEFT, RIGHT
	};

	Point start;
	Point end;
	double length;
	double slope; //radian
	int angle; //degree
	double theta; //radian
	int barblen;

	//起点,終点,矢羽根の角度,矢羽根の長さ
	public DrawArrow(Point s, Point e, int a , int b) {
		start = s;
		end = e;
		length = start.distance(end);
		slope = getSlope(start, end);
		angle = a;
		theta = Math.toRadians(angle);
		barblen = b;

		moveTo(start);
		append(new Line2D.Double(start, end), false);
		attachBarb();
	}

	public DrawArrow(Point s, Point e) {
		start = s;
		end = e;
		length = start.distance(end);
		slope = getSlope(start, end);
		angle = 18;
		theta = Math.toRadians(angle);
		barblen = 13;

		moveTo(start);
		append(new Line2D.Double(start, end), false);
		attachBarb();
	}

	public void attachBarb() {
		append(getBarb(Side.LEFT), false);
		append(getBarb(Side.RIGHT), false);
	}

	private Line2D getBarb(Side s) {
		double slant = s == Side.LEFT ? slope + theta : slope - theta;
		double tipx = end.x - barblen * Math.cos(slant);
		double tipy = end.y - barblen * Math.sin(slant);
		return new Line2D.Double(end.x, end.y, tipx, tipy);
	}

	public double getSlope(Point ps, Point pe) {
		double dy = pe.y - ps.y;
		double dx = pe.x - ps.x;
		return (Math.atan2(dy, dx));
	}

	public void moveTo(Point p) {
		moveTo((float) p.x, (float) p.y);
	}

	// このバージョンでは不使用
	public void lineTo(Point p) {
		lineTo((float) p.x, (float) p.y);
	}
}