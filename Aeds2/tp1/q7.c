#include <stdio.h>
#include <stdbool.h>

#define MAX 256

int getTam(char Str[]){
    int tam = 0;
    while(Str[tam] != '\0' && Str[tam] != '\n'){
        tam++;
    }
    return tam;
}

bool Parada(char Str[]){
    int tam = getTam(Str);

    return (tam == 3 &&
            Str[0] == 'F' &&
            Str[1] == 'I' &&
            Str[2] == 'M');
}

int getGreatSubString(char str[]){
    int ultimo[MAX];

    for(int i = 0; i < MAX; i++){
        ultimo[i] = -1;
    }

    int inicio = 0;
    int max = 0;

    for(int i = 0; str[i] != '\0' && str[i] != '\n'; i++){
        char c = str[i];

        if(ultimo[(int)c] >= inicio){
            inicio = ultimo[(int)c] + 1;
        }

        ultimo[(int)c] = i;

        int tamanhoAtual = i - inicio + 1;
        if(tamanhoAtual > max){
            max = tamanhoAtual;
        }
    }

    return max;
}

int main (){
    char Str[100];

    while (fgets(Str, 100, stdin) != NULL && !Parada(Str)){
        int resp = getGreatSubString(Str);
        printf("%d\n", resp);
    }
    return 0;
}