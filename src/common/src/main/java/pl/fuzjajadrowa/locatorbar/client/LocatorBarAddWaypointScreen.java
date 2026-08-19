package pl.fuzjajadrowa.locatorbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.locatorbar.waypoint.CustomWaypointStore;

import java.util.UUID;

public final class LocatorBarAddWaypointScreen extends Screen {
    private static final String[] ICONS = {"★", "⌂", "☠", "💎", "⚔", "🏰", "🌲", "📍", "🚩", "⛏", "❤️", "⚡"};
    private static final int[] COLORS = {
            0xFFFF4444, 0xFF44FF44, 0xFF4488FF, 0xFFFFCC00,
            0xFFCC44FF, 0xFF00FFFF, 0xFFFF8800, 0xFFFFFFFF
    };

    private EditBox nameInput;
    private int selectedIconIndex = 0;
    private int selectedColorIndex = 3; // Gold default

    public LocatorBarAddWaypointScreen() {
        super(Component.translatable("locatorbar.waypoint.add.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int startY = (this.height / 2) - 60;

        nameInput = new EditBox(this.font, centerX - 100, startY, 200, 20, Component.translatable("locatorbar.waypoint.add.name"));
        nameInput.setMaxLength(32);
        nameInput.setValue("Waypoint");
        addRenderableWidget(nameInput);

        // Icon buttons
        int iconStartX = centerX - 100;
        int iconY = startY + 30;
        for (int i = 0; i < ICONS.length; i++) {
            final int index = i;
            String iconText = ICONS[i];
            addRenderableWidget(Button.builder(Component.literal(iconText), button -> {
                selectedIconIndex = index;
            }).bounds(iconStartX + (i * 17), iconY, 16, 16).build());
        }

        // Color buttons
        int colorStartX = centerX - 100;
        int colorY = startY + 52;
        for (int i = 0; i < COLORS.length; i++) {
            final int index = i;
            addRenderableWidget(Button.builder(Component.literal("■"), button -> {
                selectedColorIndex = index;
            }).bounds(colorStartX + (i * 25), colorY, 23, 16).build());
        }

        // Save & Cancel
        int btnY = startY + 80;
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> saveAndClose())
                .bounds(centerX - 105, btnY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.cancel"), button -> onClose())
                .bounds(centerX + 5, btnY, 100, 20).build());
    }

    private void saveAndClose() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String name = nameInput.getValue().trim();
            if (name.isEmpty()) {
                name = "Waypoint";
            }
            String world = mc.player.level().dimension().identifier().toString();
            String icon = ICONS[selectedIconIndex];
            int color = COLORS[selectedColorIndex];

            CustomWaypointStore.add(new CustomWaypointStore.CustomWaypoint(
                    UUID.randomUUID(),
                    name,
                    world,
                    mc.player.getX(),
                    mc.player.getY(),
                    mc.player.getZ(),
                    color,
                    icon,
                    true
            ));
        }
        onClose();
    }

    //? if >=26.1 {
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        renderOverlay(guiGraphics);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderOverlay(guiGraphics);
    }
    *///?}

    private void renderOverlay(GuiGraphicsExtractor guiGraphics) {
        int centerX = this.width / 2;
        int startY = (this.height / 2) - 80;

        guiGraphics.centeredText(this.font, Component.translatable("locatorbar.waypoint.add.title"), centerX, startY, 0xFFFFFFFF);

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            String coords = String.format("Pos: %d, %d, %d", mc.player.getBlockX(), mc.player.getBlockY(), mc.player.getBlockZ());
            guiGraphics.centeredText(this.font, Component.literal(coords), centerX, startY + 12, 0xFFAAAAAA);
        }

        // Preview selected icon and color
        String icon = ICONS[selectedIconIndex];
        int color = COLORS[selectedColorIndex];
        guiGraphics.centeredText(this.font, Component.literal("Selected: " + icon), centerX, startY + 155, color);
    }

    @Override
    public void onClose() {
        if (this.minecraft != null) {
            //? if >=26.2 {
            this.minecraft.setScreenAndShow(null);
            //?} else {
            /*this.minecraft.setScreen(null);
            *///?}
        }
    }
}
