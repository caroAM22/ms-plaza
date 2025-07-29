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