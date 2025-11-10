package com.mobileapps.fintrack.adapter;

import android.app.Activity;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mobileapps.fintrack.R;
import com.mobileapps.fintrack.adapter.model.TipoTransacaoModel;
import com.mobileapps.fintrack.adapter.model.TransacaoModel;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TransacaoAdapter extends BaseAdapter {

    private ArrayList<TransacaoModel> transacaoModels;
    private Activity activity;

    public TransacaoAdapter(Activity activity, ArrayList<TransacaoModel> transacaoModels) {
        this.transacaoModels = transacaoModels;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return transacaoModels != null ? transacaoModels.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return transacaoModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = activity.getLayoutInflater().inflate(R.layout.item_list, parent, false);
        }

        TransacaoModel transacaoModel = transacaoModels.get(position);

        TextView nomeTransacao = convertView.findViewById(R.id.nomeDaTransacao);
        nomeTransacao.setText(transacaoModel.getNomeTransacao());

        TextView dataTransacao = convertView.findViewById(R.id.dataDaTransacao);
        dataTransacao.setText(transacaoModel.getDataTransacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        TextView valorTransacao = convertView.findViewById(R.id.valorTransacao);

        boolean isDespesa = transacaoModel.getTipoTransacao() == TipoTransacaoModel.DESPESA;
        String prefixo = isDespesa ? "-R$" : "+R$";

        valorTransacao.setText(String.format("%s%s", prefixo, transacaoModel.getValorTransacao()));
        valorTransacao.setTextColor(ContextCompat.getColor(activity,
                isDespesa ? R.color.red : R.color.green));

        boolean darkModeAtivo = (activity.getResources().getConfiguration().uiMode &
                Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        if (darkModeAtivo) {
            convertView.setBackgroundColor(ContextCompat.getColor(activity, R.color.background_escuro));
            nomeTransacao.setTextColor(ContextCompat.getColor(activity, R.color.texto_claro));
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(activity, R.color.background_claro));
            nomeTransacao.setTextColor(ContextCompat.getColor(activity, R.color.texto_escuro));
        }


        return convertView;
    }
}
