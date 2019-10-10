package Interfaces;

import java.awt.*;

public interface OvalDrawer {
    void drawOval(IPixelDrawer pd, int x, int y, int horizontalR, int verticalR, Color c);

    void fillPie(IPixelDrawer pl, int x0, int y0, int width, int height, int from, int to, Color color);
}
