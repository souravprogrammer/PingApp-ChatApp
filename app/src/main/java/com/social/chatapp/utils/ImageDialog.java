package com.social.chatapp.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.social.chatapp.R;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImageDialog extends AppCompatDialogFragment {


    private Drawable drawable;
    public ImageDialog( ) {
    }
    public ImageDialog(Drawable drawable) {
        this.drawable = drawable;
    }

    @NonNull
    @NotNull
    @Override
    public Dialog onCreateDialog(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fullimage, null);

        ImageView imageView = view.findViewById(R.id.image);
        if(drawable!=null)
        imageView.setImageDrawable(drawable);
        builder.setView(view);
        return builder.create();
    }
}
