package pl.fuzjajadrowa.locatorbar.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import pl.fuzjajadrowa.locatorbar.config.LocatorBarConfig;

public final class LocatorBarPositionScreen extends Screen {
    private final Screen parent;
    private int currentOffsetX;
    private int currentOffsetY;
    private boolean dragging = false;
    private double dragStartX;
    private double dragStartY;
    private int initialOffsetX;
    private int initialOffsetY;

    private static final int BAR_WIDTH = 102;
    private static final int BAR_HEIGHT = 10;

    public LocatorBarPositionScreen(Screen parent) {
        super(Component.translatable("locatorbar.position.title"));
        this.parent = parent;
        this.currentOffsetX = LocatorBarConfig.getCustomOffsetX();
        this.currentOffsetY = LocatorBarConfig.getCustomOffsetY();
    }

    @Override
    protected void init() {
        int buttonY = this.height - 32;

        addRenderableWidget(Button.builder(Component.translatable("locatorbar.position.reset"), button -> {
            this.currentOffsetX = 0;
            this.currentOffsetY = 0;
            LocatorBarConfig.setCustomOffsetX(0);
            LocatorBarConfig.setCustomOffsetY(0);
            LocatorBarConfig.save();
        }).bounds((this.width / 2) - 105, buttonY, 100, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.done"), button -> {
            LocatorBarConfig.setCustomOffsetX(this.currentOffsetX);
            LocatorBarConfig.setCustomOffsetY(this.currentOffsetY);
            LocatorBarConfig.save();
            if (this.minecraft != null) {
                //? if >=26.2 {
                this.minecraft.setScreenAndShow(this.parent);
                //?} else {
                /*this.minecraft.setScreen(this.parent);
                *///?}
            }
        }).bounds((this.width / 2) + 5, buttonY, 100, 20).build());
    }

    private boolean handlePress(double mouseX, double mouseY) {
        float scale = LocatorBarConfig.getScale();
        int scaledW = Math.max(1, Math.round(BAR_WIDTH * scale));
        int scaledH = Math.max(1, Math.round(BAR_HEIGHT * scale));
        int barX = ((this.width - scaledW) / 2) + this.currentOffsetX;
        int barY = 5 + this.currentOffsetY;

        int margin = 10;
        if (mouseX >= barX - margin && mouseX <= barX + scaledW + margin &&
            mouseY >= barY - margin && mouseY <= barY + scaledH + margin) {
            this.dragging = true;
            this.dragStartX = mouseX;
            this.dragStartY = mouseY;
            this.initialOffsetX = this.currentOffsetX;
            this.initialOffsetY = this.currentOffsetY;
            return true;
        }
        return false;
    }

    private void handleRelease() {
        this.dragging = false;
        LocatorBarConfig.setCustomOffsetX(this.currentOffsetX);
        LocatorBarConfig.setCustomOffsetY(this.currentOffsetY);
        LocatorBarConfig.save();
    }

    private void handleDrag(double mouseX, double mouseY) {
        int newOffsetX = this.initialOffsetX + (int) Math.round(mouseX - this.dragStartX);
        int newOffsetY = this.initialOffsetY + (int) Math.round(mouseY - this.dragStartY);

        if (Math.abs(newOffsetX) < 6) {
            newOffsetX = 0;
        }

        this.currentOffsetX = Math.max(-500, Math.min(500, newOffsetX));
        this.currentOffsetY = Math.max(-500, Math.min(500, newOffsetY));
        LocatorBarConfig.setCustomOffsetX(this.currentOffsetX);
        LocatorBarConfig.setCustomOffsetY(this.currentOffsetY);
    }

    //? if >=1.21.11 {
    @Override
    public boolean mouseClicked(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0) {
            if (handlePress(event.x(), event.y())) {
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseReleased(net.minecraft.client.input.MouseButtonEvent event) {
        if (event.button() == 0 && this.dragging) {
            handleRelease();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(net.minecraft.client.input.MouseButtonEvent event, double dragX, double dragY) {
        if (this.dragging) {
            handleDrag(event.x(), event.y());
            return true;
        }
        return super.mouseDragged(event, dragX, dragY);
    }
    //?} else {
    /*@Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (handlePress(mouseX, mouseY)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.dragging) {
            handleRelease();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.dragging) {
            handleDrag(mouseX, mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }
    *///?}

    //? if >=26.1 {
    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        renderPositionGuide(guiGraphics);
    }
    //?} else {
    /*@Override
    public void render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderPositionGuide(guiGraphics);
    }
    *///?}

    private void renderPositionGuide(GuiGraphicsExtractor guiGraphics) {
        int centerX = this.width / 2;
        guiGraphics.fill(centerX, 0, centerX + 1, this.height, 0x33FFFFFF);

        float scale = LocatorBarConfig.getScale();
        int scaledW = Math.max(1, Math.round(BAR_WIDTH * scale));
        int scaledH = Math.max(1, Math.round(BAR_HEIGHT * scale));
        int barX = ((this.width - scaledW) / 2) + this.currentOffsetX;
        int barY = 5 + this.currentOffsetY;

        int boxColor = this.dragging ? 0xFF55FF55 : 0xAA00AAFF;
        int fillColor = this.dragging ? 0x4455FF55 : 0x2200AAFF;
        guiGraphics.fill(barX - 4, barY - 4, barX + scaledW + 4, barY + scaledH + 4, fillColor);
        
        guiGraphics.fill(barX - 4, barY - 4, barX + scaledW + 4, barY - 3, boxColor);
        guiGraphics.fill(barX - 4, barY + scaledH + 3, barX + scaledW + 4, barY + scaledH + 4, boxColor);
        guiGraphics.fill(barX - 4, barY - 4, barX - 3, barY + scaledH + 4, boxColor);
        guiGraphics.fill(barX + scaledW + 3, barY - 4, barX + scaledW + 4, barY + scaledH + 4, boxColor);

        String hintText = "Click and drag the box to move Locator Bar";
        int hintWidth = this.font.width(hintText);
        guiGraphics.text(this.font, hintText, (this.width - hintWidth) / 2, barY + scaledH + 12, 0xFFE0E0E0, true);

        String posText = "Offset: X = " + this.currentOffsetX + ", Y = " + this.currentOffsetY;
        int posWidth = this.font.width(posText);
        guiGraphics.text(this.font, posText, (this.width - posWidth) / 2, barY + scaledH + 24, 0xFFFFAA00, true);
    }

    @Override
    public void onClose() {
        LocatorBarConfig.setCustomOffsetX(this.currentOffsetX);
        LocatorBarConfig.setCustomOffsetY(this.currentOffsetY);
        LocatorBarConfig.save();
        if (this.minecraft != null) {
            //? if >=26.2 {
            this.minecraft.setScreenAndShow(this.parent);
            //?} else {
            /*this.minecraft.setScreen(this.parent);
            *///?}
        }
    }
}
