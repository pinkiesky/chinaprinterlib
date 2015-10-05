package chinaprinterlib.output.image;

import java.awt.image.BufferedImage;

/**
 *
 * @author rarity
 */
public interface IThresholdFunc {
    public boolean[][] threshold (BufferedImage image);
}
