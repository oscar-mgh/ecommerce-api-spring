package com.github.oscarmgh.ecommerce_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.github.oscarmgh.ecommerce_api.entity.Category;
import com.github.oscarmgh.ecommerce_api.entity.Product;
import com.github.oscarmgh.ecommerce_api.payload.ProductRequest;
import com.github.oscarmgh.ecommerce_api.repository.CategoryRepository;
import com.github.oscarmgh.ecommerce_api.repository.ProductRepository;

import jakarta.persistence.EntityNotFoundException;

public class ProductServiceTests {

	@Mock
	private ProductRepository productRepository;

	@Mock
	private CategoryRepository categoryRepository;

	@InjectMocks
	private ProductService productService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testCreateProductWithNewCategory() {
		ProductRequest request = new ProductRequest("Test Product", "Desc", 10.0, 5, "NewCat", "img.png");
		Category category = new Category();
		category.setId(1L);
		category.setName("NewCat");
		when(categoryRepository.findByName("NewCat")).thenReturn(Optional.empty());
		when(categoryRepository.save(any(Category.class))).thenReturn(category);
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product product = productService.save(request);

		assertEquals("Test Product", product.getName());
		assertEquals("NewCat", product.getCategory().getName());
		verify(categoryRepository).save(any(Category.class));
		verify(productRepository).save(any(Product.class));
	}

	@Test
	void testFindAllProducts() {
		List<Product> products = List.of(new Product(), new Product());
		when(productRepository.findAll()).thenReturn(products);
		List<Product> result = productService.findAll();
		assertEquals(2, result.size());
		verify(productRepository).findAll();
	}

	@Test
	void testFindProductById() {
		Product product = new Product();
		product.setId(1L);
		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		Optional<Product> result = productService.findById(1L);
		assertTrue(result.isPresent());
		assertEquals(1L, result.get().getId());
		verify(productRepository).findById(1L);
	}

	@Test
	void testUpdateProduct() {
		Category category = new Category();
		category.setId(2L);
		category.setName("Cat");
		Product product = new Product();
		product.setId(1L);
		product.setName("Old");
		product.setCategory(category);
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product updated = new Product();
		updated.setId(1L);
		updated.setName("New");
		updated.setCategory(category);

		Product result = productService.save(updated);
		assertEquals("New", result.getName());
		verify(productRepository).save(updated);
	}

	@Test
	void testDeleteProductById() {
		doNothing().when(productRepository).deleteById(1L);
		productService.deleteById(1L);
		verify(productRepository).deleteById(1L);
	}

	@Test
	void testPatchProduct() {
		Category category = new Category();
		category.setId(1L);
		category.setName("Electronics");
		Product product = new Product();
		product.setId(1L);
		product.setName("OldName");
		product.setDescription("OldDesc");
		product.setPrice(100.0);
		product.setStock(10);
		product.setImgUrl("old.png");
		product.setCategory(category);

		Map<String, Object> updates = new HashMap<>();
		updates.put("name", "NewName");
		updates.put("price", 200.0);
		updates.put("imgUrl", "new.png");
		updates.put("category", "Electronics");

		when(productRepository.findById(1L)).thenReturn(Optional.of(product));
		when(categoryRepository.findByName("Electronics")).thenReturn(Optional.of(category));
		when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Product patched = productService.patch(1L, updates);
		assertEquals("NewName", patched.getName());
		assertEquals(200.0, patched.getPrice());
		assertEquals("new.png", patched.getImgUrl());
		assertEquals("Electronics", patched.getCategory().getName());
		verify(productRepository).save(product);
	}

	@Test
	void testPatchProductNotFound() {
		when(productRepository.findById(99L)).thenReturn(Optional.empty());
		Map<String, Object> updates = new HashMap<>();
		updates.put("name", "DoesNotMatter");
		assertThrows(EntityNotFoundException.class, () -> productService.patch(99L, updates));
	}
}
