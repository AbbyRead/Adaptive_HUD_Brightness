package btw.community.abbyread;

public class HUDBrightnessHelper {
    private static float current = 1.0F;
    private static float target = 1.0F;

    // Called every frame from EntityRendererMixin
    public static void setTargetBrightness(float newTarget) {
        target = newTarget;
    }

    // Called from the GUI render mixin (or elsewhere)
    public static float getSmoothBrightness() {
        // Smooth interpolation (adjust factor to taste)
        current += (target - current) * 0.1F;
        return current;
    }
}
