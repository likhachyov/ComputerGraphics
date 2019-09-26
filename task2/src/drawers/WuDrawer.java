package drawers;

import Interfaces.LineDrawer;
import Interfaces.OvalDrawer;
import Interfaces.PixelDrawer;

import java.awt.*;

public class WuDrawer implements LineDrawer {
    @Override
    public void drawLine(PixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
        int x = x1;
        int y = y1;
        double dx = Math.abs(x2 - x1);
        double dy = Math.abs(y2 - y1);
        double tangent = dy / dx;
        float error = 0;   // The offset of the real Y coordinate relative to the rendered
        int directionY = (int) Math.signum(y2 - y1);
        int directionX = (int) Math.signum(x2 - x1);
        int secondY = y+directionY;
        for (int i = 0; i <= dx; i++) {
            x += directionX;
            error += tangent;
            if (error >= 1) {
                y += directionY;
                secondY += directionY;
                error -= 1;
            }
            System.out.println("e" + error);
            pd.drawPixel(x, y, c, 1-error); // основную точку рисуем с прозрачностью 1- error
            pd.drawPixel(x, secondY, c, error);  // вторую с прозрачностью равной error
        }
    }
}
