package ro.unibuc.hello.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.IngredientEntity;
import ro.unibuc.hello.data.IngredientRepository;
import ro.unibuc.hello.data.RecipeEntity;
import ro.unibuc.hello.data.RecipeRepository;
import ro.unibuc.hello.dto.AddIngredientDto;
import ro.unibuc.hello.dto.AddRecipeDto;
import ro.unibuc.hello.dto.IngredientDto;
import ro.unibuc.hello.dto.RecipeDto;
import ro.unibuc.hello.exception.NotFoundException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicLong;

import java.util.List;
import java.util.stream.Collectors;
@Controller
public class RecipeController {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

        @Autowired
    MeterRegistry metricsRegistry;


    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/recipe")
    @Timed(value = "show.recipe.time", description = "Time taken to return recipe")
    @Counted(value = "show.recipe.count", description = "Times recipe was returned")
    @ResponseBody
    public RecipeDto getRecipe(@RequestParam(name="name") String name) {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "recipe").increment(counter.incrementAndGet());
        var entity = recipeRepository.findByName(name);
        if(entity == null) {
            throw new NotFoundException();
        }
//        for (int i = 0; i < entity.ingredientsNames.size(); i++) {
//            System.out.println(entity.ingredientsNames.get(i));
//        }
        return new RecipeDto(entity);
    }

    @PostMapping("/recipe/add")
    @Timed(value = "add.recipe.time", description = "Time taken to add recipe")
    @Counted(value = "add.recipe.count", description = "Times recipe was added")
    @ResponseStatus(HttpStatus.CREATED)
    public void addRecipe(@RequestBody AddRecipeDto model) {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "recipe/add").increment(counter.incrementAndGet());

        if (model.ingredientsNames.size() == 0) {
            throw new NotFoundException();
        }
        for (int i = 0; i < model.ingredientsNames.size(); i++) {
            var entity = ingredientRepository.findByName(model.ingredientsNames.get(i));
            if(entity == null) {
                throw new NotFoundException();
            }
//            if(entity != null)
            System.out.println(model.ingredientsNames.get(i));
        }
        RecipeEntity recipe = new RecipeEntity(model.name, model.ingredientsNames);
        recipeRepository.save(recipe);
    }

    @GetMapping("/recipes")
    @Timed(value = "show.recipes.time", description = "Time taken to return list of recipes")
    @Counted(value = "show.recipes.count", description = "Times list of recipes was returned")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<RecipeDto> getAllRecipes() {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "recipes").increment(counter.incrementAndGet());
        var entities = recipeRepository.findAll();
        if (entities.size() == 0) {
            throw new NotFoundException();
        }
        var returnedEntities = entities.stream();
        return returnedEntities.map(RecipeDto::new).collect(Collectors.toList());
    }
}
