package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Represents a linear segment between two points in 2D space.
 * Implements line segment geometry including precise point distance calculations
 * using vector projection mathematics and endpoint-based bounding box computation.
 * Provides efficient geometric operations for linear elements in vector graphics.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class Line extends Shape{
    private double x1,x2,y1,y2;

    /**
     * Constructs a new line segment between two specified endpoints.
     * Initializes the line's geometric properties and automatically computes
     * the bounding box that encloses both endpoints.
     *
     * @param name unique identifier for the line, must not be null or empty
     * @param x1 the x-coordinate of the starting point in 2D space
     * @param y1 the y-coordinate of the starting point in 2D space
     * @param x2 the x-coordinate of the ending point in 2D space
     * @param y2 the y-coordinate of the ending point in 2D space
     */
    public Line(String name, double x1, double y1, double x2, double y2) {
        super(name);
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        updateBoundingBox();
    }

    @Override
    public void  updateBoundingBox() {
        double minX = Math.min(x1, x2);
        double minY = Math.min(y1, y2);
        boundingBox = new BoundingBox(
                minX,
                minY,
                Math.abs(x1 - x2),
                Math.abs(y1 - y2)
        );
    }

    @Override
    public boolean covers(double x, double y){
        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0){
            return Math.hypot(x-x1, y-y1) < POINT_COVERAGE_THRESHOLD;
        }

        // formula for shortest distance from a point to a line segment

        double t = ((x - x1) * dx + (y - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;
        return Math.hypot(x - closestX, y - closestY) < POINT_COVERAGE_THRESHOLD;

    }
    @Override
    public void move (double dx, double dy){
        this.x1 += dx;
        this.y1 += dy;
        this.x2 += dx;
        this.y2 += dy;
        updateBoundingBox();
    }
    @Override
    public String list(){
        return String.format("Name = %s\tStarting Point = (%f,%f)\tEnd Point = (%f,%f)", getName(),x1,y1,x2,y2);
    }
}
