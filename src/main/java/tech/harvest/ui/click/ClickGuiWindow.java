package tech.harvest.ui.click;

import me.x150.renderer.render.Renderer2d;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import tech.harvest.HarvestClient;
import tech.harvest.MCHook;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.types.setting.Setting;
import tech.harvest.ui.font.TTFFontRenderer;
import tech.harvest.ui.util.ClickUtil;

import java.awt.*;
import java.util.List;

public class ClickGuiWindow implements MCHook {
    private static final Color backColor = new Color(-13158601);
    private static final Color outlineColor1 = new Color(-14671840);
    private static final Color outlineColor2 = new Color(-13553359);
    private static final TTFFontRenderer font = TTFFontRenderer.of("Roboto-Regular", 8);
    private final Color accentColor;
    private final ModuleCategory category;
    private final List<Module> modules;
    private final boolean[] mExpand;
    private DoubleSetting doubleSetting;
    private float x;
    private float y;
    private float lastX;
    private float lastY;
    private boolean dragging = false;
    private boolean expand = true;

    public ClickGuiWindow(float x, float y, ModuleCategory category) {
        this.x = x;
        this.y = y;
        this.category = category;
        this.modules = HarvestClient.getModuleManager().getModules().stream().filter(m -> m.getCategory() == category).toList();
        this.mExpand = new boolean[this.modules.size()];
        this.accentColor = switch (category) {
            case Misc -> new Color(-812270);
            case Combat -> new Color(-1553348);
            case Movement -> new Color(-13644687);
            case Player -> new Color(-7519059);
            case Render -> new Color(-13172530);
            case Ghost -> new Color(-13330213);
        };
    }

    public void init() {
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.doubleSetting != null) {
            this.doubleSetting.setValue(this.x, 120.0f, mouseX);
        }
        if (this.dragging) {
            this.x = (float) mouseX + this.lastX;
            this.y = (float) mouseY + this.lastY;
        }
        Renderer2d.renderQuad(context.getMatrices(), this.accentColor, this.x - 2.0f, this.y - 2.0f, this.x + 122.0f, this.y + 20.0f);
        Renderer2d.renderQuad(context.getMatrices(), outlineColor1, this.x - 1.0f, this.y - 1.0f, this.x + 121.0f, this.y + 19.0f);
        Renderer2d.renderQuad(context.getMatrices(), new Color(-14277082), this.x, this.y, this.x + 120.0f, this.y + 18.0f);
        Renderer2d.renderQuad(context.getMatrices(), outlineColor1, this.x - 1.0f, this.y + 17.0f, this.x + 121.0f, this.y + 18.0f);
        font.drawStringShadow(context.getMatrices(), this.category.name(), this.x + 4.0f, this.y + 4.0f, -1);
        if (!this.expand) {
            return;
        }
        float currentY = this.y + 18.0f;
        for (int i = 0; i < this.modules.size(); ++i) {
            Module m = this.modules.get(i);
            Renderer2d.renderQuad(context.getMatrices(), this.accentColor, this.x - 2.0f, currentY, this.x + 122.0f, currentY + 20.0f);
            Renderer2d.renderQuad(context.getMatrices(), outlineColor1, this.x - 1.0f, currentY, this.x + 121.0f, currentY + 19.0f);
            Renderer2d.renderQuad(context.getMatrices(), m.isEnabled() ? this.accentColor : backColor, this.x, currentY, this.x + 120.0f, currentY + 18.0f);
            if (m.getKeyCode() == -1) {
                font.drawStringShadow(context.getMatrices(), m.getName(), this.x + 116.0f - font.getStringWidth(m.getName()), currentY + 4.0f, -1);
            } else {
                try {
                    String keyName = GLFW.glfwGetKeyName(m.getKeyCode(), 1);
                    String displayKeyCode = String.format("%s [%s]", m.getName(), keyName == null ? "Unknown" : keyName.toUpperCase());
                    font.drawStringShadow(context.getMatrices(), displayKeyCode, this.x + 116.0f - font.getStringWidth(displayKeyCode), currentY + 4.0f, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            currentY += 18.0f;
            if (!this.mExpand[i]) continue;
            for (int j = 0; j < m.getSettings().size(); ++j) {
                Setting s = m.getSettings().get(j);
                if (!s.isVisible()) continue;
                Renderer2d.renderQuad(context.getMatrices(), this.accentColor, this.x - 2.0f, currentY, this.x + 122.0f, currentY + 20.0f);
                Renderer2d.renderQuad(context.getMatrices(), outlineColor1, this.x - 1.0f, currentY, this.x + 121.0f, currentY + 19.0f);
                if (s instanceof DoubleSetting ds) {
                    String v = String.valueOf(ds.getValue());
                    Renderer2d.renderQuad(context.getMatrices(), outlineColor2, this.x, currentY, this.x + 120.0f, currentY + 18.0f);
                    Renderer2d.renderQuad(context.getMatrices(), this.accentColor, this.x, currentY + 2.0f, (double) this.x + ds.getPercentage() * 120.0, currentY + 16.0f);
                    font.drawStringShadow(context.getMatrices(), s.getName(), this.x + 4.0f, currentY + 4.0f, -3092272);
                    font.drawStringShadow(context.getMatrices(), v, this.x + 116.0f - font.getStringWidth(v), currentY + 4.0f, -1);
                } else if (s instanceof ModeSetting ms) {
                    Renderer2d.renderQuad(context.getMatrices(), outlineColor2, this.x, currentY, this.x + 120.0f, currentY + 18.0f);
                    font.drawStringShadow(context.getMatrices(), s.getName(), this.x + 4.0f, currentY + 4.0f, -3092272);
                    font.drawStringShadow(context.getMatrices(), ms.getValue(), this.x + 116.0f - font.getStringWidth(ms.getValue()), currentY + 4.0f, -1);
                    if (ms.expand) {
                        for (String o : ms.getOption()) {
                            Renderer2d.renderQuad(context.getMatrices(), this.accentColor, this.x - 2.0f, currentY += 18.0f, this.x + 122.0f, currentY + 20.0f);
                            Renderer2d.renderQuad(context.getMatrices(), outlineColor1, this.x - 1.0f, currentY, this.x + 121.0f, currentY + 19.0f);
                            Renderer2d.renderQuad(context.getMatrices(), outlineColor2, this.x, currentY, this.x + 120.0f, currentY + 18.0f);
                            font.drawStringShadow(context.getMatrices(), o, this.x + 4.0f, currentY + 4.0f, ms.getValue().equals(o) ? -1 : -3092272);
                        }
                    }
                } else if (s instanceof BooleanSetting bs) {
                    Renderer2d.renderQuad(context.getMatrices(), bs.getValue() ? this.accentColor : outlineColor2, this.x, currentY, this.x + 120.0f, currentY + 18.0f);
                    font.drawStringShadow(context.getMatrices(), s.getName(), this.x + 4.0f, currentY + 4.0f, -3092272);
                }
                currentY += 18.0f;
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (ClickUtil.isHovered(this.x, this.y, 140.0, 18.0, mouseX, mouseY)) {
            if (button == 0) {
                this.lastX = (float) ((double) this.x - mouseX);
                this.lastY = (float) ((double) this.y - mouseY);
                this.dragging = true;
            } else {
                this.expand = !this.expand;
            }
            return;
        }
        if (!this.expand) {
            return;
        }
        double currentY = this.y + 18.0f;
        for (int i = 0; i < this.modules.size(); ++i) {
            Module m = this.modules.get(i);
            if (ClickUtil.isHovered2(this.x - 2.0f, currentY, this.x + 122.0f, currentY + 20.0, mouseX, mouseY)) {
                if (button == 0) {
                    m.toggle();
                } else {
                    this.mExpand[i] = !this.mExpand[i];
                }
                return;
            }
            currentY += 18.0;
            if (!this.mExpand[i]) continue;
            for (int j = 0; j < m.getSettings().size(); ++j) {
                Setting s = m.getSettings().get(j);
                if (!s.isVisible()) continue;
                if (s instanceof DoubleSetting) {
                    if (ClickUtil.isHovered2(this.x, currentY, this.x + 120.0f, currentY + 18.0, mouseX, mouseY)) {
                        this.doubleSetting = (DoubleSetting) s;
                        return;
                    }
                } else if (s instanceof ModeSetting ms) {
                    if (ClickUtil.isHovered2(this.x, currentY, this.x + 120.0f, currentY + 18.0, mouseX, mouseY)) {
                        if (button == 0) {
                            ms.increment(true);
                        } else {
                            ms.expand = !ms.expand;
                        }
                        return;
                    }
                    if (ms.expand) {
                        for (String o : ms.getOption()) {
                            if (!ClickUtil.isHovered2(this.x - 2.0f, currentY += 18.0, this.x + 122.0f, currentY + 20.0, mouseX, mouseY))
                                continue;
                            ms.setValue(o);
                            return;
                        }
                    }
                } else if (s instanceof BooleanSetting bs) {
                    if (ClickUtil.isHovered2(this.x, currentY, this.x + 120.0f, currentY + 18.0, mouseX, mouseY)) {
                        bs.switchValue();
                        return;
                    }
                }
                currentY += 18.0;
            }
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        this.dragging = false;
        this.doubleSetting = null;
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }
}
