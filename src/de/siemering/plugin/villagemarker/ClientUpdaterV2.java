package de.siemering.plugin.villagemarker;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.zip.Deflater;

import com.google.common.base.Charsets;

import net.minecraft.server.v1_9_R2.BlockPosition;
import net.minecraft.server.v1_9_R2.Village;
import net.minecraft.server.v1_9_R2.VillageDoor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class ClientUpdaterV2 extends Thread {

    private boolean stop = false;
    private static int id = 0;
    private Map<String, String> worlds = new HashMap<String, String>();


    public void setStop(boolean stop) {
        this.stop = stop;
    }

    @Override
    public void run() {
        while (!stop) {
            sendUpdate();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }
    }

    private int getDimByEnv(World world) {
        switch (world.getEnvironment()) {
            case NORMAL:  return  0;
            case NETHER:  return -1;
            case THE_END: return  1;
            default:      return  0;
        }
    }

    private void sendUpdate() {
        id = id >= 999 ? 0 : id + 1;

        int maxSize;

        for(World world : Bukkit.getWorlds()) {
            worlds.put(world.getName(), createDataString(world));
        }

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getListeningPluginChannels().contains(VillageMarker.DATA_CHANNEL)
                && !p.getListeningPluginChannels().contains(VillageMarker.DATA_CHANNEL_COMPRESSED))
                continue;

            boolean sendCompressed = p.getListeningPluginChannels().contains(VillageMarker.DATA_CHANNEL_COMPRESSED);
            World w = p.getWorld();
            String toSend = worlds.get(w.getName());
            ArrayList<String> dataStringList = new ArrayList<String>();
            int parts;
            int dim = getDimByEnv(w);

            if(sendCompressed) maxSize = 150000;
            else               maxSize = 10000;

            if (toSend.length() > maxSize) {
                parts = (int) Math.ceil(toSend.length() / maxSize);

                for (int xPart = 0; xPart < parts; xPart++) {
                    if (xPart + 1 == parts)
                        dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + toSend.substring(maxSize * xPart, toSend.length()));
                    else
                        dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + toSend.substring(maxSize * xPart, maxSize * xPart + maxSize));
                }
            } else {
                dataStringList.add(id + "<" + dim + ":" + "1:1>" + toSend);
            }

            for (String data : dataStringList) {
                if (sendCompressed) {
                    try {
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

                        VillageGZIPOutputStream gzipOutputStream = new VillageGZIPOutputStream(byteArrayOutputStream);
                        gzipOutputStream.setLevel(Deflater.BEST_COMPRESSION);
                        gzipOutputStream.write(data.getBytes(Charsets.UTF_8));
                        gzipOutputStream.close();

                        byte[] compressedData = byteArrayOutputStream.toByteArray();
                        byteArrayOutputStream.close();
                        p.sendPluginMessage(VillageMarker.instance, VillageMarker.DATA_CHANNEL_COMPRESSED, compressedData);
                    } catch(Exception e) { }
                } else {
                    p.sendPluginMessage(VillageMarker.instance, VillageMarker.DATA_CHANNEL, data.getBytes(Charsets.UTF_8));
                }
            }
        }

    }

    private String createDataString(World world) {
        try {
            List<Village> vs = ObfHelper.getVillages(world);

            StringBuilder sb = new StringBuilder(Integer.toString(getDimByEnv(world)));
            for (Village village : vs) {

                sb.append(":" + ObfHelper.villageGetSize(village));
                BlockPosition center = ObfHelper.villageGetCenter(village);

                sb.append(";" + center.getX() + "," + center.getY() + "," + center.getZ());

                List<VillageDoor> ds = ObfHelper.villageGetDoors(village);
                for (VillageDoor d : ds) {
                    BlockPosition dp = ObfHelper.villageDoorsgetBlockPosition(d);
                    sb.append(";" + dp.getX() + "," + dp.getY() + "," + dp.getZ());
                }
            }
            return sb.toString();

        } catch (Exception e) {
            Bukkit.getLogger().log(Level.WARNING, e.getMessage());
            e.printStackTrace();
        }
        // If all else fails

        return String.valueOf(getDimByEnv(world));
    }
}
