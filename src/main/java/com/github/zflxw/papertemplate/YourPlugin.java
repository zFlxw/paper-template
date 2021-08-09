package com.github.zflxw.papertemplate;

import com.github.zflxw.papertemplate.utils.commands.Command;
import com.github.zflxw.papertemplate.utils.commands.LoadCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class YourPlugin extends JavaPlugin {
    public static final String NAMESPACE = "yournamespace";

    private static YourPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        try {
            this.registerCommands();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onDisable() {

    }

    public static YourPlugin getInstance() { return instance; }

    private void registerCommands() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.github.zflxw.papertemplate.commands");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(LoadCommand.class)) {
            if (Arrays.stream(clazz.getConstructors()).anyMatch(predicate -> predicate.getParameterCount() == 0)) {
                Command command = (Command) Arrays.stream(clazz.getConstructors()).filter(predicate -> predicate.getParameterCount() == 0)
                        .findFirst().get().newInstance();

                command.register();
            }
        }
    }
}
