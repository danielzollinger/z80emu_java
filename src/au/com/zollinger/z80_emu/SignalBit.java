package au.com.zollinger.z80_emu;

public class SignalBit extends Signal {
    public final int bit;

    public SignalBit(String name, int bit, State active, State initialState) {
        super(name, active, initialState);
        this.bit = bit;
    }
}
