package au.com.zollinger.z80_emu;

public enum State {
    LOW(0),
    HIGH(1);

    private int mVal;

    State(int val) {
        mVal = val;
    }

    public int getVal() {
        return mVal;
    }

    public State inverse() {
        return this == LOW ? HIGH : LOW;
    }

    public static State create(int val) {
        return (val & 1) == 0 ? LOW : HIGH;
    }
}
