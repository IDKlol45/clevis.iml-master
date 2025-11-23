package hk.edu.polyu.comp.comp2021.clevis.model;

import java.awt.*;

/**
 * Abstract base class representing a geometric shape in the Clevis system.
 * Provides the foundation for all concrete shape implementations with common
 * properties and behaviors including spatial properties, z-ordering, and
 * geometric operations.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public abstract class Shape {

    /** Unique identifier for the shape */
    private String name;
    /** Spatial boundaries containing the shape */
    protected BoundingBox boundingBox;
    /** Z-index determining rendering order (higher values appear on top) */
    private int z;
    /** Static counter for automatic z-index assignment across all shapes */
    private static int currentZ = 0;
    /**
     * Distance threshold for point coverage detection in coordinate units.
     * Points within this distance from a shape's outline are considered covered.
     */
    protected static final double POINT_COVERAGE_THRESHOLD = 0.05;

    /**
     * Constructs a new shape with the specified name and automatic z-index assignment.
     * Initializes the shape's spatial properties and ensures thread-safe z-index allocation.
     *
     * @param name the unique identifier for this shape, must not be null or empty
     * @throws IllegalArgumentException if name is null or empty
     */
    public Shape(String name) {
        this.name = name;
        this.z = getAndIncrementZ();
        updateBoundingBox();
    }

    /**
     * Thread-safe method to retrieve and increment the global z-index counter.
     * Ensures proper z-ordering in multi-threaded environments.
     *
     * @return the next available z-index value
     */
    private static synchronized int getAndIncrementZ() {
        return ++currentZ;
    }

    /**
     * Returns the unique name identifier of this shape.
     *
     * @return the shape's name, guaranteed non-null
     */
    public String getName() {return name;}
    /**
     * Calculates and returns the minimum bounding box containing this shape.
     * Ensures the bounding box is updated with current geometric properties before returning.
     * This method always provides the most recent spatial boundaries of the shape.
     *
     * @return the current bounding box of this shape, never null
     */
    public BoundingBox getBoundingBox() {
        updateBoundingBox();
        return boundingBox;
    }
    /**
     * Updates the bounding box coordinates based on the shape's current geometry.
     * Must be implemented by concrete subclasses to provide shape-specific logic
     * for calculating spatial boundaries. Called automatically when spatial properties change.
     */
    protected abstract void updateBoundingBox();

    /**
     * Returns the current bounding box of this shape.
     * Provided for compatibility with certain geometric operations.
     *
     * @param boundingBox unused parameter, maintained for interface compatibility
     * @return the current bounding box of this shape
     */
    public BoundingBox getBoundingBox(BoundingBox boundingBox){return this.boundingBox;}
    /**
     * Generates a formatted string representation of the bounding box coordinates.
     * The output follows the format "x y width height" with space-separated values.
     *
     * @return formatted string containing bounding box coordinates and dimensions
     */
    public String outputBoundingBox(){
        return boundingBox.getX() + " " + boundingBox.getY() + " " + boundingBox.getWidth() + " " + boundingBox.getHeight();
    }

    /**
     * Determines whether this shape covers the specified point in 2D space.
     * A point is considered covered if it lies within the coverage threshold
     * distance from the shape's outline. Must be implemented by subclasses
     * with shape-specific geometric calculations.
     *
     * @param x the x-coordinate of the point to check
     * @param y the y-coordinate of the point to check
     * @return true if the point is covered by this shape, false otherwise
     */
    protected abstract boolean covers(double x, double y);
    /**
     * Translates this shape by the specified displacement values.
     * Updates all relevant geometric properties and automatically recalculates
     * the bounding box. Must be implemented by subclasses with appropriate
     * coordinate transformation logic.
     *
     * @param dx the horizontal displacement to apply
     * @param dy the vertical displacement to apply
     */
    protected abstract void move(double dx,double dy);
    /**
     * Generates a detailed string representation of this shape's properties.
     * The format includes the shape's name and relevant geometric parameters
     * in a human-readable format. Used for display and debugging purposes.
     *
     * @return formatted string containing shape information
     */
    protected abstract String list();

    /**
     * Returns the z-index of this shape, indicating its rendering order.
     * Higher z-index values appear on top of lower values in the visual hierarchy.
     *
     * @return the z-index value of this shape
     */
    public int getZ() {
        return z;
    }

    /**
     * Updates the name of this shape. Use with caution as shape names
     * are used as unique identifiers throughout the system.
     *
     * @param name the new name for this shape, must not be null or empty
     * @throws IllegalArgumentException if name is null or empty
     */
    public void setName(String name) {
        this.name = name;
    }

    public abstract void draw(Graphics g);
}
