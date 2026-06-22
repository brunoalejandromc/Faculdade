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

    sprintf(buffer,
        "[%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s]",
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


Restaurante buscar(int id, Colecao *c) {
    for (int i = 0; i < c->tamanho; i++) {
        if (c->restaurantes[i].id == id) {
            return c->restaurantes[i];
        }
    }
}

void swap(Restaurante *a, Restaurante *b,int *mov){

    Restaurante temp=*a;
    *a=*b;
    *b=temp;

    (*mov)+=3;
}

int comparar(Restaurante a, Restaurante b, int *comp){

    (*comp)++;

    if(a.data.ano != b.data.ano)
        return a.data.ano - b.data.ano;

    if(a.data.mes != b.data.mes)
        return a.data.mes - b.data.mes;

    if(a.data.dia != b.data.dia)
        return a.data.dia - b.data.dia;

    return strcmp(a.nome,b.nome);
}

void reconstruir(Restaurante array[],int tamHeap,int i,int *comp,int *mov){

    int maior=i;

    int esq=2*i+1;
    int dir=2*i+2;

    if(esq<tamHeap &&
       comparar(array[esq],array[maior],comp)>0){

        maior=esq;
    }

    if(dir<tamHeap &&
       comparar(array[dir],array[maior],comp)>0){

        maior=dir;
    }

    if(maior!=i){

        swap(
            &array[i],
            &array[maior],
            mov
        );

        reconstruir(
            array,
            tamHeap,
            maior,
            comp,
            mov
        );
    }
}

void heapSortParcial(Restaurante array[],int n,int k,int *comp,int *mov){

    if(k>n){
        k=n;
    }

    for(int i=(k/2)-1;i>=0;i--){
        reconstruir(array,k,i,comp,mov);
    }

    for(int i=k;i<n;i++){

        if(comparar(array[i],array[0],comp)<0){

            swap(
                &array[i],
                &array[0],
                mov
            );

            reconstruir(
                array,
                k,
                0,
                comp,
                mov
            );
        }
    }

    for(int i=k-1;i>0;i--){

        swap(
            &array[0],
            &array[i],
            mov
        );

        reconstruir(
            array,
            i,
            0,
            comp,
            mov
        );
    }
}



int main(){

    Colecao c;
    carregar_csv(&c,"/tmp/restaurantes.csv");

    Restaurante selecionados[MAX];
    int tamSel=0;

    int id;

    while(scanf("%d",&id)==1 && id!=-1){

        for(int i=0;i<c.tamanho;i++){

            if(c.restaurantes[i].id==id){

                selecionados[tamSel++]=
                c.restaurantes[i];

                break;
            }
        }
    }

    int comp=0;
    int mov=0;

    clock_t inicio=clock();

heapSortParcial(selecionados, tamSel, 10, &comp, &mov);

    clock_t fim=clock();

    double tempo=
    (double)(fim-inicio)/
    CLOCKS_PER_SEC;

    char buffer[500];

    for(int i=0;i<tamSel;i++){

        formatar_restaurante(
        selecionados[i],
        buffer
        );

        printf("%s\n",buffer);
    }

    FILE *log=
    fopen("869882_heapsort_parcial.txt","w");

    fprintf(log,"869882\t%d\t%d\t%lf", comp,mov,tempo );

    fclose(log);

    return 0;
}