package com.example.servicosagenda.fragment;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.servicosagenda.R;
import com.example.servicosagenda.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentHome extends Fragment implements View.OnClickListener {

    private ImageView imgView;
    private ProgressBar progressBar;

    private CardView cardView_Numero, cardView_address;

    private TextView txtView_Info, txtView_ValorServico, txtView_NumeroContato;

    private FirebaseDatabase database;
    private DatabaseReference reference;
    private ValueEventListener valueEventListener;

    private String latitude, longitude;

    public static boolean offlineFirebase = false;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentHome newInstance(String param1, String param2) {
        FragmentHome fragment = new FragmentHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        database = FirebaseDatabase.getInstance();

        iniciarModoOfflineFirebase();

        //buscando caminho do bd
        reference = database.getReference().child("DB").child("Home").child("Dados");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imgView = (ImageView) view.findViewById(R.id.imgView_Home);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar_Home);

        cardView_Numero = (CardView) view.findViewById(R.id.cardView_Home_Contato);
        cardView_address = (CardView) view.findViewById(R.id.cardView_Home_Maps);

        txtView_Info = (TextView) view.findViewById(R.id.txtView_Home_Info);
        txtView_ValorServico = (TextView) view.findViewById(R.id.txtView_Home_Valor);
        txtView_NumeroContato = (TextView) view.findViewById(R.id.txtView_Home_NumeroContato);

        cardView_Numero.setOnClickListener(this);
        cardView_address.setOnClickListener(this);

        // Inflate the layout for this fragment
        return view;
    }

    private void iniciarModoOfflineFirebase(){

        try {
            if(!offlineFirebase){
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                offlineFirebase = true;
            }else{ //esta offline

            }

        }catch (Exception e){

        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.cardView_Home_Contato:
                //Toast.makeText(getContext(), "CardView Numero de Telefone", Toast.LENGTH_LONG).show();

                clickContato();

            break;

            case R.id.cardView_Home_Maps:

                //Toast.makeText(getContext(), "CardView Numero do Mapa", Toast.LENGTH_LONG).show();

                clickMaps();

            break;
        }
    }

    private void clickMaps() {

        if(!latitude.isEmpty() && !longitude.isEmpty()){

            // verificando se tem internet
            if(Util.statusInternet_MoWi(getContext())){
                opemAppress();
            }else{
                Toast.makeText(getContext(), "Endereço da Empresa não Indisponivel", Toast.LENGTH_LONG).show();
            }

        }else{
            Toast.makeText(getContext(), "Erro de Conexao com a Internet", Toast.LENGTH_LONG).show();
        }
    }

    private void clickContato() {

        String numero = txtView_NumeroContato.getText().toString();

        if(!numero.isEmpty()){
            dialogContato(numero);
        }else{
            Toast.makeText(getContext(), "Numero de Contato Indisponivel", Toast.LENGTH_LONG).show();
        }
    }

    //--------------------------------------- ACAO DE DIALOG CONTATOS WHATSAPP E LIGACAO----------------------------------------------

    private void dialogContato(String numero){

        //(855) 98457-7457
        // replace = 855984577457

        // removendo da string contato caracteres
        String contato = numero.replace(" ", "".replace("-", "")).replace("(", "")
                                            .replace(")", "");

        StringBuffer numeroContato = new StringBuffer(contato);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()).setTitle("Entrar em Contato")
                .setMessage("O que você gostaria de Fazer?")
                .setPositiveButton("WhatsApp", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        entrarContatoWhatsApp(numeroContato);
                    }
                }).setNegativeButton("Ligar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        entrarContatoLigar(numeroContato);
                    }
                });

        builder.show();
    }

    private void entrarContatoWhatsApp(StringBuffer numero) {

        //numero = 855984577457

        try {

            // formatando numero
            numero.deleteCharAt(0); //55984577457
            numero.deleteCharAt(2); //5584577457

            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
            intent.putExtra("jid", PhoneNumberUtils.stripSeparators("55" + numero + "@s.whatsapp.net"));

            startActivity(intent);

        }catch (Exception e){
            Toast.makeText(getContext(), "Erro - Verifique se o Whatsapp esta instalado no seu celular.", Toast.LENGTH_LONG).show();
        }

    }

    private void entrarContatoLigar(StringBuffer numero) {

        //numero = 855984577457

        Uri uri = Uri.parse("tel: " + numero);

        // instaciando acao de ligacao
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);

    }

    //--------------------------------------- ACAO DE CLICK ABRIR ENDERECO ----------------------------------------------

    private void opemAppress(){

        String url = "https://www.google.com/maps/search/?api=1&query=" + latitude + longitude;

        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));

        startActivity(intent);
    }

    //---------------------------------------OUVINTE FIREBASE--------------------------------------------------

    private void ouvinte(){

        if(valueEventListener == null){

            valueEventListener = new ValueEventListener(){

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot){

                    if(dataSnapshot.exists()){

                        // pegando as referencia dos valores no bd do firebase
                        String imagemUrl = dataSnapshot.child("imagemUrl").getValue(String.class);
                        String info = dataSnapshot.child("informacao").getValue(String.class);
                        String latitude = dataSnapshot.child("latitude").getValue(String.class);
                        String longitude = dataSnapshot.child("longitude").getValue(String.class);
                        String numeroContato = dataSnapshot.child("numeroContato").getValue(String.class);
                        String valorServico = dataSnapshot.child("valorServico").getValue(String.class);

                        Log.i("ouvinte latitude", "ouvinte latitude: " + latitude);

                        if(!info.isEmpty() && !valorServico.isEmpty()){
                            updateDados(imagemUrl, info, latitude, longitude, numeroContato, valorServico);
                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError){

                }
            };

            reference.addValueEventListener(valueEventListener);
        }
    }

    private void updateDados(String imagemUrl, String info, String latitudeFirebase, String longitudeFirebase, String numeroContato, String valorServico){

        txtView_Info.setText(info);
        txtView_ValorServico.setText(valorServico);
        txtView_NumeroContato.setText(numeroContato);

        latitude = latitudeFirebase;
        longitude = longitudeFirebase;

        Glide.with(getContext()).asBitmap().load(imagemUrl).listener(new RequestListener<Bitmap>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {

                Toast.makeText(getContext(), "Erro ao realizar Donwload de imagem", Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);

                return false;
            }

            @Override
            public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target,
                                                        DataSource dataSource, boolean isFirstResource) {

                progressBar.setVisibility(View.GONE);

                return false;
            }

        }).into(imgView);

    }

    //---------------------------------------CICLO DE VIDA FRAGMENT--------------------------------------------

    @Override
    public void onStart(){
        super.onStart();

        ouvinte();
    }

    @Override
    public void onDestroy() {

        if(valueEventListener != null){
            reference.removeEventListener(valueEventListener);
            valueEventListener = null;
        }

        super.onDestroy();
    }
}