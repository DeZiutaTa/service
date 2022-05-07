package ro.unibuc.hello.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import ro.unibuc.hello.data.IngredientEntity;
import ro.unibuc.hello.data.IngredientRepository;
import ro.unibuc.hello.dto.AddIngredientDto;
import ro.unibuc.hello.dto.AddRecipeDto;
import ro.unibuc.hello.exception.NotFoundException;
import org.junit.jupiter.api.Tag;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@Tag("IT")
class IngredientControllerTestIT {

    @Autowired
    IngredientController ingredientController;

    @Autowired
    IngredientRepository ingredientRepository;

    @BeforeEach
    public void setUp() {
        var x = ingredientRepository.findByName("Nu Este");
        if(x != null)
            ingredientRepository.delete(x);

        var y = ingredientRepository.findByName("Este");
        if(y != null)
            ingredientRepository.delete(y);

        ingredientRepository.save(new IngredientEntity("Este", 2, 2, 2, 2, 2));
    }

    @Test
    @Order(1)
    public void getIngredient_Throws() {
        try {
            ingredientController.getIngredient("Nu Este");
            Assertions.fail();
        }
        catch (Exception e){
            Assertions.assertEquals(NotFoundException.class, e.getClass());
            Assertions.assertEquals("Not Found", e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void getIngredient_Returns() {

        var res = ingredientController.getIngredient("Este");
        Assertions.assertEquals("Este", res.name);
        Assertions.assertEquals(2, res.price);
        Assertions.assertEquals(2, res.calories);
        Assertions.assertEquals(2, res.protein);
        Assertions.assertEquals(2, res.carb);
        Assertions.assertEquals(2, res.fat);
    }

    @Test
    @Order(3)
    void addIngredient() {
        var ingredient = new AddIngredientDto("Nu Este", 5, 5, 5, 5, 5);

        ingredientController.addIngredient(ingredient);

        Assertions.assertNotNull(ingredientRepository.findByName("Nu Este"));
    }

/*    @AfterAll
    public void clean() {
        var x = ingredientRepository.findByName("Nu Este");
        if(x != null)
            ingredientRepository.delete(x);

        var y = ingredientRepository.findByName("Este");
        if(y != null)
            ingredientRepository.delete(y);

      }*/
}