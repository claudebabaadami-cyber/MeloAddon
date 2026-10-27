package com.melo.addon.modules;

import com.melo.addon.MeloAddon;
import meteordevelopment.meteorclient.events.render.Render3DEvent;
import meteordevelopment.meteorclient.events.world.TickEvent;
import meteordevelopment.meteorclient.renderer.ShapeMode;
import meteordevelopment.meteorclient.settings.ColorSetting;
import meteordevelopment.meteorclient.settings.EnumSetting;
import meteordevelopment.meteorclient.settings.IntSetting;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.render.color.Color;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class StorageESP extends Module {
    private final SettingGroup sgGeneral = settings.getDefaultGroup();
    private final Setting<Integer> range = sgGeneral.add(new IntSetting.Builder()
        .name("range").description("Scan radius in blocks.").defaultValue(12).range(4, 32).sliderMax(24).build());
    private final Setting<Integer> scanInterval = sgGeneral.add(new IntSetting.Builder()
        .name("scan-interval").description("Ticks between scans; larger values reduce CPU use.").defaultValue(10).range(2, 40).sliderMax(20).build());
    private final Setting<SettingColor> color = sgGeneral.add(new ColorSetting.Builder()
        .name("color").description("ESP color.").defaultValue(Color.MAGENTA).build());
    private final Setting<ShapeMode> shapeMode = sgGeneral.add(new EnumSetting.Builder<ShapeMode>()
        .name("shape-mode").description("Choose filled boxes, outlines, or both.").defaultValue(ShapeMode.Both).build());

    private final List<BlockPos> found = new ArrayList<>();
    private int ticks;

    public StorageESP() {
        super(MeloAddon.CATEGORY, "storage-esp", "Highlights nearby storage and utility blocks already known to the client.");
    }

    private boolean isStorage(Block block) {
        return block == Blocks.CHEST || block == Blocks.TRAPPED_CHEST || block == Blocks.BARREL
            || block == Blocks.FURNACE || block == Blocks.BLAST_FURNACE || block == Blocks.SMOKER
            || block == Blocks.HOPPER || block == Blocks.DISPENSER || block == Blocks.DROPPER
            || block == Blocks.JUKEBOX || block == Blocks.CRAFTING_TABLE || block instanceof ShulkerBoxBlock;
    }

    @EventHandler
    private void onTick(TickEvent.Post event) {
        if (mc.level == null || mc.player == null) { found.clear(); return; }
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
                    if (isStorage(mc.level.getBlockState(pos).getBlock())) found.add(pos.immutable());
                }
            }
        }
    }

    @EventHandler
    private void onRender(Render3DEvent event) {
        SettingColor c = color.get();
        for (BlockPos pos : found) event.renderer.box(new AABB(pos), c, c, shapeMode.get(), 0);
    }

    @Override
    public void onDeactivate() { found.clear(); }
}
