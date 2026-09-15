BEGIN;

DELETE FROM payments WHERE purchase_id IN (SELECT id FROM purchases WHERE code = 'ABC123');
DELETE FROM purchases WHERE code = 'ABC123';
DELETE FROM favorites WHERE buyer_id IN (SELECT id_user FROM users WHERE username IN ('comprador1', 'organizador1'));
DELETE FROM event_categories WHERE event_id IN (SELECT id FROM events WHERE name = 'Rock en el parque');
DELETE FROM events WHERE name = 'Rock en el parque';
DELETE FROM organizer_details WHERE id_user IN (SELECT id_user FROM users WHERE username IN ('comprador1', 'organizador1'));
DELETE FROM profiles WHERE id_user IN (SELECT id_user FROM users WHERE username IN ('comprador1', 'organizador1'));
DELETE FROM categories WHERE name = 'Conciertos';
DELETE FROM users WHERE username IN ('comprador1', 'organizador1');
DELETE FROM roles WHERE name IN ('Organizador', 'Comprador');

INSERT INTO roles (name, description) VALUES ('Organizador', 'Puede crear y administrar eventos');
INSERT INTO roles (name, description) VALUES ('Comprador', 'Puede comprar entradas y marcar favoritos');

INSERT INTO users (role_id, username, supabase_user_id, enabled) VALUES (2, 'comprador1', 'sb-uuid-1', true);
INSERT INTO users (role_id, username, supabase_user_id, enabled) VALUES (1, 'organizador1', 'sb-uuid-2', true);

INSERT INTO organizer_details (id_user, business_name, tax_id, bank_account) VALUES (2, 'Eventos Rock SRL', '20100000001', '191-000111-222');

INSERT INTO profiles (id_user, full_name, phone, address, created_at) VALUES (1, 'Comprador Uno', '988111222', 'Calle Los Pinos 789', now());
INSERT INTO profiles (id_user, full_name, phone, address, created_at) VALUES (2, 'Organizador Uno', '999111222', 'Av. Siempre Viva 123', now());

INSERT INTO categories (name) VALUES ('Conciertos');

INSERT INTO events (organizer_id, name, description, event_date, location, price, available_tickets, status, created_at, updated_at)
VALUES (2, 'Rock en el parque', 'Festival al aire libre', now() + interval '10 days', 'Parque Central', 45.00, 100, 'ACTIVE', now(), now());

INSERT INTO event_categories (event_id, category_id, is_primary) VALUES (1, 1, true);

INSERT INTO favorites (buyer_id, event_id) VALUES (1, 1);

INSERT INTO purchases (event_id, buyer_id, quantity, total_amount, code, status, created_at)
VALUES (1, 1, 2, 90.00, 'ABC123', 'PENDING', now());

INSERT INTO payments (purchase_id, method, amount, status, paid_at) VALUES (1, 'YAPE', 50.00, 'COMPLETED', now());

COMMIT;
