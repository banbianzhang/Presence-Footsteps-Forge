package eu.ha3.presencefootsteps;

import com.minelittlepony.common.client.gui.GameGui;
import com.minelittlepony.common.client.gui.Tooltip;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.element.*;
import com.mojang.blaze3d.vertex.PoseStack;
import eu.ha3.presencefootsteps.util.BlockReport;
import net.minecraft.client.gui.components.Renderable;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PFOptionsScreen extends GameGui {
    public static final Component TITLE = Component.translatable("menu.pf.title");
    public static final Component VOLUME_MIN = Component.translatable("menu.pf.volume.min");

    public PFOptionsScreen(@Nullable Screen parent) {
        super(TITLE, parent);
    }

    @Override
    public void init() {
        int left = width / 2 - 100;

        int wideLeft = width / 2 - 155;
        int wideRight = wideLeft + 160;

        int row = height / 4;

        PFConfig config = PresenceFootsteps.getInstance().getConfig();

        addRenderableWidget(new Label(width / 2, 30)).setCentered().getStyle()
                .setText(getTitle());

        var slider = addRenderableWidget(new Slider(wideLeft, row, 0, 100, config.getGlobalVolume()))
            .onChange(config::setGlobalVolume)
            .setTextFormat(this::formatVolume);
        slider.setBounds(new Bounds(row, wideLeft, 310, 20));
        slider.getStyle().setTooltip(Tooltip.of("menu.pf.volume.tooltip", 210)).setTooltipOffset(0, 25);

        slider = addRenderableWidget(new Slider(wideLeft, row += 24, 0, 100, config.getClientPlayerVolume()))
            .onChange(config::setClientPlayerVolume)
            .setTextFormat(formatVolume("menu.pf.volume.player"));
        slider.setBounds(new Bounds(row, wideLeft, 150, 20));
        slider.getStyle().setTooltip(Tooltip.of("menu.pf.volume.player.tooltip", 210)).setTooltipOffset(0, 25);

        slider = addRenderableWidget(new Slider(wideRight, row, 0, 100, config.getOtherPlayerVolume()))
            .onChange(config::setOtherPlayerVolume)
            .setTextFormat(formatVolume("menu.pf.volume.other_players"));
        slider.setBounds(new Bounds(row, wideRight, 150, 20));
        slider.getStyle().setTooltip(Tooltip.of("menu.pf.volume.other_players.tooltip", 210)).setTooltipOffset(0, 25);

        slider = addRenderableWidget(new Slider(wideLeft, row += 24, 0, 100, config.getRunningVolumeIncrease()))
            .onChange(config::setRunningVolumeIncrease)
            .setTextFormat(formatVolume("menu.pf.volume.running"));
        slider.setBounds(new Bounds(row, wideLeft, 310, 20));
        slider.getStyle().setTooltip(Tooltip.of("menu.pf.volume.running.tooltip", 210)).setTooltipOffset(0, 25);

        addRenderableWidget(new EnumSlider<>(left, row += 24, config.getLocomotion())
                .onChange(config::setLocomotion)
                .setTextFormat(v -> v.getValue().getOptionName()))
                .setTooltipFormat(v -> Tooltip.of(v.getValue().getOptionTooltip(), 250))
                .setBounds(new Bounds(row, wideLeft, 310, 20));

        addRenderableWidget(new Button(wideLeft, row += 24, 150, 20).onClick(sender -> {
            sender.getStyle().setText("menu.pf.global." + config.cycleTargetSelector().name().toLowerCase());
        })).getStyle()
            .setText("menu.pf.global." + config.getEntitySelector().name().toLowerCase());

        addRenderableWidget(new Button(wideRight, row, 150, 20).onClick(sender -> {
            sender.getStyle().setText("menu.pf.multiplayer." + config.toggleMultiplayer());
        })).getStyle()
            .setText("menu.pf.multiplayer." + config.getEnabledMP());

        addRenderableWidget(new Button(wideLeft, row += 24, 150, 20).onClick(sender -> {
            sender.setEnabled(false);
            new BlockReport("report_concise")
                .execute(state -> !PresenceFootsteps.getInstance().getEngine().getIsolator().getBlockMap().contains(state))
                .thenRun(() -> sender.setEnabled(true));
        })).setEnabled(minecraft.level != null)
            .getStyle()
            .setText("menu.pf.report.concise");

        addRenderableWidget(new Button(wideRight, row, 150, 20)
            .onClick(sender -> {
                sender.setEnabled(false);
                new BlockReport("report_full")
                    .execute(null)
                    .thenRun(() -> sender.setEnabled(true));
            }))
            .setEnabled(minecraft.level != null)
            .getStyle()
                .setText("menu.pf.report.full");

        addRenderableWidget(new Button(left, row += 34)
            .onClick(sender -> finish())).getStyle()
            .setText("gui.done");
    }

    @Override
    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, partialTicks);
        for (final Renderable renderable : this.renderables) {
            if (renderable instanceof final Button button)
                button.renderToolTip(matrices, this, mouseX, mouseY);
        }
    }

    private Component formatVolume(AbstractSlider<Float> slider) {
        if (slider.getValue() <= 0) {
            return VOLUME_MIN;
        }

        return Component.translatable("menu.pf.volume", (int)Math.floor(slider.getValue()));
    }

    static Function<AbstractSlider<Float>, Component> formatVolume(String key) {
        return slider -> Component.translatable(key, (int)Math.floor(slider.getValue()));
    }
}
