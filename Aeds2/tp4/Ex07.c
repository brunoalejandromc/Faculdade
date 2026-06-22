#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#define MAX 1000

typedef struct{
    int dia,mes,ano;
}Data;

typedef struct{
    int hora,minuto;
}Hora;

typedef struct{
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
}Restaurante;

typedef struct{
    Restaurante restaurantes[MAX];
    int tamanho;
}Colecao;

typedef struct Celula{
    Restaurante *rest;
    struct Celula *prox;
}Celula;

typedef struct No{
    char letra;
    Celula *lista;
    struct No *esq;
    struct No *dir;
}No;

Data parse_data(char *s){
    Data d;
    sscanf(s,"%d-%d-%d",&d.ano,&d.mes,&d.dia);
    return d;
}

Hora parse_hora(char *s){
    Hora h;
    sscanf(s,"%d:%d",&h.hora,&h.minuto);
    return h;
}

Restaurante parse_restaurante(char *linha){
    Restaurante r;
    char temp[500];
    strcpy(temp,linha);

    char *partes[10];
    int n=0;
    char *p=temp;

    while(n<10){
        partes[n++]=p;
        int in_bracket=0;
        char *q=p;
        char *comma=NULL;
        while(*q){
            if(*q=='[') in_bracket=1;
            else if(*q==']') in_bracket=0;
            else if(*q==',' && !in_bracket){ comma=q; break; }
            q++;
        }
        if(!comma) break;
        *comma='\0';
        p=comma+1;
        while(*p==' ') p++;
    }

    r.id=atoi(partes[0]);
    strcpy(r.nome,partes[1]);
    strcpy(r.cidade,partes[2]);
    r.capacidade=atoi(partes[3]);
    r.avaliacao=atof(partes[4]);
    strcpy(r.tipos,partes[5]);
    strcpy(r.preco,partes[6]);

    char h1[10],h2[10];
    sscanf(partes[7],"%[^-]-%s",h1,h2);
    r.abertura=parse_hora(h1);
    r.fechamento=parse_hora(h2);

    r.data=parse_data(partes[8]);

    char *aberto_str=partes[9];
    while(*aberto_str==' ') aberto_str++;
    r.aberto=strncmp(aberto_str,"true",4)==0;

    return r;
}

void carregar_csv(Colecao *c,char *path){
    FILE *f=fopen(path,"r");
    c->tamanho=0;
    char linha[500];
    fgets(linha,sizeof(linha),f);
    while(fgets(linha,sizeof(linha),f)){
        linha[strcspn(linha,"\n")]=0;
        if(strlen(linha)>0){
            c->restaurantes[c->tamanho++]=parse_restaurante(linha);
        }
    }
    fclose(f);
}

No* novoNo(char letra){
    No* novo=(No*) malloc(sizeof(No));
    novo->letra=letra;
    novo->lista=NULL;
    novo->esq=NULL;
    novo->dir=NULL;
    return novo;
}



void inserirLista(Celula **lista,Restaurante *r){
    Celula *nova=(Celula*) malloc(sizeof(Celula));
    nova->rest=r;
    nova->prox=NULL;
    if(*lista==NULL || strcmp(r->nome,(*lista)->rest->nome)<0){
        nova->prox=*lista;
        *lista=nova;
        return;
    }
    Celula *i=*lista;
    while(i->prox!=NULL && strcmp(i->prox->rest->nome,r->nome)<0){
        i=i->prox;
    }
    nova->prox=i->prox;
    i->prox=nova;
}



long comp=0;

Restaurante* pesquisarLista(Celula *lista,char nome[]){
    while(lista != NULL){

        int cmp =
        strcmp(lista->rest->nome,nome);

        if(cmp > 0){
            break;
        }

        printf("%s ",lista->rest->nome);

        if(cmp == 0){
            return lista->rest;
        }

        lista = lista->prox;
    }
    return NULL;
}

Restaurante* pesquisar(No *raiz,char nome[]){

    int pos=0;
    while(nome[pos]==' ') pos++;

    char letra = nome[pos];

    while(raiz != NULL){

        if(letra == raiz->letra){

            return pesquisarLista(
                raiz->lista,
                nome
            );

        }else if(letra < raiz->letra){

            printf("ESQ ");
            raiz = raiz->esq;

        }else{

            printf("DIR ");
            raiz = raiz->dir;
        }
    }

    return NULL;
}

void liberarLista(Celula *i){
    while(i!=NULL){
        Celula *tmp=i;
        i=i->prox;
        free(tmp);
    }
}

void liberar(No *i){
    if(i!=NULL){
        liberar(i->esq);
        liberar(i->dir);
        liberarLista(i->lista);
        free(i);
    }
}

No* inserirArvore(No* raiz, Restaurante* r){

    char letra = r->nome[0];

    if(letra >= 'a' && letra <= 'z')
        letra -= 32;

    if(raiz == NULL){

        raiz = (No*) malloc(sizeof(No));

        raiz->letra = letra;
        raiz->lista = NULL;
        raiz->esq = NULL;
        raiz->dir = NULL;

        inserirLista(&(raiz->lista), r);
    }
    else if(letra < raiz->letra){

        raiz->esq =
        inserirArvore(raiz->esq, r);

    }
    else if(letra > raiz->letra){

        raiz->dir =
        inserirArvore(raiz->dir, r);

    }
    else{

        inserirLista(&(raiz->lista), r);
    }

    return raiz;
}

int main(){
    Colecao c;
    carregar_csv(&c,"/tmp/restaurantes.csv");

    No *raiz = NULL;

    int id;
    while(scanf("%d",&id)==1 && id!=-1){
        for(int i=0;i<c.tamanho;i++){
            if(c.restaurantes[i].id==id){
               raiz =inserirArvore(raiz,&c.restaurantes[i]);
                break;
            }
        }
    }
    char lixo[200];
    fgets(lixo, sizeof(lixo), stdin);

    char nome[100];
    clock_t inicio=clock();

    while(fgets(nome,sizeof(nome),stdin)){
        nome[strcspn(nome,"\r\n")]='\0';
        if(strcmp(nome,"FIM")==0) break;
        printf("RAIZ ");
        Restaurante *resp=pesquisar(raiz,nome);
        if(resp!=NULL){
            printf("SIM [%d ## %s ## %s ## %d ## %.1f ## %s ## %s ## %02d:%02d-%02d:%02d ## %02d/%02d/%04d ## %s]\n",
                resp->id,resp->nome,resp->cidade,resp->capacidade,resp->avaliacao,
                resp->tipos,resp->preco,
                resp->abertura.hora,resp->abertura.minuto,
                resp->fechamento.hora,resp->fechamento.minuto,
                resp->data.dia,resp->data.mes,resp->data.ano,
                resp->aberto ? "true" : "false");
        }else{
            printf("NAO\n");
        }
    }

    clock_t fim=clock();
    double tempo=(double)(fim-inicio)/CLOCKS_PER_SEC;
    FILE *log=fopen("832656_hibrida_arvore_lista.txt","w");
    fprintf(log,"832656\t%ld\t%.6lf",comp,tempo);
    fclose(log);
    liberar(raiz);
    return 0;
}