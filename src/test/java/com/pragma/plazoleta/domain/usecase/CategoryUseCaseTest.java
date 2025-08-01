package com.pragma.plazoleta.domain.usecase;

import com.pragma.plazoleta.domain.model.Category;
import com.pragma.plazoleta.domain.spi.ICategoryPersistencePort;
import com.pragma.plazoleta.domain.exception.DomainException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryUseCaseTest {
    private ICategoryPersistencePort persistencePort;
    private CategoryUseCase useCase;

    @BeforeEach
    void setUp() {
        persistencePort = Mockito.mock(ICategoryPersistencePort.class);
        useCase = new CategoryUseCase(persistencePort);
    }

    @Test
    void existsByIdReturnsTrueIfCategoryFound() {
        when(persistencePort.existsById(1)).thenReturn(true);
        
        boolean result = useCase.existsById(1);
        assertTrue(result);
    }

    @Test
    void existsByIdReturnsFalseIfCategoryFound() {
        when(persistencePort.existsById(1)).thenReturn(false);
        
        boolean result = useCase.existsById(1);
        assertFalse(result);
    }

    @Test
    void throwExceptionIfCategoryNotFound() {
        when(persistencePort.getById(1)).thenReturn(Optional.empty());
        
        DomainException ex = assertThrows(DomainException.class, () -> useCase.getById(1));
        assertEquals("Category not found", ex.getMessage());
        verify(persistencePort).getById(1);
    }

    @Test
    void returnCategoryIfFound() {
        Category category = new Category(1, "Seafood", "desc1");
        when(persistencePort.getById(1)).thenReturn(Optional.of(category));
        
        Category result = useCase.getById(1);
        assertEquals(category, result);
        verify(persistencePort).getById(1);
    }

    @Test
    void getAll() {
        List<Category> categories = Arrays.asList(
                new Category(1, "Seafood", "desc1"),
                new Category(2, "Fast food", "desc2")
        );
        when(persistencePort.getAll()).thenReturn(categories);
        
        List<Category> result = useCase.getAll();
        
        assertEquals(2, result.size());
        assertEquals("Seafood", result.get(0).getName());
        verify(persistencePort).getAll();
    }

    @Test
    void getByName() {
        Category category = new Category(1, "Seafood", "Seafood desc");
        when(persistencePort.getByName("Seafood")).thenReturn(Optional.of(category));

        Category result = useCase.getByName("Seafood");
        
        assertEquals("Seafood", result.getName());
        assertEquals(1, result.getId());
        assertEquals("Seafood desc", result.getDescription());
        verify(persistencePort).getByName("Seafood");
    }

    @Test
    void getByNameNotFound() {
        when(persistencePort.getByName("Unknown")).thenReturn(Optional.empty());
        assertThrows(DomainException.class, () -> useCase.getByName("Unknown"));
        verify(persistencePort).getByName("Unknown");
    }
} 