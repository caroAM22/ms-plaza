package com.pragma.plazacomida.domain.spi;

import com.pragma.plazacomida.domain.model.ObjectModel;
import java.util.List;

public interface IObjectPersistencePort {
    ObjectModel saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
} 