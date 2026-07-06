-- Lab 05 — seed data, loaded on startup when
-- quarkus.hibernate-orm.database.generation=drop-and-create (dev & test).

INSERT INTO machines(id, location, status) VALUES
  (1, 'Concourse A Gate 12', 'ONLINE'),
  (2, 'Concourse B Gate 8',  'OFFLINE'),
  (3, 'Downtown Lobby 1',    'ONLINE');
ALTER SEQUENCE machines_seq RESTART WITH 4;

INSERT INTO products(id, sku, name, price_cents) VALUES
  (1, 'COKE-12',    'Coca-Cola 12oz', 250),
  (2, 'WATER-16',   'Bottled Water',  150),
  (3, 'CHIPS-LAYS', 'Lays Original',  175);
ALTER SEQUENCE products_seq RESTART WITH 4;