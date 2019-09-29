package Interfaces;

import drawers.Ellipse;
import drawers.WuDrawer;

import java.awt.*;
import java.util.Queue;
import java.util.Stack;

public interface PixelDrawer {

    void drawPixel(int x, int y, Color c);

    void drawPixel(int x, int y, Color c, int transparency);

    void drawMirrorPixels(Ellipse ell, int x, int y, Color color, int transparency);

    void drawPixels(Stack<WuDrawer.Point> s);
    void drawPixels(Queue<WuDrawer.Point> q);
}
