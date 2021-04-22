package au.com.zollinger.z80_emu;

public class Signal extends UpdatingVal {
    public final String name;
    public final State active;

    public State get() {
        return State.create(super.getInt());
    }

    public void set(State val) {
        super.setInt(val.getVal());
    }

    public Signal(String name, State active, State initialState) {
        super(1, initialState.getVal());
        this.name = name;
        this.active = active;
    }

    public Signal(String name, State active) {
        this(name, active, State.LOW);
    }

    public Signal(State active) {
        this("", active, State.LOW);
    }

    public void activate() {
        set(active);
    }

    public void deactivate() {
        set(active.inverse());
    }

    public void set(boolean isActive) {
        if (isActive) {
            activate();
        } else {
            deactivate();
        }
    }

    public void invert() {
        set(get().inverse());
    }

    public boolean isActive() {
        return get() == active;
    }

    @Override
    public String toString() {
        return String.format("%s:%d", name, super.getInt());
    }
}
