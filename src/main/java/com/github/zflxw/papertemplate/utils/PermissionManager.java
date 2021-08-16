package com.github.zflxw.papertemplate.utils;

import com.github.zflxw.papertemplate.YourPlugin;
import com.github.zflxw.papertemplate.utils.commands.Command;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.commands.CommandListenerWrapper;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PermissionManager {
    private final HashMap<UUID, PermissionAttachment> permissionAttachments = new HashMap<>();

    public void loadPermissions(Player player) {
        PermissionAttachment attachment;
        if (permissionAttachments.containsKey(player.getUniqueId())) {
            attachment = permissionAttachments.get(player.getUniqueId());

            for (Map.Entry<String, Boolean> permission : attachment.getPermissions().entrySet()) {
                attachment.setPermission(permission.getKey(), false);
            }
        } else {
            attachment = player.addAttachment(YourPlugin.getInstance());
            permissionAttachments.put(player.getUniqueId(), attachment);
        }

        for (CommandNode<CommandListenerWrapper> command : Command.commands) {
            attachment.setPermission("bukkit." + command.getName(), true);
            attachment.setPermission("minecraft.command." + command.getName(), true);
        }

        player.recalculatePermissions();
        player.updateCommands();
    }
}
