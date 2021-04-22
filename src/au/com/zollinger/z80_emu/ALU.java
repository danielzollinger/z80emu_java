package au.com.zollinger.z80_emu;

public class ALU {
    private final Reg8 A;
    private final Flags F;

    public ALU(Reg8 a, Flags f) {
        A = a;
        F = f;
    }

    public static boolean isOverflow(int val1, int val2, int width, boolean isAdd) {
        boolean overflow = false;

        int sum = (val1 + val2) & (width == 8 ? Byte.MAX_VALUE : Short.MAX_VALUE);
        int overflowVal = width == 8 ? Byte.MAX_VALUE : Short.MAX_VALUE;
        if (isAdd) {
            if (((val1 <= overflowVal) && (val2 <= overflowVal) && (sum > overflowVal)) ||
                    ((val1 > overflowVal) && (val2 > overflowVal) && (sum <= overflowVal))) {
                overflow = true;
            }
        } else {
            if (((val1 <= overflowVal) && (val2 > overflowVal) && (sum > overflowVal)) ||
                    ((val1 > overflowVal) && (val2 <= overflowVal) && (sum > overflowVal))) {
                overflow = true;
            }
        }
        return overflow;
    }

    public static boolean isHalfCarry(int val1, int val2, int width) {
        int mask = ~(-1 << (width - 4));
        return ((val1 & mask) + (val2 & mask)) > mask;
    }

    public static boolean isHalfBorrow(int val1, int val2, int width) {
        int mask = ~(-1 << (width - 4));
        return (val1 & mask) < (val2 & mask);
    }

    public static boolean isParity(byte val) {
        int count = 0;
        for (int i = 0; i < 8; i++) {
            count += val & 1;
            val >>= 1;
        }
        return count % 2 == 1;
    }

    public byte increment(byte val, boolean alterFlags) {
        int result = val + 1;
        byte resultByte = (byte)result;
        if (alterFlags) {
            F.s.set(resultByte > 0x7f);
            F.z.set(resultByte == 0);
            F.h.set(isHalfCarry(val, 1, 8));
            F.p.set(val == 0x7f);
            F.n.set(false);
        }
        return resultByte;
    }

    public byte increment(byte val) {
        return increment(val, true);
    }

    public void increment(Reg8 reg, boolean alterFlags)
    {
        reg.set(increment(reg.get(), alterFlags));
    }

    public void increment(Reg8 reg) {
        increment(reg, true);
    }

    public byte decrement(byte val, boolean alterFlags) {
        int result = val - 1;
        byte resultByte = (byte)result;
        if (alterFlags) {
            F.s.set(resultByte > 0x7f);
            F.z.set(resultByte == 0);
            F.h.set(isHalfBorrow(val, 1, 8));
            F.p.set(val == 0x80);
            F.n.set(true);
        }
        return resultByte;
    }

    public byte decrement(byte val) {
        return decrement(val, true);
    }

    public void decrement(Reg8 reg, boolean alterFlags) {
        reg.set(decrement(reg.get(), alterFlags));
    }

    public void decrement(Reg8 reg) {
        decrement(reg, true);
    }

    public short increment(short val) {
        return (short)(val + 1);
    }

    public void increment(Reg16 reg) {
        reg.set(increment(reg.get()));
    }

    public short decrement(short val) {
        return (short)(val - 1);
    }

    public void decrement(Reg16 reg) {
        reg.set(decrement(reg.get()));
    }

    public void incOrDec(Reg16 reg, boolean isIncrement) {
        if(isIncrement) {
            increment(reg);
        } else {
            decrement(reg);
        }
    }

    public byte add(byte val1, byte val2, boolean includeCarry) {
        int result = val1 + val2;
        if (includeCarry && F.c.isActive()) {
            result++;
        }
        byte resultByte = (byte)result;
        F.s.set(resultByte > 0x7f);
        F.z.set(resultByte == 0);
        F.h.set(isHalfCarry(val1, val2, 8));
        F.p.set(isOverflow(val1, val2, 8, true));
        F.n.set(false);
        F.c.set(result > 0xff);
        return resultByte;
    }

    public byte add(byte val1, byte val2) {
        return add(val1, val2, false);
    }

    public void add(Reg8 reg, byte val, boolean includeCarry)
    {
        reg.set(add(reg.get(), val, includeCarry));
    }

    public void add(Reg8 reg, byte val) {
        add(reg, val, false);
    }

    public short add(short val1, short val2, boolean includeCarry) {
        int result = (short)val1 + (short)val2;
        if (includeCarry && F.c.isActive()) {
            result++;
        }
        short resultShort = (short)(result);
        if (includeCarry) {
            F.s.set(resultShort > Short.MAX_VALUE);
            F.z.set(resultShort == 0);
            F.p.set(isOverflow(val1, val2, 16, true));
        }
        F.h.set(isHalfCarry(val1, val2, 16));
        F.n.set(false);
        F.c.set(result > 0xffff);
        return resultShort;
    }

    public short add(short val1, short val2) {
        return add(val1, val2, false);
    }

    public void add(Reg16 reg, short val, boolean includeCarry) {
        reg.set(add(reg.get(), val, includeCarry));
    }

    public void add(Reg16 reg, short val) {
        add(reg, val, false);
    }

    public byte subtract(byte val1, byte val2, boolean includeCarry) {
        int result = val1 - val2;
        if (includeCarry && F.c.isActive()) {
            result--;
        }
        byte resultByte = (byte)result;
        F.s.set(resultByte > 0x7f);
        F.z.set(resultByte == 0);
        F.h.set(isHalfBorrow(val1, val2, 8));
        F.p.set(isOverflow(val1, val2, 8, false));
        F.n.set(true);
        F.c.set(result > 0xff);
        return resultByte;
    }

    public byte subtract(byte val1, byte val2) {
        return subtract(val1, val2, false);
    }

    public void subtract(Reg8 reg, byte val, boolean includeCarry) {
        reg.set(subtract(reg.get(), val, includeCarry));
    }

    public void subtract(Reg8 reg, byte val) {
        subtract(reg, val, false);
    }

    public short subtract(short val1, short val2, boolean includeCarry) {
        int result = val1 - val2;
        if (includeCarry && F.c.isActive()) {
            result--;
        }
        short resultShort = (short)(result);
        F.s.set(resultShort > 0x7fff);
        F.z.set(resultShort == 0);
        F.h.set(isHalfBorrow(val1, val2, 16));
        F.p.set(isOverflow(val1, val2, 16, false));
        F.n.set(true);
        F.c.set(result > 0xffff);
        return resultShort;
    }

    public short subtract(short val1, short val2) {
        return subtract(val1, val2, false);
    }

    public void subtract(Reg16 reg, short val, boolean includeCarry) {
        reg.set(subtract(reg.get(), val, includeCarry));
    }

    public void subtract(Reg16 reg, short val) {
        subtract(reg, val, false);
    }

    public void multiply(Reg16 reg) {
        byte n = reg.nieder.get();
        byte o = reg.ober.get();
        reg.set((short)(n * o));
    }

    public void compare(byte val) {
        subtract(A.get(), val, false);
    }

    public void and(byte val) {
        byte result = (byte)(A.get() & val);
        F.s.set(result > 0x7f);
        F.z.set(result == 0);
        F.h.set(true);
        F.p.set(isParity(result));
        F.n.set(false);
        F.c.set(false);
        A.set(result);
    }

    public void or(byte val) {
        byte result = (byte)(A.get() | val);
        F.s.set(result > 0x7f);
        F.z.set(result == 0);
        F.h.set(true);
        F.p.set(isParity(result));
        F.n.set(false);
        F.c.set(false);
        A.set(result);
    }

    public void xor(byte val) {
        byte result = (byte)(A.get() ^ val);
        F.s.set(result > 0x7f);
        F.z.set(result == 0);
        F.h.set(true);
        F.p.set(isParity(result));
        F.n.set(false);
        F.c.set(false);
        A.set(result);
    }

    public void InvertA() {
        A.set((byte)~A.get());
        F.h.set(true);
        F.n.set(true);
    }

    public byte rotateLeft(byte val, boolean includeCarry) {
        int bit7 = ((val >> 7) & 1);
        int result = val << 1;
        if (includeCarry) {
            result |= F.c.get().getVal();
        } else {
            result |= bit7;
        }
        F.c.set(bit7 == 1);
        F.h.set(false);
        F.n.set(false);
        return (byte)result;
    }

    public void rotateLeft(Reg8 reg, boolean includeCarry) {
        reg.set(rotateLeft(reg.get(), includeCarry));
    }

    public byte rotateRight(byte val, boolean includeCarry) {
        int bit0 = val & 1;
        int result = val >> 1;
        if (includeCarry) {
            int c = F.c.get().getVal();
            result |= c << 7;
        } else {
            result |= bit0 << 7;
        }
        F.c.set(bit0 == 1);
        F.h.set(false);
        F.n.set(false);
        return (byte)result;
    }

    public void rotateRight(Reg8 reg, boolean includeCarry) {
        reg.set(rotateRight(reg.get(), includeCarry));
    }

    public byte shiftLeft(byte val) {
        int bit7 = (val >> 7) & 1;
        byte resultByte = (byte)(val << 1);
        F.s.set(resultByte > 0x7f);
        F.z.set(resultByte == 0);
        F.h.set(false);
        F.p.set(isParity(resultByte));
        F.n.set(false);
        F.c.set(bit7 == 1);
        return resultByte;
    }

    public void shiftLeft(Reg8 reg) {
        reg.set(shiftLeft(reg.get()));
    }

    public byte shiftRight(byte val, boolean isArithmetic) {
        int bit0 = val & 1;
        int bit7 = val & 0x80;

        int result = val >> 1;
        if (isArithmetic) {
            result |= bit7;
        }
        byte resultByte = (byte)result;
        F.s.set(resultByte > 0x7f);
        F.z.set(resultByte == 0);
        F.h.set(false);
        F.p.set(isParity(resultByte));
        F.n.set(false);
        F.c.set(bit0 == 1);
        return resultByte;
    }

    public void shiftRight(Reg8 reg, boolean isArithmetic) {
        reg.set(shiftRight(reg.get(), isArithmetic));
    }

    public byte setBit(byte val, int bit) {
        bit &= 7;
        int result = val | (1 << bit);
        return (byte)result;
    }

    public void setBit(Reg8 reg, int bit) {
        reg.set(setBit(reg.get(), bit));
    }

    public byte clearBit(byte val, int bit) {
        bit &= 7;
        int result = val & (~(1 << bit));
        return (byte)result;
    }

    public void clearBit(Reg8 reg, int bit) {
        reg.set(clearBit(reg.get(), bit));
    }

    public void testBit(byte val, int bit) {
        bit &= 7;
        int result = val & (1 << bit);
        F.z.set(result == 0);
        F.h.set(true);
        F.n.set(false);
    }
}
