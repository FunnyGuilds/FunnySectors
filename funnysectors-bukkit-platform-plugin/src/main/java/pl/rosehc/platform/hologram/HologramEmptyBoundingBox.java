package pl.rosehc.platform.hologram;

import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.MovingObjectPosition;
import net.minecraft.server.v1_8_R3.Vec3D;

public final class HologramEmptyBoundingBox extends AxisAlignedBB {

  public HologramEmptyBoundingBox() {
    super(0D, 0D, 0D, 0D, 0D, 0D);
  }

  @Override
  public double a(final AxisAlignedBB arg0, final double arg1) {
    return 0D;
  }

  @Override
  public AxisAlignedBB a(final AxisAlignedBB arg0) {
    return this;
  }

  @Override
  public AxisAlignedBB a(final double arg0, final double arg1, final double arg2) {
    return this;
  }

  @Override
  public MovingObjectPosition a(final Vec3D arg0, final Vec3D arg1) {
    return super.a(arg0, arg1);
  }

  @Override
  public boolean a(final Vec3D arg0) {
    return false;
  }

  @Override
  public double b(final AxisAlignedBB arg0, final double arg1) {
    return 0D;
  }

  @Override
  public boolean b(final AxisAlignedBB arg0) {
    return false;
  }

  @Override
  public double c(final AxisAlignedBB arg0, final double arg1) {
    return 0D;
  }

  @Override
  public AxisAlignedBB c(final double arg0, final double arg1, final double arg2) {
    return this;
  }

  @Override
  public AxisAlignedBB grow(final double arg0, final double arg1, final double arg2) {
    return this;
  }

  @Override
  public AxisAlignedBB shrink(final double arg0, final double arg1, double arg2) {
    return this;
  }
}
