package co.atlvending.domain;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

/**
 * Lab 07 — a recorded sale. In the reference app persistence is imperative
 * (classic Hibernate ORM + Panache); the live feed is delivered reactively
 * with Mutiny + Server-Sent Events (see {@code SaleResource} / {@code SaleEventBus}).
 */
@Entity
@Table(name = "sales")
public class Sale extends PanacheEntity {

    public Long machineId;

    public String productSku;

    public int priceCents;

    public Instant occurredAt;
}
