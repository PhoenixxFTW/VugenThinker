package com.phoenixx;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

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
    public static boolean excludedScripts = false;

    public static List<VugenScript> loadedScripts = new ArrayList<>();
    public static List<VugenScript> selectedScripts = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        System.out.println();
        if(DEBUG_ENV) {
            System.out.println("WARNING: The VugenThinker application is set to run in a DEBUG ENVIRONMENT. EXIT NOW OR THERE MAY BE DATA LOSS.");
        }

        currentPath = VugenThinker.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        currentPath = URLDecoder.decode(currentPath, "UTF-8");
        currentPath = currentPath.replace("\\","/");

        if(DEBUG_ENV) {
            // We remove the last part of the string if this is running in a workspace from the build's folder (/build/classes/java/main/)
            currentPath = currentPath.substring(1, currentPath.length() - 24);
            currentPath += "TestScripts/";
        }

        System.out.println("Path of current program: " + currentPath);

        File folder = new File(currentPath);
        if(!DEBUG_ENV) {
            folder = folder.getParentFile();
        }

        System.out.println("UPDATED PATH: " + folder.getAbsolutePath());

        if(!folder.isDirectory()) {
            System.out.println("Current location is not a directory!");
            return;
        }

        if(Objects.requireNonNull(folder.list()).length == 0) {
            System.out.println("No scripts found in folder: " + folder.getName());
            return;
        }

        System.out.println("Found " + Objects.requireNonNull(folder.list()).length + " potential script files, loading now...");

        List<File> vugenScripts = new ArrayList<>();

        for(File fileFound: Objects.requireNonNull(folder.listFiles())) {
            if(fileFound.isDirectory()) {
                //System.out.println("Directory: " + fileFound.getName());
                boolean isVugenScript = false;
                for(String fileName: Objects.requireNonNull(fileFound.list())) {
                    //System.out.println("\t> " + fileName);
                    if(fileName.contains(".usr")) {
                        isVugenScript = true;
                        break;
                    }
                }
                if(isVugenScript) {
                    //System.out.println("Found Vugen script: " + fileFound.getName());
                    vugenScripts.add(fileFound);
                }
            }
        }

        System.out.println("Loading vugen scripts...");
        // Script folders
        for(File scriptFolder: vugenScripts) {
            if(!scriptFolder.isDirectory()) {
                System.out.println("Script file: " + scriptFolder.getName() + " IS NOT A DIRECTORY. Cancelling...");
                continue;
            }
            VugenScript vugenScript = VugenScript.buildScript(scriptFolder);
            loadedScripts.add(vugenScript);
        }
        System.out.println("Loaded " + loadedScripts.size() + " scripts!");

        int option = -1;
        Scanner input = new Scanner(System.in);

        while (option != 0) {
            System.out.println("\n======================= Vugen Thinker Main Menu =======================");
            System.out.println("Select an option:");
            System.out.println("0) Exit");
            System.out.println("1) View scripts in current folder");
            System.out.println("2) Settings");
            System.out.println("3) Apply changes");
            System.out.print("> ");
            option = input.nextInt();

            switch (option) {
                case 0:
                    // Exit application
                    System.out.println("Exiting...");
                    break;
                case 1:
                    System.out.println();
                    displayLoadedScripts();
                    break;
                case 2:
                    int settingsOptions = -1;
                    while (true) {
                        System.out.println("\n====================== Settings Menu =====================");
                        System.out.println("Select an option: ");
                        System.out.println("0) Return to main menu");
                        System.out.println("1) Set think time");
                        System.out.println("2) Enable / Disable think time limiter");
                        System.out.println("3) Include Scripts");
                        System.out.println("4) Exclude Scripts");
                        System.out.println("5) View selected settings");
                        System.out.print("> ");
                        settingsOptions = input.nextInt();

                        if(settingsOptions == 0) {
                            break;
                        } else if(settingsOptions == 1) {
                            System.out.print("\nInput think time (0-999): ");
                            thinkTime = input.nextInt();
                        } else if(settingsOptions == 2) {
                            limitThinkTime = !limitThinkTime;
                            System.out.println((limitThinkTime ? "ENABLED" : "DISABLED") + " think time limit!");
                        } else if (settingsOptions == 3) {
                            excludedScripts = false;
                            System.out.println("\n============== Script Inclusion Menu ==============");
                            scriptSelection(input);
                        } else if(settingsOptions == 4) {
                            excludedScripts = true;
                            System.out.println("\n============== Script Exclusion Menu ==============");
                            scriptSelection(input);
                        } else if(settingsOptions == 5) {
                            System.out.println("\n============== VugenThinker Selected Settings ==============");
                            displaySelectedSettings();
                        } else {
                            System.out.println("Invalid option!");
                        }
                    }

                    break;
                case 3:
                    System.out.println("\n============== Confirmation Menu ==============");
                    displaySelectedSettings();
                    System.out.println("Are you sure you want to apply these changes to the scripts?");
                    System.out.print("(y/n): ");
                    String check = input.next();
                    if (!check.equalsIgnoreCase("y")) {
                        System.out.println("Cancelling.");
                        break;
                    }
                    System.out.println("Applying changes...");

                    List<VugenScript> scripts = (selectedScripts.size() > 0) ? selectedScripts : loadedScripts;
                    for(VugenScript script: scripts) {
                        script.setThinkTime(thinkTime);
                        script.setLimitThinkTime(limitThinkTime);
                        script.updateScript();
                    }
                    break;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    public static void scriptSelection(Scanner input) {
        displayLoadedScripts();

        int option = -1;
        while (true) {
            System.out.print("Enter the ID of a script (0 to exit): ");
            option = input.nextInt();

            if(option > 0 && option <= loadedScripts.size()) {
                // Get the selected script via its index(-1) and add it to our new list
                selectedScripts.add(loadedScripts.get(option-1));
            } else {
                break;
            }
        }
    }

    public static void displayLoadedScripts() {
        System.out.println("All scripts: " + loadedScripts.size());
        for(int i = 0; i < loadedScripts.size(); i++) {
            VugenScript vugenScript = loadedScripts.get(i);
            System.out.println((i+1)+") " + vugenScript.getScriptFile().getName());
            System.out.println("\t> Config File: " + vugenScript.getConfigName());
            System.out.println("\t> Action files: " + vugenScript.getActionFiles());
        }
    }

    public static void displaySelectedSettings() {

        System.out.println("Think time: " + thinkTime);
        System.out.println("Think time limiter: " + (limitThinkTime ? "ENABLED" : "DISABLED"));
        if(selectedScripts.size() > 0) {
            System.out.println((excludedScripts ? "Excluded" : "Included") + " Scripts: " + selectedScripts.size());
            for (int i = 0; i < selectedScripts.size(); i++) {
                System.out.println("\t" + (i + 1) + ") " + selectedScripts.get(i).getScriptFile().getName());
            }
        } else {
            System.out.println("Included scripts: ALL");
        }
    }
}
