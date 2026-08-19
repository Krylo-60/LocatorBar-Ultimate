package pl.fuzjajadrowa.locatorbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponents;
//?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
//? if >=1.20.5 {
import net.minecraft.world.item.component.LodestoneTracker;
//?}
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;
import pl.fuzjajadrowa.locatorbar.waypoint.WaypointData;
import pl.fuzjajadrowa.locatorbar.util.LocatorBarUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public final class LocatorBarHudHelper {
    private LocatorBarHudHelper() {
    }

    public record WaypointMarker(UUID waypointId, float directionYaw, int rgbColor, int index, String symbol, boolean isDeath, float distance, double deltaY) {
    }

    public record PlayerMarker(UUID playerId, Identifier skinTexture, float directionYaw, float alpha, float distance, Integer teamColor, double deltaY) {
    }

    public static List<WaypointMarker> collectWaypointMarkers(Player localPlayer) {
        List<WaypointMarker> markers = new ArrayList<>();
        UUID localPlayerId = localPlayer.getUUID();

        //? if >=1.21.11 {
        for (ItemStack stack : localPlayer.getInventory().getNonEquipmentItems()) {
            addWaypointMarker(markers, stack, localPlayer, localPlayerId);
        }
        ItemStack offhand = localPlayer.getInventory().getItem(Inventory.SLOT_OFFHAND);
        if (!offhand.isEmpty()) {
            addWaypointMarker(markers, offhand, localPlayer, localPlayerId);
        }
        //?} else {
        /*for (ItemStack stack : localPlayer.getInventory().items) {
            addWaypointMarker(markers, stack, localPlayer, localPlayerId);
        }
        for (ItemStack stack : localPlayer.getInventory().offhand) {
            addWaypointMarker(markers, stack, localPlayer, localPlayerId);
        }
        *///?}

        if (LocatorBarConfig.isShowDeathWaypoint()) {
            if (hasRecoveryCompass(localPlayer)) {
                //? if >=26.1 {
                net.minecraft.core.GlobalPos lastDeath = localPlayer.getLastDeathLocation().orElse(null);
                //?} else {
                /*net.minecraft.core.GlobalPos lastDeath = localPlayer.getLastDeathLocation().orElse(null);
                *///?}
                if (lastDeath != null && lastDeath.dimension().equals(localPlayer.level().dimension())) {
                    double dx = lastDeath.pos().getX() + 0.5D - localPlayer.getX();
                    double dz = lastDeath.pos().getZ() + 0.5D - localPlayer.getZ();
                    if (dx * dx + dz * dz >= 1.0E-6D) {
                        float directionYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                        float distance = (float) Math.sqrt(dx * dx + dz * dz);
                        double dy = lastDeath.pos().getY() - localPlayer.getY();
                        markers.add(new WaypointMarker(
                                new UUID(0L, 0L),
                                LocatorBarUtils.wrapTo180(directionYaw),
                                0xFFFFFF,
                                -1,
                                "",
                                true,
                                distance,
                                dy
                        ));
                    }
                }
            }
        }

        String currentWorld = localPlayer.level().dimension().identifier().toString();
        for (pl.fuzjajadrowa.locatorbar.waypoint.CustomWaypointStore.CustomWaypoint wp : pl.fuzjajadrowa.locatorbar.waypoint.CustomWaypointStore.getForWorld(currentWorld)) {
            double dx = wp.x() - localPlayer.getX();
            double dz = wp.z() - localPlayer.getZ();
            if (dx * dx + dz * dz >= 1.0E-6D) {
                float directionYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
                float distance = (float) Math.sqrt(dx * dx + dz * dz);
                double dy = wp.y() - localPlayer.getY();
                markers.add(new WaypointMarker(
                        wp.id(),
                        LocatorBarUtils.wrapTo180(directionYaw),
                        wp.color(),
                        -1,
                        wp.icon(),
                        false,
                        distance,
                        dy
                ));
            }
        }

        //? if >=26.2 {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getConnection() != null) {
            var waypointManager = mc.getConnection().getWaypointManager();
            if (waypointManager != null) {
                float partialTicks = mc.getDeltaTracker().getGameTimeDeltaPartialTick(false);
                net.minecraft.world.waypoints.TrackedWaypoint.Camera camera = new net.minecraft.world.waypoints.TrackedWaypoint.Camera() {
                    @Override
                    public float yaw() { return localPlayer.getYRot(); }
                    @Override
                    public net.minecraft.world.phys.Vec3 position() { return localPlayer.getEyePosition(partialTicks); }
                };

                waypointManager.forEachWaypoint(localPlayer, trackedWaypoint -> {
                    trackedWaypoint.id().right().ifPresent(name -> {
                        double distanceSq = trackedWaypoint.distanceSquared(localPlayer);
                        double yawRad = trackedWaypoint.yawAngleToCamera(localPlayer.level(), camera, entity -> partialTicks);
                        float directionYaw = (float) Math.toDegrees(yawRad);

                        int color = 0xFFFFFF;
                        var icon = trackedWaypoint.icon();
                        if (icon != null && icon.color != null && icon.color.isPresent()) {
                            color = icon.color.get();
                        } else {
                            int argb = (255 << 24) | (name.hashCode() & 0x00FFFFFF);
                            color = LocatorBarUtils.setBrightness(argb, 0.9F);
                        }

                        float distance = (float) Math.sqrt(distanceSq);
                        UUID dummyId = UUID.nameUUIDFromBytes(name.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                        markers.add(new WaypointMarker(
                                dummyId,
                                LocatorBarUtils.wrapTo180(directionYaw),
                                color,
                                -1,
                                "",
                                false,
                                distance,
                                0.0D
                        ));
                    });
                });
            }
        }
        //?}

        markers.sort((m1, m2) -> {
            if (m1.isDeath() && !m2.isDeath()) return 1;
            if (!m1.isDeath() && m2.isDeath()) return -1;
            int idx1 = m1.index() > 0 ? m1.index() : Integer.MAX_VALUE;
            int idx2 = m2.index() > 0 ? m2.index() : Integer.MAX_VALUE;
            if (idx1 != idx2) {
                return Integer.compare(idx1, idx2);
            }
            return m1.waypointId().compareTo(m2.waypointId());
        });
        return markers;
    }

    private static void addWaypointMarker(List<WaypointMarker> markers, ItemStack stack, Player localPlayer, UUID localPlayerId) {
        //? if >=1.20.5 {
        LodestoneTracker tracker = stack.get(DataComponents.LODESTONE_TRACKER);
        if (tracker == null || tracker.target().isEmpty()) {
            return;
        }
        //?} else {
        /*CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("LodestonePos") || !tag.contains("LodestoneDimension")) {
            return;
        }
        *///?}

        UUID owner = WaypointData.getOwner(stack);
        if (owner != null && !owner.equals(localPlayerId)) {
            return;
        }

        //? if >=1.20.5 {
        GlobalPos target = tracker.target().get();
        if (!target.dimension().equals(localPlayer.level().dimension())) {
            return;
        }

        double dx = target.pos().getX() + 0.5D - localPlayer.getX();
        double dz = target.pos().getZ() + 0.5D - localPlayer.getZ();
        //?} else {
        /*CompoundTag posTag = tag.getCompound("LodestonePos");
        BlockPos targetPos = net.minecraft.nbt.NbtUtils.readBlockPos(posTag);
        String dimensionStr = tag.getString("LodestoneDimension");
        if (!dimensionStr.equals(localPlayer.level().dimension().location().toString())) {
            return;
        }

        double dx = targetPos.getX() + 0.5D - localPlayer.getX();
        double dz = targetPos.getZ() + 0.5D - localPlayer.getZ();
        *///?}
        if (dx * dx + dz * dz < 1.0E-6D) {
            return;
        }

        UUID waypointId = WaypointData.getWaypointId(stack);
        if (waypointId == null) {
            return;
        }

        LocatorBarConfig.WaypointConfig config = LocatorBarConfig.getWaypointConfig(waypointId);
        boolean visible = config == null ? !WaypointData.isHidden(stack) : config.visible;
        if (!visible) {
            return;
        }

        int index = WaypointData.getWaypointIndex(stack);
        int color;
        if (config != null) {
            color = config.color;
        } else {
            Integer customColor = WaypointData.getCustomColor(stack);
            color = customColor == null ? colorFromWaypointId(waypointId) : customColor;
        }

        String symbol = config != null ? config.character : WaypointData.getWaypointSymbol(stack);

        float directionYaw = (float) Math.toDegrees(Math.atan2(-dx, dz));
        float distance = (float) Math.sqrt(dx * dx + dz * dz);
        //? if >=1.20.5 {
        double dy = target.pos().getY() - localPlayer.getY();
        //?} else {
        /*double dy = targetPos.getY() - localPlayer.getY();
        *///?}
        markers.add(new WaypointMarker(waypointId, LocatorBarUtils.wrapTo180(directionYaw), color, index, symbol, false, distance, dy));
    }

    public static int colorFromWaypointId(UUID waypointId) {
        return LocatorBarUtils.colorFromId(waypointId, 0.65F, 0.25F, 0.80F, 0.20F);
    }

    public static List<PlayerMarker> collectPlayerMarkers(Player localPlayer) {
        List<PlayerMarker> markers = new ArrayList<>();
        for (PlayerLocatorClient.Marker marker : PlayerLocatorClient.collectMarkers(localPlayer, LocatorBarHudHelper::computePlayerAlpha)) {
            markers.add(new PlayerMarker(
                    marker.playerId(),
                    marker.skinTexture(),
                    LocatorBarUtils.wrapTo180(marker.directionYaw()),
                    marker.alpha(),
                    marker.distance(),
                    marker.teamColor(),
                    marker.deltaY()
            ));
        }
        markers.sort(Comparator.comparingDouble(PlayerMarker::distance));
        return markers;
    }

    public static float computePlayerAlpha(float distance) {
        float fadeStartDistance = LocatorBarConfig.getPlayerMarkerFadeStartDistance();
        float fadeToMinDistance = LocatorBarConfig.getPlayerMarkerFadeToMinDistance();
        float hideDistance = LocatorBarConfig.getPlayerMarkerHideDistance();
        float minAlpha = LocatorBarConfig.getPlayerMarkerMinAlpha();

        if (distance <= fadeStartDistance) {
            return 1.0F;
        }
        if (distance <= fadeToMinDistance) {
            if (fadeToMinDistance <= fadeStartDistance) {
                return minAlpha;
            }
            float progress = (distance - fadeStartDistance) / (fadeToMinDistance - fadeStartDistance);
            float curvedProgress = (float) Math.pow(progress, 1.65D);
            return 1.0F - (curvedProgress * (1.0F - minAlpha));
        }
        if (distance < hideDistance) {
            return minAlpha;
        }
        return 0.0F;
    }

    public static boolean hasRecoveryCompass(Player player) {
        //? if >=1.21.11 {
        for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
            if (stack.is(net.minecraft.world.item.Items.RECOVERY_COMPASS)) {
                return true;
            }
        }
        if (player.getInventory().getItem(Inventory.SLOT_OFFHAND).is(net.minecraft.world.item.Items.RECOVERY_COMPASS)) {
            return true;
        }
        //?} else {
        /*for (ItemStack stack : player.getInventory().items) {
            if (stack.is(net.minecraft.world.item.Items.RECOVERY_COMPASS)) {
                return true;
            }
        }
        for (ItemStack stack : player.getInventory().offhand) {
            if (stack.is(net.minecraft.world.item.Items.RECOVERY_COMPASS)) {
                return true;
            }
        }
        *///?}
        return false;
    }
}