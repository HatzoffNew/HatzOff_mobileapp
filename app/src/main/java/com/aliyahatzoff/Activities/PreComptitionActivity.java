package com.aliyahatzoff.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.ComptiotionAdapter;
import com.aliyahatzoff.Models.ComptitionModel;
import com.aliyahatzoff.R;

import java.util.ArrayList;
import java.util.List;

public class PreComptitionActivity extends AppCompatActivity implements View.OnClickListener {
RecyclerView recyclerView;
List<ComptitionModel> comptitionModelList=new ArrayList<>();
ImageView back_iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_comptition);
        recyclerView=findViewById(R.id.type_comp);
        back_iv=findViewById(R.id.back_iv);
        back_iv.setOnClickListener(this::onClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(PreComptitionActivity.this,RecyclerView.VERTICAL,false));
        addData();
    }
    private void addData() {
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.singing),100,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.acting),150,R.drawable.acting));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.mimicry),120,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.dancing),180,R.drawable.dancong));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.standup_comedy),200,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.directing),100,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.cinematography),100,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.story_telling),75,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.content_creator),175,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.stuntman),300,R.drawable.stunt));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.dialog_writing),100,R.drawable.singing));
        comptitionModelList.add(new ComptitionModel(getResources().getString(R.string.creative_expert),100,R.drawable.singing));
        ComptiotionAdapter comptiotionAdapter=new ComptiotionAdapter(this,comptitionModelList);
        recyclerView.setAdapter(comptiotionAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_iv:
                finish();
                break;
        }
    }
}