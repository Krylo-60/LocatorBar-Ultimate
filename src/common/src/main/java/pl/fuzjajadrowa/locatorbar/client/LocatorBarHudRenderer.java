package pl.fuzjajadrowa.locatorbar.client;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.LocatorBarStyle;

public final class LocatorBarHudRenderer {
    public static boolean enabled = true;
    private static float currentAlpha = 1.0F;
    private static long lastRenderTimeNanos = 0L;

    private LocatorBarHudRenderer() {
    }

    public static float getCurrentAlpha() {
        return currentAlpha;
    }

    public static void render(GuiGraphicsExtractor guiGraphics) {
        long now = System.nanoTime();
        if (lastRenderTimeNanos == 0L) {
            lastRenderTimeNanos = now;
        }
        float deltaSeconds = Math.min((now - lastRenderTimeNanos) / 1_000_000_000.0F, 0.1F);
        lastRenderTimeNanos = now;

        if (LocatorBarConfig.isFadeAnimation()) {
            float targetAlpha = (enabled && LocatorBarConfig.isEnabled()) ? 1.0F : 0.0F;
            float fadeSpeed = 5.0F;
            if (currentAlpha < targetAlpha) {
                currentAlpha = Math.min(targetAlpha, currentAlpha + (deltaSeconds * fadeSpeed));
            } else if (currentAlpha > targetAlpha) {
                currentAlpha = Math.max(targetAlpha, currentAlpha - (deltaSeconds * fadeSpeed));
            }
        } else {
            currentAlpha = (enabled && LocatorBarConfig.isEnabled()) ? 1.0F : 0.0F;
        }

        if (currentAlpha <= 0.001F) {
            return;
        }

        LocatorBarStyle style = LocatorBarConfig.getStyle();
        if (style == LocatorBarStyle.CLASSIC) {
            ClassicLocatorBarHudRenderer.render(guiGraphics);
            return;
        }

        ReworkedLocatorBarHudRenderer.render(guiGraphics);
    }
}