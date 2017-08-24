package com.optimustechproject2017.payment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.citrus.sdk.Callback;
import com.citrus.sdk.CitrusClient;
import com.citrus.sdk.TransactionResponse;
import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.classes.CitrusException;
import com.citrus.sdk.payment.MasterPassOption;
import com.citrus.sdk.payment.PaymentType;
import com.citrus.sdk.response.CitrusError;
import com.citruspay.graphics.AssetsHelper;
import com.citruspay.graphics.BitmapCallBack;
import com.optimustechproject2017.R;

public class MasterPassFragment extends Fragment {


    TextView txtPay = null;
    ImageView imageView = null;
    private Amount amount = null;
    private Utils.PaymentType paymentType = null;

    public MasterPassFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static MasterPassFragment newInstance(Utils.PaymentType paymentType, Amount amount) {
        MasterPassFragment fragment = new MasterPassFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("paymentType", paymentType);
        bundle.putParcelable("amount", amount);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            paymentType = (Utils.PaymentType) bundle.getSerializable("paymentType");
            amount = bundle.getParcelable("amount");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.fragment_masterpass, container, false);
        txtPay = (TextView) returnView.findViewById(R.id.btnMasterpass);

        imageView = (ImageView) returnView.findViewById(R.id.imageView);

        BitmapCallBack bitmapCallBack = new BitmapCallBack() {
            @Override
            public void onBitmapReceived(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Bitmap bitmap) {
                imageView.setImageBitmap(bitmap);
            }
        };
        txtPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payUsingMasterPass();
            }
        });

        CitrusClient.getInstance(getActivity()).getMasterPassResource(AssetsHelper.MASTERPASS.BUTTON, bitmapCallBack);
        return returnView;

    }

    public void payUsingMasterPass() {
        try {
            MasterPassOption masterPassOption = new MasterPassOption();
            PaymentType.PGPayment pgPayment = new PaymentType.PGPayment(amount, Constants.BILL_URL, masterPassOption, null);
            CitrusClient.getInstance(getActivity()).simpliPay(pgPayment, new Callback<TransactionResponse>() {
                @Override
                public void success(TransactionResponse transactionResponse) {
                    Toast.makeText(getActivity(), transactionResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(CitrusError error) {
                    Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (CitrusException e) {
            e.printStackTrace();
        }

    }


}
