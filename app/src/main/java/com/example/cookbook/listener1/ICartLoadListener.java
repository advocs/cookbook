package com.example.cookbook.listener1;

import com.example.cookbook.model1.CartModel;
import com.example.cookbook.model1.GroceryModel;

import java.util.List;

public interface ICartLoadListener {
    void onCartLoadSuccess(List<CartModel> cartModelList);
    void onCartLoadFailure(String message);
}
