#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <time.h>

#define TAM_TABELA 31

typedef struct
{
    int hora;
    int minuto;
} hora_t;

typedef struct
{
    int ano;
    int mes;
    int dia;
} data_t;

typedef struct
{
    int id;
    char nome[200];
    char cidade[200];
    int capacidade;
    double avaliacao;
    char tipos_cozinha[10][200];
    int num_tipos_cozinha;
    int faixa_preco;
    hora_t hora_abertura;
    hora_t hora_fechamento;
    data_t data_abertura;
    int aberto; /* 1 para true, 0 para false */
} restaurante_t;

typedef struct
{
    int tamanho;
    restaurante_t restaurantes[1000];
} colecao_restaurantes_t;

typedef struct no_lista
{
    restaurante_t dado;
    struct no_lista *prox;
} no_lista_t;

no_lista_t *tabela[TAM_TABELA];

int comparacoes = 0;
int movimentacoes = 0;

// converte substring para inteiro
int str_to_int(const char *s, int ini, int fim)
{
    int r = 0;
    int i;
    for (i = ini; i < fim; i++)
    {
        r = r * 10 + (s[i] - '0');
    }
    return r;
}

// converte string para double
double str_to_double(const char *s)
{
    double r = 0, dec = 0;
    int i = 0;

    while (s[i] != '\0' && s[i] != '.')
    {
        r = r * 10 + (s[i] - '0');
        i++;
    }

    if (s[i] == '.')
    {
        i++;
        dec = 0.1;
        while (s[i] != '\0')
        {
            r += (s[i] - '0') * dec;
            dec /= 10;
            i++;
        }
    }

    return r;
}

// remove espacos das bordas
void recortar(char *destino, const char *s, int ini, int fim)
{
    int idx = 0;
    int i;

    while (ini < fim && s[ini] == ' ')
    {
        ini++;
    }
    while (fim > ini && s[fim - 1] == ' ')
    {
        fim--;
    }

    for (i = ini; i < fim; i++)
    {
        destino[idx++] = s[i];
    }
    destino[idx] = '\0';
}

// pega proximo campo separado por virgula
void prox_campo(char *destino, const char *s, int *pos)
{
    int ini = *pos, fim = ini;

    while (s[fim] != ',' && s[fim] != '\0')
    {
        fim++;
    }

    *pos = (s[fim] == ',') ? fim + 1 : fim;
    recortar(destino, s, ini, fim);
}

// divide string por separador
int dividir(char destino[10][200], const char *s, char sep)
{
    int idx = 0, ini = 0;
    int i = 0;

    while (s[i] != '\0')
    {
        if (s[i] == sep)
        {
            recortar(destino[idx], s, ini, i);
            idx++;
            ini = i + 1;
        }
        i++;
    }

    recortar(destino[idx], s, ini, i);
    idx++;

    return idx;
}

// converte "true"/"false" para inteiro
int str_to_bool(const char *s)
{
    return (s[0] == 't' && s[1] == 'r' && s[2] == 'u' && s[3] == 'e') ? 1 : 0;
}

// conta cifroes para definir faixa de preco
int simbolo_para_faixa(const char *s)
{
    int c = 0;
    int i = 0;

    while (s[i] != '\0')
    {
        if (s[i] == '$')
        {
            c++;
        }
        i++;
    }

    return c;
}

// converte faixa de preco em cifroes
void faixa_para_simbolo(char *destino, int f)
{
    if (f == 1)
    {
        sprintf(destino, "$");
    }
    else if (f == 2)
    {
        sprintf(destino, "$$");
    }
    else if (f == 3)
    {
        sprintf(destino, "$$$");
    }
    else
    {
        sprintf(destino, "$$$$");
    }
}

// converte string "hh:mm" para hora_t
hora_t parse_hora(const char *s)
{
    hora_t h;
    int sep = 0, fim = 0;

    while (s[sep] != ':' && s[sep] != '\0')
    {
        sep++;
    }

    fim = sep + 1;
    while (s[fim] != '\0')
    {
        fim++;
    }

    h.hora = str_to_int(s, 0, sep);
    h.minuto = str_to_int(s, sep + 1, fim);

    return h;
}

// converte "ano-mes-dia" para data_t
data_t parse_data(const char *s)
{
    data_t d;
    int sep1 = 0, sep2 = 0, fim;

    while (s[sep1] != '-' && s[sep1] != '\0')
    {
        sep1++;
    }

    sep2 = sep1 + 1;

    while (s[sep2] != '-' && s[sep2] != '\0')
    {
        sep2++;
    }
    fim = sep2 + 1;

    while (s[fim] != '\0')
    {
        fim++;
    }

    d.ano = str_to_int(s, 0, sep1);
    d.mes = str_to_int(s, sep1 + 1, sep2);
    d.dia = str_to_int(s, sep2 + 1, fim);

    return d;
}

// formata hora como "hh:mm"
void formatar_hora(char *destino, hora_t h)
{
    sprintf(destino, "%02d:%02d", h.hora, h.minuto);
}

// formata data como "dd/mm/aa"
void formatar_data(char *destino, data_t d)
{
    sprintf(destino, "%02d/%02d/%04d", d.dia, d.mes, d.ano);
}

// cria restaurante_t a partir de linha CSV
restaurante_t parse_restaurante(const char *linha)
{
    restaurante_t r;
    int pos = 0, i = 0, j = 0;
    char campo[200];
    char temp[200];
    char hora_str[200];
    int sep_h, fim = 0, fim2 = 0;

    // id
    prox_campo(campo, linha, &pos);

    fim = 0;
    while (campo[fim] != '\0')
    {
        fim++;
    }

    r.id = str_to_int(campo, 0, fim);

    // nome
    prox_campo(r.nome, linha, &pos);

    // cidade
    prox_campo(r.cidade, linha, &pos);

    // capacidade
    prox_campo(campo, linha, &pos);

    fim = 0;

    while (campo[fim] != '\0')
    {
        fim++;
    }
    r.capacidade = str_to_int(campo, 0, fim);

    // avaliacao
    prox_campo(campo, linha, &pos);
    r.avaliacao = str_to_double(campo);

    // tipos de cozinha
    prox_campo(campo, linha, &pos);

    if (strcmp(campo, "null") == 0)
    {
        r.num_tipos_cozinha = 0;
    }
    else
    {
        r.num_tipos_cozinha = dividir(r.tipos_cozinha, campo, ';');
    }

    // faixa de preco
    prox_campo(campo, linha, &pos);
    r.faixa_preco = simbolo_para_faixa(campo);

    // horario (formato "hh:mm-hh:mm")
    prox_campo(campo, linha, &pos);

    sep_h = 0;
    fim = 0;

    while (campo[sep_h] != '-' && campo[sep_h] != '\0')
    {
        sep_h++;
    }

    recortar(temp, campo, 0, sep_h); // hora abertura
    r.hora_abertura = parse_hora(temp);

    fim2 = sep_h + 1;
    while (campo[fim2] != '\0')
    {
        fim2++;
    }

    recortar(temp, campo, sep_h + 1, fim2); // hora fechamento
    r.hora_fechamento = parse_hora(temp);

    // data de abertura
    prox_campo(campo, linha, &pos);
    r.data_abertura = parse_data(campo);

    // aberto
    prox_campo(campo, linha, &pos);
    r.aberto = str_to_bool(campo);

    return r;
}

// formata saida do restaurante
void formatar_restaurante(char *destino, restaurante_t r)
{
    char tipos[200] = "";
    char faixa[10];
    char hora_abre[10];
    char hora_fecha[10];
    char data_abre[15];
    int i = 0, p = 0, j = 0;

    tipos[0] = '\0';
    i = 0;

    // trata lista de tipos de cozinha
    if (r.num_tipos_cozinha == 0)
    {
        tipos[0] = 'n';
        tipos[1] = 'u';
        tipos[2] = 'l';
        tipos[3] = 'l';
        tipos[4] = '\0';
    }
    else
    {
        while (i < r.num_tipos_cozinha)
        {
            p = 0;
            while (tipos[p] != '\0')
            {
                p++;
            }

            if (i > 0) // adiciona virgula entre itens
            {
                tipos[p] = ',';
                p++;
                tipos[p] = '\0';
            }

            j = 0;
            while (r.tipos_cozinha[i][j] != '\0')
            {
                tipos[p] = r.tipos_cozinha[i][j];
                p++;
                j++;
            }
            tipos[p] = '\0';
            i++;
        }
    }

    faixa_para_simbolo(faixa, r.faixa_preco);
    formatar_hora(hora_abre, r.hora_abertura);
    formatar_hora(hora_fecha, r.hora_fechamento);
    formatar_data(data_abre, r.data_abertura);

    sprintf(destino, "[%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s]",
            r.id, r.nome, r.cidade, r.capacidade, r.avaliacao,
            tipos, faixa, hora_abre, hora_fecha, data_abre,
            r.aberto ? "true" : "false");
}

// le arquivo CSV
void ler_csv(colecao_restaurantes_t *colecao, const char *path)
{
    FILE *file = fopen(path, "r");
    char linha[400];
    int k = 0;

    if (file == NULL)
    {
        return;
    }

    fgets(linha, sizeof(linha), file); // pula cabecalho

    while (fgets(linha, sizeof(linha), file))
    {
        // remove nova linha
        k = 0;
        while (linha[k] != '\n' && linha[k] != '\0')
        {
            k++;
        }
        linha[k] = '\0';

        if (linha[0] != '\0')
        {
            colecao->restaurantes[colecao->tamanho] = parse_restaurante(linha);
            colecao->tamanho++;
        }
    }

    fclose(file);
}

colecao_restaurantes_t colecao; // Feito para evitar stackoverflow

void ler_csv_arquivo() // void para que aceite Linux e Windows e não dê stackoverflow
{
    colecao.tamanho = 0;
    FILE *f = fopen("/tmp/restaurantes.csv", "r");

    if (f != NULL)
    {
        fclose(f);
        ler_csv(&colecao, "/tmp/restaurantes.csv");
    }
    else
    {
        ler_csv(&colecao, "tmp/restaurantes.csv");
    }
}

// busca restaurante por id, retorna indice ou -1
int buscar(colecao_restaurantes_t *colecao, int id)
{
    int i = 0;

    while (i < colecao->tamanho && colecao->restaurantes[i].id != id)
    {
        i++;
    }

    if (i < colecao->tamanho)
    {
        return i;
    }
    else
    {
        return -1;
    }
}

void selecao(colecao_restaurantes_t *colecao) // Faz a ordenacao por selecao
{
    int i = 0, j = 0, min = 0;
    restaurante_t temp;

    for (i = 0; i < colecao->tamanho - 1; i++)
    {
        min = i;
        for (j = i + 1; j < colecao->tamanho; j++)
        {
            comparacoes++;
            if (strcmp(colecao->restaurantes[j].nome, colecao->restaurantes[min].nome) < 0)
            {
                min = j;
            }
        }
        if (min != i)
        {
            temp = colecao->restaurantes[i];
            colecao->restaurantes[i] = colecao->restaurantes[min];
            colecao->restaurantes[min] = temp;
            movimentacoes += 3;
        }
    }
}

void gerar_log(double tempo)
{
    FILE *f = fopen("832656_hash_indireta.txt", "w");

    if (f != NULL)
    {
        fprintf(f, "%s\t%d\t%.6f\n", "832656", comparacoes, tempo);
        fclose(f);
    }
}

void inicializar_tabela()
{
    int i = 0;
    while (i < TAM_TABELA)
    {
        tabela[i] = NULL;
        i++;
    }
}

int hash(const char *nome)
{
    int soma = 0, i = 0;
    while (nome[i] != '\0')
    {
        soma += (unsigned char)nome[i];
        i++;
    }
    return soma % TAM_TABELA;
}

void inserir_hash(restaurante_t r)
{
    int pos = hash(r.nome);
    no_lista_t *novo = (no_lista_t *)malloc(sizeof(no_lista_t));
    no_lista_t *atual;

    novo->dado = r;
    novo->prox = NULL;

    if (tabela[pos] == NULL)
    {
        tabela[pos] = novo;
    }
    else
    {
        atual = tabela[pos];
        while (atual->prox != NULL)
        {
            atual = atual->prox;
        }
        atual->prox = novo;
    }
}

/* Retorna posicao na tabela (0-30) ou -1 se nao encontrado */
int buscar_hash(const char *nome)
{
    int pos = hash(nome);
    no_lista_t *atual = tabela[pos];

    while (atual != NULL)
    {
        comparacoes++;
        if (strcmp(atual->dado.nome, nome) == 0)
        {
            return pos;
        }
        atual = atual->prox;
    }

    return -1;
}

colecao_restaurantes_t selecionados;
// programa principal
int main()
{
    int id, pos, i;
    clock_t inicio, fim_clock;
    double tempo;
    char resultado[400];
    char nome_busca[200];

    inicializar_tabela();
    ler_csv_arquivo();

    /* Le ids, busca na colecao e insere na hash */
    while (scanf("%d", &id) == 1 && id != -1)
    {
        pos = buscar(&colecao, id);
        if (pos != -1)
            inserir_hash(colecao.restaurantes[pos]);
    }

    inicio = clock();
    while (scanf(" %[^\n]", nome_busca) == 1 && strcmp(nome_busca, "FIM") != 0)
    {
        pos = buscar_hash(nome_busca);

        if (pos == -1)
        {
            printf("-1\n");
        }
        else
        {
            no_lista_t *no = tabela[pos];

            while (no != NULL && strcmp(no->dado.nome, nome_busca) != 0)
            {
                no = no->prox;
            }

            sprintf(resultado, "%d ", pos);
            formatar_restaurante(resultado + strlen(resultado), no->dado);
            printf("%s\n", resultado);
        }
    }

    fim_clock = clock();
    tempo = (double)(fim_clock - inicio) / CLOCKS_PER_SEC;

    gerar_log(tempo);

    return 0;
}