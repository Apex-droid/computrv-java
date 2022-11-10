 

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;


public class computor {
    public static double sqrt(double d){
        double t = d;
        while (( t > d / t )){
            t = ( d / t + t ) / 2.0;
        }
        return t;
    }
    public static int abs(int a)
    {
        if (a < 0)
            return a * -1;
        return a;
    }
    public static double abs(double a)
    {
        if (a < 0)
            return a * -1;
        return a;
    }


    public abstract static class digits
    {
        String[] coefficents;
        String[] exps;
        long get_len() {
            return this.coefficents.length;
        }
        double get_degree(int i) {
            return Double.valueOf(this.exps[i].replace(" ",""));
        }
        public void set_digs(String s){
            this.coefficents = s.split(" \\* X\\^(\\d+\\.?(\\d+)?)");
            this.exps = s.split("((?<!^)([+-] )(?<!^)\\d+\\.?(\\d+)?) \\* X\\^");
            this.exps[0] = this.exps[0].replaceAll("\\d+\\.?(\\d+)? \\* X\\^", "");
        }
        public abstract double get_num(int i);

        digits(){}
    }
    public static class left_side extends digits {
        public double get_num(int i) {
            return Double.valueOf(this.coefficents[i].replace(" ",""));
        }
    }
    public static class right_side extends digits {
        public double get_num(int i) {
            return -Double.valueOf(this.coefficents[i].replace(" ",""));
        }
    }

    public static class digit_line
    {

        public left_side left_numbers = new left_side();

       public  right_side right_numbers = new right_side();

        public digit_line(String  Equation)
        {


            String digit = "(\\d+\\.?(\\d+)?)";
            String X_exp = " \\* X\\^";
            String member = digit + X_exp + digit;
            String side = "((- )*" + member + ")( [+-] " + member + ")*";
            Pattern p = Pattern.compile(side + " = "  + side);
            Matcher m = p.matcher(Equation);

            if(!m.matches())
            {

                System.out.println("Wrong input");
                System.exit(1);
            }
            String[] Left_And_Right = Equation.split(" = ");

            left_numbers.set_digs(Left_And_Right[0]);
            right_numbers.set_digs(Left_And_Right[1]);
        }

    };

    public static class coefficents
    {
        NumberFormat nf = new DecimalFormat("0.######");
        NavigableMap<Double, Double> other = new TreeMap<>();
        private double a = 0;
        private double b = 0;
        private double c = 0;


        private void reduce(digits numbers)
        {
            double degree;
            long len = numbers.get_len();
            for(int i = 0; i < len; i++) {

                degree = numbers.get_degree(i);
                if(degree == 0)
                    c += numbers.get_num(i);
                else if(degree == 1)
                    b += numbers.get_num(i);

                else if(degree == 2)
                    a += numbers.get_num(i);

                else if(other.containsKey(degree))
                    other.put(degree, numbers.get_num(i) + other.get(degree));

                else
                    other.put(degree, numbers.get_num(i));

                }
            }


        public void solution()
        {
            if(other.size() != 0)
                System.out.println("The polynomial degree is strictly greater than 2 or not whole number, I can't solve.");
            else if(a == 0 && b !=0)
                System.out.println("The solution is" + (-c/b));
            else if(a == 0 && b == 0 && c == 0)
                System.out.println("Each real number is a solution.");

            else
            {
                double d = b*b - 4*a*c;
                if(d < 0)
                    System.out.println("Discriminant is strictly negative, the two complex solutions are:\n"
                            + nf.format(-b/(2*a)) + " + " + nf.format(abs(sqrt(-d)/(2*a))) + "i"+ "\n"
                            + nf.format(-b/(2*a)) + " - " + nf.format(abs(sqrt(-d)/(2*a))) + "i");
                else if(d > 0)
                    System.out.println("Discriminant is strictly positive, the two real solutions are:\n"
                            + nf.format((-b + sqrt(d))/(2*a))+ "\n" + nf.format((-b - sqrt(d))/(2*a)));
                else if(d == 0)
                    System.out.println("The solution is:\n" + nf.format((-b)/(2*a)));
            }
        }
        public void reduced_form()
        {

            String reduced_form = "";            String plus = " + ";
            String minus = " - ";
            if(c != 0 || b != 0 || a != 0 )
                reduced_form += ((c < 0) ? minus : "")
                        + nf.format(abs(c)) + " * X^0";
            if(b != 0 || a != 0)
                reduced_form += ((b >= 0) ? plus : minus)
                        + nf.format(abs(b)) + " * X^1";
            if(a != 0 || other.size() !=0 )
                reduced_form += ((a >= 0) ? plus : minus)
                        + nf.format(abs(a)) + " * X^2";
            int pre = 4;
            for (Map.Entry<Double, Double> entry : other.entrySet()) {
                if(pre < entry.getKey() )
                    for(; pre < entry.getKey() ; pre++)
                        reduced_form += " + 0 * X^" + pre;
                reduced_form += ((entry.getValue() > 0) ? plus : minus)
                        + nf.format(abs(entry.getValue())) + " * X^" + nf.format(entry.getKey());
                pre = pre - pre % 1 ;
            }
            if(reduced_form == "")
                System.out.println("0 = 0");
            else
                System.out.println(reduced_form + " = 0");
        }
        public void polynomal_degree()
        {
            double max = 0;
            if(a != 0)
                max = 2;
            else if (b != 0)
                max = 1;
            if(other.size() != 0) {
                Map.Entry<Double, Double>  last = other.lastEntry();
                if(last.getKey() > max)
                    max = last.getKey();
            }
            System.out.println("Polynomial degree: " + nf.format(max));
        }

        public coefficents(String equation)
        {

            digit_line numbers = new digit_line(equation);
            reduce(numbers.left_numbers);
            reduce(numbers.right_numbers);

        }

    }

    public static void main(String[] args) {

        if(args.length != 1 )
        {
            System.out.println("Wrong input");
            System.exit(1);
        }
        coefficents Equation = new coefficents(args[0]);
        Equation.reduced_form();
        Equation.polynomal_degree();
        Equation.solution();
    }
}
