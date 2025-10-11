package btw.community.abbyread.adaptivehud;

import btw.AddonHandler;
import btw.BTWAddon;

public class AdaptiveHudBrightness extends BTWAddon {

    @SuppressWarnings("unused")
    private static AdaptiveHudBrightness instance;

    public AdaptiveHudBrightness() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(
                this.getName() + " Version " + this.getVersionString() + " Initializing..."
        );
    }
}
