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

    return (tam == 3 &&
            Str[0] == 'F' &&
            Str[1] == 'I' &&
            Str[2] == 'M');
}

int getSoma(char Str[], int sum){
    int tam = getTam(Str);
    sum = 0;
    for (int i = 0; i < tam; i++){
        if (Str[i] >= '0' && Str[i] <= '9'){
            sum += Str[i] - '0';
        }
    }
    return sum;
}

int main (){
    char Str[100];
    int s = 0;

    while (fgets(Str, 100, stdin) != NULL && !Parada(Str)){
        s = getSoma(Str, s);
        printf("%d\n", s);
    }

    return 0;
}