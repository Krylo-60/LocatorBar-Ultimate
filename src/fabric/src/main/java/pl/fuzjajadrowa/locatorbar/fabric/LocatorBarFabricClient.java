package pl.fuzjajadrowa.locatorbar.fabric;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
//? if >=26.1 {
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
//?} else {
/*import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
*///?}
import net.minecraft.client.KeyMapping;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import org.lwjgl.glfw.GLFW;
import pl.fuzjajadrowa.locatorbar.client.LocatorBarHudRenderer;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;

public final class LocatorBarFabricClient implements ClientModInitializer {
    private static KeyMapping toggleKey;
    private static KeyMapping addWaypointKey;

    @Override
    public void onInitializeClient() {
        LocatorBarFabricNetworking.initClient();

        //? if >=26.1 {
        var category = KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("locatorbar", "general"));
        toggleKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.locatorbar.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                category
        ));
        addWaypointKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.locatorbar.add_waypoint",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                category
        ));
        //?} elif >=1.21.11 {
        /*var category = KeyMapping.Category.register(net.minecraft.resources.Identifier.fromNamespaceAndPath("locatorbar", "general"));
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locatorbar.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                category
        ));
        addWaypointKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locatorbar.add_waypoint",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                category
        ));
        *///?} else {
        /*toggleKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locatorbar.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.locatorbar"
        ));
        addWaypointKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.locatorbar.add_waypoint",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.locatorbar"
        ));
        *///?}

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (addWaypointKey.consumeClick()) {
                if (client.player != null) {
                    //? if >=26.2 {
                    client.setScreenAndShow(new pl.fuzjajadrowa.locatorbar.client.LocatorBarAddWaypointScreen());
                    //?} else {
                    /*client.setScreen(new pl.fuzjajadrowa.locatorbar.client.LocatorBarAddWaypointScreen());
                    *///?}
                }
            }

            while (toggleKey.consumeClick()) {
                LocatorBarHudRenderer.enabled = !LocatorBarHudRenderer.enabled;
                if (LocatorBarConfig.isSoundEnabled()) {
                    try {
                        var soundPack = LocatorBarConfig.getSoundPack();
                        if (soundPack != pl.fuzjajadrowa.locatorbar.config.LocatorBarEnums.SoundPack.MUTE) {
                            float pitch = LocatorBarHudRenderer.enabled ? 1.5F : 0.85F;
                            switch (soundPack) {
                                case SCIFI_BLIP -> client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.ARROW_HIT_PLAYER, pitch));
                                case CLICK -> client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, pitch));
                                case LEVEL_UP -> client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.PLAYER_LEVELUP, pitch));
                                default -> client.getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, pitch));
                            }
                        }
                    } catch (Throwable ignored) {
                    }
                }
                if (client.player != null) {
                    //? if >=26.1 {
                    client.player.sendOverlayMessage(
                            Component.literal("Locator Bar: " + (LocatorBarHudRenderer.enabled ? "ON" : "OFF"))
                    );
                    //?} else {
                    /*client.player.displayClientMessage(
                            Component.literal("Locator Bar: " + (LocatorBarHudRenderer.enabled ? "ON" : "OFF")),
                            true
                    );
                    *///?}
                }
            }
        });
    }
}