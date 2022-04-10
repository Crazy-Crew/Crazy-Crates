package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.v2.utils.FileUtil;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class FileManager {

    // Private instance of the class.
    private final static FileManager instance = new FileManager();

    // The boolean value for if we should send log Messages or not.
    private boolean currentValue = false;

    /**
     * Everything related to pre-defined files.
     */
    private final HashMap<Files, File> files = new HashMap<>();
    private final HashMap<Files, FileConfiguration> configs = new HashMap<>();

    /**
     * Everything related to custom files.
     */
    private final ArrayList<String> homeFolders = new ArrayList<>();
    private final ArrayList<CustomFile> customFiles = new ArrayList<>();
    private final HashMap<String, String> jarHomeFolders = new HashMap<>();
    private final HashMap<String, String> autoGenerateFiles = new HashMap<>();

    // Exposes the instance of the class
    public static FileManager getInstance() {
        return instance;
    }
    
    /**
     * Sets up the plugin and loads all necessary files.
     */
    public FileManager setup() {

        files.clear();
        customFiles.clear();
        configs.clear();

        // Loads all the normal static files.
        for (Files file : Files.values()) {
            File newFile = new File(CrazyManager.getJavaPlugin().getDataFolder(), file.getFileLocation());

            if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Loading the " + file.getFileName());

            if (!newFile.exists()) {
                try {
                    File serverFile = new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + file.getFileLocation());
                    InputStream jarFile = getClass().getResourceAsStream("/" + file.getFileJar());

                    if (jarFile != null) {
                        FileUtil.INSTANCE.copyFile(jarFile, serverFile);
                    }

                } catch (Exception e) {
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Failed to load file: " + file.getFileName());
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
                    continue;
                }
            }

            files.put(file, newFile);
            configs.put(file, YamlConfiguration.loadConfiguration(newFile));

            if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Successfully loaded " + file.getFileName());
        }

        // Starts to load all the custom files.
        if (homeFolders.size() > 0) {
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Loading custom files.");

            for (String homeFolder : homeFolders) {

                File homeFile = new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + homeFolder);

                if (homeFile.exists()) {
                    String[] list = homeFile.list();

                    if (list != null) {
                        for (String name : list) {

                            if (name.endsWith(".yml")) {
                                CustomFile file = new CustomFile(name, homeFolder);
                                if (file.exists()) {
                                    customFiles.add(file);
                                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Loaded new custom file: " + homeFolder + "/" + name + ".");
                                }
                            }
                        }
                    }
                } else {
                    if (!homeFile.exists()) homeFile.mkdir();

                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("The folder " + homeFolder + "/ was not found so it was created.");

                    for (String fileName : autoGenerateFiles.keySet()) {
                        if (autoGenerateFiles.get(fileName).equalsIgnoreCase(homeFolder)) {
                            homeFolder = autoGenerateFiles.get(fileName);
                            try {
                                File serverFile = new File(CrazyManager.getJavaPlugin().getDataFolder(), homeFolder + "/" + fileName);
                                InputStream jarFile = getClass().getResourceAsStream((jarHomeFolders.getOrDefault(fileName, homeFolder)) + "/" + fileName);

                                if (jarFile != null) {
                                    FileUtil.INSTANCE.copyFile(jarFile, serverFile);
                                }

                                if (fileName.toLowerCase().endsWith(".yml")) customFiles.add(new CustomFile(fileName, homeFolder));

                                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Created new default file: " + homeFolder + "/" + fileName + ".");
                            } catch (Exception e) {
                                if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Failed to create new default file: " + homeFolder + "/" + fileName + "!");
                                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
                            }
                        }
                    }
                }
            }
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Finished loading custom files.");
        }
        return this;
    }
    
    /**
     * Turn on the logger system for the FileManager.
     * @param newValue True to turn it on and false for it to be off.
     */
    public FileManager logInfo(boolean newValue) {
        this.currentValue = newValue;
        return this;
    }
    
    /**
     * Check if the logger is logging in console.
     * @return True if it is and false if it isn't.
     */
    public boolean isLogging() {
        return currentValue;
    }

    // Handling default generated files.
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerDefaultGeneratedFiles(String fileName, String homeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        return this;
    }
    
    /**
     * Register a file that needs to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The name of the file you want to auto-generate when the folder doesn't exist.
     * @param homeFolder The folder that has custom files in it.
     * @param jarHomeFolder The folder that the file is found in the jar.
     */
    public FileManager registerDefaultGeneratedFiles(String fileName, String homeFolder, String jarHomeFolder) {
        autoGenerateFiles.put(fileName, homeFolder);
        jarHomeFolders.put(fileName, jarHomeFolder);
        return this;
    }
    
    /**
     * Unregister a file that doesn't need to be generated when it's home folder doesn't exist. Make sure to have a "/" in front of the home folder's name.
     * @param fileName The file that you want to remove from auto-generating.
     */
    public FileManager removeDefaultGeneratedFiles(String fileName) {
        autoGenerateFiles.remove(fileName);
        jarHomeFolders.remove(fileName);
        return this;
    }

    // End of handling default generated files.
    
    /**
     * Gets the file from the system.
     * @return The file from the system.
     */
    public FileConfiguration getFile(Files file) {
        return configs.get(file);
    }

    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadFile(Files file) {
        configs.put(file, YamlConfiguration.loadConfiguration(files.get(file)));
    }
    
    /**
     * Saves the file from the loaded state to the file system.
     */
    public void saveFile(Files file) {
        try {
            configs.get(file).save(files.get(file));
        } catch (IOException e) {
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Could not save " + file.getFileName() + "!");
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
        }
    }

    // The beginning of Custom File Management.

    /**
     * Register a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder that has custom files in it.
     */
    public FileManager registerCustomFilesFolder(String homeFolder) {
        homeFolders.add(homeFolder);
        return this;
    }

    /**
     * Unregister a folder that has custom files in it. Make sure to have a "/" in front of the folder name.
     * @param homeFolder The folder with custom files in it.
     */
    public FileManager removeCustomFilesFolder(String homeFolder) {
        homeFolders.remove(homeFolder);
        return this;
    }

    /**
     * Get a custom file from the loaded custom files instead of a hardcoded one.
     * This allows you to get custom files like Per player data files.
     * @param name Name of the crate you want. (Without the .yml)
     * @return The custom file you wanted otherwise if not found will return null.
     */
    public CustomFile getCustomFile(String name) {
        for (CustomFile file : customFiles) {
            if (file.getName().equalsIgnoreCase(name)) {
                return file;
            }
        }
        return null;
    }
    
    /**
     * Save a custom file.
     * @param name The name of the custom file.
     */
    public void saveCustomFile(String name) {
        CustomFile file = getCustomFile(name);
        if (file != null) {
            try {
                file.getFile().save(new File(CrazyManager.getJavaPlugin().getDataFolder(), file.getHomeFolder() + "/" + file.getFileName()));
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Successfully saved the " + file.getFileName() + ".");
            } catch (Exception e) {
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Could not save " + file.getFileName() + "!");
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
            }
        } else {
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("The file " + name + ".yml could not be found!");
        }
    }
    
    /**
     * Save a custom file.
     * @param file The custom file you are saving.
     */
    public void saveCustomFile(CustomFile file) {
        file.saveFile();
    }
    
    /**
     * Overrides the loaded state file and loads the file systems file.
     */
    public void reloadCustomFile(String name) {
        CustomFile file = getCustomFile(name);
        if (file != null) {
            try {
                file.file = YamlConfiguration.loadConfiguration(new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + file.getHomeFolder() + "/" + file.getFileName()));
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Successfully reloaded the " + file.getFileName() + ".");
            } catch (Exception e) {
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Could not reload the " + file.getFileName() + "!");
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
            }
        } else {
            if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("The file " + name + ".yml could not be found!");
        }
    }
    
    /**
     * Overrides the loaded state file and loads the filesystems file.
     * @return True if it reloaded correct and false if the file wasn't found.
     */
    public Boolean reloadCustomFile(CustomFile file) {
        return file.reloadFile();
    }

    // End of handling custom files.

    // Other misc methods.
    public void reloadAllFiles() {
        for (Files file : Files.values()) {
            file.reloadFile();
        }

        for (CustomFile file : customFiles) {
            file.reloadFile();
        }
    }
    
    public ArrayList<String> getAllCratesNames() {
        ArrayList<String> files = new ArrayList<>();
        File cratesFolder = new File(CrazyManager.getJavaPlugin().getDataFolder(), "/crates");

        if (!cratesFolder.exists()) cratesFolder.mkdir();

        for (String name : cratesFolder.list()) {
            if (!name.endsWith(".yml")) continue;
            files.add(name.replaceAll(".yml", ""));
        }
        return files;
    }
    
    public enum Files {
        
        //ENUM_NAME("fileName.yml", "fileLocation.yml"),
        //ENUM_NAME("fileName.yml", "newFileLocation.yml", "oldFileLocation.yml"),
        CONFIG("config.yml", "config.yml"),
        MESSAGES("messages.yml", "messages.yml"),
        LOCATIONS("locations.yml", "locations.yml"),
        DATA("data.yml", "data.yml");
        
        private final String fileName;
        private final String fileJar;
        private final String fileLocation;
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location the file in the plugin's folder.
         */
        Files(String fileName, String fileLocation) {
            this(fileName, fileLocation, fileLocation);
        }
        
        /**
         * The files that the server will try and load.
         * @param fileName The file name that will be in the plugin's folder.
         * @param fileLocation The location of the file will be in the plugin's folder.
         * @param fileJar The location of the file in the jar.
         */
        Files(String fileName, String fileLocation, String fileJar) {
            this.fileName = fileName;
            this.fileLocation = fileLocation;
            this.fileJar = fileJar;
        }
        
        /**
         * Get the name of the file.
         * @return The name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * The location the jar it is at.
         * @return The location in the jar the file is in.
         */
        public String getFileLocation() {
            return fileLocation;
        }
        
        /**
         * Get the location of the file in the jar.
         * @return The location of the file in the jar.
         */
        public String getFileJar() {
            return fileJar;
        }
        
        /**
         * Gets the file from the system.
         * @return The file from the system.
         */
        public FileConfiguration getFile() {
            return getInstance().getFile(this);
        }
        
        /**
         * Saves the file from the loaded state to the file system.
         */
        public void saveFile() {
            getInstance().saveFile(this);
        }
        
        /**
         * Overrides the loaded state file and loads the file systems file.
         */
        public void reloadFile() {
            getInstance().reloadFile(this);
        }
    }
    
    public class CustomFile {
        
        private final String name;
        private final String fileName;
        private final String homeFolder;
        private FileConfiguration file;
        
        /**
         * A custom file that is being made.
         * @param name Name of the file.
         * @param homeFolder The home folder of the file.
         */
        public CustomFile(String name, String homeFolder) {
            this.name = name.replace(".yml", "");
            this.fileName = name;
            this.homeFolder = homeFolder;

            File homeDir = new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + homeFolder);

            if (!homeDir.exists()) {
                homeDir.mkdir();
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("The folder " + homeFolder + "/ was not found so it was created.");
                file = null;
            } else {
                file = YamlConfiguration.loadConfiguration(new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + homeFolder + "/" + name));
            }
        }
        
        /**
         * Get the name of the file without the .yml part.
         * @return The name of the file without the .yml.
         */
        public String getName() {
            return name;
        }
        
        /**
         * Get the full name of the file.
         * @return Full name of the file.
         */
        public String getFileName() {
            return fileName;
        }
        
        /**
         * Get the name of the home folder of the file.
         * @return The name of the home folder the files are in.
         */
        public String getHomeFolder() {
            return homeFolder;
        }
        
        /**
         * Get the ConfigurationFile.
         * @return The ConfigurationFile of this file.
         */
        public FileConfiguration getFile() {
            return file;
        }
        
        /**
         * Check if the file actually exists in the file system.
         * @return True if it does and false if it doesn't.
         */
        public Boolean exists() {
            return file != null;
        }
        
        /**
         * Saves the custom file.
         */
        public void saveFile() {
            if (file != null) {
                try {
                    file.save(new File(CrazyManager.getJavaPlugin().getDataFolder(), homeFolder + "/" + fileName));
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Successfully saved the " + fileName + ".");
                } catch (Exception e) {
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Could not save " + fileName + "!");
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
                }
            } else {
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("There was a null custom file that could not be found!");
            }
        }
        
        /**
         * Overrides the loaded state file and loads the filesystems file.
         * @return True if it reloaded correct and false if the file wasn't found or there was an error.
         */
        public Boolean reloadFile() {
            if (file != null) {
                try {
                    file = YamlConfiguration.loadConfiguration(new File(CrazyManager.getJavaPlugin().getDataFolder(), "/" + homeFolder + "/" + fileName));
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Successfully reloaded the " + fileName + ".");
                    return true;
                } catch (Exception e) {
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("Could not reload the " + fileName + "!");
                    if (currentValue) CrazyManager.getJavaPlugin().getLogger().info("Error: " + e.getMessage());
                }
            } else {
                if (currentValue) CrazyManager.getJavaPlugin().getLogger().warning("There was a null custom file that was not found!");
            }
            return false;
        }
    }
}