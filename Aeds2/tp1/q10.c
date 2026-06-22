#include <stdio.h>
#include <stdbool.h>

int getTam(char Str[]) {
    int tam = 0;
    while (Str[tam] != '\0' && Str[tam] != '\n') {
        tam++;
    }
    return tam;
}

bool Parada(char Str[]) {
    int tam = getTam(Str);

    return (tam == 3 &&
            Str[0] == 'F' &&
            Str[1] == 'I' &&
            Str[2] == 'M');
}

bool isLetra(char c) {
    return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
}

bool isVogal(char c) {
    return (c=='a'||c=='e'||c=='i'||c=='o'||c=='u'||
            c=='A'||c=='E'||c=='I'||c=='O'||c=='U');
}

bool isVowelRec(char Str[], int i) {
    int tam = getTam(Str);

    if (tam == 0) return false;
    if (i == tam) return true;

    char c = Str[i];

    if (!isLetra(c) || !isVogal(c)) return false;

    return isVowelRec(Str, i + 1);
}

bool isConsonantRec(char Str[], int i) {
    int tam = getTam(Str);

    if (tam == 0) return false;
    if (i == tam) return true;

    char c = Str[i];

    if (!isLetra(c) || isVogal(c)) return false;

    return isConsonantRec(Str, i + 1);
}

bool isIntegerRec(char Str[], int i) {
    int tam = getTam(Str);

    if (tam == 0) return false;
    if (i == tam) return true;

    char c = Str[i];

    if (i == 0 && (c == '-' || c == '+')) {
        if (tam == 1) return false;
        return isIntegerRec(Str, i + 1);
    }

    if (c < '0' || c > '9') return false;

    return isIntegerRec(Str, i + 1);
}

bool isRealRec(char Str[], int i, int split, bool temDigito) {
    int tam = getTam(Str);

    if (tam == 0) return false;

    if (i == tam) {
        return (split == 1 && temDigito);
    }

    char c = Str[i];

    if (i == 0 && (c == '-' || c == '+')) {
        if (tam == 1) return false;
        return isRealRec(Str, i + 1, split, temDigito);
    }

    if (c == '.' || c == ',') {
        if (split == 1) return false;
        if (i == 0) return false;
        if (i == 1 && (Str[0] == '-' || Str[0] == '+')) return false;

        return isRealRec(Str, i + 1, split + 1, temDigito);
    }

    if (c >= '0' && c <= '9') {
        return isRealRec(Str, i + 1, split, true);
    }

    return false;
}

int main() {
    char Str[100];

    while (fgets(Str, 100, stdin) != NULL && !Parada(Str)) {

        bool resp;

        // VOGAL
        resp = isVowelRec(Str, 0);
        if (resp == true) {
            printf("SIM ");
        } else {
            printf("NAO ");
        }

        // CONSOANTE
        resp = isConsonantRec(Str, 0);
        if (resp == true) {
            printf("SIM ");
        } else {
            printf("NAO ");
        }

        // INTEIRO
        resp = isIntegerRec(Str, 0);
        if (resp == true) {
            printf("SIM ");
        } else {
            printf("NAO ");
        }

        // REAL
        resp = isRealRec(Str, 0, 0, false);
        if (resp == true) {
            printf("SIM\n");
        } else {
            printf("NAO\n");
        }
    }

    return 0;
}