package au.com.zollinger.z80_emu;

public class Reg8 extends Reg {
    public void set(byte val) {
        super.setInt(val);
    }

    public byte get() {
        return (byte)super.getInt();
    }

    public Reg8(String name) {
        super(name, Byte.SIZE, 0);
    }
}
