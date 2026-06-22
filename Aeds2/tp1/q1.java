
import java.util.Scanner;

public class q1 {//ciframento de cesar

    static boolean  Parada(String txt){
		if(txt.length()== 3 &&
		txt.charAt(0)=='F' &&
		txt.charAt(1)== 'I' &&
		txt.charAt(2)== 'M'){
			return true;
		}else{
			return false;
		}
	}

    public static String CifraCesar(String txt){
    int key = 3;
    String output = "";

   for(int i = 0; i < txt.length(); i++) {
    char c = txt.charAt(i);
    if(c>= ' '&& c <='~'){
        output += (char)((c - ' ' + key) % 95 + ' ');
    }else {
            output += c;
        }
   }
    return output;
}

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String txtIn = sc.nextLine();

        while (!Parada(txtIn)) {
            System.out.println(CifraCesar(txtIn));
			txtIn = sc.nextLine();
        }
        sc.close();
    }


}
