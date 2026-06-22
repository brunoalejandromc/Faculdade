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


class CelulaDupla{

    public Restaurante elemento;
    public CelulaDupla prox;
    public CelulaDupla ant;

    public CelulaDupla(){
        this(null);
    }

    public CelulaDupla(Restaurante x){

        elemento=x;
        prox=null;
        ant=null;
    }
}

class ListaDupla{

    private CelulaDupla primeiro;
    private CelulaDupla ultimo;


    public int tamanho(){

    int tam=0;

    for(CelulaDupla i=primeiro.prox;i!=null;i=i.prox){
        tam++;
    }

    return tam;
}

public void inserir(Restaurante x,int pos) throws Exception{

    int tam=tamanho();

    if(pos<0 || pos>tam)
        throw new Exception("Erro");

    else if(pos==0)
        inserirInicio(x);

    else if(pos==tam)
        inserirFim(x);

    else{

        CelulaDupla i=primeiro;

        for(int j=0;j<pos;j++,i=i.prox);

        CelulaDupla tmp=new CelulaDupla(x);

        tmp.ant=i;
        tmp.prox=i.prox;

        i.prox.ant=tmp;
        i.prox=tmp;
    }
}

    public Restaurante remover(int pos) throws Exception{

        int tam=tamanho();

        if(primeiro==ultimo || pos<0 || pos>=tam)
            throw new Exception("Erro");

        else if(pos==0)
            return removerInicio();

        else if(pos==tam-1)
            return removerFim();

        CelulaDupla i=primeiro.prox;

        for(int j=0;j<pos;j++,i=i.prox);

        Restaurante resp=i.elemento;

        i.ant.prox=i.prox;
        i.prox.ant=i.ant;

        return resp;
    }


    public ListaDupla(){

        primeiro=new CelulaDupla();
        ultimo=primeiro;
    }

    public void inserirInicio(Restaurante x){

        CelulaDupla tmp=new CelulaDupla(x);

        tmp.prox=primeiro.prox;
        tmp.ant=primeiro;

        if(primeiro==ultimo){

            ultimo=tmp;

        }else{

            primeiro.prox.ant=tmp;
        }

        primeiro.prox=tmp;
    }

    public void inserirFim(Restaurante x){

        CelulaDupla tmp=new CelulaDupla(x);

        ultimo.prox=tmp;
        tmp.ant=ultimo;

        ultimo=tmp;
    }

    public Restaurante removerInicio() throws Exception{

        if(primeiro==ultimo)
            throw new Exception("Erro");

        CelulaDupla tmp=primeiro.prox;

        Restaurante resp=tmp.elemento;

        primeiro.prox=tmp.prox;

        if(tmp==ultimo){

            ultimo=primeiro;

        }else{

            tmp.prox.ant=primeiro;
        }

        return resp;
    }

    public Restaurante removerFim() throws Exception{

        if(primeiro==ultimo)
            throw new Exception("Erro");

        Restaurante resp=ultimo.elemento;

        ultimo=ultimo.ant;

        ultimo.prox=null;

        return resp;
    }

    public CelulaDupla getPrimeiro(){

        return primeiro;
    }


}



public class TP3Q8 {

    public static void main(String[] args)throws Exception{
        Scanner sc = new Scanner(System.in);

    ColecaoRestaurantes colecao =
    new ColecaoRestaurantes();

    colecao.carregarCsv("/tmp/restaurantes.csv");

    ListaDupla lista = new ListaDupla();

    
    while(true){

        int id = sc.nextInt();

        if(id==-1)
            break;

        for(int i=0;i<colecao.getTamanho();i++){

            Restaurante r=
            colecao.getRestaurantes()[i];

            if(r.getId()==id){

                lista.inserirFim(r);
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

        
        if(partes[0].equals("II")){

            int id=
            Integer.parseInt(partes[1]);

            for(int j=0;j<colecao.getTamanho();j++){

                Restaurante r=
                colecao.getRestaurantes()[j];

                if(r.getId()==id){

                    lista.inserirInicio(r);
                    break;
                }
            }
        }

       
        else if(partes[0].equals("IF")){

            int id=
            Integer.parseInt(partes[1]);

            for(int j=0;j<colecao.getTamanho();j++){

                Restaurante r=
                colecao.getRestaurantes()[j];

                if(r.getId()==id){

                    lista.inserirFim(r);
                    break;
                }
            }
        }

        
        else if(partes[0].equals("I*")){

            int pos=
            Integer.parseInt(partes[1]);

            int id=
            Integer.parseInt(partes[2]);

            for(int j=0;j<colecao.getTamanho();j++){

                Restaurante r=
                colecao.getRestaurantes()[j];

                if(r.getId()==id){

                    lista.inserir(r,pos);

                    break;
                }
            }
        }

        
        else if(partes[0].equals("RI")){

            Restaurante r=
            lista.removerInicio();

            System.out.println(
                "(R)"+r.getNome()
            );
        }

       
        else if(partes[0].equals("RF")){

            Restaurante r=
            lista.removerFim();

            System.out.println(
                "(R)"+r.getNome()
            );
        }

      
        else if(partes[0].equals("R*")){

            int pos=
            Integer.parseInt(partes[1]);

            Restaurante r=
            lista.remover(pos);

            System.out.println(
                "(R)"+r.getNome()
            );
        }
    }

    for(CelulaDupla i=
        lista.getPrimeiro().prox;
        i!=null;
        i=i.prox){

        System.out.println(
            i.elemento.formatar()
        );
    }

    sc.close();  
    }
}