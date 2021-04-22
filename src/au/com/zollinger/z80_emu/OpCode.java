package au.com.zollinger.z80_emu;

public class OpCode {
    public static final int FIRST_LD_MASK_C0 = 0x40;
    public static final int FIRST_ADD_MASK_F8 = 0x80;
    public static final int FIRST_ADC_MASK_F8 = 0x88;
    public static final int FIRST_SUB_MASK_F8 = 0x90;
    public static final int FIRST_SBC_MASK_F8 = 0x98;
    public static final int FIRST_AND_MASK_F8 = 0xa0;
    public static final int FIRST_XOR_MASK_F8 = 0xa8;
    public static final int FIRST_OR_MASK_F8 = 0xb0;
    public static final int FIRST_CP_MASK_F8 = 0xb8;

    public enum First {
        NOP(0x00),
        LD_BC_w(0x01),
        LD_pBC_A(0x02),
        INC_BC(0x03),
        INC_B(0x04),
        DEC_B(0x05),
        LD_B_b(0x06),
        RLCA(0x07),
        EX(0x08),
        ADD_HL_BC(0x09),
        LD_A_pBC(0x0a),
        DEC_BC(0x0b),
        INC_C(0x0c),
        DEC_C(0x0d),
        LD_C_b(0x0e),
        RRCA(0x0f),
        
        DJNZ_b(0x10),
        LD_DE_w(0x11),
        LD_pDE_A(0x12),
        INC_DE(0x13),
        INC_D(0x14),
        DEC_D(0x15),
        LD_D_b(0x16),
        RLA(0x17),
        JR_b(0x18),
        ADD_HL_DE(0x19),
        LD_A_pDE(0x1a),
        DEC_DE(0x1b),
        INC_E(0x1c),
        DEC_E(0x1d),
        LD_E_b(0x1e),
        RRA(0x1f),

        JR_nz_b(0x20),
        LD_HL_w(0x21),
        LD_pw_HL(0x22),
        INC_HL(0x23),
        INC_H(0x24),
        DEC_H(0x25),
        LD_H_b(0x26),
        DAA(0x27),
        JR_z_b(0x28),
        ADD_HL_HL(0x29),
        LD_HL_pw(0x2a),
        DEC_HL(0x2b),
        INC_L(0x2c),
        DEC_L(0x2d),
        LD_L_b(0x2e),
        CPL(0x2f),

        JR_nc_b(0x30),
        LD_SP_w(0x31),
        LD_pw_A(0x32),
        INC_SP(0x33),
        INC_pHL(0x34),
        DEC_pHL(0x35),
        LD_pHL_b(0x36),
        SCF(0x37),
        JR_c_b(0x38),
        ADD_HL_SP(0x39),
        LD_A_pw(0x3a),
        DEC_SP(0x3b),
        INC_A(0x3c),
        DEC_A(0x3d),
        LD_A_b(0x3e),
        CCF(0x3f),

        HALT(0x76),

        RET_nz(0xc0),
        POP_BC(0xc1),
        JP_nz_w(0xc2),
        JP_w(0xc3),
        CALL_nz_w(0xc4),
        PUSH_BC(0xc5),
        ADD_A_b(0xc6),
        RST_00H(0xc7),
        RET_z(0xc8),
        RET(0xc9),
        JP_z_w(0xca),
        BITS(0xcb),
        CALL_z_w(0xcc),
        CALL_w(0xcd),
        ADC_A_b(0xce),
        RST_08H(0xcf),

        RET_nc(0xd0),
        POP_DE(0xd1),
        JP_nc_w(0xd2),
        OUT_pb_A(0xd3),
        CALL_nc_w(0xd4),
        PUSH_DE(0xd5),
        SUB_b(0xd6),
        RST_10H(0xd7),
        RET_c(0xd8),
        EXX(0xd9),
        JP_c_w(0xda),
        IN_A_pb(0xdb),
        CALL_c_w(0xdc),
        IX(0xdd),
        SBC_A_b(0xde),
        RST_18H(0xdf),

        RET_po_w(0xe0),
        POP_HL(0xe1),
        JP_po_w(0xe2),
        EX_pSP_HL(0xe3),
        CALL_po_w(0xe4),
        PUSH_HL(0xe5),
        AND_b(0xe6),
        RST_20H(0xe7),
        RET_pe(0xe8),
        JP_pHL(0xe9),
        JP_pe_w(0xea),
        EX_DE_HL(0xeb),
        CALL_pe_w(0xec),
        EXTD(0xed),
        XOR_b(0xee),
        RST_28H(0xef),

        RET_p(0xf0),
        POP_AF(0xf1),
        JP_p_w(0xf2),
        DI(0xf3),
        CALL_p_w(0xf4),
        PUSH_AF(0xf5),
        OR_b(0xf6),
        RST_30H(0xf7),
        RET_m(0xf8),
        LD_SP_HL(0xf9),
        JP_m_w(0xfa),
        EI(0xfb),
        CALL_m_w(0xfc),
        IY(0xfd),
        CP_b(0xfe),
        RST_38H(0xff);

        private int mVal;

        First(int val) {
            mVal = val;
        }

        public static First create(int val) {
            for(First f : values()) {
                if (f.mVal == val) {
                    return f;
                }
            }
            return NOP;
        }
    }

    public enum Extd {
        IN0_B_pb(0x00),         // HD64180
        OUT0_pb_B(0x01),        // HD64180
        TST_B(0x04),            // HD64180
        IN0_C_pb(0x08),         // HD64180
        OUT0_pb_C(0x09),        // HD64180
        TST_C(0x0c),            // HD64180
        IN0_D_pb(0x10),         // HD64180
        OUT0_pb_D(0x11),        // HD64180
        TST_D(0x14),            // HD64180
        IN0_E_pb(0x18),         // HD64180
        OUT0_pb_E(0x19),        // HD64180
        TST_E(0x1c),            // HD64180
        IN0_H_pb(0x20),         // HD64180
        OUT0_pb_H(0x21),        // HD64180
        TST_H(0x24),            // HD64180
        IN0_L_pb(0x28),         // HD64180
        OUT0_pb_L(0x29),        // HD64180
        TST_L(0x2c),            // HD64180
        IN0_pb(0x30),           // HD64180
        TST_pHL(0x34),          // HD64180
        IN0_A_pb(0x38),         // HD64180
        OUT0_pb_A(0x39),        // HD64180
        TST_A(0x3c),            // HD64180

        IN_B_pC(0x40),
        OUT_pC_B(0x41),
        SBC_HL_BC(0x42),
        LD_pw_BC(0x43),
        NEG(0x44),
        RETN(0x45),
        IM0(0x46),
        LD_I_A(0x47),
        IN_C_pC(0x48),
        OUT_pC_C(0x49),
        ADC_HL_BC(0x4a),
        LD_BC_pw(0x4b),
        MLT_BC(0x4c),           // HD64180
        RETI(0x4d),
        LD_R_A(0x4f),

        IN_D_pC(0x50),
        OUT_pC_D(0x51),
        SBC_HL_DE(0x52),
        LD_pw_DE(0x53),
        IM1(0x56),
        LD_A_I(0x57),
        IN_E_pC(0x58),
        OUT_pC_E(0x59),
        ADC_HL_DE(0x5a),
        LD_DE_pw(0x5b),
        MLT_DE(0x5c),           // HD64180
        IM2(0x5e),
        LD_A_R(0x5f),

        IN_H_pC(0x60),
        OUT_pC_H(0x61),
        SBC_HL_HL(0x62),
        LD_pw_HL(0x63),
        TST_b(0x64),            // HD64180
        RRD(0x67),
        IN_L_pC(0x68),
        OUT_pC_L(0x69),
        ADC_HL_HL(0x6a),
        LD_HL_pw(0x6b),
        MLT_HL(0x6c),           // HD64180
        RLD(0x6f),

        IN_pC(0x70),
        SBC_HL_SP(0x72),
        LD_pw_SP(0x73),
        TSTIO_b(0x74),          // HD64180
        SLP(0x76),              // HD64180
        IN_A_pC(0x78),
        OUT_pC_A(0x79),
        ADC_HL_SP(0x7a),
        LD_SP_pw(0x7b),
        MLT_SP(0x7c),           // HD64180

        OTIM(0x83),             // HD64180
        OTDM(0x8b),             // HD64180

        OTIMR(0x93),            // HD64180
        OTDMR(0x9b),            // HD64180

        LDI(0xa0),
        CPI(0xa1),
        INI(0xa2),
        OUTI(0xa3),
        LDD(0xa8),
        CPD(0xa9),
        IND(0xaa),
        OUTD(0xab),

        LDIR(0xb0),
        CPIR(0xb1),
        INIR(0xb2),
        OTIR(0xb3),
        LDDR(0xb8),
        CPDR(0xb9),
        INDR(0xba),
        OTDR(0xbb),
        INVALID(0xffff);

        private int mVal;

        Extd(int val) {
            mVal = val;
        }

        byte toByte() {
            return (byte)mVal;
        }

        public static Extd create(int val) {
            for(Extd extd : values()) {
                if (extd.mVal == val) {
                    return extd;
                }
            }
            return INVALID;
        }

        public boolean gte(Extd other) {
            return mVal >= other.mVal;
        }
    }

    public final static int BITS_RNS = 0;
    public final static int BITS_BIT = 1;
    public final static int BITS_RES = 2;
    public final static int BITS_SET = 3;

    public final static int BITS_RLC = 0;
    public final static int BITS_RRC = 1;
    public final static int BITS_RL = 2;
    public final static int BITS_RR = 3;
    public final static int BITS_SLA = 4;
    public final static int BITS_SRA = 5;
    public final static int BITS_SRL = 7;

    public enum IZ {
        INVALID(0),
        ADD_IZ_BC(0x09),

        ADD_IZ_DE(0x19),

        LD_IZ_w(0x21),
        LD_pw_IZ(0x22),
        INC_IZ(0x23),
        ADD_IZ_IZ(0x29),
        LD__IZ_pw(0x2a),
        DEC_IZ(0x2b),

        INC_pIZb(0x34),
        DEC_pIZb(0x35),
        LD_pIZb_b(0x36),
        ADD_IZ_SP(0x39),

        LD_B_pIZb(0x46),
        LD_C_pIZb(0x4e),

        LD_D_pIZb(0x56),
        LD_E_pIZb(0x5e),

        LD_H_pIZb(0x66),
        LD_L_pIZb(0x6e),

        LD_pIZb_B(0x70),
        LD_pIZb_C(0x71),
        LD_pIZb_D(0x72),
        LD_pIZb_E(0x73),
        LD_pIZb_H(0x74),
        LD_pIZb_L(0x75),
        LD_pIZb_A(0x77),
        LD_A_pIZb(0x7e),

        ADD_A_pIZb(0x86),
        ADC_pIZb(0x8e),

        SUB_pIZb(0x96),
        SBC_pIZb(0x9e),

        AND_pIZb(0xa6),
        XOR_pIZb(0xae),

        OR_pIZb(0xb6),
        CP_pIZb(0xbe),

        BITS(0xcb),

        POP_IZ(0xe1),
        EX_pSP_IZ(0xe3),
        PUSH_IZ(0xe5),
        JP_IZ(0xe9),

        LD_SP_IZ(0xf9);

        private int mVal;

        IZ(int val) {
            mVal = val;
        }

        public static IZ create(int val) {
            for(IZ iz : values()) {
                if (iz.mVal == val) {
                    return iz;
                }
            }
            return INVALID;
        }
    }

    public enum IZBits {
        INVALID(0),
        RLC_pIXb(0x06),
        RRC_pIXb(0x0e),
        RL_pIXb(0x16),
        RR_pIXb(0x1e),
        SLA_pIXb(0x26),
        SRA_pIXb(0x2e),
        SRL_pIXb(0x3e),
        BIT_0_pIXb(0x46),
        BIT_1_pIXb(0x4e),
        BIT_2_pIXb(0x56),
        BIT_3_pIXb(0x5e),
        BIT_4_pIXb(0x66),
        BIT_5_pIXb(0x6e),
        BIT_6_pIXb(0x76),
        BIT_7_pIXb(0x7e),
        RES_0_pIXb(0x86),
        RES_1_pIXb(0x8e),
        RES_2_pIXb(0x96),
        RES_3_pIXb(0x9e),
        RES_4_pIXb(0xa6),
        RES_5_pIXb(0xae),
        RES_6_pIXb(0xb6),
        RES_7_pIXb(0xbe),
        SET_0_pIXb(0xc6),
        SET_1_pIXb(0xce),
        SET_2_pIXb(0xd6),
        SET_3_pIXb(0xde),
        SET_4_pIXb(0xe6),
        SET_5_pIXb(0xee),
        SET_6_pIXb(0xf6),
        SET_7_pIXb(0xfe);

        private int mVal;

        IZBits(int val) {
            mVal = val;
        }

        public static IZBits create(int val) {
            for(IZBits izBits : values()) {
                if (izBits.mVal == val) {
                    return izBits;
                }
            }
            return INVALID;
        }
    }
}
