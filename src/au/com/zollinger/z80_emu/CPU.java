package au.com.zollinger.z80_emu;

import java.util.SortedMap;
import java.util.TreeMap;

public class CPU {
    public final Reg8 a;
    public final Flags f;
    public final Reg16 af;
    public final Reg8 b;
    public final Reg8 c;
    public final Reg16 bc;
    public final Reg8 d;
    public final Reg8 e;
    public final Reg16 de;
    public final Reg8 h;
    public final Reg8 l;
    public final Reg16 hl;

    public final Reg8 a_;
    public final Flags f_;
    public final Reg16 af_;
    public final Reg8 b_;
    public final Reg8 c_;
    public final Reg16 bc_;
    public final Reg8 d_;
    public final Reg8 e_;
    public final Reg16 de_;
    public final Reg8 h_;
    public final Reg8 l_;
    public final Reg16 hl_;

    public Reg16 ix;
    public Reg16 iy;

    public boolean iff1;
    public boolean iff2;
    public boolean halted;

    public SP sp;
    public PC pc;

    public IntVectReg ivr;
    public MemRefreshReg mrr;

    public Mem mem;
    public IO io;

    private SortedMap<String, Reg> mByName;

    enum IntMode {
        IM0, IM1, IM2
    }

    public IntMode im;

    public CPU(short ramSize, byte[] programData) {
        mByName = new TreeMap<>();
        a = new Reg8("A");
        mByName.put(a.name, a);
        f = new Flags("F");
        mByName.put(f.name, f);

        af = new Reg16(a, f);
        mByName.put(af.name, af);

        b = new Reg8("B");
        mByName.put(b.name, b);
        c = new Reg8("C");
        mByName.put(c.name, c);
        bc = new Reg16(b, c);
        mByName.put(bc.name,bc);
        d = new Reg8("D");
        mByName.put(d.name, d);
        e = new Reg8("E");
        mByName.put(e.name, e);
        de = new Reg16(d, e);
        mByName.put(de.name, de);
        h = new Reg8("H");
        mByName.put(h.name, h);
        l = new Reg8("L");
        mByName.put(l.name, l);
        hl = new Reg16(h, l);
        mByName.put(hl.name, hl);

        a_ = new Reg8("A'");
        f_ = new Flags("F'");

        af_ = new Reg16(a_, f_);
        b_ = new Reg8("B'");
        c_ = new Reg8("C'");
        bc_ = new Reg16(b_, c_);
        d_ = new Reg8("D'");
        e_ = new Reg8("E'");
        de_ = new Reg16(d_, e_);
        h_ = new Reg8("H'");
        l_ = new Reg8("L'");
        hl_ = new Reg16(h_, l_);

        ix = new Reg16("IX");
        mByName.put(ix.name, ix);
        iy = new Reg16("IY");
        mByName.put(iy.name, iy);

        mem = new Mem(ramSize, programData);
        io = new IO(f);

        sp = new SP(mem);
        mByName.put(sp.name, sp);
        pc = new PC(mem);
        mByName.put(pc.name, pc);

        ivr = new IntVectReg(mem);
        mByName.put(ivr.name, ivr);
        mrr = new MemRefreshReg();
        mByName.put(mrr.name, mrr);

        iff1 = true;

        im = IntMode.IM0;

        halted = false;
    }

    public void reset() {
        mem.reset();
        io.reset();
        af.reset();
        bc.reset();
        de.reset();
        hl.reset();
        af_.reset();
        bc_.reset();
        de_.reset();
        hl_.reset();
        ix.reset();
        iy.reset();
        sp.reset();
        pc.reset();
        ivr.reset();
        mrr.reset();

        iff1 = true;
        halted = false;
    }

    public String dumpRegs(Reg... regs) {
        StringBuilder sb = new StringBuilder("CPU - ");
        boolean first = true;
        for (Reg r : regs) {
            if (first) {
                first = false;
            } else {
                sb.append(" ");
            }
            sb.append(r.toString());
            if (r instanceof Flags) {
                sb.append(" ");
                sb.append(((Flags) r).toStringBySignal());
            }
        }
        return sb.toString();
    }

    public Reg getRegByName(String name) {
        if(!mByName.containsKey(name.toUpperCase())) {
            throw new IllegalArgumentException(name + " is not a valid CPU register name");
        }
        return mByName.get(name.toUpperCase());
    }
}
