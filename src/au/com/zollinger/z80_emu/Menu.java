package au.com.zollinger.z80_emu;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

public class Menu {
    private class CmdHelp implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            printMenu();
        }
    }

    private class CmdQuit implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            mKeepGoing = false;
        }
    }

    private BufferedReader mReader;
    private SortedMap<String, MenuCmd> mCmds;
    private boolean mKeepGoing;

    private byte[] getData(int neededLen) throws IOException {
        byte[] ba = new byte[neededLen];
        int readLen = 0;
        while(readLen < neededLen) {
            System.out.printf("%d data bytes required: ");
            String s = mReader.readLine().toLowerCase().replaceAll(" +", "");
            while (s.length() > 1) {
                ba[readLen++] = (byte) Integer.parseInt(s.substring(0, 2), 16);
                s = s.substring(2);
            }
        }
        return ba;
    }

    private MenuCmd getCmd() throws Exception {
        System.out.print("Cmd: ");
        String s = mReader.readLine().trim();
        String[] sa = s.split(" +");
        if(sa.length == 0) {
            throw new IllegalArgumentException("No cmd provided");
        }
        String cmd = sa[0];
        if(mCmds.containsKey(cmd)) {
            String[] args;
            if(sa.length > 1) {
                args = Arrays.copyOfRange(sa, 1, sa.length);
            } else {
                args = new String[0];
            }
            return mCmds.get(cmd).parse(args);
        } else {
            throw new IllegalArgumentException(cmd + " is not a valid menu cmd");
        }
    }

    private void printMenu() {
        for(String cmdStr : mCmds.keySet()) {
            MenuCmd cmd = mCmds.get(cmdStr);
            System.out.printf("%-3s", cmdStr);
            int len = 0;
            for(MenuArg arg : cmd.args) {
                System.out.printf(" <%s>", arg.name);
                len += 3 + arg.name.length();
            }
            System.out.printf("%" + Math.max(30 - len, 1) + "s - %s\n", "", cmd.description);
        }
        System.out.println("");
    }
    public Menu(BufferedReader reader) throws Exception {
        mReader = reader;
        mCmds = new TreeMap<>();
        addCmd("?", "Print this help", new CmdHelp());
        addCmd("q", "Quit this menu", new CmdQuit());
    }

    public void addCmd(String cmd, String description, CmdHandler cmdHandler, String... args) throws Exception {
        if(cmd.length() > 3) {
            throw new IllegalArgumentException("cmd: [" + cmd + "] Menu cmd string length must be <= 3");
        }
        mCmds.put(cmd, new MenuCmd(description, cmdHandler, args));
    }

    public void run() {
        mKeepGoing = true;

        printMenu();
        do {
            try {
                MenuCmd cmd = getCmd();
                cmd.cmdHandler.run(cmd.args);
            } catch (Exception e) {
                System.out.printf("Error: %s\n", e.getMessage());
                e.printStackTrace();

            }
        } while(mKeepGoing);
    }
}
