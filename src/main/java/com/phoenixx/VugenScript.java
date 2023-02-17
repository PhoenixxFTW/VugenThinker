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
                        System.out.println("STARTING ACTION READING @@@@");
                    } else if (startReadingActions) {
                        if (line.contains(".c")) {
                            vugenScript.addActionFile(line.split("=")[1]);
                        } else {
                            System.out.println("STOPPING ACTION READING @@@");
                            startReadingActions = false;
                        }
                    }

                    System.out.println(line);
                    // read next line
                    line = reader.readLine();
                }
            }
        }
        return vugenScript;
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
