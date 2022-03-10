package com.exam.natour.UI.View.Setting;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.exam.natour.Model.AuthUser;
import com.exam.natour.databinding.FragmentSettingBinding;

public class SettingFragment extends Fragment {

    private SettingViewModel settingViewModel;
    private FragmentSettingBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        settingViewModel =
                new ViewModelProvider(this).get(SettingViewModel.class);

        binding = FragmentSettingBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView username = binding.username;
        final TextView userEmail = binding.userEmail;
        final ImageButton logoutButton = binding.logoutButton;

        username.setText(settingViewModel.authUser().getName());
        userEmail.setText(settingViewModel.authUser().getEmail());

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SettingFragment", "Premuto tasto per eseguire logout");
                settingViewModel.logout(view.getContext());
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}