package pl.fuzjajadrowa.locatorbar.config;

public final class LocatorBarEnums {
    private LocatorBarEnums() {
    }

    public enum LocatorBarStyle {
        REWORKED("locatorbar.style.reworked"),
        CLASSIC("locatorbar.style.classic"),
        OFF("locatorbar.style.off");

        private final String translationKey;

        LocatorBarStyle(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }

        public LocatorBarStyle next() {
            return switch (this) {
                case REWORKED -> CLASSIC;
                case CLASSIC -> OFF;
                case OFF -> REWORKED;
            };
        }
    }


    public enum CoordinatesFormat {
        XYZ("locatorbar.coordinates_format.xyz"),
        XZ("locatorbar.coordinates_format.xz");

        private final String translationKey;

        CoordinatesFormat(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }

        public CoordinatesFormat next() {
            return this == XYZ ? XZ : XYZ;
        }
    }

    public enum DaysDisplayOrder {
        DAYS_UNDER_COORDS("locatorbar.days_order.days_under_coords"),
        COORDS_UNDER_DAYS("locatorbar.days_order.coords_under_days");

        private final String translationKey;

        DaysDisplayOrder(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }

        public DaysDisplayOrder next() {
            return this == DAYS_UNDER_COORDS ? COORDS_UNDER_DAYS : DAYS_UNDER_COORDS;
        }
    }

    public enum PlayerMarkerType {
        HEADS("locatorbar.player_marker.heads"),
        DOTS("locatorbar.player_marker.dots"),
        OFF("locatorbar.player_marker.off");

        private final String translationKey;

        PlayerMarkerType(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }

        public PlayerMarkerType next() {
            return switch (this) {
                case HEADS -> DOTS;
                case DOTS -> OFF;
                case OFF -> HEADS;
            };
        }
    }

    public enum BarTheme {
        DEFAULT("locatorbar.theme.default", 0xFFFFFFFF, 0x00000000),
        GLASS("locatorbar.theme.glass", 0x88FFFFFF, 0x55000000),
        FROSTED("locatorbar.theme.frosted", 0xCCEEEEEE, 0x88202030),
        NEON_CYAN("locatorbar.theme.neon_cyan", 0xFF00FFFF, 0xAA003344),
        AMETHYST("locatorbar.theme.amethyst", 0xFFCC88FF, 0xAA331144),
        EMERALD("locatorbar.theme.emerald", 0xFF55FF88, 0xAA004422),
        GOLD("locatorbar.theme.gold", 0xFFFFCC33, 0xAA443300),
        NETHER("locatorbar.theme.nether", 0xFFFF4444, 0xAA441111),
        CHROMA_WAVE("locatorbar.theme.chroma", 0xFFFFFFFF, 0xAA111122),
        CYBERPUNK("locatorbar.theme.cyberpunk", 0xFFFF0077, 0xCC001122),
        SUNSET("locatorbar.theme.sunset", 0xFFFF7700, 0xAA330033),
        OLED_MINIMAL("locatorbar.theme.oled", 0x00000000, 0x00000000);

        private final String translationKey;
        private final int accentColor;
        private final int backgroundColor;

        BarTheme(String translationKey, int accentColor, int backgroundColor) {
            this.translationKey = translationKey;
            this.accentColor = accentColor;
            this.backgroundColor = backgroundColor;
        }

        public String translationKey() {
            return translationKey;
        }

        public int accentColor() {
            return accentColor;
        }

        public int backgroundColor() {
            return backgroundColor;
        }

        public BarTheme next() {
            BarTheme[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }

    public enum SoundPack {
        MODERN_CHIME("locatorbar.sound.chime"),
        SCIFI_BLIP("locatorbar.sound.scifi"),
        CLICK("locatorbar.sound.click"),
        LEVEL_UP("locatorbar.sound.levelup"),
        MUTE("locatorbar.sound.mute");

        private final String translationKey;

        SoundPack(String translationKey) {
            this.translationKey = translationKey;
        }

        public String translationKey() {
            return translationKey;
        }

        public SoundPack next() {
            SoundPack[] values = values();
            return values[(ordinal() + 1) % values.length];
        }
    }
}