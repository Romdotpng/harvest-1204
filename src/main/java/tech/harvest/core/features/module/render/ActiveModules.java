package tech.harvest.core.features.module.render;

import java.awt.*;
import java.util.Comparator;
import java.util.List;

import me.x150.renderer.render.Renderer2d;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.Render2DEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.ui.font.TTFFontRenderer;
import net.minecraft.client.util.math.MatrixStack;

public class ActiveModules extends Module {
    private final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Light", 12);
    private final TTFFontRenderer common = TTFFontRenderer.of("Roboto-Regular", 11);
    private final TTFFontRenderer font1 = TTFFontRenderer.of("light", 10);
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Sigma").option("Sigma", "Flower", "SimpleFlower").end();

    public ActiveModules() {
        super("ActiveModules", "Display enabled modules", ModuleCategory.Render);
        this.getSettings().add(mode);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        MatrixStack stack = new MatrixStack();
        List<Module> modules = HarvestClient.getModuleManager().getModules().stream().filter(Module::isEnabled).toList();
        List<Module> modules2 = HarvestClient.getModuleManager().getModules().stream().filter(Module::isEnabled).sorted(Comparator.comparingInt(m -> (int) -font1.getStringWidth(m.getName()))).toList();
        switch (this.mode.getValue()) {
            case("Sigma"): {
                float currentY = 0.0f;
                for (int i = 0; i < modules.size(); i++) {
                    Module m = modules.get(i);
                    this.font.drawString(stack, m.getName(), (mc.getWindow().getWidth() / 2) - this.font.getStringWidth(m.getName()), currentY, -1);
                    currentY += this.font.getFontHeight();
                }
                break;
            }

            case("Flower"): {
                float currentY = 8.0f;
                for (int i = 0; i < modules2.size(); i++) {
                    Module m = modules2.get(i);
                    float width = font1.getStringWidth(String.format("%s", m.getName())) + 4;
                    Renderer2d.renderQuad(stack, new Color(0xBA000000, true), mc.getWindow().getScaledWidth() - width - 1, currentY, mc.getWindow().getScaledWidth(), currentY + 12);
                    font1.drawString(stack, m.getName(), mc.getWindow().getScaledWidth() - width, currentY, 0xe000ff00);
                    currentY += 12;
                }
                Renderer2d.renderQuad(stack, new Color(0xe000ff00, true), mc.getWindow().getScaledWidth() - 2, 8, mc.getWindow().getScaledWidth(), currentY);
                break;
            }
            case("SimpleFlower"): {
                Module m;
                List<Module> modules3 = HarvestClient.getModuleManager().getModules().stream().filter(Module::isEnabled).sorted(Comparator.comparingDouble(mv -> {
                    if (mv.getPrefix() == null) {
                        return -common.getStringWidth(mv.getName());
                    }
                    else {
                        return -common.getStringWidth(mv.getPrefix() + mv.getName()) - 2.0f;
                    }
                })).toList();
                float currentY = 0.0f;
                Color background = new Color(0.0f, 0.0f, 0.0f, 0.75f);
                for (int i = 0; i < modules3.size(); ++i) {
                    m = modules3.get(i);
                    Renderer2d.renderQuad(stack, background, -2 + mc.getWindow().getWidth() / 2 - 2 - ((m.getPrefix() == null) ? common.getStringWidth(m.getName()) : (2.0f + common.getStringWidth(m.getPrefix() + m.getName()))) - 2.0f, currentY, mc.getWindow().getWidth() / 2, currentY + common.getFontHeight() + 2.0f);
                    common.drawString(stack, m.getName(), -4 + mc.getWindow().getWidth() / 2 - ((m.getPrefix() == null) ? common.getStringWidth(m.getName()) : (2.0f + common.getStringWidth(m.getPrefix() + m.getName()))), currentY + 1.0f, -16711936);
                    if (m.getPrefix() != null) {
                        common.drawString(stack, m.getPrefix(), -4 + mc.getWindow().getWidth() / 2 - common.getStringWidth(m.getPrefix()), currentY + 1.0f, -1);
                    }
                    currentY += common.getFontHeight() + 2.0f;
                }
                Renderer2d.renderQuad(stack, Color.GREEN, -4 + mc.getWindow().getWidth() / 2 + 2, 0.0, mc.getWindow().getWidth() / 2, (2.0f + common.getFontHeight()) * modules3.size());
                break;
            }
        }
        super.onRender2D(event);
    }
}
