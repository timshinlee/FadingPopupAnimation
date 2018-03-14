package com.timshinlee.fadingpopupanimation;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        FadingPopupAnimationHelper fadingPopupAnimation =
                new FadingPopupAnimationHelper.Builder(this).build();

        final RecyclerView recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(new Adapter(this, fadingPopupAnimation));

    }

    static class Adapter extends RecyclerView.Adapter<Holder> {
        private Activity mActivity;
        private FadingPopupAnimationHelper mHelper;

        Adapter(Activity activity, FadingPopupAnimationHelper helper) {

            mActivity = activity;
            mHelper = helper;
        }

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
            return new Holder(view);
        }

        @Override
        public void onBindViewHolder(final Holder holder, int position) {
            holder.mText.setText("item " + position);
            mHelper.addFadingPopupAnimation(new CheckableWrapper(holder.mCheckbox) {
                @Override
                public boolean isCheck() {
                    return holder.mCheckbox.isChecked();
                }
            });
        }

        @Override
        public int getItemCount() {
            return 20;
        }
    }

    static class Holder extends RecyclerView.ViewHolder {

        private final TextView mText;
        private final CheckBox mCheckbox;

        public Holder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text);
            mCheckbox = itemView.findViewById(R.id.checkbox);
        }
    }
}
