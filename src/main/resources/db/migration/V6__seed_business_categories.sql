INSERT INTO business_categories (
    id,
    name,
    description,
    active,
    created_at,
    updated_at,
    version
)
VALUES
    (
        gen_random_uuid(),
        'Restaurant',
        'Restaurants and food outlets',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Pharmacy',
        'Medical stores and pharmacies',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Electronics',
        'Electronic devices and accessories',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Fashion',
        'Clothing and fashion businesses',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Beauty Salon',
        'Hair, beauty and spa services',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Hotel',
        'Hotels and accommodation',
        TRUE,
        NOW(),
        NOW(),
        0
    ),
    (
        gen_random_uuid(),
        'Supermarket',
        'Retail supermarkets and grocery stores',
        TRUE,
        NOW(),
        NOW(),
        0
    );