#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
#include <string.h>


// --- Utilitários manuais ---


void my_trim(char *s)
{
    for (int i = 0; s[i] != '\0'; i++)
        if (s[i] == '\r' || s[i] == '\n') { s[i] = '\0'; return; }
}

int my_strchr(const char *s, char c)
{
    for (int i = 0; s[i] != '\0'; i++)
        if (s[i] == c) return i;
    return -1;
}



// --- Data ---
typedef struct Data { int ano, mes, dia; } Data;

Data parse_data(char *s)
{
    Data d;
    sscanf(s, " %d - %d - %d ", &d.ano, &d.mes, &d.dia);
    return d;
}

void formatar_data(Data *d, char *buf)
{
    sprintf(buf, "%02d/%02d/%04d", d->dia, d->mes, d->ano);
}



// --- Hora ---
typedef struct Hora { int hora, minuto; } Hora;

Hora parse_hora(char *s)
{
    Hora h;
    sscanf(s, " %d : %d ", &h.hora, &h.minuto);
    return h;
}

void formatar_hora(Hora *h, char *buf)
{
    sprintf(buf, "%02d:%02d", h->hora, h->minuto);
}



// --- Preco ---
typedef struct Price { int faixa; } Price;

Price parse_price(char *s)
{
    Price p; p.faixa = 0;
    while (*s) { if (*s == '$') p.faixa++; s++; }
    return p;
}

void formatar_price(Price *p, char *buf)
{
    int i;
    for (i = 0; i < p->faixa; i++) buf[i] = '$';
    buf[i] = '\0';
}




// --- TiposCozinha ---

typedef struct TiposCozinha { char raw[200]; int quantidade; } TiposCozinha;

TiposCozinha create(const char *tipos)
{
    TiposCozinha tc;
    tc.quantidade = 1;
    int i = 0;
    while (tipos[i] != '\0')
    {
        tc.raw[i] = tipos[i];
        if (tipos[i] == ';') tc.quantidade++;
        i++;
    }
    tc.raw[i] = '\0';
    return tc;
}

void formatar_tipos(TiposCozinha *tc, char *buf)
{
    int bi = 0;
    buf[bi++] = '[';
    for (int i = 0; tc->raw[i] != '\0'; i++)
        buf[bi++] = (tc->raw[i] == ';') ? ',' : tc->raw[i];
    buf[bi++] = ']';
    buf[bi]   = '\0';
}




// --- Avaliacao ---
typedef struct Avaliacao { float avaliacao; } Avaliacao;

void formatar_avaliacao(Avaliacao *a, char *buf)
{
    sprintf(buf, "%.1f", a->avaliacao);
}



// --- Funcionamento ---
typedef struct Funcionamento { bool func; } Funcionamento;

void formatar_funcionamento(bool f, char *buf)
{
    sprintf(buf, f ? "true" : "false");
}




// --- Restaurante ---
typedef struct Restaurante
{
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

Restaurante parse_restaurante(char *linha)
{
    Restaurante r;
    char *campos[11];
    int idx = 0;

    campos[idx++] = linha;
    for (int i = 0; linha[i] != '\0'; i++)
    {
        if (linha[i] == ',')
        {
            linha[i] = '\0';
            campos[idx++] = &linha[i + 1];
        }
        else if (linha[i] == '\n' || linha[i] == '\r')
            linha[i] = '\0';
    }

    sscanf(campos[0], "%d",       &r.id);
    sscanf(campos[1], "%99[^\n]", r.nome);
    sscanf(campos[2], "%99[^\n]", r.cidade);
    sscanf(campos[3], "%d",       &r.capacidade);
    sscanf(campos[4], "%f",       &r.avaliacao.avaliacao);

    r.tiposCozinha = create(campos[5]);
    r.preco = parse_price(campos[6]);

    int hifen = my_strchr(campos[7], '-');
    if (hifen >= 0)
    {
        campos[7][hifen] = '\0';
        r.abertura   = parse_hora(campos[7]);
        r.fechamento = parse_hora(campos[7] + hifen + 1);
    }
    else
    {
        r.abertura.hora = r.abertura.minuto = 0;
        r.fechamento.hora = r.fechamento.minuto = 0;
    }

    r.dataAbertura = parse_data(campos[8]);

    char func_str[10] = {0};
    if (campos[9] != NULL)
        sscanf(campos[9], "%9s", func_str);
    r.funcionamento.func = (strcmp(func_str, "true") == 0 ||
                            strcmp(func_str, "True") == 0);

    return r;
}

void formatar_restaurante(Restaurante *r, char *buffer)
{
    char dataB[20], haB[20], hfB[20], precoB[10], tiposB[200], funcB[10], avalB[10];

    formatar_data(&r->dataAbertura, dataB);
    formatar_hora(&r->abertura, haB);
    formatar_hora(&r->fechamento, hfB);
    formatar_price(&r->preco, precoB);
    formatar_tipos(&r->tiposCozinha, tiposB);
    formatar_funcionamento(r->funcionamento.func, funcB);
    formatar_avaliacao(&r->avaliacao, avalB);

    sprintf(buffer,
            "[%d ## %s ## %s ## %d ## %s ## %s ## %s ## %s-%s ## %s ## %s]",
            r->id, r->nome, r->cidade, r->capacidade,
            avalB, tiposB, precoB, haB, hfB, dataB, funcB);
}

// --- Coleção ---
typedef struct { Restaurante array[1000]; int n; } ColecaoRestaurantes;

void ler_csv(ColecaoRestaurantes *c, const char *path)
{
    FILE *f = fopen(path, "r");
    if (!f) return;
    char linha[1024];
    if (fgets(linha, sizeof(linha), f) == NULL) { fclose(f); return; }
    while (fgets(linha, sizeof(linha), f) && c->n < 1000)
    {
        my_trim(linha);
        c->array[c->n++] = parse_restaurante(linha);
    }
    fclose(f);
}

Restaurante *buscar_por_id(ColecaoRestaurantes *c, int id)
{
    for (int i = 0; i < c->n; i++)
        if (c->array[i].id == id) return &c->array[i];
    return NULL;
}


// QuickSort — chave primária: nome (alfabético crescente)


double comparacoes_qs   = 0;
double movimentacoes_qs = 0;

void swap(Restaurante *a, Restaurante *b)
{
    Restaurante temp = *a;
    *a = *b;
    *b = temp;
    movimentacoes_qs += 3;
}

int particionar(Restaurante *arr, int low, int high)
{
    Restaurante *pivot = &arr[high];
    int i = low - 1;

    for (int j = low; j < high; j++)
    {
        comparacoes_qs++;
        if (strcmp(arr[j].nome, pivot->nome) <= 0)
        {
            i++;
            if (i != j) swap(&arr[i], &arr[j]);
        }
    }

    if (i + 1 != high) swap(&arr[i + 1], &arr[high]);

    return i + 1;
}

void quickSort(Restaurante *arr, int low, int high)
{
    if (low < high)
    {
        int pi = particionar(arr, low, high);
        quickSort(arr, low,    pi - 1);
        quickSort(arr, pi + 1, high);
    }
}

void ordena(ColecaoRestaurantes *c)
{
    comparacoes_qs   = 0;
    movimentacoes_qs = 0;
    if (c->n > 1)
        quickSort(c->array, 0, c->n - 1);
}


// Pesquisa Binária — busca por nome exato retorna  se encontrado, -1 caso contrário


int pesquisa_binaria(ColecaoRestaurantes *c, const char *nome)
{
    int lo = 0, hi = c->n - 1;

    while (lo <= hi)
    {
        int mid = lo + (hi - lo) / 2;
        int cmp = strcmp(c->array[mid].nome, nome);

        if (cmp == 0)  return mid;   // encontrado
        if (cmp < 0)   lo = mid + 1; // busca na metade direita
        else           hi = mid - 1; // busca na metade esquerda
    }

    return -1; // não encontrado
}

// --- Main ---
int main()
{
    ColecaoRestaurantes col;  col.n  = 0;
    ColecaoRestaurantes col2; col2.n = 0;

    ler_csv(&col, "/tmp/restaurantes.csv");

    char entrada[50];
    char buffer[500];

    // Fase 1: leitura de IDs até -1
    while (fgets(entrada, sizeof(entrada), stdin) != NULL)
    {
        my_trim(entrada);

        char word[50];
        sscanf(entrada, "%49s", word);
        if (strcmp(word, "-1") == 0) break;

        int idBusca;
        sscanf(entrada, "%d", &idBusca);
        Restaurante *r = buscar_por_id(&col, idBusca);
        if (r != NULL) { col2.array[col2.n] = *r; col2.n++; }
    }

    // Ordenar col2 por nome via QuickSort
    clock_t inicio = clock();
    ordena(&col2);
    clock_t fim = clock();
    double tempo = (double)(fim - inicio) / CLOCKS_PER_SEC * 1000.0;

    // Pesquisa binária por nome até FIM
    while (fgets(entrada, sizeof(entrada), stdin) != NULL)
    {
        my_trim(entrada);

        if (strcmp(entrada, "FIM") == 0) break;

        // Pesquisa binária pelo nome digitado
        int idx = pesquisa_binaria(&col2, entrada);
        if (idx >= 0)
            printf("SIM\n");
        else
            printf("NAO\n");
    }

    // Log
    FILE *log = fopen("844387_binaria.txt", "w");
    if (log != NULL)
    {
        fprintf(log, "844387\t%.0f\t%.0f\t%.2f",
                comparacoes_qs, movimentacoes_qs, tempo);
        fclose(log);
    }

    return 0;
}
