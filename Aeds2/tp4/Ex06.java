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
        String[] partes = s.split(",", -1);

        int id = Integer.parseInt(partes[0]);
        String nome = partes[1].trim();
        String cidade = partes[2].trim();
        int capacidade = Integer.parseInt(partes[3]);
        double avaliacao = Double.parseDouble(partes[4]);

        String tiposStr = partes[5].replace("[", "").replace("]", "");
        String[] tipos = tiposStr.split(";");

        String faixa = partes[6].trim();

        String[] horas = partes[7].split("-");

        Hora abertura = Hora.parseHora(horas[0]);
        Hora fechamento = Hora.parseHora(horas[1]);

        Data data = Data.parseData(partes[8]);

        boolean aberto =Boolean.parseBoolean(partes[9].trim());

        return new Restaurante(id, nome, cidade, capacidade, avaliacao,
                       tipos, faixa, abertura, fechamento,
                       data, aberto);
    }


    public String formatar(){
        String tiposStr = "[" + String.join(",", tiposcozinha) + "]";
        String horario = abertura.formatar() + "-" + fechamento.formatar();
        String data = dataAbertura.formatar();

        return "[" + id + " ## " + nome + " ## " + cidade + " ## " +
       capacidade + " ## " + avaliacao + " ## " +
       tiposStr + " ## " + preco + " ## " +
       horario + " ## " + data + " ## " + aberto + "]";
    }

    public int getId(){
        return id;
    }

    public String getNome() {
    return nome;
    }

    public int getCapacidade() {
    return capacidade;
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





class No{
    public Restaurante elemento;
    public No esq, dir;

    public No(Restaurante x){
        elemento = x;
        esq = dir = null;
    }
}

class AVL{
   private No raiz;

    private No inserir(Restaurante r, No i) {
        if (i == null) return new No(r);
        if (r.getNome().compareTo(i.elemento.getNome()) < 0)
            i.esq = inserir(r, i.esq);
        else if (r.getNome().compareTo(i.elemento.getNome()) > 0)
            i.dir = inserir(r, i.dir);
        return i;
    }


public Restaurante pesquisar(String nome){

    System.out.print("raiz ");

    return pesquisar(nome, raiz);
}

private Restaurante pesquisar(String nome, No i){
    Ex06.comp++;

    Restaurante resp = null;

    if(i == null){

        resp = null;

    }else if(nome.equals(i.elemento.getNome())){

        Ex06.comp++;
        resp = i.elemento;

    }else if(nome.compareTo(i.elemento.getNome()) < 0){

        Ex06.comp++;

        System.out.print("esq ");

        resp = pesquisar(nome,i.esq);

    }else{

        Ex06.comp++;

        System.out.print("dir ");

        resp = pesquisar(nome,i.dir);
    }

    return resp;
}

public void inserir(Restaurante x){
    raiz = inserir(x, raiz);
}




    public void caminharCentral(){
    caminharCentral(raiz);
}

    private void caminharCentral(No i){

        if(i != null){

            caminharCentral(i.esq);

            System.out.println(
                i.elemento.formatar()
            );

            caminharCentral(i.dir);
        }
    }

}

class NoPrincipal{

    public int chave;
    public NoPrincipal esq, dir;

    public AVL avl;

    public NoPrincipal(int chave){

        this.chave = chave;

        esq = dir = null;

        avl = new AVL();
    }

}

class ArvoreArvore{
    private NoPrincipal raiz;

public ArvoreArvore() {
    raiz = null;
}

   

    public Restaurante pesquisar(String nome){
        return pesquisar(nome,raiz);
    }
    public void inserirPrincipal(int x){

    raiz =
    inserirPrincipal(x,raiz);
}

private NoPrincipal inserirPrincipal(int x,NoPrincipal i){

    if(i==null){

        i =
        new NoPrincipal(x);

    }else if(x<i.chave){

        i.esq =
        inserirPrincipal(
        x,
        i.esq);

    }else if(x>i.chave){

        i.dir =
        inserirPrincipal(
        x,
        i.dir);
    }

    return i;
}

public void inserir(Restaurante r){
    raiz = inserir(r, raiz);
}

private NoPrincipal inserir(Restaurante r, NoPrincipal i){

    int chave = r.getCapacidade() % 15;

    if(i == null){

        i = new NoPrincipal(chave);
        i.avl.inserir(r);

    }else if(chave < i.chave){

        i.esq = inserir(r, i.esq);

    }else if(chave > i.chave){

        i.dir = inserir(r, i.dir);

    }else{

        i.avl.inserir(r);
    }

    return i;
}

private void inserir(Restaurante r,int chave,NoPrincipal i){

    if(i==null){
        return;
    }

    if(chave < i.chave){

        inserir(
        r,
        chave,
        i.esq);

    }else if(chave > i.chave){

        inserir(
        r,
        chave,
        i.dir);

    }else{

        i.avl.inserir(r);
    }
}

private Restaurante pesquisar(String nome, NoPrincipal i){

    if(i == null){
        return null;
    }

    Restaurante resp =
        i.avl.pesquisar(nome);

    if(resp != null){
        return resp;
    }

    System.out.print("ESQ ");

    resp = pesquisar(nome, i.esq);

    if(resp != null){
        return resp;
    }

    System.out.print("DIR ");

    return pesquisar(nome, i.dir);
}

    
}



public class Ex06 {

    public static long comp = 0;

    public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);



        ColecaoRestaurantes colecao = new ColecaoRestaurantes();

        colecao.lerCsv("/tmp/restaurantes.csv");

        ArvoreArvore arv = new ArvoreArvore();

        while(true){

            int id = sc.nextInt();

            if(id == -1)
                break;

            for(int i = 0; i < colecao.getTamanho(); i++){

                Restaurante r =
                colecao.getRestaurantes()[i];

                if(r.getId() == id){

                    arv.inserir(r);
                    break;
                }
            }
        }



        sc.nextLine();

        long inicio = System.nanoTime();

                while(true){

    String nome = sc.nextLine();

    if(nome.equals("FIM"))
        break;

    System.out.print("RAIZ ");

    Restaurante r = arv.pesquisar(nome);

    if(r != null){

        System.out.println(
        "SIM " +
        r.formatar());

    }else{

        System.out.println("NAO");
    }
}

        long fim =
        System.nanoTime();

        double tempo =
        (fim - inicio) / 1e9;

        PrintWriter log =new PrintWriter("832656_hibrida_arvore_arvore.txt");

        log.println(
        "832656\t" +
        comp + "\t" +
        tempo);

        log.close();

        sc.close();
    }
}