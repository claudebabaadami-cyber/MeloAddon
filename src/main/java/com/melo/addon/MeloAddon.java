package com.melo.addon;

import com.melo.addon.modules.OreESP;
import com.melo.addon.modules.DirectionalBlockESP;
import com.melo.addon.modules.Fullbright;
import com.melo.addon.modules.StorageESP;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;

public class MeloAddon extends MeteorAddon {
    public static final Category CATEGORY = new Category("Melo");

    @Override
    public void onInitialize() {
        Modules.get().add(new StorageESP());
        Modules.get().add(new OreESP());
        Modules.get().add(new DirectionalBlockESP());
        Modules.get().add(new Fullbright());
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "com.melo.addon";
    }
}
