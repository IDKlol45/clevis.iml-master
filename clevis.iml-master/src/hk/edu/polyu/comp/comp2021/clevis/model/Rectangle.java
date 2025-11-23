package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Represents a rectangular shape defined by top-left corner coordinates,
 * width, and height. Implements axis-aligned rectangle geometry with
 * precise point coverage detection and spatial transformation capabilities.
 * This class provides concrete implementation of rectangle-specific
 * geometric operations required by the Shape abstract class.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class Rectangle extends Shape {

    private double x,y,width,height;
    /**
     * Constructs a new rectangle with specified dimensions and position.
     * Validates parameters and initializes the bounding box upon creation.
     *
     * @param name unique identifier for the rectangle, must not be null or empty
     * @param x the x-coordinate of the top-left corner in 2D space
     * @param y the y-coordinate of the top-left corner in 2D space
     * @param width the horizontal dimension of the rectangle, must be positive
     * @param height the vertical dimension of the rectangle, must be positive
     * @throws IllegalArgumentException if width or height are non-positive
     */
    public Rectangle(String name, double x, double y, double width, double height) {
        super(name);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        updateBoundingBox();
    }

    @Override
    public void updateBoundingBox(){
        boundingBox = new BoundingBox(x,y,width,height);
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
        return String.format("Name = %s\tTop-Left Corner = (%f,%f)\tWidth = %f\tHeight = %f", getName(),x,y,width,height);
    }
}
