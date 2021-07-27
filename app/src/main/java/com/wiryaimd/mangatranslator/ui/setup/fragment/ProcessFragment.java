package com.wiryaimd.mangatranslator.ui.setup.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.nl.translate.TranslateRemoteModel;
import com.wiryaimd.mangatranslator.R;
import com.wiryaimd.mangatranslator.model.InfoModel;
import com.wiryaimd.mangatranslator.model.SelectedModel;
import com.wiryaimd.mangatranslator.ui.setup.SetupActivity;
import com.wiryaimd.mangatranslator.ui.setup.SetupViewModel;
import com.wiryaimd.mangatranslator.ui.setup.adapter.InfoAdapter;
import com.wiryaimd.mangatranslator.ui.setup.adapter.SelectAdapter;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.InfoDialog;
import com.wiryaimd.mangatranslator.ui.setup.fragment.dialog.ProcessDialog;
import com.wiryaimd.mangatranslator.util.Const;
import com.wiryaimd.mangatranslator.util.LanguagesData;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProcessFragment extends Fragment {

    private static final String TAG = "ProcessFragment";

    private SetupViewModel setupViewModel;

    private Button btnrequire, btnprocess;
    private ProgressBar loading;
    private ImageView imgalert;

    private TextView tvondevice, tvusingapi;

    private Spinner spinFrom, spinTo;

    private ViewPager viewPager;
    private RecyclerView recyclerView;

    private List<SelectedModel> selectedList = new ArrayList<>();
    private List<String> downloadedList = new ArrayList<>();

    private TranslateRemoteModel translateRemoteModel;
    private RemoteModelManager remoteModelManager;
    private DownloadConditions downloadConditions;

    private int flagFrom = 0, flagTo = 0;
    private boolean sucFrom = false, sucTo = false;

    private boolean downloadedFrom = true;
    private boolean downloadedTo = true;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_process, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        remoteModelManager = RemoteModelManager.getInstance();

        btnrequire = view.findViewById(R.id.processlang_downloadlanguages);
        btnprocess = view.findViewById(R.id.processlang_processtranslate);
        loading = view.findViewById(R.id.processlang_loadingdownload);
        imgalert = view.findViewById(R.id.processlang_imgalert);
        spinFrom = view.findViewById(R.id.processlang_spinfrom);
        spinTo = view.findViewById(R.id.processlang_spinto);
        recyclerView = view.findViewById(R.id.processlang_recyclerview);
        viewPager = view.findViewById(R.id.processlang_viewpager);
        tvondevice = view.findViewById(R.id.processlang_engine_ondevice);
        tvusingapi = view.findViewById(R.id.processlang_engine_api);

        // // oke ternyatod data yang tersimpan pada viewmodel berbeda ya tod tiap activity nya tod kentod
        setupViewModel = new ViewModelProvider(requireActivity()).get(SetupViewModel.class);

        selectedList = setupViewModel.getSelectedModelLiveData().getValue();
        if (selectedList == null || selectedList.size() == 0){
            requireActivity().finish();
            Toast.makeText(setupViewModel.getApplication(), "Cannot find data on fragment", Toast.LENGTH_SHORT).show();
            return;
        }

        List<InfoModel> infoList = new ArrayList<>();
        infoList.add(new InfoModel("For non-latin languages", "Currently, for non-latin comic language need to select the text manually, if you want automatic translate you can translate the latin comic e.g Manga in english, france etc"));
        infoList.add(new InfoModel("Download Models", "Before translate Manga/Manhwa/Manhua you need to download model languages which languages selected for translating process"));
        InfoAdapter infoAdapter = new InfoAdapter(setupViewModel.getApplication(), infoList);

        viewPager.setAdapter(infoAdapter);
        viewPager.setPadding(24, 0, 160, 0);

        SelectAdapter selectAdapter = new SelectAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(setupViewModel.getApplication()));
        recyclerView.setAdapter(selectAdapter);
        selectAdapter.setSelectedList(selectedList);

        ArrayAdapter<String> spinnerFromAdapter = new ArrayAdapter<>(setupViewModel.getApplication(), R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_from);
        spinFrom.setAdapter(spinnerFromAdapter);
        ArrayAdapter<String> spinnerToAdapter = new ArrayAdapter<>(setupViewModel.getApplication(), R.layout.item_spinner, R.id.spinner_language, LanguagesData.flag_to);
        spinTo.setAdapter(spinnerToAdapter);

        remoteModelManager = RemoteModelManager.getInstance();
        downloadConditions = new DownloadConditions.Builder().build();

        remoteModelManager.getDownloadedModels(TranslateRemoteModel.class).addOnSuccessListener(new OnSuccessListener<Set<TranslateRemoteModel>>() {
            @Override
            public void onSuccess(@NonNull @NotNull Set<TranslateRemoteModel> translateRemoteModels) {
                for (TranslateRemoteModel model : translateRemoteModels){
                    downloadedList.add(model.getLanguage());
                    Log.d(TAG, "onSuccess: lang: " + model.getLanguage());
                }
            }
        });

        setupViewModel.getFlagFromLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                checkRequireDownload(integer, 0);
                if (!downloadedFrom || !downloadedTo){
                    btnrequire.setVisibility(View.VISIBLE);
                    imgalert.setVisibility(View.VISIBLE);
                }else{
                    btnrequire.setVisibility(View.GONE);
                    imgalert.setVisibility(View.GONE);
                }
            }
        });

        setupViewModel.getFlagToLiveData().observe(getViewLifecycleOwner(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                checkRequireDownload(integer, 1);
                if (!downloadedFrom || !downloadedTo){
                    btnrequire.setVisibility(View.VISIBLE);
                    imgalert.setVisibility(View.VISIBLE);
                }else{
                    btnrequire.setVisibility(View.GONE);
                    imgalert.setVisibility(View.GONE);
                }
            }
        });

        spinFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupViewModel.getFlagFromLiveData().setValue(i);
                flagFrom = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinTo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setupViewModel.getFlagToLiveData().setValue(i);
                flagTo = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        tvondevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getTeLiveData().setValue(SetupViewModel.TranslateEngine.ON_DEVICE);;
                tvondevice.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                tvusingapi.setBackgroundColor(Color.WHITE);
            }
        });

        tvusingapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupViewModel.getTeLiveData().setValue(SetupViewModel.TranslateEngine.USING_API);;
                tvusingapi.setBackground(ContextCompat.getDrawable(setupViewModel.getApplication(), R.drawable.custom_2));
                tvondevice.setBackgroundColor(Color.WHITE);
            }
        });

        btnprocess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setupViewModel.getFlagFromLiveData().getValue() == null ||
                        setupViewModel.getFlagToLiveData().getValue() == null ||
                        setupViewModel.getFlagFromLiveData().getValue() == 0 ||
                        setupViewModel.getFlagToLiveData().getValue() == 0){
                    Toast.makeText(setupViewModel.getApplication(), "Please select languages", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (downloadedFrom && downloadedTo){
                    new ProcessDialog().show(getParentFragmentManager(), "PROCESS_FRAGMENT_SETUP");
                }else {
                    new InfoDialog("Download Language Required", "You will download required language before start translating", false);
                }
            }
        });

        btnrequire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (flagTo == 0 && flagFrom == 0){
                    return;
                }
                sucFrom = false;
                sucTo =  false;

                btnprocess.setEnabled(false);
                imgalert.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);

                if (flagFrom == flagTo && !downloadedFrom){

                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                        }
                    });
                    return;
                }

                if (!downloadedFrom && !downloadedTo){
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            sucFrom = true;
                            if (sucFrom && sucTo){
                                btnrequire.setVisibility(View.GONE);
                                loading.setVisibility(View.GONE);
                                btnprocess.setEnabled(true);
                            }
                        }
                    });
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_to[flagTo]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            sucTo = true;
                            if (sucFrom && sucTo){
                                btnrequire.setVisibility(View.GONE);
                                loading.setVisibility(View.GONE);
                                btnprocess.setEnabled(true);
                            }
                        }
                    });
                }else if(!downloadedFrom && flagFrom != 0){
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_from[flagFrom]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                        }
                    });
                }else if(!downloadedTo && flagTo != 0){
                    translateRemoteModel = new TranslateRemoteModel.Builder(LanguagesData.flag_id_to[flagTo]).build();
                    remoteModelManager.download(translateRemoteModel, downloadConditions).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(@NonNull @NotNull Void unused) {
                            btnrequire.setVisibility(View.GONE);
                            loading.setVisibility(View.GONE);
                            btnprocess.setEnabled(true);
                        }
                    });
                }
            }
        });

    }

    public void checkRequireDownload(int pos, int type){

        if (pos == 0 && type == 0){
            downloadedFrom = true;
            return;
        }else if(pos == 0 && type == 1){
            downloadedTo = true;
            return;
        }

        String data;
        if (type == 0) {
            data = LanguagesData.flag_code_from[pos];
        } else{
            data = LanguagesData.flag_code[pos];
        }

        for (String lang : downloadedList) {
            if (data.equalsIgnoreCase(lang)) {
                if (type == 0) {
                    downloadedFrom = true;
                } else {
                    downloadedTo = true;
                }
                return;
            }
        }

        if (type == 0) {
            downloadedFrom = false;
        }else {
            downloadedTo = false;
        }
    }
}
