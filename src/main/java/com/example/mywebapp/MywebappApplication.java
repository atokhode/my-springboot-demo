package com.example.mywebapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@SpringBootApplication
public class MywebappApplication {

	public static void main(String[] args) {
		SpringApplication.run(MywebappApplication.class, args);
	}

}


@Entity
class Coffee{

	@Id
	private String id;
	private String name;

	public Coffee(){}

	public Coffee(String id, String name){
		this.id = id;
		this.name = name;
	}

	public Coffee(String name){
		this(UUID.randomUUID().toString(), name);
	}

	public void setId(String id){
		this.id = id;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getId(){
		return this.id;
	}

	public String getName(){
		return this.name;
	}
}

interface CoffeeRepository extends CrudRepository<Coffee, String>{}

@Component
class DataLoader{
	private CoffeeRepository coffeeRepository;

	public DataLoader(CoffeeRepository coffeeRepository){
		this.coffeeRepository = coffeeRepository;
	}

	@PostConstruct
	private void loadData(){
		coffeeRepository.saveAll(List.of(
				new Coffee("Cafe Cereza"),
				new Coffee("Cafe Gandor"),
				new Coffee("Cafe lareno"),
				new Coffee("Cafe Tres Pontas")
		));
	}
}


@RestController
@RequestMapping("/coffees")
class RestApiDemoController{
	private final CoffeeRepository coffeeRepository;

	public RestApiDemoController(CoffeeRepository coffeeRepository){
		this.coffeeRepository = coffeeRepository;
	}

	@GetMapping
	Iterable<Coffee> getCoffee(){
		return coffeeRepository.findAll();
	}

	@GetMapping("/{id}")
	Optional<Coffee> getCoffeeById(@PathVariable String id){
		return coffeeRepository.findById(id);
	}

	@PostMapping
	Coffee postCoffee(@RequestBody Coffee coffee){
		return coffeeRepository.save(coffee);
	}

	@PutMapping("/{id}")
	ResponseEntity<Coffee> putCoffee(@PathVariable String id, @RequestBody Coffee coffee){

		return (coffeeRepository.existsById(id))
				? new ResponseEntity<Coffee>(coffeeRepository.save(coffee), HttpStatus.OK)
				: new ResponseEntity<Coffee>(coffeeRepository.save(coffee), HttpStatus.CREATED);
	}

	@DeleteMapping("/{id}")
	void DeleteCoffee(@PathVariable String id){
		coffeeRepository.deleteById(id);
	}

}