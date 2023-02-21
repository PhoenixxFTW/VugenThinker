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

    private int thinkTime;
    private boolean limitThinkTime;
    private final List<String> actionFiles;

    private VugenScript(File scriptFolder) {
        this.scriptFolder = scriptFolder;
        this.actionFiles = new ArrayList<>();
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
        this.updateActionFiles();
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
            //TODO Doesn't detect comments
            while ((line = bufferedReader.readLine()) != null) {
                // The good way of doing it
                //^[1-9][0-9]{1,2}$|^\d$
                /*Pattern pattern = Pattern.compile("lr_think_time\\(^[1-9]\\d{1,2}$|^\\d$\\)", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);

                boolean matchFound = matcher.matches();
                if(matchFound) {
                    System.out.println("LINE: " + line);
                }*/

                /*if (line.contains("lr_think_time")) {
                    line.replace("lr_think_time("+""+")", "lr_think_time("+this.getThinkTime()+")");
                }*/

                //FIXME The sketchy way of doing it
                //TODO Figure out how to implement multiline comment

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
                    StringBuilder timeAmount = new StringBuilder();
                    for (int i = 0; i < 6; i++) {
                        char foundChar = line.charAt(startIndex + i);
                        if (Character.isDigit(line.charAt(startIndex + i))) {
                            timeAmount.append(foundChar);
                        } else if (foundChar == ')') {
                            break;
                        }
                    }
                    int timeGiven = Integer.parseInt(timeAmount.toString());
                    //System.out.println("FOUND THINK TIME: " + timeGiven + " SCRIPT: " + actionFile);
                    line = line.replace("lr_think_time("+timeGiven+")", "lr_think_time("+this.getThinkTime()+")");
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
            System.out.println("Updated: " + actionFile);
        }
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
