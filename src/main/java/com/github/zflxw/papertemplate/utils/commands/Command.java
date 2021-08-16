package com.github.zflxw.papertemplate.utils.commands;

import com.github.zflxw.papertemplate.YourPlugin;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.v1_17_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Command {
    public static final List<CommandNode<CommandListenerWrapper>> commands = new ArrayList<>();
    private final String commandName;
    private final Permission permission;
    private final CommandType commandType;
    private final List<String> aliases;

    /**
     * initialize the command object
     * @param commandName the name of the command
     * @param permission the permission you need to execute the command
     * @param aliases aliases (alternative names) of the command
     */
    public Command(String commandName, Permission permission, CommandType commandType, String... aliases) {
        this.commandName = commandName;
        this.permission = permission;
        this.commandType = commandType;
        this.aliases = Arrays.asList(aliases);
    }

    /**
     * this method creates and executes the command when called.
     * @param rootNodeBuilder the root node of the command (the command itself)
     * @return a command node of the command
     */
    public abstract LiteralCommandNode<CommandListenerWrapper> createCommand(LiteralArgumentBuilder<CommandListenerWrapper> rootNodeBuilder);

    public void register() {
        CommandDispatcher<CommandListenerWrapper> commandDispatcher = ((CraftServer) Bukkit.getServer()).getServer().getCommandDispatcher().a();
        LiteralArgumentBuilder<CommandListenerWrapper> commandBuilder = LiteralArgumentBuilder.<CommandListenerWrapper>literal(commandName)
                .requires(requirement -> {
                    switch (commandType) {
                        case CONSOLE_ONLY -> {
                            return (requirement.getBukkitSender() instanceof ConsoleCommandSender) && requirement.getBukkitSender().hasPermission(permission);
                        }

                        case PLAYER_ONLY -> {
                            return (requirement.getBukkitSender() instanceof Player) && requirement.getBukkitSender().hasPermission(permission);
                        }

                        case BOTH -> {
                            return requirement.getBukkitSender().hasPermission(permission);
                        }

                        default -> {
                            return false;
                        }
                    }
                });

        LiteralCommandNode<CommandListenerWrapper> commandNode = createCommand(commandBuilder);

        commandDispatcher.getRoot().addChild(commandNode);
        commands.add(commandNode);

        for (String alias : aliases) {
            commandDispatcher.getRoot().addChild(createNode(alias, commandNode));
            commandDispatcher.getRoot().addChild(createNode(YourPlugin.NAMESPACE + ":" + commandName, commandNode));
        }
    }

    /**
     * @return the command name
     */
    public String getCommandName() { return this.commandName; }

    /**
     * @return the command permission
     */
    public Permission getPermission() { return this.permission; }

    /**
     * @return the command type
     */
    public CommandType getCommandType() { return this.commandType; }

    /**
     * @return the command aliases
     */
    public List<String> getAliases() { return this.aliases; }

    /**
     * this method creates a command node for you, so you do not have to mess up with that stuff
     * @return a command node with the given parameters
     */
    private CommandNode<CommandListenerWrapper> createNode(String commandName, CommandNode<CommandListenerWrapper> commandNode) {
        LiteralArgumentBuilder.<CommandListenerWrapper>literal(commandName)
                .redirect(commandNode)
                .executes(command -> commandNode.getCommand().run(command))
                .requires(requirement -> commandNode.getRequirement().test(requirement))
                .build();

        commandNode.getChildren().forEach(commandNode::addChild);
        return commandNode;
    }
}
