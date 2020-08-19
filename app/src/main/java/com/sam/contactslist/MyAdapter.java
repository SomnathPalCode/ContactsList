package com.sam.contactslist;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Filterable {

    ArrayList<Contact> mylist;
    ArrayList<Contact> filteredList;

    public MyAdapter(ArrayList<Contact> mylist) {
        this.mylist = mylist;
        this.filteredList = mylist;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final Contact item = filteredList.get(position);
        holder.nameTXT.setText(item.getName());
        holder.mobTXT.setText("Mob: "+item.getMobile());
        if(item.getEmail()!=null && !TextUtils.isEmpty(item.getEmail()))
            holder.emailTXT.setText("Email: "+item.getEmail());
        else
            holder.emailTXT.setText("Email:  N/A");
        if(item.getProfile()!=null)
            holder.profile_image.setImageBitmap(item.getProfile());
        else
            holder.profile_image.setImageResource(R.drawable.ic_person_white);
        holder.check.setChecked(item.isChecked());

        //===========click listner of check box===============//

        holder.check.setOnCheckedChangeListener((compoundButton, b) -> item.setChecked(b));

//        holder.check.setOnClickListener(v ->{
//            item.setChecked(holder.check.isChecked());
//        });
    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    filteredList = mylist;
                } else {
                    ArrayList<Contact> tempList = new ArrayList<>();
                    for (Contact row : mylist) {

                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getMobile().contains(charSequence)) {
                            tempList.add(row);
                        }
                    }

                    filteredList = tempList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredList = (ArrayList<Contact>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView nameTXT, mobTXT, emailTXT;
        CircularImageView profile_image;
        CheckBox check;

        public MyViewHolder(View itemView) {
            super(itemView);
            nameTXT = itemView.findViewById(R.id.nameTXT);
            mobTXT = itemView.findViewById(R.id.mobTXT);
            emailTXT = itemView.findViewById(R.id.emailTXT);
            profile_image = itemView.findViewById(R.id.profile_image);
            check = itemView.findViewById(R.id.check);

        }
    }

    public void checkFilterData(boolean b){
        for(int i=0; i<filteredList.size(); i++){
            filteredList.get(i).setChecked(b);
        }
        notifyDataSetChanged();
    }
}
