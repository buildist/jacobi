public strictfp class Matrix {
    public int m;
    public int n;
    private double[][] matrix;
    public Matrix(int m, int n)
    {
        create(m, n);
    }
    public Matrix(String s)
    {
        String[] parts = s.split(",");
        for(int i = 0; i < parts.length; i++)
        {
            String[] values = parts[i].split(" ");
            if(matrix == null)
                create(parts.length, values.length);
            for(int j = 0; j < values.length; j++)
            {
                set(i, j, Double.parseDouble(values[j]));
            }
        }
    }
    public Matrix(int s)
    {
        Matrix r = new Matrix(s, s);
        r.randomize();
        set(r.mul(r.transpose()));
    }
    private void create(int m, int n)
    {
        matrix = new double[m][n];
        this.m = m;
        this.n = n;
    }
    public void randomize()
    {
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < m; j++)
            {
                set(i, j, Math.random()*10);
            }
        }
    }
    public void identity()
    {
        for(int i = 0; i < m; i++)
        {
            set(i, i, 1);
        }
    }
    public double get(int m, int n)
    {
        return matrix[m][n];
    }
    public void set(int m, int n, double v)
    {
        matrix[m][n] = v;
    }
    public void set(Matrix m)
    {
        create(m.m, m.n);
        for(int i = 0; i < m.m; i++)
        {
            for(int j = 0; j < m.n; j++)
            {
                set(i, j, m.get(i, j));
            }
        }
    }
    public Matrix mul(Matrix x)
    {
        Matrix m = new Matrix(this.m, x.n);
        for(int i = 0; i < m.m; i++)
        {
            for(int j = 0; j < m.n; j++)
            {
                double sum = 0;
                for(int a = 0; a < this.n; a++)
                {
                    sum = sum + (get(i, a) * (x.get(a, j)));
                }
                m.set(i, j, sum);
            }
        }
        return m;
    }
    public Matrix transpose()
    {
        Matrix r = new Matrix(n, m);
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                r.set(j, i, get(i, j));
            }
        }
        return r;
    }
    public double off()
    {
        double r = 0;
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                if(i != j)
                    r += (get(i,j) * (get(i,j)));
            }
        }
        return r;
    }
    public double[] eigenvalues()
    {
        double a = (get(0, 0) + (get(1, 1))) / (2);
        double c = (get(0, 0) - (get(1, 1)))/2;
        double b = Math.sqrt(get(0,1)*get(0,1) + c*c);
        return new double[]{a+b, a-b};
    }
    public double[][] eigenvectors()
    {
        double[] e = eigenvalues();
        double eplus = e[0] > e[1] ? e[0] : e[1];
        Matrix b = new Matrix(m, n);
        b.set(this);
        b.set(0, 0, b.get(0, 0) - eplus);
        b.set(1, 1, b.get(1, 1) - eplus);
        
        double[] r1_perp = new double[]{b.get(1, 0), -b.get(0, 0)};
        double r1_length = Math.sqrt((r1_perp[0]*(r1_perp[0])) + (r1_perp[1]*(r1_perp[1])));
        double[] u1 = new double[]{r1_perp[0]/(r1_length), r1_perp[1]/(r1_length)};
        double[] u2 = new double[]{-u1[1], u1[0]};
        return new double[][]{u1, u2};
    }
    @Override
    public String toString()
    {
        String s = "";
        for(int i = 0; i < m; i++)
        {
            for(int j = 0; j < n; j++)
            {
                s += get(i, j)+" ";
            }
            s += "\n";
        }
        return s;
    }
}
