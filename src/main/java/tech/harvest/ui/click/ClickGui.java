package tech.harvest.ui.click;

import java.util.ArrayList;
import java.util.List;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ClickGui extends Screen {
    private final List<ClickGuiWindow> windows = new ArrayList<>();

    public ClickGui() {
        super(Text.literal(""));
        float currentX = 50.0f;
        for (ModuleCategory c : ModuleCategory.values()) {
            this.windows.add(new ClickGuiWindow(currentX, 30.0f, c));
            currentX += 150.0f;
        }
    }

    protected void init() {
        this.windows.forEach(ClickGuiWindow::init);
        ClickGui.super.init();
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        ClickGui.super.render(context, mouseX, mouseY, delta);
        this.windows.forEach(m -> {
            m.render(context, mouseX, mouseY, delta);
        });
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.windows.forEach(m -> {
            m.mouseClicked(mouseX, mouseY, button);
        });
        return ClickGui.super.mouseClicked(mouseX, mouseY, button);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        this.windows.forEach(m -> {
            m.mouseReleased(mouseX, mouseY, button);
        });
        return ClickGui.super.mouseReleased(mouseX, mouseY, button);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        this.windows.forEach(m -> {
            m.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        });
        return ClickGui.super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
}
