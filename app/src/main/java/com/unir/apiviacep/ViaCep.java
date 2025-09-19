package com.unir.apiviacep;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ViaCep {

    @GET("{cep}/json/")
    Call<Endereco> buscarEndereco(@Path("cep") String cep);

    @GET("RO/Porto Velho/{logradouro}/json/")
    Call<List<Endereco>> buscarEnderecos(@Path("logradouro") String logradouro);
}
