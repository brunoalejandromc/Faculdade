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

void inverString(char Str[], char Str1[]){
    int tam = getTam(Str);
    for (int i = 0; i < tam; i++){
        Str1[i] = Str[tam - 1 - i];
    }
    Str1[tam] = '\0';
}

int main (){
    char Str[100];
    char Str1[100];

    while (fgets(Str, 100, stdin) != NULL && !Parada(Str)){
        inverString(Str, Str1);
        printf("%s\n", Str1);
    }
    return 0;
}