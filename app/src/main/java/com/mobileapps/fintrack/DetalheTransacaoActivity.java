package com.mobileapps.fintrack;

import static android.view.View.VISIBLE;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.mobileapps.fintrack.adapter.model.TipoTransacaoModel;
import com.mobileapps.fintrack.adapter.model.TransacaoModel;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class DetalheTransacaoActivity extends AppCompatActivity {

    private EditText edtDescricao, edtValor, edtData;
    private TextView tituloPagina;
    private Spinner spinnerTipo;
    private Button btnSalvar, btnExcluir;
    private TransacaoModel transacaoEditando = null;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_transacao);

        edtDescricao = findViewById(R.id.edtDescricao);
        edtValor = findViewById(R.id.edtValor);
        edtData = findViewById(R.id.edtData);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnSalvar = findViewById(R.id.btnSalvar);
        btnExcluir = findViewById(R.id.btnExcluir);
        tituloPagina = findViewById(R.id.titulo);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                new String[]{"Receita", "Despesa"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapter);

        edtData.setOnClickListener(v -> abrirDatePicker());

        if (getIntent().hasExtra("transacao")) {
            transacaoEditando = (TransacaoModel) getIntent().getSerializableExtra("transacao");
            preencherCamposEdicao();
        }

        tituloPagina.setText(transacaoEditando == null ? "Nova Transação" : "Editar Transação");


        btnSalvar.setOnClickListener(v -> salvar());
        btnExcluir.setOnClickListener(v -> excluir());
    }

    private void abrirDatePicker() {
        final Calendar c = Calendar.getInstance();
        int ano = c.get(Calendar.YEAR);
        int mes = c.get(Calendar.MONTH);
        int dia = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            LocalDate dataSelecionada = LocalDate.of(year, month + 1, dayOfMonth);
            edtData.setText(dataSelecionada.format(formatter));
        }, ano, mes, dia).show();
    }

    private void preencherCamposEdicao() {
        edtDescricao.setText(transacaoEditando.getNomeTransacao());
        edtValor.setText(String.valueOf(transacaoEditando.getValorTransacao()));
        edtData.setText(transacaoEditando.getDataTransacao().format(formatter));
        spinnerTipo.setSelection(
                transacaoEditando.getTipoTransacao() == TipoTransacaoModel.RECEITA ? 0 : 1
        );
        btnExcluir.setVisibility(VISIBLE);
    }

    private void salvar() {
        String descricao = edtDescricao.getText().toString().trim();
        String valorStr = edtValor.getText().toString().trim();
        String dataStr = edtData.getText().toString().trim();

        if (descricao.isEmpty() || valorStr.isEmpty() || dataStr.isEmpty()) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        double valor = Double.parseDouble(valorStr);
        TipoTransacaoModel tipo = spinnerTipo.getSelectedItemPosition() == 0 ?
                TipoTransacaoModel.RECEITA : TipoTransacaoModel.DESPESA;

        TransacaoModel nova = new TransacaoModel(
                LocalDate.parse(dataStr, formatter),
                descricao,
                valor,
                tipo
        );

        Intent resultIntent = new Intent();
        resultIntent.putExtra("novaTransacao", nova);

        // 🧩 Se era edição, devolve também a posição
        if (transacaoEditando != null) {
            resultIntent.putExtra("posicao", getIntent().getIntExtra("posicao", -1));
        }

        setResult(RESULT_OK, resultIntent);
        Toast.makeText(this, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void excluir() {
        if (transacaoEditando != null) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("posicao", getIntent().getIntExtra("posicao", -1));
            setResult(RESULT_FIRST_USER, resultIntent);
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
