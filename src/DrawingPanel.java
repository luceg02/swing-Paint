// DrawingPanel.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class DrawingPanel extends JPanel {
    private ArrayList<Shape> shapes;
    private Shape currentDrawing;
    private Point startPoint;
    private boolean editMode;
    private ArrayList<Point> freehandPoints;
    private Shape selectedShape;
    private Point lastPoint;
    private String editAction;
    private String currentShape;
    private Color currentColor;

    public DrawingPanel() {
        shapes = new ArrayList<>();
        freehandPoints = new ArrayList<>();
        currentShape = "Freehand";
        currentColor = Color.BLACK;
        editAction = "Déplacer";
        setBackground(Color.WHITE);
        setupMouseListeners();
    }

    private void setupMouseListeners() {
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startPoint = e.getPoint();
                lastPoint = e.getPoint();

                if (editMode) {
                    handleEditModePress(e.getPoint());
                } else if (currentShape.equals("Freehand")) {
                    freehandPoints.clear();
                    freehandPoints.add(new Point(startPoint));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (editMode) {
                    handleEditModeRelease();
                } else {
                    finishDrawing(e.getPoint());
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (editMode) {
                    handleEditModeDrag(e.getPoint());
                } else {
                    handleDrawingDrag(e.getPoint());
                }
                lastPoint = e.getPoint();
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                updateCursor(e.getPoint());
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(mouseAdapter);
    }

    private void handleEditModePress(Point point) {
        selectedShape = null;
        for (int i = shapes.size() - 1; i >= 0; i--) {
            Shape shape = shapes.get(i);
            if (shape.contains(point)) {
                selectedShape = shape;
                if (editAction.equals("Supprimer")) {
                    shapes.remove(i);
                    repaint();
                }
                break;
            }
        }
    }

    private void handleEditModeRelease() {
        if (editAction.equals("Supprimer")) {
            selectedShape = null;
        }
    }

    private void handleEditModeDrag(Point point) {
        if (selectedShape != null && editAction.equals("Déplacer")) {
            int dx = point.x - lastPoint.x;
            int dy = point.y - lastPoint.y;
            selectedShape.move(dx, dy);
        }
    }

    private void handleDrawingDrag(Point point) {
        if (currentShape.equals("Freehand")) {
            freehandPoints.add(new Point(point));
        } else if (currentShape.equals("Eraser")) {
            handleEraser(point);
        } else {
            updateCurrentShape(point);
        }
    }

    private void handleEraser(Point point) {
        final int ERASER_SIZE = 10;
        Rectangle eraserArea = new Rectangle(
                point.x - ERASER_SIZE/2,
                point.y - ERASER_SIZE/2,
                ERASER_SIZE,
                ERASER_SIZE
        );
        shapes.removeIf(shape -> shape.getBounds().intersects(eraserArea));
    }

    private void updateCurrentShape(Point currentPoint) {
        int x = Math.min(startPoint.x, currentPoint.x);
        int y = Math.min(startPoint.y, currentPoint.y);
        int width = Math.abs(currentPoint.x - startPoint.x);
        int height = Math.abs(currentPoint.y - startPoint.y);

        switch(currentShape) {
            case "Rectangle":
                currentDrawing = new DrawingRectangle(x, y, width, height, currentColor);
                break;
            case "Circle":
                currentDrawing = new DrawingCircle(x, y, width, height, currentColor);
                break;
            case "Triangle":
                currentDrawing = new DrawingTriangle(startPoint, currentPoint, currentColor);
                break;
        }
    }

    private void finishDrawing(Point endPoint) {
        if (!currentShape.equals("Eraser")) {
            if (currentShape.equals("Freehand")) {
                if (freehandPoints.size() > 1) {
                    shapes.add(new FreehandShape(new ArrayList<>(freehandPoints), currentColor));
                }
            } else if (currentDrawing != null) {
                shapes.add(currentDrawing);
            }
        }
        currentDrawing = null;
        freehandPoints.clear();
    }

    private void updateCursor(Point point) {
        if (point == null) return;

        if (!editMode) {
            setCursor(Cursor.getDefaultCursor());
            return;
        }

        switch (editAction) {
            case "Supprimer":
                setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
                break;
            case "Déplacer":
                setCursor(new Cursor(Cursor.MOVE_CURSOR));
                break;
            default:
                setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        for (Shape shape : shapes) {
            shape.draw(g2d);
        }

        if (currentDrawing != null) {
            currentDrawing.draw(g2d);
        }

        if (currentShape.equals("Freehand") && freehandPoints.size() > 1) {
            g2d.setColor(currentColor);
            Point prev = freehandPoints.get(0);
            for (int i = 1; i < freehandPoints.size(); i++) {
                Point curr = freehandPoints.get(i);
                g2d.drawLine(prev.x, prev.y, curr.x, curr.y);
                prev = curr;
            }
        }

        if (editMode && selectedShape != null) {
            Rectangle bounds = selectedShape.getBounds();
            g2d.setColor(Color.GRAY);
            g2d.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0));
            g2d.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    }

    public void setCurrentShape(String shape) {
        this.currentShape = shape;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        if (!editMode) {
            selectedShape = null;
        }
        updateCursor(getMousePosition());
    }

    public void setEditAction(String action) {
        this.editAction = action;
        selectedShape = null;
        updateCursor(getMousePosition());
    }

    public ArrayList<Shape> getShapes() {
        return shapes;
    }

    public void setShapes(ArrayList<Shape> shapes) {
        this.shapes = shapes;
        repaint();
    }
}
