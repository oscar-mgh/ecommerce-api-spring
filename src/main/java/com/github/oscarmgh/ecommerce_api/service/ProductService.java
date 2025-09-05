package com.github.oscarmgh.ecommerce_api.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.oscarmgh.ecommerce_api.entity.Category;
import com.github.oscarmgh.ecommerce_api.entity.Product;
import com.github.oscarmgh.ecommerce_api.payload.ProductRequest;
import com.github.oscarmgh.ecommerce_api.repository.CategoryRepository;
import com.github.oscarmgh.ecommerce_api.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	private final ProductRepository productRepository;
	private final CategoryRepository categoryRepository;

	public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
		this.productRepository = productRepository;
		this.categoryRepository = categoryRepository;
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	public Optional<Product> findById(Long id) {
		return productRepository.findById(id);
	}

	public Product save(ProductRequest productRequest) {
		// Find category by name; if it doesn't exist, create it automatically
		Category category = categoryRepository.findByName(productRequest.getCategory())
				.orElseGet(() -> {
					Category newCat = new Category();
					newCat.setName(productRequest.getCategory());
					return categoryRepository.save(newCat);
				});
		
		// Create product entity
		Product product = new Product();
		product.setName(productRequest.getName());
		product.setDescription(productRequest.getDescription());
		product.setPrice(productRequest.getPrice());
		product.setStock(productRequest.getStock());
		product.setImgUrl(productRequest.getImgUrl());
		product.setCategory(category);
		
		return productRepository.save(product);
	}

	public Product save(Product product) {
		// Validate that category is set
		if (product.getCategory() == null) {
			throw new IllegalArgumentException("Category is required for product");
		}
		
		return productRepository.save(product);
	}

	public void deleteById(Long id) {
		productRepository.deleteById(id);
	}

	/**
     * Applies a partial update to an existing product,
     * using ObjectMapper to update the fields from the map.
     * @param id The ID of the product to update.
     * @param updates The map with the fields to update and their new values.
     * @return The updated product.
     */
    public Product patch(Long id, Map<String, Object> updates) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id " + id));

        // Handle category name update
		if (updates.containsKey("category")) {
			String categoryName = (String) updates.get("category");
			Category category = categoryRepository.findByName(categoryName)
					.orElseGet(() -> {
						Category newCat = new Category();
						newCat.setName(categoryName);
						return categoryRepository.save(newCat);
					});
			existingProduct.setCategory(category);
			updates.remove("category"); // Remove from updates to avoid processing by ObjectMapper
		}

        // Use Jackson's ObjectMapper to apply remaining updates
        ObjectMapper mapper = new ObjectMapper();
        Product updatedProduct = mapper.convertValue(updates, Product.class);

        // Manual mapping of non-null fields from the updated object to the existing object
        if (updatedProduct.getName() != null) {
            existingProduct.setName(updatedProduct.getName());
        }
        if (updatedProduct.getDescription() != null) {
            existingProduct.setDescription(updatedProduct.getDescription());
        }
        if (updatedProduct.getPrice() != null) {
            existingProduct.setPrice(updatedProduct.getPrice());
        }
        if (updatedProduct.getStock() != null) {
            existingProduct.setStock(updatedProduct.getStock());
        }
		if (updatedProduct.getImgUrl() != null) {
			existingProduct.setImgUrl(updatedProduct.getImgUrl());
		}
        // Note: Category is already handled above, so we don't need to check it here

        return productRepository.save(existingProduct);
    }
}