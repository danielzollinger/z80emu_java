package au.com.zollinger.z80_emu;

import java.util.List;

public class InvalidInstructionException  extends Exception {
    public InvalidInstructionException(List<Byte> opcodes)
    {
        super(getMsg(opcodes));
    }

    private static String getMsg(List<Byte> opcodes)
    {
        StringBuilder sb = new StringBuilder("Invalid Instruction:");
        for (byte b : opcodes)
        {
            sb.append(String.format(" %02X", b));
        }
        return sb.toString();
    }
}
