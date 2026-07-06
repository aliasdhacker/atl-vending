package co.atlvending.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * Lab 05 — Persistence with Panache.
 * A product that can be stocked in a machine. SKU is unique.
 */
@Entity
@Table(name = "products")
public class Product extends PanacheEntity {

    @Column(unique = true)
    public String sku;

    public String name;

    @Column(name = "price_cents")
    public int priceCents;

    public Product() {
    }

    public Product(String sku, String name, int priceCents) {
        this.sku = sku;
        this.name = name;
        this.priceCents = priceCents;
    }
}
