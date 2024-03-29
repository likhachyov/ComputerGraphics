import Interfaces.LineDrawer;
import Interfaces.IPixelDrawer;
import drawers.*;

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
//        wd.drawPie(pd, 299, 295, 1300, 1300, 0, 360, Color.WHITE, true);
//        wd.drawPie(pd, 240,250, 1000, 1000, 0, 360, Color.YELLOW, true);

//        wd.drawPie(pd,240,250,299,1,0,360, Color.GREEN, false);
        // pizza
        wd.drawPie(g2,  215, 205, 150, 150, 30, 20, Color.YELLOW,true);
//        wd.drawPie(g2,  253, 250, 25, 0, 0, 360, Color.YELLOW,true);
//        wd.drawPie(g2,  540, 550, 500, 250, 130, 10, Color.cyan,false);
//        wd.fillOval(pd,300,300,200,400, Color.RED);
//        wd.drawEllipse(g2, Color.cyan, 240, 356, 40, 3);

        SvetaPie sp = new SvetaPie();
        sp.drawPie(pd, 200, 200, 100, 3, 360, 220, Color.BLUE, true);
    }

}
