package com.github.oscarmgh.ecommerce_api.service;

import com.github.oscarmgh.ecommerce_api.entity.Category;
import com.github.oscarmgh.ecommerce_api.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceTests {
    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllCategories() {
        List<Category> categories = List.of(new Category(), new Category());
        when(categoryRepository.findAll()).thenReturn(categories);
        List<Category> result = categoryService.findAll();
        assertEquals(2, result.size());
        verify(categoryRepository).findAll();
    }

    @Test
    void testFindCategoryById() {
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        Optional<Category> result = categoryService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(categoryRepository).findById(1L);
    }

    @Test
    void testCreateCategory() {
        Category category = new Category();
        category.setName("Books");
        when(categoryRepository.save(any(Category.class))).thenReturn(category);
        Category saved = categoryService.save(category);
        assertEquals("Books", saved.getName());
        verify(categoryRepository).save(category);
    }

    @Test
    void testDeleteCategoryById() {
        doNothing().when(categoryRepository).deleteById(1L);
        categoryService.deleteById(1L);
        verify(categoryRepository).deleteById(1L);
    }
}

class CategoryService {
    private final CategoryRepository categoryRepository;
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    public List<Category> findAll() { return categoryRepository.findAll(); }
    public Optional<Category> findById(Long id) { return categoryRepository.findById(id); }
    public Category save(Category category) { return categoryRepository.save(category); }
    public void deleteById(Long id) { categoryRepository.deleteById(id); }
}
