package com.optimustechproject2017.payment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.citrus.sdk.payment.NetbankingOption;
import com.citruspay.graphics.BitmapCallBack;
import com.optimustechproject2017.R;

import java.util.ArrayList;

/**
 * Created by salil on 27/2/15.
 */
final class NetbankingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity mActivity = null;
    private ArrayList<NetbankingOption> mNetbankingOptionList = null;

    public NetbankingAdapter(Activity activity, ArrayList<NetbankingOption> netbankingOptionList) {
        this.mActivity = activity;
        this.mNetbankingOptionList = netbankingOptionList;
    }

    public void setNetbankingOptionList(ArrayList<NetbankingOption> netbankingOptionList) {
        this.mNetbankingOptionList = netbankingOptionList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.netbanking_item, viewGroup, false);

        return new NetbankingViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int index) {

        final NetbankingViewHolder itemHolder = (NetbankingViewHolder) viewHolder;
        final NetbankingOption netbankingOption = getItem(index);

        if (netbankingOption != null) {


            itemHolder.mImageView.setImageDrawable(null);
            netbankingOption.getBitmap(new BitmapCallBack() {
                @Override
                public void onBitmapReceived(Bitmap bitmap) {
                    itemHolder.mImageView.setImageBitmap(bitmap);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemHolder.mTextView.getLayoutParams();
                    //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.RIGHT_OF, itemHolder.mImageView.getId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.addRule(RelativeLayout.END_OF, itemHolder.mImageView.getId());
                    }

                    params.addRule(RelativeLayout.CENTER_VERTICAL, itemHolder.mImageView.getId());
                    itemHolder.mTextView.setLayoutParams(params); //causes layout update
                    itemHolder.mTextView.setPadding(25, 0, 0, 0);
                    itemHolder.mTextView.setText(netbankingOption.getBankName());

                }

                @Override
                public void onBitmapFailed(Bitmap bitmap) {

                    itemHolder.mImageView.setImageBitmap(bitmap);

                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) itemHolder.mTextView.getLayoutParams();
                    //params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    params.addRule(RelativeLayout.RIGHT_OF, itemHolder.mImageView.getId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        params.addRule(RelativeLayout.END_OF, itemHolder.mImageView.getId());
                    }

                    params.addRule(RelativeLayout.CENTER_VERTICAL, itemHolder.mImageView.getId());
                    itemHolder.mTextView.setLayoutParams(params); //causes layout update
                    itemHolder.mTextView.setPadding(25, 0, 0, 0);
                    itemHolder.mTextView.setText(netbankingOption.getBankName());
                }
            });

            // itemHolder.mTextView.setText(netbankingOption.getBankName());
        }
    }

    @Override
    public int getItemCount() {
        if (mNetbankingOptionList != null) {
            return mNetbankingOptionList.size();
        } else {
            return 0;
        }
    }

    /**
     * Called by RecyclerView when it stops observing this Adapter.
     *
     * @param recyclerView The RecyclerView instance which stopped observing this adapter.
     * @see #onAttachedToRecyclerView(RecyclerView)
     */
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);

        if (mNetbankingOptionList != null) {
            mNetbankingOptionList.clear();
        }

        mNetbankingOptionList = null;
    }

    private NetbankingOption getItem(int position) {
        if (mNetbankingOptionList != null && position >= 0 && position < mNetbankingOptionList.size()) {
            return mNetbankingOptionList.get(position);
        }

        return null;
    }

    public static class NetbankingViewHolder extends RecyclerView.ViewHolder {
        //   final TextView txtBankName;

        final ImageView mImageView;

        final TextView mTextView;

        final RelativeLayout mrelativeLayout;

        public NetbankingViewHolder(View v) {
            super(v);
            //  txtBankName = (TextView) v.findViewById(android.R.id.text1);

            mImageView = (ImageView) v.findViewById(R.id.iconbank);
            mTextView = (TextView) v.findViewById(R.id.txtbankname);
            mrelativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
        }
    }
}