import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;


class Data {
    private int dia;
    private int mes;
    private int ano;


    public Data(int dia, int mes, int ano){
        this.dia = dia;
        this.mes = mes;
        this.ano = ano;
    }

    public static Data parseData(String s){
        String[] partes = s.split("-");

        int ano = Integer.parseInt(partes[0]);
        int mes = Integer.parseInt(partes[1]);
        int dia = Integer.parseInt(partes[2]);

       return new Data(dia, mes, ano);
    }

    public String formatar(){

       return String.format("%02d/%02d/%04d", dia, mes, ano);
    }

}

class Hora {
    private int hora;
    private int minuto;


    public Hora(int hora, int minuto){
        this.hora = hora;
        this.minuto = minuto;
    }


    public static Hora parseHora(String s){
        String[] partes = s.split(":");
        int hora = Integer.parseInt(partes[0]);
        int minuto = Integer.parseInt(partes[1]);

        return new Hora(hora,minuto);
    }


    public String formatar(){
        return String.format("%02d:%02d", hora, minuto);
    }
}

class Restaurante {
    private int id;
    private String nome;
    private String cidade;
    private int capacidade;
    private double  avaliacao;
    private String[] tiposcozinha;
    private String preco;
    private Hora abertura;
    private Hora fechamento;
    private Data dataAbertura;
    private boolean aberto;


    public Restaurante(int id, String nome, String cidade, int capacidade, double avaliacao,
                   String[] tiposcozinha, String preco,
                   Hora abertura, Hora fechamento,
                   Data dataAbertura, boolean aberto) {

    this.id = id;
    this.nome = nome;
    this.cidade = cidade;
    this.capacidade = capacidade;
    this.avaliacao = avaliacao;
    this.tiposcozinha = tiposcozinha;
    this.preco = preco;
    this.abertura = abertura;
    this.fechamento = fechamento;
    this.dataAbertura = dataAbertura;
    this.aberto = aberto;
}

    public String getCidade() {
    return cidade;
    }

    public static Restaurante parseRestaurante(String s){
        String[] partes = s.split(",");

        int id = Integer.parseInt(partes[0]);
        String nome = partes[1];
        String cidade = partes[2];
        int capacidade = Integer.parseInt(partes[3]);
        double avaliacao = Double.parseDouble(partes[4]);

        String tiposStr = partes[5].replace("[", "").replace("]", "");
        String[] tipos = tiposStr.split(";");

        String faixa = partes[6];

        String[] horas = partes[7].split("-");

        Hora abertura = Hora.parseHora(horas[0]);
        Hora fechamento = Hora.parseHora(horas[1]);

        Data data = Data.parseData(partes[8]);

        boolean aberto = Boolean.parseBoolean(partes[9]);

        return new Restaurante(id, nome, cidade, capacidade, avaliacao,
                       tipos, faixa, abertura, fechamento,
                       data, aberto);
    }


    public String formatar(){
        String tiposStr = "[" + String.join(",", tiposcozinha) + "]";
        String horario = abertura.formatar() + "-" + fechamento.formatar();
        String data = dataAbertura.formatar();

        return id + " ## " + nome + " ## " + cidade + " ## " +
       capacidade + " ## " + avaliacao + " ## " +
       tiposStr + " ## " + preco + " ## " +
       horario + " ## " + data + " ## " + aberto;
    }

    public int getId(){
        return id;
    }

    public String getNome() {
    return nome;
    }
}

class ColecaoRestaurantes {
    private Restaurante[] restaurantes;
    private int tamanho;

    public int getTamanho(){
        return tamanho;
    }

    public Restaurante[] getRestaurantes(){
        return restaurantes;
    }

    public void carregarCsv(String path) throws Exception {
        Scanner sc = new Scanner(new File(path));

        restaurantes = new Restaurante[1000];
        tamanho = 0;

        while (sc.hasNextLine()) {
            String linha = sc.nextLine();

            if (linha.trim().isEmpty()) continue;

            Restaurante r = Restaurante.parseRestaurante(linha);

            restaurantes[tamanho] = r;
            tamanho++;
        }

        sc.close();
    }



    public void lerCsv(String path) throws Exception {
        Scanner sc = new Scanner(new File(path));

        restaurantes = new Restaurante[1000];
        tamanho = 0;
        
        sc.nextLine();

        while (sc.hasNextLine()) {
            String linha = sc.nextLine();

            if (linha.trim().isEmpty()) continue;

            restaurantes[tamanho++] = Restaurante.parseRestaurante(linha);
        }

        sc.close();
    }
}

class No {
    public char letra;
    public No esq;
    public No dir;
    public No filho;
    public Restaurante elemento;

    public No(char letra){
        this.letra = letra;
        esq = dir = filho = null;
        elemento = null;
    }
}

class Trie {

    private No raiz;
    public int comp = 0;

    public Trie(){
        raiz = new No('\0');
    }

    public void inserir(Restaurante r)
    throws Exception {

        raiz.filho =
        inserir(
            r.getNome(),
            r,
            raiz.filho,
            0
        );
    }

    private No inserir(
        String s,
        Restaurante r,
        No i,
        int pos
    ) throws Exception {

        char c = s.charAt(pos);

        if(i == null){

            i = new No(c);

            if(pos == s.length()-1){

                i.elemento = r;

            }else{

                i.filho =
                inserir(
                    s,
                    r,
                    i.filho,
                    pos+1
                );
            }
        }
        else if(c < i.letra){

            i.esq =
            inserir(
                s,
                r,
                i.esq,
                pos
            );
        }
        else if(c > i.letra){

            i.dir =
            inserir(
                s,
                r,
                i.dir,
                pos
            );
        }
        else{

            if(pos == s.length()-1){

                i.elemento = r;

            }else{

                i.filho =
                inserir(
                    s,
                    r,
                    i.filho,
                    pos+1
                );
            }
        }

        return i;
    }

    public Restaurante pesquisar(
        String nome){

        System.out.print("raiz ");

        return pesquisar(
            nome,
            raiz.filho,
            0
        );
    }

    private Restaurante pesquisar(
        String nome,
        No i,
        int pos){

        if(i == null)
            return null;

        char c = nome.charAt(pos);

        comp++;

        if(c < i.letra){

            return pesquisar(
                nome,
                i.esq,
                pos
            );
        }

        if(c > i.letra){

            return pesquisar(
                nome,
                i.dir,
                pos
            );
        }

        System.out.print(i.letra + " ");

        if(pos == nome.length()-1){

            return i.elemento;
        }

        return pesquisar(
            nome,
            i.filho,
            pos+1
        );
    }
}


class Ex10{
public static void main(String[] args)
throws Exception{

    Scanner sc=
    new Scanner(System.in);

    ColecaoRestaurantes c=
    new ColecaoRestaurantes();

    c.lerCsv(
    "/tmp/restaurantes.csv");

   Trie arv = new Trie();

    long inicio=
    System.nanoTime();

    while(true){

        int id=
        sc.nextInt();

        if(id==-1)
            break;

        for(int i=0;
            i<c.getTamanho();
            i++){

            Restaurante r=
            c.getRestaurantes()[i];

            if(r.getId()==id){

                arv.inserir(r);

                break;
            }
        }
    }

    sc.nextLine();

    while(true){

        String nome=
        sc.nextLine();

        if(nome.equals("FIM"))
            break;

    Restaurante resp = arv.pesquisar(nome);

if(resp != null){

    System.out.println(
    "SIM [" +
    resp.formatar() +
    "]");

}else{

    System.out.println(
    "NAO");
}

    }

    

    long fim=
    System.nanoTime();

    double tempo=
    (fim-inicio)/1e9;

   PrintWriter log = new PrintWriter("832656_arvore_trie_arvore.txt");

    log.println("832656\t"+arv.comp+"\t"+tempo);

    log.close();

    sc.close();
    }
}