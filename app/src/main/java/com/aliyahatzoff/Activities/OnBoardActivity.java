package com.aliyahatzoff.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.aliyahatzoff.Adapters.OnBoard_Adapter;
import com.aliyahatzoff.Models.OnBoardItem;
import com.aliyahatzoff.R;

import java.util.ArrayList;

public class OnBoardActivity extends AppCompatActivity {

    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    private ViewPager onboard_pager;

    private OnBoard_Adapter mAdapter;

    private Button btn_get_started;

    int previous_pos=0;


    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.on_board_fragment);

      //  btn_get_started = (Button) findViewById(R.id.btn_get_started);
        onboard_pager = (ViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        final Button started = (Button)findViewById(R.id.started);
        started.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent in = new Intent(OnBoardActivity.this, LoginActivity.class);
                startActivity(in);
            }
        });
      findViewById(R.id.signin).setOnClickListener(v -> {
          finish();
          Intent in = new Intent(OnBoardActivity.this,SignupActivity.class);
          startActivity(in);

      });
        loadData();

        mAdapter = new OnBoard_Adapter(this,onBoardItems);
        onboard_pager.setAdapter(mAdapter);
        onboard_pager.setCurrentItem(0);
        onboard_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                // Change the current position intimation

                for (int i = 0; i < dotsCount; i++) {
                    dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardActivity.this, R.drawable.onboarding_indicator_active));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(OnBoardActivity.this, R.drawable.onboarding_indicator_inactive));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        setUiPageViewController();

    }

    // Load data into the viewpager

    public void loadData()
    {

        int[] header = {R.string.welcome, R.string.welcome};
        int[] desc = { R.string.ob_desc2, R.string.ob_desc3};
        int[] imageId = {R.drawable.boardimg, R.drawable.working};

        for(int i=0;i<imageId.length;i++)
        {
            OnBoardItem item=new OnBoardItem();
            item.setImageID(imageId[i]);
            item.setTitle(getResources().getString(header[i]));
            item.setDescription(getResources().getString(desc[i]));

            onBoardItems.add(item);
        }
    }

    // Button bottomUp animation


    // Button Topdown animation





    // setup the
    private void setUiPageViewController() {

        dotsCount = mAdapter.getCount();
        dots = new ImageView[dotsCount];

        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(OnBoardActivity.this, R.drawable.onboarding_indicator_inactive));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(6, 0, 6, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(OnBoardActivity.this, R.drawable.onboarding_indicator_active));
    }


}