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

@LoadCommand
public class ExampleCommand extends Command {
    // The constructor provides the information about the command, such as name, permission, command type, and aliases
    public ExampleCommand() {
        super ("example", new Permission("example.permission", PermissionDefault.OP), CommandType.BOTH);
    }

    @Override
    public LiteralCommandNode<CommandListenerWrapper> createCommand(LiteralArgumentBuilder<CommandListenerWrapper> rootNodeBuilder) {
        // The root node is the command itself. In this case its "example"
        LiteralCommandNode<CommandListenerWrapper> rootNode = rootNodeBuilder
                .executes(commandContext -> {
                    // This code gets executed, when the command gets called.
                    commandContext.getSource().getBukkitSender().sendMessage("This is a cool message! :)");
                    return 1;
                })
                .build();

        // A LiteralCommandNode must be exactly the content, that is provided in the literal() method. Of course, you can add a .executes() (and more) here too.
        LiteralCommandNode<CommandListenerWrapper> confirmNode = LiteralArgumentBuilder.<CommandListenerWrapper>literal("confirm").build();

        // A ArgumentCommandNode is a node, that contains variable content of a specific type (for example a string, a number or a boolean). This content must not have a specific value.
        // ArgumentCommandNodes can be used for custom user inputs, such as username, age, or a custom message
        ArgumentCommandNode<CommandListenerWrapper, String> messageNode = RequiredArgumentBuilder.<CommandListenerWrapper, String>argument("message", StringArgumentType.greedyString())
                .suggests((commandContext, suggestionsBuilder) -> {
                    if ("message".startsWith(suggestionsBuilder.getRemaining().toLowerCase())) {
                        suggestionsBuilder.suggest("message");
                    }
                    return suggestionsBuilder.buildFuture();
                })
                .executes(commandContext -> {
                    commandContext.getSource().getBukkitSender().sendMessage("Your message is: §a" + commandContext.getArgument("message", String.class));
                    return 1;
                })
                .build();

        // This adds the confirmNode after the root node and the message node after the root node
        // So the final command should look like this: /example confirm <message>
        rootNode.addChild(confirmNode);
        confirmNode.addChild(messageNode);

        return rootNode;
    }
}
