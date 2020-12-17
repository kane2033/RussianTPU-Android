package ru.tpu.russiantpu.main.fragments.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import ru.tpu.russiantpu.R;

/**
 * Фрагмент хранит кнопки,
 * ссылающие на соответствующие разделы портала ТПУ.
 */
public class TpuPortalFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View layoutInflater = inflater.inflate(R.layout.fragment_tpu_portal, container, false);

        layoutInflater.findViewById(R.id.button_personal_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLink("private");
            }
        });

        layoutInflater.findViewById(R.id.button_grades).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLink("study");
            }
        });

        layoutInflater.findViewById(R.id.button_scholarship).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLink("stipend");
            }
        });

        layoutInflater.findViewById(R.id.button_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLink("oplata");
            }
        });

        layoutInflater.findViewById(R.id.button_my_payments).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLink("oplata/mypayment");
            }
        });

        return layoutInflater;
    }

    private void goToLink(String url) {
        String baseUrl = "https://portal.tpu.ru/desktop/student/";
        url = baseUrl + url;
        try {
            requireActivity().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/*    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_personal_card:
                goToLink("private");
                break;
            case R.id.button_grades:
                goToLink("study");
                break;
            case R.id.button_scholarship:
                goToLink("stipend");
                break;
            case R.id.button_payment:
                goToLink("oplata");
                break;
            case R.id.button_my_payments:
                goToLink("oplata/mypayment");
                break;
        }
    }*/
}
