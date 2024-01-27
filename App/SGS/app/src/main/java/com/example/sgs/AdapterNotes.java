package com.example.sgs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AdapterNotes extends RecyclerView.Adapter<AdapterNotes.ViewHolder> {
    private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    private ArrayList<modelNotes> arrNotes = new ArrayList<>();
    private CollectionReference cRef = fstore.collection("Notes");

    public AdapterNotes(ArrayList<modelNotes> arrNotes) {
        this.arrNotes = arrNotes;
    }

    @NonNull
    @Override
    public AdapterNotes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_notes,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterNotes.ViewHolder holder, int position) {
        holder.txtNoteName.setText(arrNotes.get(position).getName());
        holder.txtNote.setText(arrNotes.get(position).getContaint());

        holder.cardNote.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                deleteGoal(position,v);

                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return arrNotes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView txtNoteName,txtNote;
        CardView cardNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNoteName = itemView.findViewById(R.id.txtNoteName);
            txtNote = itemView.findViewById(R.id.txtNote);
            cardNote = itemView.findViewById(R.id.cardNote);

        }
    }
    private void deleteGoal(int position,View v) {
        AlertDialog.Builder delete = new AlertDialog.Builder(v.getContext());
        delete.setTitle("Delete Goal");
        delete.setMessage("Do you want to remove this Goal");
        delete.setIcon(R.drawable.baseline_delete_24);
        delete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String docId = arrNotes.get(position).getDocRef().getId();
                DocumentReference dref =cRef.document(docId);
                Log.d("Doc_ref", String.valueOf(dref));
                dref.delete();
                arrNotes.remove(position);
                notifyItemRemoved(position);
            }
        }); delete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        delete.show();
    }
}
