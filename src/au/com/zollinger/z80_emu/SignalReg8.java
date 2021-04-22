package au.com.zollinger.z80_emu;

import java.util.HashMap;
import java.util.Map;

public class SignalReg8 extends Reg8 implements ValChangedHandler {
    protected Map<String, SignalBit> mByName;
    protected Map<Integer, SignalBit> mByBit;
    private boolean mUpdating8;

    public byte get() {
        int val = 0;
        for (int k : mByBit.keySet()) {
            if(mByBit.get(k).get() == State.HIGH) {
                val |= 1 << k;
            }
        }
        return (byte)(val & 0xff);
    }

    public void set(byte val) {
        mUpdating8 = true;
        for (int k : mByBit.keySet()) {
            mByBit.get(k).set(((val >> k) & 1) == 1);
        }
        super.set(val);
        mUpdating8 = false;
    }

    public SignalReg8(String name, SignalBit... signals) {
        super(name);
        mUpdating8 = false;
        mByName = new HashMap<>();
        mByBit = new HashMap<>();
        for (SignalBit signal : signals) {
            mByName.put(signal.name, signal);
            mByBit.put(signal.bit, signal);
            signal.addValChangeHandler(this, signal.bit);
        }
    }

    @Override
    public void reset() {
        mUpdating8 = true;
        for (int k : mByBit.keySet()) {
            mByBit.get(k).deactivate();
        }
        mUpdating8 = false;
    }

    public SignalBit byName(String name)
    {
        return mByName.get(name);
    }

    public SignalBit byBit(int bit)
    {
        return mByBit.get(bit);
    }

    public void update(Object opaque, int newBitVal) {
        if (!mUpdating8) {
            int bit = (int)opaque;
            byte val = get();
            val = Bitwise.writeBit(val, newBitVal, bit);
            super.set(val);
        }
    }
}



