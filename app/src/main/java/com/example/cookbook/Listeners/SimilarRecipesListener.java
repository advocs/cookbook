package com.example.cookbook.Listeners;

import com.example.cookbook.Models.SimilarRecipeResponse;

import java.util.List;

public interface SimilarRecipesListener {
    void didFetch(List<SimilarRecipeResponse>response,String message);
    void didError(String message);
}
