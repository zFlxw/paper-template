package com.github.zflxw.papertemplate.utils.localization;

import com.github.zflxw.papertemplate.YourPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.logging.Level;

public class Translator {
    private final HashMap<Language, YamlConfiguration> languageList = new HashMap<>();
    private final File directory;

    public Translator(String directory) {
        this(new File(directory));
    }

    public Translator(File directory) {
        this.directory = directory;

        this.load();
    }

    private void load() {
        if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs();
        }

        for (Language language : Language.values()) {
            File languageFile = new File(directory, language.getLanguageKey() + ".yml");

            if (!languageFile.exists()) {
                Bukkit.getLogger().log(Level.WARNING, "No localization file for \"" + language.getLanguageName() + " (" + language.getLanguageKey() + ")\" found. Loading default file.");
                try {
                    Files.copy(YourPlugin.class.getClassLoader().getResourceAsStream("languages/" + language.getLanguageKey() + ".yml"), Paths.get(languageFile.toURI()));

                    languageFile = new File(directory, language.getLanguageKey() + ".yml");
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                validateConfig(languageFile, language);
            }

            languageList.put(language, YamlConfiguration.loadConfiguration(languageFile));
        }
    }

    private void validateConfig(File file, Language language) {
        try {
            File tmpFile = new File(YourPlugin.getInstance().getDataFolder() + "/temp", language.getLanguageKey() + "-tmpl.yml");
            FileUtils.copyInputStreamToFile(YourPlugin.class.getClassLoader().getResourceAsStream("languages/" + language.getLanguageKey() + ".yml"), tmpFile);

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

