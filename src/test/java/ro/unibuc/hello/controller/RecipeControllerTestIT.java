package ro.unibuc.hello.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import ro.unibuc.hello.data.IngredientEntity;
import ro.unibuc.hello.data.IngredientRepository;
import ro.unibuc.hello.data.RecipeEntity;
import ro.unibuc.hello.data.RecipeRepository;
import ro.unibuc.hello.dto.AddRecipeDto;
import ro.unibuc.hello.dto.AddIngredientDto;
import ro.unibuc.hello.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Arrays;

import static org.mockito.Mockito.*;

@SpringBootTest
class RecipeControllerTestIT {
    @Autowired
    IngredientRepository ingredientRepository;

    @Autowired
    IngredientController ingredientController;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    RecipeController recipeController;

    @BeforeEach
    public void setUp() {

        recipeRepository.deleteAll();
        ingredientRepository.deleteAll();

        ingredientRepository.save(new IngredientEntity("a", 2, 2, 2, 2, 2));
        ingredientRepository.save(new IngredientEntity("b", 2, 2, 2, 2, 2));
        ingredientRepository.save(new IngredientEntity("c", 2, 2, 2, 2, 2));

        ArrayList<String> mockRecipes = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        recipeRepository.save(new RecipeEntity("Test", mockRecipes));

    }

    @Test
    @Order(1)
    public void getRecipe_Throws() {
        ArrayList<String> mockRecipe = new ArrayList<String>();
        var Recipe = new AddRecipeDto("name", mockRecipe);

        try {
            recipeController.getRecipe("TestNot");
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(NotFoundException.class, e.getClass());
            Assertions.assertEquals("Not Found", e.getMessage());
        }
    }

    @Test
    @Order(2)
    void getRecipe_Returns() {

        var res = recipeController.getRecipe("Test");

        Assertions.assertEquals("Test", res.name);
        Assertions.assertEquals(3, res.ingredientsNames.size());
    }

    @Test
    @Order(3)
    void getAllRecipes_Returns() {
        var res = recipeController.getAllRecipes();

        Assertions.assertEquals(1, res.size());
    }

    @Test
    @Order(4)
    void addRecipe_works() {

        ArrayList<String> mockRecipes = new ArrayList<String>(Arrays.asList("a", "b", "c"));
        var Recipe2 = new AddRecipeDto("name", mockRecipes);
        recipeController.addRecipe(Recipe2);
        var res = recipeController.getAllRecipes();
        var res2=recipeController.getRecipe("name");

        Assertions.assertEquals(2, res.size());
        Assertions.assertEquals("name", res2.name);
        Assertions.assertEquals(3, res2.ingredientsNames.size());

    }





}