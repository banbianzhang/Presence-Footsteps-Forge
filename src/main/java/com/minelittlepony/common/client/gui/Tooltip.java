package com.minelittlepony.common.client.gui;

import com.google.common.base.Splitter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public interface Tooltip {
    Splitter LINE_SPLITTER = Splitter.onPattern("\r?\n|\\\\n");

    List<Component> getLines();

    default void appendNarrations(NarrationElementOutput narrationMsg) {
        this.getLines().forEach((line) -> {
            narrationMsg.add(NarratedElementType.HINT, line);
        });
    }

    default CharSequence getString() {
        StringBuilder builder = new StringBuilder();
        this.getLines().forEach((line) -> {
            if (builder.length() > 0) {
                builder.append('\n');
            }

            builder.append(line.getContents());
        });
        return builder;
    }

    default Stream<Component> stream() {
        return this.getLines().stream();
    }

    static Tooltip of(List<Component> lines) {
        List<Component> flines = (List)lines.stream().map(Tooltip::of).flatMap(Tooltip::stream).collect(Collectors.toList());
        return () -> {
            return flines;
        };
    }

    static Tooltip of(List<FormattedText> lines, Style style) {
        List<Component> flines = (List)lines.stream().map((line) -> {
            return of(line, style);
        }).flatMap(Tooltip::stream).collect(Collectors.toList());
        return () -> {
            return flines;
        };
    }

    static Tooltip of(String text) {
        return of((Component)(new TranslatableComponent(text)));
    }

    static Tooltip of(Component text) {
        return of((FormattedText)text, text.getStyle());
    }

    static Tooltip of(FormattedText text, Style styl) {
        List<Component> lines = new ArrayList();
        lines.add(new TextComponent(""));
        text.visit((style, part) -> {
            List<Component> parts = (List)LINE_SPLITTER.splitToList(part).stream().map((i) -> {
                return (new TextComponent(i)).withStyle(style);
            }).collect(Collectors.toList());
            lines.add(((MutableComponent)lines.remove(lines.size() - 1)).append((Component)parts.remove(0)));
            lines.addAll(parts);
            return Optional.empty();
        }, styl);
        return () -> {
            return lines;
        };
    }

    static Tooltip of(String text, int maxWidth) {
        return of((Component)(new TranslatableComponent(text)), maxWidth);
    }

    static Tooltip of(Component text, int maxWidth) {
        return of(text, text.getStyle(), maxWidth);
    }

    static Tooltip of(FormattedText text, Style style, int maxWidth) {
        return of(Minecraft.getInstance().font.getSplitter().splitLines(text, maxWidth, style), style);
    }
}
