package chinaprinterlib.output.image;

/**
 * Enum with dot density for XPRINTER XP80220
 * <p>
 * See C-198 in EPSON ESC/P Ref. manual
 * @author rarity
 */
public enum DotDensity {

    DEFAULT((byte)0, 60, 60, 8),
    HIGH((byte)33, 120, 180, 24);
    
    public final byte parameter;
    public final int horizontalDpi, verticalDpi, dotsPerColumn, bytesPerColumn;

    private DotDensity(byte parameter, int horizontalDpi, int verticalDpi, int dotsPerColumn) {
        this.parameter = parameter;
        this.horizontalDpi = horizontalDpi;
        this.verticalDpi = verticalDpi;
        this.dotsPerColumn = dotsPerColumn;
        this.bytesPerColumn = this.dotsPerColumn / 8;
    }
}
