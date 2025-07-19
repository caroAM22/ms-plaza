package com.pragma.plazacomida.application.handler;

import com.pragma.plazacomida.application.dto.request.ObjectRequestDto;
import com.pragma.plazacomida.application.dto.response.ObjectResponseDto;

import java.util.List;

public interface IObjectHandler {

    void saveObject(ObjectRequestDto objectRequestDto);

    List<ObjectResponseDto> getAllObjects();
} 