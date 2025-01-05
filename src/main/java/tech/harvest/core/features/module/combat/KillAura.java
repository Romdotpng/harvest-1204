package tech.harvest.core.features.module.combat;

import java.awt.Color;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;
import tech.harvest.core.features.event.ClickTickEvent;
import tech.harvest.core.features.event.InputEvent;
import tech.harvest.core.features.event.PostRender3DEvent;
import tech.harvest.core.features.event.PreUpdateEvent;
import tech.harvest.core.features.event.RotationEvent;
import tech.harvest.core.features.event.SendPacketEvent;
import tech.harvest.core.features.module.render.ESP;
import tech.harvest.core.features.setting.BooleanSetting;
import tech.harvest.core.features.setting.DoubleSetting;
import tech.harvest.core.features.setting.ModeSetting;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import tech.harvest.core.util.AlgebraUtil;
import tech.harvest.core.util.RandomUtil;
import tech.harvest.core.util.RotationUtil;
import tech.harvest.core.util.TimerUtil;
import tech.harvest.core.util.legit.LegitEntityRotation;
import tech.harvest.mixin.client.MinecraftClientAccessor;
import me.x150.renderer.render.Renderer3d;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

public class KillAura extends Module {
    private static final Comparator<LivingEntity> DISTANCE_COMP = Comparator.comparingDouble(e -> KillAura.mc.player.squaredDistanceTo(AlgebraUtil.nearest(e.getBoundingBox(), KillAura.mc.player.getEyePos())));
    private static final Comparator<LivingEntity> HEALTH_COMP = Comparator.comparingDouble(LivingEntity::getHealth);
    private static final Color COLOR = new Color(255, 0, 0, 100);
    private static int switchIndex;
    private static LivingEntity target;
    private final BooleanSetting legitAura = BooleanSetting.build().name("Legit Aura").value(false).end();
    private final ModeSetting mode = ModeSetting.build().name("Mode").option("Single", "Switch").end();
    private final BooleanSetting animals = BooleanSetting.build().name("Animals").value(true).end();
    private final BooleanSetting monsters = BooleanSetting.build().name("Monsters").value(true).end();
    private final BooleanSetting players = BooleanSetting.build().name("Players").value(true).end();
    private final BooleanSetting tick = BooleanSetting.build().name("Tick").value(true).end();
    private final ModeSetting teams = ModeSetting.build().name("Teams").option("None", "Normal").end();
    private final DoubleSetting loadRange = DoubleSetting.build().name("Load Range").value(4.0).range(0.0, 8.0).unit("Blocks").visibility(() -> !this.tick.getValue()).onSetting(v -> {
        this.loadDistSq = v * v;
    }).end();
    private final DoubleSetting minCPS = DoubleSetting.build().name("Min CPS").value(10.0).range(0.0, 20.0).unit("CPS").visibility(() -> !this.tick.getValue()).end();
    private final DoubleSetting maxCPS = DoubleSetting.build().name("Max CPS").value(12.0).range(0.0, 20.0).unit("CPS").end();
    private final LegitEntityRotation ROT = new LegitEntityRotation();
    private final TimerUtil attackTimer = new TimerUtil();
    private final float[] serverSide = new float[2];
    private final Map<Integer, Long> tickMap = new HashMap<>();
    private Comparator<LivingEntity> comparator = DISTANCE_COMP;
    private final ModeSetting sort = ModeSetting.build().name("Sort").visibility(this.players::getValue).option("Distance", "Health", "Low Armor", "High Armor", "Fov").onSetting(v -> {
        switch (v) {
            case "Distance": {
                this.comparator = DISTANCE_COMP;
                break;
            }
            case "Health": {
                this.comparator = HEALTH_COMP;
            }
        }
    }).end();
    private double attackDistSq = 9.0;
    private double loadDistSq = 16.0;
    private final DoubleSetting attackRange = DoubleSetting.build().name("Attack Range").value(3.0).range(0.0, 8.0).unit("Blocks").onSetting(v -> {
        this.attackDistSq = v * v;
    }).end();
    private final DoubleSetting lagRange = DoubleSetting.build().name("Lag Range").value(250.0).range(0.0, 5000.0).unit("ms").visibility(this.legitAura::getValue).end();
    private final ModeSetting critical = ModeSetting.build().name("Critical").option("None", "Matrix").value("None").end();
    private double currentCPS;
    private boolean skipPacket;
    private long lastInRangeTime;

    public KillAura() {
        super("KillAura", "Attacks entities around you", ModuleCategory.Combat);
        this.setKeyCode(82);
        this.getSettings().addAll(Arrays.asList(this.mode, this.animals, this.monsters, this.players, this.teams, this.sort, this.attackRange, this.loadRange, this.minCPS, this.maxCPS, this.legitAura, this.tick, this.lagRange, this.critical));
    }

    @Override
    public void onEnable() {
        target = null;
        super.onEnable();
    }

    @Override
    public String getPrefix() {
        if (target != null) {
            return target.getName().getString();
        }
        return "";
    }

    @Override
    public void onRotation(RotationEvent event) {
        //Matrix rotation
        if (target != null && this.legitAura.getValue()) {
            this.ROT.setEntity(target);
            float[] z = mc.crosshairTarget instanceof EntityHitResult ? new float[]{mc.player.getYaw(), mc.player.getPitch()} : RotationUtil.rotation(AlgebraUtil.nearest(target.getBoundingBox(), mc.player.getEyePos()).add(0.0d, RandomUtil.nextFloat(-0.1f, 0.1f), 0.0d), mc.player.getEyePos());
            if (target.hurtTime > 5 || !(mc.crosshairTarget instanceof EntityHitResult)) {
                z = RotationUtil.rotation(AlgebraUtil.nearest(target.getBoundingBox(), mc.player.getEyePos()).add(0.0d, RandomUtil.nextFloat(-0.1f, 0.1f), 0.0d), mc.player.getEyePos());
                z[0] = RotationUtil.smoothRot(mc.player.getYaw(), z[0], RandomUtil.nextFloat(0.0f, 5.0f) * 4.0f);
                z[1] = RotationUtil.smoothRot(mc.player.getPitch(), z[1], RandomUtil.nextFloat(0.0f, 5.0f) * 4.0f);
            }
            z[0] = RotationUtil.smoothRot(mc.player.getYaw(), z[0], RandomUtil.nextFloat(0.0f, 5.0f) * 10.0f);
            z[1] = RotationUtil.smoothRot(mc.player.getPitch(), z[1], RandomUtil.nextFloat(0.0f, 5.0f) * 10.0f);
            event.setYaw(z[0]);
            event.setPitch(z[1]);
            super.onRotation(event);
        }
    }

    @Override
    public  void onDisable() {
        target = null;
        super.onDisable();
    }

    @Override
    public void onClickTick(ClickTickEvent event) {
        if (target == null) {
            return;
        }
        if (this.currentCPS == 0.0) {
            this.currentCPS = 1.0;
        }
        if (this.attackTimer.hasTimeElapsed((long)(1000.0 / this.currentCPS)) && mc.player.getEyePos().distanceTo(AlgebraUtil.nearest(target.getBoundingBox(), mc.player.getEyePos())) < this.attackRange.getValue()) {
            this.attackTimer.reset();
            ++switchIndex;
            this.currentCPS = RandomUtil.nextDouble(this.minCPS.getValue(), this.maxCPS.getValue());
            if (!this.tickMap.containsKey(target.getId())) {
                this.tickMap.put(target.getId(), 0L);
            }
            if (this.tick.getValue()) {
                if (System.currentTimeMillis() - this.tickMap.get(target.getId()) > 500L) {
                    this.handleAttack();
                }
            } else {
                this.handleAttack();
            }
        }
        super.onClickTick(event);
    }

    private void handleAttack() {
        this.attackTimer.reset();
        if (this.legitAura.getValue()) {
            EntityHitResult entity;
            Vec3d vec = mc.player.getVelocity();
            if (mc.crosshairTarget instanceof EntityHitResult) {
                ((MinecraftClientAccessor)mc).accessDoAttack();
            }
            mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            mc.player.swingHand(Hand.MAIN_HAND);
            mc.player.setVelocity(vec);
            HitResult hitResult = mc.crosshairTarget;
            if (hitResult instanceof EntityHitResult && (entity = (EntityHitResult)hitResult).getEntity() == target) {
                ++switchIndex;
                this.tickMap.put(target.getId(), System.currentTimeMillis());
            }
        } else {
            Vec3d vec = mc.player.getVelocity();
            mc.getNetworkHandler().sendPacket(PlayerInteractEntityC2SPacket.attack(target, mc.player.isSneaking()));
            mc.player.swingHand(Hand.MAIN_HAND);
            ++switchIndex;
            this.tickMap.put(target.getId(), System.currentTimeMillis());
            mc.player.setVelocity(vec);
        }
        this.currentCPS = RandomUtil.nextDouble(this.minCPS.getValue(), this.maxCPS.getValue());
    }

    @Override
    public void onInput(InputEvent event) {
        super.onInput(event);
    }

    @Override
    public void onPreUpdate(PreUpdateEvent event) {
        target = null;
        Vec3d eye = mc.player.getEyePos();
        List<LivingEntity> targetEntry = StreamSupport.stream(mc.world.getEntities().spliterator(), false).filter(e -> e instanceof LivingEntity && e != mc.player).map(e -> (LivingEntity)e).filter(e -> {
            if (Antibot.isToggledStatic() && (e.getHealth() == 20.0f || mc.getNetworkHandler().getPlayerListEntry(e.getUuid()) == null)) {
                return false;
            }
            if (e instanceof AnimalEntity) {
                return this.animals.getValue();
            }
            if (e instanceof MobEntity) {
                return this.monsters.getValue();
            }
            if (e instanceof PlayerEntity) {
                if (this.players.getValue()) {
                    return switch (this.teams.getValue()) {
                        case "None" -> true;
                        case "Normal" -> {
                            if (mc.player.getTeamColorValue() != e.getTeamColorValue()) {
                                yield true;
                            }
                            yield false;
                        }
                        default -> false;
                    };
                }
                return false;
            }
            return true;
        }).filter(e -> eye.squaredDistanceTo(AlgebraUtil.nearest(e.getBoundingBox(), eye)) < this.loadDistSq).sorted(this.comparator).toList();
        if (targetEntry.isEmpty()) {
            return;
        }
        target = this.mode.getValue().equals("Switch") ? targetEntry.get(Math.min(switchIndex % targetEntry.size(), targetEntry.size() - 1)) : (this.mode.getValue().equals("Multi") ? targetEntry.get(Math.min(switchIndex % targetEntry.size(), targetEntry.size() - 1)) : targetEntry.get(0));
        super.onPreUpdate(event);
    }

    @Override
    public void onPostRender3D(PostRender3DEvent event) {
        if (target == null) {
            return;
        }
        Vec3d eSource = ESP.getInterpolatedEntityPosition(target);
        Renderer3d.renderFilled(event.getMatrices(), COLOR, eSource.subtract(new Vec3d(target.getWidth(), 0.0, target.getWidth()).multiply(0.5)), new Vec3d(target.getWidth(), target.getHeight(), target.getWidth()));
        super.onPostRender3D(event);
    }

    @Override
    public void onSendPacket(SendPacketEvent event) {
        if (target == null) {
            return;
        }
        if (this.legitAura.getValue()) {
            return;
        }
        if (this.skipPacket) {
            return;
        }
        if (!(event.getPacket() instanceof PlayerMoveC2SPacket)) {
            return;
        }
        this.ROT.setEntity(target);
        float[] z = RotationUtil.rotation(AlgebraUtil.nearest(target.getBoundingBox(), mc.player.getEyePos()), mc.player.getEyePos());
        z[0] = RotationUtil.getFixedSensitivityAngle(z[0], this.serverSide[0]);
        z[1] = RotationUtil.getFixedSensitivityAngle(z[1], this.serverSide[1]);
        this.serverSide[0] = z[0];
        this.serverSide[1] = z[1];
        boolean isMatrixCritical = this.critical.getValue().equals("Matrix");
        Packet<?> packet = event.getPacket();
        if (packet instanceof PlayerMoveC2SPacket.Full p) {
            this.skipPacket = true;
            event.cancel();
            mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.Full(p.getX(mc.player.getX()), p.getY(mc.player.getY()) + (isMatrixCritical ? 1.0E-5 : 0.0), p.getZ(mc.player.getZ()), z[0], z[1], p.isOnGround()));
            this.skipPacket = false;
        } else {
            packet = event.getPacket();
            if (packet instanceof PlayerMoveC2SPacket.LookAndOnGround p) {
                this.skipPacket = true;
                event.cancel();
                mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(z[0], z[1], p.isOnGround()));
                this.skipPacket = false;
            } else {
                packet = event.getPacket();
                if (packet instanceof PlayerMoveC2SPacket.PositionAndOnGround p) {
                    this.skipPacket = true;
                    event.cancel();
                    mc.getNetworkHandler().sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(p.getX(mc.player.getX()), p.getY(mc.player.getY()) + (isMatrixCritical ? 1.0E-5 : 0.0), p.getZ(mc.player.getZ()), p.isOnGround()));
                    this.skipPacket = false;
                }
            }
        }
        super.onSendPacket(event);
    }

    public static LivingEntity getTarget() {
        return target;
    }
}
