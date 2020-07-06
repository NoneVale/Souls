package net.nighthawkempires.souls.util;

import net.nighthawkempires.core.particle.ParticleEffect;
import net.nighthawkempires.souls.SoulsPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class ParticleUtil {

    public static void coneEffect(final Player player){
        BukkitRunnable runnable = new BukkitRunnable() {
            double phi = 0;
            public void run() {
                phi = phi + Math.PI/8;
                double x, y, z;

                Location location1 = player.getLocation();
                for (double t = 0; t <= 2*Math.PI; t = t + Math.PI/16){
                    for (double i = 0; i <= 1; i = i + 1){
                        x = 0.4*(2*Math.PI-t)*0.5*cos(t + phi + i*Math.PI);
                        y = 0.5*t;
                        z = 0.4*(2*Math.PI-t)*0.5*sin(t + phi + i*Math.PI);
                        location1.add(x, y, z);
                        ParticleEffect.ENCHANTMENT_TABLE.display(0, 0, 0, 0, 2, location1, 20.0);
                        location1.subtract(x,y,z);
                    }
                }

                if(phi > 10*Math.PI){
                    this.cancel();
                }
            }
        };
        runnable.runTaskTimer(SoulsPlugin.getPlugin(), 0, 3);
        BukkitRunnable canceller = new BukkitRunnable() {
            public void run() {
                runnable.cancel();
            }
        };
        canceller.runTaskLater(SoulsPlugin.getPlugin(), 100);
    }
}
