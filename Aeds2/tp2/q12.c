#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <string.h>

void copiar_string(char *dest, const char *src) {
    int i = 0;
    while (src[i] != '\0') {
        dest[i] = src[i];
        i++;
    }
    dest[i] = '\0';
}

// --- Data ---
typedef struct Data {
    int ano, mes, dia;
} Data;

Data parse_data(char *s) {
    Data d;
    sscanf(s, " %d - %d - %d ", &d.ano, &d.mes, &d.dia);
    return d;
}

void formatar_data(Data *d, char *buf) {
    sprintf(buf, "%02d/%02d/%04d", d->dia, d->mes, d->ano);
}

// --- Hora ---
typedef struct Hora {
    int hora, minuto;
} Hora;

Hora parse_hora(char *s) {
    Hora h;
    sscanf(s, " %d : %d ", &h.hora, &h.minuto);
    return h;
}

void formatar_hora(Hora *h, char *buf) {
    sprintf(buf, "%02d:%02d", h->hora, h->minuto);
}

//  Price 
typedef struct Price {
    int faixa;
} Price;

Price parse_price(char *s) {
    Price p; p.faixa = 0;
    while (*s) { if (*s == '$') p.faixa++; s++; }
    return p;
}

void formatar_price(Price *p, char *buf) {
    int i;
    for (i = 0; i < p->faixa; i++) buf[i] = '$';
    buf[i] = '\0';
}

//TiposCozinha 
typedef struct TiposCozinha {
    char **tipos;
    int quantidade;
} TiposCozinha;

TiposCozinha create(const char *tipos) {
    TiposCozinha tc;
    int count = 1;
    for (int i = 0; tipos[i] != '\0'; i++) {
        if (tipos[i] == ';') count++;
    }
    tc.quantidade = count;
    tc.tipos = (char **)malloc(count * sizeof(char *));
    
    int t_idx = 0;
    int start = 0;
    for (int i = 0; ; i++) {
        if (tipos[i] == ';' || tipos[i] == '\0') {
            int len = i - start;
            tc.tipos[t_idx] = (char *)malloc((len + 1) * sizeof(char));
            for (int j = 0; j < len; j++) {
                tc.tipos[t_idx][j] = tipos[start + j];
            }
            tc.tipos[t_idx][len] = '\0';
            t_idx++;
            if (tipos[i] == '\0') break;
            start = i + 1;
        }
    }
    return tc;
}

void formatar_tipos(TiposCozinha *tc, char *buf) {
    int offset = 0;
    offset += sprintf(buf + offset, "[");
    for (int i = 0; i < tc->quantidade; i++) {
        offset += sprintf(buf + offset, "%s", tc->tipos[i]);
        if (i < tc->quantidade - 1)
            offset += sprintf(buf + offset, ",");
    }
    sprintf(buf + offset, "]");
}

//Avaliacao
typedef struct Avaliacao {
    float avaliacao;
} Avaliacao;

void formatar_avaliacao(Avaliacao *a, char *buf) {
    sprintf(buf, "%.1f", a->avaliacao);
}

// Restaurante
typedef struct Restaurante {
    int id;
    char nome[100];
    char cidade[100];
    int capacidade;
    Avaliacao avaliacao;
    TiposCozinha tiposCozinha;
    Price preco;
    Data dataAbertura;
    Hora abertura, fechamento;
    bool funcionamento;
} Restaurante;

Restaurante parse_restaurante(char *linha) {
    Restaurante r;
    char *campos[11];
    int idx = 0;
    campos[idx++] = linha;
    for (int i = 0; linha[i] != '\0'; i++) {
        if (linha[i] == ',') {
            linha[i] = '\0';
            campos[idx++] = &linha[i + 1];
        } else if (linha[i] == '\n' || linha[i] == '\r') {
            linha[i] = '\0';
        }
    }
    
    sscanf(campos[0], "%d", &r.id);
    copiar_string(r.nome, campos[1]);
    copiar_string(r.cidade, campos[2]);
    sscanf(campos[3], "%d", &r.capacidade);
    sscanf(campos[4], "%f", &r.avaliacao.avaliacao);
    
    r.tiposCozinha = create(campos[5]);
    r.preco = parse_price(campos[6]);
    
    char *hifen = NULL;
    for (int i = 0; campos[7][i] != '\0'; i++) {
        if (campos[7][i] == '-') {
            hifen = &campos[7][i];
            break;
        }
    }
    
    if (hifen != NULL) {
        *hifen = '\0';
        r.abertura = parse_hora(campos[7]);
        r.fechamento = parse_hora(hifen + 1);
    }
    
    r.dataAbertura = parse_data(campos[8]);
    
    r.funcionamento = false;
    if (campos[9] != NULL) {
        if ((campos[9][0] == 't' || campos[9][0] == 'T') &&
            campos[9][1] == 'r' &&
            campos[9][2] == 'u' &&
            campos[9][3] == 'e') {
            r.funcionamento = true;
        }
    }
    return r;
}

void formatar_restaurante(Restaurante *r, char *buf) {
    char dataBuf[20], horaABuf[20], horaFBuf[20], precoBuf[10], tiposBuf[200], avalBuf[10];
    formatar_data(&r->dataAbertura, dataBuf);
    formatar_hora(&r->abertura, horaABuf);
    formatar_hora(&r->fechamento, horaFBuf);
    formatar_price(&r->preco, precoBuf);
    formatar_tipos(&r->tiposCozinha, tiposBuf);
    formatar_avaliacao(&r->avaliacao, avalBuf);
    sprintf(buf,
        "[%d ## %s ## %s ## %d ## %s ## %s ## %s ## %s-%s ## %s ## %s]",
        r->id, r->nome, r->cidade, r->capacidade,
        avalBuf, tiposBuf, precoBuf,
        horaABuf, horaFBuf, dataBuf,
        r->funcionamento ? "true" : "false");
}

// Coleção (CSV)
typedef struct {
    Restaurante array[1000];
    int n;
} ColecaoRestaurantes;

void ler_csv(ColecaoRestaurantes *c, const char *path) {
    FILE *f = fopen(path, "r");
    if (!f) return;
    char linha[1024];
    if (fgets(linha, sizeof(linha), f) == NULL) { fclose(f); return; }
    while (fgets(linha, sizeof(linha), f) && c->n < 1000) {
        for(int i = 0; linha[i] != '\0'; i++){
            if(linha[i] == '\r' || linha[i] == '\n'){
                linha[i] = '\0';
                break;
            }
        }
        c->array[c->n++] = parse_restaurante(linha);
    }
    fclose(f);
}

Restaurante* buscar_por_id(ColecaoRestaurantes *c, int id) {
    for (int i = 0; i < c->n; i++)
        if (c->array[i].id == id) return &c->array[i];
    return NULL;
}

//Pilha com alocação sequencial
typedef struct {
    Restaurante *array[1000];
    int topo;                 
} Pilha;

void pilha_init(Pilha *p) { p->topo = 0; }

void pilha_push(Pilha *p, Restaurante *r) {
    if (p->topo < 1000) p->array[p->topo++] = r;
}

Restaurante* pilha_pop(Pilha *p) {
    if (p->topo == 0) return NULL;
    return p->array[--p->topo];
}

//Main
int main() {
    ColecaoRestaurantes col;
    col.n = 0;
    ler_csv(&col, "/tmp/restaurantes.csv");

    Pilha pilha;
    pilha_init(&pilha);

    char entrada[50];

    //empilha IDs iniciais até -1
    while (scanf("%49s", entrada) == 1) {
        if (strcmp(entrada, "-1") == 0 || strcmp(entrada, "FIM") == 0) break;
        int id;
        sscanf(entrada, "%d", &id);
        Restaurante *r = buscar_por_id(&col, id);
        if (r != NULL) pilha_push(&pilha, r);
    }

    // n operações I (push) e R (pop)
    int n;
    scanf("%d", &n);

    char buf[500];
    for (int op = 0; op < n; op++) {
        scanf("%49s", entrada);
        if (strcmp(entrada, "I") == 0) {
            int id;
            scanf("%d", &id);
            Restaurante *r = buscar_por_id(&col, id);
            if (r != NULL) pilha_push(&pilha, r);
        } else if (strcmp(entrada, "R") == 0) {
            Restaurante *r = pilha_pop(&pilha);
            if (r != NULL) printf("(R)%s\n", r->nome);
        }
    }

    // Impressão do topo ao fundo
    for (int i = pilha.topo - 1; i >= 0; i--) {
        formatar_restaurante(pilha.array[i], buf);
        printf("%s\n", buf);
    }

    return 0;
}