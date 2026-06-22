#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdbool.h>
#include <time.h>

#define MAX_TIPOS_COZINHA 20
#define MAX_STR 256


typedef struct { int ano, mes, dia; } Data;
typedef struct { int hora, minuto; } Hora;

typedef struct {
    int id;
    char nome[MAX_STR];
    char city[MAX_STR];
    int capacity;
    double rating;
    int n_tipos_cozinha;
    char tipos_cozinha[MAX_TIPOS_COZINHA][MAX_STR];
    int price_range;
    Hora h_open;
    Hora h_close;
    Data d_open;
    bool is_open;
} Restaurante;




typedef struct {
    int size;
    Restaurante** items;
} Colecao;

static long long comps = 0;

Data parse_data(char* s) {
    Data d;
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

Hora parse_hora(char* s) {
    Hora h;
    sscanf(s, "%d:%d", &h.hora, &h.minuto);
    return h;
}

static char* get_field(char* src, char* dest) {
    int i = 0;
    while (*src != '\0' && *src != ',' && *src != '\n' && *src != '\r') dest[i++] = *src++;
    dest[i] = '\0';
    if (*src == ',') src++;
    return src;
}

Restaurante* parse_restaurante(char* s) {
    Restaurante* r = (Restaurante*) malloc(sizeof(Restaurante));
    char buffer[MAX_STR * 2], *ptr = s;
    ptr = get_field(ptr, buffer); r->id = atoi(buffer);
    ptr = get_field(ptr, r->nome);
    ptr = get_field(ptr, r->city);
    ptr = get_field(ptr, buffer); r->capacity = atoi(buffer);
    ptr = get_field(ptr, buffer); r->rating = atof(buffer);
    ptr = get_field(ptr, buffer);
    r->n_tipos_cozinha = 0;
    char* copy = strdup(buffer);
    char* tok = strtok(copy, ";");
    while (tok && r->n_tipos_cozinha < MAX_TIPOS_COZINHA) {
        strcpy(r->tipos_cozinha[r->n_tipos_cozinha++], tok);
        tok = strtok(NULL, ";");
    }
    free(copy);
    ptr = get_field(ptr, buffer); r->price_range = (int)strlen(buffer);
    ptr = get_field(ptr, buffer);
    char ha[10], hf[10];
    sscanf(buffer, "%[^-]-%s", ha, hf);
    r->h_open = parse_hora(ha);
    r->h_close = parse_hora(hf);
    ptr = get_field(ptr, buffer); r->d_open = parse_data(buffer);
    ptr = get_field(ptr, buffer); r->is_open = (strcmp(buffer, "true") == 0);
    return r;
}

void formatar_restaurante(Restaurante* r, char* b) {
    char fp[10] = "";
    for (int i = 0; i < r->price_range; i++) strcat(fp, "$");
    char tc[MAX_STR * MAX_TIPOS_COZINHA] = "[";
    for (int i = 0; i < r->n_tipos_cozinha; i++) {
        if (i > 0) strcat(tc, ",");
        strcat(tc, r->tipos_cozinha[i]);
    }
    strcat(tc, "]");
    char h1[10], h2[10], d1[15];
    sprintf(h1, "%02d:%02d", r->h_open.hora, r->h_open.minuto);
    sprintf(h2, "%02d:%02d", r->h_close.hora, r->h_close.minuto);
    sprintf(d1, "%02d/%02d/%04d", r->d_open.dia, r->d_open.mes, r->d_open.ano);
    sprintf(b, "[%d ## %s ## %s ## %d ## %.1f ## %s ## %s ## %s-%s ## %s ## %s]",
            r->id, r->nome, r->city, r->capacity, r->rating,
            tc, fp, h1, h2, d1, r->is_open ? "true" : "false");
}


typedef struct NoTrie NoTrie;

typedef struct CelulaTrie{
    char letra;
    NoTrie *filho;
    struct CelulaTrie *prox;
}CelulaTrie;

struct NoTrie{
    Restaurante *rest;
    CelulaTrie *primeiro;
};

NoTrie* novoNoTrie(){

    NoTrie *novo = (NoTrie*) malloc(sizeof(NoTrie));

    novo->rest = NULL;
    novo->primeiro = NULL;

    return novo;
}

CelulaTrie* buscarFilho(NoTrie *no, char letra){

    CelulaTrie *i = no->primeiro;

    while(i != NULL){

        comps++;

        if(i->letra == letra){
            return i;
        }

        i = i->prox;
    }

    return NULL;
}

CelulaTrie* inserirFilho(
    NoTrie *no,
    char letra
){

    CelulaTrie *resp =
    buscarFilho(no, letra);

    if(resp == NULL){

        resp =
        (CelulaTrie*) malloc(
        sizeof(CelulaTrie));

        resp->letra = letra;
        resp->filho = novoNoTrie();

        resp->prox =
        no->primeiro;

        no->primeiro =
        resp;
    }

    return resp;
}


void inserirTrie(NoTrie *raiz,Restaurante *r){

   NoTrie *atual = raiz;

    for(int i=0;
        r->nome[i] != '\0';
        i++){

        CelulaTrie *c =inserirFilho(atual,r->nome[i]);

        atual = c->filho;
    }

    atual->rest = r;
}

Restaurante* pesquisarTrie(NoTrie *raiz, char nome[]){

    NoTrie *atual = raiz;

    for(int i = 0; nome[i] != '\0'; i++){

        CelulaTrie *c = buscarFilho(atual, nome[i]);

        if(c == NULL){
            return NULL;
        }

        printf("%c ", nome[i]);

        atual = c->filho;
    }

    return atual->rest;
}

int main() {
    Colecao* c = (Colecao*)malloc(sizeof(Colecao));
    FILE* f = fopen("/tmp/restaurantes.csv", "r");
    char line[4096];
    fgets(line, sizeof(line), f);
    c->items = (Restaurante**)malloc(2000 * sizeof(Restaurante*));
    c->size = 0;
    while (fgets(line, sizeof(line), f)) {
        int len = strlen(line);
        while (len > 0 && (line[len-1] == '\n' || line[len-1] == '\r')) line[--len] = '\0';
        if (len > 0) c->items[c->size++] = parse_restaurante(line);
    }
    fclose(f);
    NoTrie* raiz = novoNoTrie();
    int id;
    while (scanf("%d", &id) == 1 && id != -1) {
        for (int j = 0; j < c->size; j++) {
            if (c->items[j]->id == id) {
                inserirTrie(raiz,c->items[j]);
                break;
            }
        }
    }
    char query[MAX_STR];
    scanf(" ");

clock_t inicio = clock();

while (fgets(query, sizeof(query), stdin)) {

    int len = strlen(query);

    while (len > 0 &&
          (query[len-1] == '\n' ||
           query[len-1] == '\r')) {

        query[--len] = '\0';
    }

    if (strcmp(query, "FIM") == 0)
        break;

    printf("RAIZ ");

    Restaurante *encontrado =
    pesquisarTrie(raiz, query);

    if (encontrado) {

        char b[4096];

        formatar_restaurante(
            encontrado,
            b
        );

        printf("SIM %s\n", b);

    } else {

        printf("NAO\n");
    }
}

clock_t fim = clock();

double t =(double)(fim - inicio) / CLOCKS_PER_SEC;

    FILE* l = fopen("832656_arvore_trie_lista.txt", "w");
    if (l) { fprintf(l, "832656\t%lld\t%.6f\n", comps, t); fclose(l); }
    return 0;
}