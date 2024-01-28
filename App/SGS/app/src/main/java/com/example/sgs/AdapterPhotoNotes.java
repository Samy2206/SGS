package com.example.sgs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;


public class AdapterPhotoNotes extends RecyclerView.Adapter<AdapterPhotoNotes.ViewHolder> {
    private ArrayList<ModelPhoto> arrPhoto = new ArrayList<>();
    private final String userId = FirebaseAuth.getInstance().getUid();
    private StorageReference sRef = FirebaseStorage.getInstance().getReference("user_images").child(userId);
    private OnItemClickListener onItemClickListener;

    // Constructor
    public AdapterPhotoNotes(ArrayList<ModelPhoto> arrPhoto) {
        this.arrPhoto = arrPhoto;
    }

    // Setter for the interface
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public AdapterPhotoNotes.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPhotoNotes.ViewHolder holder, int position) {
        Glide.with(holder.itemView.getContext())
                .load(arrPhoto.get(position).getImgUri())
                .into(holder.imgPhoto);
        holder.txtImgName.setText(arrPhoto.get(position).getImgName());

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder delete = new AlertDialog.Builder(v.getContext());
                delete.setTitle("Delete Image");
                delete.setMessage("Do you want to remove this Image");
                delete.setIcon(R.drawable.baseline_delete_24);
                delete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String imgName = arrPhoto.get(position).getImgName();
                        StorageReference photoRef = sRef.child(imgName);

                        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Photo deleted successfully, now remove it from the ArrayList and update the RecyclerView
                                arrPhoto.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                                Toast.makeText(holder.itemView.getContext(), "Photo deleted successfully", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure to delete the photo
                                Toast.makeText(holder.itemView.getContext(), "Failed to delete photo", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrPhoto.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtImgName;
        ImageView imgPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtImgName = itemView.findViewById(R.id.txtImgName);
            imgPhoto = itemView.findViewById(R.id.imgPhoto);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

}
