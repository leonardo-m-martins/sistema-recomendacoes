package br.sistema_recomendacoes.util;

public class Cronometro {
    private long inicio;
    private long fim;
    private long duracao;
    private double msDuracao;

    public Cronometro(){}

    public void start(){
        inicio = System.nanoTime();
    }

    public double stop(){
        fim = System.nanoTime();
        duracao = fim - inicio;
        msDuracao = duracao / 1_000_000.0;
        inicio = System.nanoTime();
        return msDuracao;
    }

    public void stop(String message){
        fim = System.nanoTime();
        duracao = fim - inicio;
        msDuracao = duracao / 1_000_000.0;
        System.out.println(message + msDuracao + " ms.");
        inicio = System.nanoTime();
    }
}
