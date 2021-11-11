package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.myapplication.models.Profesores;

import java.util.zip.Inflater;

public class ViewPagerItemFragment extends Fragment {

    //Widgets needed
    private ImageView myImageView;
    private TextView mName,mMail,mOffice,mInfo;

    //Vars
    private Profesores mProfe;

    public static ViewPagerItemFragment getInstance(Profesores profesor) {
        ViewPagerItemFragment fragment = new ViewPagerItemFragment();
        if(profesor != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("profesor",profesor);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.profesor_layout,container,false);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            mProfe = getArguments().getParcelable("profesor");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        myImageView = view.findViewById(R.id.imgprof);
        mName = view.findViewById(R.id.nameprof);
        mMail = view.findViewById(R.id.mailprof);
        mOffice = view.findViewById(R.id.officeprof);
        mInfo = view.findViewById(R.id.infoprof);

        init();
    }

    private void init(){
        if(mProfe != null){
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background);
            Glide.with(getActivity())
                    .setDefaultRequestOptions(options)
                    .load(mProfe.getImagen())
                    .into(myImageView);
            mName.setText(mProfe.getNombre());
            mMail.setText(mProfe.getCorreo());
            mOffice.setText(mProfe.getDespacho());
            mInfo.setText(mProfe.getAddinfo());
        }
    }
}
