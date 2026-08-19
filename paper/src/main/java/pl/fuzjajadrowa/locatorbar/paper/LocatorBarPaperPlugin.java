package pl.fuzjajadrowa.locatorbar.paper;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class LocatorBarPaperPlugin extends JavaPlugin implements Listener, CommandExecutor {

    public static final String PLAYER_LOCATOR_CHANNEL = "locatorbar:player_locator";
    public static final String SERVER_CONFIG_CHANNEL = "locatorbar:server_config";

    private int syncIntervalTicks;
    private double maxTrackingDistance;
    private boolean showSpectators;
    private boolean respectVanish;
    private boolean enablePlayerMarkers;
    private int maxVisiblePlayers;

    private BukkitTask broadcastTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfiguration();

        // Register outgoing plugin messaging channels
        getServer().getMessenger().registerOutgoingPluginChannel(this, PLAYER_LOCATOR_CHANNEL);
        getServer().getMessenger().registerOutgoingPluginChannel(this, SERVER_CONFIG_CHANNEL);

        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);

        // Register command
        if (getCommand("locatorbar") != null) {
            getCommand("locatorbar").setExecutor(this);
        }

        // Start broadcasting task
        startBroadcastTask();

        getLogger().info("LocatorBar Paper Plugin v" + getDescription().getVersion() + " has been enabled!");
    }

    @Override
    public void onDisable() {
        if (broadcastTask != null && !broadcastTask.isCancelled()) {
            broadcastTask.cancel();
        }
        getServer().getMessenger().unregisterOutgoingPluginChannel(this);
        getLogger().info("LocatorBar Paper Plugin has been disabled.");
    }

    private void loadConfiguration() {
        reloadConfig();
        syncIntervalTicks = getConfig().getInt("sync-interval-ticks", 2);
        maxTrackingDistance = getConfig().getDouble("max-tracking-distance", 256.0);
        showSpectators = getConfig().getBoolean("show-spectators", false);
        respectVanish = getConfig().getBoolean("respect-vanish", true);
        enablePlayerMarkers = getConfig().getBoolean("server-settings.enable-player-markers", true);
        maxVisiblePlayers = getConfig().getInt("server-settings.max-visible-players", 16);
    }

    private void startBroadcastTask() {
        if (broadcastTask != null && !broadcastTask.isCancelled()) {
            broadcastTask.cancel();
        }

        broadcastTask = Bukkit.getScheduler().runTaskTimer(this, this::broadcastPlayerLocations, 20L, syncIntervalTicks);
    }

    private void broadcastPlayerLocations() {
        if (!enablePlayerMarkers) {
            return;
        }

        List<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (onlinePlayers.isEmpty()) {
            return;
        }

        double maxDistSq = maxTrackingDistance > 0 ? (maxTrackingDistance * maxTrackingDistance) : Double.MAX_VALUE;

        for (Player receiver : onlinePlayers) {
            if (!receiver.hasPermission("locatorbar.use")) {
                continue;
            }

            Location receiverLoc = receiver.getLocation();
            List<PlayerEntry> visibleEntries = new ArrayList<>();

            for (Player target : onlinePlayers) {
                if (target.equals(receiver)) {
                    continue;
                }

                if (!target.getWorld().equals(receiver.getWorld())) {
                    continue;
                }

                if (!showSpectators && target.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }

                if (respectVanish && isVanished(target, receiver)) {
                    continue;
                }

                Location targetLoc = target.getLocation();
                double distSq = receiverLoc.distanceSquared(targetLoc);
                if (distSq <= maxDistSq) {
                    visibleEntries.add(new PlayerEntry(target.getUniqueId(), targetLoc.getX(), targetLoc.getZ(), distSq));
                }
            }

            if (visibleEntries.isEmpty()) {
                continue;
            }

            // Sort by nearest
            visibleEntries.sort((a, b) -> Double.compare(a.distSq, b.distSq));
            if (visibleEntries.size() > maxVisiblePlayers) {
                visibleEntries = visibleEntries.subList(0, maxVisiblePlayers);
            }

            byte[] packetData = buildPlayerLocatorPacket(visibleEntries);
            if (packetData != null && packetData.length > 0) {
                receiver.sendPluginMessage(this, PLAYER_LOCATOR_CHANNEL, packetData);
            }
        }
    }

    private boolean isVanished(Player target, Player receiver) {
        if (receiver.hasPermission("locatorbar.vanish.bypass")) {
            return false;
        }
        if (!receiver.canSee(target)) {
            return true;
        }
        if (target.hasMetadata("vanished") && !target.getMetadata("vanished").isEmpty()) {
            return target.getMetadata("vanished").get(0).asBoolean();
        }
        return false;
    }

    private byte[] buildPlayerLocatorPacket(List<PlayerEntry> entries) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             DataOutputStream dos = new DataOutputStream(baos)) {

            writeVarInt(dos, entries.size());
            for (PlayerEntry entry : entries) {
                dos.writeLong(entry.uuid.getMostSignificantBits());
                dos.writeLong(entry.uuid.getLeastSignificantBits());
                dos.writeDouble(entry.x);
                dos.writeDouble(entry.z);
            }
            dos.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            getLogger().warning("Failed to build player locator packet: " + e.getMessage());
            return null;
        }
    }

    private static void writeVarInt(DataOutputStream out, int value) throws IOException {
        while ((value & -128) != 0) {
            out.writeByte((value & 127) | 128);
            value >>>= 7;
        }
        out.writeByte(value & 127);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Automatically sync configuration when player joins
        Player player = event.getPlayer();
        Bukkit.getScheduler().runTaskLater(this, () -> {
            if (player.isOnline()) {
                // Send initial locator packet or sync
            }
        }, 10L);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!sender.hasPermission("locatorbar.admin")) {
                sender.sendMessage("§cYou do not have permission to execute this command.");
                return true;
            }
            loadConfiguration();
            startBroadcastTask();
            sender.sendMessage("§a[LocatorBar] Configuration reloaded successfully!");
            return true;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("status")) {
            sender.sendMessage("§e=== LocatorBar Server Status ===");
            sender.sendMessage("§7Sync Interval: §f" + syncIntervalTicks + " ticks");
            sender.sendMessage("§7Max Distance: §f" + (maxTrackingDistance < 0 ? "Unlimited" : maxTrackingDistance + " blocks"));
            sender.sendMessage("§7Online Players: §f" + Bukkit.getOnlinePlayers().size());
            return true;
        }

        sender.sendMessage("§e=== LocatorBar Paper v" + getDescription().getVersion() + " ===");
        sender.sendMessage("§7Use §f/locatorbar reload §7to reload configuration.");
        sender.sendMessage("§7Use §f/locatorbar status §7to view broadcast status.");
        return true;
    }

    private record PlayerEntry(UUID uuid, double x, double z, double distSq) {
    }
}
