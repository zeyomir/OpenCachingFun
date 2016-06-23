package com.zeyomir.ocfun.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.storage.model.LogModel;
import com.zeyomir.ocfun.view.component.DividerItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CacheLogsFragment extends BaseFragment {
    private static final String CODE = "code";

    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.list)
    RecyclerView list;
    private LogsAdapter adapter;
    private List<LogModel> logModels;
    private String code;

    @Override
    protected void onPostInjection() {
        code = getArguments().getString(CODE);
        logModels = LogModel.getForCode(code);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setItemAnimator(new DefaultItemAnimator());
        list.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new LogsAdapter();
        list.setAdapter(adapter);
        refreshViews();
    }

    private void refreshViews() {
        if (logModels.size() > 0) {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fab)
    public void add() {
        Snackbar snackbar = Snackbar.make(mainView, R.string.snack_coming_soon, Snackbar.LENGTH_SHORT);
        ViewGroup group = (ViewGroup) snackbar.getView();
        group.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.accent));
        snackbar.show();
//        bus.post(new ShowAddLogEvent(code));
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_logs_list;
    }

    public static CacheLogsFragment create(String code) {
        CacheLogsFragment fragment = new CacheLogsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CODE, code);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class LogsAdapter extends RecyclerView.Adapter<LogsViewHolder> {
        private final IconicsDrawable found;
        private final IconicsDrawable notFound;
        private final IconicsDrawable comment;

        public LogsAdapter() {
            Context context = getContext();
            found = new IconicsDrawable(context)
                    .icon(GoogleMaterial.Icon.gmd_done)
                    .color(ContextCompat.getColor(context, R.color.log_type_found))
                    .sizeDp(18);
            notFound = new IconicsDrawable(context)
                    .icon(GoogleMaterial.Icon.gmd_remove_circle)
                    .color(ContextCompat.getColor(context, R.color.log_type_not_found))
                    .sizeDp(18);
            comment = new IconicsDrawable(context)
                    .icon(GoogleMaterial.Icon.gmd_question_answer)
                    .color(ContextCompat.getColor(context, R.color.log_type_comment))
                    .sizeDp(18);
        }

        @Override
        public LogsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflated = LayoutInflater.from(getContext()).inflate(R.layout.i_log, parent, false);
            LogsViewHolder viewHolder = new LogsViewHolder(inflated);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(LogsViewHolder holder, int position) {
            LogModel logModel = logModels.get(position);
            holder.username.setText(logModel.username);
            holder.date.setText(logModel.date);
            holder.comment.setText(Html.fromHtml(logModel.comment));

            switch (logModel.type) {
                case FOUND_IT:
                    holder.type.setImageDrawable(found);
                    break;
                case DID_NOT_FIND_IT:
                    holder.type.setImageDrawable(notFound);
                    break;
                default:
                case COMMENT:
                    holder.type.setImageDrawable(comment);
                    break;

            }
        }

        @Override
        public int getItemCount() {
            return logModels.size();
        }
    }

    public class LogsViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.icon)
        ImageView type;
        @BindView(R.id.name)
        TextView username;
        @BindView(R.id.comment)
        TextView comment;
        @BindView(R.id.date)
        TextView date;

        public LogsViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
