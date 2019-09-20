import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        DDALineDrawer ld = new DDALineDrawer();
        ld.drawLine(new GraphicsPixelDrawer(g2), 30, 300, 60, 40, Color.BLACK);
    }

}
