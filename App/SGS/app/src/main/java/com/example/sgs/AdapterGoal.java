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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class AdapterGoal extends RecyclerView.Adapter<AdapterGoal.ViewHolder> {

    ArrayList<Model_Goal> arrGoal = new ArrayList<>();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    CollectionReference cRef = fstore.collection("Goals");
    String due;
    public AdapterGoal(ArrayList<Model_Goal> arrGoal) {
        this.arrGoal = arrGoal;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.goal_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.txtGoal.setText(arrGoal.get(position).getGoal());
        holder.txtDescription.setText(arrGoal.get(position).getDescription());
        holder.txtDate.setText(arrGoal.get(position).getDate());
        holder.txtDue.setText(calculateDueDate(position));

        holder.cardGoal.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                deleteGoal(position,v);
                return true;
            }
        });

    }


    @Override
    public int getItemCount() {
        return arrGoal.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtGoal,txtDescription,txtDate,txtDue;
        CardView cardGoal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtGoal = itemView.findViewById(R.id.txtGoal);
            txtDescription = itemView.findViewById(R.id.txtDescription);
            txtDate = itemView.findViewById(R.id.txtDate);
            cardGoal = itemView.findViewById(R.id.cardGoal);
            txtDue = itemView.findViewById(R.id.txtDue);




        }
    }
    private String calculateDueDate(int position) {
        String date = arrGoal.get(position).getDate().toString();

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        try{
            Date inputDate = sdf.parse(date);
            Date currentDate = new Date();

            long timeDiff = inputDate.getTime() - currentDate.getTime();
            long dayDiff = timeDiff/(24*60*60*1000);

            due = String.valueOf(dayDiff+1);
        }catch (ParseException e)
        {
            e.printStackTrace();
        }


        return due;
    }
    private void deleteGoal(int position,View v) {
        AlertDialog.Builder delete = new AlertDialog.Builder(v.getContext());
        delete.setTitle("Delete Goal");
        delete.setMessage("Do you want to remove this Goal");
        delete.setIcon(R.drawable.baseline_delete_24);
        delete.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d("###","Doc ref : "+arrGoal.get(position).docRef);
                String docId = arrGoal.get(position).getDocRef().getId();
                DocumentReference dref =cRef.document(docId);
                dref.delete();
                arrGoal.remove(position);
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
