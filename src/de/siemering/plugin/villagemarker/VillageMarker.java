package de.siemering.plugin.villagemarker;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.logging.Level;

public class VillageMarker extends JavaPlugin {

    public static VillageMarker instance;

    private ClientUpdaterV2 updater;
	
	protected static final String VILLAGEPERMISSION = "villagemarker";

    public static final String POLL_CHANNEL = "KVM|Poll";
    public static final String DATA_CHANNEL = "KVM|Data";
    public static final String DATA_CHANNEL_COMPRESSED = "KVM|DataComp";

    public static final String ANSWER_CHANNEL = "KVM|Answer";

    protected VillageMarkerListener listener = new VillageMarkerListener();

	
	@Override
	public void onEnable() {
		super.onEnable();

        // this.getServer().getMessenger().registerIncomingPluginChannel(this, DATA_CHANNEL, listener);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, DATA_CHANNEL);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, DATA_CHANNEL_COMPRESSED);
        instance = this;

		updater = new ClientUpdaterV2();
		updater.start();
		
	}
	
	
	@Override
	public void onDisable() {
		updater.setStop(true);
		
		try {
			updater.join();
		} catch (InterruptedException e) {
			getLogger().log(Level.WARNING, e.getMessage());
		}
		super.onDisable();
	}

    class VillageMarkerListener implements PluginMessageListener {

        /**
         * A method that will be thrown when a PluginMessageSource sends a plugin
         * message on a registered channel.
         *
         * @param channel Channel that the message was sent through.
         * @param player  Source of the message.
         * @param message The raw message that was sent.
         */
        @Override
        public void onPluginMessageReceived(String channel, Player player, byte[] message) {
            // Do nothing?
        }
    }
	
	
}
