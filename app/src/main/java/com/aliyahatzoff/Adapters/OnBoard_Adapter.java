package com.aliyahatzoff.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.aliyahatzoff.Models.OnBoardItem;
import com.aliyahatzoff.R;

import java.util.ArrayList;

public class OnBoard_Adapter extends PagerAdapter {

    public Context mContext;
    ArrayList<OnBoardItem> onBoardItems=new ArrayList<>();


    public OnBoard_Adapter(Context mContext, ArrayList<OnBoardItem> items) {
        this.mContext = mContext;
        this.onBoardItems = items;
    }

    @Override
    public int getCount() {
        return onBoardItems.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.activity_on_boarding1, container, false);

        OnBoardItem item=onBoardItems.get(position);
        ImageView imageView = (ImageView) itemView.findViewById(R.id.work);
        imageView.setImageResource(item.getImageID());
        TextView tv_title=(TextView)itemView.findViewById(R.id.tv_header);
        tv_title.setText(item.getTitle());

        TextView tv_content=(TextView)itemView.findViewById(R.id.tv_desc);
        tv_content.setText(item.getDescription());

        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

}