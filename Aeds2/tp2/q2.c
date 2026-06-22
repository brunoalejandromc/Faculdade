#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <math.h>
#include <stdbool.h>

// --- Estruturas e Formatação de Data ---
typedef struct Data {
    int ano;
    int mes;
    int dia;
} Data;

Data parse_data(char *s) {
    Data d;
    sscanf(s, " %d - %d - %d ", &d.ano, &d.mes, &d.dia);
    return d;
}

void formatar_data(Data *d, char *Buffer) {
    sprintf(Buffer, "%02d/%02d/%04d", d->dia, d->mes, d->ano);
}

// --- Estruturas e Formatação de Hora ---
typedef struct Hora {
    int hora;
    int minuto;
} Hora;

Hora parse_hora(char *s) {
    Hora h;
    sscanf(s, " %d : %d ", &h.hora, &h.minuto);
    return h;
}

void formatar_hora(Hora *h, char *Buffer) {
    sprintf(Buffer, "%02d:%02d", h->hora, h->minuto);
}

// --- Estruturas e Formatação de Preço ---
typedef struct Price {
    int faixa;
} Price;

Price parse_price(char *s) {
    Price p;
    p.faixa = 0;
    while (*s) {
        if (*s == '$') p.faixa++;
        s++;
    }
    return p;
}

void formatar_price(Price *p, char *Buffer) {
    int i;
    for (i = 0; i < p->faixa; i++) {
        Buffer[i] = '$';
    }
    Buffer[i] = '\0';
}

// --- Estruturas e Formatação de Tipos de Cozinha ---
typedef struct TiposCozinha {
    char **tipos;
    int quantidade;
} TiposCozinha;

TiposCozinha create(const char *tipos) {
    TiposCozinha tc;
    int count = 1;

    for (int i = 0; tipos[i] != '\0'; i++) {
        if (tipos[i] == ';') {
            count++;
        }
    }

    tc.quantidade = count;
    tc.tipos = (char **)malloc(count * sizeof(char *));

    // Correção: Substituindo strdup por malloc e strcpy (Garante compatibilidade C99/Verde)
    char temp[500];
    strcpy(temp, tipos);
    
    char *token = strtok(temp, ";");
    int i = 0;
    while (token != NULL) {
        tc.tipos[i] = (char *)malloc((strlen(token) + 1) * sizeof(char));
        strcpy(tc.tipos[i], token);
        token = strtok(NULL, ";");
        i++;
    }

    return tc;
}

void formatar_tipos(TiposCozinha *tc, char *buffer) {
    int offset = 0;
    offset += sprintf(buffer + offset, "[");

    for (int i = 0; i < tc->quantidade; i++) {
        offset += sprintf(buffer + offset, "%s", tc->tipos[i]);

        if (i < tc->quantidade - 1) {
            offset += sprintf(buffer + offset, ","); // Correção: ESPAÇO adicionado aqui!
        }
    }

    sprintf(buffer + offset, "]");
}

// --- Estruturas e Formatação de Avaliação ---
typedef struct Avaliacao {
    float avaliacao;
} Avaliacao;

void formatar_avaliacao(Avaliacao *a, char *Buffer) {
    sprintf(Buffer, "%.1f", a->avaliacao);
}

// --- Estruturas e Formatação de Funcionamento ---
typedef struct Funcionamento {
    bool func;
} Funcionamento;

void formatar_funcionamento(bool f, char *Buffer) {
    if (f) {
        strcpy(Buffer, "true");
    } else {
        strcpy(Buffer, "false");
    }
}

// --- Estrutura e Parsing Principal do Restaurante ---
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
    Funcionamento funcionamento;
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

    r.id = atoi(campos[0]);
    strcpy(r.nome, campos[1]);
    strcpy(r.cidade, campos[2]);
    r.capacidade = atoi(campos[3]);

    r.avaliacao.avaliacao = atof(campos[4]);
    r.tiposCozinha = create(campos[5]);
    r.preco = parse_price(campos[6]);

    // Proteção na quebra de hora
    char *hifen = strchr(campos[7], '-');
    if (hifen != NULL) {
        *hifen = '\0';
        r.abertura = parse_hora(campos[7]);
        r.fechamento = parse_hora(hifen + 1);
    } else {
        r.abertura.hora = 0; r.abertura.minuto = 0;
        r.fechamento.hora = 0; r.fechamento.minuto = 0;
    }

    r.dataAbertura = parse_data(campos[8]);

    // Prevenção contra \r ou espaços ocultos no dataset
    if (campos[9] != NULL) {
        r.funcionamento.func = (strncmp(campos[9], "true", 4) == 0 || strncmp(campos[9], "True", 4) == 0);
    } else {
        r.funcionamento.func = false;
    }

    return r;
}

void formatar_restaurante(Restaurante *r, char *buffer) {
    char dataBuffer[20], horaABuffer[20], horaFBuffer[20];
    char precoBuffer[10], tiposBuffer[200], funcBuffer[10], avalBuffer[10];

    formatar_data(&r->dataAbertura, dataBuffer);
    formatar_hora(&r->abertura, horaABuffer);
    formatar_hora(&r->fechamento, horaFBuffer);
    formatar_price(&r->preco, precoBuffer);
    formatar_tipos(&r->tiposCozinha, tiposBuffer);
    
    // Correção: Passando o booleano r->funcionamento.func e não a struct
    formatar_funcionamento(r->funcionamento.func, funcBuffer);
    
    formatar_avaliacao(&r->avaliacao, avalBuffer);

    sprintf(buffer,
        "[%d ## %s ## %s ## %d ## %s ## %s ## %s ## %s-%s ## %s ## %s]",
        r->id,
        r->nome,
        r->cidade,
        r->capacidade,
        avalBuffer,
        tiposBuffer,
        precoBuffer,
        horaABuffer,
        horaFBuffer,
        dataBuffer,
        funcBuffer
    );
}

// --- Coleção ---
typedef struct {
    Restaurante array[1000];
    int n;
} ColecaoRestaurantes;

void ler_csv(ColecaoRestaurantes *c, const char *path) {
    FILE *f = fopen(path, "r");
    if (!f) return;

    char linha[1024];

    // Lê a primeira linha (cabeçalho) e ignora
    if (fgets(linha, sizeof(linha), f) == NULL) {
        fclose(f);
        return;
    }

    while (fgets(linha, sizeof(linha), f) && c->n < 1000) {
        linha[strcspn(linha, "\r\n")] = '\0'; // Limpa quebra de linha
        c->array[c->n++] = parse_restaurante(linha);
    }

    fclose(f);
}

Restaurante* buscar_por_id(ColecaoRestaurantes *c, int id) {
    for (int i = 0; i < c->n; i++) {
        if (c->array[i].id == id) {
            return &c->array[i];
        }
    }
    return NULL;
}

// --- Main ---
int main() {
    ColecaoRestaurantes col;
    col.n = 0;

    ler_csv(&col, "/tmp/restaurantes.csv");

    char entrada[50];
    char buffer[500];

    // Ler como string evita loop infinito no scanf se o Verde enviar "FIM"
    while (scanf("%49s", entrada) == 1) { //while (fgets(entrada, sizeof(entrada), stdin) != NULL)
        // entrada[strcspn(entrada, "\n")] = '\0';
        if (strcmp(entrada, "FIM") == 0 || strcmp(entrada, "-1") == 0) {
            break; 
        }

        int idBusca = atoi(entrada);
        Restaurante *r = buscar_por_id(&col, idBusca);

        if (r != NULL) {
            formatar_restaurante(r, buffer);
            printf("%s\n", buffer);
        }
    }

    return 0;
}