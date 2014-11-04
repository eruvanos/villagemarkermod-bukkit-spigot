package de.siemering.plugin.villagemarker;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import net.minecraft.server.v1_6_R2.ChunkCoordinates;
import net.minecraft.server.v1_6_R2.MinecraftServer;
import net.minecraft.server.v1_6_R2.Packet250CustomPayload;
import net.minecraft.server.v1_6_R2.Village;
import net.minecraft.server.v1_6_R2.VillageDoor;
import net.minecraft.server.v1_6_R2.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ClientUpdaterV2 extends Thread {

	private boolean stop = false;

	private static int id = 0;

	/**
	 * Setzt die Laufvariable "stop" auf gewünschten Wert. Sobald die Variable auf true gesetzt wird, wird sich das Plugin nach der nächsten Updateverteilung an die Clients beenden.
	 * 
	 * @param stop
	 */
	public void setStop(boolean stop) {
		this.stop = stop;
	}

	/**
	 * Sendet alle 2 Sekunden ein Update der Villageinformationen an alle Clients mit den benötigten Rechten.
	 */
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

	private void sendUpdate() {
		id = id >= 999 ? 0 : id + 1;

		for (int index = 0; index <= 2; index++) {
			try {

				// Player suchen
				Player[] players = Bukkit.getOnlinePlayers();

				// Datenstring erstellen
				String dataString = createDataString(index);
				String dim = indexToDimension(index);

				// Datenstring ggf aufteilen
				int parts = 1;
				ArrayList<String> dataStringList = new ArrayList<String>();

				if (dataString.length() > 10000) {
					parts = (int) Math.ceil(dataString.length() / 10000.);

					for (int xPart = 0; xPart < parts; xPart++) {
						if (xPart + 1 == parts)
							dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + dataString.substring(10000 * xPart, dataString.length()));
						else
							dataStringList.add(id + "<" + dim + ":" + (xPart + 1) + ":" + parts + ">" + dataString.substring(10000 * xPart, 10000 * xPart + 10000));
					}
				} else {
					dataStringList.add(id + "<" + dim + ":" + "1:1>" + dataString);
				}

				// Bukkit.getLogger().log(Level.INFO,"[VillageMarker] " + "Teile Datastring into Parts: " + dataStringList.size());

				// Datenstrings verschicken
				for (String data : dataStringList) {
					ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

					for (int i = 0; i < data.length(); i++) {
						dataOutputStream.writeChar(data.charAt(i));
					}

					// Player benachrichtigen
					for (Player p : players) {
						// Überprüfen der Berechntigungen
						if (p.hasPermission(VillageMarker.VILLAGEPERMISSION)) {
							try {
								Packet250CustomPayload packet = new Packet250CustomPayload("KVM|Data", byteArrayOutputStream.toByteArray());
								((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);

								// Bukkit.getLogger().log(Level.INFO,"Sende:" + data);
								// throw new Exception("Index 1: Size 2");

							} catch (Exception e) {
								VillageMarker.logException(e);
							}
						}
					}
				}
			} catch (Exception e) {
				VillageMarker.logException(e);
			}

		}
	}

	// TODO Code erklären
	private String createDataString(int index) {
		
		//Check if there are really three worlds. Maybe nether or end has been disabled.
		List<WorldServer> worlds = MinecraftServer.getServer().worlds;
		if (index >= 0 && index < worlds.size()) {
			try {
				List<Village> vs = MinecraftServer.getServer().worlds.get(index).villages.getVillages();
				StringBuilder sb = new StringBuilder(indexToDimension(index));
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
				return sb.toString();

			} catch (Exception e) {
				VillageMarker.logException(e);
			}
		}
		return indexToDimension(index);
	}

	private String indexToDimension(int index) {
		switch (index) {
		case 1:
			return "-1";
		case 0:
			return "0";
		case 2:
			return "1";
		default:
			return "0";
		}
	}

}
