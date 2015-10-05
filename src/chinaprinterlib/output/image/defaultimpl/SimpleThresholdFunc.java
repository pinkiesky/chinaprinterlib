package chinaprinterlib.output.image.defaultimpl;

import chinaprinterlib.output.image.IThresholdFunc;
import java.awt.image.BufferedImage;

/**
 *
 * @author rarity
 */
public class SimpleThresholdFunc implements IThresholdFunc {
    
    private double thresholdMin, thresholdMax;

    public SimpleThresholdFunc() {
        this(127, 255);
    }

    public SimpleThresholdFunc(double thresholdMin, double thresholdMax) {
        this.thresholdMin = thresholdMin;
        this.thresholdMax = thresholdMax;
    }

    @Override
    public boolean[][] threshold(BufferedImage image) {
        boolean[][] bitImg = new boolean[image.getWidth()][image.getHeight()];
        
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                double lum = getLum(image.getRGB(x, y));
                bitImg[x][y] = thresholdMin <= lum && lum <= thresholdMax;
            }
        }
        
        return bitImg;
    }
    
    public static double getLum (int rgb) {
        int b = rgb & 0x0000ff;
        int g = (rgb & 0x00ff00) >> 8;
        int r = (rgb & 0xff0000) >> 16;
        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }
}
