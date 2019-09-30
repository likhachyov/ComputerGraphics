package Interfaces;

import drawers.Ellipse;
import java.awt.*;
import java.util.Queue;
import java.util.Stack;

public interface PixelDrawer {

    void drawPixel(int x, int y, Color c);

    void drawPixel(int x, int y, Color c, int transparency);

    void drawMirrorPixels(Ellipse ell, int x, int y, Color color, int transparency);

    void drawPixels(Stack<Ellipse.Point> s);
    void drawPixels(Queue<Ellipse.Point> q);
}
