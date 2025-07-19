package com.pragma.plazacomida.domain.api;

import com.pragma.plazacomida.domain.model.ObjectModel;

import java.util.List;

public interface IObjectServicePort {

    void saveObject(ObjectModel objectModel);

    List<ObjectModel> getAllObjects();
} 