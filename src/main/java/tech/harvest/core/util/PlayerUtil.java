package tech.harvest.core.util;

import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.player.PlayerEntity;
import tech.harvest.MCHook;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.input.Input;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.recipebook.ClientRecipeBook;
import net.minecraft.entity.Entity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.Packet;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.stat.StatHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

public class PlayerUtil implements MCHook {
    public static boolean isBlockUnder(double height) {
        return PlayerUtil.isBlockUnder(height, true);
    }

    public static Block blockRelativeToPlayer(double offsetX, double offsetY, double offsetZ) {
        return PlayerUtil.mc.world.getBlockState(PlayerUtil.mc.player.getBlockPos().add((int)offsetX, (int)offsetY, (int)offsetZ)).getBlock();
    }

    public static boolean isBlockUnder(double height, boolean boundingBox) {
        if (boundingBox) {
            int offset = 0;
            while ((double)offset < height) {
                Box bb = mc.player.getBoundingBox().offset(0.0, -offset, 0.0);
                if (mc.world.canCollide(PlayerUtil.mc.player, bb)) {
                    return true;
                }
                offset += 2;
            }
        } else {
            int offset = 0;
            while ((double)offset < height) {
                if (PlayerUtil.blockRelativeToPlayer(0.0, -offset, 0.0) != Blocks.AIR) {
                    return true;
                }
                ++offset;
            }
        }
        return false;
    }

    public static boolean isBlockUnder() {
        return PlayerUtil.isBlockUnder(mc.player.getEyeY());
    }

    public static List<Vec3d> predictPositions(Entity entity, int tick) {
        ArrayList<Vec3d> positions = new ArrayList<>();
        final Input input = mc.player.input;
        Vec3d playerVelocity = AlgebraUtil.clone(PlayerUtil.mc.player.getVelocity());
        ClientPlayerEntity player = new ClientPlayerEntity(mc, mc.world, new ClientPlayNetworkHandler(mc, new ClientConnection(NetworkSide.CLIENTBOUND), new ClientConnectionState(null, null, null, null, null, null, null)){

            public void sendPacket(Packet<?> packet) {
            }

            public GameProfile getProfile() {
                return MCHook.mc.getNetworkHandler().getProfile();
            }

            public FeatureSet getEnabledFeatures() {
                return MCHook.mc.getNetworkHandler().getEnabledFeatures();
            }
        }, new StatHandler(), new ClientRecipeBook(), entity.isSneaking(), entity.isSprinting()){

            public float getHealth() {
                return this.getMaxHealth();
            }

            public void tickMovement() {
                this.fallDistance = 0.0f;
                super.tickMovement();
            }

            public void tick() {
                this.tickMovement();
            }

            protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
            }

            protected boolean isCamera() {
                return true;
            }

            public void playSound(SoundEvent sound, float volume, float pitch) {
            }

            public void playSound(SoundEvent event, SoundCategory category, float volume, float pitch) {
            }
        };
        player.input = new Input(){

            public void tick(boolean slowDown, float slowDownFactor) {
                this.movementForward = input.movementForward;
                this.movementSideways = input.movementSideways;
                this.pressingForward = input.pressingForward;
                this.pressingBack = input.pressingBack;
                this.pressingLeft = input.pressingLeft;
                this.pressingRight = input.pressingRight;
                this.jumping = input.jumping;
                this.sneaking = input.sneaking;
                if (slowDown) {
                    this.movementSideways *= slowDownFactor;
                    this.movementForward *= slowDownFactor;
                }
            }
        };
        player.init();
        player.copyPositionAndRotation(entity);
        player.copyFrom(entity);
        player.setOnGround(entity.isOnGround());
        for (int i = 0; i < tick; ++i) {
            player.resetPosition();
            ++player.age;
            player.tick();
            positions.add(player.getPos());
        }
        mc.player.setVelocity(playerVelocity);
        return positions;
    }

    public static int getPing(PlayerEntity entity) {
        PlayerListEntry playerListEntry = mc.getNetworkHandler().getPlayerListEntry(entity.getUuid());
        return playerListEntry == null ? -1 : playerListEntry.getLatency();
    }
}