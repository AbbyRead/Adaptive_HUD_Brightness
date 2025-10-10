package btw.community.abbyread;

import btw.AddonHandler;
import btw.BTWAddon;

public class AdaptiveHudBrightness extends BTWAddon {
    private static AdaptiveHudBrightness instance;

    public AdaptiveHudBrightness() {
        super();
    }

    @Override
    public void initialize() {
        AddonHandler.logMessage(this.getName() + " Version " + this.getVersionString() + " Initializing...");
    }
}