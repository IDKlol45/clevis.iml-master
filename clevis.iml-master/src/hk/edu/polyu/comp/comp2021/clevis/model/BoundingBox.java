package hk.edu.polyu.comp.comp2021.clevis.model;

/**
 * Represents an axis-aligned rectangular boundary in 2D space.
 * Provides geometric operations for spatial relationships, collision detection,
 * and containment checks. Used throughout the Clevis system for rendering
 * optimization, intersection testing, and spatial indexing of shapes.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class BoundingBox {
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * Constructs a new bounding box with specified position and dimensions.
     * Creates an axis-aligned rectangle that can be used for spatial computations
     * and collision detection between geometric shapes.
     *
     * @param x the x-coordinate of the top-left corner in 2D space
     * @param y the y-coordinate of the top-left corner in 2D space
     * @param width the horizontal extent of the bounding box, must be non-negative
     * @param height the vertical extent of the bounding box, must be non-negative
     * @throws IllegalArgumentException if width or height are negative
     */
    public BoundingBox(double x, double y, double width, double height){
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the x-coordinate of the top-left corner of this bounding box.
     *
     * @return the x-coordinate of the bounding box origin
     */
    public double getX() { return x; }
    /**
     * Returns the y-coordinate of the top-left corner of this bounding box.
     *
     * @return the y-coordinate of the bounding box origin
     */
    public double getY() { return y; }
    /**
     * Returns the horizontal extent of this bounding box.
     *
     * @return the width of the bounding box
     */
    public double getWidth() { return width; }
    /**
     * Returns the vertical extent of this bounding box.
     *
     * @return the height of the bounding box
     */
    public double getHeight() { return height; }
    /**
     * Determines if this bounding box overlaps with another bounding box.
     * Uses the separating axis theorem for efficient overlap detection.
     * Two bounding boxes overlap if they share any internal points or
     * if their edges touch. This method provides a fast approximation
     * for shape intersection testing.
     *
     * @param other the bounding box to check for overlap with, must not be null
     * @return true if the bounding boxes overlap or touch, false if they are completely separate
     * @throws NullPointerException if the other bounding box is null
     */
    public boolean overlaps(BoundingBox other){
        double left = x;
        double right = x + width;
        double top = y;
        double bottom = y + height;

        double otherLeft = other.getX();
        double otherRight = other.getX() + other.getWidth();
        double otherTop = other.getY();
        double otherBottom = other.getY() + other.getHeight();

        return !(right < otherLeft || left > otherRight ||
                bottom < otherTop || top > otherBottom);
    }
}
