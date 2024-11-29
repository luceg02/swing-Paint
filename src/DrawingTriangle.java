// DrawingTriangle.java
import java.awt.*;

public class DrawingTriangle implements Shape {
    private static final long serialVersionUID = 1L;
    private Point start, end;
    private Color color;

    public DrawingTriangle(Point start, Point end, Color color) {
        this.start = new Point(start);
        this.end = new Point(end);
        this.color = color;
    }

    @Override
    public void draw(Graphics2D g2d) {
        g2d.setColor(color);
        int[] xPoints = {start.x, end.x, start.x};
        int[] yPoints = {start.y, end.y, end.y};
        g2d.drawPolygon(xPoints, yPoints, 3);
    }

    @Override
    public boolean contains(Point p) {
        int[] xPoints = {start.x, end.x, start.x};
        int[] yPoints = {start.y, end.y, end.y};
        Polygon triangle = new Polygon(xPoints, yPoints, 3);
        return triangle.contains(p);
    }

    @Override
    public void move(int dx, int dy) {
        start.translate(dx, dy);
        end.translate(dx, dy);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(
                Math.min(start.x, end.x),
                Math.min(start.y, end.y),
                Math.abs(end.x - start.x),
                Math.abs(end.y - start.y)
        );
    }
}
