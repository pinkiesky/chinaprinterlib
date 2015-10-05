package chinaprinterlib;

import chinaprinterlib.css.CustomImageRenderer;
import chinaprinterlib.output.PrinterOutputStream;
import chinaprinterlib.output.image.ImagePrintHelper;
import chinaprinterlib.output.image.defaultimpl.SimpleThresholdFunc;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.xml.sax.SAXException;

/**
 *
 * @author rarity
 */
public class ChinaPrinterLib {

    public static void main(String[] args) throws FileNotFoundException, IOException, SAXException {
        for (int i = 0; i < 2; i++)
            printImage(ImageIO.read(new FileInputStream("/home/rarity/lpt/cm.png")));
    }
    
    private static void printHelloWorld () throws IOException {
        try (PrinterOutputStream outputStream = new PrinterOutputStream(new FileOutputStream("/dev/lp0"))) {
            outputStream.reset();
            
            outputStream.println("Hello, world!");
            
            outputStream.cut();
        }
    }
    
    /*
    * Don't use https page
    */
    private static void printWebPage (String url) throws IOException, SAXException {
        CustomImageRenderer ir = new CustomImageRenderer(); 
        ir.setWindowSize(new Dimension(ImagePrintHelper.PrinterSettings.getDefaultSettingsForXprinterXP80220().calculatePageWidth(), 1000), true);
        BufferedImage renderURL = ir.renderURL(url);
        
        printImage(renderURL);
    }
    
    private static void printImage (BufferedImage img) throws IOException, SAXException {
        try (PrinterOutputStream outputStream = new PrinterOutputStream(new FileOutputStream("/dev/lp0"))) {
            outputStream.reset();
            
            outputStream.setLineSpacing((byte)0);
            ImagePrintHelper.printImage(img, new SimpleThresholdFunc(127, 255), outputStream);
            outputStream.cut((byte)0);
        }
    }
}
