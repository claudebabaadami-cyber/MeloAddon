package com.melo.addon.modules;

import com.melo.addon.MeloAddon;
import meteordevelopment.meteorclient.systems.modules.Module;

/** Client-side brightness helper. Restores the previous gamma value when disabled. */
public class Fullbright extends Module {
    private double previousGamma;
    private boolean saved;

    public Fullbright() {
        super(MeloAddon.CATEGORY, "melo-fullbright", "Raises client brightness while enabled.");
    }

    @Override
    public void onActivate() {
        if (mc.options == null) return;
        previousGamma = mc.options.gamma().get();
        saved = true;
        mc.options.gamma().set(16.0);
    }

    @Override
    public void onDeactivate() {
        if (mc.options != null && saved) mc.options.gamma().set(previousGamma);
        saved = false;
    }
}
