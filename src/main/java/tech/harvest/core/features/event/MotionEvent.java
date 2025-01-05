package tech.harvest.core.features.event;

import net.minecraft.util.math.BlockPos;
import tech.harvest.core.types.event.EventArgument;
import tech.harvest.core.types.event.EventListener;

public class MotionEvent extends EventArgument {
	public double x, y, z;
	public float yaw, pitch;
	public boolean onGround;

	public float lastYaw, lastPitch;
	public boolean lastOnGround;
	
	public MotionEvent(double x, double y, double z, float yaw, float pitch, boolean onGround) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
		this.yaw = yaw;
		this.pitch = pitch;
		this.onGround = onGround;
		this.lastYaw = yaw;
		this.lastPitch = pitch;
		this.lastOnGround = onGround;
	}
	
	public void setPosition(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public float[] getServerSideAngles() {
		return new float[]{yaw, pitch};
	}

	public void setPosition(BlockPos pos) {
		this.x = pos.getX() + .5;
		this.y = pos.getY();
		this.z = pos.getZ() + .5;
	}

	@Override
	public void call(EventListener listener) {
		listener.onMotion(this);
	}
}
