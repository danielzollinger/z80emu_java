package au.com.zollinger.z80_emu;

public class IntVectReg extends Reg8 {
    public static final byte INDEX_MAX = 0x7f;
    private final Mem mem;

    public IntVectReg(Mem mem) {
        super("I");
        this.mem = mem;
    }

    @Override
    public void reset()
    {
        // TODO: Set correct reset value
        set((byte)0);
    }

    public short interruptVector(byte index)
    {
        if(index > INDEX_MAX) {
            throw new IllegalArgumentException("index");
        }
        short addr = (short)(get() << 8);
        addr |= (short)(index * 2);
        return mem.read16(addr);
    }
}
