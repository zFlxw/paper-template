package com.github.zflxw.papertemplate;

import com.github.zflxw.papertemplate.utils.PermissionManager;
import com.github.zflxw.papertemplate.utils.commands.Command;
import com.github.zflxw.papertemplate.utils.commands.LoadCommand;
import com.github.zflxw.papertemplate.utils.listener.LoadListener;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.reflections8.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public final class YourPlugin extends JavaPlugin {
    /**
     * the namespace is used to identify the command, if there are multiple plugins with the same command.
     * You can call your command specifically by using /<namespace>:<command> (args...)
     */
    public static final String NAMESPACE = "yournamespace";

    private static YourPlugin instance;
    private PermissionManager permissionManager;

    @Override
    public void onEnable() {
        instance = this;

        try {
            this.registerCommands();
            this.registerListener();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        this.permissionManager = new PermissionManager();
    }

    @Override
    public void onDisable() {

    }

    public static YourPlugin getInstance() { return instance; }

    public PermissionManager getPermissionManager() { return this.permissionManager; }

    /**
     * registers all classes annotated with "LoadCommand"
     */
    private void registerCommands() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        // This string has to be your package path, where your command classes are located in.
        Reflections reflections = new Reflections("com.github.zflxw.papertemplate.commands");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(LoadCommand.class)) {
            if (!(clazz.getSuperclass() == Command.class)) {
                continue;
            }

            if (Arrays.stream(clazz.getConstructors()).anyMatch(predicate -> predicate.getParameterCount() == 0)) {
                Command command = (Command) Arrays.stream(clazz.getConstructors()).filter(predicate -> predicate.getParameterCount() == 0)
                        .findFirst().get().newInstance();

                command.register();
            }
        }
    }

    /**
     * registers all classes annotated with "LoadListener"
     */
    private void registerListener() throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("com.github.zflxw.papertemplate.listener");
        for (Class<?> clazz : reflections.getTypesAnnotatedWith(LoadListener.class)) {
            Object instance = clazz.getDeclaredConstructor().newInstance();

            if (!(instance instanceof Listener)) {
                continue;
            }

            Bukkit.getPluginManager().registerEvents((Listener) instance, this);
        }
    }
}
