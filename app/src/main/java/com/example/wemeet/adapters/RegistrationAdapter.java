package com.example.wemeet.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.wemeet.R;
import com.example.wemeet.classes.User;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class RegistrationAdapter extends RecyclerView.Adapter<RegistrationAdapter.MyViewHolder> {

    private HashMap<String, Boolean> dateHashMap;
    private List<Date> dateArrayList;
    private LayoutInflater inflater;
    public Context context;

    public RegistrationAdapter(Context context, List<Date> dateArrayList, HashMap<String, Boolean> dateHashMap) {
        inflater = LayoutInflater.from(context);
        this.dateHashMap = dateHashMap;
        this.context = context;
        this.dateArrayList = dateArrayList;
    }


    @Override
    public RegistrationAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_registration, parent, false);
        RegistrationAdapter.MyViewHolder holder = new RegistrationAdapter.MyViewHolder(view);
        view.setOnClickListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(RegistrationAdapter.MyViewHolder holder, int position) {
        holder.setData(position);
    }

    @Override
    public int getItemCount() {
        return dateArrayList.size();
    }

    public HashMap<String, Boolean> getDateHashMap() {
        return dateHashMap;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        Switch registerSwitch;

        public MyViewHolder(View itemView) {
            super(itemView);
            registerSwitch = itemView.findViewById(R.id.registerSwitch);
        }

        public void setData(int position) {

            final Date selectedDate = dateArrayList.get(position);
            DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm");
            final String strDate = dateFormat.format(selectedDate);

            registerSwitch.setText(strDate);
            if(dateHashMap == null) dateHashMap = new HashMap<>();
            if(dateHashMap.containsKey(strDate) && dateHashMap.get(strDate) != null) {
                registerSwitch.setChecked(dateHashMap.get(strDate));
            } else {
                dateHashMap.put(strDate, false);
                registerSwitch.setChecked(false);
            }
            registerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    dateHashMap.put(strDate, b);
                }
            });
        }


        @Override
        public void onClick(View v) {
            //int position = getAdapterPosition();
            //String selectedGuideID = eventArrayList.get(position).getUserID();
            //Intent intent = new Intent(context, GuideDetailsActivity.class);
            //intent.putExtra("guideID", selectedGuideID);
            //intent.putExtra("selectedDate", dateText);
            //context.startActivity(intent);
        }
    }
}
