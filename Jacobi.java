
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;



public strictfp class Jacobi {
    public static void main(String[] args)
    {
        boolean sorting = Boolean.parseBoolean(args[0]);
        ArrayList[] points = new ArrayList[2];
        for(int i = 0; i < 2; i++)
            points[i] = new ArrayList<Double>();
        int s = 5;
        Matrix a = new Matrix(s);
        
        Matrix v = new Matrix(s, s);
        v.identity();
        Matrix b = new Matrix(a.m, a.n);
        b.set(a);
        
        int iterations = 0;
        double startOffset = a.off();
        double offset = Double.MAX_VALUE;
        int nextI = 3, nextJ = 3;
        while(offset > 1e-9)
        {
            int maxI = -1;
            int maxJ = -1;
            if(sorting)
            {
                double maxValue = Double.MIN_VALUE;

                for(int i = 0; i < b.m; i++)
                {
                    for(int j = 0; j < b.n; j++)
                    {
                        if(i != j && Math.abs(b.get(i, j)) > maxValue)
                        {
                            maxI = i;
                            maxJ = j;
                            maxValue = Math.abs(b.get(i, j));
                        }
                    }
                }
            }
            else
            {
                maxI = nextI;
                maxJ = nextJ;
                nextJ++;
                if(nextJ == 5){
                    nextJ = 0;
                    nextI++;
                    if(nextI == 5)
                    {
                        nextI = nextJ = 0;
                    }
                }
            }

            Matrix smallMatrix = new Matrix(2, 2);
            smallMatrix.set(0, 0, b.get(maxI, maxI));
            smallMatrix.set(0, 1, b.get(maxI, maxJ));
            smallMatrix.set(1, 0, b.get(maxJ, maxI));
            smallMatrix.set(1, 1, b.get(maxJ, maxJ));
            
            double[][] e = smallMatrix.eigenvectors();

            Matrix u = new Matrix(2, 2);
            u.set(0, 0, e[0][0]);
            u.set(1, 0, e[0][1]);
            u.set(0, 1, e[1][0]);
            u.set(1, 1, e[1][1]);

            Matrix g = new Matrix(s, s);

            g.identity();
            g.set(maxI, maxI, u.get(0, 0));
            g.set(maxI, maxJ, u.get(0, 1));
            g.set(maxJ, maxI, u.get(1, 0));
            g.set(maxJ, maxJ, u.get(1, 1));
            
            b = (g.transpose().mul(b)).mul(g);
            v = v.mul(g);
            
            offset = b.off();
            
            iterations++;
            points[0].add(Math.log(offset));
            points[1].add(iterations*Math.log(9f/10)+Math.log(startOffset));
            System.out.println("Offset after "+iterations+" iterations: "+offset+" ");
        }

        JFrame f = new JFrame();
        f.add(new GraphPanel(points));
        f.setResizable(false);
        f.setSize(400, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        System.out.println("Original matrix:");
        System.out.println(b);
        System.out.println("Final matrix:");
        System.out.println(b);
    }
    private static class GraphPanel extends JPanel {
        private ArrayList<Double>[] points;
        private double yScale, xScale, minX, maxX, minY, maxY;
        public GraphPanel(ArrayList<Double>[] points)
        {
            this.points = points;
        }
        public void paint(Graphics g)
        {
            g.setColor(Color.white);
            g.fillRect(0, 0, 400, 300); maxY = Integer.MIN_VALUE;
            minY = Integer.MAX_VALUE;
            for(int i = 0; i < points.length; i++)
            {
                for(Object obj : points[i])
                {
                    if((Double)obj < minY)
                        minY = (Double) obj;
                    else if((Double)obj > maxY)
                        maxY = (Double) obj;
                }
            }
            yScale = 300 / (maxY - minY);
            minX = 0;
            maxX = points[0].size();
            xScale = 400 / (maxX - minX);
            for(double x = minX; x < maxX; x++)
            {
                g.setColor(Color.lightGray);
                g.drawLine(screenX(x), 0, screenX(x), 300);
                g.setColor(Color.black);
                g.drawLine(screenX(x), screenY(0)-4, screenX(x), screenY(0)+4);
            }
            for(double y = minY; y < maxY; y++)
            {
                g.setColor(Color.lightGray);
                g.drawLine(0, screenY(y), 400, screenY(y));
                g.setColor(Color.black);
                g.drawLine(screenX(0) - 4, screenY(y), screenX(0) + 4, screenY(y));
            }
            g.drawLine(0, screenY(0), 400, screenY(0));
            g.drawLine(screenX(0), 0, screenX(0), 300);
            for(int i = 0; i < points.length; i++)
            {
                if(i ==  0) g.setColor(Color.red); else g.setColor(Color.blue);
                ArrayList l = points[i];
                int lastX = -1;
                int lastY = -1;
                for(int j = 0; j < l.size(); j++)
                {
                    int sx = screenX(j);
                    int sy = screenY((Double) l.get(j));
                    if(lastX != -1)
                        g.drawLine(lastX, lastY, sx, sy);
                    lastX = sx;
                    lastY = sy;
                }
            }
        }
        private int screenX(double worldX)
        {
            return (int) ((worldX - minX)*xScale);
        }
        private int screenY(double worldY)
        {
            return (int) (300 - ((worldY - minY)*yScale));
        }
    }
}
