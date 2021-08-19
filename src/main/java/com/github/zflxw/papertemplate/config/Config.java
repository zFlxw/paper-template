package com.github.zflxw.papertemplate.config;

import com.github.zflxw.papertemplate.YourPlugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;

public class Config {
    private final File file;
    private YamlConfiguration yamlConfiguration;

    public Config(File file) {
        this.file = file;
        this.yamlConfiguration = YamlConfiguration.loadConfiguration(file);

        this.load();
    }

    /**
     * get a string from the config, with alternative color codes replaced
     * @param key the key to search for
     * @return the colored string
     */
    public String getColoredString(String key) {
        return ChatColor.translateAlternateColorCodes('&', this.yamlConfiguration.getString(key));
    }

    /**
     * get a string from the config
     * @param key the key to search for
     * @return the string of the key
     */
    public String getString(String key) {
        return this.yamlConfiguration.getString(key);
    }

    /**
     * get an int from the config
     * @param key the key to search for
     * @return the int of the key
     */
    public int getInt(String key) {
        return this.yamlConfiguration.getInt(key);
    }

    /**
     * get a double from the config
     * @param key the key to search for
     * @return the double of the key
     */
    public double getDouble(String key) {
        return this.yamlConfiguration.getDouble(key);
    }

    /**
     * get a boolean from the config
     * @param key the key to search for
     * @return the boolean of the key
     */
    public boolean getBoolean(String key) {
        return this.yamlConfiguration.getBoolean(key);
    }

    /**
     * get a list from the config
     * @param key the key to search for
     * @return the list of the key
     */
    public List<?> getList(String key) {
        return this.yamlConfiguration.getList(key);
    }

    /**
     * get the config file
     * @return the config file
     */
    public File getFile() {
        return this.file;
    }

    /**
     * get the yaml configuration
     * @return the yaml configuration
     */
    public YamlConfiguration getYamlConfiguration() {
        return this.yamlConfiguration;
    }

    /**
     * reload the yaml configuration
     */
    public void reloadConfig() {
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * loads the default config values into the given file, if not present. Otherwise, the config will be checked and validated.
     */
    private void load() {
        if (!file.exists()) {
            Bukkit.getLogger().log(Level.WARNING, "No configuration file found. Loading default config.");
            try {
                Files.copy(YourPlugin.class.getClassLoader().getResourceAsStream("config.yml"), Paths.get(file.toURI()));
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        } else {
            validateConfig(file);
        }
    }

    /**
     * check the config file with the entries from the template config. Missing entries will be added to the config file
     * redundant entries in the config file will be ignored.
     * @param file the config file
     */
    private void validateConfig(File file) {
        try {
            File tmpFile = new File(YourPlugin.getInstance().getDataFolder() + "/temp", "config-tmpl.yml");
            FileUtils.copyInputStreamToFile(YourPlugin.class.getClassLoader().getResourceAsStream("config.yml"), tmpFile);

            YamlConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);
            YamlConfiguration templateConfig = YamlConfiguration.loadConfiguration(tmpFile);

            for (String key : templateConfig.getKeys(true)) {
                if (!fileConfig.contains(key)) {
                    fileConfig.set(key, templateConfig.get(key));
                }
            }

            fileConfig.save(file);
            FileUtils.deleteDirectory(new File(YourPlugin.getInstance().getDataFolder() + "/temp"));
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
