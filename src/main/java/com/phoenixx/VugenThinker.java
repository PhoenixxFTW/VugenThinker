package com.phoenixx;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author Junaid Talpur
 * @project VugenThinker
 * @since 10:08 AM [17-02-2023]
 */
public class VugenThinker {

    //TODO Disable after compiling
    public static boolean DEBUG_ENV = true;
    public static String currentPath;

    public static int thinkTime = 0;
    public static boolean limitThinkTime = false;

    public static void main(String[] args) throws UnsupportedEncodingException {
        if(DEBUG_ENV) {
            System.out.println("WARNING: The VugenThinker application is set to run in a DEBUG ENVIRONMENT. EXIT NOW OR THERE MAY BE DATA LOSS.");
        }

        currentPath = VugenThinker.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        currentPath = URLDecoder.decode(currentPath, "UTF-8");
        currentPath = currentPath.replace("\\","/");

        if(DEBUG_ENV) {
            // We remove the last part of the string if this is running in a workspace from the build's folder (/build/classes/java/main/)
            currentPath = currentPath.substring(1, currentPath.length() - 24);
            currentPath += "/TestScripts/";
        }

        System.out.println("Path of current program; " + currentPath);

        File folder = new File(currentPath);
        if(!folder.isDirectory()) {
            System.out.println("Current location is not a directory!");
            return;
        }

        System.out.println("FOUND: " + folder.getAbsolutePath());
        System.out.println("All files in current directory: " + Arrays.toString(folder.list()));

        List<File> vugenScripts = new ArrayList<>();

        for(File fileFound: Objects.requireNonNull(folder.listFiles())) {
            if(fileFound.isDirectory()) {
                System.out.println("Directory: " + fileFound.getName());
                boolean isVugenScript = false;
                for(String fileName: Objects.requireNonNull(fileFound.list())) {
                    System.out.println("\t> " + fileName);
                    // TODO Save this in a custom object
                    if(fileName.contains(".usr")) {
                        isVugenScript = true;
                        break;
                    }
                }
                if(isVugenScript) {
                    System.out.println("Found Vugen script folder: " + fileFound.getName());
                    vugenScripts.add(fileFound);
                }
            }
        }

        // Script folders
        for(File script: vugenScripts) {
            if(!script.isDirectory()) {
                System.out.println("Script file: " + script.getName() + " IS NOT A DIRECTORY. Cancelling...");
                return;
            }
            List<File> scriptFiles = new ArrayList<>();

            // All files inside the main script file
            for(File scriptFile: Objects.requireNonNull(script.listFiles())) {
                if(scriptFile.getName().contains(".usr")) {

                }
            }
        }

        //File currentFile = new File(path);

        //String decodedPath = URLDecoder.decode(path, "UTF-8");
    }
}
