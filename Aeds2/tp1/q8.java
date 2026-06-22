import java.util.*;

public class q8 {
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

   public static boolean isPassword(String txt) {
    boolean UpCase=false;
    boolean LowCase=false;
    boolean Num=false;
    boolean special=false;
    boolean out=false;
        if(txt.length() < 8){
            return false;
        }else {
            for(int i=0;i<txt.length();i++){
                char c = txt.charAt(i);
                    if(c >= 'A' && c <= 'Z'){
                        UpCase=true;
                    }
                    
                    if(c >= 'a' && c <= 'z'){
                        LowCase=true;
                    }
                    if(c >= '0' && c <= '9'){
                        Num=true;
                    }
                    if((c>= '!'&& c <= '/')||(c >=':' && c<='@')||(c >= '[' && c <= '`')||(c >= '{' && c<= '~') ){
                        special=true;
                    }
                }
        }
        if(UpCase==true && LowCase==true && Num==true && special==true){
            return out=true;
        }else{
            return out=false;
            }   
        }  

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String txtIn = sc.nextLine();
        boolean resp=false;
        while (!Parada(txtIn)){
            resp = isPassword(txtIn);
            if(resp==true){
                System.out.println("SIM");
            }else{
                System.out.println("NAO");
            }
            txtIn = sc.nextLine();
        }
        sc.close();
    }
}


