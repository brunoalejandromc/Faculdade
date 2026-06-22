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

void cifraCesar(char Str[], int i, char out[]) {

    if(Str[i] == '\0'){
        out[i] = '\0';
        return;
    }

    int key = 3;
    char c = Str[i];

    if(c >= ' ' && c <= '~'){
        out[i] = (char)((c - ' ' + key) % 95 + ' ');
    } else {
        out[i] = c;
    }

    cifraCesar(Str, i + 1, out);
}

int main (){
    char Str[100];

    while (fgets(Str, 100, stdin) != NULL && !Parada(Str)){
        
        int tam = getTam(Str);
        Str[tam] = '\0';

        char txtOut[100];

        cifraCesar(Str, 0, txtOut);

        printf("%s\n", txtOut);
    }
    return 0;
}