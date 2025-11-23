package hk.edu.polyu.comp.comp2021.clevis;

import hk.edu.polyu.comp.comp2021.clevis.model.Clevis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(String[] args) throws IOException {

        Map<String, String> params = parseArgs(args);

        if (!params.containsKey("-html") || !params.containsKey("-txt")) {
            System.out.println("Usage: java ... Application -html <htmlPath> -txt <txtPath>");
            return;
        }

        String htmlPath = resolveFilePath(params.get("-html"));
        String txtPath  = resolveFilePath(params.get("-txt"));

        Clevis clevis = new Clevis(htmlPath, txtPath);
        clevis.run();
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new HashMap<>();

        for (int i = 0; i < args.length - 1; i += 2) {
            map.put(args[i], args[i + 1]);
        }

        return map;
    }

    private static String resolveFilePath(String givenPath) throws IOException {
        File f = new File(givenPath);

        if (f.exists() && f.isFile()) {
            return f.getAbsolutePath();
        }

        // If file does not exist, create it in current directory
        String fileName = f.getName();
        File newFile = new File(System.getProperty("user.dir"), fileName);

        if (!newFile.exists()) {
            newFile.createNewFile();
            System.out.println("Created log file: " + newFile.getAbsolutePath());
        }

        return newFile.getAbsolutePath();
    }
}
