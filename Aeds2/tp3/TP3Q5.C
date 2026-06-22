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

    partes[9][strcspn(partes[9], "\r\n")] = '\0';
    r.aberto = (strcmp(partes[9], "true") == 0);

    return r;
}

void formatar_restaurante(Restaurante r, char *buffer) {
    char data[20], h1[10], h2[10];

    formatar_data(r.data, data);
    formatar_hora(r.abertura, h1);
    formatar_hora(r.fechamento, h2);

    sprintf(buffer,"[%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s]",
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


Restaurante buscar(int id,Colecao *c){

    for(int i=0;i<c->tamanho;i++){

        if(c->restaurantes[i].id==id){

            return c->restaurantes[i];
        }
    }

    Restaurante r={0};
    r.id=-1;

    return r;
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

void inserirInicio(Lista *l,Restaurante x){

    Celula *tmp=novaCelula(x);

    tmp->prox=l->primeiro->prox;
    l->primeiro->prox=tmp;

    if(l->primeiro==l->ultimo)
        l->ultimo=tmp;
}

void inserirFim(Lista *l,Restaurante x){

    l->ultimo->prox=novaCelula(x);

    l->ultimo=l->ultimo->prox;
}

void inserir(Lista *l,Restaurante x,int pos){

    Celula *i=l->primeiro;

    for(int j=0;j<pos;j++,i=i->prox);

    Celula *tmp=novaCelula(x);

    tmp->prox=i->prox;
    i->prox=tmp;

    if(i==l->ultimo)
        l->ultimo=tmp;
}

Restaurante removerInicio(Lista *l){

    if(l->primeiro==l->ultimo){

        Restaurante r={0};
        r.id=-1;

        return r;
    }

    Celula *tmp=l->primeiro->prox;

    Restaurante resp=tmp->elemento;

    l->primeiro->prox=tmp->prox;

    if(tmp==l->ultimo){
        l->ultimo=l->primeiro;
    }

    free(tmp);

    return resp;
}

Restaurante removerFim(Lista *l){

    Celula *i=l->primeiro;

    if(l->primeiro==l->ultimo){

        Restaurante r={0};
        r.id=-1;

        return r;
    }

    while(i->prox!=l->ultimo){
        i=i->prox;
    }

    Restaurante resp=l->ultimo->elemento;

    Celula *tmp=l->ultimo;

    l->ultimo=i;

    l->ultimo->prox=NULL;

    free(tmp);

    return resp;
}

Restaurante remover(Lista *l,int pos){

    Celula *i=l->primeiro;

    for(int j=0;j<pos;j++){
        i=i->prox;
    }

    Celula *tmp=i->prox;

    Restaurante resp=tmp->elemento;

    i->prox=tmp->prox;

    if(tmp==l->ultimo){
        l->ultimo=i;
    }

    free(tmp);

    return resp;
}

int main(){

    Colecao c;

    carregar_csv(&c,"/tmp/restaurantes.csv");

    Lista lista;

    iniciarLista(&lista);

    int id;

    while(scanf("%d",&id)==1 && id!=-1){

        Restaurante r=buscar(id,&c);

        if(r.id!=-1){
            inserirFim(&lista,r);
        }
    }

    int q;

    scanf("%d",&q);

    for(int i=0;i<q;i++){

        char op[5];

        scanf("%s",op);

        if(strcmp(op,"II")==0){

            scanf("%d",&id);

            Restaurante r=buscar(id,&c);

            if(r.id!=-1)
                inserirInicio(&lista,r);
        }

        else if(strcmp(op,"IF")==0){

            scanf("%d",&id);

            Restaurante r=buscar(id,&c);

            if(r.id!=-1)
                inserirFim(&lista,r);
        }

        else if(strcmp(op,"I*")==0){

            int pos;

            scanf("%d %d",&pos,&id);

            Restaurante r=buscar(id,&c);

            if(r.id!=-1)
                inserir(&lista,r,pos);
        }

        else if(strcmp(op,"RI")==0){

            Restaurante r=removerInicio(&lista);

            if(r.id!=-1)
                printf("(R)%s\n",r.nome);
        }

        else if(strcmp(op,"RF")==0){

            Restaurante r=removerFim(&lista);

            if(r.id!=-1)
                printf("(R)%s\n",r.nome);
        }

        else if(strcmp(op,"R*")==0){

            int pos;

            scanf("%d",&pos);

            Restaurante r=remover(&lista,pos);

            if(r.id!=-1)
                printf("(R)%s\n",r.nome);
        }
    }

    char buffer[500];

    for(Celula *i=lista.primeiro->prox;i!=NULL;i=i->prox){

        formatar_restaurante(i->elemento,buffer);

        printf("%s\n",buffer);
    }

    return 0;
}