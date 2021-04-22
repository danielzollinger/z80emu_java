package au.com.zollinger.z80_emu;

public class Reg extends UpdatingVal {
    public final String name;

    protected Reg(String name, int bits, int initialValue) {
        super(bits, initialValue);
        this.name = name;
    }

    public void reset() {
        setInt(0);
    }

    @Override
    public String toString() {
        return String.format("%s:%s", name, hex());
    }

}