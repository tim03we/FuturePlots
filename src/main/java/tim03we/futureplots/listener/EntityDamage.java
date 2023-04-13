package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageEvent;
import tim03we.futureplots.utils.Settings;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Player player = (Player) entity;
            if(Settings.levels.contains(player.getLevel().getName())) {
                if(event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    if(!Settings.damage_fall) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}
