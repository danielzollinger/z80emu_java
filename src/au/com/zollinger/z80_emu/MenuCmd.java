package au.com.zollinger.z80_emu;

public class MenuCmd {
    final String description;
    final CmdHandler cmdHandler;
    final MenuArg[] args;

    private MenuCmd(String description, CmdHandler cmdHandler, MenuArg[] args) {
        this.description = description;
        this.cmdHandler = cmdHandler;
        this.args = args;
    }

    public MenuCmd(String description, CmdHandler cmdHandler, String... args) throws Exception {
        this(description, cmdHandler, parseArgs(args));
    }

    public MenuCmd parse(String[] vals) throws Exception {
        MenuCmd request = new MenuCmd(description, cmdHandler, args);
        int minValsCount = mandatoryArgCount();
        if (vals.length < minValsCount) {
            throw new IllegalArgumentException("Insufficient argument values provided.");
        }
        for(int i = 0; i < Math.min(vals.length, request.args.length); i++) {
            request.args[i].read(vals[i]);
        }
        for(int i = vals.length; i < request.args.length; i++) {
            if(!request.args[i].isOptional()) {
                throw new IllegalArgumentException();
            }
        }
        return request;
    }

    private static MenuArg[] parseArgs(String[] args) throws Exception {
        MenuArg[] argArr = new MenuArg[args.length];
        for(int i = 0; i < args.length; i++) {
            argArr[i] = new MenuArg(args[i]);
        }
        return argArr;
    }

    private int mandatoryArgCount() {
        int count = 0;
        while(count < args.length && !args[count].isOptional()) {
            count++;
        }
        return count;
    }
}
