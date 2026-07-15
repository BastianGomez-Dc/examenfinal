INSERT INTO users (email, password, active)
VALUES
    ('user1@mail.cl', crypt('abcd.1234', gen_salt('bf')), TRUE),
    ('user2@mail.cl', crypt('abcd.1234', gen_salt('bf')), TRUE),
    ('user3@mail.cl', crypt('abcd.1234', gen_salt('bf')), TRUE),
    ('user4@mail.cl', crypt('abcd.1234', gen_salt('bf')), TRUE),
    ('user5@mail.cl', crypt('abcd.1234', gen_salt('bf')), TRUE)
ON CONFLICT (email) DO NOTHING;
