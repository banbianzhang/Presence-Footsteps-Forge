package eu.ha3.presencefootsteps.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import org.jetbrains.annotations.Nullable;

import com.google.gson.stream.JsonWriter;
import com.minelittlepony.common.util.GamePaths;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.world.Lookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockReport {
    private final Path loc;

    public BlockReport(String baseName) {
        loc = getUniqueFileName(GamePaths.getGameDirectory().resolve("presencefootsteps"), baseName, ".json");
    }

    public CompletableFuture<?> execute(@Nullable Predicate<BlockState> filter) {
        return CompletableFuture.runAsync(() -> {
            try {
                writeReport(filter);
                printResults();
            } catch (Exception e) {
                addMessage(Component.translatable("pf.report.error", e.getMessage()).withStyle(s -> s.withColor(ChatFormatting.RED)));
            }
        });
    }

    private void writeReport(@Nullable Predicate<BlockState> filter) throws IOException {
        Files.createDirectories(loc.getParent());

        try (var writer = JsonObjectWriter.of(new JsonWriter(Files.newBufferedWriter(loc)))) {
            writer.object(() -> {
                final Map<String, SoundType> groups = new Object2ObjectOpenHashMap<>();
                writer.object("blocks", () -> {
                    writer.each(BuiltInRegistries.BLOCK, block -> {
                        BlockState state = block.defaultBlockState();

                        var group = block.defaultBlockState().getSoundType();
                        if (group != null && group.getStepSound() != null) {
                            String substrate = String.format(Locale.ENGLISH, "%.2f_%.2f", group.volume, group.pitch);
                            groups.put(group.getStepSound().getLocation().toString() + "@" + substrate, group);
                        }

                        if (filter == null || filter.test(state)) {
                            writer.object(BuiltInRegistries.BLOCK.getKey(block).toString(), () -> {
                                writer.field("class", getClassData(state));
                                writer.field("tags", getTagData(state));
                                writer.field("sound", getSoundData(group));
                                writer.field("association", PresenceFootsteps.getInstance().getEngine().getIsolator().getBlockMap().getAssociation(state, Lookup.EMPTY_SUBSTRATE));
                            });
                        }
                    });
                });
                writer.array("unmapped_entities", () -> {
                    writer.each(BuiltInRegistries.ENTITY_TYPE, type -> {
                        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);
                        if (!PresenceFootsteps.getInstance().getEngine().getIsolator().getLocomotionMap().contains(id)) {
                            if (type.create(Minecraft.getInstance().level) instanceof LivingEntity) {
                                writer.writer().value(id.toString());
                            }
                        }
                    });
                });
                writer.object("primitives", () -> {
                    writer.each(groups.values(), group -> {
                        String substrate = String.format(Locale.ENGLISH, "%.2f_%.2f", group.volume, group.pitch);
                        writer.field(group.getStepSound().getLocation().toString() + "@" + substrate, PresenceFootsteps.getInstance().getEngine().getIsolator().getPrimitiveMap().getAssociation(group, substrate));
                    });
                });
            });
        }
    }

    private String getSoundData(@Nullable SoundType group) {
        if (group == null) {
            return "NULL";
        }
        if (group.getStepSound() == null) {
            return "NO_SOUND";
        }
        return group.getStepSound().getLocation().getPath();
    }

    private String getClassData(BlockState state) {
        @Nullable
        String canonicalName = state.getBlock().getClass().getCanonicalName();
        if (canonicalName == null) {
            return "<anonymous>";
        }
        return FabricLoader.getInstance().getMappingResolver().unmapClassName("named", canonicalName);
    }

    private String getTagData(BlockState state) {
        return BuiltInRegistries.BLOCK.getTagNames().filter(state::is).map(TagKey::location).map(ResourceLocation::toString).collect(Collectors.joining(","));
    }

    private void printResults() {
        addMessage(Component.translatable("pf.report.save", Component.literal(loc.getFileName().toString()).withStyle(s -> s
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, loc.toString()))
                .applyFormat(ChatFormatting.UNDERLINE)))
            .withStyle(s -> s
                .withColor(ChatFormatting.GREEN)));
    }

    public static void addMessage(Component text) {
        Minecraft.getInstance().gui.getChat().addMessage(text);
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
