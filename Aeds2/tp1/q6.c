#include <stdio.h>
#include <stdbool.h>

int getTam(char Str[]){
    int tam = 0;
    while(Str[tam] != '\0' && Str[tam] != '\n'){
        tam++;
    }
    return tam;
}

bool Parada(char Str[]){
    int tam = getTam(Str);
    return (tam == 3 && Str[0] == 'F' && Str[1] == 'I' && Str[2] == 'M');
}



bool isAnagrama(char Str[], char Str2[]){
    int tam1 = getTam(Str);
    int tam2 = getTam(Str2);
    int aux=tam1;
    if(tam1 != tam2){
        return false;
	}
	for(int i =0; i<tam1;i++){
		bool find =false;
		int pos = -1;
		for(int j=0; j <tam2; j++){
			if((Str[i]==Str2[j])&& pos== -1){
				find=true;
				pos=j;
			}
		}
		if(!find){
			Str2[pos]='#';
		}
	return true;
	}
}

int main (){
    	char Str[100];
    	char Str2[100];
    	bool resp=false;

	while(scanf("%s - %s", Str, Str2) != EOF && !Parada(Str)){
        resp = isAnagrama(Str, Str2);
        if(resp==true){
            printf("SIM\n");
        }else{
            printf("NAO\n");
        }
    }
    return 0;
}
