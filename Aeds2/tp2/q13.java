
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
    public int getAnoAbertura() { return dataAbertura.getAno(); }

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

// Fila Circular com alocação sequencial, tamanho fixo MAX=5
class Fila {
    private static final int MAX = 5;
    private Restaurante[] arr;
    private int inicio, fim, tamanho;

    public Fila() {
        arr = new Restaurante[MAX];
        inicio = 0;
        fim = 0;
        tamanho = 0;
    }

    public boolean cheia() { return tamanho == MAX; }
    public boolean vazia() { return tamanho == 0; }
    public int getTamanho() { return tamanho; }

    // Retorna a média arredondada do ano de abertura dos elementos da fila
    public int mediaAnoArredondada() {
        double soma = 0;
        for (int i = 0; i < tamanho; i++)
            soma += arr[(inicio + i) % MAX].getAnoAbertura();
        return (int) Math.round(soma / tamanho);
    }

    // Enfileira: se cheia, remove o primeiro antes de inserir
    public Restaurante enfileirar(Restaurante r) {
        Restaurante removido = null;
        if (cheia()) removido = desenfileirar();
        arr[fim] = r;
        fim = (fim + 1) % MAX;
        tamanho++;
        return removido;
    }

    // Desenfileira: remove e retorna o primeiro elemento
    public Restaurante desenfileirar() {
        if (vazia()) return null;
        Restaurante r = arr[inicio];
        inicio = (inicio + 1) % MAX;
        tamanho--;
        return r;
    }

    // Retorna o elemento na posição i (do início ao fim)
    public Restaurante get(int i) { return arr[(inicio + i) % MAX]; }
}

// Coleção auxiliar para leitura do CSV e busca por ID
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
            if (fsc.hasNextLine()) fsc.nextLine();
            while (fsc.hasNextLine() && n < cRest.length)
                cRest[n++] = Restaurante.parseRestaurante(fsc.nextLine());
            fsc.close();
        } catch (FileNotFoundException e) {}
    }

    public Restaurante buscarPorId(int id) {
        for (int i = 0; i < n; i++)
            if (cRest[i].getId() == id) return cRest[i];
        return null;
    }
}

public class q13 {
    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);

        ColecaoRestaurantes colecao = new ColecaoRestaurantes();
        colecao.lerCsv("/tmp/restaurantes.csv");

        Fila fila = new Fila();

        // Parte 1: enfileira IDs iniciais até -1
        while (in.hasNextLine()) {
            String linha = in.nextLine().trim();
            if (linha.equals("-1")) break;
            if (linha.isEmpty()) continue;
            int id = Integer.parseInt(linha);
            Restaurante r = colecao.buscarPorId(id);
            if (r != null) {
                Restaurante removido = fila.enfileirar(r);
                if (removido != null) System.out.println("(R)" + removido.getNome());
                System.out.println("(I)" + fila.mediaAnoArredondada());
            }
        }

        // Parte 2: n operações I (enfileirar) e R (desenfileirar)
        int qtd = Integer.parseInt(in.nextLine().trim());

        for (int op = 0; op < qtd; op++) {
            String linha = in.nextLine().trim();
            String[] parts = linha.split(" ");
            String cmd = parts[0];

            if (cmd.equals("I")) {
                int id = Integer.parseInt(parts[1]);
                Restaurante r = colecao.buscarPorId(id);
                if (r != null) {
                    Restaurante removido = fila.enfileirar(r);
                    if (removido != null) System.out.println("(R)" + removido.getNome());
                    System.out.println("(I)" + fila.mediaAnoArredondada());
                }
            } else if (cmd.equals("R")) {
                Restaurante r = fila.desenfileirar();
                if (r != null) System.out.println("(R)" + r.getNome());
            }
        }

        // Impressão final: do primeiro ao último
        for (int i = 0; i < fila.getTamanho(); i++)
            System.out.println(fila.get(i).formatar());

        in.close();
    }
}
