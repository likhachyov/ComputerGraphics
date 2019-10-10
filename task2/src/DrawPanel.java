import Interfaces.LineDrawer;
import Interfaces.IPixelDrawer;
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
//        g2.setColor();
        g2.fillRect(0, 0, 1500,1500);
//        g2.drawOval(35,215, 400, 100);
        IPixelDrawer pd = new GraphicsPixelDrawer(g2);
        LineDrawer ld;
        ld = new DDALineDrawer();
        WuDrawer wd = new WuDrawer();
        BresenhamDrawer bd = new BresenhamDrawer();
//        bd.drawEllipse(240,250,299,1,g2);
    //  omelette
//        wd.drawPie(pd, 299, 295, 300, 300, 0, 360, Color.WHITE, true);
//        wd.drawPie(pd, 240,250, 100, 100, 0, 360, Color.YELLOW, true);

//        wd.drawPie(pd,240,250,299,1,0,360, Color.GREEN, false);
        wd.drawPie(g2,  390, 350, 500, 250, 90, 0, Color.cyan);
//        wd.drawEllipse(g2, Color.cyan, 240, 356, 40, 3);
    }

}
