package tfar.thehandofgod.util;

public class Constants {

    public enum ScreenType {
        BACKPACK(true),ENCHANTMENTS(true),POTIONS(true),TELEPORT(true),HEAVENLY_POCKET(true),
        ARCHANGEL(false),GAMEMODE(false),CLEANSE(false);

        public final boolean screen;
        ScreenType(boolean screen) {
            this.screen = screen;
        }
    }
}
