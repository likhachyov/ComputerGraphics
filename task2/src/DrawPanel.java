import Interfaces.LineDrawer;
import Interfaces.OvalDrawer;
import Interfaces.PixelDrawer;
import drawers.BresenhamDrawer;
import drawers.DDALineDrawer;
import drawers.GraphicsPixelDrawer;
import drawers.WuDrawer;

import javax.swing.*;
import java.awt.*;

public class DrawPanel extends JPanel {

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        PixelDrawer pd = new GraphicsPixelDrawer(g2);
        LineDrawer ld;
        ld = new DDALineDrawer();
        ld = new BresenhamDrawer();
        BresenhamDrawer od = new BresenhamDrawer();
//        ld.drawLine(pd, 50, 150, 250, 150, Color.RED);
        od.fillPie(pd, 200, 250, 250, 320, 0, 360, new Color(51, 0, 0));
        od.drawOval(pd,150,250,500,12, Color.RED);
//        ld.drawLine(pd, 150, 100, 150, 200, Color.RED);
//        ld.drawLine(pd, 150, 150, 221, 221, Color.RED);

    }

}
