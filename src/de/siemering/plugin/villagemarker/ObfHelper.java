package de.siemering.plugin.villagemarker;

import net.minecraft.server.v1_12_R1.Village;
import net.minecraft.server.v1_12_R1.VillageDoor;
import net.minecraft.server.v1_12_R1.BlockPosition;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;

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

    protected static List<Village> getVillages(org.bukkit.World world) {
        // World -> PersistentVillage -> Villages
        return ((CraftWorld) world).getHandle().ak().getVillages();
    }

}
