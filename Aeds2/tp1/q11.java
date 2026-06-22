import java.text.Normalizer;
import java.util.*;

public class q11 {
    static boolean Parada(String txt) {
        return (txt.length() == 3 &&
                txt.charAt(0) == 'F' &&
                txt.charAt(1) == 'I' &&
                txt.charAt(2) == 'M');
    }

    public static String inverString(String Str, String Str2){
       Str = Normalizer.normalize(Str, Normalizer.Form.NFC);

        if (Str.length() == 0) {
            return Str2;
        } else {
            char c = Str.charAt(Str.length() - 1);
            Str2 += c;
            return inverString(Str.substring(0, Str.length() - 1), Str2);
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String txtIn = sc.nextLine();

        while (!Parada(txtIn)) {
            String txtOut = inverString(txtIn, "");
            System.out.println(txtOut);
            txtIn = sc.nextLine();
        }
        sc.close();
    }
}