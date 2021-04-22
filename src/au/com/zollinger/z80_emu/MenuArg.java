package au.com.zollinger.z80_emu;

import java.util.Locale;

public class MenuArg {
    public final String name;
    // h: hex i: int s: string c: char b: bool
    private final String mType;
    private final Object mDefault;
    private Object mVal;

    public MenuArg(String arg) throws Exception {
        if(arg == null) {
            throw new IllegalArgumentException("Null arg provided to MenuArg");
        }
        if(arg.length() == 0) {
            throw new IllegalArgumentException("Empty arg provided to MenuArg");
        }
        String[] sa = arg.split(":");
        if(sa.length < 2) {
            throw new IllegalArgumentException("Invalid arg definition");
        }
        name = arg;
        mType = sa[1];
        if(!mType.matches("[bchis]")) {
            throw new IllegalArgumentException("Invalid Cmd char " + mType);
        }

        mDefault = sa.length > 2 ? parse(sa[2]) : null;
    }

    public void read(String s) throws Exception {
        mVal = parse(s);
    }

    private Object parse(String s) throws Exception {
        if(s == null) {
            throw new IllegalArgumentException("Null string");
        }
        if(s.length() < 1) {
            throw new IllegalArgumentException("Empty string");
        }
        switch (mType) {
            case "b":
                boolean b;
                try {
                    int x = Integer.parseInt(s);
                    b = x != 0;
                } catch (Exception e) {
                    b = s.toLowerCase().matches("(y(es)?|t(rue)?|on|1)");
                }
                return b;
            case "c":
                char c = s.charAt(0);
                return c;
            case "i":
                return parseNumber(s, 10);
            case "h":
                return parseNumber(s, 16);
            case "s":
            default:
                return s;
        }
    }

    private int parseNumber(String s, int radix) throws Exception {
        try {
            return Integer.parseInt(s, radix);
        } catch (Exception e) {
            throw new Exception("parsing number arg", e);
        }
    }

    private Object getVal() {
        return mVal == null ? mDefault : mVal;
    }

    public boolean getBoolean() {
        return (boolean)getVal();
    }

    public char getChar() {
        return (char)getVal();
    }

    public String getString() {
        return (String)getVal();
    }

    public int getNumber() {
        return (int)getVal();
    }

    public boolean isOptional() {
        return mDefault != null;
    }
}
