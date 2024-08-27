package com.aleunam.spring_batch_tutorial.step;

import com.aleunam.spring_batch_tutorial.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductItemProcessor.class);
    private static final float BENEFIT = 0.2F;
    private static final float DISCOUNT = 0.1F;
    private static final float TAX = 0.21F;
    private static final String KITCHEN_SECTION = "Kitchen";

    /**
     * Actualización del precio final
     * Para productos de la sección de cocina --> Aplicar un descuento
     * precioFinal = ( precioOriginal + porcentajeBeneficio - porcentajeDescuento ) * IVA
     * Para el resto productos de otras secciones --> Sin descuento
     * precioFinal = ( precioOriginal + porcentajeDeBeneficio ) * IVA
     *
     * @param product el producto original leído del fichero CSV
     * @return el producto con su precio actualizado
     */
    @Override
    public Product process(Product product) {

        final float originalPrice = product.getPrice();
        final float defaultPriceWithoutTax = originalPrice * (1 + BENEFIT);
        float updatedPrice = defaultPriceWithoutTax * (1 + TAX);
        if (product.getSection().equalsIgnoreCase(KITCHEN_SECTION)) {
            LOGGER.info("--- {}", product);
            final float discountedPriceWithoutTax = originalPrice * (1 + BENEFIT - DISCOUNT);
            updatedPrice = discountedPriceWithoutTax * (1 + TAX);
            LOGGER.info("The price of the kitchen product {} has been reduced from {} to {}\n", product.getName(),
                    defaultPriceWithoutTax, discountedPriceWithoutTax);
        }
        product.setPrice(updatedPrice);
        return product;
    }
}
