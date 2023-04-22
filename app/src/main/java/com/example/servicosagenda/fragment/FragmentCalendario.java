package com.example.servicosagenda.fragment;

import static android.widget.Toast.LENGTH_LONG;

import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.servicosagenda.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCalendario#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCalendario extends Fragment implements CalendarView.OnDateChangeListener {

    private CalendarView calendarView;

    private int diaAtual, mesAtual, anoAtual;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentCalendario() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCalendario.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCalendario newInstance(String param1, String param2) {
        FragmentCalendario fragment = new FragmentCalendario();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendario, container, false);

        calendarView = (CalendarView) view.findViewById(R.id.calendarView);

        abterDataAtual();
        configurarCaledario();

        calendarView.setOnDateChangeListener(this);

        versionLollipop();

        // Inflate the layout for this fragment
        return view;
    }

    // ----------------------------------------- OBTER DATA ATUAL --------------------------------------

    private void abterDataAtual() {

        long dataLong = calendarView.getDate(); //123456465465213

        // obter o tipo de configuracao para formata data
        Locale locale = new Locale("pt", "BR");

        SimpleDateFormat dia = new SimpleDateFormat("dd", locale);
        SimpleDateFormat mes = new SimpleDateFormat("MM", locale);
        SimpleDateFormat ano = new SimpleDateFormat("yyyy", locale);

        //SimpleDateFormat data = new SimpleDateFormat("ddd/MM/yyyy", locale);

        //diaAtual = Integer.parseInt(dia.format(dataLong));
        diaAtual = 30;
        mesAtual = Integer.parseInt(mes.format(dataLong));
        anoAtual = Integer.parseInt(ano.format(dataLong));

        //Toast.makeText(getContext(), "Dia: " + diaAtual + "\nMes: " + mesAtual + "\nAno: " + anoAtual, LENGTH_LONG).show();
    }

    // ----------------------------------------- CONFIGURAR CALENDARIO --------------------------------------

    // metodo que vai limitar os dia e mes do ano
    private void configurarCaledario() {

        Calendar dataMin = configurarDataMin();
        Calendar dataMax = configurarDataMax();

        calendarView.setMinDate(dataMin.getTimeInMillis());
        calendarView.setMaxDate(dataMax.getTimeInMillis());
    }

    private Calendar configurarDataMax() {

        Calendar dataMax = Calendar.getInstance();
        Calendar calendar = Calendar.getInstance();

        int dayFinalCalendario = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if(diaAtual == dayFinalCalendario){ // usuario so vai cair nele se ele estiver no ultimo dia do mes
            dataMax.set(anoAtual, mesAtual, 15); // for ultimo dia mes ele vai exibir proximo dia do proximo mes ate dia 15
        }else{
            dataMax.set(anoAtual, mesAtual -1, dayFinalCalendario);
        }

        return dataMax;
    }

    private Calendar configurarDataMin() {

        Calendar dataMin = Calendar.getInstance();

        int dayInicialCalendario = 1;

        dataMin.set(anoAtual, mesAtual -1, dayInicialCalendario);
        return dataMin;
    }

    // ----------------------------------------- CLICK CALENDARIO --------------------------------------

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {

        // janeiro = 0
        // fevereiro = 1

        // somando +1 para mes fica numeracao correta, pq mes no CalendarView comeca com valor 0
        int mes = month + 1;

        dataSelecionado(dayOfMonth, mes, year);

        //Toast.makeText(getContext(), "Dia: " + diaAtual + "\nMes: " + mesAtual + "\nAno: " + anoAtual, LENGTH_LONG).show();
    }

    private void dataSelecionado(int dia, int mes, int ano) {

        Locale locale = new Locale("pt", "BR");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", locale);

        Calendar data = Calendar.getInstance();

        try{

            data.setTime(simpleDateFormat.parse(dia + "/" + mes + "/" + ano));

            boolean disponivelAgendamento;

            if(mes != mesAtual){
                disponivelAgendamento = true;
            }else{
                disponivelAgendamento = agendaDisponivel(data, dia);
            }

            // disponivelAgendamento == true
            if (disponivelAgendamento){
                Toast.makeText(getContext(), "Você pode agendar nesse dia", LENGTH_LONG).show();
            }

        }catch (ParseException e){
            e.printStackTrace();
        }
    }

    private boolean agendaDisponivel(Calendar data, int dia) {

        Calendar calendar = Calendar.getInstance();

        int diaFinelCalendario = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        if(diaFinelCalendario == diaAtual){

            Toast.makeText(getContext(), "Agendamento disponivel do dia 1 para frente", LENGTH_LONG).show();

            return false;
        }else if(data.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){

            Toast.makeText(getContext(), "Infelizmente não trabalhamos no domingo", LENGTH_LONG).show();

            return false;
        }else if(dia <= diaAtual){ // 25 <= 26

            Toast.makeText(getContext(), "Agendamento disponivel do dia " + (diaAtual + 1) + " para frente.", LENGTH_LONG).show();

            return false;
        }else{

            return true;
        }
    }

    // ----------------------------------------- VERSAO ANTIGAS ANDROID --------------------------------------

    private void versionLollipop(){

        int versao = Build.VERSION.SDK_INT; // pegando versao atual do android

        if(versao <= Build.VERSION_CODES.LOLLIPOP){
            WindowManager windowManager = (WindowManager)getActivity().getSystemService(getActivity().WINDOW_SERVICE);

            Display display = windowManager.getDefaultDisplay();

            Point size = new Point();
            display.getSize(size);

            int width = size.x;

            if(width == 480){

                // tamanho calendario no celular com resolucao 480
                calendarView.getLayoutParams().width = 730;
                calendarView.getLayoutParams().height = 500;
            }else{
                calendarView.getLayoutParams().width = 800;
                calendarView.getLayoutParams().height = 650;
            }else{
                // version mais novas do android. Nao preicisa configurar manualmente o calendario.
            }

        }
    }
}