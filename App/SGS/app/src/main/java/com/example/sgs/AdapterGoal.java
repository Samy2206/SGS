    package com.example.sgs;

    import static android.content.Context.MODE_PRIVATE;

    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.DialogInterface;
    import android.content.SharedPreferences;
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


        private ArrayList<Model_Goal> arrGoal = new ArrayList<>();
        private FirebaseFirestore fstore = FirebaseFirestore.getInstance();
        private CollectionReference cRef = fstore.collection("Goals");
        private String due;
        private static int dueTomorrow=0;
        private ArrayList<Integer> arrDue = new ArrayList<>();

        Context context;
        public AdapterGoal(ArrayList<Model_Goal> arrGoal, Context context) {
            this.arrGoal = arrGoal;
            this.context = context;
        }

        public void resetDueTomorrow() {
            dueTomorrow = 0;
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
            if(Integer.parseInt(calculateDueDate(position))==1)
            {
                holder.txtDue.setText("Tomorrow");
            }
            else {
                holder.txtDue.setText(calculateDueDate(position));
            }
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
                if(Integer.parseInt(due)<0)
                {
                    arrDue.add(position);
                }else
                if(Integer.parseInt(due)==1)
                {
                    dueTomorrow++;
                }
            }catch (ParseException e)
            {
                e.printStackTrace();
            }
            SharedPreferences pref =context.getSharedPreferences("numberValues",MODE_PRIVATE);
            Log.d("dueTomorrow: ", String.valueOf(dueTomorrow));
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("dueTomorrow", dueTomorrow);
            editor.apply();
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
                   // Log.d("###","Doc ref : "+arrGoal.get(position).docRef);
                    String docId = arrGoal.get(position).getDocRef().getId();
                    DocumentReference dref =cRef.document(docId);
                    dref.delete();
                    arrGoal.remove(position);
                    notifyItemRemoved(position);
                    resetDueTomorrow();
                    notifyDataSetChanged();
                }
            }); delete.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            delete.show();
        }
        public void deleteDueGoals() {
            ArrayList<Integer> itemsToRemove = new ArrayList<>();

            for (int i = 0; i < arrGoal.size(); i++) {
                String date = arrGoal.get(i).getDate().toString();
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

                try {
                    Date inputDate = sdf.parse(date);
                    Date currentDate = new Date();
                    long timeDiff = inputDate.getTime() - currentDate.getTime();
                    long dayDiff = timeDiff / (24 * 60 * 60 * 1000);

                    String due = String.valueOf(dayDiff + 1);

                    if (Integer.parseInt(due) <= 0) {
                        itemsToRemove.add(i);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            // Remove items outside the loop to avoid ConcurrentModificationException
            for (int position : itemsToRemove) {
                String docId = arrGoal.get(position).getDocRef().getId();
                DocumentReference dref = cRef.document(docId);
                dref.delete();
                arrGoal.remove(position);
                notifyItemRemoved(position);
                resetDueTomorrow();
            }

            notifyDataSetChanged();
        }

    }
