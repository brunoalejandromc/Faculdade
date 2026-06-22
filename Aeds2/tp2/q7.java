
import java.io.*;
import java.util.*;

// --- Classes Auxiliares de Formatação ---

class Data {
    private int ano;
    private int mes;
    private int dia;

    public Data(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    public int getAno() { return this.ano; }
    public int getMes() { return this.mes; }
    public int getDia() { return this.dia; }
    public void setAno(int ano) { this.ano = ano; }

    public static Data parseData(String s) {
        Scanner sc = new Scanner(s);
        sc.useDelimiter("-");
        int ano = sc.nextInt();
        int mes = sc.nextInt();
        int dia = sc.nextInt();
        sc.close();
        return new Data(ano, mes, dia);
    }

    public String formatar() {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }
}

class Hora {
    private int hora;
    private int minuto;

    public Hora(int hora, int minuto) {
        this.hora = hora;
        this.minuto = minuto;
    }

    public static Hora parseHora(String s) {
        Scanner sc = new Scanner(s);
        sc.useDelimiter(":");
        int h = sc.nextInt();
        int m = sc.nextInt();
        sc.close();
        return new Hora(h, m);
    }

    public String formatar() { return String.format("%02d:%02d", hora, minuto); }
}

class Price {
    private int faixa;
    public Price(int faixa) { this.faixa = faixa; }

    public String formatar() {
        String resp = "";
        for (int i = 0; i < faixa; i++)
            resp += "$";
        return resp;
    }
}

class TiposCozinha {
    private String[] types;

    public TiposCozinha(String[] types) { this.types = types; }

    public static TiposCozinha create(String tipos) {
        Scanner sc = new Scanner(tipos);
        sc.useDelimiter(";");
        int count = 0;
        for (int i = 0; i < tipos.length(); i++) {
            if (tipos.charAt(i) == ';') count++;
        }
        String[] tps = {};
        if (sc.hasNext()) {
            tps = new String[count + 1];
            for (int i = 0; i < count + 1; i++) {
                tps[i] = sc.next();
            }
        }
        sc.close();
        return new TiposCozinha(tps);
    }

    public String formatar() {
        String res = "[";
        for (int i = 0; i < types.length; i++) {
            res += types[i];
            if (i < types.length - 1) res += ",";
        }
        return res + "]";
    }
}

// --- Classe Principal Restaurante ---

class Restaurante {
    private int id;
    private String nome, cidade;
    private int capacidade;
    private double avaliacao;
    private TiposCozinha tiposCozinha;
    private int faixaPreco;
    private Data dataAbertura;
    private Hora abertura, fechamento;
    private boolean aberto;

    public Restaurante(int id, String nome, String cidade, int capacidade, double avaliacao,
                       TiposCozinha tiposCozinha, int faixaPreco, Data dataAbertura,
                       Hora abertura, Hora fechamento, boolean aberto) {
        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.capacidade = capacidade;
        this.avaliacao = avaliacao;
        this.tiposCozinha = tiposCozinha;
        this.faixaPreco = faixaPreco;
        this.dataAbertura = dataAbertura;
        this.abertura = abertura;
        this.fechamento = fechamento;
        this.aberto = aberto;
    }

    public int getId()        { return id; }
    public String getNome()   { return nome; }
    public String getCidade() { return cidade; }

    public static Restaurante parseRestaurante(String s) {
        Scanner sc = new Scanner(s);
        sc.useDelimiter(",");

        int id         = sc.nextInt();
        String nome    = sc.next();
        String cidade  = sc.next();
        int capacidade = sc.nextInt();
        double avalia  = Double.parseDouble(sc.next());
        String cozinhas = sc.next();
        TiposCozinha tpc = TiposCozinha.create(cozinhas);

        int preco      = sc.next().length();
        String horas   = sc.next();

        Scanner scH = new Scanner(horas);
        scH.useDelimiter("-");
        Hora hAbrir  = Hora.parseHora(scH.next());
        Hora hFechar = Hora.parseHora(scH.next());
        Data dAbrir  = Data.parseData(sc.next());
        boolean func = sc.nextBoolean();

        sc.close();
        scH.close();
        return new Restaurante(id, nome, cidade, capacidade, avalia, tpc, preco, dAbrir, hAbrir, hFechar, func);
    }

    public String formatar() {
        Price preco = new Price(faixaPreco);
        return String.format(Locale.US, "[%d ## %s ## %s ## %d ## %.1f ## %s ## %s ## %s-%s ## %s ## %b]",
            id, nome, cidade, capacidade, avaliacao,
            tiposCozinha.formatar(), preco.formatar(),
            abertura.formatar(), fechamento.formatar(),
            dataAbertura.formatar(), aberto);
    }
}

// --- Coleção e Leitura de CSV ---

class ColecaoRestaurantes {
    private Restaurante[] cRest;
    private int n;

    public ColecaoRestaurantes() {
        cRest = new Restaurante[1000];
        n = 0;
    }

    public void lerCsv(String path) {
        try {
            Scanner fsc = new Scanner(new File(path));
            if (fsc.hasNextLine()) fsc.nextLine(); // Pula cabeçalho
            while (fsc.hasNextLine() && n < cRest.length) {
                cRest[n++] = Restaurante.parseRestaurante(fsc.nextLine());
            }
            fsc.close();
        } catch (FileNotFoundException e) {
            // sem print
        }
    }

    public Restaurante buscarPorId(int id) {
        for (int i = 0; i < n; i++) {
            if (cRest[i].getId() == id) return cRest[i];
        }
        return null;
    }

    public void adicionarRestaurante(Restaurante r) {
        if (n < cRest.length) cRest[n++] = r;
    }

    public int getN()                     { return n; }
    public Restaurante get(int i)         { return cRest[i]; }
    public void set(int i, Restaurante r) { cRest[i] = r; }
}

// --- Main ---

public class q7 {

    static double comparacoes   = 0;
    static double movimentacoes = 0;

    // Merge — chave primária: cidade | desempate: nome (ambos crescente)
    public static void merge(ColecaoRestaurantes col, int left, int mid, int right) {
        int tamL = mid - left + 1;
        int tamR = right - mid;

        Restaurante[] L = new Restaurante[tamL];
        Restaurante[] R = new Restaurante[tamR];

        for (int i = 0; i < tamL; i++) { L[i] = col.get(left + i);  movimentacoes++; }
        for (int j = 0; j < tamR; j++) { R[j] = col.get(mid + 1 + j); movimentacoes++; }

        int i = 0, j = 0, k = left;

        while (i < tamL && j < tamR) {
            comparacoes++;
            int cmp = L[i].getCidade().compareTo(R[j].getCidade());
            if (cmp == 0) {
                comparacoes++;
                cmp = L[i].getNome().compareTo(R[j].getNome());
            }
            if (cmp <= 0) {
                col.set(k, L[i]); i++;
            } else {
                col.set(k, R[j]); j++;
            }
            movimentacoes++;
            k++;
        }

        while (i < tamL) { col.set(k, L[i]); movimentacoes++; i++; k++; }
        while (j < tamR) { col.set(k, R[j]); movimentacoes++; j++; k++; }
    }

    public static void mergeSort(ColecaoRestaurantes col, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;
            mergeSort(col, left, mid);
            mergeSort(col, mid + 1, right);
            merge(col, left, mid, right);
        }
    }

    // Pesquisa Sequencial — busca por nome exato
    public static int pesquisaSequencial(ColecaoRestaurantes col, String nome) {
        for (int i = 0; i < col.getN(); i++) {
            if (col.get(i).getNome().compareTo(nome) == 0) return i;
        }
        return -1;
    }

    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);
        ColecaoRestaurantes colecao      = new ColecaoRestaurantes();
        ColecaoRestaurantes selecionados = new ColecaoRestaurantes();

        colecao.lerCsv("/tmp/restaurantes.csv");

        // Fase 1: leitura de IDs até -1
        int idBusca = in.nextInt();
        while (idBusca != -1) {
            Restaurante r = colecao.buscarPorId(idBusca);
            if (r != null) selecionados.adicionarRestaurante(r);
            idBusca = in.nextInt();
        }

        // Ordenar por cidade (desempate: nome) via MergeSort
        long inicio = System.nanoTime();
        mergeSort(selecionados, 0, selecionados.getN() - 1);
        long fim = System.nanoTime();
        double tempo = (fim - inicio) / 1_000_000.0;

        // Imprimir restaurantes ordenados por cidade (desempate: nome)
        for (int i = 0; i < selecionados.getN(); i++) {
            System.out.println(selecionados.get(i).formatar());
        }

        // Log
        PrintWriter log = new PrintWriter(new FileWriter("844387_mergesort.txt"));
        log.printf(Locale.US, "844387\t%.0f\t%.0f\t%.2f", comparacoes, movimentacoes, tempo);
        log.close();

        in.close();
    }
}
