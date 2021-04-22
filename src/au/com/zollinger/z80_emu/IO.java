package au.com.zollinger.z80_emu;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class IO {
    private final SortedMap<Short, IOReadHandler> mReadHandlers;
    private final SortedMap<Short, IOWriteHandler> mWriteHandlers;
    private final List<IOResetHandler> mResetHandlers;
    private final Flags f;

    public IO(Flags f) {
        this.f = f;
        mReadHandlers = new TreeMap<>();
        mWriteHandlers = new TreeMap<>();
        mResetHandlers = new ArrayList<>();
    }

    public void reset() {
        for(IOResetHandler handler : mResetHandlers) {
            handler.reset();
        }
    }

    public void write(short addr, byte data) {
        if (mWriteHandlers.containsKey(addr)) {
            mWriteHandlers.get(addr).write(addr, data);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public byte read(short addr) {
        if (mReadHandlers.containsKey(addr)) {
            return mReadHandlers.get(addr).read(addr);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void read(Reg8 dest, short addr) {
        byte val = read(addr);
        f.s.set(val > 0x7f);
        f.z.set(val == 0);
        f.h.deactivate();
        f.p.set(ALU.isParity(val));
        f.n.deactivate();
        if (dest != null)
        {
            dest.set(val);
        }
    }

    public void addReadHandler(short firstAddr, short lastAddr, IOReadHandler handler) {
        for(short i = firstAddr; i <= lastAddr; i++) {
            mReadHandlers.put(i, handler);
        }
    }

    public void addReadHandler(short addr, IOReadHandler handler) {
        mReadHandlers.put(addr, handler);
    }

    public void addWriteHandler(short firstAddr, short lastAddr, IOWriteHandler handler) {
        for(short i = firstAddr; i <= lastAddr; i++) {
            mWriteHandlers.put(i, handler);
        }
    }

    public void addWriteHandler(short addr, IOWriteHandler handler) {
        mWriteHandlers.put(addr, handler);
    }

    public void addResetHandler(IOResetHandler handler) {
        if(!mResetHandlers.contains(handler)) {
            mResetHandlers.add(handler);
        }
    }
}
