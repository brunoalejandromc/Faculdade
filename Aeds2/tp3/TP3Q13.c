#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#define MAX 1000


typedef struct {
    int dia, mes, ano;
} Data;

Data parse_data(char *s) {
    Data d;
    sscanf(s, "%d-%d-%d", &d.ano, &d.mes, &d.dia);
    return d;
}

void formatar_data(Data d, char *buffer) {
    sprintf(buffer, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}


typedef struct {
    int hora, minuto;
} Hora;

Hora parse_hora(char *s) {
    Hora h;
    sscanf(s, "%d:%d", &h.hora, &h.minuto);
    return h;
}

void formatar_hora(Hora h, char *buffer) {
    sprintf(buffer, "%02d:%02d", h.hora, h.minuto);
}


typedef struct {
    int id;
    char nome[100];
    char cidade[100];
    int capacidade;
    double avaliacao;
    char tipos[100];
    char preco[10];
    Hora abertura;
    Hora fechamento;
    Data data;
    int aberto;
} Restaurante;

Restaurante parse_restaurante(char *linha) {
    Restaurante r;

    char temp[500];
    strcpy(temp, linha);

    char *partes[10];
    char *token = strtok(temp, ",");
    int i = 0;

    while (token != NULL && i < 10) {
        while (*token == ' ') token++;
        partes[i++] = token;
        token = strtok(NULL, ",");
    }

    r.id = atoi(partes[0]);
    strcpy(r.nome, partes[1]);
    strcpy(r.cidade, partes[2]);
    r.capacidade = atoi(partes[3]);
    r.avaliacao = atof(partes[4]);

    strcpy(r.tipos, partes[5]);
    strcpy(r.preco, partes[6]);

    char h1[10], h2[10];
    sscanf(partes[7], "%[^-]-%s", h1, h2);

    r.abertura = parse_hora(h1);
    r.fechamento = parse_hora(h2);

    r.data = parse_data(partes[8]);

    r.aberto = strcmp(partes[9], "true") == 0;

    return r;
}

void formatar_restaurante(Restaurante r, char *buffer) {
    char data[20], h1[10], h2[10];

    formatar_data(r.data, data);
    formatar_hora(r.abertura, h1);
    formatar_hora(r.fechamento, h2);

    sprintf(buffer,
        "%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s",
        r.id, r.nome, r.cidade, r.capacidade, r.avaliacao,
        r.tipos, r.preco, h1, h2, data,
        r.aberto ? "true" : "false");
}


typedef struct {
    Restaurante restaurantes[MAX];
    int tamanho;
} Colecao;

void carregar_csv(Colecao *c, char *path) {
    FILE *f = fopen(path, "r");

    c->tamanho = 0;

    char linha[500];
    fgets(linha, sizeof(linha), f);
    while (fgets(linha, sizeof(linha), f)) {
        linha[strcspn(linha, "\n")] = 0;

        if (strlen(linha) == 0) continue;

        c->restaurantes[c->tamanho++] = parse_restaurante(linha);
    }

    fclose(f);
}




typedef struct No{

    Restaurante elemento;

    struct No *esq;
    struct No *dir;

}No;

No* novoNo(Restaurante x){

    No *novo=
    (No*)malloc(sizeof(No));

    novo->elemento=x;

    novo->esq=NULL;
    novo->dir=NULL;

    return novo;
}

long comp=0;


No* inserir(No *i,
            Restaurante x){

    if(i==NULL){

        i=novoNo(x);
    }

    else if(
    strcmp(
    x.nome,
    i->elemento.nome)<0){

        i->esq=
        inserir(
        i->esq,
        x);
    }

    else if(
    strcmp(
    x.nome,
    i->elemento.nome)>0){

        i->dir=
        inserir(
        i->dir,
        x);
    }

    return i;
}

int pesquisar(No *i, char nome[]){

    int resp;

    comp++;

    if(i==NULL){

        resp=0;
    }

    else if(
    strcmp(
    nome,
    i->elemento.nome)==0){

        resp=1;
    }

    else if(
    strcmp(
    nome,
    i->elemento.nome)<0){

        printf("esq ");

        resp=pesquisar(i->esq,nome);
    }

    else{

        printf("dir ");

        resp=pesquisar(i->dir,nome);
    }

    return resp;
}


void caminharCentral(No *i){

    char buffer[500];

    if(i!=NULL){

        caminharCentral(i->esq);

        formatar_restaurante(i->elemento,buffer);

        printf("[%s]\n",buffer);

        caminharCentral(i->dir);
    }
}


int main(){

    Colecao c;

    carregar_csv(&c,"/tmp/restaurantes.csv");

    // cria mapa id -> restaurante
    Restaurante mapa[20000];
    int existe[20000]={0};

    for(int i=0;i<c.tamanho;i++){

        int idAtual=
        c.restaurantes[i].id;

        if(idAtual<20000){

            mapa[idAtual]=
            c.restaurantes[i];

            existe[idAtual]=1;
        }
    }

    No *raiz=NULL;

    clock_t inicio=clock();

    int id;

    // leitura dos ids
    while(scanf("%d",&id)==1){

        if(id==-1)
            break;

        if(id<20000 &&
           existe[id]){

            raiz=inserir(raiz,mapa[id]);
        }
    }

    getchar();

    char nome[100];

    while(1){

        fgets(nome,sizeof(nome),stdin);

        nome[strcspn(nome,"\n")]=0;

        if(strcmp(nome,"FIM")==0)
            break;

        printf( "raiz ");

        int achou=pesquisar(raiz, nome);

        printf("%s\n",achou? "SIM":"NAO");
    }

    caminharCentral(raiz);

    clock_t fim= clock();

    double tempo=(double)(fim-inicio)/CLOCKS_PER_SEC;

    FILE *log=fopen("869882_arvore_binaria.txt","w");

    fprintf(log,"869882\t%ld\t%lf",comp,tempo);

    fclose(log);

    return 0;
}