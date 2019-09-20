import java.awt.*;

public class DDALineDrawer implements LineDrawer {
    @Override
    public void drawLine(PixelDrawer pd, int x1, int y1, int x2, int y2, Color c) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double D = Math.max(Math.abs(dx), Math.abs(dy));
        double stepX = dx / D;
        double stepY = dy / D;
        for (int i = 0; i <= D; i++) {
            pd.drawPixel((int) (x1 + i * stepX), (int) (y1 + i * stepY), c);
        }
    }
}
