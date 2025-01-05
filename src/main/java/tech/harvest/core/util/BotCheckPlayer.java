package tech.harvest.core.util;

import com.mojang.authlib.GameProfile;

public class BotCheckPlayer {
	public long getRequiredMs() {
		return RequiredMs;
	}

	public void setRequiredMs(long requiredMs) {
		RequiredMs = requiredMs;
	}

	public GameProfile profile;
	public long RequiredMs;
	
	public BotCheckPlayer(GameProfile packet2, long requiredMs2) {
		profile = packet2;
		RequiredMs = requiredMs2;
	}

	public GameProfile getProfile() {
		return profile;
	}

	public void setProfile(GameProfile profile) {
		this.profile = profile;
	}
}