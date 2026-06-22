import java.util.*;

public class q2{
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
	static String randAlt(String txt, Random gen){
		String ans = "";
		
		char a1=(char)('a'+(Math.abs(gen.nextInt())%26));
		char a2=(char)('a'+(Math.abs(gen.nextInt())%26));
		
		for(int i=0; i < txt.length(); i++){
			if(txt.charAt(i)==a1){
				ans+= a2;
			}else{
				ans+=txt.charAt(i);
			}
		}
		return ans;
	}

	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		String txt = sc.nextLine();
		boolean stop =false;	
	
		Random gen = new Random(); 
		// random foi feito por uma IA, porque eu nao estava conseguindo fazer funcionar, continuo sem entender do pq o meu nao funcionou		gen.setSeed(4);
		while(!Parada(txt)){
			System.out.println(randAlt(txt,gen));
			txt = sc.nextLine();
		}
	sc.close();
	}	
}
