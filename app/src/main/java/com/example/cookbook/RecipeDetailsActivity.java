package com.example.cookbook;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cookbook.Adapters.IngredientsAdapter;
import com.example.cookbook.Adapters.InstructionsAdapter;
import com.example.cookbook.Adapters.SimilarRecipeAdapter;
import com.example.cookbook.Listeners.InstructionsListener;
import com.example.cookbook.Listeners.RecipeClickListener;
import com.example.cookbook.Listeners.RecipeDetailsListener;
import com.example.cookbook.Listeners.SimilarRecipesListener;
import com.example.cookbook.Models.InstructionsResponse;
import com.example.cookbook.Models.RecipeDetailsResponse;
import com.example.cookbook.Models.SimilarRecipeResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {
    int id;
    TextView textview_meal_name,textview_meal_source,textview_meal_summary;
    ImageView imageview_meal_image;
    RecyclerView recycler_meal_ingredients,recycler_meal_similar,recycler_meal_instructions;
    RequestManager manager;
    ProgressDialog dialog;
    IngredientsAdapter ingredientsAdapter;
    SimilarRecipeAdapter similarRecipeAdapter;
    InstructionsAdapter instructionsAdapter;
    Button orderbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        findViews();

        id=Integer.parseInt(getIntent().getStringExtra("id"));
        manager=new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener,id);
        manager.getSimilarRecipes(similarRecipesListener,id);
        manager.getInstructions(instructionsListener,id);
        dialog=new ProgressDialog(this);
        dialog.setTitle("Loading details...");
        dialog.show();

    }

    private void findViews() {
        textview_meal_name=findViewById(R.id.textview_meal_name);
        textview_meal_source=findViewById(R.id.textview_meal_source);
        textview_meal_summary=findViewById(R.id.textview_meal_summary);
        imageview_meal_image=findViewById(R.id.imageview_meal_image);
        recycler_meal_ingredients=findViewById(R.id.recycler_meal_ingredients);
        recycler_meal_similar=findViewById(R.id.recycler_meal_similar);
        recycler_meal_instructions=findViewById(R.id.recycler_meal_instructions);
        orderbtn=findViewById(R.id.orderbtn);


        orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent i=getPackageManager().getLaunchIntentForPackage("com.whatsaap");
                //startActivity(i);
                Intent intent=new Intent(RecipeDetailsActivity.this,MainActivity4.class);
                startActivity(intent);
            }


        });

        /*orderbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void openApp(View view) {
                Intent launchIntent=getPackageManager().getLaunchIntentForPackage("com.rajendra.onlinedailygroceries");
                startActivity(launchIntent);
                //Intent intent=new Intent(RecipeDetailsActivity.this,MainActivity3.class);
                //startActivity(intent);
            }
        });*/

    }


    private final RecipeDetailsListener recipeDetailsListener=new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            dialog.dismiss();
            textview_meal_name.setText(response.title);
            textview_meal_source.setText(response.sourceName);
            textview_meal_summary.setText(response.summary);
            Picasso.get().load(response.image).into(imageview_meal_image);

            recycler_meal_ingredients.setHasFixedSize(true);
            recycler_meal_ingredients.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false));
            ingredientsAdapter=new IngredientsAdapter(RecipeDetailsActivity.this,response.extendedIngredients);
            recycler_meal_ingredients.setAdapter(ingredientsAdapter);


        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this,message,Toast.LENGTH_SHORT).show();

        }
    };

    private final SimilarRecipesListener similarRecipesListener=new SimilarRecipesListener() {
        @Override
        public void didFetch(List<SimilarRecipeResponse> response, String message) {
            recycler_meal_similar.setHasFixedSize(true);
            recycler_meal_similar.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this,LinearLayoutManager.HORIZONTAL,false));
            similarRecipeAdapter=new SimilarRecipeAdapter(RecipeDetailsActivity.this,response,recipeClickListener);
            recycler_meal_similar.setAdapter(similarRecipeAdapter);

        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this, message, Toast.LENGTH_SHORT).show();

        }
    };

    private final RecipeClickListener recipeClickListener=new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(RecipeDetailsActivity.this,RecipeDetailsActivity.class)
                    .putExtra("id",id));
            //Toast.makeText(RecipeDetailsActivity.this,id,Toast.LENGTH_SHORT).show();



        }
    };
    private final InstructionsListener instructionsListener=new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            recycler_meal_instructions.setHasFixedSize(true);
            recycler_meal_instructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this,LinearLayoutManager.VERTICAL,false));
            instructionsAdapter=new InstructionsAdapter(RecipeDetailsActivity.this,response);
            recycler_meal_instructions.setAdapter(instructionsAdapter);


        }

        @Override
        public void didError(String message) {

        }
    };



}