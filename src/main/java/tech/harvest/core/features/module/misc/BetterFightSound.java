package tech.harvest.core.features.module.misc;

import tech.harvest.core.features.event.GetPacketEvent;
import tech.harvest.core.types.module.Module;
import tech.harvest.core.types.module.ModuleCategory;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

public class BetterFightSound extends Module {
    private static final SoundEvent[] BAN = {SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK};

    public BetterFightSound() {
        super("BetterFightSound", "Better fight sound", ModuleCategory.Misc);
    }

    @Override
    public void onGetPacket(GetPacketEvent event) {
        PlaySoundS2CPacket packet = (PlaySoundS2CPacket) event.getPacket();
        if (packet instanceof PlaySoundS2CPacket) {
            SoundEvent[] soundEventArr = BAN;
            int length = soundEventArr.length;
            int i = 0;
            while (true) {
                if (i >= length) {
                    break;
                }
                SoundEvent sound = soundEventArr[i];
                if (packet.getSound().value() == sound) {
                    event.cancel();
                    break;
                }
                i++;
            }
        }
        super.onGetPacket(event);
    }
}
