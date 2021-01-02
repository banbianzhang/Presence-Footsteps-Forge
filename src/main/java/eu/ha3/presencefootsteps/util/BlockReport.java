package eu.ha3.presencefootsteps.util;

import com.google.gson.stream.JsonWriter;
import com.minelittlepony.common.util.GamePaths;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.world.Lookup;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Predicate;

public class BlockReport {
    private final Path loc;

    public BlockReport(String baseName) {
        loc = getUniqueFileName(GamePaths.getGameDirectory().resolve("presencefootsteps"), baseName, ".json");
    }

    public void execute(@Nullable Predicate<BlockState> filter) {
        try {
            writeReport(filter);
            printResults();
        } catch (Exception e) {
            addMessage(new TranslationTextComponent("pf.report.error", e.getMessage()).modifyStyle(s -> s.setFormatting(TextFormatting.RED)));
        }
    }

    private void writeReport(@Nullable Predicate<BlockState> filter) throws IOException {
        Files.createDirectories(loc.getParent());

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(loc))) {
            writer.setIndent("    ");
            writer.beginObject();
            writer.name("blocks");
            writer.beginObject();
            Registry.BLOCK.forEach(block -> {
                BlockState state = block.getDefaultState();

                try {
                    if (filter == null || filter.test(state)) {
                        writer.name(Registry.BLOCK.getKey(block).toString());
                        writer.beginObject();
                        writer.name("class");
                        writer.value(getClassData(state));
                        writer.name("sound");
                        writer.value(getSoundData(state));
                        writer.name("association");
                        writer.value(PresenceFootsteps.getInstance().getEngine().getIsolator().getBlockMap().getAssociation(state, Lookup.EMPTY_SUBSTRATE));
                        writer.endObject();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.endObject();
            writer.name("unmapped_entities");
            writer.beginArray();
            Registry.ENTITY_TYPE.forEach(type -> {
                if (type.create(Minecraft.getInstance().world) instanceof LivingEntity) {
                    ResourceLocation id = Registry.ENTITY_TYPE.getKey(type);
                    if (!PresenceFootsteps.getInstance().getEngine().getIsolator().getLocomotionMap().contains(id)) {
                        try {
                            writer.value(id.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            writer.endArray();
            writer.endObject();
        }
    }

    private String getSoundData(BlockState state) {
        if (state.getSoundType() == null) {
            return "NULL";
        }
        if (state.getSoundType().getStepSound() == null) {
            return "NO_SOUND";
        }
        return state.getSoundType().getStepSound().getName().getPath();
    }

    private String getClassData(BlockState state) {
        Block block = state.getBlock();

        String soundName = "";

        if (block instanceof AbstractPressurePlateBlock) soundName += ",EXTENDS_PRESSURE_PLATE";
        if (block instanceof AbstractRailBlock) soundName += ",EXTENDS_RAIL";
        if (block instanceof ContainerBlock) soundName += ",EXTENDS_CONTAINER";
        if (block instanceof FlowingFluidBlock) soundName += ",EXTENDS_LIQUID";
        if (block instanceof BushBlock) soundName += ",EXTENDS_PLANT";
        if (block instanceof DoublePlantBlock) soundName += ",EXTENDS_DOUBLE_PLANT";
        if (block instanceof SixWayBlock) soundName += ",EXTENDS_CONNECTED_PLANT";
        if (block instanceof LeavesBlock) soundName += ",EXTENDS_LEAVES";
        if (block instanceof SlabBlock) soundName += ",EXTENDS_SLAB";
        if (block instanceof StairsBlock) soundName += ",EXTENDS_STAIRS";
        if (block instanceof SnowyDirtBlock) soundName += ",EXTENDS_SNOWY";
        if (block instanceof SpreadableSnowyDirtBlock) soundName += ",EXTENDS_SPREADABLE";
        if (block instanceof FallingBlock) soundName += ",EXTENDS_PHYSICALLY_FALLING";
        if (block instanceof PaneBlock) soundName += ",EXTENDS_PANE";
        if (block instanceof HorizontalBlock) soundName += ",EXTENDS_PILLAR";
        if (block instanceof TorchBlock) soundName += ",EXTENDS_TORCH";
        if (block instanceof CarpetBlock) soundName += ",EXTENDS_CARPET";
        if (block instanceof SilverfishBlock) soundName += ",EXTENDS_INFESTED";
        if (block instanceof BreakableBlock) soundName += ",EXTENDS_TRANSPARENT";

        return soundName;
    }

    private void printResults() {
        addMessage(new TranslationTextComponent("pf.report.save")
                .append(new StringTextComponent(loc.getFileName().toString()).modifyStyle(s -> s
                    .setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, loc.toString()))
                    .applyFormatting(TextFormatting.UNDERLINE)))
                .modifyStyle(s -> s
                    .setFormatting(TextFormatting.GREEN)));
    }

    public static void addMessage(ITextComponent text) {
        Minecraft.getInstance().ingameGUI.getChatGUI().printChatMessage(text);
    }

    static Path getUniqueFileName(Path directory, String baseName, String ext) {
        Path loc = null;

        int counter = 0;
        while (loc == null || Files.exists(loc)) {
            loc = directory.resolve(baseName + (counter == 0 ? "" : "_" + counter) + ext);
            counter++;
        }

        return loc;
    }
}
