
import java.io.*;
import java.util.*;

class Data {
    private int ano, mes, dia;

    public Data(int ano, int mes, int dia) {
        this.ano = ano;
        this.mes = mes;
        this.dia = dia;
    }

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

// Lista com alocação sequencial (array) de referências a Restaurante
class Lista {
    private Restaurante[] arr;
    private int n;

    public Lista() {
        arr = new Restaurante[1000];
        n = 0;
    }

    public int getN() { return n; }
    public Restaurante get(int i) { return arr[i]; }

    // Insere no início, remane os demais para a direita
    public void inserirInicio(Restaurante r) {
        for (int i = n; i > 0; i--)
            arr[i] = arr[i - 1];
        arr[0] = r;
        n++;
    }

    // Insere na posição dada, remanejando os demais para a direita
    public void inserir(Restaurante r, int posicao) {
        for (int i = n; i > posicao; i--)
            arr[i] = arr[i - 1];
        arr[posicao] = r;
        n++;
    }

    // Insere no fim
    public void inserirFim(Restaurante r) {
        arr[n++] = r;
    }

    // Remove e retorna o primeiro registro, remanejando os demais para a esquerda
    public Restaurante removerInicio() {
        if (n == 0) return null;
        Restaurante r = arr[0];
        for (int i = 0; i < n - 1; i++)
            arr[i] = arr[i + 1];
        arr[--n] = null;
        return r;
    }

    // Remove e retorna o registro na posição dada, remanejando os demais para a esquerda
    public Restaurante remover(int posicao) {
        if (posicao < 0 || posicao >= n) return null;
        Restaurante r = arr[posicao];
        for (int i = posicao; i < n - 1; i++)
            arr[i] = arr[i + 1];
        arr[--n] = null;
        return r;
    }

    // Remove e retorna o último registro
    public Restaurante removerFim() {
        if (n == 0) return null;
        Restaurante r = arr[--n];
        arr[n] = null;
        return r;
    }
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
}

public class q11 {
    public static void main(String[] args) throws Exception {

        Scanner in = new Scanner(System.in);

        ColecaoRestaurantes colecao = new ColecaoRestaurantes();
        colecao.lerCsv("/tmp/restaurantes.csv");

        Lista lista = new Lista();

        // Parte 1: leitura dos IDs iniciais até -1
        while (in.hasNextLine()) {
            String linha = in.nextLine().trim();
            if (linha.equals("-1")) break;
            if (linha.isEmpty()) continue;
            int id = Integer.parseInt(linha);
            Restaurante r = colecao.buscarPorId(id);
            if (r != null) lista.inserirFim(r);
        }

        // Parte 2: n operações de inserção/remoção
        int qtd = Integer.parseInt(in.nextLine().trim());

        for (int op = 0; op < qtd; op++) {
            String linha = in.nextLine().trim();
            String[] parts = linha.split(" ");
            String cmd = parts[0];

            if (cmd.equals("II")) {
                int id = Integer.parseInt(parts[1]);
                Restaurante r = colecao.buscarPorId(id);
                if (r != null) lista.inserirInicio(r);

            } else if (cmd.equals("IF")) {
                int id = Integer.parseInt(parts[1]);
                Restaurante r = colecao.buscarPorId(id);
                if (r != null) lista.inserirFim(r);

            } else if (cmd.equals("I*")) {
                int pos = Integer.parseInt(parts[1]);
                int id  = Integer.parseInt(parts[2]);
                Restaurante r = colecao.buscarPorId(id);
                if (r != null) lista.inserir(r, pos);

            } else if (cmd.equals("RI")) {
                Restaurante r = lista.removerInicio();
                if (r != null) System.out.println("(R)" + r.getNome());

            } else if (cmd.equals("RF")) {
                Restaurante r = lista.removerFim();
                if (r != null) System.out.println("(R)" + r.getNome());

            } else if (cmd.equals("R*")) {
                int pos = Integer.parseInt(parts[1]);
                Restaurante r = lista.remover(pos);
                if (r != null) System.out.println("(R)" + r.getNome());
            }
        }

        // Impressão final: todos os registros da lista do primeiro ao último
        for (int i = 0; i < lista.getN(); i++) {
            System.out.println(lista.get(i).formatar());
        }

        in.close();
    }
}
