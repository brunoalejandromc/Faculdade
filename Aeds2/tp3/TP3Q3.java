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


public class TP3Q3 {

    static double comparacoes = 0;
    static double movimentacoes = 0;

    static int comparar(Restaurante a, Restaurante b){

        if(a.getAvaliacao() < b.getAvaliacao()) return -1;
        if(a.getAvaliacao() > b.getAvaliacao()) return 1;

        return a.getNome().compareTo(b.getNome());
    }

    static void swap(Restaurante[] array,int i,int j){

        Restaurante temp = array[i];
        array[i] = array[j];
        array[j] = temp;

        movimentacoes += 3;
    }

    static void quickSortParcial(Restaurante[] array,int esq,int dir,int k){

        int i = esq;
        int j = dir;

        Restaurante pivo =
        array[(esq+dir)/2];

        while(i<=j){

            while(true){

                comparacoes++;

                if(comparar(array[i],pivo)<0){
                    i++;
                }else{
                    break;
                }
            }

            while(true){

                comparacoes++;

                if(comparar(array[j],pivo)>0){
                    j--;
                }else{
                    break;
                }
            }

            if(i<=j){

                swap(array,i,j);

                i++;
                j--;
            }
        }

        if(esq<j){
            quickSortParcial(array,esq,j,k);
        }

        if(i<k && i<dir){
            quickSortParcial(array,i,dir,k);
        }
    }

    public static void main(String[] args)throws Exception{

        Scanner sc =
        new Scanner(System.in);

        ColecaoRestaurantes colecao =
        new ColecaoRestaurantes();

        colecao.carregarCsv("/tmp/restaurantes.csv");

        Restaurante[] selecionados =
        new Restaurante[1000];

        int tamSel=0;

        while(true){

            int id =
            Integer.parseInt(sc.nextLine());

            if(id==-1) break;

            for(int i=0;i<
            colecao.getTamanho();
            i++){

                Restaurante r =
                colecao.getRestaurantes()[i];

                if(r.getId()==id){

                    selecionados[tamSel++]=r;
                    break;
                }
            }
        }

        long inicio =
        System.nanoTime();

        quickSortParcial(
            selecionados,
            0,
            tamSel-1,
            10
        );

        long fim =
        System.nanoTime();

        double tempo =
        (fim-inicio)/1000000.0;

        for(int i=0;i<tamSel;i++){

            System.out.println(
            selecionados[i].formatar()
            );
        }

        PrintWriter log =
        new PrintWriter(
        "869882_quicksort_parcial.txt"
        );

        log.printf(
        Locale.US,
        "869882\t%.0f\t%.0f\t%.2f\n",
        comparacoes,
        movimentacoes,
        tempo
        );

        log.close();
        sc.close();
    }
}