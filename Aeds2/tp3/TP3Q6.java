import java.io.*;
import java.util.*;


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

        boolean aberto = Boolean.parseBoolean(partes[9].trim());

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

    public Data getData(){
        return dataAbertura;
    }
    
    public String getCidade(){
    return cidade;
    }

    public String[] getTipos(){
    return tiposcozinha;
    }

    public double getAvaliacao(){
    return avaliacao;
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

        sc.nextLine(); 

        while (sc.hasNextLine()) {
            String linha = sc.nextLine();

            if (linha.trim().isEmpty()) continue;

            Restaurante r = Restaurante.parseRestaurante(linha);

            restaurantes[tamanho] = r;
            tamanho++;
        }

        sc.close();
    }


}
class Celula{

    public Restaurante elemento;
    public Celula prox;

    public Celula(Restaurante x){

        elemento=x;
        prox=null;
    }
}

class Pilha{

    private Celula topo;

    public Pilha(){
        topo=null;
    }

    public void inserir(Restaurante x){

        Celula tmp=new Celula(x);

        tmp.prox=topo;

        topo=tmp;
    }

    public Restaurante remover(){

        if(topo==null){
            return null;
        }

        Restaurante resp=topo.elemento;

        Celula tmp=topo;

        topo=topo.prox;

        tmp.prox=null;

        return resp;
    }

    public Celula getTopo(){
        return topo;
    }
}

public class TP3Q6 {



    public static void main(String[] args)throws Exception{

    Scanner sc=new Scanner(System.in);

    ColecaoRestaurantes colecao=
    new ColecaoRestaurantes();

    colecao.carregarCsv("/tmp/restaurantes.csv");

    Pilha pilha=new Pilha();

    while(true){

        int id=sc.nextInt();

        if(id==-1)
            break;

        for(int i=0;i<colecao.getTamanho();i++){

            Restaurante r=
            colecao.getRestaurantes()[i];

            if(r.getId()==id){

                pilha.inserir(r);

                break;
            }
        }
    }

    int q=sc.nextInt();

    sc.nextLine();

    for(int i=0;i<q;i++){

        String linha=sc.nextLine();

        String[] partes=
        linha.split(" ");

        if(partes[0].equals("I")){

            int id=
            Integer.parseInt(partes[1]);

            for(int j=0;j<colecao.getTamanho();j++){

                Restaurante r=
                colecao.getRestaurantes()[j];

                if(r.getId()==id){

                    pilha.inserir(r);

                    break;
                }
            }

        }else if(partes[0].equals("R")){

            Restaurante r=
            pilha.remover();

            if(r!=null)
                System.out.println("(R)"+r.getNome());
        }
    }

    for(Celula i=pilha.getTopo();i!=null;i=i.prox){

        System.out.println(
            i.elemento.formatar()
        );
    }

    sc.close();
}
}