package com.example.servicosagenda.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.servicosagenda.R;

import java.util.ArrayList;

public class AgendamentoServicoActivity extends AppCompatActivity {

    private ArrayList<String> data = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agendamento_servico);

        data = getIntent().getStringArrayListExtra("data");

        Toast.makeText(getBaseContext(), "Ano: " + data.get(2) + "\nMes: " + data.get(1)
                + "\nDia: " + data.get(0) + "\nHorario: " + data.get(3), Toast.LENGTH_LONG).show();
    }
}