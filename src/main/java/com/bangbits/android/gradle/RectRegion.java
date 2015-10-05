package com.bangbits.android.gradle;

public class RectRegion {
    
    private final int offsetX;
    private final int offsetY;
    private final int width;
    private final int height;
    
    public RectRegion(int offsetX, int offsetY, int width, int height){
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
    }
    
    public int getX(){
        return offsetX;
    }
    
    public int getY(){
        return offsetY;
    }

    public int getWidth(){
        return width;
    }
    
    public int getHeight(){
        return height;
    }
    
    public int getArea(){
        return height * width;
    }

    @Override
    public String toString() {
        return "RectRegion{" + "offsetX=" + offsetX + ", offsetY=" + offsetY + ", width=" + width + ", height=" + height + '}';
    }
    
}
