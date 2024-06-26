package com.example.ecom.services;

import com.example.ecom.exceptions.ProductNotFoundException;
import com.example.ecom.models.Product;
import com.example.ecom.models.ProductGroup;
import com.example.ecom.repositories.ProductGroupsRepository;
import com.example.ecom.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecommendationsServiceImpl implements RecommendationsService {
    private final ProductRepository productRepository;
    private final ProductGroupsRepository productGroupsRepository;

    public RecommendationsServiceImpl(ProductRepository productRepository, ProductGroupsRepository productGroupsRepository) {
        this.productRepository = productRepository;
        this.productGroupsRepository = productGroupsRepository;
    }

    @Override
    public List<Product> getRecommendations(int productId) throws ProductNotFoundException {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isEmpty()) {
            throw new ProductNotFoundException("Product not found");
        }
        Product product = optionalProduct.get();

        List<ProductGroup> productGroupList = productGroupsRepository.findByProductsContaining(product);

        return productGroupList.stream()
                .flatMap(productGroup -> productGroup.getProducts().stream())
                .distinct()
                .filter(product1 -> product1.getId() != productId)
                .collect(Collectors.toList());
    }
}
