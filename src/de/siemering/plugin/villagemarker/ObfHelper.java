package de.siemering.plugin.villagemarker;

import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.MinecraftServer;
import net.minecraft.server.v1_8_R2.Village;
import net.minecraft.server.v1_8_R2.VillageDoor;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;

import java.util.List;

/**
 * Created by guntherdw on 13/01/15.
 */
public class ObfHelper {

    protected static int villageGetSize(Village v) {
        // Village e -> size
        return v.b();
    }

    protected static BlockPosition villageGetCenter(Village v) {
        // Village d -> center
        return v.a();
    }

    protected static List<VillageDoor> villageGetDoors(Village v) {
        // Village b -> doors
        return v.f();
    }

    protected static BlockPosition villageDoorsgetBlockPosition(VillageDoor vd) {
        // VillageDoor a -> position
        return vd.d();
    }

    protected static List<Village> getVillages(int index) {
        // World -> PersistentVillage -> Villages
        return MinecraftServer.getServer().worlds.get(index).ae().getVillages();
    }

    protected static List<Village> getVillages(org.bukkit.World world) {
        // World -> PersistentVillage -> Villages
        return ((CraftWorld) world).getHandle().ae().getVillages();
    }

}