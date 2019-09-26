package Interfaces;

import java.awt.*;

public interface PixelDrawer {

    void drawPixel(int x, int y, Color c);

    void drawPixel(int x, int y, Color c, int transparency);
}
