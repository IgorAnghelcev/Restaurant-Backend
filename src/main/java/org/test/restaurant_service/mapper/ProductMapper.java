package org.test.restaurant_service.mapper;


import org.mapstruct.*;

import org.mapstruct.factory.Mappers;
import org.test.restaurant_service.dto.request.ProductRequestDTO;
import org.test.restaurant_service.dto.response.ProductIdsResponse;
import org.test.restaurant_service.dto.response.ProductResponseDTO;
import org.test.restaurant_service.dto.view.ProductIdsView;
import org.test.restaurant_service.dto.view.ProductLocalizedView;
import org.test.restaurant_service.entity.Photo;
import org.test.restaurant_service.entity.Product;
import org.test.restaurant_service.entity.ProductHistory;

import java.util.List;

@Mapper(componentModel = "spring", uses = ProductTypeMapper.class)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(source = "typeId", target = "type.id")
    @Mapping(target = "id", ignore = true) // Игнорирование id при обновлении
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "available", ignore = true)
    Product toEntity(ProductRequestDTO requestDTO);

    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(source = "photos", target = "photoUrl", qualifiedByName = "mapPhotoUrl")
    ProductResponseDTO toResponseDTO(Product product);

    //use for resolve lazy init of Product
    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    ProductResponseDTO toResponseIgnorePhotos(Product product);


    @Mapping(target = "typeName", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    ProductResponseDTO toResponseIgnorePhotosAndType(Product product);

    @Mapping(source = "type.name", target = "typeName")
    @Mapping(target = "quantity", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "cookingTime", ignore = true)
    @Mapping(target = "photoUrl", ignore = true)
    ProductResponseDTO toResponseForStats(Product product);


    @Mapping(target = "type", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "changedAt", ignore = true)
    @Mapping(source = "photos", target = "photoUrl", qualifiedByName = "mapPhotoUrl")
    ProductHistory toProductHistory(Product product);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "type", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "available", ignore = true)
    void updateEntityFromRequestDTO(ProductRequestDTO requestDTO, @MappingTarget Product product);

    @Named("mapPhotoUrl")
    default String mapPhotoUrl(java.util.List<Photo> photos) {
        return (photos != null && !photos.isEmpty()) ? photos.get(0).getUrl() : null;
    }

    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "quantity", ignore = true)
    ProductResponseDTO fromProjection(ProductLocalizedView view);

    ProductIdsResponse toProductIds(ProductIdsView allProductIds);
}