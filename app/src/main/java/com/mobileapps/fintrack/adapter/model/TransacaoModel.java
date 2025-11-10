package com.mobileapps.fintrack.adapter.model;

import java.io.Serializable;
import java.time.LocalDate;

public class TransacaoModel implements Serializable {
    private LocalDate dataTransacao;
    private String nomeTransacao;
    private Double valorTransacao;
    private TipoTransacaoModel tipoTransacao;

    public TransacaoModel(LocalDate dataTransacao, String nomeTransacao, Double valorTransacao, TipoTransacaoModel tipoTransacao) {
        this.dataTransacao = dataTransacao;
        this.nomeTransacao = nomeTransacao;
        this.valorTransacao = valorTransacao;
        this.tipoTransacao = tipoTransacao;
    }

    public LocalDate getDataTransacao() {
        return dataTransacao;
    }

    public void setDataTransacao(LocalDate dataTransacao) {
        this.dataTransacao = dataTransacao;
    }

    public String getNomeTransacao() {
        return nomeTransacao;
    }

    public void setNomeTransacao(String nomeTransacao) {
        this.nomeTransacao = nomeTransacao;
    }

    public Double getValorTransacao() {
        return valorTransacao;
    }

    public void setValorTransacao(Double valorTransacao) {
        this.valorTransacao = valorTransacao;
    }

    public TipoTransacaoModel getTipoTransacao() {
        return tipoTransacao;
    }

    public void setTipoTransacao(TipoTransacaoModel tipoTransacao) {
        this.tipoTransacao = tipoTransacao;
    }
}
