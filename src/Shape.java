// Shape.java
import java.awt.*;
import java.io.Serializable;

public interface Shape extends Serializable {
    void draw(Graphics2D g2d);
    boolean contains(Point p);
    void move(int dx, int dy);
    Rectangle getBounds();
}