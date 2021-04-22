package au.com.zollinger.z80_emu;

import java.util.ArrayList;
import java.util.List;

public class InstructionReg {
    private final CPU cpu;
    private final Reg8 a;
    private final Flags f;
    private final Reg8 b;
    private final Reg8 c;
    private final Reg16 bc;
    private final Reg8 d;
    private final Reg8 e;
    private final Reg16 de;
    private final Reg8 h;
    private final Reg8 l;
    private final Reg16 hl;
    private final Reg16 ix;
    private final Reg16 iy;
    private final SP sp;
    private final PC pc;
    private final ALU alu;
    private final Mem mem;
    private final IO io;

    private final List<Byte> mCurrentInstruction;

    /**
     * Run instructions
     * @param cpu CPU class to run instructions against
     */
    public InstructionReg(CPU cpu) {
        this.cpu = cpu;
        a = cpu.a;
        f = cpu.f;
        b = cpu.b;
        c = cpu.c;
        bc = cpu.bc;
        d = cpu.d;
        e = cpu.e;
        de = cpu.de;
        h = cpu.h;
        l = cpu.l;
        hl = cpu.hl;
        ix = cpu.ix;
        iy = cpu.iy;
        sp = cpu.sp;
        pc = cpu.pc;
        mem = cpu.mem;
        io = cpu.io;
        alu = new ALU(a, f);

        mCurrentInstruction = new ArrayList<>();
    }

    private void invalidInstruction() throws InvalidInstructionException {
        throw new InvalidInstructionException(mCurrentInstruction);
    }

    private byte nextOpCode(boolean clear) {
        if(clear) {
            mCurrentInstruction.clear();
        }
        byte opcode = pc.next8();
        mCurrentInstruction.add(opcode);
        return opcode;
    }

    private byte getReg8Val(byte opcode, int bitOffset) {
        switch ((opcode >> bitOffset) & 7) {
            case 0:
                return b.get();
            case 1:
                return c.get();
            case 2:
                return d.get();
            case 3:
                return e.get();
            case 4:
                return h.get();
            case 5:
                return l.get();
            case 6:
                return mem.read8(hl.get());
            default:
                return a.get();
        }
    }

    private void setReg8Val(byte val, byte opcode, int bitOffset) {
        switch ((opcode >> bitOffset) & 7) {
            case 0:
                b.set(val);
                break;
            case 1:
                c.set(val);
                break;
            case 2:
                d.set(val);
                break;
            case 3:
                e.set(val);
                break;
            case 4:
                h.set(val);
                break;
            case 5:
                l.set(val);
                break;
            case 6:
                mem.write(hl.get(), val);
                break;
            default:
                a.set(val);
                break;
        }
    }

    private void exchangeRegisters(Reg16 a, Reg16 b) {
        short tmpShort = a.get();
        a.set(b.get());
        b.set(tmpShort);
    }

    private void call() {
        sp.push((short)(pc.get() + 3));
        pc.set(pc.next16());
    }

    private void ret()
    {
        pc.set(sp.pop16());
    }

    private void rst(short val) {
        short tmpShort = pc.get();
        tmpShort++;
        sp.push(tmpShort);
        pc.set(val);
    }

    /**
     * Process the next instruction
     * @throws InvalidInstructionException On bad instruction bytes
     */
    public void processNext() throws InvalidInstructionException {
        short addr;
        short tmpShort;
        byte tmpByte;

        int opCode = nextOpCode(true);
        OpCode.First ocf = OpCode.First.create(opCode);
        byte srcVal = getReg8Val((byte)opCode, 0);

        if ((opCode & 0xc0) == OpCode.FIRST_LD_MASK_C0 && !ocf.equals(OpCode.First.HALT)) {
            setReg8Val(srcVal, (byte)opCode, 3);
        } else if ((opCode & 0xf8) == OpCode.FIRST_ADD_MASK_F8) {
            alu.add(a, srcVal, false);
        } else if ((opCode & 0xf8) == OpCode.FIRST_ADC_MASK_F8) {
            alu.add(a, srcVal, true);
        } else if ((opCode & 0xd0) == OpCode.FIRST_SUB_MASK_F8) {
            alu.subtract(a, srcVal, false);
        } else if ((opCode & 0xf8) == OpCode.FIRST_SBC_MASK_F8) {
            alu.subtract(a, srcVal, true);
        } else if ((opCode & 0xf8) == OpCode.FIRST_AND_MASK_F8) {
            alu.and(srcVal);
        } else if ((opCode & 0xf8) == OpCode.FIRST_XOR_MASK_F8) {
            alu.xor(srcVal);
        } else if ((opCode & 0xf8) == OpCode.FIRST_OR_MASK_F8) {
            alu.or(srcVal);
        } else if ((opCode & 0xf8) == OpCode.FIRST_CP_MASK_F8) {
            alu.compare(srcVal);
        } else {
            switch (ocf) {
                case NOP:
                    break;
                case LD_BC_w:
                    bc.set(pc.next16());
                    break;
                case LD_pBC_A:
                    mem.write(bc.get(), a.get());
                    break;
                case INC_BC:
                    alu.increment(bc);
                    break;
                case INC_B:
                    alu.increment(b);
                    break;
                case DEC_B:
                    alu.decrement(b);
                    break;
                case LD_B_b:
                    b.set(pc.next8());
                    break;
                case RLCA:
                    alu.rotateLeft(a, false);
                    break;
                case EX:
                    exchangeRegisters(cpu.af, cpu.af_);
                    break;
                case ADD_HL_BC:
                    alu.add(hl, bc.get(), false);
                    break;
                case LD_A_pBC:
                    a.set(mem.read8(bc.get()));
                    break;
                case DEC_BC:
                    alu.decrement(bc);
                    break;
                case INC_C:
                    alu.increment(c);
                    break;
                case DEC_C:
                    alu.decrement(c);
                    break;
                case LD_C_b:
                    c.set(pc.next8());
                    break;
                case RRCA:
                    alu.rotateRight(a, true);
                    break;

                case DJNZ_b:
                    a.set((byte)(a.get() - 1));
                    if (a.get() != 0) {
                        pc.jumpRelative();
                    }
                    break;
                case LD_DE_w:
                    de.set(pc.next16());
                    break;
                case LD_pDE_A:
                    mem.write(de.get(), a.get());
                    break;
                case INC_DE:
                    alu.increment(de);
                    break;
                case INC_D:
                    alu.increment(d);
                    break;
                case DEC_D:
                    alu.decrement(d);
                    break;
                case LD_D_b:
                    d.set(pc.next8());
                    break;
                case RLA:
                    alu.rotateLeft(a, true);
                    break;
                case JR_b:
                    pc.jumpRelative();
                    break;
                case ADD_HL_DE:
                    alu.add(hl, de.get(), false);
                    break;
                case LD_A_pDE:
                    a.set(mem.read8(de.get()));
                    break;
                case DEC_DE:
                    alu.decrement(de);
                    break;
                case INC_E:
                    alu.increment(e);
                    break;
                case DEC_E:
                    alu.decrement(e);
                    break;
                case LD_E_b:
                    e.set(pc.next8());
                    break;
                case RRA:
                    alu.rotateRight(a, false);
                    break;

                case JR_nz_b:
                    if (!f.z.isActive()) {
                        pc.jumpRelative();
                    }
                    break;
                case LD_HL_w:
                    hl.set(pc.next16());
                    break;
                case LD_pw_HL:
                    mem.write(pc.next16(), hl.get());
                    break;
                case INC_HL:
                    alu.increment(hl);
                    break;
                case INC_H:
                    alu.increment(h);
                    break;
                case DEC_H:
                    alu.decrement(h);
                    break;
                case LD_H_b:
                    h.set(pc.next8());
                    break;
                case DAA:
                    // TODO work out this

                    break;
                case JR_z_b:
                    if (f.z.isActive()) {
                        pc.jumpRelative();
                    }
                    break;
                case ADD_HL_HL:
                    alu.add(hl, hl.get(), false);
                    break;
                case LD_HL_pw:
                    hl.set(mem.read16(pc.next16()));
                    break;
                case DEC_HL:
                    alu.decrement(hl);
                    break;
                case INC_L:
                    alu.increment(l);
                    break;
                case DEC_L:
                    alu.decrement(l);
                    break;
                case LD_L_b:
                    l.set(pc.next8());
                    break;
                case CPL:
                    alu.InvertA();
                    break;

                case JR_nc_b:
                    if (!f.c.isActive()) {
                        pc.jumpRelative();
                    }
                    break;
                case LD_SP_w:
                    sp.set(pc.next16());
                    break;
                case LD_pw_A:
                    mem.write(pc.next16(), a.get());
                    break;
                case INC_SP:
                    alu.increment(sp);
                    break;
                case INC_pHL:
                    addr = hl.get();
                    tmpByte = mem.read8(addr);
                    mem.write(addr, alu.increment(tmpByte));
                    break;
                case DEC_pHL:
                    addr = hl.get();
                    tmpByte = mem.read8(addr);
                    mem.write(addr, alu.decrement(tmpByte));
                    break;
                case LD_pHL_b:
                    mem.write(hl.get(), pc.next8());
                    break;
                case SCF:
                    f.c.activate();
                    break;
                case JR_c_b:
                    if (f.c.isActive()) {
                        pc.jumpRelative();
                    }
                    break;
                case ADD_HL_SP:
                    alu.add(hl, sp.get(), false);
                    break;
                case LD_A_pw:
                    a.set(mem.read8(pc.next16()));
                    break;
                case DEC_SP:
                    alu.decrement(sp);
                    break;
                case INC_A:
                    alu.increment(a);
                    break;
                case DEC_A:
                    alu.decrement(a);
                    break;
                case LD_A_b:
                    a.set(pc.next8());
                    break;
                case CCF:
                    f.c.invert();
                    break;

                case HALT:
                    cpu.halted = true;
                    break;

                case RET_nz:
                    if (!f.z.isActive()) {
                        ret();
                    }
                    break;
                case POP_BC:
                    bc.set(sp.pop16());
                    break;
                case JP_nz_w:
                    if (!f.z.isActive()) {
                        pc.jump();
                    }
                    break;
                case JP_w:
                    pc.jump();
                    break;
                case CALL_nz_w:
                    if (!f.z.isActive()) {
                        call();
                    }
                    break;
                case PUSH_BC:
                    sp.push(bc.get());
                    break;
                case ADD_A_b:
                    alu.add(a, pc.next8(), false);
                    break;
                case RST_00H:
                    rst((byte)0x00);
                    break;
                case RET_z:
                    if (f.z.isActive()) {
                        ret();
                    }
                    break;
                case RET:
                    ret();
                    break;
                case JP_z_w:
                    if (f.z.isActive()) {
                        pc.jump();
                    }
                    break;
                case BITS:
                    processBits();
                    break;
                case CALL_z_w:
                    if (f.z.isActive())
                    {
                        call();
                    }
                    break;
                case CALL_w:
                    call();
                    break;
                case ADC_A_b:
                    alu.add(a, pc.next8(), true);
                    break;
                case RST_08H:
                    rst((byte)0x08);
                    break;

                case RET_nc:
                    if (!f.c.isActive()) {
                        ret();
                    }
                    break;
                case POP_DE:
                    de.set(sp.pop16());
                    break;
                case JP_nc_w:
                    if (!f.c.isActive()) {
                        pc.jump();
                    }
                    break;
                case OUT_pb_A:
                    tmpShort = a.get();
                    tmpShort <<= 8;
                    tmpShort |= pc.next8();
                    io.write(tmpShort, a.get());
                    break;
                case CALL_nc_w:
                    if (!f.c.isActive()) {
                        call();
                        pc.set(pc.next16());
                    }
                    break;
                case PUSH_DE:
                    sp.push(de.get());
                    break;
                case SUB_b:
                    alu.subtract(a, pc.next8(), false);
                    break;
                case RST_10H:
                    rst((byte)0x10);
                    break;
                case RET_c:
                    if (f.c.isActive()) {
                        ret();
                    }
                    break;
                case EXX:
                    exchangeRegisters(bc, cpu.bc);
                    exchangeRegisters(de, cpu.de_);
                    exchangeRegisters(hl, cpu.hl);
                    break;
                case JP_c_w:
                    if (f.c.isActive()) {
                        pc.jump();
                    }
                    break;
                case IN_A_pb:
                    addr = a.get();
                    addr <<= 8;
                    addr |= pc.next8();
                    a.set(io.read(addr));
                    break;
                case CALL_c_w:
                    if (f.c.isActive()) {
                        call();
                    }
                    break;
                case IX:
                    processIZ(ix);
                    break;
                case SBC_A_b:
                    alu.subtract(a, pc.next8(), true);
                    break;
                case RST_18H:
                    rst((byte)0x18);
                    break;

                case RET_po_w:
                    if (f.p.isActive()) {
                        ret();
                    }
                    break;
                case POP_HL:
                    hl.set(sp.pop16());
                    break;
                case JP_po_w:
                    if (f.p.isActive()) {
                        pc.jump();
                    }
                    break;
                case EX_pSP_HL:
                    addr = sp.get();
                    tmpShort = mem.read16(addr);
                    mem.write(addr, hl.get());
                    hl.set(tmpShort);
                    break;
                case CALL_po_w:
                    if (f.p.isActive()) {
                        call();
                    }
                    break;
                case PUSH_HL:
                    sp.push(hl.get());
                    break;
                case AND_b:
                    alu.and(pc.next8());
                    break;
                case RST_20H:
                    rst((byte)0x20);
                    break;
                case RET_pe:
                    if (!f.p.isActive()) {
                        ret();
                    }
                    break;
                case JP_pHL:
                    pc.set(hl.get());
                    break;
                case JP_pe_w:
                    if (!f.p.isActive()) {
                        pc.jump();
                    }
                    break;
                case EX_DE_HL:
                    exchangeRegisters(de, hl);
                    break;
                case CALL_pe_w:
                    if (!f.p.isActive()) {
                        call();
                    }
                    break;
                case EXTD:
                    processExtd();
                    break;
                case XOR_b:
                    alu.xor(pc.next8());
                    break;
                case RST_28H:
                    rst((byte)0x28);
                    break;

                case RET_p:
                    if (!f.s.isActive()) {
                        ret();
                    }
                    break;
                case POP_AF:
                    cpu.af.set(sp.pop16());
                    break;
                case JP_p_w:
                    if (!f.s.isActive()) {
                        pc.jump();
                    }
                    break;
                case DI:
                    cpu.iff1 = false;
                    break;
                case CALL_p_w:
                    if (!f.s.isActive()) {
                        call();
                    }
                    break;
                case PUSH_AF:
                    sp.push(cpu.af.get());
                    break;
                case OR_b:
                    alu.or(pc.next8());
                    break;
                case RST_30H:
                    rst((byte)0x30);
                    break;
                case RET_m:
                    if (f.s.isActive()) {
                        ret();
                    }
                    break;
                case LD_SP_HL:
                    sp.set(hl.get());
                    break;
                case JP_m_w:
                    if (f.s.isActive()) {
                        pc.jump();
                    }
                    break;
                case EI:
                    cpu.iff1 = true;
                    break;
                case CALL_m_w:
                    if (f.s.isActive()) {
                        call();
                    }
                    break;
                case IY:
                    processIZ(iy);
                    break;
                case CP_b:
                    alu.compare(pc.next8());
                    break;
                case RST_38H:
                    rst((byte)0x38);
                    break;
                default:
                    invalidInstruction();
                    break;
            }
        }
    }

    private void processBits() throws InvalidInstructionException {
        byte opCodeByte = nextOpCode(false);
        int type = (opCodeByte >> 6) & 3;
        int bit = (opCodeByte >> 3) & 7;
        byte regVal = getReg8Val(opCodeByte, 0);

        switch (type) {
            case OpCode.BITS_RNS:
                switch (bit) {
                    case OpCode.BITS_RLC:
                        setReg8Val(alu.rotateLeft(regVal, false), opCodeByte, 0);
                        break;
                    case OpCode.BITS_RRC:
                        setReg8Val(alu.rotateRight(regVal, false), opCodeByte, 0);
                        break;
                    case OpCode.BITS_RL:
                        setReg8Val(alu.rotateLeft(regVal, true), opCodeByte, 0);
                        break;
                    case OpCode.BITS_RR:
                        setReg8Val(alu.rotateRight(regVal, true), opCodeByte, 0);
                        break;
                    case OpCode.BITS_SLA:
                        setReg8Val(alu.shiftLeft(regVal), opCodeByte, 0);
                        break;
                    case OpCode.BITS_SRA:
                        setReg8Val(alu.shiftRight(regVal, true), opCodeByte, 0);
                        break;
                    case OpCode.BITS_SRL:
                        setReg8Val(alu.shiftRight(regVal, false), opCodeByte, 0);
                        break;
                    default:
                        invalidInstruction();
                        break;
                }
                break;
            case OpCode.BITS_BIT:
                alu.testBit(regVal, bit);
                break;
            case OpCode.BITS_RES:
                setReg8Val(alu.clearBit(regVal, bit), opCodeByte, 0);
                break;
            case OpCode.BITS_SET:
                setReg8Val(alu.setBit(regVal, bit), opCodeByte, 0);
                break;
            default:
                invalidInstruction();
                break;
        }
    }

    private void processExtd() throws InvalidInstructionException {
        byte opCodeByte = nextOpCode(false);
        OpCode.Extd extdOpCode = OpCode.Extd.create(opCodeByte);
        byte tmpByte, tmpA, resultByte;
        short addr;

        switch (extdOpCode) {
            case IN_B_pC:
                io.read(b, bc.get());
                break;
            case OUT_pC_B:
                io.write(bc.get(), a.get());
                break;
            case SBC_HL_BC:
                alu.subtract(hl, bc.get(), true);
                break;
            case LD_pw_BC:
                mem.write(pc.next16(), bc.get());
                break;
            case NEG:
                tmpA = a.get();
                resultByte = alu.subtract((byte)0, tmpA);
                a.set(resultByte);
                f.z.set(resultByte == 0);
                f.s.set(resultByte < 0);
                f.p.set(tmpA == -128);
                f.c.set(tmpA == 0);
                break;
            case RETN:
                cpu.iff1 = cpu.iff2;
                ret();
                break;
            case IM0:
                cpu.im = CPU.IntMode.IM0;
                break;
            case LD_I_A:
                cpu.ivr.set(this.a.get());
                break;
            case IN_C_pC:
                io.read(c, bc.get());
                break;
            case OUT_pC_C:
                io.write(bc.get(), c.get());
                break;
            case ADC_HL_BC:
                alu.add(hl, bc.get(), true);
                break;
            case LD_BC_pw:
                bc.set(pc.next16());
                break;
            case RETI:
                ret();
                break;
            case LD_R_A:
                cpu.mrr.set(this.a.get());
                break;

            case IN_D_pC:
                io.read(d, bc.get());
                break;
            case OUT_pC_D:
                io.write(bc.get(), d.get());
                break;
            case SBC_HL_DE:
                alu.subtract(hl, de.get(), true);
                break;
            case LD_pw_DE:
                mem.write(pc.next16(), de.get());
                break;
            case IM1:
                cpu.im = CPU.IntMode.IM1;
                break;
            case LD_A_I:
                this.a.set(cpu.ivr.get());
                break;
            case IN_E_pC:
                io.read(e, bc.get());
                break;
            case OUT_pC_E:
                io.write(bc.get(), e.get());
                break;
            case ADC_HL_DE:
                alu.add(hl, de.get(), true);
                break;
            case LD_DE_pw:
                de.set(mem.read16(pc.next16()));
                break;
            case IM2:
                cpu.im = CPU.IntMode.IM2;
                break;
            case LD_A_R:
                this.a.set(cpu.mrr.get());
                break;

            case IN_H_pC:
                io.read(h, bc.get());
                break;
            case OUT_pC_H:
                io.write(bc.get(), h.get());
                break;
            case SBC_HL_HL:
                alu.subtract(hl, hl.get(), true);
                break;

            case LD_pw_HL:
                mem.write(pc.next16(), hl.get());
                break;
            case RRD:
                addr = hl.get();
                tmpByte = mem.read8(addr);
                tmpA = a.get();
                resultByte = (byte)((tmpA & 0xf0) | (tmpByte & 0xf));
                mem.write(addr, (byte)((tmpA << 4) | (tmpByte >> 4)));
                f.s.set(resultByte < 0);
                f.z.set(resultByte == 0);
                f.h.deactivate();
                f.p.set(ALU.isParity(resultByte));
                f.n.deactivate();
                break;
            case IN_L_pC:
                io.read(l, bc.get());
                break;
            case OUT_pC_L:
                io.write(bc.get(), l.get());
                break;
            case ADC_HL_HL:
                alu.add(hl, hl.get(), true);
                break;
            case LD_HL_pw:
                hl.set(mem.read16(pc.next16()));
                break;
            case RLD:
                addr = hl.get();
                tmpByte = mem.read8(addr);
                tmpA = a.get();
                resultByte = (byte)((tmpA & 0xf0) | (tmpByte >> 4));
                mem.write(addr, (byte)((tmpByte << 4) | (tmpA & 0xf)));
                f.s.set(resultByte < 0);
                f.z.set(resultByte == 0);
                f.h.deactivate();
                f.p.set(ALU.isParity(resultByte));
                f.n.deactivate();
                break;

            case IN_pC:
                io.read(null, bc.get());
                break;
            case SBC_HL_SP:
                alu.subtract(hl, sp.get(), true);
                break;
            case LD_pw_SP:
                mem.write(pc.next16(), sp.get());
                break;
            case IN_A_pC:
                io.read(a, bc.get());
                break;
            case OUT_pC_A:
                io.write(bc.get(), this.a.get());
                break;
            case ADC_HL_SP:
                alu.add(hl, sp.get(), true);
                break;
            case LD_SP_pw:
                sp.set(mem.read16(pc.next16()));
                break;
            case LDI:
            case LDD:
            case LDIR:
            case LDDR:
                mem.write(de.get(), mem.read8(hl.get()));
                alu.incOrDec(de, (opCodeByte & 0xf) == 0);
                alu.incOrDec(hl, (opCodeByte & 0xf) == 0);
                alu.decrement(bc);
                f.h.deactivate();
                f.p.set(bc.get() != 0);
                f.n.deactivate();
                if(extdOpCode.gte(OpCode.Extd.LDIR) && bc.get() != 0) {
                    pc.back(2);
                }
                break;
            case CPI:
            case CPD:
            case CPIR:
            case CPDR:
                tmpA = this.a.get();
                tmpByte = mem.read8(hl.get());
                resultByte = (byte)(tmpA - tmpByte);
                alu.incOrDec(hl, (opCodeByte & 0xf) == 1);
                alu.decrement(bc);
                f.s.set(resultByte < 0);
                f.z.set(resultByte == 0);
                f.h.set(ALU.isHalfBorrow(tmpA, tmpByte, 8));
                f.p.set(bc.get() != 0);
                f.n.activate();
                if (extdOpCode.gte(OpCode.Extd.CPIR) && bc.get() != 0) {
                    pc.back(2);
                }
                break;
            case INI:
            case IND:
            case INIR:
            case INDR:
                mem.write(hl.get(), io.read(bc.get()));
                alu.decrement(b, false);
                alu.incOrDec(hl, (opCodeByte & 0xf) == 2);
                f.z.set(a.get() == 0);
                f.n.activate();
                if (extdOpCode.gte(OpCode.Extd.INIR) && bc.get() != 0) {
                    pc.back(2);
                }
                break;
            case OUTI:
            case OUTD:
            case OTIR:
            case OTDR:
                tmpByte = mem.read8(hl.get());
                alu.decrement(b, false);
                io.write(bc.get(), tmpByte);
                alu.incOrDec(hl, (opCodeByte & 0xf) == 3);
                f.z.set(a.get() == 0);
                f.n.activate();
                if (extdOpCode.gte(OpCode.Extd.OTIR) && bc.get() != 0) {
                    pc.back(2);
                }
                break;
            case MLT_BC:
                alu.multiply(bc);
                break;
            case MLT_DE:
                alu.multiply(de);
                break;
            case MLT_HL:
                alu.multiply(hl);
                break;
            case MLT_SP:
                alu.multiply(sp);
                break;
            default:
                invalidInstruction();
                break;
        }
    }

    private short immDeltaAddr(Reg16 reg) {
        return (short)(reg.get() + pc.next8());
    }

    private void processIZ(Reg16 regIZ) throws InvalidInstructionException {
        short addr, tmpShort;
        byte opCodeByte = nextOpCode(false);
        switch(OpCode.IZ.create(opCodeByte)) {
            case ADD_IZ_BC:
                alu.add(regIZ, bc.get());
                break;
            case ADD_IZ_DE:
                alu.add(regIZ, de.get());
                break;
            case LD_IZ_w:
                regIZ.set(pc.next16());
                break;
            case LD_pw_IZ:
                mem.write(pc.next16(), regIZ.get());
                break;
            case INC_IZ:
                alu.increment(regIZ);
                break;
            case ADD_IZ_IZ:
                alu.add(regIZ, regIZ.get());
                break;
            case LD__IZ_pw:
                regIZ.set(mem.read16(pc.next16()));
                break;
            case DEC_IZ:
                alu.decrement(regIZ);
                break;

            case INC_pIZb:
                addr = immDeltaAddr(regIZ);
                mem.write(addr, alu.increment(mem.read8(addr)));
                break;
            case DEC_pIZb:
                addr = immDeltaAddr(regIZ);
                mem.write(addr, alu.decrement(mem.read8(addr)));
                break;
            case LD_pIZb_b:
                mem.write(immDeltaAddr(regIZ), pc.next8());
                break;
            case ADD_IZ_SP:
                alu.add(regIZ, sp.get());
                break;

            case LD_B_pIZb:
                b.set(mem.read8(immDeltaAddr(regIZ)));
                break;
            case LD_C_pIZb:
                c.set(mem.read8(immDeltaAddr(regIZ)));
                break;

            case LD_D_pIZb:
                d.set(mem.read8(immDeltaAddr(regIZ)));
                break;
            case LD_E_pIZb:
                e.set(mem.read8(immDeltaAddr(regIZ)));
                break;

            case LD_H_pIZb:
                h.set(mem.read8(immDeltaAddr(regIZ)));
                break;
            case LD_L_pIZb:
                l.set(mem.read8(immDeltaAddr(regIZ)));
                break;

            case LD_pIZb_B:
            case LD_pIZb_C:
            case LD_pIZb_D:
            case LD_pIZb_E:
            case LD_pIZb_H:
            case LD_pIZb_L:
            case LD_pIZb_A:
                mem.write(immDeltaAddr(regIZ), getReg8Val(opCodeByte, 0));
                break;

            case LD_A_pIZb:
                a.set(mem.read8(immDeltaAddr(regIZ)));
                break;
            case ADD_A_pIZb:
                alu.add(a, mem.read8(immDeltaAddr(regIZ)));
                break;
            case ADC_pIZb:
                alu.add(a, mem.read8(immDeltaAddr(regIZ)), true);
                break;
            case SUB_pIZb:
                alu.subtract(a, mem.read8(immDeltaAddr(regIZ)));
                break;
            case SBC_pIZb:
                alu.subtract(a, mem.read8(immDeltaAddr(regIZ)), true);
                break;
            case AND_pIZb:
                alu.and(mem.read8(immDeltaAddr(regIZ)));
                break;
            case XOR_pIZb:
                alu.xor(mem.read8(immDeltaAddr(regIZ)));
                break;
            case OR_pIZb:
                alu.or(mem.read8(immDeltaAddr(regIZ)));
                break;
            case CP_pIZb:
                alu.compare(mem.read8(immDeltaAddr(regIZ)));
                break;

            case BITS:
                processIZBits(regIZ);
                break;
            case POP_IZ:
                regIZ.set(sp.pop16());
                break;
            case EX_pSP_IZ:
                addr = sp.get();
                tmpShort = mem.read16(addr);
                mem.write(addr, regIZ.get());
                regIZ.set(tmpShort);
                break;
            case PUSH_IZ:
                sp.push(regIZ.get());
                break;
            case JP_IZ:
                pc.jump(ix.get());
                break;
            case LD_SP_IZ:
                sp.set(regIZ.get());
                break;
        }
    }
    private void processIZBits(Reg16 rIZ) throws InvalidInstructionException {
        short addr = immDeltaAddr(rIZ);
        byte u8 = mem.read8(addr);
        byte b = nextOpCode(false);
        OpCode.IZBits izBitsOpCode = OpCode.IZBits.create(b);
        int bit = (b >> 3) & 7;

        switch (izBitsOpCode) {
            case RLC_pIXb:
                mem.write(addr, alu.rotateLeft(u8, false));
                break;
            case RRC_pIXb:
                mem.write(addr, alu.rotateRight(u8, false));
                break;
            case RL_pIXb:
                mem.write(addr, alu.rotateLeft(u8, true));
                break;
            case RR_pIXb:
                mem.write(addr, alu.rotateRight(u8, true));
                break;
            case SLA_pIXb:
                mem.write(addr, alu.shiftLeft(u8));
                break;
            case SRA_pIXb:
                mem.write(addr, alu.shiftRight(u8, true));
                break;
            case SRL_pIXb:
                mem.write(addr, alu.shiftRight(u8, false));
                break;
            case BIT_0_pIXb:
            case BIT_1_pIXb:
            case BIT_2_pIXb:
            case BIT_3_pIXb:
            case BIT_4_pIXb:
            case BIT_5_pIXb:
            case BIT_6_pIXb:
            case BIT_7_pIXb:
                alu.testBit(u8, bit);
                break;
            case RES_0_pIXb:
            case RES_1_pIXb:
            case RES_2_pIXb:
            case RES_3_pIXb:
            case RES_4_pIXb:
            case RES_5_pIXb:
            case RES_6_pIXb:
            case RES_7_pIXb:
                mem.write(addr, alu.clearBit(u8, bit));
                break;
            case SET_0_pIXb:
            case SET_1_pIXb:
            case SET_2_pIXb:
            case SET_3_pIXb:
            case SET_4_pIXb:
            case SET_5_pIXb:
            case SET_6_pIXb:
            case SET_7_pIXb:
                mem.write(addr, alu.setBit(u8, bit));
                break;
            default:
                invalidInstruction();
                break;
        }
    }
}
