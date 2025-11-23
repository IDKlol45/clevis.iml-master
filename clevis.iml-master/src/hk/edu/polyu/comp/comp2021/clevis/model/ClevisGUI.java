package hk.edu.polyu.comp.comp2021.clevis.model;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Graphical User Interface for the Clevis vector graphics tool.
 * Provides a visual canvas for rendering shapes, a command input field,
 * and a console output area. Automatically scales and centers the view
 * to fit all shapes while maintaining aspect ratio. Implements event-driven
 * command processing and real-time graphics updates.
 *
 * Note: This class assumes modifications to the existing model classes:
 * 1. Add import java.awt.*; and java.awt.geom.*; to Shape subclasses.
 * 2. Implement draw(Graphics g) in each Shape subclass as follows:
 *    - Line:
 *      public void draw(Graphics g) {
 *        if (g instanceof Graphics2D g2) {
 *          g2.draw(new Line2D.Double(x1, y1, x2, y2));
 *        }
 *      }
 *    - Circle:
 *      public void draw(Graphics g) {
 *        if (g instanceof Graphics2D g2) {
 *          g2.draw(new Ellipse2D.Double(x - radius, y - radius, 2 * radius, 2 * radius));
 *        }
 *      }
 *    - Rectangle:
 *      public void draw(Graphics g) {
 *        if (g instanceof Graphics2D g2) {
 *          g2.draw(new Rectangle2D.Double(x, y, width, height));
 *        }
 *      }
 *    - Square:
 *      public void draw(Graphics g) {
 *        if (g instanceof Graphics2D g2) {
 *          g2.draw(new Rectangle2D.Double(x, y, side, side));
 *        }
 *      }
 *    - ShapeGroup:
 *      public void draw(Graphics g) {
 *        List<Shape> sortedShapes = new ArrayList<>(shapes);
 *        sortedShapes.sort(Comparator.comparingInt(Shape::getZ));
 *        for (Shape s : sortedShapes) {
 *          s.draw(g);
 *        }
 *      }
 * 3. In Clevis, add a ClevisLogger member and a public String processCommand(String command) method
 *    that extracts the command processing logic from run(), logs the command, performs the action,
 *    collects output messages in a StringBuilder, and returns the output string. For 'quit', return "quit".
 *
 * To launch: Create an instance with log paths, e.g., new ClevisGUI("log.txt", "log.html");
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class ClevisGUI extends JFrame {
    private final Clevis model;
    private final JTextField commandField;
    private final JTextArea outputArea;
    private final DrawingPanel drawingPanel;

    /**
     * Constructs and initializes the GUI with specified log file paths.
     * Sets up the window layout, components, and event listeners.
     *
     * @param txtPath path for text log file
     * @param htmlPath path for HTML log file
     */
    public ClevisGUI(String txtPath, String htmlPath) {
        model = new Clevis(txtPath, htmlPath);

        setTitle("Clevis GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        // Command input panel
        JPanel inputPanel = new JPanel(new BorderLayout());
        commandField = new JTextField();
        JButton executeButton = new JButton("Execute");
        inputPanel.add(new JLabel("Command: "), BorderLayout.WEST);
        inputPanel.add(commandField, BorderLayout.CENTER);
        inputPanel.add(executeButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.NORTH);

        // Drawing canvas
        drawingPanel = new DrawingPanel();
        add(new JScrollPane(drawingPanel), BorderLayout.CENTER);

        // Output console
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        add(new JScrollPane(outputArea), BorderLayout.SOUTH);

        // Event listeners
        ActionListener executeListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String cmd = commandField.getText().trim();
                if (!cmd.isEmpty()) {
                    try {
                        String result = model.run(cmd);
                        if ("quit".equals(result)) {
                            dispose();
                        } else {
                            outputArea.append("> " + cmd + "\n" + result + "\n");
                            drawingPanel.repaint();
                        }
                    } catch (IOException ex) {
                        outputArea.append("Error: " + ex.getMessage() + "\n");
                    }
                    commandField.setText("");
                }
            }
        };
        commandField.addActionListener(executeListener);
        executeButton.addActionListener(executeListener);

        setVisible(true);
    }

    private class DrawingPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Compute total bounding box
            BoundingBox totalBB = computeTotalBoundingBox();
            if (totalBB == null) return;

            // Add margin
            double margin = 1.0;
            double minX = totalBB.getX() - margin;
            double minY = totalBB.getY() - margin;
            double w = totalBB.getWidth() + 2 * margin;
            double h = totalBB.getHeight() + 2 * margin;

            // Compute scale and translation to fit and center
            double sx = getWidth() / w;
            double sy = getHeight() / h;
            double scale = Math.min(sx, sy) * 0.9; // 90% to leave some padding

            AffineTransform at = new AffineTransform();
            at.translate((getWidth() - w * scale) / 2, (getHeight() - h * scale) / 2);
            at.scale(scale, scale);
            at.translate(-minX, -minY);
            g2.setTransform(at);

            // Draw shapes in z-order (ascending z, lower first)
            List<Shape> sortedShapes = new ArrayList<>(model.getShapesHashMap().values());
            Collections.sort(sortedShapes, Comparator.comparingInt(Shape::getZ));
            g2.setColor(Color.BLACK);
            for (Shape shape : sortedShapes) {
                shape.draw(g2);
            }
        }

        private BoundingBox computeTotalBoundingBox() {
            if (model.getShapesHashMap().isEmpty()) return null;

            double minX = Double.MAX_VALUE;
            double minY = Double.MAX_VALUE;
            double maxX = Double.MIN_VALUE;
            double maxY = Double.MIN_VALUE;

            for (Shape s : model.getShapesHashMap().values()) {
                BoundingBox bb = s.getBoundingBox();
                minX = Math.min(minX, bb.getX());
                minY = Math.min(minY, bb.getY());
                maxX = Math.max(maxX, bb.getX() + bb.getWidth());
                maxY = Math.max(maxY, bb.getY() + bb.getHeight());
            }

            return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
        }
    }
}