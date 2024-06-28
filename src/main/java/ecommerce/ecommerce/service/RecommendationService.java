package ecommerce.ecommerce.service;

import ecommerce.ecommerce.model.Producto;
import ecommerce.ecommerce.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    @Autowired
    private final ProductoRepository productRepository;

    public RecommendationService(ProductoRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Producto> recommendProducts(String occasion, String gender, Integer age, String interests,
            Double minPrice, Double maxPrice) {
        Logger logger = Logger.getLogger(RecommendationService.class.getName());

        List<Producto> products = productRepository.findAll();
        logger.info("Total products: " + products.size());

        // Compilar patrón de búsqueda para intereses
        Pattern pattern = null;
        if (interests != null && !interests.isEmpty()) {
            try {
                pattern = Pattern.compile(".*" + Pattern.quote(interests) + ".*", Pattern.CASE_INSENSITIVE);
            } catch (Exception e) {
                logger.severe("Failed to compile pattern: " + e.getMessage());
            }
        }

        Pattern finalPattern = pattern;
        List<Producto> filteredProducts = products.stream()
                .filter(product -> {
                    boolean result = (minPrice == null || product.getPrecio() >= minPrice);
                    if (!result)
                        logger.info("Filtered out by minPrice: " + product);
                    return result;
                })
                .filter(product -> {
                    boolean result = (maxPrice == null || product.getPrecio() <= maxPrice);
                    if (!result)
                        logger.info("Filtered out by maxPrice: " + product);
                    return result;
                })
                .filter(product -> {
                    if (finalPattern == null) {
                        return true;
                    }
                    if (product.getDescripcion() == null) {
                        logger.info("Filtered out by null description: " + product);
                        return false;
                    }
                    boolean result = (finalPattern == null || finalPattern.matcher(product.getDescripcion()).matches());
                    if (!result)
                        logger.info("Filtered out by interests: " + product);
                    return result;
                })
                .collect(Collectors.toList());

        logger.info("Filtered products: " + filteredProducts.size());
        return filteredProducts;
    }
}
