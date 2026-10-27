package com.melo.addon.modules;

import com.melo.addon.MeloAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class DirectionalBlockESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range").description("Scan radius in blocks.").defaultValue(10).range(3, 20).sliderMax(16).build());
    private final Setting<Integer> scanInterval = sgGeneral.add(new IntSetting.Builder()
        .name("scan-interval").description("Ticks between scans; higher values reduce CPU use.").defaultValue(15).range(3, 60).sliderMax(30).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color").description("ESP color.").defaultValue(Color.ORANGE).build());

    private final List<BlockPos> found = new ArrayList<>();
    private int ticks;

    public DirectionalBlockESP() {
        super(MeloAddon.CATEGORY, "directional-block-esp", "Highlights loaded blocks with a facing property; does not reveal unloaded or withheld data.");
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.level == null || mc.player == null) return;
        if (++ticks < scanInterval.get()) return;
        ticks = 0;
        found.clear();
        BlockPos center = mc.player.blockPosition();
        int r = range.get();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = center.getX() - r; x <= center.getX() + r; x++) {
            for (int y = Math.max(mc.level.getMinY(), center.getY() - r); y <= Math.min(mc.level.getMaxY(), center.getY() + r); y++) {
                for (int z = center.getZ() - r; z <= center.getZ() + r; z++) {
                    pos.set(x, y, z);
                    if (!mc.level.hasChunkAt(pos)) continue;
                    BlockState state = mc.level.getBlockState(pos);
                    boolean facing = false;
                    for (Property<?> property : state.getProperties()) {
                        if ("facing".equals(property.getName())) { facing = true; break; }
                    }
                    if (facing) found.add(pos.immutable());
                }
            }
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        SettingColor c = color.get();
        for (BlockPos pos : found) event.renderer.box(new AABB(pos), c, c, ShapeMode.Both, 0);
    }

    @Override
    public void onDeactivate() { found.clear(); }
}
