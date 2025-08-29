package uy.com.inventory.inventory_service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import uy.com.inventory.inventory_service.domain.Stock;
import uy.com.inventory.inventory_service.repository.StockRepository;

@EnableScheduling
@SpringBootApplication
public class InventoryServiceManagerApplication {
	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceManagerApplication.class, args);
	}
}
