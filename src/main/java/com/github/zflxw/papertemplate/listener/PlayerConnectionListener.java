package com.github.zflxw.papertemplate.listener;

import com.github.zflxw.papertemplate.YourPlugin;
import com.github.zflxw.papertemplate.utils.listener.LoadListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@LoadListener
public class PlayerConnectionListener implements Listener {
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        YourPlugin.getInstance().getPermissionManager().loadPermissions(event.getPlayer());
    }
}
