package tech.harvest.core.util;

import net.minecraft.client.util.InputUtil;
import tech.harvest.MCHook;
import tech.harvest.core.features.event.MoveEvent;
import net.minecraft.entity.effect.StatusEffects;

public class MoveUtil implements MCHook {
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873;
        if (mc.player.getStatusEffects().stream().toList().stream().anyMatch(p -> p.getEffectType() == StatusEffects.SPEED)) {
            int amp = MoveUtil.mc.player.getStatusEffects().stream().toList().stream().filter(p -> p.getEffectType() == StatusEffects.SPEED).findAny().get().getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (double)(amp + 1);
        }
        return baseSpeed;
    }

    public static double getSpeed() {
        return mc.player.getVelocity().horizontalLength();
    }

    public static double getSpeed2() {
        double motx=mc.player.getVelocity().x;
        double motz=mc.player.getVelocity().z;
        return Math.sqrt(motx*motx+motz*motz);
    }

    public static boolean isMoving() {
        return mc.options.forwardKey.isPressed() || mc.options.backKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.leftKey.isPressed();
    }

    public static int InputY() {
        return (mc.options.jumpKey.isPressed() ? 1 : 0) + (mc.options.sneakKey.isPressed() ? -1 : 0);
    }

    public static void strafe() {
        double yaw = getDirection(RotationUtil.virtualYaw);
        mc.player.setVelocity((-Math.sin((float) yaw)) * getSpeed(), mc.player.getVelocity().y, Math.cos((float) yaw) * getSpeed());
    }

    public static void strafe(float speed) {
        double yaw = getDirection(mc.player.getYaw());
        mc.player.setVelocity((-Math.sin((float) yaw)) * speed, mc.player.getVelocity().y, Math.cos((float) yaw) * speed);
    }

    public static void strafe(float speed, float yaw1) {
        double yaw = getDirection(yaw1);
        mc.player.setVelocity((-Math.sin((float) yaw)) * speed, mc.player.getVelocity().y, Math.cos((float) yaw) * speed);
    }

    public static double getDirection(float yaw) {
        float rotationYaw = yaw;
        if (mc.options.backKey.isPressed()) {
            rotationYaw += 180.0f;
        }
        float forward = 1.0f;
        if (mc.options.backKey.isPressed()) {
            forward = -0.5f;
        } else if (mc.options.forwardKey.isPressed()) {
            forward = 0.5f;
        }
        if (mc.options.leftKey.isPressed()) {
            rotationYaw -= 90.0f * forward;
        }
        if (mc.options.rightKey.isPressed()) {
            rotationYaw += 90.0f * forward;
        }
        return Math.toRadians(rotationYaw);
    }

    public static float getdir() {
        boolean isRight = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.rightKey.getBoundKeyTranslationKey()).getCode());
        boolean isLeft = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.leftKey.getBoundKeyTranslationKey()).getCode());
        boolean isForward = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.forwardKey.getBoundKeyTranslationKey()).getCode());
        boolean isBack = InputUtil.isKeyPressed(mc.getWindow().getHandle(), InputUtil.fromTranslationKey(mc.options.backKey.getBoundKeyTranslationKey()).getCode());

        if (!isRight && !isLeft && !isForward && !isBack) {
            return -1;
        }

        float yaw = mc.player.getYaw();

        if (isBack) {
            yaw -= 180;
        }

        if (isLeft) {
            yaw -= isForward || isBack ? 45 : 90;
        }

        if (isRight) {
            yaw += isForward || isBack ? 45 : 90;
        }

        return yaw;
    }

    public static float getmf() {
        int indexctnj=0;
        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.forwardKey.getDefaultKey().getCode())) {
            indexctnj++;
        }
        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.backKey.getDefaultKey().getCode())) {
            indexctnj--;
        }
        return indexctnj;
    }

    public static float getms() {
        int indexctnj=0;
        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.rightKey.getDefaultKey().getCode())) {
            indexctnj++;
        }
        if(InputUtil.isKeyPressed(mc.getWindow().getHandle(), mc.options.leftKey.getDefaultKey().getCode())) {
            indexctnj--;
        }
        return indexctnj;
    }

    public static double nextSpeed(double d) {
        return d * 0.9900000095367432d;
    }

    public static double nextY(double y) {
        return (y - .08d) * 0.9800000190734863d;
    }

    public static double nextVelocity(double velocity, double ticks) {
        double a = -0.08;
        double b = 0.98;
        velocity += a * ticks;
        velocity *= Math.pow(b, ticks);
        return velocity;
    }

    public static void strafe(MoveEvent event, double speed) {
        double yaw = getDirection(mc.player.getYaw());
        event.x = (-Math.sin((float) yaw)) * speed;
        event.z = Math.cos((float) yaw) * speed;
    }
}
