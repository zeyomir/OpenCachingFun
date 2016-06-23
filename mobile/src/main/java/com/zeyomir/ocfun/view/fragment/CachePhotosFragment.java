package com.zeyomir.ocfun.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.zeyomir.ocfun.R;
import com.zeyomir.ocfun.storage.model.ImageModel;
import com.zeyomir.ocfun.view.activity.MediaActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CachePhotosFragment extends BaseFragment {
    private static final String CODE = "code";
    @BindView(R.id.empty)
    View empty;
    @BindView(R.id.list)
    RecyclerView list;
    private List<ImageModel> imageModels;
    private ImagesAdapter adapter;
    private Picasso picasso;

    @Override
    protected void onPostInjection() {
        String code = getArguments().getString(CODE);
        imageModels = ImageModel.getForCode(code);
        picasso = Picasso.with(getContext());

        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(getContext()));
        list.setItemAnimator(new DefaultItemAnimator());
        adapter = new ImagesAdapter();
        list.setAdapter(adapter);
        refreshViews();
    }

    private void refreshViews() {
        if (imageModels.size() > 0) {
            empty.setVisibility(View.GONE);
            list.setVisibility(View.VISIBLE);
        } else {
            empty.setVisibility(View.VISIBLE);
            list.setVisibility(View.GONE);
        }
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.f_images_list;
    }

    public static CachePhotosFragment create(String code) {
        CachePhotosFragment fragment = new CachePhotosFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CODE, code);
        fragment.setArguments(bundle);
        return fragment;
    }

    public class ImagesAdapter extends RecyclerView.Adapter<ImagesViewHolder> {
        private View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagesViewHolder child = (ImagesViewHolder) list.getChildViewHolder(v);
                Intent intent = MediaActivity.getIntent(getContext(), child.code, child.name);
                startActivity(intent);
            }
        };

        @Override
        public ImagesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflated = LayoutInflater.from(getContext()).inflate(R.layout.i_image, parent, false);
            ImagesViewHolder viewHolder = new ImagesViewHolder(inflated);
            inflated.setOnClickListener(clickListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ImagesViewHolder holder, int position) {
            ImageModel imageModel = imageModels.get(position);
            holder.name = imageModel.name;
            holder.code = imageModel.code;
            holder.caption.setText(imageModel.caption);
            if (imageModel.isSpoiler) {
                holder.spoiler.setVisibility(View.VISIBLE);
            } else {
                holder.spoiler.setVisibility(View.INVISIBLE);
                picasso.load(fileManager.get().getImageFile(imageModel.code, imageModel.name)).fit().centerCrop().into(holder.thumb);
            }
        }

        @Override
        public int getItemCount() {
            return imageModels.size();
        }
    }

    public class ImagesViewHolder extends RecyclerView.ViewHolder {
        String name;
        String code;
        @BindView(R.id.thumb)
        ImageView thumb;
        @BindView(R.id.caption)
        TextView caption;
        @BindView(R.id.spoiler)
        TextView spoiler;

        public ImagesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
