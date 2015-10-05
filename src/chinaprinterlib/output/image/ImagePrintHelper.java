package chinaprinterlib.output.image;

import chinaprinterlib.output.PrinterOutputStream;
import static chinaprinterlib.output.PrinterOutputStream.ESC;
import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author rarity
 */
public class ImagePrintHelper {
    private ImagePrintHelper () {}
    
    public static void printImage (BufferedImage image, IThresholdFunc func, PrinterOutputStream pos) throws IOException {
        printImage(func.threshold(image), pos, PrinterSettings.getDefaultSettingsForXprinterXP80220());
    }
    
    public static void printImage (BufferedImage image, IThresholdFunc func, PrinterOutputStream pos, PrinterSettings ps) throws IOException {
        printImage(func.threshold(image), pos, ps);
    }
    
    public static void printImage (boolean[][] bitImg, PrinterOutputStream out) throws IOException {
        printImage (bitImg, out, PrinterSettings.getDefaultSettingsForXprinterXP80220());
    }
    
    public static void printImage (boolean[][] bitImg, PrinterOutputStream out, PrinterSettings ps) throws IOException {
        int imageWidth = bitImg.length;
        int imageHeight = bitImg[0].length;
        
        int pageWidth = ps.calculatePageWidth();
        byte nh = (byte) (pageWidth / 256);
        byte nl = (byte) (pageWidth % 256);
        
        int normalizeWidth = Math.min(imageWidth, pageWidth);
        int normalizeHeight = (int)(Math.ceil(imageHeight * 1f / ps.density.dotsPerColumn + 1) * ps.density.dotsPerColumn);
        
        byte[][] parts = new byte[normalizeHeight / ps.density.dotsPerColumn][pageWidth * ps.density.bytesPerColumn]; // todo remove this array
        
        for (int partsIndex = 0; partsIndex < parts.length; partsIndex++) {
            byte[] currentPart = parts[partsIndex];
            for (int byteNumber = 0; byteNumber < currentPart.length; byteNumber++) {
                for (int bitNumber = 0; bitNumber < 8; bitNumber++) {
                    int x = (byteNumber / ps.density.bytesPerColumn) % normalizeWidth;
                    int y = (((byteNumber * 8 + bitNumber) % ps.density.dotsPerColumn) + partsIndex * ps.density.dotsPerColumn) % imageHeight;
                    
                    int rgb = x < imageWidth && y < imageHeight && bitImg[x][y] ? 0 : 1;
                    currentPart[byteNumber] |= rgb << (7 - bitNumber);
                }
            }
            
            out.write(new byte[] {ESC, 0x2A, ps.density.parameter, nl, nh});
            out.write(currentPart);
        }
        
        out.reset();
    }
    
    public static class PrinterSettings {
        public final int dotPerWidth;
        public final DotDensity density;

        public PrinterSettings(int dotPerWidth, DotDensity density) {
            this.dotPerWidth = dotPerWidth;
            this.density = density;
        }
        
        public int calculatePageWidth () {
            return dotPerWidth * (density.horizontalDpi / DotDensity.DEFAULT.horizontalDpi);
        }
        
        public static PrinterSettings getDefaultSettingsForXprinterXP80220 () {
            return new PrinterSettings(283, DotDensity.HIGH);
        }
    }
}
