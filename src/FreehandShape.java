// FreehandShape.java
import java.awt.*;
import java.util.ArrayList;

public class FreehandShape implements Shape {
    private static final long serialVersionUID = 1L;
    private ArrayList<Point> points;
    private Color color;

    public FreehandShape(ArrayList<Point> points, Color color) {
        this.points = new ArrayList<>();
        for (Point p : points) {
            this.points.add(new Point(p));
        }
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        if (points.size() > 1) {
            Point prev = points.get(0);
            for (int i = 1; i < points.size(); i++) {
                Point curr = points.get(i);
                g2d.drawLine(prev.x, prev.y, curr.x, curr.y);
                prev = curr;
            }
        }
    }

    @Override
    public boolean contains(Point p) {
        for (int i = 1; i < points.size(); i++) {
            Point p1 = points.get(i - 1);
            Point p2 = points.get(i);
            if (distanceToSegment(p, p1, p2) < 5) {
                return true;
            }
        }
        return false;
    }

    private double distanceToSegment(Point p, Point start, Point end) {
        double l2 = Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2);
        if (l2 == 0) return p.distance(start);

        double t = Math.max(0, Math.min(1,
                ((p.x - start.x) * (end.x - start.x) + (p.y - start.y) * (end.y - start.y)) / l2));

        double projX = start.x + t * (end.x - start.x);
        double projY = start.y + t * (end.y - start.y);

        return Point.distance(p.x, p.y, projX, projY);
    }

    @Override
    public void move(int dx, int dy) {
        for (Point p : points) {
            p.translate(dx, dy);
        }
    }

    @Override
    public Rectangle getBounds() {
        if (points.isEmpty()) return new Rectangle();

        int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;

        for (Point p : points) {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y);
            maxX = Math.max(maxX, p.x);
            maxY = Math.max(maxY, p.y);
        }

        return new Rectangle(minX, minY, maxX - minX, maxY - minY);
    }
}