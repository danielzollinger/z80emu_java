package au.com.zollinger.z80_emu;

public class Bitwise {
    public static int setBit(int val, int bit) {
        checkBitInRange(Integer.SIZE, bit);
        return val | (1 << bit);
    }

    public static short setBit(short val, int bit) {
        checkBitInRange(Short.SIZE, bit);
        return (short)(val | (1 << bit));
    }

    public static byte setBit(byte val, int bit) {
        checkBitInRange(Byte.SIZE, bit);
        return (byte)(val | (1 << bit));
    }

    public static int clearBit(int val, int bit) {
        checkBitInRange(Integer.SIZE, bit);
        return val & ~(1 << bit);
    }

    public static short clearBit(short val, int bit) {
        checkBitInRange(Short.SIZE, bit);
        return (short)(val & ~(1 << bit));
    }

    public static byte clearBit(byte val, int bit) {
        checkBitInRange(Byte.SIZE, bit);
        return (byte)(val & ~(1 << bit));
    }

    public static int writeBit(int val, int bitVal, int bit) {
        if (bitVal == 0) {
            return clearBit(val, bit);
        } else {
            return setBit(val, bit);
        }
    }

    public static short writeBit(short val, int bitVal, int bit) {
        if (bitVal == 0) {
            return clearBit(val, bit);
        } else {
            return setBit(val, bit);
        }
    }

    public static byte writeBit(byte val, int bitVal, int bit) {
        if (bitVal == 0) {
            return clearBit(val, bit);
        } else {
            return setBit(val, bit);
        }
    }

    public int readBit(int val, int bit) {
        checkBitInRange(Integer.SIZE, bit);
        return (val & (1 << bit)) >> bit;
    }

    public short readBit(short val, int bit) {
        checkBitInRange(Short.SIZE, bit);
        return (short)((val & (1 << bit)) >> bit);
    }

    public byte readBit(byte val, int bit) {
        checkBitInRange(Byte.SIZE, bit);
        return (byte)((val & (1 << bit)) >> bit);
    }

    private static int getMask(int width, int bit) throws IllegalArgumentException {
        checkBitInRange(width, bit);
        return (1 << bit) - 1;
    }

    private static void checkBitInRange(int width, int bit) throws IllegalArgumentException {
        if(bit < 0 || bit > width  - 1) {
            throw new IllegalArgumentException("Bit out of range");
        }
    }
}
