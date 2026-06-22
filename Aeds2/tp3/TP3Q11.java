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

    public long comp=0;
    public long mov=0;

    public CelulaDupla getUltimo(){
        return ultimo;
    }

    private int comparar(
        Restaurante a,
        Restaurante b){

        comp++;

        if(a.getAvaliacao()
        <b.getAvaliacao())
            return -1;

        if(a.getAvaliacao()
        >b.getAvaliacao())
            return 1;

        return a.getNome()
                .compareTo(
                b.getNome());
    }

    private CelulaDupla partition(
        CelulaDupla esq,
        CelulaDupla dir){

        Restaurante pivo=
        dir.elemento;

        CelulaDupla i=
        esq.ant;

        for(CelulaDupla j=esq;
            j!=dir;
            j=j.prox){

            if(comparar(
            j.elemento,
            pivo)<=0){

                i=(i==null)?
                esq:
                i.prox;

                Restaurante temp=
                i.elemento;

                i.elemento=
                j.elemento;

                j.elemento=
                temp;

                mov+=3;
            }
        }

        i=(i==null)?
        esq:
        i.prox;

        Restaurante temp=
        i.elemento;

        i.elemento=
        dir.elemento;

        dir.elemento=
        temp;

        mov+=3;

        return i;
    }

    private void quicksort(
        CelulaDupla esq,
        CelulaDupla dir){

        if(dir!=null &&
        esq!=dir &&
        esq!=dir.prox){

            CelulaDupla p=
            partition(esq,dir);

            quicksort(
            esq,p.ant);

            quicksort(
            p.prox,dir);
        }
    }

    public void ordenar(){

        quicksort(
        primeiro.prox,
        ultimo);
    }

    public ListaDupla(){

        primeiro=new CelulaDupla();
        ultimo=primeiro;
    }



    public void inserirFim(Restaurante x){

        CelulaDupla tmp=new CelulaDupla(x);

        ultimo.prox=tmp;
        tmp.ant=ultimo;

        ultimo=tmp;
    }


    public CelulaDupla getPrimeiro(){

        return primeiro;
    }


}

public class TP3Q11 {

    public static void main(String[] args)throws Exception{

    Scanner sc =
    new Scanner(System.in);

    ColecaoRestaurantes c=
    new ColecaoRestaurantes();

    c.carregarCsv(
    "/tmp/restaurantes.csv");

    ListaDupla lista=
    new ListaDupla();

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

                lista.inserirFim(r);

                break;
            }
        }
    }

    long inicio=
    System.currentTimeMillis();

    lista.ordenar();

    long fim=
    System.currentTimeMillis();

    for(CelulaDupla i=
        lista.getPrimeiro().prox;
        i!=null;
        i=i.prox){

        System.out.println(
        i.elemento.formatar());
    }

    PrintWriter log=
    new PrintWriter(
    "869882_quicksort_flexivel.txt");

    log.println(
    "869882\t"+
    lista.comp+"\t"+
    lista.mov+"\t"+
    ((fim-inicio)/1000.0));

    log.close();

    sc.close();
}
}