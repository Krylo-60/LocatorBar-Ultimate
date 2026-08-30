package pl.fuzjajadrowa.locatorbar.radar;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

public class BiomeRadar {
    private static boolean enabled = true;

    public static boolean isEnabled() { return enabled; }
    public static void setEnabled(boolean e) { enabled = e; }
    public static void toggle() { enabled = !enabled; }

    public static String getCurrentBiomeAndWeather() {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.player == null || client.level == null) return "";

        BlockPos pos = client.player.blockPosition();
        Holder<Biome> biomeEntry = client.level.getBiome(pos);

        String biomeName = "Unknown";
        if (biomeEntry != null && biomeEntry.unwrapKey().isPresent()) {
            String keyStr = biomeEntry.unwrapKey().get().toString();
            String raw = keyStr.contains("/") ? keyStr.substring(keyStr.lastIndexOf('/') + 1).replace("]", "").trim() : keyStr;
            if (raw.contains(":")) raw = raw.substring(raw.indexOf(':') + 1);
            biomeName = formatBiomeName(raw);
        }

        // Weather indicator
        String weather = "☀️";
        if (client.level.isThundering()) {
            weather = "⚡ Storm";
        } else if (client.level.isRaining()) {
            weather = "🌧️ Rain";
        } else if (client.level.dimension() == Level.NETHER) {
            weather = "🔥 Nether";
        } else if (client.level.dimension() == Level.END) {
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
