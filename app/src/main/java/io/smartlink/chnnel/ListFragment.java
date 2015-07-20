package io.smartlink.chnnel;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class ListFragment extends Fragment {

    public ListFragment() {
        super();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        RecyclerView view = new RecyclerView(container.getContext());
        view.setHasFixedSize(true);
        view.setLayoutManager(new LinearLayoutManager(container.getContext()));

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView v = (RecyclerView) view;
        v.setAdapter(new Adapter(view.getContext()));



    }

    public void update() {

    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private int background;
        private int itemHeight;
        private int textPadding;
        private ViewGroup.LayoutParams lp;

        JSONArray channels = ((MainActivity) getActivity()).getChannels();

        public Adapter(Context context) {
            TypedValue v = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, v, true);
            background = v.resourceId;
            itemHeight = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 64.f, context.getResources().getDisplayMetrics());
            textPadding = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 12.f, context.getResources().getDisplayMetrics()
            );
            lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup container, int viewType) {
            TextView tv = new TextView(container.getContext());
            tv.setClickable(true);
            tv.setBackgroundResource(background);
            tv.setLayoutParams(lp);
            tv.setMinHeight(itemHeight);
            tv.setGravity(Gravity.CENTER_VERTICAL);
            tv.setPadding(textPadding, textPadding, textPadding, textPadding);

            return new ItemHolder(tv);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof ItemHolder) {
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(), DetailActivity.class);
                        startActivity(i);
                    }
                });

                try {
                    JSONObject obj = (JSONObject)channels.get(position);
                    ((ItemHolder) holder).setText( obj.getString("name"));

                    System.out.print("NAME : " + obj.getString("name"));

                } catch(Throwable t) {

                }

            }
        }

        @Override
        public int getItemCount() {
            return channels.length();
        }

        class ItemHolder extends RecyclerView.ViewHolder {

            public ItemHolder(View itemView) {
                super(itemView);
            }

            public void setText(String str) {
                ((TextView)itemView).setText(str);
            }
        }
    }


}
