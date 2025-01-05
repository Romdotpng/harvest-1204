package tech.harvest.core.features.module.movement;

import net.minecraft.fluid.Fluids;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import tech.harvest.core.features.event.MotionEvent;
import tech.harvest.core.features.event.UpdateVelocityEvent;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.MoveUtil;

import java.text.DecimalFormat;

public class Jesus extends Module {
    private final ModeSetting mode = ModeSetting.build().name("Mode").value("Hikakin").option("Hikakin").end();
    private final BooleanSetting damageBoost = BooleanSetting.build().name("DamageBoost").value(true).end();
    int test0;
    int test1;
    int test2;
    boolean start;

    public Jesus() {
        super("Jesus", "water walk/speed", ModuleCategory.Movement);
    }

    @Override
    public void onEnable() {
        test0 = 0;
        test1 = 0;
        test2 = 0;
        start = false;
    }

    @Override
    public void onUpdateVelocity(UpdateVelocityEvent event) {
        super.onUpdateVelocity(event);
    }

    @Override
    public void onMotion(MotionEvent event) {
        DecimalFormat df = new DecimalFormat("#.####");
        if (!MoveUtil.isMoving()) mc.inGameHud.getChatHud().addMessage(Text.literal("何してんのー"));
        mc.inGameHud.getChatHud().addMessage(Text.literal("BBG " + df.format(MoveUtil.getSpeed())));
        switch (this.mode.getValue()) {
            //Yumi
            case "Hikakin": {
                if (motYstate() != -1) {
                    if (!mc.player.isTouchingWater()) {

                        if ((mc.world.getBlockState(
                                        new BlockPos((int) (mc.player.getX()
                                                + mc.player.getVelocity().x),
                                                (int) (mc.player.getY()
                                                        + mc.player.getVelocity().y),
                                                (int) (mc.player.getZ()
                                                        + mc.player.getVelocity().z)))
                                .getFluidState()
                                .getFluid()
                                == Fluids.WATER)) {
                            double deltaYR =
                                    mc.player.getY() - Math.round(mc.player.getY());
                            double psY = Math.round(mc.player.getY());
                            for (int i = 0; i < (Math.abs(mc.player.getVelocity().y) + 1);
                                 i++) {
                                if ((mc.world
                                        .getBlockState(new BlockPos(
                                                (int) (mc.player.getX()
                                                        + mc.player.getVelocity().x),
                                                (int) (psY - i),
                                                (int) (mc.player.getZ()
                                                        + mc.player.getVelocity().z)))
                                        .getFluidState()
                                        .getFluid()
                                        == Fluids.WATER)) {
                                    mc.player.setVelocity(mc.player.getVelocity().x, -(deltaYR + i - 1 + 0.12), mc.player.getVelocity().z);
                                    break;
                                }
                            }
                        }
                    }
                }
                if (motYstate() == 0 && mc.player.isTouchingWater()) {
                    if (!mc.player.isSubmergedInWater()) {
                        mc.player.setVelocity(mc.player.getVelocity().x, 0, mc.player.getVelocity().z);
                    }
                    mc.player.setVelocity(mc.player.getVelocity().x * 1.294, mc.player.getVelocity().y, mc.player.getVelocity().z * 1.294);
                    //MoveUtil.strafe(0.099f);
                }
                if (motYstate() == 1) {
                    if (mc.player.isTouchingWater() && !mc.player.isSubmergedInWater()) {
                        mc.player.setVelocity(mc.player.getVelocity().x, 0.131, mc.player.getVelocity().z);
                    }

                    if (mc.player.isTouchingWater() && mc.player.isSubmergedInWater()) {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y + 0.01, mc.player.getVelocity().z);
                    }
                }

                if (motYstate() == -1) {
                    if (mc.player.isTouchingWater()) {
                        mc.player.setVelocity(mc.player.getVelocity().x, mc.player.getVelocity().y - 0.25, mc.player.getVelocity().z);
                    }
                }

                boolean canboost = !mc.player.isUsingItem();

                if (!mc.player.isTouchingWater() && start) {
                    mc.player.setVelocity(mc.player.getVelocity().x, 0.131, mc.player.getVelocity().z);
                }

                if (!mc.player.isTouchingWater()) {
                    if (mc.player.isOnGround() || test1 >= 8) {
                        test1 = 0;
                    }

                    if (test1 != 0) {
                        if (canboost) {
                            mc.player.setVelocity(mc.player.getVelocity().x * 2, mc.player.getVelocity().y, mc.player.getVelocity().z * 2);
                        }
                    }

                    test0++;
                } else {
                    test1++;
                    test0 = 0;
                }
                start = mc.player.isTouchingWater();
                break;
            }
        }
        super.onMotion(event);
    }

    private int motYstate() {
        int youkya = 0;
        if(mc.options.jumpKey.isPressed()) {
            youkya++;
        }
        boolean inkya;
        inkya = mc.player.isSneaking();
        if(inkya) {
            youkya--;
        }
        return youkya;
    }
}
