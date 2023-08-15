package com.example.cookbook.listener1;

import com.example.cookbook.model1.GroceryModel;

import java.util.List;

public interface IGroceryLoadListener {

    void onGroceryLoadSuccess(List<GroceryModel> groceryModelList);
    void onGroceryLoadFailure(String message);
}
