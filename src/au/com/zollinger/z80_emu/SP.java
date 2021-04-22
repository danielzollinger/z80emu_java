package au.com.zollinger.z80_emu;

public class SP extends Reg16 {
    private final Mem mem;

    public SP(Mem mem) {
        super("SP");
        this.mem = mem;
    }

    @Override
    public void reset() {
        // TODO: Set inital SP value
        set((short)0);
    }

    public void push(byte data) {
        set((short)(get() - 1));
        mem.write(get(), data);
    }

    public void push(short data) {
        set((short)(get() - 2));
        mem.write(get(), data);
    }

    public byte pop8() {
        byte x = mem.read8(get());
        set((short)(get() + 1));
        return x;
    }

    public short pop16() {
        short x = mem.read16(get());
        set((short)(get() + 2));
        return x;
    }
}
