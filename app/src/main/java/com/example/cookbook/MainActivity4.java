package com.example.cookbook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.cookbook.adapter1.MyGroceryAdapter;
import com.example.cookbook.eventbus.MyUpdateCartEvent;
import com.example.cookbook.listener1.ICartLoadListener;
import com.example.cookbook.listener1.IGroceryLoadListener;
import com.example.cookbook.model1.CartModel;
import com.example.cookbook.model1.GroceryModel;
import com.example.cookbook.utils.SpaceItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nex3z.notificationbadge.NotificationBadge;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity4 extends AppCompatActivity implements IGroceryLoadListener, ICartLoadListener {
    FirebaseAuth mAuth;
    FirebaseUser mUser;


    @BindView(R.id.recycler_grocery)
    RecyclerView recyclerGrocery;
    @BindView(R.id.mainLayout)
    RelativeLayout mainLayout;
    @BindView(R.id.badge)
    NotificationBadge badge;
    @BindView(R.id.btnCart)
    FrameLayout btnCart;

    IGroceryLoadListener groceryLoadListener;
    ICartLoadListener cartLoadListener;

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        if(EventBus.getDefault().hasSubscriberForEvent(MyUpdateCartEvent.class))
            EventBus.getDefault().removeStickyEvent(MyUpdateCartEvent.class);
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void onUpdateCart(MyUpdateCartEvent event)
    {
        countCartItem();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4);

        init();
        loadGroceryFromFirebase();
        countCartItem();
    }

    private void loadGroceryFromFirebase() {
        List<GroceryModel> groceryModels=new ArrayList<>();
        FirebaseDatabase.getInstance()
                .getReference("Grocery")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            for (DataSnapshot grocerySnapshot:snapshot.getChildren())
                            {
                                GroceryModel groceryModel=grocerySnapshot.getValue(GroceryModel.class);
                                groceryModel.setKey(grocerySnapshot.getKey());
                                groceryModels.add(groceryModel);
                            }
                            groceryLoadListener.onGroceryLoadSuccess(groceryModels);
                        }
                        else
                            groceryLoadListener.onGroceryLoadFailure("Can't find any");

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        groceryLoadListener.onGroceryLoadFailure(error.getMessage());

                    }
                });
    }

    private  void init(){
        ButterKnife.bind(this);


        groceryLoadListener = this;
        cartLoadListener=this;

        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerGrocery.setLayoutManager(gridLayoutManager);
        recyclerGrocery.addItemDecoration(new SpaceItemDecoration());

        btnCart.setOnClickListener(v ->  startActivity(new Intent(this,CartActivity.class)));

    }


    @Override
    public void onCartLoadSuccess(List<CartModel> cartModelList) {

        int cartSum=0;
        for(CartModel cartModel: cartModelList)
            cartSum+=cartModel.getQuantity();
        badge.setNumber(cartSum);

    }

    @Override
    public void onCartLoadFailure(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }
    @Override
    protected void onResume(){
        super.onResume();
        countCartItem();
    }

    private void countCartItem() {
        List<CartModel> cartModels=new ArrayList<>();
        FirebaseDatabase
                .getInstance().getReference("Cart")
                .child("id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot cartSnapshot:snapshot.getChildren())
                        {
                            CartModel cartModel=cartSnapshot.getValue(CartModel.class);
                            cartModel.setKey(cartSnapshot.getKey());
                            cartModels.add(cartModel);
                        }
                        cartLoadListener.onCartLoadSuccess(cartModels);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        cartLoadListener.onCartLoadFailure(error.getMessage());

                    }
                });
    }

    @Override
    public void onGroceryLoadSuccess(List<GroceryModel> groceryModelList) {
        MyGroceryAdapter adapter=new MyGroceryAdapter(this,groceryModelList,cartLoadListener);
        recyclerGrocery.setAdapter(adapter);

    }

    @Override
    public void onGroceryLoadFailure(String message) {
        Snackbar.make(mainLayout,message,Snackbar.LENGTH_LONG).show();

    }
}