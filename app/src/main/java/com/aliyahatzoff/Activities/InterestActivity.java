package com.aliyahatzoff.Activities;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.aliyahatzoff.Adapters.InterestAdapter;
import com.aliyahatzoff.Api.Common;
import com.aliyahatzoff.Models.Interest;
import com.aliyahatzoff.R;
import com.aliyahatzoff.utils.Functions;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
public class InterestActivity extends AppCompatActivity {
RecyclerView recyclerView;
InterestAdapter interestAdapter;
List<Interest> interestList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interested_categories_activities);
        recyclerView=findViewById(R.id.rv_interest);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        findViewById(R.id.next).setOnClickListener(v -> {
            String interest=interestAdapter.getselectedItem();
            CheckBox term=findViewById(R.id.chk_term);
            if(!term.isChecked())
            {
                Functions.Show_Alert(InterestActivity.this,"Hatzoof","Please accept terms and conditions");
                return;

            }
            if(interest==null)
            {
                Functions.Show_Alert(InterestActivity.this,"Hatzoof","Please Select Your Interest");
                return;
            }
            else
            {
                Functions.showLoadingDialog(InterestActivity.this);
                Common.getHatzoffApi().addinterest(interest).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if(response.isSuccessful())
                        {
                            Functions.cancelLoading();
                            finish();

                            startActivity(new Intent(InterestActivity.this,Congratulation.class));
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Functions.cancelLoading();
                    }
                });
            }

        });
        updatelist();
        loaddata();
    }
    void loaddata()
    {
        interestAdapter=new InterestAdapter(interestList,this);
            recyclerView.setAdapter(interestAdapter);
    }
    void updatelist()
    {
        interestList.add(new Interest(getResources().getString(R.string.singing),false));
        interestList.add(new Interest(getResources().getString(R.string.acting),false));
        interestList.add(new Interest(getResources().getString(R.string.mimicry),false));
        interestList.add(new Interest(getResources().getString(R.string.dancing),false));
        interestList.add(new Interest(getResources().getString(R.string.standup_comedy),false));
        interestList.add(new Interest(getResources().getString(R.string.directing),false));
        interestList.add(new Interest(getResources().getString(R.string.cinematography),false));
        interestList.add(new Interest(getResources().getString(R.string.story_telling),false));
        interestList.add(new Interest(getResources().getString(R.string.content_creator),false));
        interestList.add(new Interest(getResources().getString(R.string.dialog_writing),false));
        interestList.add(new Interest(getResources().getString(R.string.stuntman),false));
        interestList.add(new Interest(getResources().getString(R.string.creative_expert),false));
    }
}