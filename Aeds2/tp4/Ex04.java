import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

class Hora
{
    private int hora;
    private int minuto;

    public Hora()
    {
        // construtor padrao
    }

    public int getHora()
    {
        return this.hora;
    }

    public int getMinuto()
    {
        return this.minuto;
    }

    // converte substring para inteiro
    static int strToInt(String s, int ini, int fim)
    {
        int r = 0;
        for (int i = ini; i < fim; i++)
        {
            r = r * 10 + (s.charAt(i) - '0');
        }
        return r;
    }

    // converte string "hh:mm" para Hora
    public static Hora parseHora(String s)
    {
        Hora h = new Hora();
        int sep = -1;
        int i = 0;

        while (i < s.length() && sep == -1)
        {
            if (s.charAt(i) == ':')
            {
                sep = i;
            }
            i++;
        }

        h.hora = strToInt(s, 0, sep);
        h.minuto = strToInt(s, sep + 1, s.length());
        return h;
    }

    // formata como "hh:mm"
    public String formatar()
    {
        return String.format("%02d:%02d", hora, minuto);
    }
}

class Data
{
    private int ano;
    private int mes;
    private int dia;

    public Data()
    {
        // construtor padrao
    }

    public int getAno()
    {
        return ano;
    }

    public int getMes()
    {
        return mes;
    }

    public int getDia()
    {
        return dia;
    }

    // converte "ano-mes-dia" para Data
    public static Data parseData(String s)
    {
        Data d = new Data();
        int sep1 = 0, sep2 = 0;
        int i = 0;

        while (i < s.length() && s.charAt(i) != '-')
        {
            i++;
        }
        sep1 = i++;

        while (i < s.length() && s.charAt(i) != '-')
        {
            i++;
        }
        sep2 = i;

        d.ano = Hora.strToInt(s, 0, sep1);
        d.mes = Hora.strToInt(s, sep1 + 1, sep2);
        d.dia = Hora.strToInt(s, sep2 + 1, s.length());
        return d;
    }

    // formata como "dd/mm/aa"
    public String formatar()
    {
        return String.format("%02d/%02d/%02d", dia, mes, ano);
    }
}

class Restaurante
{
    private int id;
    private String nome;
    private String cidade;
    private int capacidade;
    private double avaliacao;
    private String[] tiposCozinha;
    private int numTiposCozinha;
    private int faixaPreco;
    private Hora horaAbertura;
    private Hora horaFechamento;
    private Data dataAbertura;
    private boolean aberto;

    public Restaurante()
    {
        // construtor padrao
    }

    // getters
    public int getId()
    {
        return id;
    }

    public String getNome()
    {
        return nome;
    }

    public String getCidade()
    {
        return cidade;
    }

    public int getCapacidade()
    {
        return capacidade;
    }

    public double getAvaliacao()
    {
        return avaliacao;
    }

    public String[] getTiposCozinha()
    {
        return tiposCozinha;
    }

    public int getNumTiposCozinha()
    {
        return numTiposCozinha;
    }

    public int getFaixaPreco()
    {
        return faixaPreco;
    }

    public Hora getHoraAbertura()
    {
        return horaAbertura;
    }

    public Hora getHoraFechamento()
    {
        return horaFechamento;
    }

    public Data getDataAbertura()
    {
        return dataAbertura;
    }

    public boolean getAberto()
    {
        return aberto;
    }

    // remove espacos das bordas
    private static String recortar(String s, int ini, int fim)
    {
        while (ini < fim && s.charAt(ini) == ' ') ini++;
        while (fim > ini && s.charAt(fim - 1) == ' ') fim--;
        char[] buf = new char[fim - ini];
        for (int i = 0; i < buf.length; i++)
        {
            buf[i] = s.charAt(ini + i);
        }
        return new String(buf);
    }

    // pega proximo campo separado por virgula
    private static String proxCampo(String s, int[] pos)
    {
        int ini = pos[0], fim = ini;
        while (fim < s.length() && s.charAt(fim) != ',') fim++;
        pos[0] = fim + 1;
        return recortar(s, ini, fim);
    }

    // converte string para double
    private static double strToDouble(String s)
    {
        double r = 0, dec = 0;
        int i = 0;
        while (i < s.length() && s.charAt(i) != '.')
        {
            r = r * 10 + (s.charAt(i++) - '0');
        }
        if (i < s.length() && s.charAt(i) == '.')
        {
            i++;
            dec = 0.1;
            while (i < s.length())
            {
                r += (s.charAt(i++) - '0') * dec;
                dec /= 10;
            }
        }
        return r;
    }

    // divide string por separador
    private static String[] dividir(String s, char sep)
    {
        int n = 1;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == sep) n++;
        }
        String[] arr = new String[n];
        int idx = 0, ini = 0;
        for (int i = 0; i <= s.length(); i++)
        {
            if (i == s.length() || s.charAt(i) == sep)
            {
                arr[idx++] = recortar(s, ini, i);
                ini = i + 1;
            }
        }
        return arr;
    }

    // converte "true" para booleano
    private static boolean strToBool(String s)
    {
        return s.length() == 4 && s.charAt(0) == 't' && s.charAt(1) == 'r'
                && s.charAt(2) == 'u' && s.charAt(3) == 'e';
    }

    // conta cifroes para definir faixa de preco
    private static int simboloParaFaixa(String s)
    {
        int c = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '$') c++;
        }
        return c;
    }

    // converte faixa de preco em cifroes
    private static String faixaParaSimbolo(int f)
    {
        if (f == 1) return "$";
        if (f == 2) return "$$";
        if (f == 3) return "$$$";
        return "$$$$";
    }

    // cria Restaurante a partir de linha CSV
    public static Restaurante parseRestaurante(String s)
    {
        Restaurante r = new Restaurante();
        int[] pos = { 0 };
        String campo;
        int sepH = 0;

        campo = proxCampo(s, pos);
        r.id = Hora.strToInt(campo, 0, campo.length());

        r.nome = proxCampo(s, pos);
        r.cidade = proxCampo(s, pos);

        campo = proxCampo(s, pos);
        r.capacidade = Hora.strToInt(campo, 0, campo.length());

        campo = proxCampo(s, pos);
        r.avaliacao = strToDouble(campo);

        campo = proxCampo(s, pos);
        r.tiposCozinha = dividir(campo, ';');
        r.numTiposCozinha = r.tiposCozinha.length;

        campo = proxCampo(s, pos);
        r.faixaPreco = simboloParaFaixa(campo);

        campo = proxCampo(s, pos);
        while (sepH < campo.length() && campo.charAt(sepH) != '-')
        {
            sepH++;
        }
        r.horaAbertura = Hora.parseHora(recortar(campo, 0, sepH));
        r.horaFechamento = Hora.parseHora(recortar(campo, sepH + 1, campo.length()));

        campo = proxCampo(s, pos);
        r.dataAbertura = Data.parseData(campo);

        campo = proxCampo(s, pos);
        r.aberto = strToBool(campo);

        return r;
    }

    // formata saida do restaurante
    public String formatar()
    {
        String tipos = tiposCozinha[0];
        for (int i = 1; i < numTiposCozinha; i++)
        {
            tipos = tipos + "," + tiposCozinha[i];
        }
        return String.format("[%d ## %s ## %s ## %d ## %.1f ## [%s] ## %s ## %s-%s ## %s ## %s]",
                id, nome, cidade, capacidade, avaliacao, tipos, faixaParaSimbolo(faixaPreco),
                horaAbertura.formatar(), horaFechamento.formatar(), dataAbertura.formatar(), aberto);
    }
}

class ColecaoRestaurantes
{
    private int tamanho;
    private Restaurante[] restaurantes;

    public ColecaoRestaurantes()
    {
        tamanho = 0;
        restaurantes = new Restaurante[10000];
    }

    public int getTamanho()
    {
        return tamanho;
    }

    public Restaurante[] getRestaurante()
    {
        return restaurantes;
    }

    public void incrementarTamanho()
    {
        tamanho++;
    }

    private static String removerBarraR(String s) // Remove \r presente no Windows/buffer
{
    int len = s.length();
    if (len > 0 && s.charAt(len - 1) == '\r')
    {
        len--;
    }

    char[] buf = new char[len];
    
    for (int i = 0; i < len; i++)
    {
        buf[i] = s.charAt(i);
    }

    return new String(buf);
}

    // le arquivo CSV
    public void lerCsv(String path) throws Exception
    {
        Scanner sc = new Scanner(new File(path));
        sc.nextLine(); // pula cabecalho
        while (sc.hasNextLine())
        {
            String linha = removerBarraR(sc.nextLine());
            if (linha.length() > 0)
            {
                restaurantes[tamanho++] = Restaurante.parseRestaurante(linha);
            }
        }
        sc.close();
    }

    // tenta caminhos padrao e alternativo
    public static ColecaoRestaurantes lerCsv() throws Exception
    {  // Precisei usar dessa forma para que funcione tanto para Linux quanto para Windows
		
        ColecaoRestaurantes c = new ColecaoRestaurantes();
        String caminho = "/tmp/restaurantes.csv";
        File f = new File(caminho);
        if (!f.exists())
        {
            caminho = "tmp/restaurantes.csv";
        }
        c.lerCsv(caminho);
        return c;
    }
}

class TabelaHash
{
    private static final int TAM_TABELA = 83;
    private Restaurante[] tabela;
    private int comparacoes;

    public TabelaHash()
    {
        tabela = new Restaurante[TAM_TABELA];
        comparacoes = 0;
        int i = 0;
        while (i < TAM_TABELA)
        {
            tabela[i] = null;
            i++;
        }
    }

    public int getComparacoes()
    {
        return comparacoes;
    }

    // Soma dos codigos ASCII do nome % 83
    private int hash1(String nome)
    {
        int soma = 0;
        int i = 0;
        while (i < nome.length())
        {
            soma += (int) nome.charAt(i);
            i++;
        }
        return soma % TAM_TABELA;
    }

    // (Soma dos codigos ASCII do nome + 1) % 83
    private int hash2(String nome)
    {
        int soma = 0;
        int i = 0;
        while (i < nome.length())
        {
            soma += (int) nome.charAt(i);
            i++;
        }
        return (soma + 1) % TAM_TABELA;
    }

    // Insere usando rehash: tenta hash1, depois hash2, depois imprime o nome
    public void inserir(Restaurante r)
    {
        int pos1 = hash1(r.getNome());

        if (tabela[pos1] == null)
        {
            tabela[pos1] = r;
        }
        else
        {
            int pos2 = hash2(r.getNome());
            if (tabela[pos2] == null)
            {
                tabela[pos2] = r;
            }
            else
            {
                System.out.println(r.getNome());
            }
        }
    }

    // Busca pelo nome: tenta hash1, depois hash2, retorna posicao ou -1
    public int buscar(String nome)
    {
        int pos1 = hash1(nome);

        comparacoes++;
        if (tabela[pos1] != null && compararNomes(tabela[pos1].getNome(), nome) == 0)
        {
            return pos1;
        }

        int pos2 = hash2(nome);

        comparacoes++;
        if (tabela[pos2] != null && compararNomes(tabela[pos2].getNome(), nome) == 0)
        {
            return pos2;
        }

        return -1;
    }

    // Comparacao de strings sem usar compareTo
    private int compararNomes(String a, String b)
    {
        int tamA = a.length();
        int tamB = b.length();
        int tamMin = 0;

        if (tamA < tamB)
        {
            tamMin = tamA;
        }
        else
        {
            tamMin = tamB;
        }

        int i = 0;
        while (i < tamMin)
        {
            int diferenca = a.charAt(i) - b.charAt(i);
            if (diferenca != 0)
            {
                return diferenca;
            }
            i++;
        }

        return tamA - tamB;
    }

    // Retorna o restaurante na posicao dada
    public Restaurante getRestaurante(int pos)
    {
        return tabela[pos];
    }
}

class Ex04
{
    public static void main(String[] args) throws Exception
    {
        TabelaHash hash = new TabelaHash();
        ColecaoRestaurantes colecao = ColecaoRestaurantes.lerCsv();
        Scanner sc = new Scanner(System.in);

        String linha;
        int id = 0;
        Restaurante[] r = null;
        int tam = 0;
        int j = 0;
        double tempo = 0;
        long fim = 0, inicio = 0;
        boolean achou = false;

        // Le IDs e insere na hash
        linha = sc.nextLine();
        id = Hora.strToInt(linha, 0, linha.length());

        while (linha.charAt(0) != '-')
        {
            achou = false;
            r = colecao.getRestaurante();
            tam = colecao.getTamanho();
            j = 0;

            while (j < tam && !achou)
            {
                if (r[j].getId() == id)
                {
                    hash.inserir(r[j]);
                    achou = true;
                }
                j++;
            }

            linha = sc.nextLine();
            id = Hora.strToInt(linha, 0, linha.length());
        }

        // Le nomes e busca, medindo tempo
        linha = sc.nextLine();
        inicio = System.nanoTime();

        while (linha.charAt(0) != 'F' || linha.length() != 3)
        {
            int pos = hash.buscar(linha);

            if (pos == -1)
            {
                System.out.println("-1");
            }
            else
            {
                System.out.println(pos + " " + hash.getRestaurante(pos).formatar());
            }

            linha = sc.nextLine();
        }

        fim = System.nanoTime();
        tempo = (fim - inicio) / 1000000.0;

        FileWriter fw = new FileWriter("832656_hash_rehash.txt");
        fw.write("832656\t" + hash.getComparacoes() + "\t" + String.format("%.2f", tempo));
        fw.close();

        sc.close();
    }
}