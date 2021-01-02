package com.minelittlepony.common.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.google.common.base.Splitter;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public interface Tooltip {
    Splitter LINE_SPLITTER = Splitter.onPattern("\r?\n|\\\\n");

    List<ITextComponent> getLines();

    default CharSequence getString() {
        StringBuilder builder = new StringBuilder();
        getLines().forEach(line -> {
            if (builder.length() > 0) {
                builder.append('\n');
            }
            builder.append(line.getUnformattedComponentText());
        });
        return builder;
    }

    default Stream<ITextComponent> stream() {
        return getLines().stream();
    }

    static Tooltip of(String text) {
        return of(new TranslationTextComponent(text));
    }

    static Tooltip of(List<ITextComponent> lines) {
        List<ITextComponent> flines = lines.stream()
                .map(Tooltip::of)
                .flatMap(Tooltip::stream)
                .collect(Collectors.toList());
        return () -> flines;
    }

    static Tooltip of(ITextComponent text) {

        List<ITextComponent> lines = new ArrayList<>();
        lines.add(new StringTextComponent(""));

        text.getComponentWithStyle((style, part) -> {
            List<ITextComponent> parts = LINE_SPLITTER.splitToList(part)
                    .stream()
                    .map(i -> new StringTextComponent(i).mergeStyle(style))
                    .collect(Collectors.toList());

            lines.add(((IFormattableTextComponent)lines.remove(lines.size() - 1)).append(parts.remove(0)));
            lines.addAll(parts);

            return Optional.empty();
        }, text.getStyle());

        return () -> lines;
    }
}
