package tim03we.futureplots.listener;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import tim03we.futureplots.utils.Settings;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if(entity instanceof Player) {
            Player player = (Player) entity;
            Entity damager = event.getDamager();
            if(damager instanceof Player) {
                Player killer = (Player) damager;
                if(Settings.damage_from_players) {
                    event.setCancelled(true);
                }
            } else {
                if(Settings.damage_from_entitys) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
