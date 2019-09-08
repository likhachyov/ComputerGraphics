import java.awt.*;

public class Sun implements PicObject {

    private int cX, cY, r;

    private int rayCount, rayLength;

    private double angleBetweenRay;

    private Color color, rayColor;

    public Sun(int cX, int cY, int r, int rayCount, int rayLength, Color color, Color rayColor) {
        this.cX = cX;
        this.cY = cY;
        this.r = r;
        this.rayCount = rayCount;
        this.rayLength = rayLength;
        this.color = color;
        this.rayColor = rayColor;
        if (rayCount != 0)
            angleBetweenRay = 2 * Math.PI / rayCount;
    }

    @Override
    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.fillOval(cX - r, cY - r, 2 * r, 2 * r);
        double x0, x1, y0, y1;
        for (int i = 0; i < rayCount; i++) {
            x0 = cX + r * Math.cos(angleBetweenRay * i);
            x1 = cX + (r + rayLength) * Math.cos(angleBetweenRay * i);
            y0 = cY + r * Math.sin(angleBetweenRay * i);
            y1 = cY + (r + rayLength) * Math.sin(angleBetweenRay * i);
            g2.drawLine((int) x0, (int) y0, (int) x1, (int) y1);
        }
    }
}
