package au.com.zollinger.z80_emu;

import java.util.HashMap;
import java.util.Map;

public class UpdatingVal {
    private static final int BITS_PER_HEX = 4;

    private Map<ValChangedHandler,Object> mList;
    private int mVal;
    private int mBitWidth;

    private int maxVal() {
        return (1 << mBitWidth) - 1;
    }

    protected UpdatingVal(int bits, int initialValue) {
        mList = new HashMap<>();
        mVal = initialValue;
        mBitWidth = bits;
    }

    protected void setInt(int val) {
        if(val != this.getInt()) {
            if(val < 0 || val > maxVal()) {
                throw new IllegalArgumentException(val + " is too large");
            }
            mVal = val;
            for (ValChangedHandler vcr : mList.keySet()) {
                vcr.update(mList.get(vcr), val);
            }
        }
    }

    protected int getInt() {
        return mVal;
    }

    public String hex() {
        int hexLen = (mBitWidth + BITS_PER_HEX - 1) / BITS_PER_HEX;
        int val = mVal & ((1 << mBitWidth) - 1);
        return String.format("%0" + hexLen + "X", val);
    }

    public void addValChangeHandler(ValChangedHandler vcr, Object opaque) {
        mList.put(vcr, opaque);
    }
}
