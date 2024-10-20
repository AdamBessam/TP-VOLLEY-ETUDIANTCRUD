package com.ensa.etudiantcrud.ui.gallery;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ensa.etudiantcrud.R;
import com.ensa.etudiantcrud.ui.EditEtudiantActivity;
import com.ensa.etudiantcrud.ui.EtudiantDetail;
import com.ensa.etudiantcrud.ui.adapter.EtudiantAdapter;
import com.ensa.etudiantcrud.ui.model.Etudiant;

import java.util.ArrayList;
import java.util.List;

public class GalleryFragment extends Fragment {

    private GalleryViewModel galleryViewModel;
    private EtudiantAdapter etudiantAdapter;
    private final Paint paint = new Paint();
    private final float textSize = 40;
    private List<Etudiant> allEtudiants = new ArrayList<>();
    private SearchView searchView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().invalidateOptionsMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycle_view);
        searchView = view.findViewById(R.id.search_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        galleryViewModel = new ViewModelProvider(this).get(GalleryViewModel.class);

        galleryViewModel.getEtudiants().observe(getViewLifecycleOwner(), new Observer<List<Etudiant>>() {
            @Override
            public void onChanged(List<Etudiant> etudiants) {
                allEtudiants = new ArrayList<>(etudiants);
                if (etudiantAdapter == null) {
                    etudiantAdapter = new EtudiantAdapter(getContext(), etudiants);
                    recyclerView.setAdapter(etudiantAdapter);
                } else {
                    etudiantAdapter.updateEtudiants(etudiants);
                }

                etudiantAdapter.setOnEtudiantClickListener(new EtudiantAdapter.OnEtudiantClickListener() {
                    @Override
                    public void onDoubleClick(Etudiant etudiant) {
                        Intent intent = new Intent(getContext(), EtudiantDetail.class);
                        intent.putExtra("name", etudiant.getNom());
                        intent.putExtra("surname", etudiant.getPrenom());
                        intent.putExtra("ville", etudiant.getVille());
                        intent.putExtra("sexe", etudiant.getSexe());
                        intent.putExtra("imageUrl", etudiant.getImageUrl());
                        startActivity(intent);
                    }
                });
            }
        });

        galleryViewModel.startFetchingEtudiants();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Etudiant etudiant = etudiantAdapter.getEtudiantAtPosition(position);

                if (direction == ItemTouchHelper.LEFT) {
                    galleryViewModel.deleteEtudiant(etudiant.getId());
                    etudiantAdapter.removeEtudiant(position);
                } else if (direction == ItemTouchHelper.RIGHT) {
                    Intent intent = new Intent(getContext(), EditEtudiantActivity.class);
                    intent.putExtra("etudiant_id", etudiant.getId());
                    intent.putExtra("name", etudiant.getNom());
                    intent.putExtra("surname", etudiant.getPrenom());
                    intent.putExtra("ville", etudiant.getVille());
                    intent.putExtra("sexe", etudiant.getSexe());
                    intent.putExtra("imageUrl", etudiant.getImageUrl());
                    startActivity(intent);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getHeight();

                    float textSize = Math.min(50, 20 + Math.abs(dX) / 10); // Max size 50, min size 20

                    // If swiping left
                    if (dX < 0) {
                        paint.setColor(Color.RED);
                        RectF background = new RectF(itemView.getRight() + dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                        c.drawRect(background, paint);

                        paint.setColor(Color.WHITE);
                        paint.setTextSize(textSize);
                        String text = "Supprimer";
                        float textWidth = paint.measureText(text);
                        float textX = itemView.getRight() - textWidth - 16 + dX / 2;
                        c.drawText(text, textX, itemView.getTop() + height / 2 + textSize / 4, paint);
                    } else if (dX > 0) {
                        paint.setColor(Color.BLUE);
                        RectF background = new RectF(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + dX, itemView.getBottom());
                        c.drawRect(background, paint);

                        paint.setColor(Color.WHITE);
                        paint.setTextSize(textSize);
                        String text = "Modifier";
                        float textWidth = paint.measureText(text);
                        float textX = itemView.getLeft() + 16 + dX / 2;
                        c.drawText(text, textX, itemView.getTop() + height / 2 + textSize / 4, paint);
                    }

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);

        // Set up the search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        galleryViewModel.startFetchingEtudiants();
    }

    @Override
    public void onStop() {
        super.onStop();
        galleryViewModel.stopFetchingEtudiants();
    }

    // Filter method to filter the student list based on the search query
    private void filter(String text) {
        List<Etudiant> filteredList = new ArrayList<>();
        for (Etudiant etudiant : allEtudiants) {
            if (etudiant.getNom().toLowerCase().contains(text.toLowerCase()) ||
                    etudiant.getPrenom().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(etudiant);
            }
        }
        etudiantAdapter.updateEtudiants(filteredList);
    }
}
