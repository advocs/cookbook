package com.example.cookbook.adapter1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.cookbook.R;
import com.example.cookbook.eventbus.MyUpdateCartEvent;
import com.example.cookbook.listener1.ICartLoadListener;
import com.example.cookbook.listener1.IRecyclerViewClickListener;
import com.example.cookbook.listener1.IRecyclerViewClickListener;
import com.example.cookbook.model1.CartModel;
import com.example.cookbook.model1.GroceryModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyGroceryAdapter extends RecyclerView.Adapter<MyGroceryAdapter.MyGroceryViewHolder> {
    FirebaseAuth mAuth;
    FirebaseUser mUser;



    private Context context;
    private List<GroceryModel> groceryModelList;
    private ICartLoadListener iCartLoadListener;

    public MyGroceryAdapter(Context context, List<GroceryModel> groceryModelList, ICartLoadListener iCartLoadListener) {
        this.context = context;
        this.groceryModelList = groceryModelList;
        this.iCartLoadListener = iCartLoadListener;
    }

    @NonNull
    @Override
    public MyGroceryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        return new MyGroceryViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_grocery,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyGroceryViewHolder holder, int position) {
        Glide.with(context)
                .load(groceryModelList.get(position).getImage())
                .into(holder.imgview);
        holder.txtPrice.setText(new StringBuilder("Rs ").append(groceryModelList.get(position).getPrice()));
        holder.txtName.setText(new StringBuilder().append(groceryModelList.get(position).getName()));

        holder.setListener((view, adapterPosition) -> {
            addToCart(groceryModelList.get(position));

        });

    }

    private void addToCart(GroceryModel groceryModel) {
        DatabaseReference userCart= FirebaseDatabase
                .getInstance()
                .getReference("Cart")
                .child(mUser.getUid());
        userCart.child(groceryModel.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            CartModel cartModel= snapshot.getValue(CartModel.class);
                            cartModel.setQuantity(cartModel.getQuantity()+1);
                            Map<String,Object> updateData=new HashMap<>();
                            updateData.put("Quantity",cartModel.getQuantity());
                            updateData.put("totalPrice",cartModel.getQuantity()*Float.parseFloat(cartModel.getPrice()));


                            userCart.child(groceryModel.getKey())
                                    .updateChildren(updateData)
                                    .addOnSuccessListener(aVoid -> {
                                        iCartLoadListener.onCartLoadFailure("Add To Cart Success");


                                    })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailure(e.getMessage()));

                        }
                        else
                        {
                            CartModel cartModel=new CartModel();
                            cartModel.setName(groceryModel.getName());
                            cartModel.setImage(groceryModel.getImage());
                            cartModel.setKey(groceryModel.getKey());
                            cartModel.setPrice(groceryModel.getPrice());
                            cartModel.setQuantity(1);
                            cartModel.setTotalPrice(Float.parseFloat(groceryModel.getPrice()));

                            userCart.child(groceryModel.getKey())
                                    .setValue(cartModel)
                                    .addOnSuccessListener(aVoid -> {
                            iCartLoadListener.onCartLoadFailure("Add To Cart Success");


                        })
                                    .addOnFailureListener(e -> iCartLoadListener.onCartLoadFailure(e.getMessage()));



                        }
                        EventBus.getDefault().postSticky(new MyUpdateCartEvent());

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        iCartLoadListener.onCartLoadFailure(error.getMessage());


                    }
                });
    }

    @Override
    public int getItemCount() {
        return groceryModelList.size();
    }

    public class MyGroceryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.imgview)
        ImageView imgview;
        @BindView(R.id.txtName)
        TextView txtName;
        @BindView(R.id.txtPrice)
        TextView txtPrice;

        IRecyclerViewClickListener listener;

        public void setListener(IRecyclerViewClickListener listener) {
            this.listener = listener;
        }

        private Unbinder unbinder;

        public MyGroceryViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onRecyclerClick(v,getAdapterPosition());
        }
    }
}
