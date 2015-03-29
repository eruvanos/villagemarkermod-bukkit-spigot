package de.siemering.plugin.villagemarker;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.*;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.util.*;
import java.util.logging.Level;

import static de.siemering.plugin.villagemarker.ReflectionUtils.getField;
import static de.siemering.plugin.villagemarker.ReflectionUtils.makeAccessible;

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
                //
            }
        }
    }

    private void sendUpdateNEW() {
        id = id >= 999 ? 0 : id + 1;

        Collection<? extends Player> players = Bukkit.getOnlinePlayers();
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

    private static List<Village> getVillages(WorldServer world) {
        Object o = null;
        try {
            Field villagesF = getField(WorldServer.class, "villages");
            makeAccessible(villagesF);
            o = villagesF.get(world);
        } catch (Exception e) {
            Logger.logException(e);
        }

        if(o == null){
            return new ArrayList<Village>();
        }else{
            return ((PersistentVillage) o).getVillages();
        }
    }

    private List<String> generateDataString(WorldServer world) {
        int dim = worldServerTodim(world);

        //DataString erstellen
        List<Village> vs = getVillages(world);
        StringBuilder sb = new StringBuilder("" + dim);
        for (Village village : vs) {

            sb.append(":").append(village.c());
             BlockPosition center = village.a();
            sb.append(";").append(center.getX()).append(",").append(center.getY()).append(",").append(center.getZ());
            List ds = village.f();
            for (Object obj : ds) {

                VillageDoor d = (VillageDoor) obj;
                //.d() oder .e()
                sb.append(";").append(d.d().getX()).append(",").append(d.d().getY()).append(",").append(d.d().getZ());
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
                    ByteBuf byteBuf = ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, CharBuffer.wrap(data), Charsets.UTF_8);
                    PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuf);

                    PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("KVM|Data", packetDataSerializer);
                    ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                } catch (Exception e) {
                    Logger.logException(e);
                }
            }
        } else {
            String leerInfo = id + "<" + dim + ":" + "1:1>" + dim;
            try {
                ByteBuf byteBuf = ByteBufUtil.encodeString(ByteBufAllocator.DEFAULT, CharBuffer.wrap(leerInfo), Charsets.UTF_8);
                PacketDataSerializer packetDataSerializer = new PacketDataSerializer(byteBuf);

                PacketPlayOutCustomPayload packet = new PacketPlayOutCustomPayload("KVM|Data", packetDataSerializer);
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
