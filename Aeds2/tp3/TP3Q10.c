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




typedef struct Celula{
    Restaurante elemento;
    struct Celula *prox;
}Celula;

typedef struct{
    Celula *primeiro;
    Celula *ultimo;
}Lista;

Celula* novaCelula(Restaurante x){

    Celula *nova=(Celula*)malloc(sizeof(Celula));
    
    nova->elemento=x;
    nova->prox=NULL;
    
    return nova;
}

void iniciarLista(Lista *l){
    
    l->primeiro=novaCelula((Restaurante){0});
    l->ultimo=l->primeiro;
}

void inserirFim(Lista *l,Restaurante x){
    
    l->ultimo->prox=novaCelula(x);
    l->ultimo=l->ultimo->prox;
}



void SelectionSort(Lista *l,int *comp,int *mov){

    for(Celula *i=l->primeiro->prox;i!=NULL;i=i->prox){

        Celula *menor=i;

        for(Celula *j=i->prox;j!=NULL;j=j->prox){

            (*comp)++;

            if(strcmp(j->elemento.nome,
                      menor->elemento.nome)<0){

                menor=j;
            }
        }

        if(menor!=i){

            Restaurante temp=
            i->elemento;

            i->elemento=
            menor->elemento;

            menor->elemento=
            temp;

            (*mov)+=3;
        }
    }
}
int main(){

    Colecao c;

    carregar_csv(&c, "/tmp/restaurantes.csv");

    Lista lista;

    iniciarLista(&lista);

    int comp=0,mov=0;

    int id;

    while(scanf("%d",&id)==1 && id!=-1){

        for(int i=0;i<c.tamanho;i++){

            if(c.restaurantes[i].id==id){

                inserirFim(
                &lista,
                c.restaurantes[i]);

                break;
            }
        }
    }

    clock_t inicio= clock();

    SelectionSort(&lista, &comp,&mov);

    clock_t fim = clock();

    double tempo=(double)(fim-inicio)/CLOCKS_PER_SEC;

    char buffer[500];

    for(Celula *i=
        lista.primeiro->prox;
        i!=NULL;
        i=i->prox){

        formatar_restaurante(
        i->elemento,
        buffer);

        printf("[%s]\n",
        buffer);
    }

    FILE *log = fopen("869882_selecao_flexivel.txt","w");

    fprintf(log,"869882\t%d\t%d\t%lf",comp,mov,tempo);

    fclose(log);

    return 0;
}