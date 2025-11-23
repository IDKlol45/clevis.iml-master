package hk.edu.polyu.comp.comp2021.clevis.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Handles dual-format command logging for Clevis application sessions.
 * Maintains simultaneous HTML and plain text logs with proper formatting,
 * automatic indexing, and HTML character escaping to ensure data integrity
 * and prevent markup injection. Provides thread-safe logging operations
 * for command history tracking and session auditing.
 *
 * @author Group03 COMP2021 (November 23, 25)
 */
public class ClevisLogger {
    private final BufferedWriter txtLog;
    private final BufferedWriter htmlLog;
    private int index = 1; // used for the table index on html file

    /**
     * Initializes logging to both specified file paths and creates
     * the initial HTML table structure. Opens file streams for both
     * log formats and prepares them for command recording.
     *
     * @param txtPath filesystem path for the plain text log file
     * @param htmlPath filesystem path for the HTML formatted log file
     * @throws IOException if file creation, writing, or access fails
     * @throws SecurityException if file access permissions are insufficient
     */
    public ClevisLogger(String txtPath, String htmlPath) throws IOException {
        txtLog = new BufferedWriter(new FileWriter(txtPath));
        htmlLog = new BufferedWriter(new FileWriter(htmlPath));

        htmlLog.write("<html><body><table border=\"1\">\n");
        htmlLog.write("<tr><th>#</th><th>Command</th></tr>\n");

    }

    /**
     * Records a command to both log formats with automatic indexing
     * and HTML character escaping. Ensures consistent command history
     * across both output formats while maintaining data integrity.
     *
     * @param cmd the command string to be logged, must not be null
     * @throws IOException if write operations to either log file fail
     * @throws NullPointerException if the command string is null
     */
    public void log(String cmd) throws IOException {
        txtLog.write(cmd);
        txtLog.newLine();

        htmlLog.write(STR."""
<tr><td>\{index++}</td><td>\{escape(cmd)}</td></tr>
""");
    }

    /**
     * Properly closes both log files and finalizes the HTML document structure.
     * Ensures all buffered data is flushed to disk and file handles are released.
     * This method must be called to prevent resource leaks and ensure complete
     * log files with proper HTML document closure.
     *
     * @throws IOException if closing either file stream fails
     */
    public void close() throws IOException {
        txtLog.close();
        htmlLog.write("</table></body></html>");
        htmlLog.close();
    }

    private String escape(String cmd) { // in case the command has a character that is used in an html tag
        return cmd.replace("<", "&lt;").replace(">", "&gt;");
    }
}
