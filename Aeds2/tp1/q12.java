import java.util.*;
public class q12 {
    static boolean Parada(String txt) {
        if (txt.length() == 3
                && txt.charAt(0) == 'F'
                && txt.charAt(1) == 'I'
                && txt.charAt(2) == 'M') {
            return true;
        } else {
            return false;
        }
    }

public static int getSum(String txt, int i, int sum) {
    if (i >= txt.length()) {
        return sum;
    }

    char c = txt.charAt(i);

    if (c >= '0' && c <= '9') {
        return getSum(txt, i + 1, sum + (c - '0'));
    } else {
        return getSum(txt, i + 1, sum);
    }
}
    public static void main(String[] args){
        int i=0 , sum=0;
		Scanner sc = new Scanner(System.in);
		String txt = sc.nextLine();

		while(!Parada(txt)){
            System.out.println(getSum(txt, i, sum));
			txt = sc.nextLine();
		}
	sc.close();
	}
}