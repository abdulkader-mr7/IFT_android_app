package com.tamilquran.ift.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tamilquran.ift.AppContainer;
import com.tamilquran.ift.R;
import com.tamilquran.ift.controller.NavigationController;
import com.tamilquran.ift.controller.SuraController;
import com.tamilquran.ift.model.entity.SuraHeader;
import com.tamilquran.ift.model.preference.PreferencesRepository;
import com.tamilquran.ift.model.repository.QuranRepository;
import com.tamilquran.ift.view.activity.MainActivity;
import com.tamilquran.ift.view.adapter.SuraListAdapter;

public class SuraIndexFragment extends BaseFragment {

    private SuraController suraController;
    private NavigationController navigationController;
    private SuraListAdapter adapter;
    private PreferencesRepository.DisplaySettings displaySettings;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        QuranRepository repository = AppContainer.getInstance(requireContext()).getQuranRepository();
        suraController = new SuraController(repository);
        navigationController = new NavigationController(requireActivity());
        displaySettings = repository.getPreferencesRepository().getDisplaySettings();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sura_index, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.suraRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SuraListAdapter(
                header -> navigationController.openSuraDetail(header.suraNo, 0),
                displaySettings.tamilFontSize
        );
        recyclerView.setAdapter(adapter);
        suraController.loadSuraHeadersAsync(headers -> {
            if (isAdded()) {
                adapter.submitList(headers);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.quicksarch) {
            showQuickGotoDialog();
            return true;
        }
        if (item.getItemId() == R.id.gotobookmark) {
            openLastBookmark();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showQuickGotoDialog() {
        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.alert_dialog_text_entry, null, false);
        EditText suraInput = dialogView.findViewById(R.id.surano);
        EditText ayahInput = dialogView.findViewById(R.id.ayahno);

        new AlertDialog.Builder(requireContext())
                .setTitle("Enter Sura & Ayah No.")
                .setView(dialogView)
                .setPositiveButton(R.string.ok, (dialog, which) -> {
                    SuraController.QuickGotoResult result = suraController.validateQuickGoto(
                            suraInput.getText().toString(),
                            ayahInput.getText().toString()
                    );
                    if (!result.valid) {
                        Toast.makeText(requireContext(), result.error, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    navigationController.openSuraDetail(result.sura, result.ayah);
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void openLastBookmark() {
        int[] lastRead = suraController.getLastRead();
        if (lastRead[0] == 0) {
            Toast.makeText(requireContext(), R.string.no_bookmark, Toast.LENGTH_LONG).show();
            return;
        }
        navigationController.openSuraDetail(lastRead[0], lastRead[1]);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).setToolbarTitle("ஸூரா அட்டவணை");
        }
    }
}
