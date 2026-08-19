package pl.fuzjajadrowa.locatorbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import pl.fuzjajadrowa.locatorbar.LocatorBar;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.CoordinatesFormat;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.DaysDisplayOrder;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.PlayerMarkerType;
import pl.fuzjajadrowa.locatorbar.util.LocatorBarUtils;

import java.util.List;

public final class ReworkedLocatorBarHudRenderer {
    private static final Identifier LOCATOR_BAR_BACKGROUND = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/reworked_locator_bar_background.png"
    );
    private static final Identifier NORTH = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/north.png"
    );
    private static final Identifier SOUTH = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/south.png"
    );
    private static final Identifier EAST = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/east.png"
    );
    private static final Identifier WEST = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/west.png"
    );
    private static final Identifier WAYPOINT = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/waypoint.png"
    );
    private static final Identifier DEATH_WAYPOINT = Identifier.fromNamespaceAndPath(
            LocatorBar.MOD_ID,
            "textures/gui/death_waypoint.png"
    );
    private static final int BAR_TEXTURE_WIDTH = 102;
    private static final int BAR_TEXTURE_HEIGHT = 10;
    private static final int ICON_TEXTURE_SIZE = 36;
    private static final int ICON_MARGIN = 4;
    private static final int ICON_DOT_SIZE = ICON_TEXTURE_SIZE - (ICON_MARGIN * 2);
    private static final int BASE_DIRECTION_MARKER_SIZE = 12;
    private static final int BASE_DIRECTION_OVERFLOW = 2;
    private static final int BASE_PLAYER_HEAD_MARKER_SIZE = 12;
    private static final int BASE_PLAYER_HEAD_OVERFLOW = 2;
    private static final int WAYPOINT_TEXTURE_SIZE = 36;
    private static final int BASE_WAYPOINT_MARKER_SIZE = 14;
    private static final float WAYPOINT_TEXT_SCALE = 0.75F;

    private static final Identifier DOT_TEXTURE = Identifier.fromNamespaceAndPath(LocatorBar.MOD_ID, "textures/gui/locator_bar_dot.png");

    private ReworkedLocatorBarHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor guiGraphics) {
        if (!LocatorBarConfig.isEnabled()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        //? if >=26.2 {
        if (minecraft.gui.hud.isHidden()) {
            return;
        }
        //?} else {
        /*if (minecraft.options.hideGui) {
            return;
        }
        *///?}

        float scale = LocatorBarConfig.getScale();
        float halfViewAngle = LocatorBarConfig.getViewAngle() / 2.0F;
        int scaledBarWidth = Math.max(1, Math.round(BAR_TEXTURE_WIDTH * scale));
        int scaledBarHeight = Math.max(1, Math.round(BAR_TEXTURE_HEIGHT * scale));
        int directionMarkerSize = Math.max(4, Math.round(BASE_DIRECTION_MARKER_SIZE * LocatorBarConfig.getWorldDirectionsScale()));
        int playerHeadMarkerSize = Math.max(6, Math.round(BASE_PLAYER_HEAD_MARKER_SIZE * LocatorBarConfig.getPlayerMarkersScale()));
        int waypointMarkerSize = Math.max(6, Math.round(BASE_WAYPOINT_MARKER_SIZE * LocatorBarConfig.getWaypointsScale()));
        int waypointTopOverflow = Math.round(waypointMarkerSize * (8.0F / WAYPOINT_TEXTURE_SIZE));
        int waypointBottomOverflow = Math.round(waypointMarkerSize * (4.0F / WAYPOINT_TEXTURE_SIZE));
        int directionOverflow = Math.max(BASE_DIRECTION_OVERFLOW, ((directionMarkerSize - BAR_TEXTURE_HEIGHT) / 2) + BASE_DIRECTION_OVERFLOW);
        int playerHeadOverflow = Math.max(BASE_PLAYER_HEAD_OVERFLOW, ((playerHeadMarkerSize - BAR_TEXTURE_HEIGHT) / 2) + BASE_PLAYER_HEAD_OVERFLOW);

        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int x = ((screenWidth - scaledBarWidth) / 2) + LocatorBarConfig.getCustomOffsetX();
        int y = 5 + LocatorBarConfig.getCustomOffsetY();

        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        float yaw = LocatorBarUtils.wrapTo180(player.getYRot());
        float centerX = BAR_TEXTURE_WIDTH / 2.0F;
        int directionMarkerY = -directionOverflow + ((BAR_TEXTURE_HEIGHT + (directionOverflow * 2) - directionMarkerSize) / 2);
        int headMarkerY = -playerHeadOverflow + ((BAR_TEXTURE_HEIGHT + (playerHeadOverflow * 2) - playerHeadMarkerSize) / 2);
        int waypointMarkerY = -waypointTopOverflow;
        int scissorOverflow = Math.max(
                Math.max(directionOverflow, playerHeadOverflow),
                Math.max(waypointTopOverflow, waypointBottomOverflow)
        );

        int scissorTop = y - Math.round(scissorOverflow * scale);
        int scissorBottom = y + Math.round((BAR_TEXTURE_HEIGHT + scissorOverflow) * scale);
        guiGraphics.enableScissor(x, scissorTop, x + scaledBarWidth, scissorBottom);
        RenderCompat.push(guiGraphics);
        RenderCompat.translate(guiGraphics, x, y);
        RenderCompat.scale(guiGraphics, scale, scale);

        var theme = LocatorBarConfig.getTheme();
        if (theme == pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.BarTheme.DEFAULT) {
            RenderCompat.blit(guiGraphics, LOCATOR_BAR_BACKGROUND, 0, 0, 0, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT);
        } else if (theme == pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.BarTheme.CHROMA_WAVE) {
            int chroma1 = getChromaColor(0.0F);
            int chroma2 = getChromaColor(0.5F);
            guiGraphics.fill(0, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, 0xCC111122);
            guiGraphics.fill(0, 0, BAR_TEXTURE_WIDTH, 1, chroma1);
            guiGraphics.fill(0, BAR_TEXTURE_HEIGHT - 1, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, chroma2);
            RenderCompat.blitTinted(guiGraphics, LOCATOR_BAR_BACKGROUND, 0, 0, 0, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, chroma1);
        } else if (theme == pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.BarTheme.OLED_MINIMAL) {
            // Minimalist borderless style
        } else {
            guiGraphics.fill(0, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, theme.backgroundColor());
            guiGraphics.fill(0, 0, BAR_TEXTURE_WIDTH, 1, theme.accentColor());
            guiGraphics.fill(0, BAR_TEXTURE_HEIGHT - 1, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, theme.accentColor());
            guiGraphics.fill(0, 0, 1, BAR_TEXTURE_HEIGHT, theme.accentColor());
            guiGraphics.fill(BAR_TEXTURE_WIDTH - 1, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, theme.accentColor());
            RenderCompat.blitTinted(guiGraphics, LOCATOR_BAR_BACKGROUND, 0, 0, 0, 0, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, BAR_TEXTURE_WIDTH, BAR_TEXTURE_HEIGHT, theme.accentColor());
        }

        renderDegreeTicks(guiGraphics, yaw, halfViewAngle, centerX);

        if (LocatorBarConfig.isShowWorldDirections()) {
            renderDirectionMarker(guiGraphics, NORTH, 180.0F, yaw, halfViewAngle, centerX, directionMarkerY, directionMarkerSize);
            renderDirectionMarker(guiGraphics, SOUTH, 0.0F, yaw, halfViewAngle, centerX, directionMarkerY, directionMarkerSize);
            renderDirectionMarker(guiGraphics, EAST, -90.0F, yaw, halfViewAngle, centerX, directionMarkerY, directionMarkerSize);
            renderDirectionMarker(guiGraphics, WEST, 90.0F, yaw, halfViewAngle, centerX, directionMarkerY, directionMarkerSize);
        }

        if (LocatorBarConfig.isShowWaypoints()) {
            int fallbackIndex = 1;
            int renderedWaypoints = 0;
            int maxWaypoints = LocatorBarConfig.getMaxVisibleWaypoints();
            for (LocatorBarHudHelper.WaypointMarker marker : LocatorBarHudHelper.collectWaypointMarkers(player)) {
                String displayText = marker.symbol();
                boolean defaultIndexText = displayText == null || displayText.isEmpty();
                if (displayText == null || displayText.isEmpty()) {
                    int displayNumber = marker.index() > 0 ? marker.index() : fallbackIndex++;
                    displayText = Integer.toString(displayNumber);
                }
                if (renderWaypointMarker(
                        guiGraphics,
                        marker,
                        displayText,
                        yaw,
                        halfViewAngle,
                        centerX,
                        waypointMarkerY,
                        waypointMarkerSize,
                        defaultIndexText
                )) {
                    renderedWaypoints++;
                    if (renderedWaypoints >= maxWaypoints) {
                        break;
                    }
                }
            }
        }

        if (LocatorBarConfig.getPlayerMarkerType() != PlayerMarkerType.OFF) {
            List<LocatorBarHudHelper.PlayerMarker> markers = LocatorBarHudHelper.collectPlayerMarkers(player);
            int maxVisible = Math.min(markers.size(), LocatorBarConfig.getMaxVisiblePlayers());
            for (int i = 0; i < maxVisible; i++) {
                renderPlayerMarker(
                        guiGraphics,
                        markers.get(i),
                        yaw,
                        halfViewAngle,
                        centerX,
                        headMarkerY,
                        playerHeadMarkerSize,
                        LocatorBarConfig.isPlayerMarkerOutline()
                );
            }
        }

        RenderCompat.pop(guiGraphics);
        guiGraphics.disableScissor();
        if (LocatorBarConfig.isShowCoordinates() || LocatorBarConfig.isShowDays()) {
            renderInfoText(
                    guiGraphics,
                    player,
                    x + (scaledBarWidth / 2.0F),
                    y + scaledBarHeight + Math.round(3.0F * scale),
                    scale
            );
        }
    }

    private static int getChromaColor(float offset) {
        float hue = ((System.currentTimeMillis() % 4000L) / 4000.0F + offset) % 1.0F;
        int rgb = java.awt.Color.HSBtoRGB(hue, 0.85F, 1.0F);
        return 0xFF000000 | (rgb & 0x00FFFFFF);
    }

    private static void renderDegreeTicks(GuiGraphicsExtractor guiGraphics, float playerYaw, float halfViewAngle, float centerX) {
        if (!LocatorBarConfig.isShowDegrees()) {
            return;
        }

        for (int deg = 0; deg < 360; deg += 15) {
            float relative = LocatorBarUtils.wrapTo180(deg - playerYaw);
            if (Math.abs(relative) > halfViewAngle) {
                continue;
            }

            float normalized = relative / halfViewAngle;
            float tickX = centerX + normalized * (BAR_TEXTURE_WIDTH / 2.0F);

            if (deg % 45 == 0 && deg % 90 != 0) {
                String intercardinal = switch (deg) {
                    case 45 -> "NE";
                    case 135 -> "SE";
                    case 225 -> "SW";
                    default -> "NW";
                };
                float textScale = 0.48F;
                float tw = Minecraft.getInstance().font.width(intercardinal) * textScale;
                RenderCompat.push(guiGraphics);
                RenderCompat.translate(guiGraphics, tickX - (tw / 2.0F), 2.5F);
                RenderCompat.scale(guiGraphics, textScale, textScale);
                RenderCompat.text(guiGraphics, intercardinal, 0, 0, 0xCCFFFFFF, false);
                RenderCompat.pop(guiGraphics);
            } else if (deg % 90 != 0) {
                int tickH = (deg % 30 == 0) ? 3 : 2;
                int tickY = (BAR_TEXTURE_HEIGHT - tickH) / 2;
                guiGraphics.fill((int) tickX, tickY, (int) tickX + 1, tickY + tickH, 0x55FFFFFF);
            }
        }
    }

    private static String formatDistance(float distance) {
        if (distance < 1000.0F) {
            return Math.round(distance) + "m";
        }
        return String.format(java.util.Locale.ROOT, "%.1fk", distance / 1000.0F);
    }

    private static String getHeightIndicator(double deltaY) {
        if (deltaY >= 3.0D) {
            return "▲";
        } else if (deltaY <= -3.0D) {
            return "▼";
        }
        return "";
    }

    private static void renderDirectionMarker(
            GuiGraphicsExtractor guiGraphics,
            Identifier texture,
            float directionYaw,
            float playerYaw,
            float halfViewAngle,
            float centerX,
            int markerY,
            int directionMarkerSize
    ) {
        float relative = LocatorBarUtils.wrapTo180(directionYaw - playerYaw);
        if (Math.abs(relative) > halfViewAngle) {
            return;
        }

        float normalized = relative / halfViewAngle;
        float markerX = LocatorBarUtils.quantizeToHalfPixel(centerX + normalized * (BAR_TEXTURE_WIDTH / 2.0F) - (directionMarkerSize / 2.0F));

        RenderCompat.push(guiGraphics);
        RenderCompat.translate(guiGraphics, markerX, markerY);
        RenderCompat.blitRegion(
                guiGraphics,
                texture,
                0,
                0,
                ICON_MARGIN,
                ICON_MARGIN,
                directionMarkerSize,
                directionMarkerSize,
                ICON_DOT_SIZE,
                ICON_DOT_SIZE,
                ICON_TEXTURE_SIZE,
                ICON_TEXTURE_SIZE
        );
        RenderCompat.pop(guiGraphics);
    }

    private static boolean renderWaypointMarker(
            GuiGraphicsExtractor guiGraphics,
            LocatorBarHudHelper.WaypointMarker marker,
            String displayText,
            float playerYaw,
            float halfViewAngle,
            float centerX,
            int markerY,
            int waypointMarkerSize,
            boolean defaultIndexText
    ) {
        float relative = LocatorBarUtils.wrapTo180(marker.directionYaw() - playerYaw);
        if (Math.abs(relative) > halfViewAngle) {
            if (LocatorBarConfig.isShowOffscreenArrows()) {
                boolean toLeft = relative < 0;
                String arrow = toLeft ? "◄" : "►";
                int arrowX = toLeft ? 1 : BAR_TEXTURE_WIDTH - 6;
                float arrowScale = 0.55F;
                RenderCompat.push(guiGraphics);
                RenderCompat.translate(guiGraphics, arrowX, 2.0F);
                RenderCompat.scale(guiGraphics, arrowScale, arrowScale);
                RenderCompat.text(guiGraphics, arrow, 0, 0, 0xCC000000 | (marker.rgbColor() & 0x00FFFFFF), false);
                RenderCompat.pop(guiGraphics);
            }
            return false;
        }

        float normalized = relative / halfViewAngle;
        float markerX = centerX + normalized * (BAR_TEXTURE_WIDTH / 2.0F) - (waypointMarkerSize / 2.0F);
        int drawY = marker.isDeath() ? (BAR_TEXTURE_HEIGHT - waypointMarkerSize) / 2 : markerY;

        if (Math.abs(relative) < 2.5F) {
            float pulse = (float) (0.5F + 0.5F * Math.sin(System.currentTimeMillis() / 120.0D));
            int pulseAlpha = Math.round(pulse * 100.0F);
            guiGraphics.fill((int) markerX - 1, drawY - 1, (int) markerX + waypointMarkerSize + 1, drawY + waypointMarkerSize + 1, (pulseAlpha << 24) | 0x00FFFFFF);
        }

        RenderCompat.push(guiGraphics);
        RenderCompat.translate(guiGraphics, markerX, drawY);
        Identifier texture = marker.isDeath() ? DEATH_WAYPOINT : WAYPOINT;
        RenderCompat.blitTinted(
                guiGraphics,
                texture,
                0,
                0,
                0,
                0,
                waypointMarkerSize,
                waypointMarkerSize,
                WAYPOINT_TEXTURE_SIZE,
                WAYPOINT_TEXTURE_SIZE,
                WAYPOINT_TEXTURE_SIZE,
                WAYPOINT_TEXTURE_SIZE,
                0xFF000000 | marker.rgbColor()
        );

        if (marker.isDeath()) {
            RenderCompat.pop(guiGraphics);
            return true;
        }

        float dynamicTextScale = WAYPOINT_TEXT_SCALE * (waypointMarkerSize / (float) BASE_WAYPOINT_MARKER_SIZE);
        if (defaultIndexText && displayText.length() > 1) {
            dynamicTextScale *= 0.54F;
        }
        float textWidth = Minecraft.getInstance().font.width(displayText) * dynamicTextScale;
        float textHeight = Minecraft.getInstance().font.lineHeight * dynamicTextScale;
        float textX = ((waypointMarkerSize - textWidth) / 2.0F) + 0.45F;
        float textY = (waypointMarkerSize - textHeight) / 2.0F;
        RenderCompat.push(guiGraphics);
        RenderCompat.translate(guiGraphics, textX, textY);
        RenderCompat.scale(guiGraphics, dynamicTextScale, dynamicTextScale);
        RenderCompat.text(guiGraphics, displayText, 0, 0, 0xFFFFFFFF, false);
        RenderCompat.pop(guiGraphics);

        if (LocatorBarConfig.isShowMarkerDistances() || LocatorBarConfig.isShowMarkerHeight()) {
            String badge = "";
            if (LocatorBarConfig.isShowMarkerDistances() && marker.distance() > 0.0F) {
                badge += formatDistance(marker.distance());
            }
            if (LocatorBarConfig.isShowMarkerHeight()) {
                String arrow = getHeightIndicator(marker.deltaY());
                if (!arrow.isEmpty()) {
                    if (!badge.isEmpty()) badge += " ";
                    badge += arrow;
                }
            }
            if (!badge.isEmpty()) {
                float badgeScale = 0.55F;
                float bw = Minecraft.getInstance().font.width(badge) * badgeScale;
                float bx = ((waypointMarkerSize - bw) / 2.0F);
                float by = waypointMarkerSize - 1.0F;
                RenderCompat.push(guiGraphics);
                RenderCompat.translate(guiGraphics, bx, by);
                RenderCompat.scale(guiGraphics, badgeScale, badgeScale);
                RenderCompat.text(guiGraphics, badge, 0, 0, 0xE0FFFFFF, true);
                RenderCompat.pop(guiGraphics);
            }
        }

        RenderCompat.pop(guiGraphics);
        return true;
    }

    private static void renderPlayerMarker(
            GuiGraphicsExtractor guiGraphics,
            LocatorBarHudHelper.PlayerMarker marker,
            float playerYaw,
            float halfViewAngle,
            float centerX,
            int markerY,
            int markerSize,
            boolean outline
    ) {
        float relative = LocatorBarUtils.wrapTo180(marker.directionYaw() - playerYaw);
        if (Math.abs(relative) > halfViewAngle) {
            if (LocatorBarConfig.isShowOffscreenArrows()) {
                boolean toLeft = relative < 0;
                String arrow = toLeft ? "◄" : "►";
                int arrowX = toLeft ? 1 : BAR_TEXTURE_WIDTH - 6;
                float arrowScale = 0.55F;
                int playerColor = marker.teamColor() != null ? marker.teamColor() : LocatorBarUtils.colorFromPlayerId(marker.playerId());
                RenderCompat.push(guiGraphics);
                RenderCompat.translate(guiGraphics, arrowX, 2.0F);
                RenderCompat.scale(guiGraphics, arrowScale, arrowScale);
                RenderCompat.text(guiGraphics, arrow, 0, 0, 0xCC000000 | (playerColor & 0x00FFFFFF), false);
                RenderCompat.pop(guiGraphics);
            }
            return;
        }

        float normalized = relative / halfViewAngle;
        float markerX = LocatorBarUtils.quantizeToHalfPixel(centerX + normalized * (BAR_TEXTURE_WIDTH / 2.0F) - (markerSize / 2.0F));

        if (Math.abs(relative) < 2.5F) {
            float pulse = (float) (0.5F + 0.5F * Math.sin(System.currentTimeMillis() / 120.0D));
            int pulseAlpha = Math.round(pulse * 100.0F);
            guiGraphics.fill((int) markerX - 1, markerY - 1, (int) markerX + markerSize + 1, markerY + markerSize + 1, (pulseAlpha << 24) | 0x00FFFFFF);
        }

        RenderCompat.push(guiGraphics);
        RenderCompat.translate(guiGraphics, markerX, markerY);

        int alpha = Math.max(0, Math.min(255, Math.round(marker.alpha() * 255.0F)));

        if (LocatorBarConfig.getPlayerMarkerType() == PlayerMarkerType.DOTS) {
            Identifier dotTexture = DOT_TEXTURE;
            int playerColor = marker.teamColor() != null ? marker.teamColor() : LocatorBarUtils.colorFromPlayerId(marker.playerId());
            int tint = (alpha << 24) | (playerColor & 0x00FFFFFF);
            int dotSize = Math.round(markerSize * 1.5F);
            float offset = (markerSize - dotSize) / 2.0F;
            RenderCompat.push(guiGraphics);
            RenderCompat.translate(guiGraphics, offset, offset);
            RenderCompat.blitTinted(guiGraphics, dotTexture, 0, 0, 0, 0, dotSize, dotSize, 8, 8, 8, 8, tint);
            RenderCompat.pop(guiGraphics);
        } else {
            int drawOffset = 0;
            int drawSize = markerSize;
            if (outline) {
                guiGraphics.fill(0, 0, markerSize, markerSize, alpha << 24);
                int border = Math.max(1, Math.round(markerSize * 0.14F));
                drawOffset = border;
                drawSize = Math.max(1, markerSize - (border * 2));
            }
            int tint = (alpha << 24) | 0x00FFFFFF;
            RenderCompat.blitPlayerHead(guiGraphics, marker.skinTexture(), drawOffset, drawOffset, drawSize, tint);
        }

        if (LocatorBarConfig.isShowMarkerDistances() || LocatorBarConfig.isShowMarkerHeight()) {
            String badge = "";
            if (LocatorBarConfig.isShowMarkerDistances() && marker.distance() > 0.0F) {
                badge += formatDistance(marker.distance());
            }
            if (LocatorBarConfig.isShowMarkerHeight()) {
                String arrow = getHeightIndicator(marker.deltaY());
                if (!arrow.isEmpty()) {
                    if (!badge.isEmpty()) badge += " ";
                    badge += arrow;
                }
            }
            if (!badge.isEmpty()) {
                float badgeScale = 0.55F;
                float bw = Minecraft.getInstance().font.width(badge) * badgeScale;
                float bx = ((markerSize - bw) / 2.0F);
                float by = markerSize - 1.0F;
                RenderCompat.push(guiGraphics);
                RenderCompat.translate(guiGraphics, bx, by);
                RenderCompat.scale(guiGraphics, badgeScale, badgeScale);
                RenderCompat.text(guiGraphics, badge, 0, 0, (alpha << 24) | 0x00E0E0E0, true);
                RenderCompat.pop(guiGraphics);
            }
        }

        RenderCompat.pop(guiGraphics);
    }

    private static void renderInfoText(GuiGraphicsExtractor guiGraphics, Player player, float centerX, int startY, float scale) {
        String coordsText = null;
        if (LocatorBarConfig.isShowCoordinates()) {
            if (LocatorBarConfig.getCoordinatesFormat() == CoordinatesFormat.XZ) {
                coordsText = "(" + player.getBlockX() + " " + player.getBlockZ() + ")";
            } else {
                coordsText = "(" + player.getBlockX() + " " + player.getBlockY() + " " + player.getBlockZ() + ")";
            }
        }

        String daysText = null;
        if (LocatorBarConfig.isShowDays()) {
            //? if >=26.1
            long days = player.level().getOverworldClockTime() / 24000L;
            //? if <26.1
            /*long days = player.level().getDayTime() / 24000L;*/
            daysText = "Day " + days;
        }

        RenderCompat.push(guiGraphics);
        RenderCompat.scale(guiGraphics, scale, scale);
        float scaledCenterX = centerX / scale;
        int scaledStartY = Math.round(startY / scale);
        int lineStep = Minecraft.getInstance().font.lineHeight + 1;

        if (coordsText != null && daysText != null) {
            if (LocatorBarConfig.getDaysDisplayOrder() == DaysDisplayOrder.DAYS_UNDER_COORDS) {
                drawCenteredText(guiGraphics, coordsText, scaledCenterX, scaledStartY);
                drawCenteredText(guiGraphics, daysText, scaledCenterX, scaledStartY + lineStep);
            } else {
                drawCenteredText(guiGraphics, daysText, scaledCenterX, scaledStartY);
                drawCenteredText(guiGraphics, coordsText, scaledCenterX, scaledStartY + lineStep);
            }
        } else if (coordsText != null) {
            drawCenteredText(guiGraphics, coordsText, scaledCenterX, scaledStartY);
        } else if (daysText != null) {
            drawCenteredText(guiGraphics, daysText, scaledCenterX, scaledStartY);
        }

        RenderCompat.pop(guiGraphics);
    }

    private static void drawCenteredText(GuiGraphicsExtractor guiGraphics, String text, float centerX, int y) {
        int textX = Math.round(centerX - (Minecraft.getInstance().font.width(text) / 2.0F));
        RenderCompat.text(guiGraphics, text, textX, y, 0xFFFFFFFF, true);
    }
}