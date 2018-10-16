package edu.vt.cs5560.amiout.domain;

public enum NERCRegion
{
    WECC,
    MRO,
    FRCC,
    RF,
    SERC,
    RE,
    TRE,
    MAAC,
    SPP,
    WSCC,
    MAIN,
    NPCC,
    ECAR,
    RFC,
    SPP_RE,
    REC,
    PR,
    ERCOT,
    HI,
    MECO,
    HECO,
    NPPC;

    public static NERCRegion getValue(String v)
    {
        return NERCRegion.valueOf(v);
    }
}
