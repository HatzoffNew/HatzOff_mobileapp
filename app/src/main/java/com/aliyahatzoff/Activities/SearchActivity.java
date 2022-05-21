package com.aliyahatzoff.Activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.UserSearchAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.User;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    EditText searchText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        recyclerView=findViewById(R.id.rv_user);
        searchText=findViewById(R.id.et_search);
        loaddata("All");
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loaddata(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    void loaddata(String search)
    {
        Functions.showLoadingDialog(SearchActivity.this);
        if(search==null || search.isEmpty())
            search="All";
        Common.getHatzoffApi().searchuser(search).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                try{System.out.println(response.errorBody().string());
                    Functions.cancelLoading();
                }catch (Exception e){}
                if(response.isSuccessful())
                {
                    Functions.cancelLoading();
                    recyclerView.setAdapter(new UserSearchAdapter(response.body(),getApplicationContext()));
                }
            }
            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Functions.cancelLoading();
            }
        });
    }
}