package au.com.zollinger.z80_emu;

public interface IOReadHandler {
    byte read(short addr);
}
