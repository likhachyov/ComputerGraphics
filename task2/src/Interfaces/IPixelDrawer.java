package Interfaces;

import drawers.Ellipse;
import java.awt.*;
import java.util.Queue;
import java.util.Stack;

public interface IPixelDrawer {

    void drawPixel(int x, int y, Color c);

    void drawPixel(int x, int y, int transparency, Color c);

    void drawMirrorPixels(Ellipse ell, int x, int y, Color color, int transparency);

    void drawPixels(Stack<Ellipse.Point> s);
    void drawPixels(Queue<Ellipse.Point> q);

    void putPixels(int x0, int y0, double x, double y, int from, int to, double curAlpha, int part, Color color);

    void fill(int x0, int y0, double x, double y, int from, int to, double curAlpha, Color color);
}
