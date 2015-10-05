package com.bangbits.android.gradle;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

public class ImageColorStats{
    
    private final static int BORDER_IGNORE_OFFSET = 0;
    
    public int red;
    public int green;
    public int blue;
    public int alpha;
    public float luminance;
    public int darkPixelCount;
    public int brightPixelCount;
    public int alphaPixelCount;
    
    public ImageColorStats(BufferedImage inputImage){

        for(int i=BORDER_IGNORE_OFFSET; i < inputImage.getWidth()-BORDER_IGNORE_OFFSET ; i++){
            for(int j=BORDER_IGNORE_OFFSET; j < inputImage.getHeight()-BORDER_IGNORE_OFFSET ; j++){
                int pixelColor = inputImage.getRGB(i, j);
                int pixelAlpha = (pixelColor >> 24) & 0xff;
                int pixelRed = (pixelColor >>> 16) & 0xff;
                int pixelGreen = (pixelColor >>>  8) & 0xff;
                int pixelBlue = pixelColor & 0xff;
                
                if(!isTransparent(alpha)){
                    
                    float pixelLuminance = calculateSRGBLuminance(pixelRed, pixelGreen, pixelBlue);
                    
                    if (pixelLuminance < 0.1f) {
                        darkPixelCount++;
                    }else if(pixelLuminance > 0.9f){
                        brightPixelCount++;
                    }
                }
                else{
                    alphaPixelCount++;
                }
                
                red += pixelRed;
                green += pixelGreen;
                blue += pixelBlue;
                alpha += pixelAlpha;
            }
        }

        int pixelCount = (inputImage.getWidth()-(2*BORDER_IGNORE_OFFSET))*
                (inputImage.getHeight()-(2*BORDER_IGNORE_OFFSET));
        
        red /= pixelCount;
        green /= pixelCount;
        blue /= pixelCount;
        alpha /= pixelCount;
        luminance = calculateSRGBLuminance((int)red, (int)green, (int)blue);
    }
    
    private static float calculateSRGBLuminance(int red, int green, int blue){
        return (red * 0.2126f + green * 0.7152f + blue * 0.0722f) / 255;        
    }
    
    private static boolean isTransparent( int alpha ) {
        return alpha == 0x00;
    }

    @Override
    public String toString() {
        return "ImageStats{" + "r=" + red 
                + ", g=" + green 
                + ", b=" + blue 
                + ", a=" + alpha 
                + ", lum=" + new DecimalFormat("0.#").format(luminance)
                + ", darkPixels=" + darkPixelCount
                + ", brightPixels=" + brightPixelCount
                + ", transPixels=" + alphaPixelCount + '}';
    }
}