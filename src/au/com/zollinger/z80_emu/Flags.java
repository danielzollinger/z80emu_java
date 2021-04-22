package au.com.zollinger.z80_emu;

public class Flags extends SignalReg8 {
    public final SignalBit c;
    public final SignalBit n;
    public final SignalBit p;
    public final SignalBit h;
    public final SignalBit z;
    public final SignalBit s;

    public Flags(String name) {
        super(name, new SignalBit("C", 0, State.HIGH, State.LOW),
                new SignalBit("N", 1, State.HIGH, State.LOW),
                new SignalBit("P", 2, State.HIGH, State.LOW),
                new SignalBit("H", 4, State.HIGH, State.LOW),
                new SignalBit("Z", 6, State.HIGH, State.LOW),
                new SignalBit("S", 7, State.HIGH, State.LOW));
        c = byName("C");
        n = byName("N");
        p = byName("P");
        h = byName("H");
        z = byName("Z");
        s = byName("S");
    }

    public String toStringBySignal() {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for(int bit : mByBit.keySet()) {
            if(first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append((mByBit.get(bit).toString()));
        }
        return sb.toString();
    }
}
