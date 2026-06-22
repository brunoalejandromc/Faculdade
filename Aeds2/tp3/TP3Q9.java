import java.util.*;

class Celula{

    public int elemento;
    public Celula sup,inf,esq,dir;

    public Celula(){
        this(0);
    }

    public Celula(int x){
        elemento=x;
        sup=inf=esq=dir=null;
    }
}

class Matriz{

    private Celula inicio;
    private int linha,coluna;

    public Matriz(int linha,int coluna){

        this.linha=linha;
        this.coluna=coluna;

        montarMatriz();
    }

private void montarMatriz(){

    inicio = new Celula(0);

    Celula linhaAnterior = null;

    for(int i = 0; i < linha; i++){

        Celula linhaAtual = null;
        Celula anterior = null;

        for(int j = 0; j < coluna; j++){

            Celula nova = new Celula(0);

            if(i == 0 && j == 0){
                inicio = nova;
            }

          
            if(anterior != null){
                anterior.dir = nova;
                nova.esq = anterior;
            }

           
            if(j == 0){
                linhaAtual = nova;
            }

            
            if(linhaAnterior != null){

                Celula acima = linhaAnterior;

                for(int k=0; k<j; k++){
                    acima = acima.dir;
                }

                acima.inf = nova;
                nova.sup = acima;
            }

            anterior = nova;
        }

        linhaAnterior = linhaAtual;
    }
}


    public void ler(Scanner sc){

        Celula lin=inicio;

        for(int i=0;i<linha;i++){

            Celula col=lin;

            for(int j=0;j<coluna;j++){

                col.elemento=sc.nextInt();

                col=col.dir;
            }

            lin=lin.inf;
        }
    }

    public void mostrarDiagonalPrincipal() throws Exception{

        if(linha != coluna)
            return;

        Celula i=inicio;

        while(i!=null){

            System.out.print(i.elemento);

            if(i.inf!=null && i.dir!=null){
                System.out.print(" ");
                i=i.inf.dir;
            }
            else{
                i=null;
            }
        }

        System.out.println();
    }

    public void mostrarDiagonalSecundaria() throws Exception{

        if(linha != coluna)
            return;

        Celula i=inicio;

        while(i.dir!=null)
            i=i.dir;

        while(i!=null){

            System.out.print(i.elemento);

            if(i.inf!=null && i.esq!=null){
                System.out.print(" ");
                i=i.inf.esq;
            }
            else{
                i=null;
            }
        }

        System.out.println();
    }

    public Matriz somar(Matriz m){

        Matriz resp=
        new Matriz(linha,coluna);

        Celula aLin=inicio;
        Celula bLin=m.inicio;
        Celula rLin=resp.inicio;

        while(aLin!=null){

            Celula a=aLin;
            Celula b=bLin;
            Celula r=rLin;

            while(a!=null){

                r.elemento=
                a.elemento+b.elemento;

                a=a.dir;
                b=b.dir;
                r=r.dir;
            }

            aLin=aLin.inf;
            bLin=bLin.inf;
            rLin=rLin.inf;
        }

        return resp;
    }

    public Matriz multiplicar(Matriz m){

        Matriz resp=
        new Matriz(linha,m.coluna);

        Celula rLinha=resp.inicio;
        Celula aLinha=inicio;

        for(int i=0;i<linha;i++){

            Celula rCol=rLinha;

            for(int j=0;j<m.coluna;j++){

                int soma=0;

                Celula a=aLinha;
                Celula b=m.inicio;

                for(int k=0;k<j;k++)
                    b=b.dir;

                for(int k=0;k<coluna;k++){

                    soma+=
                    a.elemento*b.elemento;

                    a=a.dir;
                    b=b.inf;
                }

                rCol.elemento=soma;

                rCol=rCol.dir;
            }

            aLinha=aLinha.inf;
            rLinha=rLinha.inf;
        }

        return resp;
    }

    public void mostrar(){

        Celula lin=inicio;

        while(lin!=null){

            Celula col=lin;

            while(col!=null){

                System.out.print(col.elemento);

                if(col.dir!=null)
                    System.out.print(" ");

                col=col.dir;
            }

            System.out.println();
            
            lin=lin.inf;
            }
    }
}

public class TP3Q9{

   public static void main(String[] args) throws Exception {

        Scanner sc = new Scanner(System.in);

        int casos = sc.nextInt();

        for(int i=0;i<casos;i++){

            int linhas = sc.nextInt();
            int colunas = sc.nextInt();

            Matriz m1 = new Matriz(linhas,colunas);
            m1.ler(sc);

            Matriz m2 = new Matriz(linhas,colunas);
            m2.ler(sc);

            m1.mostrarDiagonalPrincipal();
            m2.mostrarDiagonalSecundaria();

            Matriz soma = m1.somar(m2);
            soma.mostrar();

            Matriz mult = m1.multiplicar(m2);
            mult.mostrar();
        }

        sc.close();
    }
}




