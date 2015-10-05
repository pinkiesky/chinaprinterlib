package chinaprinterlib.output;

import java.awt.image.BufferedImage;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 *
 * @author rarity
 */
public class PrinterOutputStream extends FilterOutputStream {
    public static final byte ESC = 0x1B, GS = 0x1D;
    
    private Charset charset;
    
    public PrinterOutputStream(OutputStream out) {
        this(out, Charset.forName("CP437"));
    }
    
    public PrinterOutputStream(OutputStream out, Charset charset) {
        super(out);
        this.charset = charset;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }
    
    /**
     * Resets the printer to its default settings<p>
     * See C-198 in EPSON ESC/P Ref. manual
     * @throws IOException
     */
    public void reset () throws IOException {
        out.write(ESC);
        out.write(0x40);
    }
    
    /**
     * Select character code table
     * <p>
     * Warning: before you need select charset (<code>setCharset</code>) for use in 
     * string encode
     * <p>
     * See C-79 in EPSON ESC/P Ref. manual
     * @param table
     * @throws IOException
     */
    public void selectChTable (byte table) throws IOException {
        out.write(ESC);
        out.write(0x74);
        out.write(table);
    }
    
    /**
     * Prints a string
     * @param str
     * @throws IOException
     */
    public void print (String str) throws IOException {
        out.write(str.getBytes(charset));
    }
    
    /**
     * Terminates the current line by writing the line separator string
     * @throws IOException
     */
    public void newLine () throws IOException {
        out.write(0x0A);
    }
    
    /**
     * Prints a String and then terminate the line
     * @param str
     * @throws IOException
     */
    public void println (String str) throws IOException {
        print(str);
        newLine();
    }
    
    /**
     * Cut paper with default margin
     * @throws IOException
     */
    public void cut () throws IOException {
        cut((byte) 125);
    }
    
    /**
     * Cut paper with user set margin
     * <p>
     * margin = [0, 255]
     * @param len margin
     * @throws IOException
     */
    public void cut (byte len) throws IOException {
        out.write(GS);
        out.write(0x56);
        out.write(0x41);
        out.write(len);
    }
    
    /**
     * Sets the line spacing to n / 180
     * @param n
     * @throws IOException
     */
    public void setLineSpacing (byte n) throws IOException {
        out.write(ESC);
        out.write(0x33);
        out.write(n);
    }
    
    @Deprecated
    public void setPrintSpeed (byte n) throws IOException {
        out.write(ESC);
        out.write(0x73);
        out.write(n);
    }
    
    public void setPapperOutDetectorEnabled (boolean enabled) throws IOException {
        out.write(ESC);
        out.write(enabled ? 0x39 : 0x38);
    }
    
    @Deprecated
    public void setLeftMargin (byte n) throws IOException {
        out.write(ESC);
        out.write(0x6C);
        out.write(n);
    }
    
    @Deprecated
    public void printImage (BufferedImage image) throws IOException {
        int numberDotPerColumn = 283 * (120 / 60); // MAGIC
        byte nh = (byte) (numberDotPerColumn / 256);
        byte nl = (byte) (numberDotPerColumn % 256);
        
        int normalizeWidth = Math.min(image.getWidth(), numberDotPerColumn);
        int normalizeHeight = (int)(Math.ceil(image.getHeight() / 24f + 1) * 24);
        
        
        byte[][] parts = new byte[normalizeHeight / 24][numberDotPerColumn * 3];
        
        for (int partsIndex = 0; partsIndex < parts.length; partsIndex++) {
            byte[] currentPart = parts[partsIndex];
            for (int byteNumber = 0; byteNumber < currentPart.length; byteNumber++) {
                for (int bitNumber = 0; bitNumber < 8; bitNumber++) {
                    int x = (byteNumber / 3) % normalizeWidth;
                    int y = (((byteNumber * 8 + bitNumber) % 24) + partsIndex * 24) % image.getHeight();
                    
                    int rgb = x < image.getWidth() && y < image.getHeight() ? image.getRGB(x, y) : 0;
                    byte color = (byte) ((rgb & 0xff) == 255 ? 0 : 1);
                    currentPart[byteNumber] |= color << (7 - bitNumber);
                }
            }
            
            //lpt.send_lpt([ESC_CH, chr(0x2A), chr(33), chr(nl), chr(nh)]) # start
            out.write(new byte[] {ESC, 0x2A, 33, nl, nh});
            out.write(currentPart);
        }
        
        reset();
    }
    
    @Deprecated
    public void printImage (boolean[][] bitImg) throws IOException {
        int imageWidth = bitImg.length;
        int imageHeight = bitImg[0].length;
        
        int numberDotPerColumn = 283 * (120 / 60); // MAGIC
        byte nh = (byte) (numberDotPerColumn / 256);
        byte nl = (byte) (numberDotPerColumn % 256);
        
        int normalizeWidth = Math.min(imageWidth, numberDotPerColumn);
        int normalizeHeight = (int)(Math.ceil(imageHeight / 24f + 1) * 24);
        
        
        byte[][] parts = new byte[normalizeHeight / 24][numberDotPerColumn * 3];
        
        for (int partsIndex = 0; partsIndex < parts.length; partsIndex++) {
            byte[] currentPart = parts[partsIndex];
            for (int byteNumber = 0; byteNumber < currentPart.length; byteNumber++) {
                for (int bitNumber = 0; bitNumber < 8; bitNumber++) {
                    int x = (byteNumber / 3) % normalizeWidth;
                    int y = (((byteNumber * 8 + bitNumber) % 24) + partsIndex * 24) % imageHeight;
                    
                    int rgb = x < imageWidth && y < imageHeight && bitImg[x][y] ? 0 : 1;
                    currentPart[byteNumber] |= rgb << (7 - bitNumber);
                }
            }
            
            //lpt.send_lpt([ESC_CH, chr(0x2A), chr(33), chr(nl), chr(nh)]) # start
            out.write(new byte[] {ESC, 0x2A, 33, nl, nh});
            out.write(currentPart);
        }
        
        reset();
    }
    
    
    @Override
    public void write (byte b[]) throws IOException {
        out.write(b);
    }
}
