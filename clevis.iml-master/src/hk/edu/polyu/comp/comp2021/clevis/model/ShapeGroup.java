package hk.edu.polyu.comp.comp2021.clevis.model;

import java.util.ArrayList;

/**
 * Represents a composite shape that contains multiple shapes as a single logical entity.
 * Implements the Composite design pattern, allowing groups of shapes to be treated
 * as individual shapes while maintaining hierarchical relationships. Provides
 * recursive operations that propagate to all contained shapes, enabling efficient
 * manipulation of complex graphical assemblies.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class ShapeGroup extends Shape{

    private ArrayList<Shape> shapes;

    /**
     * Constructs a new shape group containing the specified array of shapes.
     * Transfers ownership of the contained shapes to this group and automatically
     * computes the union bounding box that encompasses all member shapes.
     *
     * @param name unique identifier for the group, must not be null or empty
     * @param shapesList array of shapes to be contained in this group, must not be null
     * @throws IllegalArgumentException if shapesList is null or empty
     * @throws NullPointerException if any shape in shapesList is null
     */
    public ShapeGroup(String name,Shape[] shapesList){
        super(name);
        this.shapes = new ArrayList<>();
        for(Shape s: shapesList){
            this.shapes.add(s);
        }
        updateBoundingBox();
    }

    @Override
    protected void updateBoundingBox() {
        if (shapes.isEmpty()) {
            boundingBox = new BoundingBox(0, 0, 0, 0);
            return;
        }
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;

        for (Shape s : shapes) {
            BoundingBox bb = s.getBoundingBox();
            minX = Math.min(minX, bb.getX());
            minY = Math.min(minY, bb.getY());
            maxX = Math.max(maxX, bb.getX() + bb.getWidth());
            maxY = Math.max(maxY, bb.getY() + bb.getHeight());
        }
        boundingBox = new BoundingBox(minX, minY, maxX - minX, maxY - minY);

    }

    /**
     * Returns the collection of shapes contained within this group.
     * Provides direct access to the group's members for inspection and
     * manipulation. The returned list should not be modified directly
     * to maintain group integrity.
     *
     * @return the list of shapes in this group, never null
     */
    public ArrayList<Shape> getShapes() {return this.shapes;}
    public String[] getShapesString() {
        ArrayList<Shape> group = this.getShapes();
        Shape[] res = group.toArray(new Shape[group.size()]);
        String[] strings = new String[res.length];
        for (int i = 0; i < res.length; i++) {
            strings[i] = res[i].getName();
        }
        return strings;
    }
    @Override
    public BoundingBox getBoundingBox() {return boundingBox;}
    @Override
    public void move(double dx, double dy){
        for (Shape s : shapes){
            s.move(dx,dy);
        }
        this.updateBoundingBox();
    }
    @Override
    public boolean covers(double x, double y){
        for (Shape s : shapes){
            if (s.covers(x, y)){
                return true;
            }
        }
        return false;
    }
    @Override
    public String list() {
        return listHelper();
    }

    private String listHelper() {
        StringBuilder res = new StringBuilder(this.getName()); // start with the group name
        for (Shape s : shapes) {
            res.append("\t"); // tab before each child
            if (s instanceof ShapeGroup) {
                // recursively append children
                res.append(((ShapeGroup) s).listHelper());
            } else {
                res.append(s.getName());
            }
        }
        return res.toString();
    }
}
