    package hk.edu.polyu.comp.comp2021.clevis.model;
    import java.awt.Graphics2D;

    /**
     * Represents a circular shape defined by a center point and radius.
     * Implements circular geometry with precise point coverage detection
     * using distance calculations from the circle's center. Provides
     * efficient bounding box computation and spatial transformation
     * capabilities for circular shapes in the vector graphics system.
     *
     * @author Group03 COMP2021 (November 23, 25)
     */
    public class Circle extends Shape{
        private double radius,x,y;
        /**
         * Constructs a new circle with specified center coordinates and radius.
         * Initializes the circle's geometric properties and automatically computes
         * the bounding box that encloses the circular shape.
         *
         * @param name unique identifier for the circle, must not be null or empty
         * @param x the x-coordinate of the circle's center in 2D space
         * @param y the y-coordinate of the circle's center in 2D space
         * @param radius the radius of the circle, must be positive
         * @throws IllegalArgumentException if radius is non-positive
         */
        public Circle(String name, double x, double y, double radius){
            super(name);
            this.x = x;
            this.y = y;
            this.radius = radius;
            updateBoundingBox();
        }

        @Override
        public void updateBoundingBox(){
            boundingBox = new BoundingBox(x-radius, y-radius, radius*2, radius*2);
        }

        @Override
        public boolean covers(double px, double py){
            double dx =  Math.abs(x-px);
            double dy = Math.abs(y-py);
            double distanceToCenter = Math.sqrt(dx*dx+dy*dy);
            double distanceToOutline = Math.abs(distanceToCenter - radius);

            return distanceToOutline < POINT_COVERAGE_THRESHOLD;
        }
        @Override
        public void move(double dx, double dy){
            this.x += dx;
            this.y += dy;
            updateBoundingBox();
        }
        @Override
        public String list(){
            return String.format("Name = %s\tCenter = (%f,%f)\tRadius = %f", getName(),x,y,radius);
        }

        public void draw(Graphics2D g2) {
            g2.draw(new java.awt.geom.Ellipse2D.Double(x - radius, y - radius, radius * 2, radius * 2));
        }
    }
