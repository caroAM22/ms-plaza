INSERT INTO category (name, description)
SELECT 
    category_name,
    category_description
FROM (
    SELECT 'Seafood' as category_name, 'Seafood dishes' as category_description
    UNION ALL
    SELECT 'Mexican food', 'Mexican cuisine'
    UNION ALL
    SELECT 'Fast food', 'Burgers, fries, and more'
    UNION ALL
    SELECT 'Pasta', 'Italian pasta dishes'
    UNION ALL
    SELECT 'Desserts', 'Cakes, ice cream, and more'
) AS categories_to_insert
WHERE NOT EXISTS (SELECT 1 FROM category WHERE name = categories_to_insert.category_name); 