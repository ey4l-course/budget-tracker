SELECT
    bc.category_name,
    bc.category_type,
    bc.amount_limit,
    COALESCE(SUM(t.amount), 0) as current_subtotal
FROM budget_configs bc
LEFT JOIN transactions t ON
    bc.username = t.username AND
    bc.category_name = COALESCE(t.user_defined_category, t.default_category) AND
    bc.category_type = t.category_type AND
    t.timestamp >= DATE_FORMAT(CURRENT_DATE, '%Y-%m-01') AND
    t.is_split = FALSE
WHERE bc.username = :username
GROUP BY bc.category_name, bc.category_type, bc.amount_limit;