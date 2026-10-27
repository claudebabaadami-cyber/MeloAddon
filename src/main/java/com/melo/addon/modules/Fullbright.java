package com.melo.addon.modules;

import com.melo.addon.MeloAddon;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.settings.DoubleSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;

/** Adjustable client-side brightness helper. Restores the prior gamma when disabled. */
public class Fullbright extends Module {
    private final Setting<Double> gamma = settings.getDefaultGroup().add(new DoubleSetting.Builder()
        .name("brightness")
        .description("Client brightness value while enabled.")
        .defaultValue(16.0)
        .min(1.0)
        .max(32.0)
        .sliderMin(1.0)
        .sliderMax(20.0)
        .build());

    private double previousGamma;
    private boolean saved;

    public Fullbright() {
        super(MeloAddon.CATEGORY, "melo-fullbright", "Adjusts client brightness while enabled.");
    }

    @Override
    public void onActivate() {
        if (mc.options == null) return;
        previousGamma = mc.options.gamma().get();
        saved = true;
        mc.options.gamma().set(gamma.get());
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.options != null) mc.options.gamma().set(gamma.get());
    }

    @Override
    public void onDeactivate() {
        if (mc.options != null && saved) mc.options.gamma().set(previousGamma);
        saved = false;
    }
}
