package com.pragma.plazacomida.infrastructure.configuration;

import com.pragma.plazacomida.domain.api.IObjectServicePort;
import com.pragma.plazacomida.domain.spi.IObjectPersistencePort;
import com.pragma.plazacomida.domain.usecase.ObjectUseCase;
import com.pragma.plazacomida.infrastructure.out.jpa.adapter.ObjectJpaAdapter;
import com.pragma.plazacomida.infrastructure.out.jpa.mapper.IObjectEntityMapper;
import com.pragma.plazacomida.infrastructure.out.jpa.repository.IObjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class BeanConfiguration {
    private final IObjectRepository objectRepository;
    private final IObjectEntityMapper objectEntityMapper;

    @Bean
    public IObjectPersistencePort objectPersistencePort() {
        return new ObjectJpaAdapter(objectRepository, objectEntityMapper);
    }

    @Bean
    public IObjectServicePort objectServicePort() {
        return new ObjectUseCase(objectPersistencePort());
    }
} 