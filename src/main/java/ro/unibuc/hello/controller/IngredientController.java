package ro.unibuc.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ro.unibuc.hello.data.IngredientEntity;
import ro.unibuc.hello.data.IngredientRepository;
import ro.unibuc.hello.dto.AddIngredientDto;
import ro.unibuc.hello.dto.IngredientDto;
import ro.unibuc.hello.exception.BadRequestException;
import ro.unibuc.hello.exception.NotFoundException;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.MeterRegistry;
import java.util.concurrent.atomic.AtomicLong;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class IngredientController {

    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Autowired
    MeterRegistry metricsRegistry;

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/ingredient")
    @Timed(value = "show.ingredient.time", description = "Time taken to return ingredient")
    @Counted(value = "show.ingredient.count", description = "Times ingredient was returned")
    @ResponseBody
    public IngredientDto getIngredient(@RequestParam(name="name") String name) {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "ingredient").increment(counter.incrementAndGet());
        var entity = ingredientRepository.findByName(name);
        if(entity == null) {
            throw new NotFoundException();
        }
        return new IngredientDto(entity);
    }

    @GetMapping("/ingredients")
    @Timed(value = "show.ingredients.time", description = "Time taken to return all ingredients")
    @Counted(value = "show.ingredients.count", description = "Times list of ingredients was returned")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<IngredientDto> getAllIngredients() {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "ingredients").increment(counter.incrementAndGet());
        var entities = ingredientRepository.findAll();
        if (entities.size() == 0) {
            throw new NotFoundException();
        }
        var returnedEntities = entities.stream();


        return returnedEntities.map(IngredientDto::new).collect(Collectors.toList());
    }

    @PostMapping("/ingredient/add")
     @Timed(value = "add.ingredient.time", description = "Time taken to add ingredient")
    @Counted(value = "add.ingredient.count", description = "Times an ingredient was added")
    @ResponseStatus(HttpStatus.CREATED)
    public void addIngredient(@RequestBody AddIngredientDto model) {
        metricsRegistry.counter("my_non_aop_metric", "endpoint", "add/ingredient").increment(counter.incrementAndGet());

        if (model.price < 0) {
            throw new BadRequestException(new HashMap<>() {{
                put("price", "negative");
            }});
        }

        IngredientEntity ingredient = new IngredientEntity(model.name, model.price, model.calories, model.protein, model.carb, model.fat);
        ingredientRepository.save(ingredient);
    }

}
