package pl.fuzjajadrowa.locatorbar.radar;

import net.minecraft.client.MinecraftClient;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class BiomeRadar {
    private static boolean enabled = true;

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean e) { enabled = e; }
    public static void toggle() { enabled = !enabled; }

    public static String getCurrentBiomeAndWeather() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null || client.world == null) return "";

        BlockPos pos = client.player.getBlockPos();
        RegistryEntry<Biome> biomeEntry = client.world.getBiome(pos);

        String biomeName = "Unknown";
        if (biomeEntry != null && biomeEntry.getKey().isPresent()) {
            String raw = biomeEntry.getKey().get().getValue().getPath();
            biomeName = formatBiomeName(raw);
        }

        // Weather indicator
        String weather = "☀️";
        if (client.world.isThundering()) {
            weather = "⚡ Storm";
        } else if (client.world.isRaining()) {
            weather = "🌧️ Rain";
        } else if (client.world.getRegistryKey() == World.NETHER) {
            weather = "🔥 Nether";
        } else if (client.world.getRegistryKey() == World.END) {
            weather = "🌌 Void";
        }

        return "§7" + biomeName + " §8| §f" + weather;
    }

    private static String formatBiomeName(String raw) {
        if (raw == null || raw.isEmpty()) return "Unknown";
        String[] parts = raw.split("_");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.length() > 0) {
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(" ");
            }
        }
        return sb.toString().trim();
    }
}
