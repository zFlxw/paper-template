package com.github.zflxw.papertemplate.commands;

import com.github.zflxw.papertemplate.utils.commands.Command;
import com.github.zflxw.papertemplate.utils.commands.CommandType;
import com.github.zflxw.papertemplate.utils.commands.LoadCommand;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.Locale;

@LoadCommand
public class ExampleCommand extends Command {
    public ExampleCommand() {
        super ("example", new Permission("example.permission", PermissionDefault.OP), CommandType.BOTH);
    }
    @Override
    public LiteralCommandNode<CommandListenerWrapper> createCommand(LiteralArgumentBuilder<CommandListenerWrapper> rootNodeBuilder) {
        LiteralCommandNode<CommandListenerWrapper> rootNode = rootNodeBuilder
                .executes(commandContext -> {
                    commandContext.getSource().getBukkitSender().sendMessage("This is a cool message! :)");
                    return 1;
                })
                .build();

        ArgumentCommandNode<CommandListenerWrapper, String> messageNode = RequiredArgumentBuilder.<CommandListenerWrapper, String>argument("message", StringArgumentType.greedyString())
                .suggests((commandContext, suggestionsBuilder) -> {
                    if ("message".startsWith(suggestionsBuilder.getRemaining().toLowerCase())) {
                        suggestionsBuilder.suggest("message");
                        suggestionsBuilder.suggest("message1");
                    }
                    return suggestionsBuilder.buildFuture();
                })
                .executes(commandContext -> {
                    commandContext.getSource().getBukkitSender().sendMessage("Your message is: §a" + commandContext.getArgument("message", String.class));
                    return 1;
                })
                .build();

        rootNode.addChild(messageNode);

        return rootNode;
    }
}
