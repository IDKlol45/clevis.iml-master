package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.util.*;

/**
 * Command Line Vector Graphics Software (Clevis) main controller class.
 * Provides comprehensive functionality for creating, manipulating, and managing
 * vector graphics through an interactive command-line interface. Implements
 * the MVC pattern as the central controller coordinating user input, shape
 * management, and output operations.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class Clevis {
    private HashMap<String,Shape> shapesHashMap;
    private String txtPath;
    private String htmlPath;

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private HashMap<String, Shape> deletedShapes = new HashMap<>();
    /**
     * Default constructor initializes an empty shape registry.
     * Creates a new HashMap for storing shapes by their unique identifiers.
     */
    public Clevis(){
        setShapesHashMap(new HashMap<>());
    }

    /**
     * Parameterized constructor with log file path specification.
     * Initializes the shape registry and configures logging destinations
     * for session command history.
     *
     * @param txtPath filesystem path for plain text command logging
     * @param htmlPath filesystem path for HTML formatted command logging
     */
    public Clevis(String txtPath, String htmlPath){
        this();
        this.setTxtPath(txtPath);
        this.setHtmlPath(htmlPath);
    }

    /**
     * Main execution loop for the Clevis application.
     * Processes user commands interactively until termination, providing
     * real-time feedback and comprehensive error handling. Manages the
     * complete lifecycle of vector graphics creation and manipulation.
     *
     * @throws IOException if log file initialization or writing fails
     */


	public void run() throws IOException {
        System.out.println("Welcome to clevis");
        System.out.println("Please type 'quit' to exit the program");
        ClevisLogger log = new ClevisLogger(getTxtPath(), getHtmlPath());
        while(true){
            Scanner input = new Scanner(System.in);
            System.out.print("Enter command for graphic creation: ");
            String command = input.nextLine();
            if (command.isEmpty()) continue;
            log.log(command);
            String[] c = command.split(" "); // "commands", used "c" for easier reading
            String shape = c[0].toLowerCase();
            switch (shape){
                case "rectangle":
                    if (c.length < 6) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Shape with same name already exists, please try again");
                        break;
                    }
                    Rectangle rec1 = new Rectangle(c[1],Double.parseDouble(c[2]),Double.parseDouble(c[3]),Double.parseDouble(c[4]),Double.parseDouble(c[5]));
                    getShapesHashMap().put(c[1],rec1);
                    System.out.println("Successfully added rectangle");
                    undoStack.push("rectangle " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " " + c[5]);
                    redoStack.clear();
                    break;
                case "line":
                    if (c.length < 6) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Shape with same name already exists, please try again");
                        break;
                    }
                    Line line1 = new Line(c[1],Double.parseDouble(c[2]),Double.parseDouble(c[3]),Double.parseDouble(c[4]),Double.parseDouble(c[5]));
                    getShapesHashMap().put(c[1],line1);
                    System.out.println("Successfully added line");
                    undoStack.push("line " + c[1] + " " + c[2] + " " + c[3] + " " + c[4] + " " + c[5]);
                    redoStack.clear();
                    break;
                case "circle":
                    if (c.length < 5) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Shape with same name already exists, please try again");
                        break;
                    }
                    Circle circle = new Circle(c[1],Double.parseDouble(c[2]),Double.parseDouble(c[3]),Double.parseDouble(c[4]));
                    getShapesHashMap().put(c[1],circle);
                    System.out.println("Successfully added circle");
                    undoStack.push("circle " + c[1] + " " + c[2] + " " + c[3] + " " + c[4]);
                    redoStack.clear();
                    break;
                case "square":
                    if (c.length < 5) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Shape with same name already exists, please try again");
                        break;
                    }
                    Square square = new Square(c[1],Double.parseDouble(c[2]),Double.parseDouble(c[3]),Double.parseDouble(c[4]));
                    getShapesHashMap().put(c[1],square);
                    System.out.println("Successfully added square");
                    undoStack.push("square " + c[1] + " " + c[2] + " " + c[3] + " " + c[4]);
                    redoStack.clear();
                    break;
                case "group":
                    if (c.length < 2) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Shape with same name already exists, please try again");
                        break;
                    }
                    if (c.length < 3) {
                        System.out.println("Please enter shapes to group");
                        break;
                    }
                    Shape[] shapes = new Shape[c.length-2];
                    boolean hasError = false;
                    for (int i = 2; i < c.length; i++) {
                        Shape tempShape = getShapesHashMap().get(c[i]);
                        if(tempShape == null){
                            System.out.println(STR."Error: Shape \{c[i]} not found");
                            hasError = true;
                            break;
                        }
                        for (int j = i + 1; j < c.length; j++) {
                            if (tempShape.getName().equals(c[j])) {
                                System.out.println(STR."Error: Duplicate shape name '\{tempShape.getName()}' in group command");
                                hasError = true;
                                break;
                            }
                        }
                        if (!hasError) {
                            getShapesHashMap().remove(c[i]);
                            shapes[i - 2] = tempShape;
                        }
                    }
                    if (!hasError) {
                        ShapeGroup newGroup = new ShapeGroup(c[1], shapes);
                        getShapesHashMap().put(c[1], newGroup);
                        System.out.println(STR."Successfully created group \{c[1]}");
                        undoStack.push("group " + c[1] + Arrays.toString(newGroup.getShapesString()));
                        redoStack.clear();
                    }
                    break;
                case "ungroup":
                    if (c.length < 2){
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (!getShapesHashMap().containsKey(c[1])){
                        System.out.println("Group not found please try again");
                        break;
                    }
                    ShapeGroup group = (ShapeGroup) getShapesHashMap().get(c[1]);
                    for (Shape childShape : group.getShapes()){
                        getShapesHashMap().put(childShape.getName(),childShape);
                    }
                    undoStack.push("ungroup " + c[1] + Arrays.toString(group.getShapesString()));
                    getShapesHashMap().remove(c[1]);
                    redoStack.clear();
                    break;
                case "delete":
                    if(c.length < 2){
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (!getShapesHashMap().containsKey(c[1])) {
                        System.out.println(STR."Error: Shape \{c[1]} not found");
                        break;
                    }
                    Shape shapeToDelete = getShapesHashMap().get(c[1]);
                    deletedShapes.put(c[1], shapeToDelete);

                    getShapesHashMap().remove(c[1]);

                    undoStack.push("delete " + c[1]);
                    redoStack.clear();
                    System.out.println("Successfully removed shape");
                    break;
                case "boundingbox":
                    if (c.length < 2) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (!getShapesHashMap().containsKey(c[1])) {
                        System.out.println(STR."Error: Shape \{c[1]} not found");
                        break;
                    }
                    getShapesHashMap().get(c[1]).outputBoundingBox();
                    break;
                case "move":
                    if  (c.length < 4) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (!getShapesHashMap().containsKey(c[1])) {
                        System.out.println(STR."Error: Shape with name \{c[1]} not found");
                        break;
                    }
                    Shape moveShape =  getShapesHashMap().get(c[1]);
                    double moveX  = Double.parseDouble(c[2]);
                    double moveY  = Double.parseDouble(c[3]);
                    moveShape.move(moveX,moveY);
                    undoStack.push("move " + c[1] + " " + c[2] + " " + c[3]);
                    redoStack.clear();
                    break;
                case "shapeAt":
                    if (c.length < 3) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    double x = Double.parseDouble(c[1]);
                    double y = Double.parseDouble(c[2]);
                    Shape res = null;
                    for (Shape s : getShapesHashMap().values()){
                        if (s.covers(x,y)){
                            if (res == null || s.getZ() > res.getZ()){
                                res = s;
                            }
                        }
                    }
                    if (res != null) System.out.println(res.getName());
                    else System.out.println(STR."Error: No shape covering \{x} \{y} found");
                    break;
                case "intersect":
                    if (c.length < 3) {
                        System.out.println("Invalid number of inputs, please try again");
                    }
                    String n1 = c[1], n2 = c[2];
                    Shape shape1 = getShapesHashMap().get(n1);
                    Shape shape2 = getShapesHashMap().get(n2);
                    if (shape1 == null || shape2 == null) {
                        System.out.println(STR."Error: Shape \{n1} or \{n2} not found");
                        break;
                    }
                    boolean intersects = shape1.getBoundingBox().overlaps(shape2.getBoundingBox());
                    if (!intersects) {
                        System.out.println(STR."The shape \{c[1]}and \{c[2]} does not intersect");
                    }
                    else {
                        System.out.println(STR."The shape \{c[1]}and \{c[2]} does intersect");
                    }
                    break;
                case "list":
                    if (c.length < 2) {
                        System.out.println("Invalid number of inputs, please try again");
                        break;
                    }
                    if (!getShapesHashMap().containsKey(c[1])) {
                        System.out.println("Error: Shape "+c[1]+" not found");
                        break;
                    }
                    System.out.println(getShapesHashMap().get(c[1]).list());
                    break;
                case "listAll":
                    if (getShapesHashMap().isEmpty()) {
                        System.out.println("No shapes to display, please create shapes");
                        break;
                    }

                    List<Shape> allShapes = new ArrayList<>(getShapesHashMap().values());
                    allShapes.sort((a, b) -> b.getZ() - a.getZ());

                    for (Shape s : allShapes) {
                        if (s instanceof ShapeGroup) {
                            System.out.println(s.getName() + " (Group):");
                            ShapeGroup shapeGroup = (ShapeGroup) s;
                            for (Shape child : shapeGroup.getShapes()) {
                                System.out.println("  " + child.list());
                            }
                        } else {
                            System.out.println(s.list());
                        }
                    }
                    break;
                case "undo":
                    if (undoStack.isEmpty()) {
                        System.out.println("No action to undo!");
                        break;
                    }
                    String lastAction = undoStack.pop();
                    redoStack.push(lastAction);

                    String[] action = lastAction.split(" ");
                    switch(action[0]){
                        case "rectangle":
                        case "line":
                        case "circle":
                        case "square":
                            shapesHashMap.remove(action[1]);
                            System.out.println("Undo successful");
                            break;

                        case "move":
                            Shape shapeMoveUndo = shapesHashMap.get(action[1]);
                            if (shapeMoveUndo != null) {
                                shapeMoveUndo.move(-Double.parseDouble(action[2]), -Double.parseDouble(action[3]));
                                System.out.println("Undo successful");
                            }
                            break;

                        case "delete":
                            Shape restored = deletedShapes.get(action[1]);
                            if (restored != null) {
                                shapesHashMap.put(action[1], restored);
                                deletedShapes.remove(action[1]);
                                System.out.println("Undo successful");
                            }
                            break;

                        case "group":
                            Shape groupUndo = shapesHashMap.get(action[1]);
                            if (groupUndo instanceof ShapeGroup) {
                                for (Shape child : ((ShapeGroup) groupUndo).getShapes()) {
                                    shapesHashMap.put(child.getName(), child);
                                }
                                shapesHashMap.remove(action[1]);
                                System.out.println("Undo successful");
                            }
                            break;

                        case "ungroup":
                            String[] names =  Arrays.copyOfRange(action,2,action.length);
                            String groupName = action[1];
                            Shape[] shapesListUngroup = new Shape[names.length];
                            for (int i = 0; i < names.length; i++) {
                                shapesListUngroup[i] =  shapesHashMap.get(names[i]);
                                shapesHashMap.remove(names[i]);
                            }
                            ShapeGroup groupUngroup = new ShapeGroup(groupName,shapesListUngroup);
                            shapesHashMap.put(action[1], groupUngroup);
                            System.out.println("Undo successful");
                            break;
                    }
                    break;

                case "redo":
                    if (redoStack.isEmpty()) {
                        System.out.println("No action to redo!");
                        break;
                    }

                    String redoAction = redoStack.pop();
                    undoStack.push(redoAction);

                    System.out.println("Redo successful");

                    String[] redoParts = redoAction.split(" ");
                    switch(redoParts[0]){
                        case "rectangle":
                            shapesHashMap.put(redoParts[1], new Rectangle(redoParts[1], Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]), Double.parseDouble(redoParts[4]), Double.parseDouble(redoParts[5])));
                            break;
                        case "line":
                            shapesHashMap.put(redoParts[1], new Line(redoParts[1], Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]), Double.parseDouble(redoParts[4]), Double.parseDouble(redoParts[5])));
                            break;
                        case "circle":
                            shapesHashMap.put(redoParts[1], new Circle(redoParts[1], Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]), Double.parseDouble(redoParts[4])));
                            break;
                        case "square":
                            shapesHashMap.put(redoParts[1], new Square(redoParts[1], Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]), Double.parseDouble(redoParts[4])));
                            break;
                        case "move":
                            Shape s = shapesHashMap.get(redoParts[1]);
                            if (s!=null) s.move(Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]));
                            break;
                        case "delete":
                            deletedShapes.put(redoParts[1], shapesHashMap.get(redoParts[1]));
                            shapesHashMap.remove(redoParts[1]);
                            break;
                        case "group":
                            String groupRedoName = redoParts[1];
                            String[] redoGroupNames = new  String[redoParts.length-1];
                            Shape[] redoGroupShapes = new Shape[redoGroupNames.length];
                            for (int i = 0; i < redoGroupNames.length; i++) {
                                redoGroupShapes[i] = shapesHashMap.get(redoGroupNames[i]);
                                shapesHashMap.remove(redoGroupNames[i]);
                            }
                            ShapeGroup redoGroup = new ShapeGroup(groupRedoName,redoGroupShapes);
                            shapesHashMap.put(groupRedoName, redoGroup);
                            break;
                        case "ungroup":
                            ShapeGroup ungroupShapeGroup = (ShapeGroup) shapesHashMap.get(redoParts[1]);
                            for (Shape ungroupChild : ungroupShapeGroup.getShapes()) {
                                shapesHashMap.put(ungroupChild.getName(), ungroupChild);
                            }
                            shapesHashMap.remove(redoParts[1]);
                            break;
                    }
                    break;
                case "quit":
                    System.out.println("Thank you for using CLEVIS");
                    log.close();
                    System.exit(0);
                default:
                    System.out.println("Invalid shape");
                    break;
            }
        }
    }

    /**
     * Returns the registry of all shapes managed by this Clevis instance.
     *
     * @return HashMap containing all shapes with their names as keys
     */
    public HashMap<String, Shape> getShapesHashMap() {
        return shapesHashMap;
    }

    /**
     * Sets the shape registry for this Clevis instance.
     *
     * @param shapesHashMap the new HashMap to use for shape storage
     */
    public void setShapesHashMap(HashMap<String, Shape> shapesHashMap) {
        this.shapesHashMap = shapesHashMap;
    }

    /**
     * Returns the file path for HTML format command logging.
     *
     * @return the HTML log file path
     */
    public String getHtmlPath() {
        return htmlPath;
    }

    /**
     * Sets the file path for HTML format command logging.
     *
     * @param htmlPath the new HTML log file path
     */
    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    /**
     * Returns the file path for plain text format command logging.
     *
     * @return the text log file path
     */
    public String getTxtPath() {
        return txtPath;
    }

    /**
     * Sets the file path for plain text format command logging.
     *
     * @param txtPath the new text log file path
     */
    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }
}
