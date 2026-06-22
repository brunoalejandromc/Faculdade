import java.util.*;

public class q3 {
    private static boolean o;

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

    static boolean isVowel(String txt) {
        if (txt.length() == 0) {
            return false;
        }

        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);

            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                return false;
            }

            if (c != 'a' && c != 'e' && c != 'i' && c != 'o' && c != 'u'
                    && c != 'A' && c != 'E' && c != 'I' && c != 'O' && c != 'U') {
                return false;
            }
        }
        return true;
    }

    static boolean isConsonant(String txt) {
        if (txt.length() == 0) {
            return false;
        }

        for (int i = 0; i < txt.length(); i++) {
            char c = txt.charAt(i);

            if (!((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z'))) {
                return false;
            }

            if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u'
                    || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U') {
                return false;
            }
        }
        return true;
    }

static boolean isInteger(String txt) {
    if (txt == null || txt.length() == 0) return false;

    for (int i = 0; i < txt.length(); i++) {
        char c = txt.charAt(i);

        if (i == 0 && (c == '-' || c == '+')) {
            if (txt.length() == 1) return false;
        } 
        else if (c < '0' || c > '9') {
            return false;
        }
    }
    return true;
}

static boolean isReal(String txt) {
    if (txt == null || txt.length() == 0) return false;

    int split = 0;
    boolean temDigito = false;

    for (int i = 0; i < txt.length(); i++) {
        char c = txt.charAt(i);

        if (i == 0 && (c == '-' || c == '+')) {
            if (txt.length() == 1) return false;
        } 
        else if (c == '.' || c == ',') {
            split++;
            if (split > 1 || i == 0 || (i == 1 && (txt.charAt(0) == '-' || txt.charAt(0) == '+'))) {
                return false;
            }
        } 
        else if (c >= '0' && c <= '9') {
            temDigito = true;
        } 
        else {
            return false;
        }
    }

    return split == 1 && temDigito;
}
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String txtIn = sc.nextLine();
        while (!Parada(txtIn)) {
            String x1;
            if (isVowel(txtIn)) {
                x1 = "SIM";
            } else {
                x1 = "NAO";
            }

            String x2;
            if (isConsonant(txtIn)) {
                x2 = "SIM";
            } else {
                x2 = "NAO";
            }

            String x3;
            if (isInteger(txtIn)) {
                x3 = "SIM";
            } else {
                x3 = "NAO";
            }

            String x4;
            if (isReal(txtIn)) {
                x4 = "SIM";
            } else {
                x4 = "NAO";
            }
            System.out.println(x1 + " " + x2 + " " + x3 + " " + x4);

            txtIn = sc.nextLine();
        }
        sc.close();
    }
}
