package pl.fuzjajadrowa.locatorbar.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.lwjgl.glfw.GLFW;
import pl.fuzjajadrowa.locatorbar.client.LocatorBarConfigScreen;
import pl.fuzjajadrowa.locatorbar.client.LocatorBarHudRenderer;
import pl.fuzjajadrowa.locatorbar.client.PlayerLocatorClient;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;

public final class LocatorBarNeoForgeClient {
    private static KeyMapping toggleKey;
    private static KeyMapping addWaypointKey;

    private LocatorBarNeoForgeClient() {
    }

    public static void init(ModContainer modContainer, IEventBus modEventBus) {
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, (container, parentScreen) -> new LocatorBarConfigScreen(parentScreen));
        modEventBus.addListener(LocatorBarNeoForgeClient::onRegisterKeyMappings);
        NeoForge.EVENT_BUS.addListener(LocatorBarNeoForgeClient::onClientTick);
        NeoForge.EVENT_BUS.addListener(LocatorBarNeoForgeClient::onRenderGui);
        NeoForge.EVENT_BUS.addListener(LocatorBarNeoForgeClient::onLoggingOut);
    }

    private static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        toggleKey = new KeyMapping(
                "key.locatorbar.toggle",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.locatorbar"
        );
        addWaypointKey = new KeyMapping(
                "key.locatorbar.add_waypoint",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "category.locatorbar"
        );
        event.register(toggleKey);
        event.register(addWaypointKey);
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (addWaypointKey != null) {
            while (addWaypointKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new pl.fuzjajadrowa.locatorbar.client.LocatorBarAddWaypointScreen());
                }
            }
        }

        if (toggleKey != null) {
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
                    client.player.displayClientMessage(
                            Component.literal("Locator Bar: " + (LocatorBarHudRenderer.enabled ? "ON" : "OFF")),
                            true
                    );
                }
            }
        }
    }

    private static void onRenderGui(RenderGuiEvent.Post event) {
        LocatorBarHudRenderer.render(event.getGuiGraphics());
    }

    private static void onLoggingOut(ClientPlayerNetworkEvent.LoggingOut event) {
        LocatorBarConfig.clearServerSettings();
        PlayerLocatorClient.clear();
    }
}