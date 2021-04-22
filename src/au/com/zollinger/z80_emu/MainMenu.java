package au.com.zollinger.z80_emu;

import java.io.BufferedReader;

public class MainMenu extends Menu {
    private final CPU mCPU;

    private class CmdSetRegister implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            Reg r = mCPU.getRegByName(args[0].getString());
            r.setInt(args[1].getNumber());
        }
    }

    private class CmdSetFlag implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            SignalBit sb = mCPU.f.byName(args[0].getString().toUpperCase()
            );
            if(args[1].getBoolean()) {
                sb.activate();
            } else {
                sb.deactivate();
            }
        }
    }

    private class CmdPrintRegisters implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            String regName = args[0].getString();
            if(regName.equals("all")) {
                System.out.println(mCPU.dumpRegs(mCPU.a, mCPU.bc, mCPU.de,
                        mCPU.hl, mCPU.ix, mCPU.iy, mCPU.pc, mCPU.sp, mCPU.f));
            } else {
                System.out.println(mCPU.getRegByName(regName));
            }
        }
    }

    private class CmdPrintMem implements CmdHandler {
        @Override
        public void run(MenuArg[] args) {
            int addr = args[0].getNumber();
            int len = args[1].getNumber();
            dumpMem(addr, len);
        }
    }

    private void dumpMem(int addr, int len) {
        StringBuilder sb = new StringBuilder();
        StringBuilder ascii = new StringBuilder();
        int startAddr = Math.max(addr - (addr % 16), 0);
        int rows = ((len + 15)  / 16);
        for (int row = 0; row < rows; row++) {
            sb.append(String.format("%04X: ", startAddr + row * 16));
            for (int col = 0; col < 16; col++) {
                if (col > 0) {
                    sb.append(" ");
                }
                short a = (short) (startAddr + row * 16 + col);
                byte b = mCPU.mem.read8(a);
                sb.append(String.format("%02X", b));
                ascii.append((b >= 0x20 && b < 0x7f) ? (char)b : '.');
            }
            sb.append("   ");
            sb.append(ascii);
            ascii.setLength(0);
            sb.append("\n");
        }
        System.out.print(sb);
    }

    public MainMenu(BufferedReader reader) throws Exception {
        super(reader);
        mCPU = new CPU(Short.MAX_VALUE, null);
        addCmd("pm", "Print Memory <addr>", new CmdPrintMem(), "addr:h", "len:h:10");
        addCmd("pr", "Print CPU registers", new CmdPrintRegisters(), "reg:s:all");
        addCmd("sr", "Set register", new CmdSetRegister(), "reg:s", "val:h");
        addCmd("sf", "Set/Clear flag", new CmdSetFlag(), "flag:s", "state:b");
    }
}
