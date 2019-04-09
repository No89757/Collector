
package com.horizon.collector.unsplash.ui;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.horizon.base.ui.BaseAdapter;
import com.horizon.base.widget.FlowImageView;
import com.horizon.collector.R;
import com.horizon.collector.common.ExtraKey;
import com.horizon.collector.common.PhotoDetailActivity;
import com.horizon.collector.unsplash.suorce.UPhoto;
import com.horizon.doodle.DiskCacheStrategy;
import com.horizon.doodle.Doodle;

import java.util.List;

public class UPhotoAdapter extends BaseAdapter<UPhoto, UPhotoAdapter.UPhotoHolder> {

    public UPhotoAdapter(Context context, List<UPhoto> data, boolean loadMoreFlag) {
        super(context, data, loadMoreFlag);
    }

    @Override
    protected UPhotoHolder getItemHolder(ViewGroup parent) {
        return new UPhotoHolder(inflate(R.layout.item_flow, parent));
    }

    @Override
    protected void bindHolder(UPhoto item, int position,UPhotoHolder holder) {
        holder.flowIv.setSourceSize(item.width, item.height);
        holder.flowIv.requestLayout();

        int desWidth;
        if(holder.flowIv.getWidth() > 0){
            desWidth = holder.flowIv.getWidth();
        }else {
            Resources resources = mContext.getResources();
            int margin = resources.getDimensionPixelSize(R.dimen.flow_item_margin);
            int width = resources.getDisplayMetrics().widthPixels;
            desWidth = (width - 6 * margin) / 3;
        }

        float rate = (float)item.height / (float)item.width;
        int desHeight = desWidth > 0 ? Math.round(desWidth * rate) : 0;

        String url = item.getPhotoURL(desWidth);

        Doodle.load(url)
                .host(getHost())
                .override(desWidth, desHeight)
                .placeholder(0)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.flowIv);
    }

    class UPhotoHolder extends RecyclerView.ViewHolder {
        private FlowImageView flowIv;

        public UPhotoHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toDetailActivity(getAdapterPosition());
                }
            });
            flowIv = itemView.findViewById(R.id.flow_iv);
        }

        private void toDetailActivity(int position) {
            if (position >= 0 && position < mData.size()) {
                Intent intent = new Intent(mContext, PhotoDetailActivity.class);
                intent.putExtra(ExtraKey.DETAIL_URL, mData.get(position).getRawURL());
                mContext.startActivity(intent);
            }
        }


    }
}
