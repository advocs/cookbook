package com.example.cookbook.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cookbook.Models.Equipment;
import com.example.cookbook.Models.Ingredient;
import com.example.cookbook.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InstructionsEquipmentsAdapter extends RecyclerView.Adapter<InstructionEquipmentsViewHolder>{

    Context context;
    List<Equipment> list;

    public InstructionsEquipmentsAdapter(Context context, List<Equipment> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionEquipmentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionEquipmentsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_step_items,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionEquipmentsViewHolder holder, int position) {
        holder.textView_instructions_steps_item.setText(list.get(position).name);
        holder.textView_instructions_steps_item.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/equipment_100x100/"+list.get(position).image).into(holder.imageView_instruction_step_item);


    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}
class InstructionEquipmentsViewHolder extends RecyclerView.ViewHolder {

    TextView textView_instructions_steps_item;
    ImageView imageView_instruction_step_item;
    public InstructionEquipmentsViewHolder(@NonNull View itemView) {
        super(itemView);
        imageView_instruction_step_item=itemView.findViewById(R.id.imageView_instruction_step_item);
        textView_instructions_steps_item=itemView.findViewById(R.id.textView_instructions_steps_item);

    }
}
