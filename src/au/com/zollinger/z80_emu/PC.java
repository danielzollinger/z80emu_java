package au.com.zollinger.z80_emu;

public class PC extends Reg16 {
    private final Mem mem;

    public PC(Mem mem) {
        super("PC");
        this.mem = mem;
    }

    public byte next8() {
        byte x = mem.read8(get());
        set((short)(get() + 1));
        return x;
    }

    public short next16() {
        int x = next8();
        int y = next8();
        return (short)(y << 8 | x);
    }

    public void jumpRelative() {
        set((short)(get() + next8()));
    }

    public void jump() {
        set(next16());
    }

    public void jump(short addr) {
        set(addr);
    }

    public void back(int n) {
        set((short)(get() - n));
    }
}
