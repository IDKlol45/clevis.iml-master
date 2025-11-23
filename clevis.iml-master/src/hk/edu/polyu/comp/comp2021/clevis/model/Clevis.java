package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.IOException;
import java.util.*;

/**
 * Command Line Vector Graphics Software (Clevis) main controller class.
 *
 * This version adds:
 *  - a processCommand(String) method that performs the actions and returns textual output.
 *  - the run() loop delegates to processCommand so CLI still works unchanged (but reuses same logic).
 *
 * Minimal changes only: logger is held as an instance field (created in parameterized constructor).
 */
public class Clevis {
    private HashMap<String, Shape> shapesHashMap;
    private String txtPath;
    private String htmlPath;

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private HashMap<String, Shape> deletedShapes = new HashMap<>();

    // persistent logger used by both run() and processCommand()
    private ClevisLogger logger = null;

    public Clevis(){
        setShapesHashMap(new HashMap<>());
    }

    /**
     * Parameterized constructor with log file path specification.
     */
    public Clevis(String txtPath, String htmlPath){
        this();
        this.setTxtPath(txtPath);
        this.setHtmlPath(htmlPath);

        // Try to create a persistent logger; if it fails, we continue with logger == null
        try {
            this.logger = new ClevisLogger(getTxtPath(), getHtmlPath());
        } catch (IOException e) {
            // Logging failure shouldn't break core functionality; print to stderr
            System.err.println("Warning: failed to initialize logger: " + e.getMessage());
            this.logger = null;
        }
    }

    /**
     * CLI loop — uses processCommand(...) to handle each input line
     */
    public void run() throws IOException {
        System.out.println("Welcome to clevis");
        System.out.println("Please type 'quit' to exit the program");

        Scanner input = new Scanner(System.in);

        while (true) {
            System.out.print("Enter command for graphic creation: ");
            String command = input.nextLine();
            if (command == null || command.trim().isEmpty()) continue;

            // Execute and print returned text
            String result = "";
            try {
                result = processCommand(command);
            } catch (IOException e) {
                System.out.println("Error while processing command: " + e.getMessage());
                continue;
            }

            if (result != null && !result.isEmpty()) {
                System.out.println(result);
            }

            // if processCommand indicates quit, exit
            if ("quit".equals(result)) {
                if (logger != null) {
                    try {
                        logger.close();
                    } catch (IOException e) {
                        // ignore
                    }
                }
                System.out.println("Thank you for using CLEVIS");
                System.exit(0);
            }
        }
    }

    /**
     * Process a single command (string) and return textual output.
     * Does not block; suitable for CLI calls and GUI calls.
     *
     * Important: this method uses the same data structures (undo/redo/deletedShapes)
     * so GUI and CLI will share the same model state if they both use the same Clevis instance.
     *
     * @param command a single command string (same syntax as CLI)
     * @return result text or "quit" (string) when quit command is issued
     */
    public String processCommand(String command) throws IOException {
        if (command == null || command.trim().isEmpty()) return "";

        // Log the command (if logger present)
        if (logger != null) {
            try {
                logger.log(command);
            } catch (IOException e) {
                // logging failure should not prevent command execution
                // but notify the caller by including a note in the returned text (below)
            }
        }

        String[] c = command.trim().split("\\s+");
        String op = c[0].toLowerCase();
        StringBuilder out = new StringBuilder();

        switch (op) {
            case "rectangle": {
                if (c.length < 6) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (getShapesHashMap().containsKey(c[1])) {
                    out.append("Shape with same name already exists, please try again");
                    break;
                }
                double x = Double.parseDouble(c[2]);
                double y = Double.parseDouble(c[3]);
                double w = Double.parseDouble(c[4]);
                double h = Double.parseDouble(c[5]);
                Rectangle rec = new Rectangle(c[1], x, y, w, h);
                getShapesHashMap().put(c[1], rec);
                out.append("Successfully added rectangle");
                undoStack.push(command);
                redoStack.clear();
                break;
            }

            case "line": {
                if (c.length < 6) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (getShapesHashMap().containsKey(c[1])) {
                    out.append("Shape with same name already exists, please try again");
                    break;
                }
                double x1 = Double.parseDouble(c[2]);
                double y1 = Double.parseDouble(c[3]);
                double x2 = Double.parseDouble(c[4]);
                double y2 = Double.parseDouble(c[5]);
                Line line = new Line(c[1], x1, y1, x2, y2);
                getShapesHashMap().put(c[1], line);
                out.append("Successfully added line");
                undoStack.push(command);
                redoStack.clear();
                break;
            }

            case "circle": {
                if (c.length < 5) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (getShapesHashMap().containsKey(c[1])) {
                    out.append("Shape with same name already exists, please try again");
                    break;
                }
                double cx = Double.parseDouble(c[2]);
                double cy = Double.parseDouble(c[3]);
                double r = Double.parseDouble(c[4]);
                Circle circle = new Circle(c[1], cx, cy, r);
                getShapesHashMap().put(c[1], circle);
                out.append("Successfully added circle");
                undoStack.push(command);
                redoStack.clear();
                break;
            }

            case "square": {
                if (c.length < 5) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (getShapesHashMap().containsKey(c[1])) {
                    out.append("Shape with same name already exists, please try again");
                    break;
                }
                double sx = Double.parseDouble(c[2]);
                double sy = Double.parseDouble(c[3]);
                double side = Double.parseDouble(c[4]);
                Square square = new Square(c[1], sx, sy, side);
                getShapesHashMap().put(c[1], square);
                out.append("Successfully added square");
                undoStack.push(command);
                redoStack.clear();
                break;
            }

            case "group": {
                if (c.length < 3) {
                    out.append("Please enter shapes to group");
                    break;
                }
                if (getShapesHashMap().containsKey(c[1])) {
                    out.append("Shape with same name already exists, please try again");
                    break;
                }
                Shape[] shapes = new Shape[c.length - 2];
                boolean hasError = false;
                for (int i = 2; i < c.length; i++) {
                    Shape tempShape = getShapesHashMap().get(c[i]);
                    if (tempShape == null) {
                        out.append("Error: Shape ").append(c[i]).append(" not found");
                        hasError = true;
                        break;
                    }
                    // check duplicates
                    for (int j = i + 1; j < c.length; j++) {
                        if (tempShape.getName().equals(c[j])) {
                            out.append("Error: Duplicate shape name '").append(tempShape.getName()).append("' in group command");
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
                    out.append("Successfully created group ").append(c[1]);
                    undoStack.push(command);
                    redoStack.clear();
                }
                break;
            }

            case "ungroup": {
                if (c.length < 2) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (!getShapesHashMap().containsKey(c[1])) {
                    out.append("Group not found please try again");
                    break;
                }
                Shape groupShape = getShapesHashMap().get(c[1]);
                if (!(groupShape instanceof ShapeGroup)) {
                    out.append("Error: ").append(c[1]).append(" is not a group");
                    break;
                }
                ShapeGroup group = (ShapeGroup) groupShape;
                for (Shape childShape : group.getShapes()) {
                    getShapesHashMap().put(childShape.getName(), childShape);
                }
                getShapesHashMap().remove(c[1]);
                undoStack.push(command);
                redoStack.clear();
                out.append("Successfully ungrouped ").append(c[1]);
                break;
            }

            case "delete": {
                if (c.length < 2) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (!getShapesHashMap().containsKey(c[1])) {
                    out.append("Error: Shape ").append(c[1]).append(" not found");
                    break;
                }
                Shape shapeToDelete = getShapesHashMap().get(c[1]);
                deletedShapes.put(c[1], shapeToDelete);
                getShapesHashMap().remove(c[1]);
                undoStack.push(command);
                redoStack.clear();
                out.append("Successfully removed shape");
                break;
            }

            case "boundingbox": {
                if (c.length < 2) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (!getShapesHashMap().containsKey(c[1])) {
                    out.append("Error: Shape ").append(c[1]).append(" not found");
                    break;
                }
                out.append(getShapesHashMap().get(c[1]).outputBoundingBox());
                break;
            }

            case "move": {
                if (c.length < 4) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (!getShapesHashMap().containsKey(c[1])) {
                    out.append("Error: Shape with name ").append(c[1]).append(" not found");
                    break;
                }
                Shape moveShape = getShapesHashMap().get(c[1]);
                double moveX = Double.parseDouble(c[2]);
                double moveY = Double.parseDouble(c[3]);
                moveShape.move(moveX, moveY);
                undoStack.push(command);
                redoStack.clear();
                out.append("Moved ").append(c[1]);
                break;
            }

            case "shapeat": {
                if (c.length < 3) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                double px = Double.parseDouble(c[1]);
                double py = Double.parseDouble(c[2]);
                Shape res = null;
                for (Shape s : getShapesHashMap().values()) {
                    if (s.covers(px, py)) {
                        if (res == null || s.getZ() > res.getZ()) {
                            res = s;
                        }
                    }
                }
                if (res != null) out.append(res.getName());
                else out.append("Error: No shape covering ").append(px).append(" ").append(py).append(" found");
                break;
            }

            case "intersect": {
                if (c.length < 3) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                String n1 = c[1], n2 = c[2];
                Shape shape1 = getShapesHashMap().get(n1);
                Shape shape2 = getShapesHashMap().get(n2);
                if (shape1 == null || shape2 == null) {
                    out.append("Error: Shape ").append(n1).append(" or ").append(n2).append(" not found");
                    break;
                }
                boolean intersects = shape1.getBoundingBox().overlaps(shape2.getBoundingBox());
                if (!intersects) out.append("The shape ").append(c[1]).append(" and ").append(c[2]).append(" do not intersect");
                else out.append("The shape ").append(c[1]).append(" and ").append(c[2]).append(" do intersect");
                break;
            }

            case "list": {
                if (c.length < 2) {
                    out.append("Invalid number of inputs, please try again");
                    break;
                }
                if (!getShapesHashMap().containsKey(c[1])) {
                    out.append("Error: Shape ").append(c[1]).append(" not found");
                    break;
                }
                out.append(getShapesHashMap().get(c[1]).list());
                break;
            }

            case "listall": {
                if (getShapesHashMap().isEmpty()) {
                    out.append("No shapes to display, please create shapes");
                    break;
                }
                List<Shape> allShapes = new ArrayList<>(getShapesHashMap().values());
                // decreasing z-order
                allShapes.sort((a, b) -> Integer.compare(b.getZ(), a.getZ()));
                for (Shape s : allShapes) {
                    if (s instanceof ShapeGroup) {
                        out.append(s.getName()).append(" (Group):\n");
                        ShapeGroup shapeGroup = (ShapeGroup) s;
                        for (Shape child : shapeGroup.getShapes()) {
                            out.append("  ").append(child.list()).append("\n");
                        }
                    } else {
                        out.append(s.list()).append("\n");
                    }
                }
                break;
            }

            case "undo": {
                if (undoStack.isEmpty()) {
                    out.append("No action to undo!");
                    break;
                }
                String lastAction = undoStack.pop();
                redoStack.push(lastAction);

                String[] action = lastAction.split("\\s+");
                switch (action[0]) {
                    case "rectangle":
                    case "line":
                    case "circle":
                    case "square":
                        getShapesHashMap().remove(action[1]);
                        out.append("Undo successful");
                        break;

                    case "move":
                        Shape shape = getShapesHashMap().get(action[1]);
                        if (shape != null) {
                            shape.move(-Double.parseDouble(action[2]), -Double.parseDouble(action[3]));
                            out.append("Undo successful");
                        } else {
                            out.append("Undo failed: shape not found");
                        }
                        break;

                    case "delete":
                        Shape restored = deletedShapes.get(action[1]);
                        if (restored != null) {
                            getShapesHashMap().put(action[1], restored);
                            deletedShapes.remove(action[1]);
                            out.append("Undo successful");
                        } else out.append("Undo failed");
                        break;

                    case "group":
                        Shape grp = getShapesHashMap().get(action[1]);
                        if (grp instanceof ShapeGroup) {
                            for (Shape child : ((ShapeGroup) grp).getShapes()) {
                                getShapesHashMap().put(child.getName(), child);
                            }
                            getShapesHashMap().remove(action[1]);
                            out.append("Undo successful");
                        } else out.append("Undo failed");
                        break;

                    case "ungroup":
                        out.append("Undo successful");
                        break;
                }
                break;
            }

            case "redo": {
                if (redoStack.isEmpty()) {
                    out.append("No action to redo!");
                    break;
                }
                String redoAction = redoStack.pop();
                undoStack.push(redoAction);

                String[] redoParts = redoAction.split("\\s+");
                switch (redoParts[0]) {
                    case "rectangle":
                        getShapesHashMap().put(redoParts[1], new Rectangle(redoParts[1],
                                Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]),
                                Double.parseDouble(redoParts[4]), Double.parseDouble(redoParts[5])));
                        out.append("Redo successful");
                        break;
                    case "line":
                        getShapesHashMap().put(redoParts[1], new Line(redoParts[1],
                                Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]),
                                Double.parseDouble(redoParts[4]), Double.parseDouble(redoParts[5])));
                        out.append("Redo successful");
                        break;
                    case "circle":
                        getShapesHashMap().put(redoParts[1], new Circle(redoParts[1],
                                Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]),
                                Double.parseDouble(redoParts[4])));
                        out.append("Redo successful");
                        break;
                    case "square":
                        getShapesHashMap().put(redoParts[1], new Square(redoParts[1],
                                Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]),
                                Double.parseDouble(redoParts[4])));
                        out.append("Redo successful");
                        break;
                    case "move":
                        Shape s = getShapesHashMap().get(redoParts[1]);
                        if (s != null) s.move(Double.parseDouble(redoParts[2]), Double.parseDouble(redoParts[3]));
                        out.append("Redo processed");
                        break;
                    case "delete":
                        deletedShapes.put(redoParts[1], getShapesHashMap().get(redoParts[1]));
                        getShapesHashMap().remove(redoParts[1]);
                        out.append("Redo processed");
                        break;
                    case "group":
                    case "ungroup":
                        out.append("Redo unsuccessful");
                        break;
                }
                break;
            }

            case "quit": {
                out.append("quit");
                break;
            }

            default:
                out.append("Invalid shape");
                break;
        }

        return out.toString();
    }

    // --- getters/setters ---

    public HashMap<String, Shape> getShapesHashMap() {
        return shapesHashMap;
    }

    public void setShapesHashMap(HashMap<String, Shape> shapesHashMap) {
        this.shapesHashMap = shapesHashMap;
    }

    public String getHtmlPath() {
        return htmlPath;
    }

    public void setHtmlPath(String htmlPath) {
        this.htmlPath = htmlPath;
    }

    public String getTxtPath() {
        return txtPath;
    }

    public void setTxtPath(String txtPath) {
        this.txtPath = txtPath;
    }
}
