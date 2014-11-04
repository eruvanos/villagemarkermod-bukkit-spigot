package de.siemering.plugin.villagemarker;

import com.google.common.base.Charsets;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class ClientUpdaterV2 extends Thread {
    private static int id = 0;

    private boolean stop = false;
    private YamlConfiguration pconfig;

    public ClientUpdaterV2(YamlConfiguration pconfig) {
        super();
        this.pconfig = pconfig;
    }

    /**
     * Setzt die Laufvariable "stop" auf gewuenschten Wert. Sobald die Variable auf true gesetzt wird, wird sich das Plugin nach der naechsten Updateverteilung an die Clients beenden.
     *
     * @param stop
     */
    public void setStop(boolean stop) {
        this.stop = stop;
    }

    /**
     * Sendet alle 2 Sekunden ein Update der Villageinformationen an alle Clients mit den benoetigten Rechten.
     */
    @Override
    public void run() {
        while (!stop) {
            sendUpdateNEW();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }

    private void sendUpdateNEW() {
        id = id >= 999 ? 0 : id + 1;

        Player[] players = Bukkit.getOnlinePlayers();
        Map<UUID, List<String>> dataStringCache = new HashMap<UUID, List<String>>();
        Map<UUID, WorldServer> worldCache = new HashMap<UUID, WorldServer>();

        List<WorldServer> worlds = MinecraftServer.getServer().worlds;
        for (WorldServer world : worlds) {
            worldCache.put(world.getWorld().getUID(), world);
        }

        for (Player player : players) {
            List<String> dataStringList;

            UUID worldUID = player.getWorld().getUID();

            //Datastring erstellen und ggf cachen
            if (dataStringCache.containsKey(worldUID)) {
                dataStringList = dataStringCache.get(worldUID);
            } else {

                dataStringList = generateDataString(worldCache.get(worldUID));
                dataStringCache.put(worldUID, dataStringList);
            }

            //Versende Daten
            sendDataToPlayer(player, dataStringList, worldServerTodim(worldCache.get(worldUID)));
        }

    }


    private List<String> generateDataString(WorldServer world) {
        int dim = worldServerTodim(world);

        //DataString erstellen
        List<Village> vs = world.villages.getVillages();
        StringBuilder sb = new StringBuilder("" + dim);
        for (Village village : vs) {

            sb.append(":" + village.getSize());
            ChunkCoordinates center = village.getCenter();
            sb.append(";" + center.x + "," + center.y + "," + center.z);
            List ds = village.getDoors();
            for (Object obj : ds) {

                VillageDoor d = (VillageDoor) obj;
                sb.append(";" + d.locX + "," + d.locY + "," + d.locZ);
            }
        }

        // Datenstring ggf aufteilen
        String dataString = sb.toString();
        ArrayList<String> dataStringList = new ArrayList<String>();

        if (dataString.length() > 10000) {
            int parts = (int) Math.ceil(dataString.length() / 10000.);

            for (int xPart = 0; xPart < parts; xPart++) {
                if (xPart + 1 == parts)
                    dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + dataString.substring(10000 * xPart, dataString.length()));
                else
                    dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + dataString.substring(10000 * xPart, 10000 * xPart + 10000));
            }
        } else {
            dataStringList.add(id + "<" + dim + ":" + "1:1>" + dataString);
        }

        return dataStringList;
    }

    private void sendDataToPlayer(Player p, List<String> dataStringList, int dim) {


        if (p.hasPermission(VillageMarker.VILLAGEPERMISSION) && pconfig.getBoolean(p.getName(), true)) {
            for (String data : dataStringList) {
                try {
                    PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("KVM|Data", data.getBytes(Charsets.UTF_8));
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                } catch (Exception e) {
                    Logger.logException(e);
                }
            }
        } else {
            String leerInfo = id + "<" + dim + ":" + "1:1>" + dim;
            try {
                PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("KVM|Data", leerInfo.getBytes(Charsets.UTF_8));
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            } catch (Exception e) {
                Logger.logException(e);
            }
        }
    }

    private int worldServerTodim(WorldServer worldServer){
        World.Environment environment = worldServer.getWorld().getEnvironment();

        if(environment == World.Environment.NORMAL){
            return 0;
        } else if(environment == World.Environment.NETHER){
            return -1;
        } else {
            return 1;
        }

    }


}
