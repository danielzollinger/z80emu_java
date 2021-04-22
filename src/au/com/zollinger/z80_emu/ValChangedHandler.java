package au.com.zollinger.z80_emu;

public interface ValChangedHandler {
    void update(Object opaque, int newVal);
}
