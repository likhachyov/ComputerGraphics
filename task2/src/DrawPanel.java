import Interfaces.LineDrawer;
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
//        g2.drawOval(35,215, 400, 100);
        PixelDrawer pd = new GraphicsPixelDrawer(g2);
        LineDrawer ld;
        ld = new DDALineDrawer();
        WuDrawer wd = new WuDrawer();
        BresenhamDrawer bd = new BresenhamDrawer();
//        od.drawLine(pd,150, 250, 150, 50, Color.RED);
//        od.fillPie(pd, 200, 250, 250, 320, 0, 360, new Color(51, 0, 0));
//        od.drawOval(pd,220,250,200,50, Color.BLACK);
        bd.drawEllipse(220, 255, 290, 200, g2);
//        ld.drawLine(pd, 150, 100, 150, 200, Color.RED);
//        ld.drawLine(pd, 150, 150, 221, 221, Color.RED);
//        ld.drawLine(pd, 50, 150, 250, 150, Color.RED);

//        od.fillPie(pd,150, 250, 150, 50, 45, 190, Color.GREEN);
        wd.drawPie(pd, 240, 255, 200, 50, 199, 265, Color.BLACK, true);
    }

}
