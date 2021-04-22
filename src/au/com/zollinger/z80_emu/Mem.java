package au.com.zollinger.z80_emu;

public class Mem {
    private byte[] mMem;
    private int mRamStart;

    public Mem(int ramStart, byte[] programData) {
        mMem = new byte[Constants.ADDRESSABLE_SIZE];
        mRamStart = ramStart;
        if(programData != null) {
            for(int i = 0; i < programData.length; i++) {
                mMem[i] = programData[i];
            }
        }
    }

    public void reset() {
    }

    public void write(short addr, byte data) {
        if(addr >= mRamStart && addr <= 0xffff) {
            mMem[addr] = data;
        }
    }

    public void write(short addr, short data) {
        write(addr, (byte)(data & 0xff));
        write((short)(addr + 1), (byte)((data >> 8) & 0xff));
    }

    public byte read8(short addr) {
        return mMem[addr];
    }

    public short read16(short addr) {
        int x = read8((short)(addr + 1));
        x <<= 8;
        x |= read8(addr);
        return (short)(x & 0xffff);
    }
}

