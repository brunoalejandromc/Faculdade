
import java.io.*;
import java.util.*;

class Data {
    private int ano, mes, dia;

    public Data(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

    public int getAno() { return ano; }
    public int getMes() { return mes; }
    public int getDia() { return dia; }
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

    // Retorna inteiro para comparação cronológica
    public int toInt() {
        return ano * 10000 + mes * 100 + dia;
    }

    public String formatar() {
        return String.format("%02d/%02d/%04d", dia, mes, ano);
    }
}

class Hora {
    private int hora, minuto;

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
        for (int i = 0; i < faixa; i++) resp += "$";
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
            for (int i = 0; i < count + 1; i++) tps[i] = sc.next();
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

    public int getId() { return id; }
    public String getNome() { return nome; }
    public Data getDataAbertura() { return dataAbertura; }

    public static Restaurante parseRestaurante(String s) {
        Scanner sc = new Scanner(s);
        sc.useDelimiter(",");

        int id = sc.nextInt();
        String nome = sc.next();
        String cidade = sc.next();
        int capacidade = sc.nextInt();
        double avalia = Double.parseDouble(sc.next());
        String cozinhas = sc.next();
        TiposCozinha tpc = TiposCozinha.create(cozinhas);

        int preco = sc.next().length();
        String horas = sc.next();

        Scanner scH = new Scanner(horas);
        scH.useDelimiter("-");
        Hora hAbrir = Hora.parseHora(scH.next());
        Hora hFechar = Hora.parseHora(scH.next());
        Data dAbrir = Data.parseData(sc.next());
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

class ColecaoRestaurantes {
    private Restaurante[] cRest;
    private int n;

    public ColecaoRestaurantes() {
        cRest = new Restaurante[1000];
        n = 0;
    }

    public int getN() { return n; }
    public Restaurante get(int i) { return cRest[i]; }

    public void adicionar(Restaurante r) {
        if (n < cRest.length) cRest[n++] = r;
    }

    public void lerCsv(String path) {
        try {
            Scanner fsc = new Scanner(new File(path));
            if (fsc.hasNextLine()) fsc.nextLine();
            while (fsc.hasNextLine() && n < cRest.length) {
                cRest[n++] = Restaurante.parseRestaurante(fsc.nextLine());
            }
            fsc.close();
        } catch (FileNotFoundException e) {}
    }

    public Restaurante buscarPorId(int id) {
        for (int i = 0; i < n; i++) {
            if (cRest[i].getId() == id) return cRest[i];
        }
        return null;
    }

    // Compara por data crescente; empate: nome A-Z
    private int compareRestaurantes(Restaurante a, Restaurante b) {
        int diffData = a.getDataAbertura().toInt() - b.getDataAbertura().toInt();
        if (diffData != 0) return diffData;
        return a.getNome().compareTo(b.getNome());
    }

    // Mantém a propriedade de max-heap a partir do nó raiz
    private void heapify(int tamHeap, int raiz, double[] stats) {
        int maior = raiz;
        int esq   = 2 * raiz + 1;
        int dir   = 2 * raiz + 2;

        if (esq < tamHeap) {
            stats[0]++;
            if (compareRestaurantes(cRest[esq], cRest[maior]) > 0)
                maior = esq;
        }

        if (dir < tamHeap) {
            stats[0]++;
            if (compareRestaurantes(cRest[dir], cRest[maior]) > 0)
                maior = dir;
        }

        if (maior != raiz) {
            Restaurante temp = cRest[raiz];
            cRest[raiz]      = cRest[maior];
            cRest[maior]     = temp;
            stats[1] += 3;

            heapify(tamHeap, maior, stats);
        }
    }

    // Ordena usando HeapSort; retorna [comparacoes, movimentacoes]
    public double[] ordena() {
        double[] stats = {0, 0};

        for (int i = n / 2 - 1; i >= 0; i--)
            heapify(n, i, stats);

        for (int i = n - 1; i > 0; i--) {
            Restaurante temp = cRest[0];
            cRest[0]         = cRest[i];
            cRest[i]         = temp;
            stats[1] += 3;

            heapify(i, 0, stats);
        }

        return stats;
    }
}

public class q9 {
    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);

        ColecaoRestaurantes colecao = new ColecaoRestaurantes();
        colecao.lerCsv("/tmp/restaurantes.csv");

        ColecaoRestaurantes colecao2 = new ColecaoRestaurantes();

        while (in.hasNextLine()) {
            String linha = in.nextLine();
            String word = linha.trim();
            if (word.compareTo("FIM") == 0 || word.compareTo("-1") == 0) break;

            Scanner scLinha = new Scanner(word);
            if (!scLinha.hasNextInt()) { scLinha.close(); continue; }
            int idBusca = scLinha.nextInt();
            scLinha.close();

            Restaurante r = colecao.buscarPorId(idBusca);
            if (r != null) colecao2.adicionar(r);
        }
        in.close();

        long inicio = System.currentTimeMillis();
        double[] stats = colecao2.ordena();
        long fim = System.currentTimeMillis();
        double tempo = (double)(fim - inicio);

        for (int i = 0; i < colecao2.getN(); i++) {
            System.out.println(colecao2.get(i).formatar());
        }

        try {
            PrintWriter log = new PrintWriter(new FileWriter("844387_heapSort.txt"));
            log.printf("844387\t%.0f\t%.0f\t%.2f", stats[0], stats[1], tempo);
            log.close();
        } catch (IOException e) {}
    }
}
