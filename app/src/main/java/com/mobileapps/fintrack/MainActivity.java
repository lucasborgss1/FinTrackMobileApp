package com.mobileapps.fintrack;

import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mobileapps.fintrack.adapter.TransacaoAdapter;
import com.mobileapps.fintrack.adapter.model.MesesModel;
import com.mobileapps.fintrack.adapter.model.TipoTransacaoModel;
import com.mobileapps.fintrack.adapter.model.TransacaoModel;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private ListView listaTransacoes;
    private PieChart pieChart;
    private TransacaoAdapter adapter;
    private ArrayList<TransacaoModel> todasTransacoes;
    private Spinner spinnerMes;
    private boolean ignoreSpinnerEvents = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pieChart = findViewById(R.id.pieChartResumo);
        listaTransacoes = findViewById(R.id.listaTransacoes);
        spinnerMes = findViewById(R.id.spinnerMes);

        FloatingActionButton fab = findViewById(R.id.fabAdicionar);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetalheTransacaoActivity.class);
            startActivityForResult(intent, 1);
        });



        todasTransacoes = new ArrayList<>();
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,1), "Mercado", 20.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,10), "Salário", 1200.00, TipoTransacaoModel.RECEITA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,2), "Farmácia", 5.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,4), "Mensalidade Faculdade Lucas", 500.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,7), "Pagamento Aluguel", 4000.00, TipoTransacaoModel.RECEITA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,11,10), "Venda ML", 800.00, TipoTransacaoModel.RECEITA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,1), "Mercado", 120.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,10), "Salário", 1200.00, TipoTransacaoModel.RECEITA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,2), "Farmácia", 56.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,4), "Mensalidade Faculdade Julia", 3000.99, TipoTransacaoModel.DESPESA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,7), "Pagamento Aluguel", 4000.00, TipoTransacaoModel.RECEITA));
        todasTransacoes.add(new TransacaoModel(LocalDate.of(2025,10,10), "Venda ML", 100.00, TipoTransacaoModel.RECEITA));

        configurarSpinnerMes();
        ordenarPorData(todasTransacoes);

        LocalDate hoje = LocalDate.now();
        int mesAtual = hoje.getMonthValue();
        int anoAtual = hoje.getYear();

        spinnerMes.setSelection(mesAtual - 1);

        atualizarFiltro(mesAtual, anoAtual);

        adapter = new TransacaoAdapter(MainActivity.this, todasTransacoes);


        listaTransacoes.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listaTransacoes.setOnItemClickListener((parent, view, position, id) -> {
            TransacaoModel transacaoSelecionada = (TransacaoModel) parent.getItemAtPosition(position);
            Intent intent = new Intent(MainActivity.this, DetalheTransacaoActivity.class);
            intent.putExtra("transacao", transacaoSelecionada);
            intent.putExtra("posicao", position);
            startActivityForResult(intent, 2);
        });


        gerarGraficoCircular(todasTransacoes);
    }

    private void gerarGraficoCircular(ArrayList<TransacaoModel> transacoes) {

        float totalReceitas = 0f;
        float totalDespesas = 0f;

        for (TransacaoModel t : transacoes) {
            if (t.getTipoTransacao() == TipoTransacaoModel.RECEITA) {
                totalReceitas += t.getValorTransacao();
            } else {
                totalDespesas += t.getValorTransacao();
            }
        }

        float saldoFinal = totalReceitas - totalDespesas;

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(totalDespesas, "Despesas"));
        entries.add(new PieEntry(totalReceitas, "Receitas"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(new int[]{
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.green)
        });

        PieData data = new PieData(dataSet);
        data.setDrawValues(false);
        pieChart.setDrawEntryLabels(false);



        pieChart.setData(data);
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setHoleRadius(70f);
        pieChart.setTransparentCircleRadius(75f);
        pieChart.setCenterText(String.format("Saldo Mensal\nR$ %.2f", saldoFinal));
        pieChart.getLegend().setTextSize(14f);


        pieChart.setCenterTextSize(16f);
        pieChart.animateY(1000);
        pieChart.invalidate();


        boolean darkModeAtivo = (getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (darkModeAtivo) {
            pieChart.setHoleColor(Color.parseColor("#121212"));
            pieChart.setEntryLabelColor(Color.WHITE);
            pieChart.setCenterTextColor(Color.WHITE);
            pieChart.getLegend().setTextColor(Color.WHITE);
        } else {
            pieChart.setHoleColor(Color.WHITE);
            pieChart.setEntryLabelColor(Color.BLACK);
            pieChart.setCenterTextColor(Color.BLACK);
            pieChart.getLegend().setTextColor(Color.BLACK);
        }
    }

    private void configurarSpinnerMes() {
        ArrayList<MesesModel> meses = new ArrayList<>(Arrays.asList(MesesModel.values()));

        ArrayAdapter<MesesModel> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                meses
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMes.setAdapter(spinnerAdapter);

        spinnerMes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LocalDate hoje = LocalDate.now();
                int anoAtual = hoje.getYear();
                atualizarFiltro(position + 1, anoAtual);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }
    private void atualizarFiltro(int mes, int ano) {
        ArrayList<TransacaoModel> filtradas = filtrarPorMesEAno(todasTransacoes, mes, ano);
        spinnerMes.setSelection(mes - 1);
        gerarGraficoCircular(filtradas);

        ordenarPorData(todasTransacoes);
        adapter = new TransacaoAdapter(MainActivity.this, todasTransacoes);
        listaTransacoes.setAdapter(adapter);
    }

    private void ordenarPorData(ArrayList<TransacaoModel> lista) {
        lista.sort((t1, t2) -> t2.getDataTransacao().compareTo(t1.getDataTransacao()));
    }

    private ArrayList<TransacaoModel> filtrarPorMesEAno(ArrayList<TransacaoModel> todas, int mes, int ano) {
        ArrayList<TransacaoModel> filtradas = new ArrayList<>();

        for (TransacaoModel t : todas) {
            if (t.getDataTransacao().getMonthValue() == mes &&
                    t.getDataTransacao().getYear() == ano) {
                filtradas.add(t);
            }
        }

        return filtradas;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) return;

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Nova transação
            TransacaoModel nova = (TransacaoModel) data.getSerializableExtra("novaTransacao");
            if (nova != null) {
                todasTransacoes.add(nova);
                ordenarPorData(todasTransacoes);
                atualizarFiltro(nova.getDataTransacao().getMonthValue(), nova.getDataTransacao().getYear());
                adapter.notifyDataSetChanged();
            }
        }
        else if (requestCode == 2) {
            int posicao = data.getIntExtra("posicao", -1);

            // Edição
            if (resultCode == RESULT_OK && data.hasExtra("novaTransacao") && posicao != -1) {
                TransacaoModel editada = (TransacaoModel) data.getSerializableExtra("novaTransacao");
                todasTransacoes.set(posicao, editada);
                ordenarPorData(todasTransacoes);
                atualizarFiltro(editada.getDataTransacao().getMonthValue(), editada.getDataTransacao().getYear());
                adapter.notifyDataSetChanged();
            }

            // Exclusão
            if (resultCode == RESULT_FIRST_USER && posicao != -1) {
                todasTransacoes.remove(posicao);
                adapter.notifyDataSetChanged();
                atualizarFiltro(LocalDate.now().getMonthValue(), LocalDate.now().getYear());
                Toast.makeText(this, "Transação removida", Toast.LENGTH_SHORT).show();
            }
        }
    }



}