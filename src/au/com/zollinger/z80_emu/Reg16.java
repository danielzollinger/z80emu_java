package au.com.zollinger.z80_emu;

public class Reg16 extends Reg implements ValChangedHandler {
    public final Reg8 ober;
    public final Reg8 nieder;
    private boolean mUpdatingVal;

    private static String setName(Reg... regs) {
        StringBuilder sb = new StringBuilder();
        boolean isAlt = false;
        for(Reg r : regs) {
            if (r.name.endsWith("'")) {
                isAlt = true;
                sb.append(r.name.replaceFirst("'", ""));
            } else {
                sb.append(r.name);
            }
        }
        if (isAlt) {
            sb.append("'");
        }
        return sb.toString();
    }

    private Reg16(String name, Reg8 ober, Reg8 nieder) {
        super(name, Short.SIZE, 0);
        this.ober = ober;
        this.ober.addValChangeHandler(this, false);
        this.nieder = nieder;
        this.nieder.addValChangeHandler(this, true);
    }

    public Reg16(String name) {
        this(name, new Reg8(name + "o"), new Reg8(name + "n"));
    }

    public Reg16(Reg8 ober, Reg8 nieder) {
        this(setName(ober, nieder), ober, nieder);
    }

    public void set(short val) {
        if (val != get()) {
            mUpdatingVal = true;
            ober.set((byte) ((val >> 8) & 0xff));
            nieder.set((byte) (val & 0xff));
            mUpdatingVal = false;
            setInt(val);
        }
    }

    public short get() {
        return (short)getInt();
    }

    public void update(Object opaque, int val) {
        if (!mUpdatingVal) {
            byte niederVal = (boolean)opaque ? (byte)val : nieder.get();
            byte oberVal = (boolean)opaque ? ober.get() : (byte)val;
            setInt(((oberVal << 8) | niederVal));
        }
    }
}