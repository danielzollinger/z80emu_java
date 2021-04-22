package au.com.zollinger.z80_emu;

public class MemRefreshReg extends Reg8 {
    public MemRefreshReg() {
        super("R");
    }

    @Override
    public void reset() {
        // FIXME: Set correct reset value
        set((byte)0);
    }
}
