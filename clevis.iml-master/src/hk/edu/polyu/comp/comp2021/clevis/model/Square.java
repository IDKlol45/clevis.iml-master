package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Represents a square shape defined by top-left corner coordinates and side length.
 * Extends the rectangle concept with the constraint of equal width and height,
 * providing specialized geometric operations for square-specific functionality.
 * Implements efficient point coverage detection and spatial transformations
 * while maintaining the invariant of equal side lengths.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class Square extends Shape{
    private double side,x,y;

    /**
     * Constructs a new square with specified position and side length.
     * Initializes the square's geometric properties and automatically computes
     * the bounding box. The square maintains equal width and height throughout
     * its lifecycle.
     *
     * @param name unique identifier for the square, must not be null or empty
     * @param x the x-coordinate of the top-left corner in 2D space
     * @param y the y-coordinate of the top-left corner in 2D space
     * @param side the length of all sides of the square, must be positive
     * @throws IllegalArgumentException if side length is non-positive
     */
    public Square(String name, double x, double y, double side) {
        super(name);
        this.x = x;
        this.y = y;
        this.side = side;
        updateBoundingBox();
    }

    @Override
    public void updateBoundingBox(){
        boundingBox = new BoundingBox(x,y,side,side);
    }

    @Override
    public boolean covers(double x, double y){
        BoundingBox bb = this.boundingBox;
        double left = bb.getX();
        double right = left + bb.getWidth();
        double top = bb.getY();
        double bottom = top + bb.getHeight();

        double dx = Math.min(Math.abs(x - left),Math.abs(x-right)); // check if it is within the x range of the shape
        double dy = Math.min(Math.abs(y-top),Math.abs(y-bottom)); // check if it is within the y range of the shape

        boolean nearHorizontal = left >= dx && dx >= right && (Math.abs(y-top) < POINT_COVERAGE_THRESHOLD || Math.abs(y-bottom) < POINT_COVERAGE_THRESHOLD);
        boolean nearVertical = top >= dy && dy >= bottom && (Math.abs(x-left) < POINT_COVERAGE_THRESHOLD || Math.abs(x-right) < POINT_COVERAGE_THRESHOLD);

        return nearHorizontal || nearVertical;
    }

    @Override
    public void move(double dx, double dy){
        this.x += dx;
        this.y += dy;
        updateBoundingBox();
    }
    @Override
    public String list(){
        return String.format("Name = %s\tTop-Left Corner = (%f,%f)\tSide Length = %f", getName(),x,y,side);
    }
}
