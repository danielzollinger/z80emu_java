package au.com.zollinger.z80_emu;

public interface IOWriteHandler {
    void write(short addr, byte val);
    void reset();
}
