package com.unir.apiviacep;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private Button btnBuscar;
    private RecyclerView recyclerView;
    private TextInputEditText edtEntrada;
    private ArrayList<Endereco> enderecos;
    private Retrofit retrofit;
    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        radioGroup = findViewById(R.id.radioGroup);
        btnBuscar = findViewById(R.id.btnBuscar);
        recyclerView = findViewById(R.id.recyclerView);
        edtEntrada = findViewById(R.id.edtEntrada);
        enderecos = new ArrayList<>();
        String url = "https://viacep.com.br/ws/";
        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(enderecos);
        recyclerView.setAdapter(adapter);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!edtEntrada.getText().toString().isEmpty()){
                    if (radioGroup.getCheckedRadioButtonId() == R.id.radioButtonCep){
                        String cep = edtEntrada.getText().toString();
                        buscarEndereco(cep);
                    }else{
                        String logradouro = edtEntrada.getText().toString();
                        buscarEnderecos(logradouro);
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Informe o CEP ou parte do endereço", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void buscarEndereco(String cep){
        ViaCep viaCep = retrofit.create(ViaCep.class);
        Call<Endereco> call = viaCep.buscarEndereco(cep);
        call.enqueue(new Callback<Endereco>() {
            @Override
            public void onResponse(Call<Endereco> call, Response<Endereco> response) {
                if (response.isSuccessful()) {
                    Endereco endereco = response.body();
                    enderecos.clear();
                    enderecos.add(endereco);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(MainActivity.this, "CEP não encontrado", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Endereco> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao buscar endereço", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void buscarEnderecos(String logradouro){
        ViaCep viaCep = retrofit.create(ViaCep.class);
        Call<List<Endereco>> call = viaCep.buscarEnderecos(logradouro);
        call.enqueue(new Callback<List<Endereco>>() {
            @Override
            public void onResponse(Call<List<Endereco>> call, Response<List<Endereco>> response) {
                if (response.isSuccessful()){
                    List<Endereco> resultado = response.body();
                    enderecos.clear();
                    enderecos.addAll(resultado);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(MainActivity.this, "Endereço não encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Endereco>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao buscar endereços", Toast.LENGTH_SHORT).show();
            }
        });
    }
}