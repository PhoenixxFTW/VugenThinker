package com.phoenixx;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Junaid Talpur
 * @project VugenThinker
 * @since 11:53 AM [17-02-2023]
 */
public class VugenScript {
    // The folder containing all the scripts files
    private final File scriptFolder;
    // The main script file ending in .usr
    private File scriptFile;
    // Config folder name which contains the runtime settings
    private String configName;
    private String globalHFileName = "globals.h";

    private int thinkTime;
    private boolean limitThinkTime;
    private final List<String> actionFiles;
    private final List<String> globalVarNames;

    private VugenScript(File scriptFolder) {
        this.scriptFolder = scriptFolder;
        this.actionFiles = new ArrayList<>();
        this.globalVarNames = new ArrayList<>();
    }

    // Builder
    public static VugenScript buildScript(File scriptFolder) throws IOException {
        VugenScript vugenScript = new VugenScript(scriptFolder);

        // All files inside the main script folder
        for(File scriptFile: Objects.requireNonNull(scriptFolder.listFiles())) {

            // Check if the given file ends with the .usr extension to confirm it's a script file
            if (scriptFile.getName().contains(".usr")) {
                vugenScript.setScriptFile(scriptFile);

                BufferedReader reader = new BufferedReader(new FileReader(scriptFile));
                String line = reader.readLine();

                // Read in all the lines from the script file
                boolean startReadingActions = false;
                while (line != null) {
                    // Load config file name
                    if (line.startsWith("Default Profile=")) {
                        vugenScript.setConfigName(line.replace("Default Profile=", ""));
                    }
                    if (!startReadingActions && line.equalsIgnoreCase("[Actions]")) {
                        startReadingActions = true;
                        //System.out.println("STARTING ACTION READING @@@@");
                    } else if (startReadingActions) {
                        if (line.contains(".c")) {
                            vugenScript.addActionFile(line.split("=")[1]);
                        } else {
                            //System.out.println("STOPPING ACTION READING @@@");
                            startReadingActions = false;
                        }
                    }

                    //System.out.println(line);
                    // read next line
                    line = reader.readLine();
                }
            }
        }
        return vugenScript;
    }

    // Called to update think times in both the config and action files
    public void updateScript() throws IOException {
        this.updateConfig();
        if(!limitThinkTime) {
            this.updateActionFiles();
            this.updateGlobals();
        }
    }

    private void updateConfig() throws IOException {
        List<String> lines = new ArrayList<>();

        String line;
        File configFolder = new File(scriptFolder, this.getConfigName());
        FileReader fileReader = new FileReader(configFolder);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        while ((line = bufferedReader.readLine()) != null) {
            if (line.contains("LimitFlag=")) {
                line = "LimitFlag="+(this.isLimitThinkTime() ? 1 : 0);
            }
            lines.add(line);
        }
        fileReader.close();
        bufferedReader.close();

        FileOutputStream fos = new FileOutputStream(configFolder);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (String newLine: lines) {
            bw.write(newLine);
            bw.newLine();
        }
        bw.close();
    }

    private void updateActionFiles() throws IOException {
        for(String actionFile: this.getActionFiles()) {
            List<String> lines = new ArrayList<>();

            String line;
            File configFolder = new File(scriptFolder, actionFile);
            FileReader fileReader = new FileReader(configFolder);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            //TODO Figure out how to replace the string using regex

            boolean multiComment = false;
            while ((line = bufferedReader.readLine()) != null) {
                // Multiline comment detection
                if(!multiComment && line.contains("/*")) {
                    multiComment = true;
                }
                if(multiComment && line.contains("*/")) {
                    multiComment = false;
                }

                int commentIndex = line.indexOf("//");
                if(!multiComment && line.contains("lr_think_time") && !(line.contains("//") && !(commentIndex > line.indexOf("lr_think_time")))) {
                    int startIndex = line.indexOf("lr_think_time(");
                    startIndex += 14; // Size of the function name

                    //System.out.println("STARTING INDEX: " + startIndex);

                    // We manually count the numbers passed into the function.
                    // THIS IS An EXTREMELY HACKY WAY of doing it, USE REGEX instead
                    String timeAmount = "";
                    String varName = "";
                    boolean isVar = false;
                    for (int i = 0; i < 30; i++) {
                        char foundChar = line.charAt(startIndex + i);
                        if (foundChar == ')') {
                            break;
                        } else if (!isVar && Character.isDigit(foundChar)) {
                            timeAmount += foundChar;
                            // If instead the character we found is a letter, it's probably a variable in globals.h
                        } else if (Character.isAlphabetic(foundChar) || isVar) {
                            isVar = true;
                            varName += foundChar;
                        }
                    }
                    if(varName.isEmpty()) {
                        int timeGiven = Integer.parseInt(timeAmount.toString());
                        //System.out.println("FOUND THINK TIME: " + timeGiven + " SCRIPT: " + actionFile);
                        line = line.replace("lr_think_time(" + timeGiven + ")", "lr_think_time(" + this.getThinkTime() + ")");
                    } else {
                        this.globalVarNames.add(varName);
                        System.out.println("FOUND VAR: " + varName);
                    }
                }

                lines.add(line);
            }
            fileReader.close();
            bufferedReader.close();

            FileOutputStream fos = new FileOutputStream(configFolder);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String newLine: lines) {
                bw.write(newLine);
                bw.newLine();
            }
            bw.close();
        }
        System.out.println("Updated: " + this.scriptFile.getName());
        System.out.println("\t> " + actionFiles);
        System.out.println("");
    }

    private void updateGlobals() throws IOException {
        File globalFile = new File(scriptFolder, this.globalHFileName);
        //System.out.println("GLOBAL FILE: " + globalFile.getAbsolutePath());
        if(!globalFile.exists()) {
            System.out.println("VugenThinker could not find " + this.globalHFileName + "! Are you sure it exists?");
            return;
        }

        String line;
        List<String> lines = new ArrayList<>();
        FileReader fileReader = new FileReader(globalFile);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        boolean multiComment = false;
        while ((line = bufferedReader.readLine()) != null) {
            // Multiline comment detection
            if (!multiComment && line.contains("/*")) {
                multiComment = true;
            }
            if (multiComment && line.contains("*/")) {
                multiComment = false;
            }

            int commentIndex = line.indexOf("//");
            if (!multiComment) {
                for (String varName : this.globalVarNames) {
                    String varNameCheck = varName + " = ";

                    /*System.out.println("Checking line: " + line);
                    System.out.println("FOR : " + varNameCheck);
                    System.out.println("COMMENTED: " + (!(line.contains("//") && !(commentIndex > line.indexOf(varNameCheck)))));
                    System.out.println("CONTAINS: " + line.contains(varNameCheck));*/
                    if(line.contains(varNameCheck) && !(line.contains("//") && !(commentIndex > line.indexOf(varNameCheck)))) {
                        int startIndex = line.indexOf(varNameCheck) + varNameCheck.length(); // Size of the variable name
                        // Replace
                        String timeAmount = "";
                        for (int i = 0; i < 30; i++) {
                            char foundChar = line.charAt(startIndex + i);
                            if (Character.isDigit(foundChar)) {
                                timeAmount += foundChar;
                                // If instead the character we found is a letter, its probably a variable in globals.h
                            } else if (foundChar == ';') {
                                break;
                            }
                        }
                        //System.out.println("FOUND TIME AMOUNT FOR GLOBAL VAR: " + varName + " TIME: " + timeAmount);
                        line = line.replace(varNameCheck + timeAmount, varNameCheck + this.getThinkTime());
                    }
                }
            }
            //System.out.println("ADDED LINE: " + line);
            lines.add(line);
        }

        fileReader.close();
        bufferedReader.close();

        FileOutputStream fos = new FileOutputStream(globalFile);
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
        for (String newLine: lines) {
            bw.write(newLine);
            bw.newLine();
        }
        bw.close();
        System.out.println("Updated globals.h!");
        //System.out.println("FULLY UPDATED GLOBALS.H FILE @@@@");
    }

    public void setScriptFile(File scriptFile) {
        this.scriptFile = scriptFile;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public VugenScript setThinkTime(int thinkTime) {
        this.thinkTime = thinkTime;
        return this;
    }

    public VugenScript setLimitThinkTime(boolean limitThinkTime) {
        this.limitThinkTime = limitThinkTime;
        return this;
    }

    public void addActionFile(String actionFile) {
        this.actionFiles.add(actionFile);
    }

    public int getThinkTime() {
        return thinkTime;
    }

    public boolean isLimitThinkTime() {
        return limitThinkTime;
    }

    public List<String> getActionFiles() {
        return actionFiles;
    }

    public String getConfigName() {
        return configName;
    }

    public File getScriptFile() {
        return scriptFile;
    }

    public File getScriptFolder() {
        return scriptFolder;
    }
}
