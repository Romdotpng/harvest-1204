package tech.harvest.core.features.module.render;

import me.x150.renderer.render.Renderer2d;
import tech.harvest.HarvestClient;
import tech.harvest.core.features.event.Render2DEvent;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.PlayerUtil;
import tech.harvest.ui.font.TTFFontRenderer;

import java.awt.*;

public class HUD extends Module {
    private final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Light", 20);
    private final TTFFontRenderer font1 = TTFFontRenderer.of("medium", 9);
    private final TTFFontRenderer font2 = TTFFontRenderer.of("sf", 20);
    private final TTFFontRenderer font3 = TTFFontRenderer.of("light", 11);
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Sigma").option("Sigma", "Flower", "SimpleFlower").end();

    public HUD() {
        super("HUD", "", ModuleCategory.Render);
        this.getSettings().add(mode);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        switch (this.mode.getValue()) {
            case ("Sigma"): {
                font.drawString(event.getContext().getMatrices(), HarvestClient.CLIENT_NAME, 5.0f, 5.0f, -1);
                break;
            }
            case ("Flower"): {
                //client name version
                font2.drawStringShadow(event.getContext().getMatrices(), HarvestClient.CLIENT_NAME, 1, 2, 0xe000ff00);
                font1.drawStringShadow(event.getContext().getMatrices(), HarvestClient.CLIENT_VERSION, font2.getStringWidth(HarvestClient.CLIENT_NAME), 2, -1);

                //frame
                Renderer2d.renderQuad(event.getContext().getMatrices(), new Color(0x94000000, true), 10, mc.getWindow().getScaledHeight() - 45, 115, mc.getWindow().getScaledHeight() - 5);
                Renderer2d.renderQuad(event.getContext().getMatrices(), new Color(0xe000ff00), 10, mc.getWindow().getScaledHeight() - 45, 115, mc.getWindow().getScaledHeight() - 44);
                Renderer2d.renderQuad(event.getContext().getMatrices(), new Color(0xe000ff00), 10, mc.getWindow().getScaledHeight() - 5, 115, mc.getWindow().getScaledHeight() - 4);
                Renderer2d.renderQuad(event.getContext().getMatrices(), new Color(0xe000ff00), 10, mc.getWindow().getScaledHeight() - 45, 11, mc.getWindow().getScaledHeight() - 5);
                Renderer2d.renderQuad(event.getContext().getMatrices(), new Color(0xe000ff00), 114, mc.getWindow().getScaledHeight() - 45, 115, mc.getWindow().getScaledHeight() - 5);

                //id fps ping
                font3.drawStringShadow(event.getContext().getMatrices(), String.format("ID : %s", mc.player.getName().getString()), 12.5f, mc.getWindow().getScaledHeight() - 35, 0xe000ff00);
                font3.drawStringShadow(event.getContext().getMatrices(), String.format("FPS : %d PING : %d", mc.getCurrentFps(), PlayerUtil.getPing(mc.player)), 12.5f, mc.getWindow().getScaledHeight() - 20, 0xe000ff00);
                break;
            }

            case ("SimpleFlower"): {
                font.drawString(event.getContext().getMatrices(), HarvestClient.CLIENT_NAME, 5.0f, 5.0f, -16711936);
                font.drawString(event.getContext().getMatrices(), HarvestClient.CLIENT_VERSION, 5.0f + font.getStringWidth(HarvestClient.CLIENT_NAME), 5.0f, -1);
                break;
            }
        }
        super.onRender2D(event);
    }
}
