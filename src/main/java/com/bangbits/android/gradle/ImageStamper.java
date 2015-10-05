package com.bangbits.android.gradle;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

/*
Typical Android launcher icon sizes:
    48 × 48 (mdpi)
    72 × 72 (hdpi)
    96 × 96 (xhdpi)
    144 × 144 (xxhdpi)
    192 × 192 (xxxhdpi)
    512 × 512 (Google Play store)
*/
public class ImageStamper {
    
    private static final float LOWER_3RD_Y_OFFSET = 0.66f;
    private static final Color LOWER3RD_DARK_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.3f);
    private static final Color LABEL_DARK_COLOR = new Color(1.0f, 1.0f, 1.0f, 1.0f);
    
    public static BufferedImage readIntoBufferedImage(File inputFile){
        BufferedImage inputImage = null;

        try {
            inputImage = ImageIO.read(inputFile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return inputImage;
    }

    public static void generateImage(BufferedImage inputImage, 
            ImageColorStats imageStats, 
            String buildTypeName, 
            File outputFile) {

        BufferedImage outputImage = generateImage(inputImage, imageStats, buildTypeName);

        try {
            ImageIO.write(outputImage, "png", outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
    public static BufferedImage generateImage(BufferedImage inputImage, ImageColorStats imageStats, String buildTypeName) {
        
        int width = inputImage.getWidth();
        int height = inputImage.getHeight();
        
        RectRegion imageRectRegion = detectImageRectRegion(inputImage);
        //System.out.println("+-Icon RectRegion: " + imageRectRegion.toString());

        // Find the lower-third label region
        RectRegion lower3rdRectRegion = new RectRegion(
                0,
                imageRectRegion.getY() + (int)(imageRectRegion.getHeight()*LOWER_3RD_Y_OFFSET),
                inputImage.getWidth(),
                inputImage.getHeight() - (int)(imageRectRegion.getHeight()*LOWER_3RD_Y_OFFSET) - imageRectRegion.getY()
        );
        //System.out.println("+-Lower 3rd RectRegion: " + lower3rdRectRegion.toString());

        // Find the lower-third label region
        RectRegion labelRectRegion = new RectRegion(
                imageRectRegion.getX(),
                imageRectRegion.getY() + (int)(imageRectRegion.getHeight()*LOWER_3RD_Y_OFFSET),
                imageRectRegion.getWidth(),
                imageRectRegion.getHeight() - (int)(imageRectRegion.getHeight()*LOWER_3RD_Y_OFFSET)
        );
        //System.out.println("+-Label RectRegion: " + labelRectRegion.toString());
        
        BufferedImage outputImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = outputImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Draw upper 2/3 unaltered
        g2d.drawImage(inputImage,
                0,
                0,
                lower3rdRectRegion.getWidth(),
                height-lower3rdRectRegion.getHeight(),
                0,
                0,
                lower3rdRectRegion.getWidth(),
                height-lower3rdRectRegion.getHeight(),
                null);
        
        // Draw lower 1/3 from a blurred version of the image
        Kernel kernel = createKernel(3, 3);
        BufferedImageOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        BufferedImage blurredInputImage = op.filter(inputImage, null);
        g2d.drawImage(blurredInputImage, 
                0,
                height-lower3rdRectRegion.getHeight(),
                width,
                height,
                0,
                height-lower3rdRectRegion.getHeight(),
                width,
                height,
                null);
        
        drawColorOverlay(g2d, lower3rdRectRegion, LOWER3RD_DARK_COLOR);
        drawLabel(g2d, labelRectRegion, LABEL_DARK_COLOR, buildTypeName);
        
        /*    
        if(imageStats.luminance < 0.3f){
            drawColorOnRegion(g2d, lower3rdRectRegion, LOWER3RD_BRIGHT_COLOR);
        }else{
            drawColorOnRegion(g2d, lower3rdRectRegion, LOWER3RD_DARK_COLOR);
        }
        
        if(imageStats.brightPixelCount > imageStats.darkPixelCount){
            drawLabel(g2d, labelRectRegion, LABEL_BRIGHT_COLOR, buildTypeName);
        }else{
            drawLabel(g2d, labelRectRegion, LABEL_DARK_COLOR, buildTypeName);
        }
        */
        g2d.dispose();
        
        return outputImage;
    }

    private static RectRegion detectImageRectRegion(BufferedImage inputImage) {

        int height = inputImage.getHeight();
        int width = inputImage.getWidth();
        
        // Search for transparent image padding so that we draw on actual visible pixels
        int paddingLeft = Collections.max(Arrays.asList(
                getIconAlphaPaddingLeft(inputImage, width, (int)(height * 0.75f)),
                getIconAlphaPaddingLeft(inputImage, width, (int)(height * 0.8f)),
                getIconAlphaPaddingLeft(inputImage, width, (int)(height * 0.85f))
            )
        );
        int paddingRight = Collections.max(Arrays.asList(
                getIconAlphaPaddingRight(inputImage, width, (int)(height * 0.75f)),
                getIconAlphaPaddingRight(inputImage, width, (int)(height * 0.8f)),
                getIconAlphaPaddingRight(inputImage, width, (int)(height * 0.85f))
            )
        );        
        int paddingTop = getIconAlphaPaddingTop(inputImage, height, (int)(width * 0.5f));
        int paddingBottom = Collections.max(Arrays.asList(
                getIconAlphaPaddingBottom(inputImage, height, (int)(width * 0.4f)),
                getIconAlphaPaddingBottom(inputImage, height, (int)(width * 0.5f)),
                getIconAlphaPaddingBottom(inputImage, height, (int)(width * 0.6f))
            )
        );
        
        return new RectRegion(
            paddingLeft,
            paddingTop,
            width - (paddingRight + paddingLeft),
            height - (paddingBottom + paddingTop)
        );
    }
    
    private static int getIconAlphaPaddingLeft(BufferedImage inputImage, int width, int y) {
        for(int x = 0; x < width/3; x++){
            if(isOpague(inputImage.getRGB(x, y))){
                //System.out.println("PaddingLeft@" + y + ": " + x);
                return x;
            }
        }
        //System.out.println("PaddingLeft@" + y + ": " + 0);
        return width/3;
    }

    private static int getIconAlphaPaddingRight(BufferedImage inputImage, int width, int y) {
        for(int x = width-1; x >= width/3; x--){
            if(isOpague(inputImage.getRGB(x, y))){
                //System.out.println("PaddingRight@" + y + ": " + (width-x-1));
                return width-x-1;
            }
        }
        //System.out.println("PaddingRight@" + y + ": " + 0);
        return width/3;
    }

    private static int getIconAlphaPaddingTop(BufferedImage inputImage, int height, int x) {
        for(int y = 0; y < height/2; y++){
            if(isOpague(inputImage.getRGB(x, y))){
                return y;
            }
        }
        return 0;
    }    
    
    private static int getIconAlphaPaddingBottom(BufferedImage inputImage, int height, int x) {
        for(int y = height-1; y >= height/2; y--){
            if(isOpague(inputImage.getRGB(x, y))){
                return height-y+1;
            }
        }
        return 0;
    }    

    private static void drawColorOverlay(Graphics2D g2d, RectRegion labelRectRegion, Color color) {
        
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.7f));
        g2d.setPaint(color);
        g2d.fillRect(labelRectRegion.getX(), 
                labelRectRegion.getY(), 
                labelRectRegion.getWidth(), 
                labelRectRegion.getHeight());
    }

    private static void drawLabel(Graphics2D g2d, RectRegion labelRectRegion, Color color, String labelext) {

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 1.0f));
        g2d.setColor(color);
        
        FontMetrics fm;
        int textWidth = 0;
        int fontsize = 50;
        String text = labelext.toUpperCase();
        do{
            g2d.setFont(new Font("Serif", Font.BOLD, fontsize--));
            fm = g2d.getFontMetrics();
            textWidth = fm.stringWidth(text);
            
        }while(textWidth > (labelRectRegion.getWidth()-2));

        int x = labelRectRegion.getX() + (labelRectRegion.getWidth() - fm.stringWidth(text))/2 + 1;
        int y = labelRectRegion.getY() + (fm.getAscent() + (labelRectRegion.getHeight() - (fm.getAscent() + fm.getDescent())) / 2) + 1;
        
        g2d.drawString(text, x, y);
    }

    /*
    private static Color calcAvgColor(BufferedImage image, RectRegion rectRegion) {
        int r = 0;
        int g = 0;
        int b = 0;
        for(int y = rectRegion.getY(); y < (rectRegion.getY()+rectRegion.getHeight()); y++){
            for(int x = rectRegion.getX(); x < (rectRegion.getX()+rectRegion.getWidth()); x++){
                int color = image.getRGB(x, y);
                r+= (color >>> 16) & 0xff;
                g+= (color >>>  8) & 0xff;
                b+= color & 0xff;                
            }
        }
        r /= rectRegion.getArea();
        g /= rectRegion.getArea();
        b /= rectRegion.getArea();
        return new Color(r, g, b);
    }*/

    private static boolean isBright(float luminance) {
        return !isDark(luminance);
    }
    
    private static boolean isDark(float luminance) {
        return luminance < 0.5;
    }

    private static Kernel createKernel(int x, int y) {
        int dimension = x*y;
        float pixelQuantity = 1f/(dimension);
        float[] array = new float[dimension];
        Arrays.fill(array, pixelQuantity);
        return new Kernel(x, y, array);
    }

    private static boolean isOpague(int rgba) {
        return (((rgba >> 24) & 0xff) != 0);
    }
}
